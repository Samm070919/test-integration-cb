package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.PaisConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroPaises;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroPaisesInteractor {

    private static final String TAG = RegistroTpvInteractor.class.getSimpleName();
    private final ApiMposServicesInterface service;

    public RegistroPaisesInteractor() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL_REGISTRO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void registroPaises(final RetrofitListener interactorListener) {
        final Call<ArrayList<RegistroPaises>> call = service.registroPaises(); //72902408
        call.enqueue(new Callback<ArrayList<RegistroPaises>>() {

            @Override
            public void onResponse(Call<ArrayList<RegistroPaises>> call, Response<ArrayList<RegistroPaises>> response) {
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
            public void onFailure(final Call<ArrayList<RegistroPaises>> call, final Throwable exc) {
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                interactorListener.onFailure(exc);
            }
        });
    }

    public void configPais(final RetrofitListener<PaisConfig> retrofitListener, String countryCode) {
        final Call<PaisConfig> call = service.paisConfig(countryCode);
        call.enqueue(new Callback<PaisConfig>() {
            @Override
            public void onResponse(Call<PaisConfig> call, Response<PaisConfig> response) {
                if (response.isSuccessful() && response.body() != null) {
                    retrofitListener.onSuccess(response.body());
                } else {
                    try {
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                        retrofitListener.onFailure(new Throwable(response.errorBody().string()));
                    } catch (IOException exe) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PaisConfig> call, Throwable t) {
                Logger.LOGGER.throwing(TAG, 1, t, t.getMessage());
                retrofitListener.onFailure(t);
            }
        });
    }
}
