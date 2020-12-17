package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import org.json.JSONObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VincularQRService {

    private static final String TAG = VincularQRService.class.getSimpleName();
    private ApiMposServicesInterface serviceMpos;

    public VincularQRService() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofitMpos = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        serviceMpos = retrofitMpos.create(ApiMposServicesInterface.class);
    }

    public void vincularQR (final String qrData ,final String plate,  final String sesionid,final String tpvcod , final RetrofitListener<ResponseBody> interactorListener)
    {
        final Call<ResponseBody> call = serviceMpos.vincularQr(plate,qrData ,sesionid,tpvcod);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final @NonNull Call<ResponseBody> call, final @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    try {
                        JSONObject responseObject =  new JSONObject(response.errorBody().string());
                        final String FailureResponce = responseObject.getString("message");
                        interactorListener.onFailure(new Throwable(FailureResponce));
                    }catch ( Exception exe ){
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                        interactorListener.onFailure( new Throwable("Error al Vincular"));
                    }
                }
            }
            @Override
            public void onFailure(final @NonNull Call<ResponseBody> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }






}
