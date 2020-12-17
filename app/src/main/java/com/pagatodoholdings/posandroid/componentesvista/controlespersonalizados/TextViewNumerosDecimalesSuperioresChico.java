package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.util.AttributeSet;

import com.pagatodoholdings.posandroid.R;

public class TextViewNumerosDecimalesSuperioresChico extends AbstractTextViewNumeroDecimalesSuperiores {
    @Override
    protected void setIdVista() {
        idVista = R.layout.textview_numeros_decimales_superiores_chico;
    }

    public TextViewNumerosDecimalesSuperioresChico(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }
}
