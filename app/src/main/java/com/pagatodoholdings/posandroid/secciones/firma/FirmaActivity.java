package com.pagatodoholdings.posandroid.secciones.firma;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.ActivityFirmaBinding;
import com.pagatodoholdings.posandroid.secciones.firma.firmatools.SigningView;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import java.io.ByteArrayOutputStream;

public class FirmaActivity extends Activity { //NOSONAR

    protected static final String TAG = FirmaActivity.class.getSimpleName();
    public static final String CARD_HOLDER_NAME = "arg-card-holder-name";
    public static final String CLIENT_DNI_CODE = "DNIString";

    private SigningView signingView;
    private String dni;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initUi();
    }

    protected void initUi() {
        ActivityFirmaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_firma);

        signingView = new SigningView(this, binding.tvFirmaAqui);
        signingView.setDrawingCacheEnabled(true);
        signingView.buildDrawingCache();
        binding.firmaPanelFirma.addView(this.signingView);
        binding.tvFirmaAqui.setText(getText(R.string.firma_aqui));
        binding.tvFirmaAqui.setVisibility(View.VISIBLE);
        binding.btnFinalizar.setOnClickListener(vista -> sendFirmaResult());
        binding.btnLimpiarFirma.setOnClickListener(vista -> signingView.clear());

        dni = getIntent().getStringExtra(CLIENT_DNI_CODE);
        String name = !UtilsKt.isNullOrBlank(getIntent().getStringExtra(CARD_HOLDER_NAME))
                ? getIntent().getStringExtra(CARD_HOLDER_NAME)
                : dni;
        binding.tvNombreFirma.setText(name);
    }

    private void sendFirmaResult() {
        final Bitmap bmp = signingView.getDrawingCache();

        new Handler().postDelayed(() -> {
            if (signingView.getSign().getNumberStrokes() > 0) {
                onFinishSignature(shrinkBitmap(bmp, 500), dni);
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_envio_firma), Toast.LENGTH_SHORT).show();
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
        signatureResult.putExtra(CLIENT_DNI_CODE, DNI);
        this.setResult(Activity.RESULT_OK, signatureResult);
        this.finish();
    }

    public void onFailedSignature() {
        Logger.LOGGER.throwing(TAG, 1, new Throwable("Error al generar la contraseña"), "");
        final Intent signatureResult = new Intent();
        signatureResult.putExtra(TAG, "FailActivityResult");
        signatureResult.putExtra("ByteImage", new Parcelable[0]);
        signatureResult.putExtra(CLIENT_DNI_CODE, "");
        this.setResult(Activity.RESULT_CANCELED, signatureResult);
        this.finish();
    }

    @Override
    public void onBackPressed() {

    }

    public byte[] getByteArray(final Bitmap bitmap) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

}
