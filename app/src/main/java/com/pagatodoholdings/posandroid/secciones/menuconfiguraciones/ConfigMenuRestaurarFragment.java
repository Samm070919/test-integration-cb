package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuRestaurarBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;

@SuppressLint("ClickableViewAccessibility")
public class ConfigMenuRestaurarFragment extends Fragment {

    private FragmentConfigMenuRestaurarBinding binding;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {

        binding = FragmentConfigMenuRestaurarBinding.inflate(infalter, container, false);
        binding.btnRestaurarCancelar.setOnClickListener(view -> {
            reiniciarContador();
            ((DialogFragment) getParentFragment()).dismiss();

        });

        binding.btnRestaurarAccept.setOnClickListener(view -> {
            reiniciarContador();
            Toast.makeText(getActivity(), getString(R.string.success_message_restaurar_fabrica), Toast.LENGTH_LONG).show();
            getActivity().finishAffinity();
            final Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

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
}


