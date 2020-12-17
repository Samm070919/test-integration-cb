package com.pagatodoholdings.posandroid.secciones.promociones.view;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentMensajesDetalleBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.promociones.Mensaje;

/**
 * A simple {@link Fragment} subclass.
 */
public class MensajesDetalleFragment extends AbstractFragment {


    private FragmentMensajesDetalleBinding binding;
    private HomeActivity homeActivity;
    private Mensaje mensaje;

    public void setMensajeDetalle(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }


    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        homeActivity = (HomeActivity) getActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mensajes_detalle, container, false);

        binding.tvTitleMensaje.setText(mensaje.getTitulo());
        binding.tvMessageBody.setText(mensaje.getMensaje());

        binding.ivClose.setOnClickListener(v ->
                cerrar(MensajesDetalleFragment.this));
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No implementation
    }

    private void cerrar(Fragment fragment) {
        homeActivity.getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            homeActivity.cargarFragmentPromociones();
        }
    }

}
