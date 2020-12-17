package com.pagatodoholdings.posandroid.utils;

import android.util.Log;

import net.fullcarga.android.api.log.FullLogger;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.logging.Level;

import static net.fullcarga.android.api.util.StringUtil.parseParams;

public enum Logger implements FullLogger {

    LOGGER;
    private static final String TAG = Logger.class.getSimpleName();

    @Override
    public void fine(final String TAG, final String mensaje) {
        Log.i(TAG, mensaje);
    }

    @Override
    public void fine(final String TAG, final String mensaje, final Object... params) {
        Log.i(TAG, parseParams(mensaje, params));
    }

    @Override
    public void fine(final String TAG, final int error, final String mensaje) {
        Log.i(TAG, mensaje + "{" + error + "}");
    }

    @Override
    public void fine(final String TAG, final int error, final String mensaje, final Object...
            params) {
        Log.i(TAG, parseParams(mensaje, params) + "{" + error + "}");
    }

    @Override
    public void finest(final String TAG, final String mensaje) {
        Log.i(TAG, mensaje);
    }

    @Override
    public void finest(final String TAG, final String mensaje, final Object... params) {
        Log.i(TAG, parseParams(mensaje, params));
    }

    @Override
    public void finest(final String TAG, final int error, final String mensaje) {
        Log.i(TAG, mensaje + "{" + error + "}");
    }

    @Override
    public void finest(final String TAG, final int error, final String mensaje, final Object...
            params) {
        Log.i(TAG, parseParams(mensaje, params) + "{" + error + "}");
    }

    @Override
    public void info(final String TAG, final String mensaje) {
        Log.i(TAG, mensaje);

    }

    @Override
    public void info(final String TAG, final String mensaje, final Object... params) {
        Log.i(TAG, parseParams(mensaje, params));

    }

    @Override
    public void info(final String TAG, final int error, final String mensaje) {
        Log.i(TAG, mensaje + "{" + error + "}");

    }

    @Override
    public void info(final String TAG, final int error, final String mensaje, final Object...
            params) {
        Log.i(TAG, parseParams(mensaje, params) + "{" + error + "}");

    }

    @Override
    public void setLevel(final Level level) {  //NOSONAR
        Log.i(TAG, "setLevel ->" + level);  //NOSONAR
    }  //NOSONAR

    /**
     * Se activa el tag No sonar ya que estos metodos son Heredados de clases
     * de la libreria de sigma
     */
    @Override
    public void setLimite(final long limit) {  //NOSONAR
        Log.i(TAG, "setLimite ->" + limit);  //NOSONAR
    }  //NOSONAR

    @Override
    public void sql(final String TAG, final String mensaje, final Object... params) {
        Log.i(TAG, parseParams(mensaje, params));

    }

    @Override
    public void throwing(final String TAG, final int error, final Throwable thrwbl,
                         final String mensaje) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(thrwbl);
        Log.e(TAG, mensaje, thrwbl);
    }

    @Override
    public void throwing(final String TAG, final int error, final Throwable thrwbl,
                         final String mensaje, final Object... params) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(thrwbl);
        Log.e(TAG, parseParams(mensaje, params) + "{" + error + "}", thrwbl);
    }

    @Override
    public void throwing(final String TAG, final int error, final SQLException sqle,
                         final String mensaje) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(sqle);
        Log.e(TAG, mensaje + "{" + error + "}", sqle);
    }

    @Override
    public void throwing(final String TAG, final int error, final SQLException sqle,
                         final String mensaje, final Object... params) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(sqle);
        Log.e(TAG, parseParams(mensaje, params) + "{" + error + "}", sqle);
    }

    @Override
    public void throwing(final String TAG, final int error,
                         final SocketException socketException, final String mensaje) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(socketException);
        Log.e(TAG, mensaje + "{" + error + "}", socketException);
    }

    @Override
    public void throwing(final String TAG, final int error,
                         final SocketException socketException, final String mensaje, final Object... params) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(socketException);
        Log.e(TAG, parseParams(mensaje, params) + "{" + error + "}", socketException);
    }

    @Override
    public void throwing(final String TAG, final int error,
                         final SocketTimeoutException sockettimeoutexception, final String mensaje) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(sockettimeoutexception);
        Log.e(TAG, mensaje + "{" + error + "}", sockettimeoutexception);
    }

    @Override
    public void throwing(final String TAG, final int error, final SocketTimeoutException sockettimeoutexception, final String mensaje, final Object... params) {
//        Utilities.appendDeviceInfo();
        //Crashlytics.logException(sockettimeoutexception);
        Log.e(TAG, parseParams(mensaje, params) + "{" + error + "}", sockettimeoutexception);
    }

    @Override
    public void warning(final String TAG, final int error, final String mensaje) {
        Log.i(TAG, mensaje + "{" + error + "}");

    }

    @Override
    public void warning(final String TAG, final int error, final String mensaje, final Object... params) {
        Log.i(TAG, parseParams(mensaje, params) + "{" + error + "}");
    }


}
