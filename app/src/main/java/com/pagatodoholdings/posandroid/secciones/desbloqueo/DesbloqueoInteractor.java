package com.pagatodoholdings.posandroid.secciones.desbloqueo;

import com.pagatodo.sigmalib.listeners.InteractorListener;
import com.pagatodo.sigmalib.util.SecurityUtil;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import net.fullcarga.android.api.util.GeneradorPindesbloqueoAndroid;
import org.joda.time.DateTime;
import java.security.GeneralSecurityException;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.CLAVE_TPV;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;

class DesbloqueoInteractor {

    void validarDesbloqueo(final PreferenceManager preferenceManager, final String claveDeDesbloqueo, final String pinUsuario, final InteractorListener<Object> listener) {
        try {
            final String claveTpv = calcularClaveTPV(
                    claveDeDesbloqueo,
                    preferenceManager.getValue(NUMERO_SERIE, ""));
            preferenceManager.setValue(CLAVE_TPV, SecurityUtil.encrypt(claveTpv, pinUsuario));
            listener.onResponse(claveTpv);
        } catch (GeneralSecurityException | RuntimeException exc) {
            listener.onResponse(exc);
        }
    }

    private String calcularClaveTPV(final String clavedeDesbloque, final String serialNumber) {
        final String claveTpv = GeneradorPindesbloqueoAndroid.descifraPin(clavedeDesbloque, serialNumber, new DateTime());
        final int passwordInt = Integer.parseInt(claveTpv);
        if (passwordInt < 999999 && passwordInt > 0) {
            return claveTpv;
        } else {
            throw new IllegalArgumentException("Clave de tpv incorrecta");
        }
    }
}
