package com.pagatodoholdings.posandroid.secciones.inicio;

import androidx.databinding.DataBindingUtil;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityBienvenidaProfesionistaBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;

import java.util.Collections;
import java.util.List;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DB_NAME;

public class BienvenidaProfesionistaActivity extends AbstractActivity {//NOSONAR

    protected void initUi() {
        //----------UI-------------------------------------------------------
        ActivityBienvenidaProfesionistaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_bienvenida_profesionista);
        binding.btnAceptarProfesionista.setOnClickListener(v -> {
            preferenceManager.deletePreference(DB_NAME);
            cambiaDeActividad(LoginActivity.class);
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
