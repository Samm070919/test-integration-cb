package com.pagatodoholdings.posandroid.secciones.money_in.retrofit;

import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank;
import com.pagatodoholdings.posandroid.secciones.money_in.models.CodeBar;
import com.pagatodoholdings.posandroid.secciones.money_in.models.PagoPse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoneyInApiInterface {

    @GET("depositos/bancos")
    Call<List<Bank>> getBankList();

    @Headers({"content-type: application/json;charset=UTF-8"})
    @GET("depositos/codigobarras/{tpvcod}")
    Call<CodeBar> getClientCode(@Path("tpvcod") String tpvcod);

    //============
    //COLOMBIA PSE
    //============
    @POST("pagopse")
    Call<PagoPse> postRegistroPse (
            @Query("importe")   float importe,
            @Header("sesionid") String sesionId,
            @Header("tpvcod")   String tpvCode
    );

    @GET("pagopse/{idpagopse}")
    Call<PagoPse> getPagoPse (@Path("idpagopse") Integer idPagoPse );
    //============
}
