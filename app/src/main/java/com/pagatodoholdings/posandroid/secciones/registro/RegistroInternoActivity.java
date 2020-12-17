package com.pagatodoholdings.posandroid.secciones.registro;

import androidx.databinding.DataBindingUtil;
import android.text.InputFilter;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityRegistroInternoBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Arrays;
import java.util.List;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.ESTADO_REGISTRO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;

public class RegistroInternoActivity extends AbstractActivity { //NOSONAR
    //----------UI-------------------------------------------------------
    private ActivityRegistroInternoBinding binding;

    //-----Inst ----------------------------------------------------------

    //----- Var ----------------------------------------------------------

    protected void initUi() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registro_interno);
        binding.btnRegistro.setOnClickListener(view -> {
            if (validaCampos()) {
                firebaseAnalytics.logEvent(Event.EVENT_COMPLETE_REGISTRO_INTERNO.key, null);
                registrar();
            }
        });

        binding.etdSeriePrefijo.obtenEtCampo().setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        binding.etdSeriePrefijo.obtenEtCampo().setText(tipoConfiguracion.getPrefijo());
        final String serie = getNumeroSerie();

        final InputFilter[] xmlFilters = binding.etdSerie.obtenEtCampo().getFilters();
        InputFilter[] completeFilters = ArrayUtils.add(xmlFilters, new InputFilter.LengthFilter(9));
        completeFilters = ArrayUtils.add(completeFilters, new InputFilter.AllCaps());
        completeFilters = ArrayUtils.add(completeFilters, (src, start, end, dst, dstart, dend) -> {
            if ("".equals(src)) { // for backspace
                return src;
            }
            if (src.toString().matches("[a-zA-Z 0-9]+")) {
                return src;
            }
            return "";
        });

        binding.etdSerie.obtenEtCampo().setFilters(completeFilters);
        binding.etdSerie.obtenEtCampo().setText(serie.substring(serie.length() - 9, serie.length()));
        binding.etdSerie.obtenEtCampo().requestFocus();
    }

    private void registrar() {
        preferenceManager.setValue(ESTADO_REGISTRO, Constantes.EstadosRegistro.REGISTRADO.name());
        preferenceManager.setValue(NUMERO_SERIE, binding.etdSeriePrefijo.obtenEtCampo().getText().toString() + binding.etdSerie.obtenEtCampo().getText().toString());
        cambiaDeActividad(LoginActivity.class);
    }

    //----------Override Methods-------------------------------------------------------

    @Override
    protected boolean validaCampos() {
        for (final EditTextDatosUsuarios campoValidar : registraCamposValidar()) {
            if (campoValidar.estaVacio()) {
                campoValidar.obtenEtCampo().setError(getString(R.string.campo_requerido));
                return false;
            }
            if (campoValidar.obtenEtCampo().length() != 9) {
                campoValidar.obtenEtCampo().setError(getString(R.string.longitud_incorrecta, "9"));
                return false;
            }
        }
        return true;
    }

    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Arrays.asList(
                binding.etdSerie);
    }

    @Override
    protected void realizaAlPresionarBack() {
        finish();
    }
}
