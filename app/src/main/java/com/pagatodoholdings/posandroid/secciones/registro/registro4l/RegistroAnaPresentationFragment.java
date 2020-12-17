package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.app.Fragment;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentAnaPresentationBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistroAnaPresentationFragment extends AbstractStepFragment {

    public RegistroAnaPresentationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRegisterActions(getActivity());
        FragmentAnaPresentationBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ana_presentation, container, false);

        mBinding.buttonContinue.setOnClickListener(v -> advanceToNextStep());

        return mBinding.getRoot();
    }
}
