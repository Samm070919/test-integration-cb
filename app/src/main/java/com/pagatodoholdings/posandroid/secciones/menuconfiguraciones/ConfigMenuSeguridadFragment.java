package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.pagatodoholdings.posandroid.databinding.FragmentAjustesSeguridadBinding;

public class ConfigMenuSeguridadFragment extends AbstractConfigMenu {

    private FragmentAjustesSeguridadBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initUI(final LayoutInflater infalter, final ViewGroup container) {

        binding = FragmentAjustesSeguridadBinding.inflate(infalter, container, false);

        binding.ivClose.setOnClickListener(view -> loadMiCuenta(ConfigMenuSeguridadFragment.this));

        binding.btnCambiarContraseA.setOnClickListener(v -> cargarFragmentCambioContrasena());



        binding.cbSaldos.setOnCheckedChangeListener((compoundButton, b) -> {
            //No definition
        });
    }

    private void cargarFragmentCambioContrasena() {
        final ConfigMenuCambioContrasenaFragment fragmentCambioContrasena= new ConfigMenuCambioContrasenaFragment();
        fragmentCambioContrasena.setListener(getListener());

        getListener().getManejadorFragments().cargarFragment(fragmentCambioContrasena, getListener().getBinding().flMainPantallaCompleta.getId());
        getListener().getBinding().toolbar.setVisibility(View.GONE);
        getListener().getBinding().navBar.setVisibility((View.GONE));
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No definition
    }
}
