package com.pagatodoholdings.posandroid.secciones.money_in;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityMoneyInOnlinePayWebviewBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.money_in.models.PagoPse;
import com.pagatodoholdings.posandroid.secciones.retrofit.InfoPagoRegistrado;
import com.pagatodoholdings.posandroid.secciones.retrofit.TransactionService;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.NetworkUtils;
import com.pagatodoholdings.posandroid.utils.UtilsMoneyIn;

import java.util.Collections;
import java.util.List;

public class OnLinePayActivity extends AbstractActivity {//NOSONAR
    protected static final String TAG = OnLinePayActivity.class.getSimpleName();
    private ActivityMoneyInOnlinePayWebviewBinding binding;
    private PagoPse pagoPse;
    private static final String PAIS = "PAIS";
    private static final String IMPORTE_SELECCIONADO =  "IMPORTE_SELECCIONADO";

    @Override
    protected void initUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_money_in_online_pay_webview);
        Intent intent = getIntent();

        String importe = intent.getExtras() != null ? intent.getExtras().getString(IMPORTE_SELECCIONADO, "") : "";
        String country = intent.getExtras() != null ? intent.getExtras().getString(PAIS, "") : "";

        String baseUrl = UtilsMoneyIn.obtenerUrlPais(country);

        if (NetworkUtils.isConnected(getApplicationContext())) {

            final TransactionService registraService = new TransactionService();
            registraService.postTransactionMoneyInBycard(ApiData.APIDATA.getDatosSesion().getIdSesion(),
                    ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod(),
                    importe,
                    Constantes.FORMA_PAGO_PSE_CAD
                    , new RetrofitListener<InfoPagoRegistrado>() {

                        @Override
                        public void onSuccess(InfoPagoRegistrado infoPagoRegistrado) {

                            if (NetworkUtils.isConnected(getApplicationContext())) {
                                cargarPagina(infoPagoRegistrado.getUrl());
                                // ALMACENAR EN PREFERENCIAS EL IDPAGOPSE
                                preferenceManager.setIdPagoPse(infoPagoRegistrado.getIdpagopse());
                                // preferenceManager.setCodigoClientePse(pagoPse.getClicod()!=null?pagoPse.getClicod():-1);
                            } else {
                                despliegaModal(true, false, getResources().getString(R.string.error), getResources().getString(R.string.no_internet), () -> {
                                });
                            }

                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            despliegaModal(true, false, getResources().getString(R.string.error),throwable.getMessage(), () -> { finish(); });
                            Logger.LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                        }

                        @Override
                        public void showMessage(String s) {
                            //None
                        }
                    });

        } else {
            despliegaModal(true, false, getResources().getString(R.string.error),getResources().getString(R.string.no_internet), () -> {
            });
        }


//        MoneyInInteractor iteractor = new MoneyInInteractor(baseUrl);
//        if (NetworkUtils.isConnected(getApplicationContext())) {
//            iteractor.postRegistroPse(Float.parseFloat(importe),
//                    ApiData.APIDATA.getDatosSesion().getIdSesion(),
//                    ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod(),
//                    new RetrofitListener() {
//                        @Override
//                        public void showMessage(String message) {
//                            //NOT IMPLEMENTED
//                        }
//
//                        @Override
//                        public void onFailure(Throwable throwable) {
//                            Logger.LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
//
//                        }
//
//                        @Override
//                        public void onSuccess(Object result) {
//                            pagoPse = (PagoPse) result;
//                            if (NetworkUtils.isConnected(getApplicationContext())) {
//                                cargarPagina(pagoPse.getUrl());
//                                // ALMACENAR EN PREFERENCIAS EL IDPAGOPSE
//                                preferenceManager.setIdPagoPse(pagoPse.getIdpagopse());
//                                preferenceManager.setCodigoClientePse(pagoPse.getClicod()!=null?pagoPse.getClicod():-1);
//                            } else {
//                                despliegaModal(true, false, getResources().getString(R.string.error),getResources().getString(R.string.no_internet), () -> {
//                                });
//                            }
//                        }
//                    });
//
//        } else {
//            despliegaModal(true, false, getResources().getString(R.string.error),getResources().getString(R.string.no_internet), () -> {
//            });
//        }

        binding.onBack.setOnClickListener(view -> finish());
    }

    private void cargarPagina(final String url) {
        final ProgressDialog progressDialog = new ProgressDialog(binding.getRoot().getContext());
        progressDialog.setMessage(getString(R.string.cargando));
        progressDialog.setCancelable(false);
        progressDialog.show();

        binding.wvViewTermsBody.setWebViewClient(new WebViewClient());
        binding.wvViewTermsBody.getSettings().setBuiltInZoomControls(true);
        binding.wvViewTermsBody.getSettings().setJavaScriptEnabled(true);

        binding.wvViewTermsBody.loadUrl(url);
        binding.wvViewTermsBody.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    binding.wvViewTermsBody.setEnabled(true);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Logger.LOGGER.info(TAG, error.getDescription().toString());
                }
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Logger.LOGGER.info(TAG, errorResponse.getReasonPhrase());
                }
            }

        });

    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        //NO IMPLEMENTADO
    }
}
