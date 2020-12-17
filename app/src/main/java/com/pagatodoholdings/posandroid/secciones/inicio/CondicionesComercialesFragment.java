package com.pagatodoholdings.posandroid.secciones.inicio;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCondicionesComercialesBinding;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.utils.Constantes;

public class CondicionesComercialesFragment extends AbstractStepFragment {
    private int idCallingActivity = 0;
    public CondicionesComercialesFragment (int idCallingActivity){
        this.idCallingActivity = idCallingActivity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        FragmentCondicionesComercialesBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_condiciones_comerciales, container, false);
            if(idCallingActivity > 0) {
                Intent output = new Intent();
                output.putExtra("DECODED", idCallingActivity);
                getActivity().setResult(Constantes.REQUEST_ADD_CARD_BY_MENU, output);
                getActivity().finish();
            }else
                advanceToNextStep();

        return mBinding.getRoot();
    }
}


