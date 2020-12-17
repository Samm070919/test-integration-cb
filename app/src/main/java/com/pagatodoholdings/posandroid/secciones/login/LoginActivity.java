package com.pagatodoholdings.posandroid.secciones.login; //NOPMD

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.ActivityLoginBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.manager.FirebaseManager;
import com.pagatodoholdings.posandroid.secciones.Preference;
import com.pagatodoholdings.posandroid.secciones.desbloqueo.DesbloqueoActivity;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCliente;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.secciones.retrofit.LoginServiceInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegisterFirebaseService;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroTpvInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TpvRegistro;
import com.pagatodoholdings.posandroid.utils.NetworkUtils;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsKt;
import com.squareup.picasso.Picasso;

import net.fullcarga.android.api.sesion.DatosSesion;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.EMAIL;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NOMBRE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.PAIS;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.SUPER_APP_ACCOUNT;
import static com.pagatodoholdings.posandroid.utils.Constantes.PREFIJOIMAGEN;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;
import static net.fullcarga.android.api.oper.TipoOperacion.INIT_LIST;
import static net.fullcarga.android.api.oper.TipoOperacion.LOGIN;

@SuppressWarnings("PMD.GodClass")
public class LoginActivity extends AbstractActivity {//NOPMD //NOSONAR Nivel de herencia mayor de 5
    //----------UI-------------------------------------------------------
    private ActivityLoginBinding binding;

