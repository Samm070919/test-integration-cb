package com.pagatodoholdings.posandroid.secciones.sales.processing;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.ProcessingFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;

public class ProcessingFragment extends PciSalesFragmentSupport<ProcessingFragmentBinding> {

    public static ProcessingFragment newInstance(){
        return new ProcessingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.processing_fragment,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.hideAllToolbar();
        new Handler().postDelayed(() -> activity.getRouter().showFirm(), 5000);
    }
}
