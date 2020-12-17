package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TransactionService {

    private static final String TAG = TransactionService.class.getSimpleName();
    private ApiMposServicesInterface serviceMpos;

    public TransactionService() {
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


    public void postTransactionMoneyOutResponse(final RetrofitListener interactorListener, String sesionid, String tpvcod, MoneyOutImporteTransaction importe)
    {
        final Call<DatosDesfogue> call = serviceMpos.desfogue(sesionid,tpvcod,importe);
        call.enqueue(new Callback<DatosDesfogue>() {
            @Override
            public void onResponse(final @NonNull Call<DatosDesfogue> call, final @NonNull Response<DatosDesfogue> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al realizar Transaction"));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<DatosDesfogue> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void postTransactionMoneyInBycard(String sesionid, String tpvcod, String importe, String formapago, final RetrofitListener interactorListener)
    {
        final Call<InfoPagoRegistrado> call = serviceMpos.registraPago(sesionid, tpvcod, importe, formapago);
        call.enqueue(new Callback<InfoPagoRegistrado>() {
            @Override
            public void onResponse(final @NonNull Call<InfoPagoRegistrado> call, final @NonNull Response<InfoPagoRegistrado> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al realizar Transaction"));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<InfoPagoRegistrado> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getSaldo(String sesionid, String tpvcod, final RetrofitListener interactorListener)
    {
        final Call<SaldosResponseBean> call = serviceMpos.getSaldo(tpvcod, sesionid);
        call.enqueue(new Callback<SaldosResponseBean>() {
            @Override
            public void onResponse(final @NonNull Call<SaldosResponseBean> call, final @NonNull Response<SaldosResponseBean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Consultar el Saldo"));
                }
            }
            @Override
            public void onFailure(final @NonNull Call<SaldosResponseBean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }


}
