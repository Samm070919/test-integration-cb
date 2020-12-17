package com.pagatodoholdings.posandroid.secciones.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TipoDocumentoInteractor {

    private static final String TAG = LoginServiceInteractor.class.getSimpleName();
    private ApiMposServicesInterface service;

    public TipoDocumentoInteractor(String countryUrl) {
        createRetrofitService(countryUrl);
    }

    private void createRetrofitService(String baseUrl) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void getTiposDoc(final RetrofitListener<List<TipoDocBean>> interactorListener){
        final Call<List<TipoDocBean>> call = service.listTiposDoc();
        call.enqueue(new Callback<List<TipoDocBean>>() {
            @Override
            public void onResponse(Call<List<TipoDocBean>> call, Response<List<TipoDocBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener el Tipo Documento"));
                }
            }

            @Override
            public void onFailure(Call<List<TipoDocBean>> call, Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getTiposDocNegocio(final RetrofitListener<List<TipoDocBean>> interactorListener){
        final Call<List<TipoDocBean>> call = service.listTiposDocNegocio();
        call.enqueue(new Callback<List<TipoDocBean>>() {
            @Override
            public void onResponse(Call<List<TipoDocBean>> call, Response<List<TipoDocBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener el Tipo Documento"));
                }
            }

            @Override
            public void onFailure(Call<List<TipoDocBean>> call, Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

}
