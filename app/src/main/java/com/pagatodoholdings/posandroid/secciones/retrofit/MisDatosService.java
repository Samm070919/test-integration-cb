package com.pagatodoholdings.posandroid.secciones.retrofit;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MisDatosService {

    private static final String TAG = MisDatosService.class.getSimpleName();
    private ApiMposServicesInterface serviceWallet;
    private ApiMposServicesInterface serviceMpos;


    public MisDatosService() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofitWallet = new Retrofit.Builder()
                .baseUrl(BuildConfig.URL_REGISTRO)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        final Retrofit retrofitMpos = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();

        serviceWallet = retrofitWallet.create(ApiMposServicesInterface.class);
        serviceMpos = retrofitMpos.create(ApiMposServicesInterface.class);
    }

    public void getDatosPersonales(final RetrofitListener<DatosPersonales> interactorListener, String email, String token,String tpvcod) {
        final Call<DatosPersonales> call = serviceWallet.datosConsultar(email,token,tpvcod);
        call.enqueue(new Callback<DatosPersonales>() {
            @Override
            public void onResponse(final @NonNull Call<DatosPersonales> call, final @NonNull Response<DatosPersonales> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Datos Personales"));
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                }
            }

            @Override
            public void onFailure(final @NonNull Call<DatosPersonales> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getDatosNegocio(final RetrofitListener<DatosNegocio> interactorListener, String sesionid, String tpvcod)
    {
        final Call<DatosNegocio> call = serviceMpos.datosnegocioConsultar(sesionid,tpvcod);
        call.enqueue(new Callback<DatosNegocio>() {
            @Override
            public void onResponse(final @NonNull Call<DatosNegocio> call, final @NonNull Response<DatosNegocio> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Datos Negocio"));
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                }
            }
            @Override
            public void onFailure(final @NonNull Call<DatosNegocio> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getDatosCuentaBancaria(final RetrofitListener<DatosCuentaBancaria> interactorListener,String sesionid, String tpvcod)
    {
        final Call<DatosCuentaBancaria> call = serviceMpos.datoscuentabcaConsultar(sesionid,tpvcod);
        call.enqueue(new Callback<DatosCuentaBancaria>() {
            @Override
            public void onResponse(final @NonNull Call<DatosCuentaBancaria> call, final @NonNull Response<DatosCuentaBancaria> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Datos Cuenta Bancaria"));
                    Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                }
            }
            @Override
            public void onFailure(final @NonNull Call<DatosCuentaBancaria> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void verificarCuentaBancaria(final RetrofitListener interactorListener,String sesionid, String tpvcod,int impValCtaBancaria)
    {
        final Call<ResponseBody> call = serviceMpos.datosCuentabcaValidar(sesionid,tpvcod,impValCtaBancaria);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final @NonNull Call<ResponseBody> call, final @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    String error = "Error al Verificar la Cuenta Bancaria";
                    if(response.errorBody()!=null && response.errorBody().source()!=null)
                    {
                        error=response.errorBody().source().toString();
                        if(error.contains("Importe") && error.contains("no valido")) {
                            error="Importe de validación introducido no valido";
                            interactorListener.onFailure(new IllegalStateException(error));
                            Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                        }else{
                            interactorListener.onFailure(new IllegalStateException(error));
                            Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                        }
                    }else{
                        interactorListener.onFailure(new IllegalStateException(error));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    }//end if errorBody
                }//end if SuccessResponse
            }
            @Override
            public void onFailure(final @NonNull Call<ResponseBody> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }


}
