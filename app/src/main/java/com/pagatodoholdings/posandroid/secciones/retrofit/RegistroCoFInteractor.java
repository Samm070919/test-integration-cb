package com.pagatodoholdings.posandroid.secciones.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKitCoF;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroCoFInteractor {

    private static final String TAG = RegistroCoFInteractor.class.getSimpleName();
    private final ApiWsPasarelasServicesInterface service;

    public RegistroCoFInteractor(String baseUrl) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiWsPasarelasServicesInterface.class);
    }

    public void registroTarjetaCoF(
            AltaTarjetaCoFBean bean,
            String token,
            String tpvcod,
            String pais,
            String usuario,
            RetrofitListener<DatosTarjetaCoFBean> callback) {
        Call<DatosTarjetaCoFBean> registro = service.addCoF(bean, token, tpvcod, pais, usuario);
        registro.enqueue(new Callback<DatosTarjetaCoFBean>() {
            @Override
            public void onResponse(Call<DatosTarjetaCoFBean> call, Response<DatosTarjetaCoFBean> response) {
                //Si la llamada no fue exitosa
                if (!response.isSuccessful()) {
                    try {
                        Log.d(TAG,response.errorBody().string());
                        callback.onFailure(new Throwable(response.errorBody().string()));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.errorBody().string()), response.errorBody().toString());
                    } catch (IOException exe) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                } else {
                    Logger.LOGGER.info(TAG, 1, response.body().toString());
                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<DatosTarjetaCoFBean> call, Throwable error) {
                Logger.LOGGER.throwing(TAG, 1, error, error.getMessage());
                callback.onFailure(error);
            }
        });
    }

    public void getTarjetas(
            String token,
            String tpvcod,
            String pais,
            String usuario,
            RetrofitListener<List<DatosTarjetaCoFBean>> callback) {
        Call<ArrayList<DatosTarjetaCoFBean>> registro = service.getCards(token, tpvcod, pais, usuario);
        registro.enqueue(new Callback<ArrayList<DatosTarjetaCoFBean>>() {
            @Override
            public void onResponse(Call<ArrayList<DatosTarjetaCoFBean>> call, Response<ArrayList<DatosTarjetaCoFBean>> response) {
                //Si la llamada no fue exitosa
                if (!response.isSuccessful()) {
                    try {
                        Log.d(TAG,response.errorBody().string());
                        callback.onFailure(new Throwable(response.errorBody().string()));
                        //Logger.LOGGER.throwing(TAG, 1, new Throwable(response.errorBody().string()), response.err());
                    } catch (IOException exe) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                } else {

                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DatosTarjetaCoFBean>> call, Throwable error) {
                Logger.LOGGER.throwing(TAG, 1, error, error.getMessage());
                callback.onFailure(error);
            }
        });
    }

    public void deleteTarjetas(
            int idTarjeta,
            String token,
            String tpvcod,
            String pais,
            String usuario,
            RetrofitListener<ResponseBody> callback) {
        Call<ResponseBody> registro = service.deleteCoF(idTarjeta, token, tpvcod, pais, usuario);
        registro.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Si la llamada no fue exitosa
                if (!response.isSuccessful()) {
                    try {
                        Log.d(TAG,response.errorBody().string());
                        callback.onFailure(new Throwable(response.errorBody().string()));
                        //Logger.LOGGER.throwing(TAG, 1, new Throwable(response.errorBody().string()), response.err());
                    } catch (IOException exe) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                } else {

                    callback.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable error) {
                Logger.LOGGER.throwing(TAG, 1, error, error.getMessage());
                callback.onFailure(error);
            }
        });
    }

    public void comptraImporteCoF(DatosCompraKitCoF datosImporte, final String pais, final String token, final String tpvcod, final String usuario, final RetrofitListener<InfoCompraKitCoF> interactorListener)
    {
        final Call<InfoCompraKitCoF> call = service.compraKitCoF(datosImporte,pais, token,tpvcod, usuario);
        call.enqueue(new Callback<InfoCompraKitCoF>() {
            @Override
            public void onResponse(final @NonNull Call<InfoCompraKitCoF> call, final @NonNull Response<InfoCompraKitCoF> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    try {
                        interactorListener.onFailure(new IllegalStateException(response.errorBody().string()));
                    } catch (IOException e) {
                        interactorListener.onFailure(new IllegalStateException(response.message()));
                    }
                }
            }
            @Override
            public void onFailure(final @NonNull Call<InfoCompraKitCoF> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }
}
