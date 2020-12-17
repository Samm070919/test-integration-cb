package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodoholdings.posandroid.R;

public class RegistroRegistraTarjetaProcessing extends DialogFragment {


    public RegistroRegistraTarjetaProcessing() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegistroRegistraTarjetaProcessing newInstance() {
        RegistroRegistraTarjetaProcessing fragment = new RegistroRegistraTarjetaProcessing();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registro_registra_tarjeta_processing, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
