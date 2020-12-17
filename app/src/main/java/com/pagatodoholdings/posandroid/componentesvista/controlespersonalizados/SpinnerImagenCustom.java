package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.R;

public class SpinnerImagenCustom extends ConstraintLayout {
    private ImageView ivArrow;
    private TextView tvHint;
    private ConstraintLayout clSpinner;
    private Spinner spContenido;

    public SpinnerImagenCustom(final Context context) {
        super(context);
        inflate(context, R.layout.spinner_imagen_custom, this);
        initRef();
    }

    public SpinnerImagenCustom(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.spinner_imagen_custom, this);
        initRef();
        initView(attrs);
    }

    private void initRef() {
        tvHint = findViewById(R.id.tv_hint);
        clSpinner = findViewById(R.id.cl_spinner);
        ivArrow = findViewById(R.id.iv_arrow);
        spContenido =findViewById(R.id.sp_contenido);

        ivArrow.setOnClickListener(view -> spContenido.performClick());
        clSpinner.setOnClickListener(view -> spContenido.performClick());
    }

    private void initView(final AttributeSet attrs) {

        spContenido.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        // Load attributes
        final TypedArray atributo = getContext().obtainStyledAttributes(attrs, R.styleable.SpinnerImagenCustom);

        final String textoHint = atributo.getString(R.styleable.SpinnerImagenCustom_hint_x);
        if (textoHint != null) {
            tvHint.setText(textoHint);
        }
        final int hintColor=atributo.getColor(R.styleable.SpinnerImagenCustom_hintColor,0);
        tvHint.setTextColor(hintColor);

        final Drawable fondo = atributo.getDrawable(R.styleable.SpinnerImagenCustom_fondo);
        if (fondo != null) {
            clSpinner.setBackground(fondo);
        }
        final Drawable imageArrow= atributo.getDrawable(R.styleable.SpinnerImagenCustom_arrowImage);
        if(imageArrow!=null){
            ivArrow.setBackground(imageArrow);
        }


    }

    public Spinner getSpContenido() {
        return spContenido;
    }

    public void setTvHint(final String hint) {
        this.tvHint.setText(hint);
    }
}
