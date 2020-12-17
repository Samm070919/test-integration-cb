package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.text.InputType;import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;

import net.fullcarga.android.api.formulario.Formato;
import net.fullcarga.android.api.formulario.Formulario;
import net.fullcarga.android.api.formulario.Parametro;

public class FormularioLayout extends RelativeLayout {

    private final Context context;
    private LinearLayout llContenidoFormulario;
    private Formulario formulario;

    public FormularioLayout(final Context context) {
        super(context);
        this.context = context;
    }

    public FormularioLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FormularioLayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void initUI() {
        llContenidoFormulario = findViewById(R.id.ll_contenido_formulario);
    }

    public void initData(final Formulario formulario) {
        this.formulario = formulario;
        if (formulario != null) {
            addParams();
        }
    }

    private void addParams() {

        for (final Parametro param : formulario.getParametros()) {

            if (param.getFormato().getTipo() == Formato.Tipo.ENUMERACION) {

                final SpinnerFormulario spinnerFormulario = getSpinnerFormulario(context, param);
                llContenidoFormulario.addView(spinnerFormulario);
            } else {

                if (param.getFormato().getTipo()!=Formato.Tipo.UID  && param.getFormato().getTipo() != Formato.Tipo.KEYSTORE && param.getFormato().getTipo()!=Formato.Tipo.CARDREAD
                        && param.getFormato().getTipo() != Formato.Tipo.CARDWRITE && param.getFormato().getTipo() != Formato.Tipo.CARDREMOVE) {
                    final EditTextFormulario view;
                    view = getEditTextFormulario(context, null);
                    if(param.getFormato().getTipo() == Formato.Tipo.TELEFONO_USUARIO) {
                        view.etCampo.setInputType(InputType.TYPE_CLASS_NUMBER);
                        view.setTextoFijo(validateInputPhone());
                    }
                    view.initData(param);
                    llContenidoFormulario.addView(view);
                }
            }
        }
    }

    private String validateInputPhone() {
        if (MposApplication.getInstance().getDatosLogin().getCliente().getTelefono().contains("+")) {
            return MposApplication.getInstance().getDatosLogin().getCliente().getTelefono().substring(3);
        } else {
            return MposApplication.getInstance().getDatosLogin().getCliente().getTelefono();
        }
    }

    private SpinnerFormulario getSpinnerFormulario(final Context context, final Parametro param) {
        return new SpinnerFormulario(context, param);
    }

    private EditTextFormulario getEditTextFormulario(final Context context, final AttributeSet attrs) {
        return new EditTextFormulario(context, attrs);
    }
}
