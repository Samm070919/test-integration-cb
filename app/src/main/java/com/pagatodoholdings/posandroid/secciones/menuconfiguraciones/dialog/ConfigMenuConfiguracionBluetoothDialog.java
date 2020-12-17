package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.dialog;

import androidx.fragment.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pagatodoholdings.posandroid.R;

/**
 * A simple subclass.
 */
public class ConfigMenuConfiguracionBluetoothDialog extends DialogFragment {

    public ConfigMenuConfiguracionBluetoothDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config_menu_configuracion_bluetooth_dialog, container, false);
    }
}
