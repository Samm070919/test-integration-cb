package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.pagatodoholdings.posandroid.R;

import net.fullcarga.android.api.formulario.Parametro;

import static net.fullcarga.android.api.formulario.Formato.Tipo.NUMERICO;
import static net.fullcarga.android.api.formulario.Formato.Tipo.PASSWORD;

public class EditTextDatosUsuarios extends AbstractEditTextPersonalizado {

    public EditTextDatosUsuarios(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initDatos(context, attrs);
    }

    @Override
    protected void setIdVista() {
        idVista = R.layout.campo_validado;
    }

    public void setValueListener(final Parametro parametro) {
        final int max = 100;
        final int min = 0;
        etCampo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence string, final int start, final int count, final int after) {
                if (string.toString().equals(" ")) {
                    parametro.setValue("");
                }
            }

            @Override
            public void onTextChanged(final CharSequence string, final int start, final int before, final int count) {
                if (string.toString().equals(" ")) {
                    parametro.setValue("");
                }
            }

            @Override
            public void afterTextChanged(final Editable string) {
                if (string.toString().length() < max && string.toString().length() > min) {
                    parametro.setValue(string.toString());
                }
            }
        });
    }

    public void setText(final String string) {
        etCampo.setText(string);
    }
    public String getText()  {
        return String.valueOf(etCampo.getText());
    }

    public void initDatos(final Context context, final AttributeSet attributeSet) {
        final TypedArray arrayattributes = context.obtainStyledAttributes(attributeSet, R.styleable.EditTextDatosUsuarios);

        final boolean isContrasena = arrayattributes.getBoolean(R.styleable.EditTextDatosUsuarios_iscontrasena, false);

        if (isContrasena) {
            final CheckBox imageOjo = findViewById(R.id.iv_icono_visible);
            imageOjo.setVisibility(VISIBLE);
            setDataType(PASSWORD);
            imageOjo.setOnCheckedChangeListener((CompoundButton compoundButton, boolean isChecked)-> {
                    if (isChecked) {
                        setDataType(NUMERICO);
                    } else {
                        setDataType(PASSWORD);
                    }
                    if (etCampo.length() > 0) {
                        etCampo.setSelection(etCampo.length());
                    }
            });
        }
    }
}
