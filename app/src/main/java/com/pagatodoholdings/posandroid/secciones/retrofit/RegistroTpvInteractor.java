package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroTpvInteractor {

    private static final String TAG = RegistroTpvInteractor.class.getSimpleName();
    private final ApiMposServicesInterface service;

    public RegistroTpvInteractor() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL_REGISTRO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void registroTpv( final  RetrofitListener interactorListener, final TpvRegistro tpvRegistro) {
        final Call<DatosLogin> call = service.registroTpv(tpvRegistro); //72902408
        call.enqueue(new Callback<DatosLogin>() {

            @Override
            public void onResponse(Call<DatosLogin> call, Response<DatosLogin> response) {
                if (response.body() != null && response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    try {
                        interactorListener.onFailure(new Throwable(response.errorBody().string()));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    }
                    catch ( IOException exe ) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(final Call<DatosLogin> call, final Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }
}
