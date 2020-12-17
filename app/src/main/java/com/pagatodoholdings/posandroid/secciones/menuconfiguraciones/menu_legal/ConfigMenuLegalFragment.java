package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_legal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.pdfviewer.PDFView;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuLegalBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigMenuLegalFragment extends AbstractConfigMenu {

    private FragmentConfigMenuLegalBinding binding;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        activity = (HomeActivity) getActivity();
        binding = FragmentConfigMenuLegalBinding.inflate(infalter, container, false);
        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });

        binding.btnTerminosYCondiciones.setOnClickListener(v -> showTermsAndCoditions());

        binding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuLegalFragment.this));

    }


    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }


    private void showTermsAndCoditions() {

        File file = new File(getContext().getCacheDir(), "terminos_condicionesv1.pdf");
        if (!file.exists()) {
            FileOutputStream output = null;
            try (InputStream asset = getActivity().getAssets().open("terminos_condicionesv1.pdf");){
                output = new FileOutputStream(file);
                final byte[] buffer = new byte[1024];
                int size;
                while ((size = asset.read(buffer)) != -1) {
                    output.write(buffer, 0, size);
                }
            } catch (IOException exe) {
                Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
            }finally {
                try {
                    if(output != null)
                        output.close();
                }catch (IOException e){
                    Logger.LOGGER.throwing(TAG, 1, e, e.getMessage());
                }
            }
        }


        PDFView.with(getActivity())
                .setfilepath(file.getAbsolutePath())
                .setSwipeOrientation(0)
                .start();
    }

}