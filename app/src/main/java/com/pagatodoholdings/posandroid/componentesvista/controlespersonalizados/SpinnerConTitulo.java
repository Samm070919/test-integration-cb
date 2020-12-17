package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;

public class SpinnerConTitulo extends LinearLayout {
    Spinner spinnerContenido;
    TextView tvTitulo;

    public SpinnerConTitulo(final Context context) {
        super(context);
    }

    public SpinnerConTitulo(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.spinner_con_titulo, this);
        init(context.obtainStyledAttributes(attrs, R.styleable.SpinnerConTitulo));
    }

    private void init(final TypedArray atributos) {
        spinnerContenido = findViewById(R.id.sp_contenido);
        tvTitulo = findViewById(R.id.tv_titulo_spinner);
        if (atributos.hasValue(R.styleable.SpinnerConTitulo_cabecera)) {
            tvTitulo.setText(atributos.getString(R.styleable.SpinnerConTitulo_cabecera));
        }
    }

    public Spinner obtenSpinnerContenido() {
        return spinnerContenido;
    }

    public TextView obtenTvTitulo() {
        return tvTitulo;
    }

    public GenericAdaptadorSpinner obtenAdaptadorNivlesPoliticos() {
        return (GenericAdaptadorSpinner) spinnerContenido.getAdapter();
    }
}
