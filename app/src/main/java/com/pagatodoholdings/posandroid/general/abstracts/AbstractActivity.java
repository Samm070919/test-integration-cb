package com.pagatodoholdings.posandroid.general.abstracts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.qposlib.broadcasts.BroadcastListener;
import com.pagatodo.qposlib.broadcasts.BroadcastManager;
import com.pagatodo.qposlib.dongleconnect.PosInterface;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.SplashActivity;
import com.pagatodoholdings.posandroid.TipoConfiguracion;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.CustomProgressLoader;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.general.interfaces.BuildManager;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.general.interfaces.GenericSimpleCallBack;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.secciones.acquiring.support.ActivitySupport;
import com.pagatodoholdings.posandroid.secciones.calendar.SnackBarListener;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;
import com.pagatodoholdings.posandroid.utils.Constantes;
import java.util.List;
import static com.pagatodo.qposlib.broadcasts.BroadcastManager.BLUETHOOTH_DESCONECTADO;

public abstract class AbstractActivity extends ActivitySupport implements BroadcastListener {//NOSONAR
    //----------UI-------------------------------------------------------
    protected CustomProgressLoader loader;
    private CustomProgressLoader timeOutMessage;
    protected ModalFragment modalFragment;

    //-----Inst ----------------------------------------------------------
    protected final TipoConfiguracion tipoConfiguracion;
    protected final ConfigManager configManager;
    protected final PreferenceManager preferenceManager;
    protected final BuildManager buildManager;
    protected FirebaseAnalytics firebaseAnalytics;
    private String modalTag = "Modal";

