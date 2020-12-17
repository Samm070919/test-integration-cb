package com.pagatodoholdings.posandroid.secciones.acquiring.views;

import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;

public class MoneySpan extends RelativeSizeSpan {

    private float ratio;
    private boolean isTop;

    public MoneySpan(float proportion, boolean isTop) {
        super(proportion);
        ratio = proportion;
        this.isTop = isTop;

    }

    @Override
    public void updateDrawState(@NonNull TextPaint paint) {
        paint.setTextSize(ratio * 0.5f);
        if (isTop) {
            paint.baselineShift += paint.ascent();
        } else {
            paint.baselineShift += ((paint.descent() + paint.ascent()) / 2);
        }
    }

    @Override
    public void updateMeasureState(@NonNull TextPaint ds) {
        updateDrawState(ds);
    }
}
