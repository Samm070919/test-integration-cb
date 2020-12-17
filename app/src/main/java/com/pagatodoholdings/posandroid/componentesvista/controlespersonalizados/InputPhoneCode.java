package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

public class InputPhoneCode extends LinearLayout {
    private boolean mIsCodeSet = false;

    private TextView[] mCodes;

    public InputPhoneCode(Context context) {
        super(context);
        initUI();
    }

    public InputPhoneCode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(attrs);
    }

    public InputPhoneCode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(attrs);
    }

    public InputPhoneCode(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI(attrs);
    }

    private void initUI() {
        inflate(getContext(), R.layout.edit_text_phone_code, this);
        bindReferences();
    }

    private void initUI(final AttributeSet attributes) {
        initUI();

        final TypedArray attrs = getContext().obtainStyledAttributes(attributes, R.styleable.InputPhoneCode);

        if (attrs.hasValue(R.styleable.InputPhoneCode_android_textSize)) {
            final float textSize = attrs.getDimensionPixelSize(R.styleable.InputPhoneCode_android_textSize, 0);
            setTextSize(textSize);
        }

        if (attrs.hasValue(R.styleable.InputPhoneCode_android_textColor)) {
            final int color = attrs.getColor(R.styleable.InputPhoneCode_android_textColor, 0);
            setTextColor(color);
        }

        attrs.recycle();
    }

    private void bindReferences() {
        TextView tvCode0 = findViewById(R.id.ediTextPhoneCode0);
        TextView tvCode1 = findViewById(R.id.ediTextPhoneCode1);
        TextView tvCode2 = findViewById(R.id.ediTextPhoneCode2);
        TextView tvCode3 = findViewById(R.id.ediTextPhoneCode3);
        TextView tvCode4 = findViewById(R.id.ediTextPhoneCode4);
        TextView tvCode5 = findViewById(R.id.ediTextPhoneCode5);

        mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5};
    }

    private void setTextColor(final int color) {
        for (TextView code : mCodes) {
            code.setTextColor(color);
        }
    }

    private void setTextSize(final float textSize) {
        for (TextView code : mCodes) {
            code.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    public void setCode(String text){
        if (text.length() != 6) {
            throw new IllegalArgumentException("El código debe contener 6 dígitos");
        }

        final char[] textArray = text.toCharArray();

        int index = 0;

        for (TextView code : mCodes) {
            String stringKey = String.valueOf(textArray[index++]);
            code.setText(stringKey);
        }
        mIsCodeSet = true;
    }

    public boolean isCodeSet() {
        return mIsCodeSet;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }
}
