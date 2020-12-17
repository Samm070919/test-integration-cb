package com.pagatodoholdings.posandroid.secciones.inicio;

import androidx.databinding.DataBindingUtil;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodo.sigmalib.util.QPOSSessionKeys;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityBienvenidaShopperBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.StanProviderMock;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import net.fullcarga.android.api.bd.sigma.manager.BdSigmaManager;
import net.fullcarga.android.api.data.DatosConexion;
import net.fullcarga.android.api.data.DatosTPV;
import net.fullcarga.android.api.data.VersionProtocolo;
import net.fullcarga.android.api.sesion.DatosSesion;
import net.fullcarga.android.api.sesion.SessionFactory;
import net.fullcarga.android.api.util.HexUtil;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static com.pagatodoholdings.posandroid.utils.Utilities.build;
import static net.fullcarga.android.api.oper.TipoOperacion.INIT_LIST;
import static net.fullcarga.android.api.oper.TipoOperacion.LOGIN;
import static net.fullcarga.android.api.oper.TipoOperacion.SOFTWARE_UPDATE;

public class BienvenidaShopperActivity extends AbstractActivity {//NOSONAR

    //----------UI-------------------------------------------------------
    private ActivityBienvenidaShopperBinding binding;

    private static final String TAG = BienvenidaShopperActivity.class.getSimpleName();
    //-----Inst ----------------------------------------------------------

    //----- Var ----------------------------------------------------------


    protected void initUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bienvenida_shopper);
        binding.btnAceptarShopper.setOnClickListener(v -> {
            try {
                final DatosSesion datosSesion = build();
                enviarLogin(datosSesion);
            }catch ( SQLException exe ){
                Logger.LOGGER.throwing(TAG,1,exe,exe.getMessage());
                despliegaModal(true, false, getString(R.string.error), Utilities.obtenerMensajeError(exe), () -> {});
            }
        });
    }


    private void enviarLogin(final DatosSesion datosSesion) {//NOSONAR
        ApiData.APIDATA.setDatosSesion(datosSesion);
        binding.btnAceptarShopper.setEnabled(false);
        muestraProgressDialog("Ingresando");

        TransaccionFactory.crearTransacion(LOGIN, abstractRespuesta -> {
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                operSiguiente();
            } else {
                if (abstractRespuesta.isCorrecta()){
                    if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())
                            && preferenceManager.isExiste(Constantes.Preferencia.ICONZIP_NAME)) {

                            isPciOrTransporte();
                            ApiData.APIDATA.setNotificationsID (getString(R.string.wallet_notificacion_path, BuildConfig.APPLICATION_ID,
                                    MposApplication.getInstance().getDatosLogin().getCliente().getPais()));
                        } else {
                            TransaccionFactory.crearTransacion(INIT_LIST, null, null).realizarOperacion();
                        }
                    }else {
                        ocultaProgressDialog();
                        despliegaModal(true, false, getString(R.string.error), abstractRespuesta.getMsjError(), () -> {});
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(), abstractRespuesta.getMsjError());
                    }
                }
        }, throwable -> {
            firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
            runOnUiThread(() -> {
                binding.btnAceptarShopper.setEnabled(true);
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.error), Utilities.obtenerMensajeError(throwable), () -> {
                    //No implementation
                });
            });
        }).realizarOperacion();
    }

    private void operSiguiente() {
        if (!ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion().equals(SOFTWARE_UPDATE)) {
            TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                    ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                    .realizarOperacion();
        }
    }

    private void isPciOrTransporte() {
        cambiaDeActividad(HomeActivity.class);
    }

    //----------Override Methods-------------------------------------------------------

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
        finish();
    }




    public static DatosSesion buildSessionPci(final String pinPci, final QPOSSessionKeys qposSessionKeys) throws SQLException {
        try (final BdSigmaManager bdSigmaManager = StorageUtil.crearConexionSigmaManager()) {
            final DatosConexion datosConexion = bdSigmaManager.crearDatosConexionPciTrx();

            return SessionFactory.crearSesionPci(
                    datosConexion,
                    ApiData.APIDATA.getDatosSesion(),
                    pinPci,
                    HexUtil.hex2byte(HexUtil.hexStringFromBytes(ApiData.APIDATA.getClaveBytePCI()), net.fullcarga.android.api.constantes.Constantes.DEF_CHARSET),
                    ApiData.APIDATA.getNameRsaPCI(),
                    qposSessionKeys.getEnDataCardKey(),
                    qposSessionKeys.getEnKcvDataCardKey(),//los 3 primeros bytes
                    qposSessionKeys.getEnPinKey(),
                    qposSessionKeys.getEnPinKcvKey());//los 3 primeros bytes
        }
    }




}
