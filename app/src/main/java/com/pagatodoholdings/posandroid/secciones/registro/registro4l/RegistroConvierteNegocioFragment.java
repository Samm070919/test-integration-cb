package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.secciones.retrofit.Merchant;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroMerchantInteractor;
import com.pagatodoholdings.posandroid.utils.Utilities;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIPO_USUARIO;
import static com.pagatodoholdings.posandroid.utils.TerminosYCondicionesManager.createSpannableForTermsAndCond;

public class RegistroConvierteNegocioFragment extends AbstractStepFragment {

    public RegistroConvierteNegocioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRegisterActions(getActivity());
        View view = inflater.inflate(R.layout.fragment_registro_convierte_negocio, container, false);
        bindListeners(view);
        return view;
    }

    private void bindListeners(View view) {
        BotonClickUnico boton = view.findViewById(R.id.botonUnicoContinuar);
        CheckBox cbCondiciones = view.findViewById(R.id.checkBoxCondiciones);

        boton.setOnClickListener(v -> {
            if (cbCondiciones.isChecked()) {
                registerMerchant();
            } else {
                if (MposApplication.getInstance().isBuildDebug()) {
                    advanceToNextStep();
                } else {
                    despliegaModalDeError("Terminos y Condiciones","Debes Aceptar los Terminos y Condiciones para Poder Continuar",
                            () -> {

                            });
                }
            }
        });
        TextView otroMomento = view.findViewById(R.id.textViewOtroMomento);
        otroMomento.setOnClickListener(v -> ((AbstractActivity) getActivity()).goToLogin());
        SpannableStringBuilder sBuilder = createSpannableForTermsAndCond(getActivity());
        cbCondiciones.setText(sBuilder);
        cbCondiciones.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void registerMerchant() {
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        if (datosLogin == null) {
            advanceToNextStepIfDebug();
            return;
        }
        Integer clicod = datosLogin.getDatosTpv().getClicod();
        String email = datosLogin.getCliente().getEmail();
        RegistroMerchantInteractor registroMerchantInteractor = new RegistroMerchantInteractor();
        Merchant datosMerchant = new Merchant(clicod, email);
        registroMerchantInteractor.registroMerchant(datosMerchant, new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onFailure(Throwable throwable) {
                despliegaModalDeError(
                        "Error al Registrar Merchant",
                        Utilities.obtenerMensajeError(throwable),
                        () -> {
                        }
                );
            }

            @Override
            public void onSuccess(Object result) {
                PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
                preferenceManager.setValue(TIPO_USUARIO, Utilities.TipoUsuario.MERC.getTipo());
                merchantRegisterSuccessful();
            }
        }, MposApplication.getInstance().getDatosLogin().getToken());
    }

    private void merchantRegisterSuccessful() {
        goToLogin();
    }

    private void goToLogin() {
        AbstractActivity activity = (AbstractActivity) getActivity();
        activity.goToLogin();
    }

    private void advanceToNextStepIfDebug() {
        if (MposApplication.getInstance().isBuildDebug()) {
            goToLogin();
        }
    }
}
