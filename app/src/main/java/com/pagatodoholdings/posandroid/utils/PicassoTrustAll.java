package com.pagatodoholdings.posandroid.utils;

import android.content.Context;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

/*
* Clase que emplea la librería Picasso para obtener imagenes desde sitios seguros (HTTPS)
*
 */
public class PicassoTrustAll {
    private static Picasso mInstance = null;
    private static final  String TAG = PicassoTrustAll.class.getSimpleName();

    private PicassoTrustAll(){}

    private static void PicassoTrustInit(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier((s, sslSession) -> s.equalsIgnoreCase(sslSession.getPeerHost()));
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
                //NOT IMPLEMENTED
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws java.security.cert.CertificateException {
                //NOT IMPLEMENTED
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
        } };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
        } catch (Exception exe) {
            LOGGER.throwing(TAG,1,exe,exe.getMessage());
        }

        mInstance = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(client))
                .listener((picasso, uri, exception) -> LOGGER.throwing(TAG, 1, exception, exception.getMessage())).build();

    }

    public static Picasso getInstance(Context context) {
        if (mInstance == null) {
            new PicassoTrustAll();
            PicassoTrustInit(context);
        }
        return mInstance;
    }
}
