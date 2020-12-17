package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInMenuBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsMoneyIn;

import java.math.BigDecimal;
import java.util.Map;

public class MoneyInMenuFragment extends AbstractMoney {
    private FragmentMoneyInMenuBinding binding;
    private static final String PAIS = "PAIS";
    private static final String IMPORTE_SELECCIONADO = "IMPORTE_SELECCIONADO";
    private Map<String, BigDecimal> saldos;
    private String[] importes;
    private String importeElegido;
    private String pais = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = FragmentMoneyInMenuBinding.inflate(inflater, container, false);

        initUI();
        reiniciarContador();
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        setFragmentsContainer(homeActivity.getBinding().flMainPantallaCompleta);

        if(!isPSEEnabled()){
            binding.llPays.setWeightSum(2f);
            binding.btnPagosEnLinea.setVisibility(View.GONE);
        }else{
            binding.llPays.setWeightSum(3f);
            binding.btnPagosEnLinea.setVisibility(View.VISIBLE);
        }

        importes = UtilsMoneyIn.getMontosPersonalizadosPais(pais);
        importeElegido ="0";

        BigDecimal bigDecimal = ((HomeActivity) getActivity()).getSaldo();
        binding.tvSaldoRellena.setNewTextSizeIndividualMonto(16f, 24f, 16f);
        binding.tvSaldoRellena.setMonto(Utilities.getFormatedImportObject(bigDecimal));

        binding.btnMonto1.setText(Utilities.getFormatedImport( new BigDecimal(importes[0])));
        binding.btnMonto2.setText(Utilities.getFormatedImport( new BigDecimal(importes[1])));
        binding.btnMonto3.setText(Utilities.getFormatedImport( new BigDecimal(importes[2])));
        binding.btnMonto4.setText(Utilities.getFormatedImport( new BigDecimal(importes[3])));

        binding.btnMonto1.setTextSize(UtilsMoneyIn.getImportesTextSizePais(pais));
        binding.btnMonto2.setTextSize(UtilsMoneyIn.getImportesTextSizePais(pais));
        binding.btnMonto3.setTextSize(UtilsMoneyIn.getImportesTextSizePais(pais));
        binding.btnMonto4.setTextSize(UtilsMoneyIn.getImportesTextSizePais(pais));


        binding.mvcOtraCantidad.getEditText().setText("0");

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });

        binding.btnPagosEnLinea.setOnClickListener(view -> loadActivityOnLinePay());

        binding.btnEfectivo.setOnClickListener(view -> loadFragmentBankList());

        binding.btnTarjeta.setOnClickListener(v -> loadFragmentLastRegisterCard());

        binding.ivOnBack.setOnClickListener(view -> close());

        binding.btnMonto1.setOnClickListener(view -> actualizarImporte(0));

        binding.btnMonto2.setOnClickListener(view -> actualizarImporte(1));

        binding.btnMonto3.setOnClickListener(view -> actualizarImporte(2));

        binding.btnMonto4.setOnClickListener(view -> actualizarImporte(3));

        binding.mvcOtraCantidad.getEditText().requestFocus();

    }


    private boolean isPSEEnabled(){
        return pais.equals(Country.COLOMBIA.getItemIsoCode());
    }

    private void actualizarImporte(final int indice) {
        importeElegido = importes[indice];
        switch (indice) {
            case 0:
                binding.btnMonto1.setBackground(getContext().getDrawable(R.drawable.selector_bg_white_buttons));
                binding.btnMonto2.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto3.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto4.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                break;
            case 1:
                binding.btnMonto1.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto2.setBackground(getContext().getDrawable(R.drawable.selector_bg_white_buttons));
                binding.btnMonto3.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto4.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                break;
            case 2:
                binding.btnMonto1.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto2.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto3.setBackground(getContext().getDrawable(R.drawable.selector_bg_white_buttons));
                binding.btnMonto4.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                break;
            case 3:
                binding.btnMonto1.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto2.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto3.setBackground(getContext().getDrawable(R.drawable.selector_bg_transp_buttons));
                binding.btnMonto4.setBackground(getContext().getDrawable(R.drawable.selector_bg_white_buttons));
                break;
            default:
                break;
        }
        binding.mvcOtraCantidad.getEditText().setText(importeElegido);
    }

    @Override
    public void onResume() {
        super.onResume();
        reiniciarContador();
    }

    private void loadActivityOnLinePay() {
        //Adjust PSE
        AdjustEvent adjustEvent = new AdjustEvent("v5i7bj");
        Adjust.trackEvent(adjustEvent);
        importeElegido =  binding.mvcOtraCantidad.getEditText().getText().toString();
        Float fImporte = Float.parseFloat(importeElegido);
        if ( fImporte <= 0f) {
            showInfoAlert();
        } else {
            showDialog(R.layout.layout_money_in_pse_dialog, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                @Override
                public void onCancel() {
                    //NO IMPLEMENTADO
                }

                @Override
                public void onAccept() {
                    Intent intent = new Intent(MoneyInMenuFragment.this.getActivity(), OnLinePayActivity.class);
                    intent.putExtra(IMPORTE_SELECCIONADO, String.valueOf(fImporte));
                    intent.putExtra(PAIS, MposApplication.getInstance().getDatosLogin().getPais().getCodigo());
                    startActivity(intent);
                    detenerContador();
                }
            });

        }
    }

    private void loadFragmentBankList() {

        final MoneyInCashBankListFragment fragment;
        fragment = MoneyInCashBankListFragment.getInstance(MposApplication.getInstance().getDatosLogin().getPais().getCodigo());
        loadMoneyInFragments(fragment);
    }

    private void loadFragmentLastRegisterCard() {
        final MoneyInByCardFragment fragment;
        fragment = new MoneyInByCardFragment(importeElegido);
        loadMoneyInFragments(fragment);
    }

    private void close() {
        hideViewElements((ViewGroup) binding.getRoot());
        closeFragment(MoneyInMenuFragment.this);
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    private void showInfoAlert() {
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setTitle("PSE")
                .setMessage("Selecciona una Cantidad")
                .setPositiveButton("Aceptar", (dialogInterface, i) -> binding.mvcOtraCantidad.getEditText().requestFocus())
                .show();

    }

    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final android.app.AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btn_aceptar_pse);
        btnAceptar.setText(getString(R.string.continuar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }


}
