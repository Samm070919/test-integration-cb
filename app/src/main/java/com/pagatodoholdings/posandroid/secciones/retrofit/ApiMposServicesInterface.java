package com.pagatodoholdings.posandroid.secciones.retrofit;


import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosNegocio;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosUsuarioRegistroCliente;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.PaisConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroPaises;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.ImporteBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.ImporteKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.secciones.ticket.models.EmailTicket;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author dsoria
 */
public interface ApiMposServicesInterface {

    @GET("/wsmpos/niveles")
    Call<List<String>> listNiveles();

    @GET("/wsmpos/nivel0")
    Call<List<NivelBean>> listNivel0();

    @GET("/wsmpos/nivel1/{padre}")
    Call<List<NivelBean>> listNivel1(@Path("padre") int padre);

    @GET("/wsmpos/nivel2/{padre}")
    Call<List<NivelBean>> listNivel2(@Path("padre") int padre);

    @GET("/wsmpos/nivel3/{padre}")
    Call<List<NivelBean>> listNivel3(@Path("padre") int padre);

    @GET("/wsmpos/nivelesTipoNegocio")
    Call<List<String>> listNivelesTipoNegocio();

    @GET("/wsmpos/ctabancaria/bancos")
    Call<List<CuentaBancariaBean>> listBancos();

    @GET("/wsmpos/ctabancaria/tipocta")
    Call<List<TipoCuentaBancariaBean>> listTiposCta();

    @Headers("Content-Type: application/json")
    @POST("wsmpos/ctabancaria/alta")
    Call<Boolean> registraCuenta(
            @Body AltaCuentaBean bean,
            @Header("sesionid") String sesionId,
            @Header("tpvcod") String tpvcod
    );

    @GET("/wsmpos/tiposNegocio")
    Call<List<TipoNegocioBean>> listTipoNegocio();

    @GET("/wsmpos/tiposNegocio/{tipoNegocio}")
    Call<List<SubtipoNegocioBean>> listSubTipoNegocio(@Path("tipoNegocio") int tipoNegocio);

    @GET("/wsmpos/documento/shopper")
    Call<List<TipoDocBean>> listTiposDoc();

    @GET("/wsmpos/documento/profesionista")
    Call<List<TipoDocBean>> listTiposDocNegocio();

    @POST("/wsmpos/registro")
    Call<RegistroBean> registro(@Body RegistroBean registro);

    @GET("/wsescaneo/catalogo")
    Call<ResponseBody> downloadEscaneoDb(@Header("tpvcod") String tpvcod, @Header("sesionid") String sesionId);

    @GET("/wsmpos/updateapk")
    Call<ResponseBody> downloadApk(@Header("tpvcod") String tpvcod, @Header("sesionid") String sesionId);

    @Multipart
    @POST("/wsmpos/ticket")
    Call<Boolean> ticket(
            @Part MultipartBody.Part file,
            @Part("mail") RequestBody email,
            @Part("operacion") RequestBody operacion,
            @Part("pci") RequestBody pci,
            @Part("reflocal") RequestBody refLocal,
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod
    );


    @POST("/wsmpos/ticketHtml")
    Call<Boolean> eTicket(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod,
            @Body EmailTicket emailTicket
    );

    @POST("/wsmpos/desbloqueo/{serie}")
    Call<ResponseBody> recuperarPassword(@Path("serie") String serie);

    @Headers("Content-Type: application/json")
    @POST("/wswallet/login")
    Call<DatosLogin> login(
            @Query("email") String email,
            @Query("password") String password,
            @Query("serie") String serie
    );


    @Headers("Content-Type: application/json")
    @POST("/wswallet/password/renovar")
    Call<ResponseBody> renovarPass(@Query("email") String email);

    @Headers("Content-Type: application/json")
    @POST("/wswallet/password/cambiar")
    Call<ResponseBody> cambiarPass(
            @Query("email") String email,
            @Query("generatedPassword") String generated,
            @Query("newPassword") String newpass
    );


    @Headers("Content-Type: application/json")
    @POST("/wswallet/registro/merchant")
    Call<Merchant> registroMerchant(
            @Body Merchant merchant,
            @Header("token") String header
    );


    @Headers("Content-Type: application/json")
    @POST("/wswallet/registro/cliente")
    Call<DatosLogin> registroCliente(@Body DatosUsuarioRegistroCliente datosUsuario);

    @POST("/wswallet/registro/datosnegocio")
    Call<DatosLogin> registroDatosNegocio(
            @Body DatosNegocio datosUsuario,
            @Header("token") String header
    );

