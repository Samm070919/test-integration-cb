package com.pagatodoholdings.posandroid.secciones.registro.externo;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.widget.CheckBox;
import android.widget.TextView;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.secciones.retrofit.Merchant;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroMerchantInteractor;
import com.pagatodoholdings.posandroid.utils.Utilities;

import java.util.Collections;
import java.util.List;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIPO_USUARIO;
import static com.pagatodoholdings.posandroid.utils.TerminosYCondicionesManager.createSpannableForTermsAndCond;

public class RegistroMerchantActivity extends AbstractActivity {//NOSONAR

    protected void initUi() {
        setContentView(R.layout.activity_registro_merchant);
        BotonClickUnico button = findViewById(R.id.btn_merch_continuar);
        CheckBox checkBox = findViewById(R.id.check_condiciones);
        button.setOnClickListener(v -> {
            if (checkBox.isChecked()) {
                registerMerchant();
            } else {
                despliegaModal(true, false, "Terminos y Condiciones",
                        "Debes aceptar los Terminos y Condiciones para poder continuar", () -> {
                            //No implementation
                        });
            }
        });
        TextView otroMomento = findViewById(R.id.tv_otro_momento);
        otroMomento.setOnClickListener(v -> goToLogin());
        SpannableStringBuilder sBuilder = createSpannableForTermsAndCond(this);
        checkBox.setText(sBuilder);
        checkBox.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void registerMerchant() {
        muestraProgressDialog(getResources().getString(R.string.cargando));
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        if (datosLogin == null) {
            ocultaProgressDialog();
            goToLogin();
            return;
        }
        Integer clicod = datosLogin.getDatosTpv().getClicod();
        String email = datosLogin.getCliente().getEmail();
        RegistroMerchantInteractor registroMerchantInteractor = new RegistroMerchantInteractor();
        Merchant datosMerchant = new Merchant(clicod, email);
        registroMerchantInteractor.registroMerchant(datosMerchant, new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //No implementation
            }

            @Override
            public void onFailure(Throwable throwable) {
                ocultaProgressDialog();
                despliegaModal(true, false, "Error al Registrar Merchante",
                        throwable.getMessage(), () -> {

                        });
            }

            @Override
            public void onSuccess(Object result) {
                ocultaProgressDialog();
                PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
                preferenceManager.setValue(TIPO_USUARIO, Utilities.TipoUsuario.MERC.getTipo());
                merchantRegisterSuccessful();
            }
        }, MposApplication.getInstance().getDatosLogin().getToken());
    }

    private void merchantRegisterSuccessful() {
        despliegaModal(false, false, getResources().getString(R.string.registro_exitoso),
                "El registro Merchant se ha concluído con éxito", this::goToLogin);
    }

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
        //No implementation
    }
}
