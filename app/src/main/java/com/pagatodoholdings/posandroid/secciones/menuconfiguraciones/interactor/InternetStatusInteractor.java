package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.interactor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.utils.Logger;

public class InternetStatusInteractor extends AsyncTask<Void, Integer, Void> {

    private static final String TAG = InternetStatusInteractor.class.getSimpleName();
    private final Context context;
    private final InteractorListener interfaceWifi;

    public InternetStatusInteractor(final Context contexto, final InteractorListener interfacewifi) {
        this.context = contexto;
        this.interfaceWifi = interfacewifi;
    }

    @Override
    protected Void doInBackground(final Void... voids) {
        int repeat = 0;
        while (repeat < 5) {

            isConnectedInternet();
            repeat++;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                break;
            }
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(final Integer... values) {
        super.onProgressUpdate(values);
        interfaceWifi.onWifiStatusUpdate(values[0], values[1], values[2]);
    }

    private void isConnectedInternet() {
        Integer mbps = 0;
        Integer typeconn = 0;
        Integer stateconn = 0;
        int speedMbps = 0;
        final ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo connection = connManager.getActiveNetworkInfo();

        if (connection != null) {
            if (connection.getType() == ConnectivityManager.TYPE_WIFI) {
                final WifiManager connManagere = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                final WifiInfo wifiInfo = connManagere.getConnectionInfo();
                Logger.LOGGER.fine(TAG, "Conectado a wifi");
                typeconn = R.string.wifi_title;
                stateconn = R.string.wifi_conectado;
                speedMbps = wifiInfo.getLinkSpeed();
                mbps = speedMbps;

            } else if (connection.getType() == ConnectivityManager.TYPE_MOBILE) {
                final TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Logger.LOGGER.fine(TAG, "Conectado a datos");
                typeconn = R.string.datos_moviles_title;
                stateconn = R.string.datos_moviles_conectado;
                speedMbps = tel.getDataActivity();
                mbps = speedMbps;
            } else if (connection.getType() == ConnectivityManager.TYPE_ETHERNET) {
                Logger.LOGGER.fine(TAG, "Conectado a ethernet");
                mbps = 0;
                stateconn = R.string.ethernet_conectado;
                typeconn = R.string.ethernet_title;
            }
            publishProgress(mbps, stateconn, typeconn);
        } else {
            Thread.currentThread().interrupt();
            interfaceWifi.onErrorWifi(MposApplication.getInstance().getResources().getString(R.string.no_internet));
        }
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        interfaceWifi.finish();
    }

    public interface InteractorListener {
        void onWifiStatusUpdate(Integer mbps, Integer stateconn, Integer typeconn);

        void onErrorWifi(String message);

        void finish();
    }
}

