package com.pagatodoholdings.posandroid.secciones.qrcode;

import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodo.sigmalib.util.DateUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentQrConfimacionBinding;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.secciones.ticket.ETicket;

/**
 * A simple {@link Fragment} subclass.
 */
public class QrConfimacionFragment extends AbstractConfigMenu {

    private FragmentQrConfimacionBinding binding;
    private static final String KEY_PAYMENT_AMOUNT = "KEY_PAYMENT_AMOUNT";
    private static final String KEY_PAYMENT_REF_NME = "KEY_PAYMENT_REF_NME";
    private static final String KEY_PAYMENT_REF_NUM = "KEY_PAYMENT_REF_NUM";
    private static final String KEY_PAYMENT_REF_MCH = "KEY_PAYMENT_REF_MCH";

    public QrConfimacionFragment() {
        // Required empty public constructor
    }

    public static QrConfimacionFragment newInstance(String amount, String referenceNumber, String merchant, String refName) {
        QrConfimacionFragment f = new QrConfimacionFragment();
        Bundle args = new Bundle();
        args.putString(KEY_PAYMENT_AMOUNT, amount);
        args.putString(KEY_PAYMENT_REF_NUM, referenceNumber);
        args.putString(KEY_PAYMENT_REF_MCH, merchant);
        args.putString(KEY_PAYMENT_REF_NME, refName);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_qr_confimacion, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        if (getArguments() != null) {
            binding.payMonto.setNewTextSizeIndividualMonto(12,25,12);
            binding.payMonto.setMonto(getArguments().getString(KEY_PAYMENT_AMOUNT));
            binding.qrAgentNum.setText(getArguments().getString(KEY_PAYMENT_REF_MCH));
            binding.payRef.setText(getArguments().getString(KEY_PAYMENT_REF_NUM));
            binding.payDate.setText(DateUtil.getDateNow());
            binding.payDest.setText(getArguments().getString(KEY_PAYMENT_REF_NME));
        }

        binding.botonEnvTicket.setOnClickListener(v -> {
            if (!binding.etEmailEnvio.getText().equals("") && Patterns.EMAIL_ADDRESS.matcher(binding.etEmailEnvio.getText()).find()) {
                activity.muestraProgressDialog("Enviando Correo a " + binding.etEmailEnvio.getText());

                AsyntaskSendEmail sendEmailAsyn = new AsyntaskSendEmail();
                sendEmailAsyn.execute();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    //que hacer despues de 3 segundos
                    activity.ocultaProgressDialog();
                    enviarMailExitoso(true);
                }, 3000);

            } else if(!binding.etEmailEnvio.getText().isEmpty()){
                showInfoAlert("Correo no válido");
            }else {
                getListener().regresaMenu();
            }
        });
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    private void enviarMailExitoso(final boolean exitoso){
        final int idLayoutDialogo;
        if (exitoso) {
            idLayoutDialogo = R.layout.dialog_correo_enviado;
        } else {
            idLayoutDialogo = R.layout.dialog_correo_error;
        }

        activity.showDialogButtonAcept(idLayoutDialogo, "Aceptar", new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
            @Override
            public void onCancel() {
                //NOT IMPLEMENTED
            }

            @Override
            public void onAccept() {
                getListener().regresaMenu();
            }
        });
    }

    private void showInfoAlert(final String message) {
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setTitle("QR")
                .setMessage(message)
                .setPositiveButton("Aceptar", (dialogInterface, i) -> {

                })
                .show();
    }


    private class AsyntaskSendEmail extends AsyncTask<Void, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            ETicket digitalTicket = new ETicket(getArguments().getString(KEY_PAYMENT_REF_NME),
                    getArguments().getString(KEY_PAYMENT_REF_NUM),
                    getArguments().getString(KEY_PAYMENT_AMOUNT),
                    DateUtil.getDateNow(),
                    binding.etEmailEnvio.getText(),
                    new ETicket.EnvioMailInterfece() {

                        @Override
                        public void onSuccesMail() {
                            activity.ocultaProgressDialog();
                        }

                        @Override
                        public void onFailMail() {
                            activity.ocultaProgressDialog();
                        }
                    });

            digitalTicket.initQr();
            return true;
        }
    }
}
