package com.pagatodoholdings.posandroid.componentesvista.animaciones;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;

public class AnimacionBlink {
    ObjectAnimator animator;
    static final int TIEMPO_DEFAULT = 1500;
    boolean isAnimating;

    public AnimacionBlink(final View view, final int inicioColor, final int medioColor, final int finalColor) {
        animator = ObjectAnimator.ofInt(view, "backgroundColor", inicioColor, medioColor, finalColor);
        animator.setDuration(TIEMPO_DEFAULT);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(Animation.INFINITE);
    }

    public void inicia() {
        animator.start();
        isAnimating = true;
    }

    public void deten() {
        animator.cancel();
        isAnimating = false;
    }
}
