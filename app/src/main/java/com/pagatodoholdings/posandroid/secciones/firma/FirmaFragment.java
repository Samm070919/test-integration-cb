package com.pagatodoholdings.posandroid.secciones.firma;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.databinding.FragmentFirmaBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.firma.firmatools.SigningView;
import com.pagatodoholdings.posandroid.utils.Logger;

import java.io.ByteArrayOutputStream;

public class FirmaFragment extends AbstractFragment {

    protected FragmentFirmaBinding binding;

    private SigningView signingView;
    private String dni;


    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }


    private void initUI(final LayoutInflater infalter, final ViewGroup container){
        binding = DataBindingUtil.inflate(infalter, R.layout.fragment_firma,container, false);
        Context context = getActivity();
        RelativeLayout rlPanelFirma;
        BotonClickUnico btnLimpiar;
        BotonClickUnico btnFinalizar;
        TextView tvFirmaAqui;
        rlPanelFirma = binding.firmaPanelFirma;
        btnLimpiar = binding.btnLimpiarFirma;
        btnFinalizar = binding.btnFinalizar;
        tvFirmaAqui = binding.tvFirmaAqui;
        btnLimpiar.setOnClickListener((final View view) -> signingView.clear());

        signingView = new SigningView(context, tvFirmaAqui);
        signingView.setDrawingCacheEnabled(true);
        signingView.buildDrawingCache();
        rlPanelFirma.addView(this.signingView);
        tvFirmaAqui.setText(getText(R.string.firma_aqui));
        btnFinalizar.setOnClickListener((final View vista)-> sendFirmaResult());
    }


    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(final Throwable throwable) {
        // None
    }

    private void sendFirmaResult() {
        final Bitmap bmp = signingView.getDrawingCache();

        new Handler().postDelayed(() -> {
            if (signingView.getSign().getNumberStrokes() > 0) {
                onFinishSignature(shrinkBitmap(bmp, 500), dni);
            } else {
                onFailedSignature();
            }
        }, 1000);
    }

    private Bitmap shrinkBitmap(final Bitmap myBitmap, final int maxRatio) {
        final int maxSize = maxRatio;
        int outWidth;
        int outHeight;
        final int inWidth = myBitmap.getWidth();
        final int inHeight = myBitmap.getHeight();
        if (inWidth > inHeight) {
            outWidth = maxSize;

            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        return Bitmap.createScaledBitmap(myBitmap, outWidth, outHeight, false);
    }

    private void onFinishSignature(final Bitmap bitmapResult, final String DNI) {

        final Intent signatureResult = new Intent();
        signatureResult.putExtra(TAG, "ActivityResult");
        signatureResult.putExtra("ByteImage", getByteArray(bitmapResult));
        signatureResult.putExtra("DNIString", DNI);
        activity.setResult(Activity.RESULT_OK, signatureResult);
        activity.finish();
    }

    public void onFailedSignature() {
        Logger.LOGGER.throwing(TAG, 1, new Throwable("Error al generar la contraseña"), "");
        final Intent signatureResult = new Intent();
        signatureResult.putExtra(TAG, "FailActivityResult");
        signatureResult.putExtra("ByteImage", new Parcelable[0]);
        signatureResult.putExtra("DNIString", "");
        activity.setResult(Activity.RESULT_CANCELED, signatureResult);
        activity.finish();
    }

    public byte[] getByteArray(final Bitmap bitmap) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }
}
