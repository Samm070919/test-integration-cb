package com.pagatodoholdings.posandroid.secciones.money_out;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.util.DateUtil;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoViewController;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyOutTransactionBinding;
import com.pagatodoholdings.posandroid.secciones.money_in.AbstractMoney;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosCuentaBancaria;
import com.pagatodoholdings.posandroid.utils.Utilities;
import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoneyOutTransactionFragment extends AbstractMoney {

    private FragmentMoneyOutTransactionBinding binding;
    private DatosCuentaBancaria datosCuentaBancaria;
    private String importeRetirado;


    public void setDatosConfirmacion(DatosCuentaBancaria datosCuentaBancaria, String importeRetirado)
    {
        this.datosCuentaBancaria =datosCuentaBancaria;
        this.importeRetirado = importeRetirado;
    }

    public MoneyOutTransactionFragment() {
        // Required empty public constructor
    }



    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_money_out_transaction, container, false);
        cargarDatosTransaccion();

        binding.btnAceptar.setOnClickListener(v -> getListener().restauraHome());

        binding.ivCompartir.setOnClickListener(v -> {
            if(Utilities.takeScreenshot(getView())) {
                showDialog(R.layout.layout_money_in_screenshot, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                    @Override
                    public void onCancel() {
                        //NO IMPLEMENTADO
                    }

                    @Override
                    public void onAccept() {
                        //NO IMPLEMENTADO
                    }
                });
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Por el momento no fue posible tomar Captura de Pantalla",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosTransaccion()
    {
        TextView tvFooterMonto = binding.lImporte.findViewById(R.id.tv_subtitle);
        tvFooterMonto.setText(getResources().getString(R.string.money_out_trans_monto_retirado));
        tvFooterMonto.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

        MontoViewController mvMontoRetirado = binding.lImporte.findViewById(R.id.mv_importe);
        mvMontoRetirado.setNewTextSizeIndividualMonto(18f,25f,16f);
        BigDecimal importe = new BigDecimal(importeRetirado);
        mvMontoRetirado.setMonto(SigmaBdManager.formatoSaldo(importe, new BasicOnFailureListener(TAG,"Error de formato")));

        binding.tvDestinatarioResult.setText(datosCuentaBancaria.getNombre());
        binding.tvCtaCorrienteResult.setText(mascaraCuentaBancaria(datosCuentaBancaria.getCtabancaria()));
        binding.tvRefLocalResult.setText(datosCuentaBancaria.getDocid());
        binding.tvCtaCorrienteTitle.setText(datosCuentaBancaria.getTipoctadescripcion());
        binding.tvFechaResult.setText(DateUtil.getDateNow());

    }

    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btn_aceptar_screenshot);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private String mascaraCuentaBancaria(String cuentaBancaria)
    {
        String ctaMascara;
        ctaMascara = "*" + cuentaBancaria.substring(cuentaBancaria.length()-4);
        return ctaMascara;
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No definido
    }

}
