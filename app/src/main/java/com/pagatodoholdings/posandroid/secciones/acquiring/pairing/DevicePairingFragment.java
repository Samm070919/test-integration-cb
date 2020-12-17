package com.pagatodoholdings.posandroid.secciones.acquiring.pairing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuConfiguracionSubVincularBinding;
import com.pagatodoholdings.posandroid.secciones.acquiring.support.TemplateFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

public class DevicePairingFragment extends TemplateFragment<FragmentConfigMenuConfiguracionSubVincularBinding> {

    public static DevicePairingFragment newInstance(){
        return new DevicePairingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_config_menu_configuracion_sub_vincular,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        homeActivity.setTitle("");
        homeActivity.setSubtitle("");
//        homeActivity.hideToolbar();
        homeActivity.hideBackButton();
    }

    @Override
    public void setListener(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }
}
