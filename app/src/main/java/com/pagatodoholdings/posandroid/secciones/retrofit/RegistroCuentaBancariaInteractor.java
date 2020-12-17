package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroCuentaBancariaInteractor {

    private static final String TAG = RegistroCuentaBancariaInteractor.class.getSimpleName();
    private final ApiMposServicesInterface service;

    public RegistroCuentaBancariaInteractor(String baseUrl) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void registroCuentaBancaria(
            AltaCuentaBean bean,
            String sesionid,
            String tpvcod,
            RetrofitListener<Boolean> callback) {
        Call<Boolean> registro = service.registraCuenta(bean, sesionid, tpvcod);
        registro.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                //Si la llamada no fue exitosa
                if (!response.isSuccessful()) {
                    try {
                        callback.onFailure(new Throwable(response.errorBody().string()));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    } catch (IOException exe) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                } else {
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable error) {
                Logger.LOGGER.throwing(TAG, 1, error, error.getMessage());
                callback.onFailure(error);
            }
        });
    }
}
