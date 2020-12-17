package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.util.AttributeSet;

import com.pagatodoholdings.posandroid.R;

public class TextViewNumerosDecimalesSuperioresGrande extends AbstractTextViewNumeroDecimalesSuperiores {

    public TextViewNumerosDecimalesSuperioresGrande(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setIdVista() {
        idVista = R.layout.textview_numeros_decimales_superiores_grande;
    }
}
