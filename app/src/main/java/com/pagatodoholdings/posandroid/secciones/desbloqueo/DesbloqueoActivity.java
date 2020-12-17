package com.pagatodoholdings.posandroid.secciones.desbloqueo;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityClaveDesbloqueoBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuCambioContrasenaFragment;

import java.util.Collections;
import java.util.List;

public class DesbloqueoActivity extends AbstractActivity {//NOPMD //NOSONAR Nivel de herencia mayor de 5
    //----------UI-------------------------------------------------------
    private ActivityClaveDesbloqueoBinding binding;
    private String correoElectronico;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    protected void initUi() {
        // Inicializa variables yAxis vistas
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clave_desbloqueo);
        existeCorreo();

        binding.ivRegresar.setOnClickListener(view -> finish());

        binding.btnSendEmail.setOnClickListener(v -> {
            correoElectronico = binding.edCorreoElectronico.getText().trim();

            if (correoElectronico.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.sin_correo), Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(correoElectronico).matches()) {
                Toast.makeText(getApplicationContext(), getString(R.string.email_invalido), Toast.LENGTH_SHORT).show();
            } else {
                cargarFragmentCambioContrasena(correoElectronico);
            }
        });
    }

    private void existeCorreo() {
        correoElectronico = MposApplication.getInstance().getPreferenceManager().getValue(Constantes.Preferencia.EMAIL, "");
        binding.edCorreoElectronico.setVisibility(View.VISIBLE);
        binding.algunCorreo.setVisibility(View.GONE);

        if (correoElectronico.length() > 0) {
            binding.edCorreoElectronico.setText(correoElectronico);
        } else {
            binding.algunCorreo.setText(getString(R.string.sin_correo));
        }
    }

    private void cargarFragmentCambioContrasena(String email) {
        binding.btnSendEmail.setVisibility(View.GONE);
        binding.lnCabecera.setVisibility(View.GONE);
        binding.tituloRecuperar.setVisibility(View.GONE);
        binding.textRecuperar.setVisibility(View.GONE);
        binding.algunCorreo.setVisibility(View.GONE);
        final ConfigMenuCambioContrasenaFragment fragmentCambioContrasena = ConfigMenuCambioContrasenaFragment.newInstance(email);
        fragmentCambioContrasena.setListenerActivity(this);
        getSupportFragmentManager().beginTransaction().replace(binding.containerDesbloqueo.getId(), fragmentCambioContrasena).commit();
    }

    //----------Override Methods-------------------------------------------------------
    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        finish();
    }
}



