package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

public class TextViewPersonalizado extends LinearLayout {

    protected TextView tvCampo;
    protected int idVista = R.layout.text_view_personalizado;

    public TextViewPersonalizado(Context context) {
        super(context);
    }

    public void init(String texto) {
        inflate(getContext(), idVista, this);
        tvCampo = findViewById(R.id.tv_campo);
        tvCampo.setId(-1);
        tvCampo.setText(texto);
    }

    public TextView obtenTvCampo() {
        return tvCampo;
    }
}
