package com.pagatodoholdings.posandroid.secciones.money_in.retrofit;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank;
import com.pagatodoholdings.posandroid.secciones.money_in.models.CodeBar;
import com.pagatodoholdings.posandroid.secciones.money_in.models.PagoPse;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoneyInInteractor {

    private MoneyInApiInterface service;

    private static final String TAG = MoneyInInteractor.class.getSimpleName();

    public MoneyInInteractor(final String baseUrl) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(MoneyInApiInterface.class);
    }

    public void searchBankList(final RetrofitListener interactorListener) {

        Call<List<Bank>> bankListCall = service.getBankList();
        bankListCall.enqueue(new Callback<List<Bank>>() {
            @Override
            public void onResponse(Call<List<Bank>> call, Response<List<Bank>> response) {
                interactorListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Bank>> call, Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });

    }

    public void searchCodeBar(final String tpvCode, final RetrofitListener interactorListener) {

        Call<CodeBar> codeBarCall = service.getClientCode(tpvCode);
        codeBarCall.enqueue(new Callback<CodeBar>() {
            @Override
            public void onResponse(Call<CodeBar> call, Response<CodeBar> response) {
                interactorListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<CodeBar> call, Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }

    public void postRegistroPse (final float importe, final String sesionId, final String tpvCode, final RetrofitListener interactorListener) {
        Call<PagoPse> pagoPseCall = service.postRegistroPse(importe, sesionId, tpvCode);

        pagoPseCall.enqueue(new Callback<PagoPse>() {

            @Override
            public void onResponse(Call<PagoPse> call, Response<PagoPse> response) {
                interactorListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<PagoPse> call, Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }

    public void searchPagoPse(final int idPagoPse, final RetrofitListener interactorListener) {
        Call<PagoPse> pagoPseStatusCall = service.getPagoPse(idPagoPse);

        pagoPseStatusCall.enqueue(new Callback<PagoPse>() {
            @Override
            public void onResponse(Call<PagoPse> call, Response<PagoPse> response) {
                interactorListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<PagoPse> call, Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }


}
