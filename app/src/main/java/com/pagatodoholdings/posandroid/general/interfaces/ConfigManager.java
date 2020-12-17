package com.pagatodoholdings.posandroid.general.interfaces;

import java.security.PublicKey;

public interface ConfigManager {
    String getConfigTipoApk();

    String getVersionBdApp();

    boolean isPermiteCambioSerie();

    boolean impresoraHabilitada();

    String getConfigSerie();

    String getConfigClaveTPV();

    String getConfigBuildType();

    boolean isDebugEnable();

    String getTipoLog();

    int getVersionCode();

    String getVersionName();

    String getQposFirmware();

    String getUrlRegistro();

    String getIpServer();

    int getPuerto();

    int getTimeOutConnection();

    int getTimeOutResponse();

    byte[] getClaveHexLocal();

    byte[] getClaveHexPci();

    String getNameRsa();

    String getNameRsaPci();

    String getPaisCode();

    String getUrlContacto();

    String getEmailContacto();

    String getNumeroContacto();

    boolean isIgnorarCertificadoSSL();

    String getUrlSyn();

    int getTicketSizeSmall();

    int getTicketTextSizeNormal();

    int getTicketTextSizeMedium();

    int getTicketTextSizeLarge();


    int getTextSizeSmall();

    int getTextSizeNormal();

    int getTextSizeMedium();

    int getTextSizeLarge();

    int getDecimalesPais();

    String getColorAplication();

    int getTimeSyn();

    PublicKey getDERPublicKeyFromPEM();

    String getBuildType();

    String getNotificacionApplicationId();

    Boolean getPayValidation();

}
