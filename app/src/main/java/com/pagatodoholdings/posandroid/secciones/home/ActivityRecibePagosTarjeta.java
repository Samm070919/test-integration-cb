package com.pagatodoholdings.posandroid.secciones.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.ActivityRecibePagosTarjetaBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;

import static com.pagatodoholdings.posandroid.utils.Constantes.ACTIVITY_CODE_KEY;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_REGISTERPROFESIONISTA_BY_BUTTON;

public class ActivityRecibePagosTarjeta extends AbstractFragment {

   private ActivityRecibePagosTarjetaBinding binding ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater inflater, final ViewGroup container){
        binding =  DataBindingUtil.inflate(inflater, R.layout.activity_recibe_pagos_tarjeta,container, false);
        binding.textViewOtroMomento.setOnClickListener((View v)-> closeFragment(this));
        binding.botonUnicoSiQuiero.setOnClickListener((View v)->
        {
            Intent intent = new Intent(getActivity(), RegistroDatosNegocio.class);
            intent.putExtra(ACTIVITY_CODE_KEY, REQUEST_REGISTERPROFESIONISTA_BY_BUTTON);
            getActivity().startActivityForResult(intent, 5);
            //((HomeActivity)getActivity()).cambiaDeActividadSinCerrar(RegistroDatosNegocio.class)
        });
    }


    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }

    protected void closeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).regresaMenu();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_REGISTERPROFESIONISTA_BY_BUTTON) {
            ((HomeActivity) getActivity()).restauraHome();
        }
    }

}
