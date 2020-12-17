package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInCashBankDetailBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank;
import com.pagatodoholdings.posandroid.secciones.money_in.models.BankDetailExtraField;
import com.pagatodoholdings.posandroid.secciones.money_in.models.CodeBar;
import com.pagatodoholdings.posandroid.secciones.money_in.retrofit.MoneyInInteractor;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsMoneyIn;

import java.util.Collections;
import java.util.List;

public class MoneyInCashBankDetailFragment extends AbstractMoney {

    private static final String PAIS = "PAIS";
    private static final String CONVENIO = "CONVENIO";
    private static final String CUENTA = "CUENTA";
    private static final String BANCO = "BANCO";
    private static final char FNC_1 = 0xF1;

    private String country;
    private Bank bank;
    private CodeBar codeBar;
    private FragmentMoneyInCashBankDetailBinding binding;
    private HomeActivity homeActivity;

    public static MoneyInCashBankDetailFragment getInstance(final String pais, final Bank bank) {
        final MoneyInCashBankDetailFragment fragment = new MoneyInCashBankDetailFragment();
        final Bundle args = new Bundle();

        args.putSerializable(PAIS, pais);
        args.putSerializable(BANCO, bank);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            country = getArguments().getSerializable(PAIS) != null ? getArguments().getSerializable(PAIS).toString() : "";
            bank = getArguments().getSerializable(BANCO) != null ? (Bank) getArguments().getSerializable(BANCO) : null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoneyInCashBankDetailBinding.inflate(inflater, container, false);

        initUI();

        return binding.rlToolbar.getRootView();
    }

    @SuppressLint("NewApi")
    private void initUI() {
        String codRecaudo = "";
        homeActivity = (HomeActivity) getActivity();

        setFragmentsContainer(homeActivity.getBinding().flMainPantallaCompleta);



        if (country.equals(Country.PERU.getItemIsoCode())){
            codRecaudo = bank.getCodigo();
            if (bank.getBancocod() == 154 /*Scotiabank Perú*/ ) {
                codRecaudo = "\n" + codRecaudo;
            }
        } else if (country.equals(Country.COLOMBIA.getItemIsoCode())) {

        }
        binding.tvTitleInstructions.setText(UtilsMoneyIn.getTituloInstruccionesDetalleBancoPais(country));
        binding.tvInstructions.setText(UtilsMoneyIn.getInstruccionesDetalleBancoPais(country, codRecaudo));
        setStyleInstructions();

        binding.tvLabel2.setText(UtilsMoneyIn.getTextoCodigoClientePais(country));
        binding.tvField2.setText(SigmaBdManager.getParametroFijo("0001", new BasicOnFailureListener(TAG, "Error al obtener el Clicode")));

        if ( UtilsMoneyIn.debeObtenerCodeBarPais(country) ) {
            binding.ivCodebarEan128.setVisibility(View.VISIBLE);
            binding.tvCodebar.setVisibility(View.VISIBLE);
            getCodeBar();
        } else {
            binding.ivCodebarEan128.setVisibility(View.GONE);
            binding.tvCodebar.setVisibility(View.GONE);
        }
        //getExtraFields();

        binding.ivOnBack.setOnClickListener(view -> backPreviousFragment());
        binding.ivCompartir.setOnClickListener(view -> {
            takeScreenshot();
        });

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });

        binding.btnVerMapa.setOnClickListener(view -> loadNearbyPlacesFragment(bank.getDescripcion()));

        binding.btnGuardarimagen.setOnClickListener(view -> takeScreenshot() );

        binding.tvTitleForm.setText(bank.getDescripcion());
    }

    private void setStyleInstructions() {
        SpannableString spannable;
        spannable = UtilsMoneyIn.getEstiloInstruccionesPais(country, binding.tvInstructions.getText().toString());
        binding.tvInstructions.setText(spannable);
    }

    private void getExtraFields(){
        List<BankDetailExtraField> extraFields = Collections.emptyList();
        TextView tvFieldLabel = new TextView(getContext(), null, 0, R.style.TextViewExtraFieldLabel);
        TextView tvFieldValue = new TextView(getContext(), null, 0, R.style.TextViewExtraFieldValue);
        LinearLayout layoutExtra = new LinearLayout(getContext(), null, 0, R.style.LayoutExtraFields);
        LinearLayout layoutSeparador = new LinearLayout(getContext(), null, 0, R.style.LayoutExtraFields);
        View separador = new View(getContext(), null, 0, R.style.ViewSeparador);

        tvFieldLabel.setLayoutParams(binding.tvLabel2.getLayoutParams());
        tvFieldValue.setLayoutParams(binding.tvField2.getLayoutParams());
        separador.setLayoutParams (binding.view2.getLayoutParams());

        extraFields = UtilsMoneyIn.getCamposExtraDetalleBancoPais(country);
        if ( !extraFields.isEmpty() ){
            for ( BankDetailExtraField ef: extraFields ) {
                if (ef.getName().equals("codigo") ) {
                    tvFieldLabel.setText(ef.getLabel());
                    tvFieldValue.setText(bank.getCodigo());

                    layoutExtra.setOrientation(LinearLayout.HORIZONTAL);
                    layoutExtra.addView(tvFieldLabel);
                    layoutExtra.addView(tvFieldValue);
                    binding.llExtraFieldsContainer.addView(layoutExtra);

                    layoutSeparador.setOrientation(LinearLayout.VERTICAL);
                    layoutSeparador.addView(separador);
                    binding.llExtraFieldsContainer.addView(layoutSeparador);
                }
            }
        }

    }

    private void getCodeBar(){
        homeActivity.muestraProgressDialog("Obteniendo Código de Barras");
        String baseUrl = UtilsMoneyIn.obtenerUrlPais(country);
        MoneyInInteractor iteractor = new MoneyInInteractor(baseUrl);

        String tpvCode = MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod();
        iteractor.searchCodeBar(tpvCode, new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onFailure(Throwable throwable) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onSuccess(Object result) {
                homeActivity.ocultaProgressDialog();
                if (result!= null){
                    Bitmap bCodeBar;
                    codeBar = (CodeBar) result;
                    String preformatedCode = FNC_1 + codeBar.getCodBarras() + FNC_1;
                    bCodeBar = UtilsMoneyIn.encodeAsBitmap(preformatedCode, BarcodeFormat.CODE_128 );

                    binding.ivCodebarEan128.setImageBitmap(bCodeBar);
                    binding.tvCodebar.setText(codeBar.getCodBarras());
                } else {
                    ((HomeActivity)getActivity()).despliegaModal(true, false, getResources().getString(R.string.error), "Error al Intentar Obtener Código de Barras", () -> { backPreviousFragment(); });
                }
            }
        });
    }

    private void takeScreenshot() {
        if (Utilities.takeScreenshot(getActivity().getWindow().getDecorView().getRootView())) {
            showDialog(R.layout.layout_money_in_screenshot, new ModalFragment.CommonDialogFragmentCallBack() {
                @Override
                public void onAccept() {
                    //none
                }
            }, getString(R.string.txt_close));
        }
    }

    private void loadNearbyPlacesFragment(final String bank) {
        final NearbyPlacesMapFragment fragment;
        homeActivity.muestraProgressDialog(getString(R.string.moneyin_loading_map));

        fragment = NearbyPlacesMapFragment.getInstance(bank);
        loadMoneyInFragments(fragment);
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

}
