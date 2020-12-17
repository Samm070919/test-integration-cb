package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pagatodoholdings.posandroid.R;

public class TextViewMoneda extends LinearLayout {

    public TextViewMoneda(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.text_view_moneda_monto, this, true);

        String moneda;
        String monto;
        String centavos;
        int mndSize, mntsize, cntSize;
        boolean isCleared = false;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextViewMoneda, 0, 0);
        int sizeMin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        int sizeMax = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics());
        try {
            moneda = a.getString(R.styleable.TextViewMoneda_txtMoneda);
            monto = a.getString(R.styleable.TextViewMoneda_txtMonto);
            centavos = a.getString(R.styleable.TextViewMoneda_txtCentavos);
            mndSize = a.getDimensionPixelSize(R.styleable.TextViewMoneda_mndSize, sizeMin);
            mntsize = a.getDimensionPixelSize(R.styleable.TextViewMoneda_mntSize, sizeMax);
            cntSize = a.getDimensionPixelSize(R.styleable.TextViewMoneda_cntSize, sizeMin);
            isCleared = a.getBoolean(R.styleable.TextViewMoneda_ivOver,false);
        } finally {
            a.recycle();
        }

        init("", monto, centavos, mndSize, mntsize, cntSize, isCleared);
    }

    // Setup views
    private void init(String moneda, String monto, String centavos, int mndSize, int mntSize, int cntSize, boolean isCleared) {
        TextView tvMoneda = (TextView) findViewById(R.id.tvMoneda);
        TextView tvMonto = (TextView) findViewById(R.id.tvMonto);
        TextView tvCentavos = (TextView) findViewById(R.id.tcCentavos);
        TextView tvParentesis = (TextView) findViewById(R.id.tvParentesisDerecho);
        View view = (View) findViewById(R.id.ivOver);

        tvMoneda.setText(moneda);
        tvMonto.setText(monto);
        tvCentavos.setText(centavos);
        tvMoneda.setTextSize(mndSize);
        tvMonto.setTextSize(mntSize);
        tvCentavos.setTextSize(cntSize);
        tvParentesis.setTextSize(mntSize);
        view.setVisibility(isCleared? VISIBLE: GONE);
    }

    public void setText(String monto, String cents, String moneda){
        TextView tvMoneda = (TextView) findViewById(R.id.tvMoneda);
        TextView tvMonto = (TextView) findViewById(R.id.tvMonto);
        TextView tvCentavos = (TextView) findViewById(R.id.tcCentavos);

        tvMoneda.setText(moneda);
        tvMonto.setText(monto);
        tvCentavos.setText(cents);
    }

    public void changecolor(int color) {
        TextView tvMoneda = (TextView) findViewById(R.id.tvMoneda);
        TextView tvMonto = (TextView) findViewById(R.id.tvMonto);
        TextView tvCentavos = (TextView) findViewById(R.id.tcCentavos);
        TextView tvParentesis = (TextView) findViewById(R.id.tvParentesisDerecho);
        tvMoneda.setTextColor(color);
        tvMonto.setTextColor(color);
        tvCentavos.setTextColor(color);
        tvParentesis.setTextColor(color);

    }

    public void showParentesis() {
        TextView tvParentesis = (TextView) findViewById(R.id.tvParentesisDerecho);
        tvParentesis.setVisibility(VISIBLE);

    }
}
