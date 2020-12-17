package com.pagatodoholdings.posandroid;

import android.content.Context;
import com.pagatodo.sigmalib.util.SecurityUtil;
import com.pagatodoholdings.posandroid.general.exceptions.ClaveIncorrectaException;
import com.pagatodoholdings.posandroid.general.interfaces.BuildManager;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import net.fullcarga.android.api.util.StringUtil;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.UUID;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.*;

/**
 * @author dsoria
 */
public enum TipoConfiguracion {

    REGISTRO_EXTERNO("07", 6, true, new GeneradorSerieUUID(), new CalcularClaveTPVEnPreferencias());

    final String prefijo;
    final int longitudPassword;
    final boolean registro;
    final GeneradorSerieUUID generadorSerie;
    final CalcularClaveTPV calcularClaveTPV;

    TipoConfiguracion(
            final String prefijo,
            final int longitudPassword,
            final boolean registro,
            final GeneradorSerieUUID generadorSerie,
            final CalcularClaveTPV calcularClaveTPV) {

        this.prefijo = prefijo;
        this.longitudPassword = longitudPassword;
        this.registro = registro;
        this.generadorSerie = generadorSerie;
        this.calcularClaveTPV = calcularClaveTPV;
    }

    /**
     * Este método devuelve la instancia de Capacities de acuerdo a la configuraicón de la máquina
     *
     * @return Capacities
     */
    public static TipoConfiguracion getInstance(final String device) {
        return valueOf(device);
    }

    public String getPrefijo() {
        return prefijo;
    }

    public int getLongitudPassword() {
        return longitudPassword;
    }

    public String getNumeroSerie(final PreferenceManager preferencesManager, final BuildManager buildManager) {
       if (preferencesManager.isExiste(NUMERO_SERIE) && preferencesManager.isExiste(SUPER_APP_ACCOUNT)
               && preferencesManager.getValue(SUPER_APP_ACCOUNT,"").equals("0")) { //sino se comprueba si hemos guarddo ya uno
            return preferencesManager.getValue(NUMERO_SERIE, "");
        } else { // sino se genera yAxis se devuelve de acuerdo al prefijo yAxis al tipo de generacion del número de serie
            final String uuid = generadorSerie.getSerie(buildManager);
            final String serie = "07" + StringUtil.fillLeft(uuid, '0', 9);
            preferencesManager.setValue(NUMERO_SERIE, serie);
            return serie;
        }
    }

    public String getClaveTpv(final ConfigManager configManager, final PreferenceManager preferencesManager, final String pinUsuario) throws ClaveIncorrectaException {
        if (configManager.isDebugEnable()) { //si forzamos un serie para debug se devuelve ese siempre
            return configManager.getConfigClaveTPV();
        } else {
            return calcularClaveTPV.getClaveTPV(preferencesManager, pinUsuario);
        }
    }

    public boolean isRegistroExterno() {
        return registro;
    }

    private interface GeneradorSerie {
        String getSerie(final BuildManager buildManager);
    }

    static class GeneradorSerieUUID implements GeneradorSerie {
        @Override
        public String getSerie(final BuildManager buildManager) {
            return UUID.randomUUID().toString().toUpperCase(Locale.US);
        }
    }

    static class GeneradorSerieMaquina implements GeneradorSerie {
        @Override
        public String getSerie(final BuildManager buildManager) {
            return buildManager.getSerial().toUpperCase(Locale.US);
        }
    }

    private interface CalcularClaveTPV {
        String getClaveTPV(final PreferenceManager preferencesManager, final String pinUsuario) throws ClaveIncorrectaException;
    }

    private static class CalcularClaveTPVEnPreferencias implements CalcularClaveTPV {
        protected Context context;

        @Override
        public String getClaveTPV(final PreferenceManager preferencesManager, final String pinUsuario) throws ClaveIncorrectaException {
            try {
                if (preferencesManager.isExiste(CLAVE_TPV)) {
                    return SecurityUtil.decrypt(preferencesManager.getValue(CLAVE_TPV, ""), pinUsuario);
                } else {
                    throw new IllegalStateException(context.getString(R.string.clave_usuario_incorrecta));
                }
            } catch (GeneralSecurityException exc) {
                throw new ClaveIncorrectaException(context.getString(R.string.clave_usuario_incorrecta), exc);
            }
        }
    }
}