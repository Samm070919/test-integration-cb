package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;


public class SelectorDosOpciones extends ConstraintLayout {
    private View fondoIzquierdo;
    private View fondoDerecho;
    private Drawable drawableSeleccionado;
    private Drawable drawableNoSeleccionado;
    private TextView tvIzquierdo;
    private TextView tvDerecho;

    public SelectorDosOpciones(final Context context) {
        super(context);
        init(null, 0);
    }

    public SelectorDosOpciones(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SelectorDosOpciones(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(final AttributeSet attrs, final int defStyle) {
        // Load attributes
        inflate(getContext(), R.layout.selector_dos_opciones, this);
        fondoIzquierdo = findViewById(R.id.v_seleccionizquierda);
        fondoDerecho = findViewById(R.id.v_seleccionderecha);

        final TypedArray atributos = getContext().obtainStyledAttributes(
                attrs, R.styleable.SelectorDosOpciones, defStyle, 0);

        final Drawable drawableIzquierdo = atributos.getDrawable(R.styleable.SelectorDosOpciones_iconoIzquierdo);
        if (drawableIzquierdo != null) {
            final ImageView ivIzquierdo = findViewById(R.id.iv_izquierdo);
            ivIzquierdo.setBackground(drawableIzquierdo);
        }

        final Drawable drawableDerecho = atributos.getDrawable(R.styleable.SelectorDosOpciones_iconoDerecho);
        if (drawableDerecho != null) {
            final ImageView ivDerecho = findViewById(R.id.iv_derecho);
            ivDerecho.setBackground(drawableIzquierdo);
        }

        final String textoIzquierdo = atributos.getString(R.styleable.SelectorDosOpciones_textoIzquierdo);
        tvIzquierdo = findViewById(R.id.tv_izquierdo);
        if (textoIzquierdo != null) {
            tvIzquierdo.setText(textoIzquierdo);
        }

        tvDerecho = findViewById(R.id.tv_derecho);
        final String textoDerecho = atributos.getString(R.styleable.SelectorDosOpciones_textoDerecho);
        if (textoDerecho != null) {

            tvDerecho.setText(textoDerecho);
        }


        drawableSeleccionado = atributos.getDrawable(R.styleable.SelectorDosOpciones_imagenSeleccionado);
        if (drawableSeleccionado != null) {
            fondoDerecho.setBackground(drawableSeleccionado);
            tvDerecho.setTextColor(Color.WHITE);
        }
        drawableNoSeleccionado = atributos.getDrawable(R.styleable.SelectorDosOpciones_imageDefault);
        if (drawableNoSeleccionado != null) {
            fondoIzquierdo.setBackground(drawableNoSeleccionado);
        }
        final BotonClickUnico btnIzquierdo = findViewById(R.id.btn_izquierdo);
        btnIzquierdo.setOnClickListener(view -> setSeleccionIzquierda());

        final BotonClickUnico btnDerecho = findViewById(R.id.btn_derecho);
        btnDerecho.setOnClickListener(view -> setSeleccionDerecha());


    }

    private void setSeleccionIzquierda() {
        fondoIzquierdo.setBackground(drawableSeleccionado);
        tvIzquierdo.setTextColor(Color.WHITE);
        fondoDerecho.setBackground(drawableNoSeleccionado);
        tvDerecho.setTextColor(Color.GRAY);
    }

    private void setSeleccionDerecha() {
        fondoDerecho.setBackground(drawableSeleccionado);
        tvDerecho.setTextColor(Color.WHITE);
        fondoIzquierdo.setBackground(drawableNoSeleccionado);
        tvIzquierdo.setTextColor(Color.GRAY);

    }

}
