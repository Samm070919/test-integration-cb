package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuSalirBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

public class ConfigMenuSalirFragment extends AbstractFragment {

    private FragmentConfigMenuSalirBinding binding;
    private HomeActivity listener;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {

        binding = FragmentConfigMenuSalirBinding.inflate(infalter, container, false);
        binding.btnExitDialogFragmentCancel.setOnClickListener(view -> {
            binding.fragmentSalir.setVisibility(View.GONE);
            getActivity().getSupportFragmentManager().beginTransaction().remove(ConfigMenuSalirFragment.this).commit();
            if (getParentFragment() != null) {
                ((DialogFragment) getParentFragment()).dismiss();
            } else {
                listener.regresaMenu();
            }
        });

        binding.btnExitDialogFragmentAccept.setOnClickListener(view -> cerrarSessionApp());

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    private void cerrarSessionApp() {
        ((AbstractActivity) getActivity()).finishApp();
    }

    public ConfigMenuSalirFragment setListener(final HomeActivity listener) {
        this.listener = listener;

        return this;
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //NOT IMPLEMENTED
    }
}