    @POST("/wswallet/registro/dongle")
    Call<DongleRegistro> registroDongle(
            @Body DongleRegistro dongleRegistro,
            @Header("token") String header
    );

    @POST("/wswallet/registro/desvinculardongle")
    Call<DongleRegistro> desvincularDongle(@Body DongleRegistro dongleRegistro);

    @POST("/wswallet/registro/tpv")
    Call<DatosLogin> registroTpv(@Body TpvRegistro tpvRegistro);

    @Headers("Content-Type: application/json")
    @GET("/wswallet/registro/paises")
    Call<ArrayList<RegistroPaises>> registroPaises();

    @Headers("Content-Type: application/json")
    @POST("/wswallet/datos/consultar")
    Call<DatosPersonales> datosConsultar(
            @Query("email") String email,
            @Query("token") String token,
            @Query("tpvcod") String tpvcod

    );

    @Headers("Content-Type: application/json")
    @POST("/wsmpos/asignarQr")
    Call<ResponseBody> vincularQr(
            @Query("plate ") String email,
            @Query("qr") String token,
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod);


    //Cambiar a header en lugar de query
    @Headers("Content-Type: application/json")
    @POST("/wsmpos/datosnegocio")
    Call<com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio> datosnegocioConsultar(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod
    );


    @Headers("Content-Type: application/json")
    @POST("/wsqr/wallet/pago")
    Call<DatosPagoQr> enviarPagoQr(
            @Body QrWalletPago walletPago,
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod);


    @Headers("Content-Type: application/json")
    @POST("/wsmpos/ctabancaria/datos")
    Call<DatosCuentaBancaria> datoscuentabcaConsultar(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod
    );

    @POST("/wsmpos/v2/desfogue")
    Call<DatosDesfogue> desfogue(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod,
            @Body MoneyOutImporteTransaction importe
    );

    @Headers("Content-Type: application/json")
    @POST("/wsmpos/ctabancaria/validar")
    Call<ResponseBody> datosCuentabcaValidar(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod,
            @Query("impValCtaBancaria") int impValCtaBancaria
    );


    @POST("/wsmpos/compraproductofisico/consultaimporte")
    Call<ImporteKit> consultarImporteKit(@Body ImporteBean importeBean);


    @POST("/wsmpos/compraproductofisico")
    Call<InfoCompraKit> compraKit(@Body DatosCompraKit importeBean,
                                  @Header("sesionid") String sesionid,
                                  @Header("tpvcod") String tpvcod);

    @POST("/wsmpos/altafirebase")
    Call<Boolean> alta_firebase(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod
    );


    @Headers("Content-Type: application/json")
    @POST("wsmpos/calendario/evento/alta")
    Call<Boolean> favoritoAlta(
            @Body FavoritoBean bean,
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId

    );

    @Headers("Content-Type: application/json")
    @POST("wsmpos/calendario/evento/baja/{alias}")
    Call<Boolean> favoritoBaja(
            @Path("alias") String alias,
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId

    );

    @Headers("Content-Type: application/json")
    @POST("wsmpos/calendario/evento/editar/{alias}")
    Call<Boolean> favoritoEditar(
            @Path("alias") String alias,
            @Body FavoritoBean bean,
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId

    );

    @Headers("Content-Type: application/json")
    @POST("wsmpos/calendario/evento/pagado/{alias}")
    Call<Boolean> favoritoPagado(
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId,
            @Path("alias") String alias,
            @Query("fecha") String fecha


    );

    @GET("/wsmpos/calendario/consulta/{fechaInicio}/{fechaFin}")
    Call<List<FavoritoBean>> listaFechasFavoritos(
            @Path("fechaInicio") String fechaInicio,
            @Path("fechaFin") String fechaFin,
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId);

    @GET("/wsmpos/calendario/consultafavoritos")
    Call<List<FavoritoBean>> listaFavoritos(
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId);

    @POST("/wsmpos/pago")
    Call<InfoPagoRegistrado> registraPago(
            @Header("sesionid") String sesionid,
            @Header("tpvcod") String tpvcod,
            @Query("importe") String importe,
            @Query("formapago") String formapago

    );

    @GET("/wsmpos/calendario/productosfavoritos")
    Call<ResponseBody> favoritosPrincipales(
            @Header("sesionid") String sesionId,
            @Header("tpvcod") String tpvcod);


    @Headers("Content-Type: application/json")
    @GET("wswallet/config/{pais}")
    Call<PaisConfig> paisConfig(@Path("pais") String countryCode);

    @GET("/wsmpos/cliente/consultaSaldos")
    Call<SaldosResponseBean> getSaldo(
            @Header("tpvcod") String tpvcod,
            @Header("sesionid") String sesionId);
}
