package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInCashBankListBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank;
import com.pagatodoholdings.posandroid.secciones.money_in.retrofit.MoneyInInteractor;
import com.pagatodoholdings.posandroid.secciones.money_in.viewmodel.MoneyInViewModel;
import com.pagatodoholdings.posandroid.utils.UtilsMoneyIn;

import java.util.List;

public class MoneyInCashBankListFragment extends AbstractMoney {
    HomeActivity homeActivity;
    private FragmentMoneyInCashBankListBinding binding;
    private List<Bank> bankList = null;
    private BankListAdapter adapter;
    private static final String PAIS = "PAIS";
    private String country;


    public static MoneyInCashBankListFragment getInstance (final String pais) {
        final MoneyInCashBankListFragment fragment = new MoneyInCashBankListFragment();
        final Bundle args = new Bundle();
        args.putSerializable(PAIS, pais);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            country = getArguments().getSerializable(PAIS).toString();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoneyInCashBankListBinding.inflate(inflater, container, false);

        initUI();

        return binding.rlToolbar.getRootView();
    }

    @SuppressLint("NewApi")
    private void initUI() {
        homeActivity = (HomeActivity) getActivity();

        homeActivity.muestraProgressDialog("Obteniendo Listado de Bancos");
        setFragmentsContainer(homeActivity.getBinding().flMainPantallaCompleta);

        MoneyInViewModel moneyInViewModel = new MoneyInViewModel(getActivity().getApplication());
        moneyInViewModel.getBankListLiveData().observe(this, bankListObserver);

        if (bankList != null) {
            setBankListAdapter();
            homeActivity.ocultaProgressDialog();

        } else {
            String baseUrl = UtilsMoneyIn.obtenerUrlPais(country);
            MoneyInInteractor iteractor = new MoneyInInteractor(baseUrl);

            iteractor.searchBankList(new RetrofitListener() {
                @Override
                public void showMessage(String message) {
                    //NOT IMPLEMENTED
                }

                @Override
                public void onFailure(Throwable throwable) {
                    homeActivity.ocultaProgressDialog();
                    ((HomeActivity)getActivity()).despliegaModal(true, false, getResources().getString(R.string.error), throwable.getMessage(), () -> { backPreviousFragment(); });
                }

                @Override
                public void onSuccess(Object result) {
                    homeActivity.ocultaProgressDialog();
                    if (result != null) {
                        moneyInViewModel.setBankList((List<Bank>) result);
                        setBankListAdapter();
                    } else {
                        ((HomeActivity)getActivity()).despliegaModal(true, false, getResources().getString(R.string.error), "Error al Intentar Obtener Listado de Bancos", () -> { backPreviousFragment(); });
                    }

                }
            });


        }


        binding.ivOnBack.setOnClickListener(view -> backPreviousFragment());

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });

    }

private void setBankListAdapter(){
    if (adapter==null && bankList !=null){
        adapter = new BankListAdapter(bankList, (vista, position) -> loadFragmentCodeBarMap(country, bankList.get(position)) );
    } else {
        binding.rvBankList.setHasFixedSize(true);
        binding.rvBankList.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.rvBankList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}

    private void loadFragmentCodeBarMap(final String pais, Bank bank) {
        final MoneyInCashBankDetailFragment fragment;
        fragment = MoneyInCashBankDetailFragment.getInstance( pais, bank);
        Bundle datosAEnviar = new Bundle();
        datosAEnviar.putString("PAIS", pais);
        datosAEnviar.putSerializable("BANCO", bank);
        loadMoneyInFragments(fragment);
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    final Observer<List<Bank>> bankListObserver = new Observer<List<Bank>>() {
        @Override
        public void onChanged(@Nullable final List<Bank> bankListCh) {
//            Capitalización de nombres e Bancos
//            for (Bank bank: bankListCh) {
//                bank.setDescripcion(WordUtils.capitalizeFully(bank.getDescripcion(), ' '));
//            }
            bankList = bankListCh;
            setBankListAdapter();
        }
    };
}
