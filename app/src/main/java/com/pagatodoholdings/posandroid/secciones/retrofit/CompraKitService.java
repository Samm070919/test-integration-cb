package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompraKitService {

    private static final String TAG = CompraKitService.class.getSimpleName();
    private ApiMposServicesInterface serviceWalletCo;


    public CompraKitService() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofitWalletCo = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlwalletpublica())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        serviceWalletCo = retrofitWalletCo.create(ApiMposServicesInterface.class);
    }

    public void comptraImporteKit(DatosCompraKit datosImporte,final String sesionid,final String tpvcod , final RetrofitListener<InfoCompraKit> interactorListener)
    {
        final Call<InfoCompraKit> call = serviceWalletCo.compraKit(datosImporte,sesionid,tpvcod);
        call.enqueue(new Callback<InfoCompraKit>() {
            @Override
            public void onResponse(final @NonNull Call<InfoCompraKit> call, final @NonNull Response<InfoCompraKit> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Realizar la Operación"));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<InfoCompraKit> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

}
