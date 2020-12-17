package com.pagatodoholdings.posandroid.secciones.retrofit;


import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosTPV;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKitCoF;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author dsoria
 */
public interface ApiWsPasarelasServicesInterface {

    @POST("cof/card/")
    Call<DatosTarjetaCoFBean> addCoF(
            @Body AltaTarjetaCoFBean bean,
            @Header("token") String token,
            @Header("tpvcod") String tpvcod,
            @Header("pais") String pais,
            @Header("usuario") String usuario
    );

    @GET("cof/cards/")
    Call<ArrayList<DatosTarjetaCoFBean>> getCards(
            @Header("token") String token,
            @Header("tpvcod") String tpvcod,
            @Header("pais") String pais,
            @Header("usuario") String usuario
    );

    @POST("cof/payment")
    Call<InfoCompraKitCoF> compraKitCoF(@Body DatosCompraKitCoF importeBean,
                                        @Header("pais") String pais,
                                        @Header("token") String token,
                                        @Header("tpvcod") String tpvcod,
                                        @Header("usuario") String usuario);


    @Headers("Content-Type: application/json")
    @POST("/wswallet/password/validar")
    Call<DatosTPV> validaContrasena (
            @Query("email") String email,
            @Query("password") String password,
            @Query("serie") String serie
    );

    @DELETE("/wsserviciopasarelas/cof/card/{idtarjeta}")
    Call<ResponseBody> deleteCoF(
            @Path("idtarjeta") Integer idtarjeta,
            @Header("token") String token,
            @Header("tpvcod") String tpvcod,
            @Header("pais") String pais,
            @Header("usuario") String usuario
    );
}
