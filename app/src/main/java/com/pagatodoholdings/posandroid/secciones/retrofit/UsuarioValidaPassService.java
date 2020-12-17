package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosTPV;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioValidaPassService {

    private static final String TAG = UsuarioValidaPassService.class.getSimpleName();
    private ApiWsPasarelasServicesInterface serviceWs;

    public UsuarioValidaPassService(){
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofitMpos = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL_REGISTRO)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        serviceWs = retrofitMpos.create(ApiWsPasarelasServicesInterface.class);
    }
    public void getPassUser(final String email,final String passwordUser,final String serie,final RetrofitListener<DatosTPV> interactorListener){

        final Call<DatosTPV> call  = serviceWs.validaContrasena(email,passwordUser,serie);

        call.enqueue(new Callback<DatosTPV>() {
            @Override
            public void onResponse(Call<DatosTPV> call, Response<DatosTPV> response) {
                if(response.isSuccessful()){
                    interactorListener.onSuccess(response.body());
                }else {
                    interactorListener.showMessage(response.message());
                    interactorListener.onFailure(new IllegalAccessException(response.message()));
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()), response.message());
                }
            }

            @Override
            public void onFailure(Call<DatosTPV> call, Throwable t) {
                Logger.LOGGER.throwing(TAG, 1, t, t.getMessage());
                interactorListener.onFailure(t);
            }
        });
    }
}
