package com.pagatodoholdings.posandroid.secciones.kit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityOnlinekitpayBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.utils.Logger;

import java.util.Collections;
import java.util.List;

import static com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarFragment.KITDATA_SEND;

public class OnlineKitPayActivity extends AbstractActivity {//NOSONAR

    private static final String TAG = OnlineKitPayActivity.class.getSimpleName();
    private ActivityOnlinekitpayBinding binding;
    private InfoCompraKit infoCompraKit;

    @Override
    protected void initUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_onlinekitpay);
        Intent extraIntent = getIntent();
        infoCompraKit = new Gson().fromJson(extraIntent.getStringExtra(KITDATA_SEND),InfoCompraKit.class);
        if(infoCompraKit != null) {
            cargarPagina(infoCompraKit.getUrl());
        }
        else {
            //restauraHome();
            finish();
         }

        binding.onBack.setOnClickListener(v -> {
                //restauraHome()
                finish();
            });
        }

    private void cargarPagina(final String url) {
        final ProgressDialog progressDialog = new ProgressDialog(binding.getRoot().getContext());
        progressDialog.setMessage(getString(R.string.cargando));
        progressDialog.setCancelable(false);
        progressDialog.show();

        binding.wvPayKit.setWebViewClient(new WebViewClient());
        binding.wvPayKit.getSettings().setBuiltInZoomControls(true);
        binding.wvPayKit.getSettings().setJavaScriptEnabled(true);

        binding.wvPayKit.loadUrl(url);
        binding.wvPayKit.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    binding.wvPayKit.setEnabled(true);
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
        //NOT IMPLEMENTED
    }
}
