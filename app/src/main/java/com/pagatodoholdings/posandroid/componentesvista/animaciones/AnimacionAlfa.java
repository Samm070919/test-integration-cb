package com.pagatodoholdings.posandroid.componentesvista.animaciones;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

public class AnimacionAlfa {
    public AnimacionAlfa(final View vista, final float alfaFinal, final long duracion) {
        final float alfaInicial = (alfaFinal - 1) * (-1);
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(vista, "Alpha", alfaInicial, alfaFinal);
        objectAnimator.setDuration(duracion);
        objectAnimator.start();
    }

    public AnimacionAlfa(final View view, final float alfaFinal, final long duracion, final Animator.AnimatorListener listener) {
        final float firstalpha = (alfaFinal - 1) * (-1);
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "Alpha", firstalpha, alfaFinal);
        objectAnimator.setDuration(duracion);
        objectAnimator.addListener(listener);
        objectAnimator.start();
    }
}
