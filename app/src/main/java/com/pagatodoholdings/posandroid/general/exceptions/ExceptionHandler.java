package com.pagatodoholdings.posandroid.general.exceptions;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.utils.Logger;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.ISUPDATING;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIEMPO_INACTIVIDAD;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final AbstractActivity context;
    private static Thread.UncaughtExceptionHandler crashlyticsHandler;

    public ExceptionHandler(final AbstractActivity context) {
        this.context = context;
        setCrashlyticsHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    private static void setCrashlyticsHandler() {

        synchronized (ExceptionHandler.class) {
            if (crashlyticsHandler == null) {
                crashlyticsHandler = Thread.getDefaultUncaughtExceptionHandler();
            }
        }


    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable exc) {
        Log.d(ExceptionHandler.class.getSimpleName(), "uncaughtException() called with: thread = [" + thread + "], exc = [" + exc + "]");
        appendDeviceInfo();
        crashlyticsHandler.uncaughtException(thread, exc);
        if(MposApplication.getInstance().getPreferenceManager().getValue(ISUPDATING)  == 1){

            context.restaurarDespuesDeUpdateFirmware();
        }else {
            context.restauraEnLugarDeCrash();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static void appendDeviceInfo() {

        final ConfigManager configManager = MposApplication.getInstance().getConfigManager();
        final PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();

        try {
            Crashlytics.setString("ConfigManager.getConfigBuildType", configManager.getConfigBuildType());
            Crashlytics.setString("ConfigManager.getConfigClaveTPV", preferenceManager.isExiste(Constantes.Preferencia.DATOS_SESION) ? preferenceManager.getDatosSesion().getDatosTPV().getTpvcod() : "Empty");
            Crashlytics.setString("ConfigManager.getConfigSerie", configManager.getConfigSerie());
            Crashlytics.setString("ConfigManager.getConfigTipoApk", configManager.getConfigTipoApk());
            Crashlytics.setInt("ConfigManager.getVersionCode", configManager.getVersionCode());
            Crashlytics.setString("ConfigManager.getVersionName", configManager.getVersionName());
            Crashlytics.setInt("PreferenceManager.getValue(PreferenceManager.Preferencia.TIEMPO_INACTIVIDAD, \"\")", preferenceManager.getValue(TIEMPO_INACTIVIDAD));
        } catch (Exception exe) {
            Logger.LOGGER.throwing("appendTransactionSigmaInfo", 1, exe, exe.getMessage());
        }
    }

}
