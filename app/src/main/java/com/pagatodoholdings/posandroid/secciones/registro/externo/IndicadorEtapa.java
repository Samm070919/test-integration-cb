package com.pagatodoholdings.posandroid.secciones.registro.externo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class IndicadorEtapa extends ConstraintLayout {

    private int steps = 2;
    private Drawable dDone;
    private boolean hideSteps;

    public IndicadorEtapa(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        inflate(context, R.layout.indicador_etapa, this);
        final View view = findViewById(R.id.line);
        final LinearLayout linearLayout = findViewById(R.id.stepsContainer);
        linearLayout.removeAllViews();

        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.IndicadorEtapa);
        view.setBackgroundColor(attributes.getColor(R.styleable.IndicadorEtapa_lineColor, Color.BLACK));
        steps = attributes.getInteger(R.styleable.IndicadorEtapa_steps, 1);
        dDone=attributes.getDrawable(R.styleable.IndicadorEtapa_srcDone);
        hideSteps = attributes.getBoolean(R.styleable.IndicadorEtapa_showSteps, true);
        dDone= ContextCompat.getDrawable(getContext(), R.drawable.circle_off);

        if (!attributes.getBoolean(R.styleable.IndicadorEtapa_showLine, true)) {
            view.setVisibility(GONE);
        }
        attributes.recycle();

        buildRangeBar(0);
    }

    public void setSteps(int steps) {
        this.steps = steps;
        buildRangeBar(0);
    }

    public final void buildRangeBar(final int pos){
        final LinearLayout linearLayout = findViewById(R.id.stepsContainer);
        linearLayout.removeAllViews();
        for (int i = 0; i < steps; i++){

            final LinearLayout.LayoutParams params = getParams(i);

            final RelativeLayout layout = getRelativeLayout();
            layout.setLayoutParams(params);
            final int sizeDone = 12;
            if (i < pos){
                final ImageView imageView = getImageView();
                imageView.setLayoutParams(getCLParams(sizeDone, sizeDone));
                imageView.setImageDrawable(dDone);
                layout.addView(imageView);

                final RelativeLayout.LayoutParams params1 = getRLParams();
                params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                final TextView textView = getTextView();
                textView.setLayoutParams(params1);
                if (hideSteps) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sizeDone); //10
                    textView.setText(String.valueOf(i + 1));
                    textView.setTextColor(Color.GRAY);
                }
                layout.addView(textView);
            } else if (i == pos){
                final ImageView imageView = getImageView();
                imageView.setLayoutParams(getCLParams(sizeDone, sizeDone));
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circle_on));
                layout.addView(imageView);

                final RelativeLayout.LayoutParams params1 = getRLParams();
                params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                final TextView textView = getTextView();
                textView.setLayoutParams(params1);
                if (hideSteps) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sizeDone);
                    textView.setText(String.valueOf(i + 1));
                    textView.setTextColor(Color.WHITE);
                }
                layout.addView(textView);
            } else {
                final ImageView imageView = getImageView();
                imageView.setLayoutParams(getCLParams(sizeDone, sizeDone));
                imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.circle_off));
                layout.addView(imageView);

                final RelativeLayout.LayoutParams params1 = getRLParams();
                params1.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                final TextView textView = getTextView();
                textView.setLayoutParams(params1);
                if (hideSteps) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sizeDone); //10
                    textView.setText(String.valueOf(i + 1));
                    textView.setTextColor(Color.GRAY);
                }
                layout.addView(textView);
            }

            linearLayout.addView(layout);
        }
    }

    private LinearLayout.LayoutParams getParams(int i) {
        final LinearLayout.LayoutParams params = getLLParams();
        if (i == steps - 1){
            params.setMargins(0,0,0,0);
        } else {
            params.setMargins(0,0,24,0);
        }
        return params;
    }

    private LayoutParams getCLParams(final int width, final int height) {
        return new LayoutParams(px2dp(width), px2dp(height));
    }

    private RelativeLayout.LayoutParams getRLParams() {
        return new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    }

    private TextView getTextView() {
        return new TextView(getContext());
    }

    private ImageView getImageView() {
        return new ImageView(getContext());
    }

    private RelativeLayout getRelativeLayout() {
        return new RelativeLayout(getContext());
    }

    private LinearLayout.LayoutParams getLLParams() {
        return new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    }

    public void setEtapa(final int posicion){
        buildRangeBar(posicion);
    }

    private int px2dp(final int dps){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int)(dps * scale + 0.5f);
    }
}