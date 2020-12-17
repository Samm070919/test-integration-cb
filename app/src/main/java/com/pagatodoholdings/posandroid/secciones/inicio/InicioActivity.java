package com.pagatodoholdings.posandroid.secciones.inicio;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityInicioBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCliente;

import java.util.Collections;
import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.SUPER_APP_ACCOUNT;

public class InicioActivity extends AbstractActivity {//NOSONAR

    protected void initUi() {
        //----------UI-------------------------------------------------------
        ActivityInicioBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_inicio);
        binding.btnIniciarSesion.setOnClickListener(v -> {
            if (!preferenceManager.isExiste(SUPER_APP_ACCOUNT)) {
                preferenceManager.setValue(SUPER_APP_ACCOUNT, "1");
            }
            final Intent intent = new Intent(InicioActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        binding.btnCrearCuenta.setOnClickListener(v -> {
            preferenceManager.setValue(SUPER_APP_ACCOUNT, "0");
            final Intent intent = new Intent(InicioActivity.this, RegistroCliente.class);
            startActivity(intent);
        });
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
