package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.R;
import java.util.ArrayList;
import java.util.List;

public class IndicadorEstado extends ConstraintLayout {

    List<ImageView> states;
    private ImageView marker;
    private static  final int TIME_ANIMATION_DELTA = 200;
    private static  final int CIRCLES_DPS_HEIGHT = 9;

    public IndicadorEstado(final Context context) {
        super(context);
    }

    public IndicadorEstado(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        final TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.IndicadorEstado);
        init(attr);
    }

    private void init(final TypedArray attr) {
        inflate(getContext(), R.layout.slider_circular_states, this);
        final int numberOfStates = attr.getInt(R.styleable.IndicadorEstado_number_states, 1);
        initializeIndicators(numberOfStates);
        marker = findViewById(R.id.iv_marker);
        TextView tvSliderTitle;
        tvSliderTitle = findViewById(R.id.tv_state_title);
        tvSliderTitle.setText(attr.hasValue(R.styleable.IndicadorEstado_slider_title) ? attr.getString(R.styleable.IndicadorEstado_slider_title) : "");
    }

    public void initializeIndicators(final int numberOfStates) {
        states = new ArrayList<>();
        final LinearLayout stateLayout = findViewById(R.id.states_container_llyo);
        stateLayout.removeAllViews();
        for (int i = 1; i <= numberOfStates; i++) {
            final ImageView stateCircle = getImageView();
            stateCircle.setImageResource(R.drawable.circle_unselected);
            final LinearLayout.LayoutParams stateCirclelp = getLLParams();
            stateCirclelp.setMargins(0, 0, dpToPx(5), 0);
            stateCircle.setLayoutParams(stateCirclelp);
            stateLayout.addView(stateCircle);
            states.add(stateCircle);
        }
    }

    private LinearLayout.LayoutParams getLLParams() {
        return new LinearLayout.LayoutParams(dpToPx(CIRCLES_DPS_HEIGHT), dpToPx(CIRCLES_DPS_HEIGHT));
    }

    private ImageView getImageView() {
        return new ImageView(getContext());
    }

    public void changeIndicators(final int numberOfStates, final int contorno) {
        states = new ArrayList<>();
        final LinearLayout stateLayout = findViewById(R.id.states_container_llyo);
        stateLayout.removeAllViews();
        for (int i = 1; i <= numberOfStates; i++) {
            final ImageView stateCircle = getImageView();
            stateCircle.setImageResource(contorno);
            final LinearLayout.LayoutParams stateCirclelp = getLLParams();
            stateCirclelp.setMargins(0, 0, dpToPx(5), 0);
            stateCircle.setLayoutParams(stateCirclelp);
            stateLayout.addView(stateCircle);
            states.add(stateCircle);
        }
    }

    public void animateStateChange(final int statenumber) {
        final float distanciaRecorrer = getXAbsoluto(states.get(statenumber)) - getXAbsoluto(states.get(0));
        final ObjectAnimator animation = ObjectAnimator.ofFloat(marker, "translationX", distanciaRecorrer);
        animation.setDuration(TIME_ANIMATION_DELTA);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(final Animator animation) {
                //Sin usar pero es obligatoria

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                marker.setX(getXAbsoluto(states.get(statenumber)));
            }

            @Override
            public void onAnimationCancel(final Animator animation) {
                //Sin usar pero es obligatoria
            }

            @Override
            public void onAnimationRepeat(final Animator animation) {
                //Sin usar pero es obligatoria
            }
        });
        animation.start();

    }

    private int dpToPx(final int dps) {
        final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dps * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private float getXAbsoluto(final View view) {
        final int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[0];
    }


}
