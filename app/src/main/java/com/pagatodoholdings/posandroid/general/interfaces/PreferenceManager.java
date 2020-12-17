package com.pagatodoholdings.posandroid.general.interfaces;

import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;

import net.fullcarga.android.api.sesion.DatosSesion;

import java.io.Serializable;
import java.util.Set;

public interface PreferenceManager {
    // Esta clase se puede cambiar ya que igual interesa poner metodos tipo set yAxis get que utilicen tipos int, long, bool, ademas de string

    // Cuando se requeira pasar objetos cifrados en el value se debe mandar ya la informacion cifrada, de esta forma esta clase queda solo con una responsabilidad que es leer yAxis guardar preferencias

    // hay que ver si realmente es util separar las preferencias en varios "archivos" o realmente no hace falta. Ahora usa por separado preferancias para las descargas de menu yAxis asi que no se si realmente interesa difereneciar.



    void deletePreference(final Constantes.Preferencia preferencia);

    boolean isExiste(final Constantes.Preferencia preferencia);

    String getValue(final Constantes.Preferencia preferencia, final String defValue);

    <O extends Serializable> O getValueObject(final Constantes.Preferencia preferencia);

    void setValue(final Constantes.Preferencia preferencia, final String value);

    <O extends Serializable> void setValueObject(final Constantes.Preferencia preferencia, O value);

    DatosSesion getDatosSesion();

    void setDatosSesion(final DatosSesion datosSesion);

    DatosSesion getDatosSesionPCI();

    void setDatosSessionPCI(final DatosSesion datosSessionPCI);

    Integer getValue(final Constantes.Preferencia preferencia);

    void setValue(final Constantes.Preferencia preferencia, final int value);

    boolean getBooleanValue(final Constantes.Preferencia preferencia);

    void setBooleanValue(final Constantes.Preferencia preferencia, final boolean value);

    Set<String> getNotificaciones();

    void addNotificaciones(String notificacionId);

    void deleteidNotificacion(String notificacionId);

    void guardarDatosUsuarioWallet(DatosLogin datosLogin);

    void setIdPagoPse(int idPagoPse);

    void setCodigoClientePse(int codigoCliente);

    int getIdPagoPse();

    int getCodigoClientePse();
}
