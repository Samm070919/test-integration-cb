package com.pagatodoholdings.posandroid.secciones.desbloqueo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.general.interfaces.ConfigManager;
import com.pagatodoholdings.posandroid.secciones.retrofit.ApiMposServicesInterface;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecuperarPasswordInteractor {

    private static final String TAG = RecuperarPasswordInteractor.class.getSimpleName();
    private RetrofitListener interactorListener;
    private final ApiMposServicesInterface service;

    public RecuperarPasswordInteractor(final ConfigManager configManager) {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(configManager.getUrlRegistro())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void recuperarPassword(final String serie){
        final Call<ResponseBody> call = service.recuperarPassword(serie);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null && response.raw().code() == 200) {

                    interactorListener.onSuccess(response);
                } else {
                    interactorListener.showMessage(response.message());
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }

    public void setInteractorListener(final RetrofitListener interactorListener) {
        this.interactorListener = interactorListener;
    }

}
