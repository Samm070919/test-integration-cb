package com.pagatodoholdings.posandroid.secciones.sales.successfully;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.SuccessfulSaleFragmentBinding;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;

public class SuccessfulSaleFragment extends PciSalesFragmentSupport<SuccessfulSaleFragmentBinding> {

    public static SuccessfulSaleFragment newInstance(){
        return new SuccessfulSaleFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.successful_sale_fragment,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.setTitle("");
        activity.hideBackButton();
    }
}
