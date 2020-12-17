package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFirebaseService {

    private static final String TAG = RegisterFirebaseService.class.getSimpleName();
    private ApiMposServicesInterface service;


    public RegisterFirebaseService() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void registerFirebase(final String sesionid,final String tpvcod , final RetrofitListener<Boolean> interactorListener)
    {
        final Call<Boolean> call = service.alta_firebase(sesionid,tpvcod);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final @NonNull Call<Boolean> call, final @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error Registrar TPV Firebase"));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<Boolean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

}
