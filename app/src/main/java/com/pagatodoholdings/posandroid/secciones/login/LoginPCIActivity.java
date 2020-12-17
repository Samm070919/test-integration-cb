package com.pagatodoholdings.posandroid.secciones.login;

import androidx.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.pagatodo.qposlib.Logger;
import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.QPOSSessionKeys;
import com.pagatodo.sigmalib.util.SesionBuilder;
import com.pagatodo.sigmalib.util.SigmaUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityLoginpciBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import net.fullcarga.android.api.oper.TipoOperacion;
import net.fullcarga.android.api.sesion.DatosSesion;
import net.fullcarga.android.api.util.HexUtil;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import static net.fullcarga.android.api.oper.TipoOperacion.SOFTWARE_UPDATE;

public class LoginPCIActivity extends AbstractActivity {//NOSONAR

    private static final String TAG = LoginPCIActivity.class.getSimpleName();
    private ActivityLoginpciBinding binding;

    @Override
    protected void initUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loginpci);
        binding.etPassword.setNumeroCampos(true);
        binding.btnLoginPCI.setOnClickListener(view -> realizarSessionPCI());
        binding.activityLoginPCIRoot.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            final Rect rect = new Rect();
            binding.activityLoginPCIRoot.getWindowVisibleDisplayFrame(rect);
            final int screenHeight = binding.activityLoginPCIRoot.getRootView().getHeight();
            final int keypadHeight = screenHeight - rect.bottom;
            if (keypadHeight > screenHeight * 0.15) {
                cambiadeVisibilidadconEstilo(binding.ivLogoLoginPci, View.GONE);
                cambiadeVisibilidadconEstilo(binding.ivLogoIconPciLogo, View.GONE);
            } else {
                cambiadeVisibilidadconEstilo(binding.ivLogoLoginPci, View.VISIBLE);
                cambiadeVisibilidadconEstilo(binding.ivLogoIconPciLogo, View.VISIBLE);
            }
        });

        binding.etPassword.estableceAccionIme((textView, imeAction, keyEvent) -> {

            if (imeAction == EditorInfo.IME_ACTION_DONE) {
                SigmaUtil.hideSoftKeyboard(LoginPCIActivity.this);
                realizarSessionPCI();
                return true;
            }

            return false;
        });
        mostrarLineadeError(false);
    }

    @Override
    protected boolean validaCampos() {
        if (binding.etPassword.isVacio()) {
            despliegaModal(true, false, getString(R.string.cve_usuario), getString(R.string.campo_requerido), null);
            mostrarLineadeError(true);
            return false;
        }
        if (!binding.etPassword.laLongitudEsValida()) {
            despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_clave_invalida), null);
            mostrarLineadeError(true);
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
        cambiaDeActividad(LoginActivity.class);
    }

    private void realizarSessionPCI() {

        try {
            if (validaCampos()) {
                final DatosSesion datosSesion = SesionBuilder.buildSessionPci(binding.etPassword.getTexto(), sessionKeys());
                ApiData.APIDATA.setDatosSesionPCI(datosSesion);
                enviarLoginPCI();
            }
        } catch (RuntimeException exc) {
            ocultaProgressDialog();
            despliegaModal(true, false, getString(R.string.error), getString(R.string.error_intente_mas_tarde), null);
            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
        } catch (SQLException exc) {
            ocultaProgressDialog();
            despliegaModal(true, false, getString(R.string.error), getString(R.string.error_conexion_bd), null);
            Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
        }
    }

    private void enviarLoginPCI() {
        cambiaVisibilidadElementosInteractivos(View.GONE);
        muestraProgressDialog(getResources().getString(R.string.cargando));

        TransaccionFactory.crearTransacion(TipoOperacion.PCI_LOGIN, abstractRespuesta -> {
            if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                operSiguiente();
            } else {
                cambiaDeActividad(HomeActivity.class);
            }
        }, throwable -> {
            firebaseAnalytics.logEvent(Event.EVENT_LOGIN_ERROR.key, null);
            runOnUiThread(() -> {
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.error), UtilsKt.capitalizeWords(throwable.getMessage()), null);
                cambiaVisibilidadElementosInteractivos(View.VISIBLE);
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

    private void mostrarLineadeError(final boolean esError) {
        binding.etPassword.establecerColorError(esError ? R.color.colorErrorYellow : R.color.colorWhite);
    }

    private void cambiaVisibilidadElementosInteractivos(final int visible) {
        cambiadeVisibilidadconEstilo(binding.btnLoginPCI, visible);
        cambiadeVisibilidadconEstilo(binding.etPassword, visible);
    }


    private QPOSSessionKeys sessionKeys (){

        QPOSSessionKeys qposSessionKeys = null;
        final String strPinKey = "enPinKcvKey";


        if (MposApplication.getInstance().isSunmiDevice()){
            qposSessionKeys = new QPOSSessionKeys(
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get("enDataCardKey"), StandardCharsets.ISO_8859_1),
                    PosInstance.getInstance().getSessionKeys().get(strPinKey).getBytes(),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get(strPinKey), StandardCharsets.ISO_8859_1),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get("rsaReginLen"), StandardCharsets.ISO_8859_1),
                    PosInstance.getInstance().getSessionKeys().get("enKcvDataCardKey").getBytes(),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get("rsaReginString"), StandardCharsets.ISO_8859_1)
            );
        }else {
            qposSessionKeys = new QPOSSessionKeys(HexUtil.hex2byte(  PosInstance.getInstance().getSessionKeys().get("enDataCardKey"), StandardCharsets.ISO_8859_1),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get(strPinKey).substring(0, 6), StandardCharsets.ISO_8859_1),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get(strPinKey), StandardCharsets.ISO_8859_1),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get("rsaReginLen"), StandardCharsets.ISO_8859_1),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get("enKcvDataCardKey").substring(0, 6), StandardCharsets.ISO_8859_1),
                    HexUtil.hex2byte(PosInstance.getInstance().getSessionKeys().get("rsaReginString"), StandardCharsets.ISO_8859_1)
            );
        }

        return qposSessionKeys;

    }
}
