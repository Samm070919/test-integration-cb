package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PagoQrServiceInteractor {

    private static final String TAG = PagoQrServiceInteractor.class.getSimpleName();

    private ApiMposServicesInterface serviceWallet;


    public PagoQrServiceInteractor() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofitWallet = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlqr())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        serviceWallet = retrofitWallet.create(ApiMposServicesInterface.class);
    }

    public void enviarPagoQr(final RetrofitListener<PagoQrData> interactorListener, String sessionId, String tpvcod, QrWalletPago walletPago) {
        final Call<DatosPagoQr> call = serviceWallet.enviarPagoQr(walletPago, sessionId, tpvcod);
        call.enqueue(new Callback<DatosPagoQr>() {
            @Override
            public void onResponse(Call<DatosPagoQr> call, Response<DatosPagoQr> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getSuccess()){
                        interactorListener.onSuccess(response.body().getData());
                    }
                } else {
                    try {
                        String messageError;
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        messageError = jsonObject.getJSONArray("errorList").getJSONObject(0).getString("description");
                        interactorListener.showMessage(messageError);
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    } catch (JSONException e) {
                        Logger.LOGGER.throwing(TAG, 1, new Throwable("Error"), "JSONException: " + e.getMessage());
                        interactorListener.onFailure(e);
                    } catch (IOException e) {
                        Logger.LOGGER.throwing(TAG, 1, new Throwable("Error"), "IOException: " + e.getMessage());
                        interactorListener.onFailure(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<DatosPagoQr> call, Throwable t) {
                Logger.LOGGER.throwing(TAG, 1, t, t.getMessage());
                interactorListener.onFailure(t);
            }
        });
    }
}
