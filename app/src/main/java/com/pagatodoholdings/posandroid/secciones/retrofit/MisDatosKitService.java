package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.ImporteBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.ImporteKit;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MisDatosKitService {

    private static final String TAG = MisDatosKitService.class.getSimpleName();
    private ApiMposServicesInterface serviceWalletCo;
    private String errorMessage = "Error al Consultar";

    public MisDatosKitService() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofitWalletCo = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        serviceWalletCo = retrofitWalletCo.create(ApiMposServicesInterface.class);
    }

    public void consultarImporteKit(final RetrofitListener<ImporteKit> interactorListener, ImporteBean datosImporte)
    {
        final Call<ImporteKit> call = serviceWalletCo.consultarImporteKit(datosImporte);
        call.enqueue(new Callback<ImporteKit>() {
            @Override
            public void onResponse(final @NonNull Call<ImporteKit> call, final @NonNull Response<ImporteKit> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    try {
                        JSONObject responseObject =  new JSONObject(response.errorBody().string());
                        final String FailureResponce = responseObject.getString("message");
                        interactorListener.onFailure(new Throwable(FailureResponce));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    }catch ( Exception exe ){
                        Logger.LOGGER.throwing(TAG,1,  new Throwable(errorMessage),new Throwable(errorMessage).getMessage());
                        interactorListener.onFailure( new Throwable(errorMessage));
                    }
                }
            }
            @Override
            public void onFailure(final @NonNull Call<ImporteKit> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

}
