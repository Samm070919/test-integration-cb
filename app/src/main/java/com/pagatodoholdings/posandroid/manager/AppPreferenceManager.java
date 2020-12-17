package com.pagatodoholdings.posandroid.manager;

import android.content.SharedPreferences;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodo.sigmalib.util.SerializeUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import net.fullcarga.android.api.sesion.DatosSesion;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DATOS_SESION;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DATOS_SESION_PCI;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NOTIFICACIONES_ARRAY;

public class AppPreferenceManager implements PreferenceManager {

    private final SharedPreferences sharedPreferences;

    public AppPreferenceManager(final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void deletePreference(final Constantes.Preferencia preferencia) {
        if (isExiste(preferencia)) {
            sharedPreferences.edit().remove(preferencia.name()).apply();
        }
    }

    @Override
    public boolean isExiste(final Constantes.Preferencia preferencia) {
        return sharedPreferences.contains(preferencia.name());
    }

    @Override
    public String getValue(final Constantes.Preferencia preferencia, final String defValue) {
        return sharedPreferences.getString(preferencia.name(), defValue);
    }

    @Override
    public <O extends Serializable> O getValueObject(final Constantes.Preferencia preferencia) {
        return SerializeUtil.deserialize(getValue(preferencia, ""));
    }

    @Override
    public void setValue(final Constantes.Preferencia preferencia, final String value) {
        sharedPreferences.edit().putString(preferencia.name(), value).apply();
    }

    @Override
    public <O extends Serializable> void setValueObject(final Constantes.Preferencia preferencia, final O value) {
        setValue(preferencia, SerializeUtil.serialize(value));
    }

    @Override
    public DatosSesion getDatosSesion() {
        return getValueObject(DATOS_SESION);
    }

    @Override
    public void setDatosSesion(final DatosSesion datosSesion) {
        setValueObject(DATOS_SESION, datosSesion);
    }

    @Override
    public DatosSesion getDatosSesionPCI() {
        return getValueObject(DATOS_SESION_PCI);
    }

    @Override
    public void setDatosSessionPCI(final DatosSesion datosSessionPCI) {
        setValueObject(DATOS_SESION_PCI, datosSessionPCI);
    }

    @Override
    public Integer getValue(final Constantes.Preferencia preferencia) {
        return sharedPreferences.getInt(preferencia.toString(), 0);
    }

    @Override
    public void setValue(final Constantes.Preferencia preferencia, final int value) {
        sharedPreferences.edit().putInt(preferencia.name(), value).apply();
    }

    @Override
    public boolean getBooleanValue(Constantes.Preferencia preferencia) {
        return sharedPreferences.getBoolean(preferencia.name(), false);
    }

    @Override
    public void setBooleanValue(Constantes.Preferencia preferencia, boolean value) {
        sharedPreferences.edit().putBoolean(preferencia.name(), value);
    }

    @Override
    public Set<String> getNotificaciones() {
        return sharedPreferences.getStringSet(NOTIFICACIONES_ARRAY.toString(), new HashSet<String>());
    }

    @Override
    public void addNotificaciones(final String notificacionId) {
        final Set<String> notificacionIdSet = sharedPreferences.getStringSet(NOTIFICACIONES_ARRAY.toString(), new HashSet<String>());
        final Set<String> inNotification = new HashSet<>(notificacionIdSet);
        inNotification.add(notificacionId);
        sharedPreferences.edit().putStringSet(NOTIFICACIONES_ARRAY.toString(), inNotification).commit();
    }

    @Override
    public void deleteidNotificacion(final String notificacionId) {
        final Set<String> leidas = getNotificaciones();
        leidas.remove(notificacionId);
    }

    @Override
    public void guardarDatosUsuarioWallet(DatosLogin datosLogin) {
        MposApplication.getInstance().setDatosLogin(datosLogin);
        setValue(Constantes.Preferencia.EMAIL, datosLogin.getCliente().getEmail());
        setValue(Constantes.Preferencia.NOMBRE, datosLogin.getCliente().getNombre());
        setValue(Constantes.Preferencia.GENERO, datosLogin.getCliente().getGenero());
        setValue(Constantes.Preferencia.TELEFONO, datosLogin.getCliente().getTelefono());
        setValue(Constantes.Preferencia.NUMERO_SERIE, datosLogin.getCliente().getSerie());
        setValue(Constantes.Preferencia.TIPO_USUARIO, datosLogin.getCliente().getTipo());
    }

    @Override
    public void setIdPagoPse(int idPagoPse) {
        setValue(Constantes.Preferencia.ID_PAGO_PSE, idPagoPse);

    }

    @Override
    public void setCodigoClientePse(int codigoCliente) {
        setValue(Constantes.Preferencia.CODIGO_CLIENTE_PSE, codigoCliente);
    }

    @Override
    public int getIdPagoPse() {
        return getValue(Constantes.Preferencia.ID_PAGO_PSE);
    }

    @Override
    public int getCodigoClientePse() {
        return getValue(Constantes.Preferencia.CODIGO_CLIENTE_PSE);
    }

}
