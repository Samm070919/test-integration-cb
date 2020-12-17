package com.pagatodoholdings.posandroid.secciones.dongleconnect;

import com.pagatodo.qposlib.PosInstance;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.QPOSSessionKeys;
import com.pagatodo.sigmalib.util.SesionBuilder;
import com.pagatodoholdings.posandroid.MposApplication;
import net.fullcarga.android.api.oper.TipoOperacion;
import net.fullcarga.android.api.sesion.DatosSesion;
import net.fullcarga.android.api.util.HexUtil;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;

public final class PciLoginManager {

    public interface SessionPciListener {
        /**
         * Se llama cuando el proceso de login comience
         */
        void loginInProcess();

        /**
         * Se ejecuta cuando el proceso de login termina
         * @param isSuccessful el proceso fue exitoso
         * @param result el resultado de la transacción
         */
        void loginProcessFinished(boolean isSuccessful, Object result);
    }

    private static SessionPciListener sCallback;
    private static boolean sIsLoggedIn;

    private PciLoginManager() {
    }

    public static boolean isPciLoggedIn(){
        return PosInstance.getInstance().getDongle() != null
                && sIsLoggedIn;
    }

    public static void realizarSessionPCI(final SessionPciListener callback) throws SQLException{
        sCallback = callback;
        final DatosSesion datosSesion = SesionBuilder.buildSessionPci(MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvpasswordpci(), getQposSessionKeys());
        ApiData.APIDATA.setDatosSesionPCI(datosSesion);
        enviarLoginPCI();
    }

    public static void enviarLoginPCI() {
        if (sCallback == null) {
            return;
        }

        sCallback.loginInProcess();
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(TipoOperacion.PCI_LOGIN, abstractRespuesta -> {
            if (abstractRespuesta.isCorrecta()) {
                sIsLoggedIn = true;
                if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                    TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                            ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                            .realizarOperacion();
                } else {
                    sCallback.loginProcessFinished(true, abstractRespuesta);
                }
            }else{
                sIsLoggedIn = false;
                sCallback.loginProcessFinished(false, abstractRespuesta);
            }
        }, throwable -> {
            sIsLoggedIn = false;
            sCallback.loginProcessFinished(false, throwable);
        });
        transaccion.realizarOperacion();
    }

    private static QPOSSessionKeys getQposSessionKeys() {
        HashMap<String, String> sessionKeys = new HashMap<>();
        sessionKeys.putAll(PosInstance.getInstance().getSessionKeys());
        return new QPOSSessionKeys(
                HexUtil.hex2byte(sessionKeys.get("enDataCardKey"), StandardCharsets.ISO_8859_1),
                HexUtil.hex2byte(sessionKeys.get("enPinKcvKey").substring(0, 6), StandardCharsets.ISO_8859_1),
                HexUtil.hex2byte(sessionKeys.get("enPinKey"), StandardCharsets.ISO_8859_1),
                HexUtil.hex2byte(sessionKeys.get("rsaReginLen"), StandardCharsets.ISO_8859_1),
                HexUtil.hex2byte(sessionKeys.get("enKcvDataCardKey").substring(0, 6), StandardCharsets.ISO_8859_1),
                HexUtil.hex2byte(sessionKeys.get("rsaReginString"), StandardCharsets.ISO_8859_1));
    }
}
