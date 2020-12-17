package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import java.math.BigDecimal;

public abstract class AbstractTextViewNumeroDecimalesSuperiores extends ConstraintLayout {

    private final TextView entero;
    private final TextView decimal;
    protected int idVista = R.layout.textview_numeros_decimales_superiores_grande;
    private static final int NUMERO_CARACTERES_SIN_DECIMAL = 2;
    private static final int NUMERO_CARACTERES_UN_DECIMAL = 3;

    protected abstract void setIdVista();

    public AbstractTextViewNumeroDecimalesSuperiores(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setIdVista();
        inflate(getContext(), idVista, this);
        entero = findViewById(R.id.tv_total_entero);
        decimal = findViewById(R.id.tv_total_decimales);
    }

    public void setNumero(final BigDecimal cantidad) {
        entero.setText(getParteEntera(cantidad));
        decimal.setText(getParteDecimal(cantidad));
    }

    private String getParteEntera(final BigDecimal cantidad) {
        return cantidad.toBigInteger().toString();
    }

    private String getParteDecimal(final BigDecimal cantidad) {
        final String decimales = cantidad.remainder(BigDecimal.ONE).toString();
        if (decimales.length() <= NUMERO_CARACTERES_SIN_DECIMAL) {
            return ".00";
        } else if (decimales.length() == NUMERO_CARACTERES_UN_DECIMAL) {
            return decimales.substring(1) + "0";
        } else {
            return decimales.substring(1);
        }
    }
}
