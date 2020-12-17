package com.pagatodoholdings.posandroid.secciones.qrcode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.ActivityCodeScannerBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.codescanner.CodeScanner;
import com.pagatodoholdings.posandroid.secciones.codescanner.CodeScannerView;
import com.pagatodoholdings.posandroid.secciones.retrofit.VincularQRService;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import org.apache.commons.lang3.StringUtils;
import java.util.Collections;
import java.util.List;
import okhttp3.ResponseBody;

public class CodeScannerActivity extends AbstractActivity {//NOSONAR

    private static final int RC_PERMISSION = 10;
    private static final String TAG = CodeScannerActivity.class.getSimpleName() ;
    private static final int RC_SCAN = 1444;
    private static final int SENT_COBRAR = 555;
    private CodeScanner mCodeScanner;
    private boolean mPermissionGranted;
    private String verificatioCode ;


    protected void initUi() {
        ActivityCodeScannerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_code_scanner);
        CodeScannerView scannerView = binding.scannerView;
        mCodeScanner = new CodeScanner(this, scannerView);
        setSupportActionBar(binding.toolbar3);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar3.setNavigationIcon(R.drawable.ic_icono_back_white);

        binding.toolbar3.setNavigationOnClickListener(v -> {
            mCodeScanner.releaseResources();
            finish();
        });

        binding.icTurnFlash.setOnClickListener(binding.scannerView.setTurnOnFlash());

        binding.botonPagarQr.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("COBRAR", "1");
            setResult(SENT_COBRAR, intent);
            finish();
        });

        if(getIntent()!=null && getIntent().hasExtra("VINCULAR"))
        {
            binding.botonPagarQr.setVisibility(View.GONE);
        }


        mCodeScanner.setDecodeCallback(result -> {
            if (!result.getText().isEmpty()) {
                if(getIntent()!=null &&getIntent().hasExtra("VINCULAR"))
                {
                    runOnUiThread(() -> {
                        mCodeScanner.stopPreview();
                        String qrVinculacion = result.getText();
                        vincularQRService(qrVinculacion);
                    });

                } else{
                    Intent intent = new Intent();
                    intent.putExtra("DECODED", result.getText());
                    setResult(RC_SCAN, intent);
                    finish();
                }
            }
        });

        mCodeScanner.setErrorCallback(error -> {

        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false;
                requestPermissions(new String[] {Manifest.permission.CAMERA}, RC_PERMISSION);
            } else {
                mPermissionGranted = true;
            }
        } else {
            mPermissionGranted = true;
        }
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


    public void showDialog(ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(R.layout.layout_vincular_qr, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final EditText tvCodVerification = view.findViewById(R.id.QRVerificationCode);
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btnConfirmacion);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            if(!StringUtils.isBlank(tvCodVerification.getText().toString()) ){
                verificatioCode = tvCodVerification.getText().toString();
                callBack.onAccept();
                alertDialog.dismiss();
            }
        });
        final BotonClickUnico btnCancelar = view.findViewById(R.id.btnCancel);
        btnCancelar.setText(getString(R.string.cancelar));
        btnCancelar.setTextSize(14);
        btnCancelar.setOnClickListener(view12 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }







    private void vincularQRService(final String qrData ) {

        showDialog(new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
            @Override
            public void onCancel() {
                finish();
            }

            @Override
            public void onAccept() {
                runOnUiThread(() -> vincularService(qrData,verificatioCode));
            }
        });

    }

    private void vincularService( final String qrData , final String verificatioCode ) {

                muestraProgressDialog(getString(R.string.kit_vincular_qr_procesando));

           final VincularQRService vincularQRService =  new VincularQRService();
                vincularQRService.vincularQR(qrData, verificatioCode, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(), new RetrofitListener<ResponseBody>() {
                            private void onAccept2() {
                                finish();
                            }

                            private void onAccept() {
                                finish();
                            }

                            @Override
                    public void showMessage(String s) {
                        //NONE
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        ocultaProgressDialog();
                        Logger.LOGGER.throwing(TAG,1,throwable,throwable.getMessage());
                        runOnUiThread(() -> despliegaModal(true, false, getString(R.string.kit_vincular_qr_error), Utilities.obtenerMensajeError(throwable), this::onAccept));

                    }

                    @Override
                    public void onSuccess(ResponseBody responseBody) {
                        ocultaProgressDialog();
                            Logger.LOGGER.fine(TAG,responseBody.toString());
                            runOnUiThread(() -> despliegaModal(false, true, getString(R.string.kit_vincular_qr_correcto), getString(R.string.kit_vincular_qr_correcto), this::onAccept2));

                        }
                });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true;
                mCodeScanner.startPreview();
            } else {
                mPermissionGranted = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPermissionGranted) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        mCodeScanner.releaseResources();
        finish();
    }
}
