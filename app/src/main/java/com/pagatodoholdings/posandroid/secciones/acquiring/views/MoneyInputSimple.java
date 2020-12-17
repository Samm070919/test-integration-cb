package com.pagatodoholdings.posandroid.secciones.acquiring.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class MoneyInputSimple extends AppCompatEditText {

    private String symbol = "$";
    private NumberFormat numberFormat;

    public MoneyInputSimple(Context context) {
        super(context);
        init();
    }

    public MoneyInputSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoneyInputSimple(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSingleLine(true);
        setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        setBackgroundColor(Color.TRANSPARENT);
        setCursorVisible(true);
        setFocusable(false);
        setFocusableInTouchMode(false);
        symbol = "=";
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text.toString().isEmpty() || text.toString().contains("\\$0.00")) {
            super.setText(formatMoney("0.00"), type);
            setSelection(Objects.requireNonNull(getText()).length());
        } else {
            super.setText(formatMoney(text.toString()), type);
            setSelection(Objects.requireNonNull(getText()).length());
        }
    }

    public void setText(String text, Boolean isAlreadyFormatted) {
        if (isAlreadyFormatted) {
            super.setText(text, BufferType.SPANNABLE);
        } else {
            setText(text);
        }
    }

    public void setText(String text) {
        setText(text, BufferType.SPANNABLE);
    }

    private SpannableString formatTextView(CharSequence text) {
        SpannableString spannable = new SpannableString(text.toString());
        if (symbol == null) symbol = "=";
        int endIndex = ((String) text).indexOf(symbol);
        if (endIndex < 0) return spannable;
        spannable.setSpan(new MoneySpan(getTextSize(), false),
                endIndex,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private SpannableString formatMoney(String money) {
        try {
            if (numberFormat != null) {
                String format = numberFormat.format(Double.parseDouble(money));
                int tams = symbol.length();
                return formatTextView(format.substring(0, format.length() - tams) + symbol);
            } else {
                NumberFormat aux = NumberFormat.getNumberInstance(currentLocale());
                aux.setMaximumFractionDigits(0);
                return formatTextView(aux.format(Double.parseDouble(money)) + symbol);
            }

        } catch (NumberFormatException e) {
            return new SpannableString(money);
        }
    }

    private Locale currentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getContext().getResources().getConfiguration().getLocales().get(0);
        } else {
            return getContext().getResources().getConfiguration().locale;
        }
    }

    public void setSymbol(String symbol) {

        int tamSym = this.symbol.length();
        String text = Objects.requireNonNull(getText()).toString().substring(tamSym);
        this.symbol = symbol;
        setText(text);
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }
}
