package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCoFResultBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCoF;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRecibePagosTarjetaFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosTarjetaCoFBean;
import com.pagatodoholdings.posandroid.utils.Constantes;

import static android.app.Activity.RESULT_OK;


public class CoFResultFragment extends Fragment {

    FragmentCoFResultBinding binding;
    Boolean isSuccess;
    private int idActivityResult = 0;
    private DatosTarjetaCoFBean datosTarjetaCoFBean;

    public CoFResultFragment(){ }

    public CoFResultFragment(Boolean isSucces, int idActivityResult, DatosTarjetaCoFBean datosTarjetaCoFBean){
        this.isSuccess = isSucces;
        this.idActivityResult = idActivityResult;
        this.datosTarjetaCoFBean = datosTarjetaCoFBean;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_co_f_result, container, false);
        inutUi();
        return binding.getRoot();
    }

    private void inutUi() {
        binding.btnContinuarRegistro.setOnClickListener(v -> {
            if(idActivityResult > 0){
                Bundle bundle = new Bundle();
                Intent output = new Intent();
                output.putExtra("DECODED", idActivityResult);
                bundle.putSerializable("DatosTarjeta", datosTarjetaCoFBean);
                output.putExtra("DatosTarjeta", bundle);
                getActivity().setResult(Constantes.REQUEST_ADD_CARD_BY_MENU, output);
                getActivity().finish();
            }else {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_cof, new RegistroRecibePagosTarjetaFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        binding.btnContinuarRegistroError.setOnClickListener(v -> {
            if(idActivityResult > 0){
                Intent output = new Intent();
                output.putExtra("DECODED", idActivityResult);
                getActivity().setResult(Constantes.REQUEST_ADD_CARD_BY_MENU, output);
                getActivity().finish();
            }else {
                Intent reintent = new Intent(getContext(), RegistroCoF.class);
                reintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(reintent);
                getActivity().finish();
            }
        });

        binding.tvNoTarjetaError.setOnClickListener(v -> {
            if(idActivityResult > 0){
                ((AbstractActivity) getActivity()).restauraHome();
            }
            else {
                //((AbstractActivity) getActivity()).goToLogin();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container_cof, new RegistroRecibePagosTarjetaFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.clError.setVisibility(isSuccess? View.GONE: View.VISIBLE);
        binding.clSuccess.setVisibility(isSuccess? View.VISIBLE: View.GONE);
    }

    public DatosTarjetaCoFBean getDatosTarjetaCoFBean() {
        return datosTarjetaCoFBean;
    }

    public void setDatosTarjetaCoFBean(DatosTarjetaCoFBean datosTarjetaCoFBean) {
        this.datosTarjetaCoFBean = datosTarjetaCoFBean;
    }
}
