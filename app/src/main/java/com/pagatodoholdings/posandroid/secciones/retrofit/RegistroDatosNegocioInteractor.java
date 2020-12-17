package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosNegocio;
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

public class RegistroDatosNegocioInteractor {

    private static final String TAG = LoginServiceInteractor.class.getSimpleName();
    private final ApiMposServicesInterface service;

    public RegistroDatosNegocioInteractor() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL_REGISTRO)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void registroDatosNegocio(final DatosNegocio datosNegocio, final String token, final RetrofitListener interactorListener) {

        final Call<DatosLogin> call = service.registroDatosNegocio(datosNegocio, token); //72902408

        call.enqueue(new Callback<DatosLogin>() {

            @Override
            public void onResponse(Call<DatosLogin> call, Response<DatosLogin> response) {
                if (response.body() != null && response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    try {
                        String messageError;
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        messageError = jsonObject.getString("message");
                        interactorListener.showMessage(messageError);
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    } catch (JSONException e) {
                        Logger.LOGGER.throwing("Mensaje error", 1, new Throwable("Error"), "JSONException: " + e.getMessage());
                        interactorListener.onFailure(e);
                    } catch (IOException e) {
                        Logger.LOGGER.throwing("Mensaje error", 1, new Throwable("Error"), "IOException: " + e.getMessage());
                        interactorListener.onFailure(e);
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
