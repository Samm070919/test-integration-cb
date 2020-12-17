package com.pagatodoholdings.posandroid.secciones.registro.externo;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityRegistroExternoBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.desbloqueo.DesbloqueoActivity;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.NombreNegocioFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroAgregaCuentaActivity;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroTelefonoFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.UbicacionNegocioFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroBean;
import com.pagatodoholdings.posandroid.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.ESTADO_REGISTRO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.REGISTRO_CUENTA;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_MINEGOCIO;

public class RegistroDatosNegocio extends AbstractActivity implements AbstractStepFragment.RegisterActions {//NOSONAR

    private ActivityRegistroExternoBinding binding;
    private final ConstraintSet constraintSet = new ConstraintSet();
    private RegistroInteractor registroInteractor;
    private final RegistroBean registroBean = new RegistroBean();
    private String ubicacionActual = "";
    private Map<Integer, Integer> pageEtapaMap;
    String messageError = "Error desconocido";
    private static final String TAG = RegistroDatosNegocio.class.getSimpleName();
    private static final String DESHABILITA_BACK = "DESHABILITA_BACK";
    private FragmentManager fm;
    private static int idCallingActivity = 0;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        if (getIntent().getExtras() != null) {
            botonBackDeshabilitado = getIntent().getBooleanExtra(DESHABILITA_BACK, false);
            if(getCallingActivity() != null)
                idCallingActivity = getIntent().getIntExtra(com.pagatodoholdings.posandroid.utils.Constantes.ACTIVITY_CODE_KEY,0);
        } else {
            botonBackDeshabilitado = false;
        }

        super.onCreate(savedInstanceState);

        if (botonBackDeshabilitado) {
            setBackArrowVisibility(View.INVISIBLE);
        } else {
            setBackArrowVisibility(View.VISIBLE);
        }

        initPageEtapaMap();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final String serialNumber = preferenceManager.getValue(NUMERO_SERIE, "");
        registroBean.setSerie(serialNumber);
        registroBean.setTipodoc(0);
        registroBean.setModelo(registroInteractor.getDeviceInfo());
        registroBean.setLocalizacion(ubicacionActual);
        registroBean.setAppInstalacion(configManager.getVersionBdApp() + "(" + configManager.getVersionCode() + ")" + "|" + serialNumber);
    }

    private void initPageEtapaMap() {
        pageEtapaMap = new HashMap<>();
        pageEtapaMap.put(0, 0);
        pageEtapaMap.put(1, 1);
    }

    @Override
    protected void initUi() {
        fm = getSupportFragmentManager();
        registroInteractor = new RegistroInteractor();
        preferenceManager.setValue(NUMERO_SERIE, tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registro_externo);

        binding.onBack.setOnClickListener(v -> backArrowBtn());
        constraintSet.clone(binding.cvContainer);

        /**      * @deprecated      */
        @Deprecated final StepsFragmentPagerAdapter stepsFragmentPagerAdapter = new StepsFragmentPagerAdapter(fm);

        binding.vpSteps.setAdapter(stepsFragmentPagerAdapter);
        binding.vpSteps.setOffscreenPageLimit(2);
        binding.vpSteps.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                //no necesario
            }

            @Override
            public void onPageSelected(final int position) {


                Fragment page = fm
                        .findFragmentByTag("android:switcher:" + binding.vpSteps.getId() + ":" + position);
                if (page instanceof RegistroTelefonoFragment) {
                    ((RegistroTelefonoFragment) page).setCountryCode();
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                //no necesario
            }
        });
    }

    public void habilitarUbicacion() {
        Toast.makeText(this, "Habilite los permisos de ubicación", Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    public void backArrowBtn() {
        realizaAlPresionarBack();
    }

    public void setBackArrowVisibility(int visibility) {
        binding.onBack.setVisibility(visibility);
    }

    public void nextStep() {
        final int lastStep = StepsFragmentPagerAdapter.ITEM_COUNT - 1;
        final int currentStep = binding.vpSteps.getCurrentItem();
        final int nextStep = currentStep + 1;

        if (nextStep <= lastStep) {
            binding.vpSteps.setCurrentItem(nextStep, true);
        } else {
            if(idCallingActivity == REQUEST_COMPRA_KIT_BY_MINEGOCIO){/*sólo si se llama desde datos de mi negocio no solicitará cuenta y termina el activity*/
                finish();
            }else {
                cambiaDeActividad();
            }
        }
    }

    private void cambiaDeActividad() {
        String registeredAccount = preferenceManager.getValue(REGISTRO_CUENTA,"");
        if(registeredAccount.isEmpty()) {
            Intent intent = new Intent(this, RegistroAgregaCuentaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(DESHABILITA_BACK, true);
            startActivity(intent);
        }else {
            Intent output = new Intent();
            output.putExtra("DECODED", idCallingActivity);
            this.setResult(com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_MENU, output);
        }
        finish();
    }

    public void prevStep() {
        int currentStep = binding.vpSteps.getCurrentItem();
        final int step = currentStep != 0 ? currentStep - 1 : 0;
        binding.vpSteps.setCurrentItem(step, true);
    }

    public RegistroBean getRegistroBean() {
        return registroBean;
    }

    public void terminarRegistro() {
        final String serialNumber = preferenceManager.getValue(NUMERO_SERIE, "");

        registroBean.setLocalizacion(ubicacionActual);
        registroBean.setAppInstalacion(configManager.getVersionBdApp() + "(" + configManager.getVersionCode() + ")" + "|" + serialNumber);

        registrar();
    }

    private void obtenerMensajeError(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            messageError = jsonObject.getString("message");
        } catch (JSONException e) {
            messageError = response;
            Logger.LOGGER.throwing(TAG, 1, new Throwable("Error"), "JSONException: " + e.getMessage());
        }
    }

    void registrar() {
        muestraProgressDialog(getString(R.string.cargando));
        registroInteractor.registro(registroBean, new RetrofitListener<RegistroBean>() {
            @Override
            public void onSuccess(final RegistroBean registroBeanResponse) {
                despliegaModal(false, false, getString(R.string.exito), getString(R.string.registro_exitoso), () -> {
                    firebaseAnalytics.logEvent(Event.EVENT_COMPLETE_REGISTRO.key, null);
                    preferenceManager.setValue(ESTADO_REGISTRO, Constantes.EstadosRegistro.ENVIADO.name());
                    cambiaDeActividad(DesbloqueoActivity.class);
                });
            }

            @Override
            public void onFailure(final Throwable thr) {
                obtenerMensajeError(thr.getMessage());
                firebaseAnalytics.logEvent(Event.EVENT_FAIL_REGISTRO.key, null);
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_fallido), messageError, null);
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        if (binding.vpSteps.getCurrentItem() == 0) {
            if(idCallingActivity == com.pagatodoholdings.posandroid.utils.Constantes.NON_ACTIVITY_IS_CALLING)
                goToLogin();
            else
                finish();
        } else {
            prevStep();
        }
    }

    /**
     *      * @deprecated      
     */
    @Deprecated
    public static class StepsFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int ITEM_COUNT = 2;

        /**
         *      * @deprecated      
         */
        @Deprecated
        public StepsFragmentPagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case 0:
                    return NombreNegocioFragment.newInstance(idCallingActivity);
                case 1:
                    return UbicacionNegocioFragment.newInstance(idCallingActivity);
                default:
                    return null;
            }
        }
    }
}