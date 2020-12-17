package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCommTermsBinding;

public class CommTermsFragment extends AbstractStepFragment {

    public CommTermsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());

        FragmentCommTermsBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comm_terms, container, false);
        binding.webView.loadUrl("http://www.greens.org/about/software/editor.txt");

        binding.btnAcceptCond.setOnClickListener(v -> {
            if (binding.checkBox2.isChecked()) {
                advanceToNextStep();
            }
        });

        return binding.getRoot();
    }


    public static Fragment newInstance() {
        CommTermsFragment fragment = new CommTermsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
