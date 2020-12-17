package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;


import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.4f;
    private static final float MAX_SCALE = 1.0f;

    public void transformPage(final View view, final float position) {

        if (position < -1 || position > 1) {
            view.setScaleX(MIN_SCALE);
            view.setScaleY(MIN_SCALE);
        } else {
            final float signoPendiente = position < 0 ? -1 : 1;
            final float scaleFactor = MAX_SCALE + signoPendiente * position * (MIN_SCALE - MAX_SCALE);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        }
    }
}