    //-----Inst ----------------------------------------------------------
    ModalFragment.CommonDialogFragmentCallBack callBack;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Location location;
    private Preference prf;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void initUi() {

        prf = new Preference(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.activityLoginRoot.requestFocus();
        binding.btnLogin.setOnClickListener(view -> {
                    if (NetworkUtils.isConnected(getApplicationContext())) {
                        buttonLoginClick();
                    } else {
                        despliegaModal(true, false, getResources().getString(R.string.generic_error), getResources().getString(R.string.no_internet), () -> {
                            //Not implemented
                        });
                    }
                });
        binding.edContrasena.estableceAccionIme((textView, imeAction, keyEvent) -> {

            if (imeAction == EditorInfo.IME_ACTION_DONE) {
                Utilities.hideSoftKeyboard(LoginActivity.this);
                if (NetworkUtils.isConnected(getApplicationContext())) {
                    buttonLoginClick();
                } else {
                    despliegaModal(true, false, getResources().getString(R.string.generic_error), getResources().getString(R.string.no_internet) , () -> {
                    });
                }
                return true;
            }

            return false;
        });
        binding.tvNocuenta.setOnClickListener(v -> cambiaDeActividad(RegistroCliente.class));


        setLogoImage();

        //binding.ivLogoLogin.setImageResource(R.drawable.ya_icon_logo);
        if (preferenceManager.isExiste(EMAIL)) {
            vinculadoLogin();
        }else {
            loginNuevo();
        }

        binding.tvOlvidoContrasena.setOnClickListener(view -> cambioDesbloqueo());

        binding.activityLoginRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            final Rect rect = new Rect();
            binding.activityLoginRoot.getWindowVisibleDisplayFrame(rect);
            final int screenHeight = binding.activityLoginRoot.getRootView().getHeight();
            final int keypadHeight = screenHeight - rect.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                cambiadeVisibilidadconEstilo(binding.ivLogoLogin, View.GONE);
            } else {
                cambiadeVisibilidadconEstilo(binding.ivLogoLogin, View.VISIBLE);
            }
        });

            location = Utilities.obtenerUbicacion(this);
    }

    private void vinculadoLogin() {
        binding.edCorreoElectronico.setText(preferenceManager.getValue(EMAIL, ""));
        //Poner IInvisible los componentes
        binding.edCorreoElectronico.setVisibility(View.INVISIBLE);
        //binding.tvNocuenta.setVisibility(View.GONE);
        //binding.txtBienvenida.setVisibility(View.VISIBLE);
        binding.bienvenido.setText(getResources().getString(R.string.msj_bienvenida_nombre, preferenceManager.getValue(NOMBRE, "")));
        if (StorageUtil.validarArchivo(StorageUtil.getStoragePath() + PREFIJOIMAGEN)) {
            binding.ivLogoLogin.setImageBitmap(Utilities.getProfilePic());
        }
    }

    private void loginNuevo() {
        //Poner IInvisible los componentes
        binding.edCorreoElectronico.setVisibility(View.VISIBLE);
        binding.tvNocuenta.setVisibility(View.VISIBLE);
        //binding.txtBienvenida.setVisibility(View.GONE);
        binding.ivLogoLogin.setImageResource(R.drawable.ya_icon_logo);
    }

    public void buttonLoginClick() {
        // Valida si hacemos registro deTpV o login con datos cargados.
        if (validaCampos() ) {
            if (!preferenceManager.isExiste(EMAIL)) {
                muestraProgressDialog("Registrando Nuevo Celular..");
                realizaRegistroTpv();
            } else {

                /*
                * Se valida si existe la preferencia de migración SUPER_APP_ACCOUNT y si es necesario Actualizar y Registrar un nuevo TPV
                * */
                if(preferenceManager.isExiste(SUPER_APP_ACCOUNT) && preferenceManager.getValue(SUPER_APP_ACCOUNT,"").equals("0")) {

                    muestraProgressDialog("Ingresando..");
                    final LoginServiceInteractor login = new LoginServiceInteractor();
                    login.loginService(binding.edCorreoElectronico.obtenEtCampo().getText().toString().toLowerCase().trim()
                            , binding.edContrasena.getTexto(),
                            preferenceManager.getValue(NUMERO_SERIE, ""), new RetrofitListener() {
                                @Override
                                public void showMessage(String message) {
                                    //None
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    ocultaProgressDialog();
                                    despliegaModal(true,
                                            false,
                                            getResources().getString(R.string.generic_error),
                                            Utilities.obtenerMensajeError(throwable), () -> {
                                            });
                                }

                                @Override
                                public void onSuccess(Object result) {
                                    final DatosLogin loginServiceData = (DatosLogin) result;
                                    MposApplication.getInstance().setDatosLogin(loginServiceData);
                                    preferenceManager.guardarDatosUsuarioWallet(loginServiceData);
                                    preferenceManager.setValue(NOMBRE, loginServiceData.getCliente().getNombre());
                                    preferenceManager.setValue(PAIS, loginServiceData.getCliente().getPais());
                                    ocultaProgressDialog();

                                    try {
                                        loginSigma();
                                    } catch (RuntimeException exc) {
                                        binding.btnLogin.setEnabled(true);
                                        mostrarProgreso(false);
                                        despliegaModal(true, false, getString(R.string.app_name), getString(R.string.contrasena_usuario_incorrecta), callBack);
                                        LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                                    } catch (SQLException exc) {
                                        binding.btnLogin.setEnabled(true);
                                        mostrarProgreso(false);
                                        despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_conexion_bd), callBack);
                                        LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                                    }
                                }
                            }
                    );

                }else{
                    ///verificar si existe la base de datos, eliminarla
                    if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())){
                        StorageUtil.deleteIfExistFile(StorageUtil.getSigmaDbPath());
                    }

                    preferenceManager.setValue(SUPER_APP_ACCOUNT, "1");
                    muestraProgressDialog("Actualizando Nuevo Dispositivo..");
                    actualizarTpv();
                }



            }
        }
    }

    private void addAdjustEvent(DatosLogin loginServiceData){
        AdjustEvent adjustEvent = new AdjustEvent("ys6qxl");
        adjustEvent.addCallbackParameter("clicod", loginServiceData.getDatosTpv().getClicod().toString());
        adjustEvent.addCallbackParameter("geolocalizacion", "Latitud: " + (location==null ? 0.00:location.getLatitude())
                + " Longitud: " + (location==null ? 0.00: location.getLongitude()));

        LOGGER.fine("ADJUST", "Latitud: " + (location==null ? 0.00:location.getLatitude())
                + " Longitud: " + (location==null ? 0.00: location.getLongitude()));
        Adjust.trackEvent(adjustEvent);
    }

    /**
     * Valida Tipo De Usuario para Consultar Datos de negocio
     * @throws SQLException
     */
    private void loginSigma() throws SQLException {
        final DatosSesion datosSesion = build();
        enviarLogin(datosSesion);
        /** Adjust Event **/
        addAdjustEvent(MposApplication.getInstance().getDatosLogin());
    }

    private void mostrarProgreso(final boolean mostrar) {
        if (mostrar) {
            this.muestraProgressDialog(getString(R.string.cargando));
        } else {
            this.ocultaProgressDialog();
        }
    }

    private void mostrarProgreso(final boolean mostrar,String texto) {
        if (mostrar) {
            this.muestraProgressDialog(texto);
        } else {
            this.ocultaProgressDialog();
        }
    }

    private void enviarLogin(final DatosSesion datosSesion) {//NOSONAR
        ApiData.APIDATA.setDatosSesion(datosSesion);
        binding.btnLogin.setEnabled(true);
        mostrarProgreso(true);
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(LOGIN, abstractRespuesta -> {
            if (abstractRespuesta.isCorrecta()) {
                FirebaseManager.enablePushNotifications(MposApplication.getInstance().getDatosLogin().getCliente().getPais(), ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod());
                if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                    operSiguiente();
                } else {
                        if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())
                                && preferenceManager.isExiste(Constantes.Preferencia.ICONZIP_NAME)
                        ) {
                            ocultaProgressDialog();
                            isPciOrTransporte();
                            ApiData.APIDATA.setNotificationsID (getString(R.string.wallet_notificacion_path, BuildConfig.APPLICATION_ID,
                                    MposApplication.getInstance().getDatosLogin().getCliente().getPais()));
                        } else {
                            mostrarProgreso(false);
                            mostrarProgreso(true,getString(R.string.configurando_dispositivo));
                            TransaccionFactory.crearTransacion(INIT_LIST,
                                    abstractRespuesta1 -> {
                                        if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                                            operSiguiente();
                                        } else {
                                            if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())
                                                    && preferenceManager.isExiste(Constantes.Preferencia.ICONZIP_NAME)
                                            ) {
                                                ocultaProgressDialog();
                                                isPciOrTransporte();
                                                ApiData.APIDATA.setNotificationsID(getString(R.string.wallet_notificacion_path, BuildConfig.APPLICATION_ID,
                                                        MposApplication.getInstance().getDatosLogin().getCliente().getPais()));
                                            }
                                        }
                                    }
                                    , throwable -> {
                                        ocultaProgressDialog();
                                        despliegaModal(true, false, getString(R.string.generic_error), UtilsKt.capitalizeWords(Utilities.obtenerMensajeError(throwable)), callBack);
                                    }).realizarOperacion();
                        }
                    }
                } else {
                    final String errorMsj = abstractRespuesta.getMsjError();
                    preferenceManager.setValue(NUMERO_SERIE, tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));
                    runOnUiThread(() -> {
                        binding.btnLogin.setEnabled(true);
                        mostrarProgreso(false);
                        despliegaModal(true, false, getString(R.string.generic_error), UtilsKt.capitalizeWords(errorMsj), callBack);
                    });
                }
        }, throwable -> {
            firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
            runOnUiThread(() -> {
                binding.btnLogin.setEnabled(true);
                mostrarProgreso(false);
                despliegaModal(true, false, getString(R.string.generic_error), UtilsKt.capitalizeWords(Utilities.obtenerMensajeError(throwable)), callBack);
            });
        });
        transaccion.realizarOperacion();
    }

    private void operSiguiente() {
        TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                .realizarOperacion();
    }

    private void isPciOrTransporte() {
        //Service Alta Firebase SIGMA
        registerFirebase();
    }

    private void registerFirebase() {

            //Validar si existen los datos de compra del kit
                RegisterFirebaseService service = new RegisterFirebaseService();

                service.registerFirebase(ApiData.APIDATA.getDatosSesion().getIdSesion(),
                        MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(), new RetrofitListener<Boolean>() {
                            @Override
                            public void showMessage(String s) {
                                //No implements
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                                cambiaDeActividad(HomeActivity.class);
                            }

                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                LOGGER.fine(TAG, "ALTA FIREBASE SIGMA SUCCESS");
                                cambiaDeActividad(HomeActivity.class);
                            }
                        });
        }

    private void realizaRegistroTpv() {
        final RegistroTpvInteractor registroTpvInteractor = new RegistroTpvInteractor();
        registroTpvInteractor.registroTpv(new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //No implementation
            }

            @Override
            public void onFailure(Throwable throwable) {
                ocultaProgressDialog();
                despliegaModal(true,
                        false,
                        "Error al Ingresar",
                        Utilities.obtenerMensajeError(throwable), () -> {
                        });
            }

            @Override
            public void onSuccess(Object result) {
                ocultaProgressDialog();
                final DatosLogin loginServiceData = (DatosLogin) result;
                MposApplication.getInstance().setDatosLogin(loginServiceData);
                PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
                preferenceManager.guardarDatosUsuarioWallet(loginServiceData);
                ocultaProgressDialog();
                try {
                    loginSigma();
                } catch (RuntimeException exc) {
                    binding.btnLogin.setEnabled(true);
                    ocultaProgressDialog();
                    despliegaModal(true, false, getString(R.string.app_name), getString(R.string.contrasena_usuario_incorrecta), callBack);
                    LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                } catch (SQLException exc) {
                    binding.btnLogin.setEnabled(true);
                    ocultaProgressDialog();
                    despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_conexion_bd), callBack);
                    LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                }
            }
        }, new TpvRegistro(binding.edCorreoElectronico.obtenEtCampo().getText().toString().toLowerCase().trim(),
                "MODELO_LOGICO_WALLET",
                binding.edContrasena.obtenEtCampo().getText().toString(),
                preferenceManager.getValue(NUMERO_SERIE, "")));
    }

    private void actualizarTpv() {
        String email;
        if(preferenceManager.getValue(EMAIL,"")!=null || !preferenceManager.getValue(EMAIL,"").equals("")){
            email = preferenceManager.getValue(EMAIL,"");
        }else{
            email = binding.edCorreoElectronico.obtenEtCampo().getText().toString().toLowerCase().trim();
        }


        final RegistroTpvInteractor registroTpvInteractor = new RegistroTpvInteractor();
        registroTpvInteractor.registroTpv(new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //No implementation
            }

            @Override
            public void onFailure(Throwable throwable) {
                ocultaProgressDialog();
                preferenceManager.setValue(NUMERO_SERIE, tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));
                despliegaModal(true,
                        false,
                        "Error al Ingresar",
                        Utilities.obtenerMensajeError(throwable), () -> {
                        });
            }

            @Override
            public void onSuccess(Object result) {
                ocultaProgressDialog();
                final DatosLogin loginServiceData = (DatosLogin) result;
                MposApplication.getInstance().setDatosLogin(loginServiceData);
                PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
                preferenceManager.guardarDatosUsuarioWallet(loginServiceData);
                ocultaProgressDialog();
                preferenceManager.setValue(SUPER_APP_ACCOUNT, "0");

                try {
                    loginSigma();
                } catch (RuntimeException exc) {
                    binding.btnLogin.setEnabled(true);
                    ocultaProgressDialog();
                    despliegaModal(true, false, getString(R.string.app_name), getString(R.string.contrasena_usuario_incorrecta), callBack);
                    LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                } catch (SQLException exc) {
                    binding.btnLogin.setEnabled(true);
                    ocultaProgressDialog();
                    despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_conexion_bd), callBack);
                    LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                }
            }
        }, new TpvRegistro(email,
                "MODELO_LOGICO_WALLET",
                binding.edContrasena.obtenEtCampo().getText().toString(),
                preferenceManager.getValue(NUMERO_SERIE, "")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            cambiaDeActividad(LoginPCIActivity.class);
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            despliegaModal(true, false, getString(R.string.error_conexion), getString(R.string.error_conexion_dongle), callBack);
        }
    }


    @Override
    protected void onResume() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onResume();
    }

    @Override
    protected boolean validaCampos() {
        if (binding.edCorreoElectronico.obtenEtCampo().getVisibility() == View.VISIBLE && !binding.edCorreoElectronico.esCorreo()) {
            despliegaModal(true, false, getString(R.string.cve_usuario), getString(R.string.campo_requerido_correo), callBack);
            return false;
        }
        if (binding.edContrasena.getTexto().length() > 6) {
            despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_clave_invalida), callBack);
            return false;
        }
        if (binding.edContrasena.getTexto().trim().length() == 0) {
            despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_clave_vacia), callBack);
            return false;
        }
        return true;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
         /**
         * Si eres un Usuario Nuevo, al dar Back de enviará al Intro,
         *  de lo contrario cerrará la App.
         * **/

        if(!preferenceManager.isExiste(EMAIL)) {
            finish();
        }else{
            killApp();
        }
    }

    private void cambioDesbloqueo() {
        Intent intent = new Intent(this, DesbloqueoActivity.class);
        startActivity(intent);
    }

    private void setLogoImage(){
        if(prf.getProfileImage("personal") == null){
            binding.ivLogoLogin.setImageResource(R.drawable.ic_agregar_foto);
        }else{
            Picasso.with(getApplicationContext()).load(prf.getProfileImage("personal"))
                    .resize(74, 74)
                    .into(binding.ivLogoLogin);
        }
    }

}