    //-----Inst ----------------------------------------------------------
    protected BroadcastManager broadcastManager;
    private final BroadcastReceiver updateEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.hasExtra(Constantes.ID_APP) && intent.getStringExtra(Constantes.ID_APP) != null) {
                final Intent launch = context.getApplicationContext().getPackageManager().getLaunchIntentForPackage(intent.getStringExtra(Constantes.ID_APP));
                launch.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launch);
            }
        }
    };

    //----- Var ----------------------------------------------------------
    protected boolean botonBackDeshabilitado;
    private static final int MSG_DISMISS_DIALOG = 0;
    private Handler handler;
    public static final String DESHABILITA_BACK = "DESHABILITA_BACK";

    public AbstractActivity() {
        this.tipoConfiguracion = MposApplication.getInstance().getTipoConfiguracion();
        this.configManager = MposApplication.getInstance().getConfigManager();
        this.preferenceManager = MposApplication.getInstance().getPreferenceManager();
        this.buildManager = MposApplication.getInstance().getBuildManager();
    }

    @Nullable
    @Override
    public View onCreateView(final String name, final Context context, final AttributeSet attrs) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.logEvent(this.getClass().getSimpleName(), null);
        loader = new CustomProgressLoader(this);
        timeOutMessage = new CustomProgressLoader(this);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        initUi();
        registerReceiver(updateEndReceiver, new IntentFilter(Constantes.ACTION_UPDATE_END));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateEndReceiver);
    }

    @Override
    public void onBackPressed() {
        if (!botonBackDeshabilitado) {
            realizaAlPresionarBack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean isBotonBackDeshabilitado() {
        return botonBackDeshabilitado;
    }

    public void setBotonBackDeshabilitado(boolean botonBackDeshabilitado) {
        this.botonBackDeshabilitado = botonBackDeshabilitado;
    }

    public void muestraProgressDialog(final String mensaje) {
        runOnUiThread( () -> {
                if (loader != null) {
                    loader.setMessage(mensaje);
                    loader.show();
                }
        });
    }

    /**
     * Misma utilidad con un pequeño callback que se ejecuta cuando el time out se activa, usa esto
     * para notificar del error del time out. Esto automaticamente quita el dialogo que mandaste,
     * así que no es necesario que lo ocultes en la implementación de tu callback
     *
     * @param mensaje  Texto del mensaje
     * @param timeOut  MILI-SEGUNDOS
     * @param callBack Declara tu interfaz para manejar el time out
     */
    @SuppressLint("HandlerLeak")
    public void muestraProgressDialogConTimeOut(final String mensaje, long timeOut, final GenericSimpleCallBack callBack) {
        if (callBack == null || timeOutMessage.isShowing()) {
            return;
        }
        timeOutMessage.setMessage(mensaje);
        timeOutMessage.show();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_DISMISS_DIALOG && timeOutMessage.isShowing()) {
                    callBack.onResponse();
                }
            }
        };
        handler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, timeOut);
    }

    /**
     * Sólo funciona para ocultar el diálogo específico de Time Out, no funciona para el normal
     */
    public void ocultaProgressDialogTimeOut() {
        if (timeOutMessage.isShowing() && handler != null) {
            handler.removeMessages(MSG_DISMISS_DIALOG);
            timeOutMessage.dismiss();
            handler = null;
        }
    }

    public void actualizarProductos(final String mensaje) {
        if (loader != null && !isModalFragmentShowing()) {
            loader.setMessage(mensaje);
            loader.mostrarActualizacionProductos();
        }
    }

    protected boolean isModalFragmentShowing() {

        return modalFragment != null
                && modalFragment.getDialog() != null
                && modalFragment.getDialog().isShowing()
                && !modalFragment.isRemoving();
    }

    public void ocultaProgressDialog() {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
        if (isModalFragmentShowing()) {
            dismissConfirmation();
        }
    }

    public void despliegaModal(
            final boolean esDeError,
            final boolean tieneCancelar,
            final String cabecera,
            final String cuerpo,
            final ModalFragment.CommonDialogFragmentCallBack callback) {
        new ModalFragment.DialogBuilder().setTitle(cabecera)
                .setBody(cuerpo)
                .setSingleButton(!tieneCancelar)
                .setAccept(getString(R.string.aceptar))
                .setCancel(getString(R.string.cancelar))
                .setCanceledOnTouchOutside(false)
                .ponEsError(esDeError)
                .ponInterfaceCallBack(callback)
                .build()
                .show(getSupportFragmentManager(), modalTag);
    }


    public void showErrorSnackBar(Activity rootView, String message, int length) {
        Snackbar snack = Snackbar.make(rootView.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redColorTransparent));
        snack.show();
    }

    public  void showErrorSnackBar(View rootView, String message) {
        Snackbar snack = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redColorTransparent));
        snack.show();
    }

    public void showErrorSnackBar(Activity rootView, String message, SnackBarListener listener) {
        Snackbar snack = Snackbar.make(rootView.getWindow().getDecorView(), message, Snackbar.LENGTH_INDEFINITE);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redColorTransparent));
        snack.show();
        new Handler().postDelayed(() -> {
            snack.dismiss();
            listener.afterFinishing();
        },1500);
    }

    public void showErrorSnackBar(View rootView, String message, SnackBarListener listener){
        Snackbar snack = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redColorTransparent));
        snack.show();
        new Handler().postDelayed(() -> {
            snack.dismiss();
            listener.afterFinishing();
        },1500);
    }

    public void showSuccessSnackBar(View rootView, String message) {
        Snackbar snack = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redGreenTransparent));
        snack.show();
    }

    public void showSuccessSnackBar(Activity rootView, String message, int length) {
        Snackbar snack = Snackbar.make(rootView.getWindow().getDecorView(), message, Snackbar.LENGTH_LONG);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redGreenTransparent));
        snack.show();
    }

    public void showSuccessSnackBar(Activity rootView, String message, SnackBarListener listener) {
        Snackbar snack = Snackbar.make(rootView.getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redGreenTransparent));
        snack.show();
        new Handler().postDelayed(() -> {
            snack.dismiss();
            listener.afterFinishing();
        },1500);
    }

    public void showSuccessSnackBar(Activity rootView, String message){
        Snackbar snack = Snackbar.make(rootView.getWindow().getDecorView(), message, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.redGreenTransparent));
        snack.show();
    }


    public void showConfirmation(
            final String cabecera,
            final View productConfirmationLayout,
            final ModalFragment.CommonDialogFragmentCallBack callback) {

        modalFragment = new ModalFragment.DialogBuilder().setTitle(cabecera)
                .setBody("")
                .setSingleButton(false)
                .setAccept(getString(R.string.aceptar))
                .setCancel(getString(R.string.cancelar))
                .setCanceledOnTouchOutside(false)
                .ponEsError(false)
                .ponInterfaceCallBack(callback)
                .setExtraContent(productConfirmationLayout)
                .build();

        modalFragment.show(getSupportFragmentManager(), modalTag);
    }

    public void showSaldo(
            final boolean tieneCancelar,
            final String cabecera,
            final View productConfirmationLayout,
            final ModalFragment.CommonDialogFragmentCallBack callback) {

        modalFragment = new ModalFragment.DialogBuilder().setTitle(cabecera)
                .setBody("")
                .setSingleButton(!tieneCancelar)
                .setAccept(getString(R.string.aceptar))
                .setCancel(getString(R.string.cancelar))
                .setCanceledOnTouchOutside(true)
                .ponEsError(false)
                .ponInterfaceCallBack(callback)
                .setExtraContent(productConfirmationLayout)
                .build();

        modalFragment.show(getSupportFragmentManager(), modalTag);
    }

    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btnConfirmacion);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        final BotonClickUnico btnCancelar = view.findViewById(R.id.btnCancel);
        btnCancelar.setText(getString(R.string.cancelar));
        btnCancelar.setTextSize(14);
        btnCancelar.setOnClickListener(view12 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public void dismissConfirmation() {

        modalFragment.dismiss();
    }

    public void restauraEnLugarDeCrash() {
        final Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(getString(R.string.error_fatal_key), true);
        startActivity(intent);
        finishAffinity();
    }

    public void restauraHome() {
        final Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    public void restaurarDespuesDeUpdateFirmware() {
        final Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra(getString(R.string.update_qpos_correct_blue), true);
        startActivity(intent);
        finishAffinity();
    }

    public void cambiaDeActividad(final Class<? extends Activity> claseNuevaActividad) {
        final Intent intent = new Intent(this, claseNuevaActividad);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void cambiaDeActividadSinCerrar(final Class<? extends Activity> claseNuevaActividad) {
        final Intent intent = new Intent(this, claseNuevaActividad);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void cambiaDeActividadBundle(final Class<? extends Activity> claseNuevaActividad, final Bundle bundle) {
        final Intent intent = new Intent(this, claseNuevaActividad);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    public void goToLogin() {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DESHABILITA_BACK, true);
        startActivity(intent);
        finish();
    }

    protected void cambiadeVisibilidadconEstilo(final View vista, final int visibilidad) {
        if (vista.getParent() instanceof ViewGroup) {
            final AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(Constantes.TIEMPO_TRANSICIONES);
            TransitionManager.beginDelayedTransition((ViewGroup) vista.getParent(), autoTransition);
            vista.setVisibility(visibilidad);
        }
    }

    public String getNumeroSerie() {
        return tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager);
    }

    protected abstract void initUi();

    protected abstract boolean validaCampos();

    protected abstract List<EditTextDatosUsuarios> registraCamposValidar();

    protected abstract void realizaAlPresionarBack();



    public void finishApp() {
        firebaseAnalytics.logEvent(Event.EVENT_LOGOUT.key, null);
        disconnectDongle();
        finishAffinity();
        cambiaDeActividad(LoginActivity.class);
    }

    public void killApp() {
        disconnectDongle();
        finishAndRemoveTask();
    }

    public void disconnectDongle() {
        if (MposApplication.getInstance().getPreferedDongle() != null) {
            final AbstractDongle qposDongle = MposApplication.getInstance().getPreferedDongle().getQpos(PosInterface.Tipodongle.DSPREAD);
            qposDongle.closeCommunication();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onRecive(String bundle) {
        if (bundle.equals(BLUETHOOTH_DESCONECTADO)) {
            PosInstance.getInstance().setDongle(null);
        }
    }

    public void showDialogButtonAcept(int layout, String buttonText, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(true);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view .findViewById(R.id.appCompatButton);
        btnAceptar.setText(buttonText);
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }
}