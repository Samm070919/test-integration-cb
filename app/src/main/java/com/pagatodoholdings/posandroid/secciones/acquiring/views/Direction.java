package com.pagatodoholdings.posandroid.secciones.acquiring.views;

import androidx.annotation.AnimRes;

import com.pagatodoholdings.posandroid.R;

public enum Direction {
    FORDWARD(R.anim.slide_from_right, R.anim.slide_to_left),
    BACK(R.anim.slide_from_left, R.anim.slide_to_right),
    UPWARD(R.anim.slide_from_bottom, R.anim.slide_from_top),
    DOWNWARD(R.anim.slide_from_top, R.anim.slide_from_bottom),
    NONE(0, 0);

    private int enterAnimation;
    private int exitAnimation;

    Direction(@AnimRes int enterAnimation, @AnimRes int exitAnimation) {
        this.enterAnimation = enterAnimation;
        this.exitAnimation = exitAnimation;
    }

    public int getEnterAnimation() {
        return enterAnimation;
    }

    public int getExitAnimation() {
        return exitAnimation;
    }
}
