package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pagatodoholdings.posandroid.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class TextViewNumerosDecimalesNew extends LinearLayout {

    private TextView textView_currency;
    private TextView textView_integer;
    private TextView textView_decimal;
    private TextView textView_currency2;

    public TextViewNumerosDecimalesNew(Context context) {
        this(context, null);
    }

    public TextViewNumerosDecimalesNew(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.custom_numeros_decimales_new, this, true);

        textView_currency = mView.findViewById(R.id.tv_currency);
        textView_decimal = mView.findViewById(R.id.tv_decimal);
        textView_integer = mView.findViewById(R.id.tv_integer);
        textView_currency2 = mView.findViewById(R.id.tv_currency2);
    }

    public void setPrice(double price) {

        String[] splitter =roundWithTwoDecimals(price).split("\\.");
        String integer_part = String.format(Locale.US, "%s", splitter[0]);
        String decimal_part = String.format(Locale.US, ".%s", splitter[1]);

        textView_integer.setText(integer_part);
        textView_decimal.setText(decimal_part);
        textView_currency.setText("$");
        textView_currency2.setText("COP");
    }

    public void setPricePeru(double price){
        String[] splitter =roundWithTwoDecimals(price).split("\\.");
        String importantString = String.format(Locale.US, "%s", splitter[0]);
        String integerAmount = importantString.substring(0, importantString.length() - 2);
        String decimalAmount = "." + importantString.substring(importantString.length() - 2);
        //String decimal_part = String.format(Locale.US, ".%s", splitter[1]);

        textView_integer.setText(integerAmount.replace(",", ""));
        textView_decimal.setVisibility(View.VISIBLE);
        textView_decimal.setText(decimalAmount);
        textView_currency.setVisibility(View.VISIBLE);
        textView_currency.setText("S/");
        textView_currency2.setVisibility(View.GONE);
    }

    public String getValue(){
        return textView_integer.getText().toString() + textView_decimal.getText().toString();
    }

    public void setStyle(float size_sp_integer, float size_sp_decimal, float size_sp_currency, int color) {

        textView_integer.setTextSize(TypedValue.COMPLEX_UNIT_SP, size_sp_integer);
        textView_decimal.setTextSize(TypedValue.COMPLEX_UNIT_SP, size_sp_decimal);
        textView_currency.setTextSize(TypedValue.COMPLEX_UNIT_SP, size_sp_currency);
        textView_currency2.setTextSize(TypedValue.COMPLEX_UNIT_SP,size_sp_currency);

        textView_integer.setTextColor(color);
        textView_decimal.setTextColor(color);
        textView_currency.setTextColor(color);
        textView_currency2.setTextColor(color);
    }

    private String roundWithTwoDecimals(double number) {
        try {
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
            df.setDecimalFormatSymbols(dfs);
            df.applyPattern("###,##0.00");
            return df.format(number);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
