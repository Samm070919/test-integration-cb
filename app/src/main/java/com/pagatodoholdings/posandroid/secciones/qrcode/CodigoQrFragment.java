package com.pagatodoholdings.posandroid.secciones.qrcode;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentCodigoQrBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class CodigoQrFragment extends AbstractConfigMenu {

    private static final int VINCULAR = 556;
    private FragmentCodigoQrBinding binding;

    public CodigoQrFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_codigo_qr, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iniUI();
    }

    private void iniUI() {
        activity = (HomeActivity) getActivity();
        binding.toolbar2.setNavigationIcon(R.drawable.ic_icono_back);
        getListener().setSupportActionBar(binding.toolbar2);
        getListener().getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar2.setNavigationOnClickListener(v -> getListener().regresaMenu());

        binding.icScreenshot.setOnClickListener(v -> {
            if (Utilities.takeScreenshot(getView())) {
                Logger.LOGGER.fine("CodigoQrFragment", "Error al generar el screenshot");
            } else {
                Logger.LOGGER.fine("CodigoQrFragment", "Error al generar el screenshot");
            }
        });

        binding.codeContainer.setImageBitmap(MposApplication.getInstance().getQrCode());


        binding.botonPagarQr.setOnClickListener(v -> {
            Intent i=new Intent(getListener(), CodeScannerActivity.class);
            i.putExtra("VINCULAR",556);
            startActivityForResult(i, VINCULAR);
        });
    }

}