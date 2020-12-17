package com.pagatodoholdings.posandroid.secciones.registro;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.general.interfaces.BuildManager;
import com.pagatodoholdings.posandroid.secciones.retrofit.ApiMposServicesInterface;
import com.pagatodoholdings.posandroid.secciones.retrofit.CuentaBancariaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.NivelBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.SubtipoNegocioBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoCuentaBancariaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoNegocioBean;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import org.apache.commons.lang3.ObjectUtils;
import java.util.List;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.Subject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroInteractor {

    private static final String TAG = RegistroInteractor.class.getSimpleName();
    private static final String PIPE = "|";
    private ApiMposServicesInterface service;
    private String errorMessage = "Error al Obtener el Tipo de Negocio";

    public RegistroInteractor() {
        final Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(ApiMposServicesInterface.class);
    }

    public void cargarNivel0(final RetrofitListener<List<NivelBean>> interactorListener) {
        final Call<List<NivelBean>> call = service.listNivel0();
        call.enqueue(new Callback<List<NivelBean>>() {
            @Override
            public void onResponse(final @NonNull Call<List<NivelBean>> call, final @NonNull Response<List<NivelBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Nivel 0"));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<NivelBean>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getNivel1(final RetrofitListener<List<NivelBean>> interactorListener, final int idNivel1) {
        final Call<List<NivelBean>> call = service.listNivel1(idNivel1);
        call.enqueue(new Callback<List<NivelBean>>() {
            @Override
            public void onResponse(final @NonNull Call<List<NivelBean>> call, final @NonNull Response<List<NivelBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Nivel 1"));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<NivelBean>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getNivel2(final RetrofitListener<List<NivelBean>> interactorListener, final int idNivel2) {
        final Call<List<NivelBean>> call = service.listNivel2(idNivel2);
        call.enqueue(new Callback<List<NivelBean>>() {
            @Override
            public void onResponse(final @NonNull Call<List<NivelBean>> call, final @NonNull Response<List<NivelBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Nivel 2"));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<NivelBean>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getNivel3(final RetrofitListener<List<NivelBean>> interactorListener, final int idNivel3) {
        final Call<List<NivelBean>> call = service.listNivel3(idNivel3);
        call.enqueue(new Callback<List<NivelBean>>() {
            @Override
            public void onResponse(final @NonNull Call<List<NivelBean>> call, final @NonNull Response<List<NivelBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Nivel 3"));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<NivelBean>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getNiveles(final RetrofitListener<List<String>> interactorListener) {
        if (MposApplication.getInstance().isBuildDebug() && service == null) {
            interactorListener.onFailure(new Exception("Modo debug, el servicio no fue creado"));
            return;
        }

        final Call<List<String>> call = service.listNiveles();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(final @NonNull Call<List<String>> call, final @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException("Error al Obtener Niveles"));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<String>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getNivelesTipoNegocio(final RetrofitListener<List<String>> interactorListener) {
        final Call<List<String>> call = service.listNivelesTipoNegocio();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(final @NonNull Call<List<String>> call, final @NonNull Response<List<String>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException(errorMessage));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<String>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public void getTipoNegocio(final RetrofitListener<List<TipoNegocioBean>> interactorListener) {
        if (service == null && MposApplication.getInstance().isBuildDebug()) {
            interactorListener.onFailure(new Exception("Estás en modo debug y no hiciste registro"));
            return;
        }

        if(service != null) {
            final Call<List<TipoNegocioBean>> call = service.listTipoNegocio();
            call.enqueue(new Callback<List<TipoNegocioBean>>() {
                @Override
                public void onResponse(final @NonNull Call<List<TipoNegocioBean>> call, final @NonNull Response<List<TipoNegocioBean>> response) {
                    if (response.isSuccessful()) {
                        interactorListener.onSuccess(response.body());
                    } else {
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                        interactorListener.onFailure(new IllegalStateException("Error al Obtener el Tipo de Negocio"));
                    }
                }

                @Override
                public void onFailure(final @NonNull Call<List<TipoNegocioBean>> call, final @NonNull Throwable thr) {
                    Logger.LOGGER.throwing(TAG, 1, thr, thr.getMessage());
                    interactorListener.onFailure(thr);
                }
            });
        }
    }

    public void getSubTipoNegocio(final RetrofitListener<List<SubtipoNegocioBean>> interactorListener, final int tipoNegocio) {
        final Call<List<SubtipoNegocioBean>> call = service.listSubTipoNegocio(tipoNegocio);
        call.enqueue(new Callback<List<SubtipoNegocioBean>>() {
            @Override
            public void onResponse(final @NonNull Call<List<SubtipoNegocioBean>> call, final @NonNull Response<List<SubtipoNegocioBean>> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException(errorMessage));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<List<SubtipoNegocioBean>> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }

    public String getDeviceInfo() {
        final BuildManager buildManager = MposApplication.getInstance().getBuildManager();
        return new StringBuilder()
                .append(ObjectUtils.firstNonNull(buildManager.getManufacturer(), "")).append(PIPE)
                .append(ObjectUtils.firstNonNull(buildManager.getBrand(), "")).append(PIPE)
                .append(ObjectUtils.firstNonNull(buildManager.getProduct(), "")).append(PIPE)
                .append(ObjectUtils.firstNonNull(buildManager.getModel(), "")).append(PIPE)
                .append(ObjectUtils.firstNonNull(buildManager.getVersionRelease(), "")).append(PIPE)
                .append(ObjectUtils.firstNonNull(buildManager.getSerial(), "")).append(PIPE)
                .toString();
    }

    public void getBanksList(Consumer<? super List<CuentaBancariaBean>> onNext, Consumer<? super Throwable> onError) {
        Subject<List<CuentaBancariaBean>> subject = AsyncSubject.create();
        final Disposable subscription = subject.subscribe(
                onNext,
                onError);

        if (service == null) {
            subject.onError(new Exception("El servicio de retrofit no fue iniciado"));
            subject.onComplete();

            if (!subscription.isDisposed()) {
                subscription.dispose();
            }
            return;
        }

        final Call<List<CuentaBancariaBean>> call = service.listBancos();

        call.enqueue(new Callback<List<CuentaBancariaBean>>() {
            @Override
            public void onResponse(Call<List<CuentaBancariaBean>> call, Response<List<CuentaBancariaBean>> response) {
                if (response.isSuccessful()) {
                    subject.onNext(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    subject.onError(new Exception("Falló la operación"));
                }

                subject.onComplete();

                if (!subscription.isDisposed()) {
                    subscription.dispose();
                }
            }

            @Override
            public void onFailure(Call<List<CuentaBancariaBean>> call, Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
            }
        });
    }

    public void getTiposCtaList(Consumer<? super List<TipoCuentaBancariaBean>> onNext, Consumer<? super Throwable> onError) {
        Subject<List<TipoCuentaBancariaBean>> subject = AsyncSubject.create();
        final Disposable subscription = subject.subscribe(
                onNext,
                onError);

        if (service == null) {
            subject.onError(new Exception("El servicio de retrofit no fue iniciado"));
            subject.onComplete();

            if (!subscription.isDisposed()) {
                subscription.dispose();
            }
            return;
        }

        final Call<List<TipoCuentaBancariaBean>> call = service.listTiposCta();

        call.enqueue(new Callback<List<TipoCuentaBancariaBean>>() {
            @Override
            public void onResponse(Call<List<TipoCuentaBancariaBean>> call, Response<List<TipoCuentaBancariaBean>> response) {
                if (response.isSuccessful()) {
                    subject.onNext(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    subject.onError(new Exception("Falló la operación"));
                }

                subject.onComplete();

                if (!subscription.isDisposed()) {
                    subscription.dispose();
                }
            }

            @Override
            public void onFailure(Call<List<TipoCuentaBancariaBean>> call, Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
            }
        });
    }

    public void registro(final RegistroBean registroBean, final RetrofitListener<RegistroBean> interactorListener) {
        final Call<RegistroBean> call = service.registro(registroBean);
        call.enqueue(new Callback<RegistroBean>() {
            @Override
            public void onResponse(final @NonNull Call<RegistroBean> call, final @NonNull Response<RegistroBean> response) {
                if (response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    Logger.LOGGER.throwing(TAG,1, new Throwable(response.message()),response.message());
                    interactorListener.onFailure(new IllegalStateException("Respuesta Registro Incorrecta"));
                }
            }

            @Override
            public void onFailure(final @NonNull Call<RegistroBean> call, final @NonNull Throwable thr) {
                Logger.LOGGER.throwing(TAG,1,thr, thr.getMessage());
                interactorListener.onFailure(thr);
            }
        });
    }
}
