package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouritesServices {

    private static final String TAG = FavouritesServices.class.getSimpleName();
    private ApiMposServicesInterface serviceMpos;


    public FavouritesServices() {
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

    public void getFechasFavoritos(final RetrofitListener<List<FavoritoBean>> interactorListener, String fechaInicio, String fechaFin, String tpvcod, String sesionId){
        final Call<List<FavoritoBean>> call = serviceMpos.listaFechasFavoritos(fechaInicio,fechaFin,tpvcod,sesionId);
        call.enqueue(new Callback<List<FavoritoBean>>() {
            @Override
            public void onResponse(Call<List<FavoritoBean>> call, Response<List<FavoritoBean>> response) {
                if(response.isSuccessful()){
                   interactorListener.onSuccess(response.body());
                }else {
                    interactorListener.onFailure(new IllegalAccessException(response.message()));
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()), response.message());
                }
            }

            @Override
            public void onFailure(Call<List<FavoritoBean>> call, Throwable t) {
                Logger.LOGGER.throwing(TAG, 1, t, t.getMessage());
                interactorListener.onFailure(t);
            }
        });
    }

    public void getFavoritos(final RetrofitListener<List<FavoritoBean>> interactorListener, String tpvcod, String sesionId){
        final Call<List<FavoritoBean>> call = serviceMpos.listaFavoritos(tpvcod,sesionId);
        call.enqueue(new Callback<List<FavoritoBean>>() {
            @Override
            public void onResponse(Call<List<FavoritoBean>> call, Response<List<FavoritoBean>> response) {
                if(response.isSuccessful()){
                    interactorListener.onSuccess(response.body());
                }else {
                    interactorListener.onFailure(new IllegalAccessException(response.message()));
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()), response.message());
                }
            }

            @Override
            public void onFailure(Call<List<FavoritoBean>> call, Throwable t) {
                Logger.LOGGER.throwing(TAG, 1, t, t.getMessage());
                interactorListener.onFailure(t);
            }
        });
    }

    public void altaFavorito(final RetrofitListener<Boolean> interactorListener,final FavoritoBean favoritoBean,final String sesionid,final String tpvcod)
    {
        final Call<Boolean> call = serviceMpos.favoritoAlta(favoritoBean,tpvcod,sesionid);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final @NonNull Call<Boolean> call, final @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException(response.message()));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<Boolean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void editarFavorito(final RetrofitListener<Boolean> interactorListener,final FavoritoBean favoritoBean,final String alias, final String sesionid,final String tpvcod)
    {
        final Call<Boolean> call = serviceMpos.favoritoEditar(alias,favoritoBean,tpvcod,sesionid);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final @NonNull Call<Boolean> call, final @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException(response.message()));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<Boolean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void bajaFavorito(final RetrofitListener<Boolean> interactorListener,final String alias,final String sesionid,final String tpvcod)
    {
        final Call<Boolean> call = serviceMpos.favoritoBaja(alias,tpvcod,sesionid);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final @NonNull Call<Boolean> call, final @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException(response.message()));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<Boolean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void pagarFavorito(final RetrofitListener<Boolean> interactorListener,final String fecha,final String alias,final String sesionid,final String tpvcod)
    {
        final Call<Boolean> call = serviceMpos.favoritoPagado(tpvcod,sesionid,alias,fecha);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(final @NonNull Call<Boolean> call, final @NonNull Response<Boolean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException(response.message()));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<Boolean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void consultaFavoritosPrincipales(final RetrofitListener<ResponseBody> interactorListener, String sesionId, String tpvcod){
        final Call<ResponseBody> call = serviceMpos.favoritosPrincipales(sesionId,tpvcod);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    interactorListener.onSuccess(response.body());
                }else {
                    interactorListener.onFailure(new IllegalAccessException(response.message()));
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()), response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.LOGGER.throwing(TAG, 1, t, t.getMessage());
                interactorListener.onFailure(t);
            }
        });
    }
}
