package com.pagatodoholdings.posandroid.manager;

import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.utils.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import static com.pagatodo.sigmalib.util.Base64Util.b64decode;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

public class AppConfigManager implements ConfigManager {

    private static final String TAG = AppConfigManager.class.getSimpleName();

    @Override
    public String getConfigTipoApk() {
        return BuildConfig.TIPO_REGISTRO;
    }

    @Override
    public String getVersionBdApp() { return BuildConfig.VERSION_BD_APP; }

    @Override
    public boolean isPermiteCambioSerie() {
        return BuildConfig.CAMBIO_SERIAL;
    }

    @Override
    public boolean impresoraHabilitada() { return BuildConfig.HABILITA_IMPRESORA; }

    @Override
    public String getConfigSerie() {
        return BuildConfig.SERIAL_KEY;
    }

    @Override
    public String getConfigClaveTPV() {
        return BuildConfig.POS_KEY;
    }

    @Override
    public String getConfigBuildType() {
        return BuildConfig.BUILD_TYPE;
    }

    @Override
    public boolean isDebugEnable() {
        return "debug".equals(BuildConfig.BUILD_TYPE);
    }

    @Override
    public String getTipoLog() {
        return BuildConfig.TIPO_LOG;
    }

    @Override
    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getQposFirmware() {
        return BuildConfig.QPOS_VERSION;
    }

    @Override
    public String getUrlRegistro() {

        return BuildConfig.URL_REGISTRO;
    }

    @Override
    public String getIpServer() {
        return BuildConfig.IP_SERVER;
    }

    @Override
    public int getPuerto() {
        return BuildConfig.PUERTO;
    }

    @Override
    public int getTimeOutConnection() {
        return BuildConfig.TIME_OUT_CONNNECTION;
    }

    @Override
    public int getTimeOutResponse() {
        return BuildConfig.TIME_OUT_RESPONSE;
    }

    @Override
    public byte[] getClaveHexLocal() {
        return leerClave(getNameRsa());
    }

    @Override
    public byte[] getClaveHexPci() {
        return leerClave(getNameRsaPci());
    }

    @Override
    public String getNameRsa() {
        return BuildConfig.NAME_RSA;
    }

    @Override
    public String getNameRsaPci() {
        return BuildConfig.NAME_RSA_PCI;
    }

    @Override
    public String getPaisCode() {
        return "0170";
    }

    @Override
    public String getUrlContacto() {
        return null;
    }

    @Override
    public String getEmailContacto() {
        return null;
    }

    @Override
    public String getNumeroContacto() {
        return null;
    }

    @Override
    public boolean isIgnorarCertificadoSSL() {
        return BuildConfig.IGNORAR_CERTIFICADOS_SSL;
    }

    @Override
    public String getUrlSyn() {
        return BuildConfig.URL_SYN;
    }

    @Override
    public int getTicketSizeSmall() {
        return BuildConfig.TEXT_SIZE_SMALL;
    }

    @Override
    public int getTicketTextSizeNormal() {
        return BuildConfig.TEXT_SIZE_NORMAL;
    }

    @Override
    public int getTicketTextSizeMedium() {
        return BuildConfig.TEXT_SIZE_MEDIUM;
    }

    @Override
    public int getTicketTextSizeLarge() {
        return BuildConfig.TEXT_SIZE_LARGE;
    }

    @Override
    public int getTextSizeSmall() {
        return BuildConfig.TEXT_SIZE_VIEW_SMALL;
    }

    @Override
    public int getTextSizeNormal() {
        return BuildConfig.TEXT_SIZE_VIEW_NORMAL;
    }

    @Override
    public int getTextSizeMedium() {
        return BuildConfig.TEXT_SIZE_VIEW_MEDIUM;
    }

    @Override
    public int getTextSizeLarge() {
        return BuildConfig.TEXT_SIZE_VIEW_LARGE;
    }

    @Override
    public int getDecimalesPais() {
        return BuildConfig.DECIMALES_PAISES;
    }

    @Override
    public String getColorAplication() { return "AZUL"; }

    @Override
    public int getTimeSyn() {
        return BuildConfig.TIME_SYN;
    }

    @Override
    public String getBuildType() { return BuildConfig.BUILD_TYPE; }

    @Override
    public String getNotificacionApplicationId() {
        /*
        ADVERTENCIA NO CAMBIAR
        pagatodo.com.totemmp es el aplicationId de algunos dispositivos que estan en produccion, se reemplaza por com.pagatodo.apidemo
        que es el id correcto para que encuentre el nodo de las notificaciones
         */
        return BuildConfig.APPLICATION_ID.replace("pagatodo.com.totemmp", "com.pagatodo.apidemo").replace(".", "_");
    }

    @Override
    public Boolean getPayValidation() {
        return false;
    }

    private static final byte[]  leerClave( final String assetFile) {
        try {
            return b64decode(
                            new String(leerAsset(assetFile), ISO_8859_1)
                                    .replace("-----BEGIN PUBLIC KEY-----", "")
                                    .replace("-----END PUBLIC KEY-----", "")
                                    .replace("\r\n", "")
                                    .replace("\n", "")
                    );

        } catch (IOException exc) {
            throw new IllegalStateException(exc);
        }
    }




    public PublicKey getDERPublicKeyFromPEM() {
        try {
            // strip of header, footer, newlines, whitespaces
            final String publicKeyPEM = new String(leerAsset(BuildConfig.NAME_RSA_PCI), ISO_8859_1)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replace("\r\n", "")
                    .replace("\n", "");

            // decode to get the binary DER representation
            final byte[] publicKeyDER = b64decode(publicKeyPEM);

            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyDER));
        } catch (Exception exe) {
            Logger.LOGGER.throwing(TAG,1,exe,exe.getMessage());
            return null;
        }
    }

        public static byte[] leerAsset(final String assetsFile) throws IOException {
        try (final InputStream inputStream = MposApplication.getInstance().getAssets().open(assetsFile)) {
            final byte[] buffer = new byte[(int) inputStream.available()];
            if (inputStream.read(buffer) > 0) {
                return buffer;
            }
            return new byte[0];
        }
    }
}
