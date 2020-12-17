package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroMerchantInteractor {

    private static final String TAG = LoginServiceInteractor.class.getSimpleName();
    private final ApiMposServicesInterface service;

    public RegistroMerchantInteractor() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL_REGISTRO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void registroMerchant(final Merchant merchant, final RetrofitListener interactorListener, String token) {

        final Call<Merchant> call = service.registroMerchant(merchant, token);

        call.enqueue(new Callback<Merchant>() {

            @Override
            public void onResponse(Call<Merchant> call, Response<Merchant> response) {
                if (response.body() != null && response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    try {
                        interactorListener.onFailure(new Throwable(response.errorBody().string()));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    } catch (IOException exe) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(final Call<Merchant> call, final Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }
}
