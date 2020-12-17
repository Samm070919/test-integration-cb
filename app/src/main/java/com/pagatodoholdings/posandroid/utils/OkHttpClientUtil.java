package com.pagatodoholdings.posandroid.utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public final class OkHttpClientUtil {

    public static final String TLSV1_2 = "TLSv1.2";

    private OkHttpClientUtil() {

    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkServerTrusted(final java.security.cert.X509Certificate[] chain, final String authType) { //NOSONAR
                            Logger.LOGGER.info(OkHttpClientUtil.class.getSimpleName(),"no se valida ningun certificado");
                        }

                        @Override
                        public void checkClientTrusted(final java.security.cert.X509Certificate[] chain, final String authType)  { //NOSONAR
                            Logger.LOGGER.info(OkHttpClientUtil.class.getSimpleName(),"no se valida ningun certificado");
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance(TLSV1_2);
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]) //NOTA: el metodo con solo el socketFactory esta deprecado
                    .connectTimeout(15, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.MINUTES)
                    .writeTimeout(15, TimeUnit.MINUTES)
                    .hostnameVerifier((hostname, session) -> hostname != null).addInterceptor(interceptor).connectTimeout(60,TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build();

        } catch (NoSuchAlgorithmException | KeyManagementException exc) {
            throw new IllegalStateException(exc);
        }
    }
}
