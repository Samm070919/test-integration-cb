package com.pagatodoholdings.posandroid.secciones.acquiring.support;

import android.inputmethodservice.KeyboardView;
import android.net.ParseException;
import android.util.Log;

import com.pagatodoholdings.posandroid.secciones.acquiring.charge.SumListener;
import java.math.BigInteger;

public abstract class FragmentKeyBoard<T> extends TemplateFragment<T> implements KeyboardView.OnKeyboardActionListener {

    private StringBuilder builder;
    private StringBuilder sumBuilder;
    private boolean isSum;
    private boolean isAddSum;
    private SumListener listener;
    private String tag = "KEYBOARD";

    public void setEditText(SumListener listener) {
        this.builder = new StringBuilder();
        this.sumBuilder = new StringBuilder();
        this.listener = listener;
        this.isSum = false;
        this.isAddSum = false;

    }

    @Override
    public void onPress(int primaryCode) {
        Log.d(tag,"onPress: " + primaryCode);
        int lastPosBuilder = builder.length() -1;
        int lastPosSum = sumBuilder.length() -1;


            switch (primaryCode) {
                case -5:
                    makeCaseFive(lastPosSum, lastPosBuilder);
                    break;
                case 81:
                    makeCase81();
                    break;
                default:
                    makeDefault(primaryCode);
                    break;
            }

            listener.onSum(makeSum());
            listener.onTextSum(sumBuilder.toString());
    }

    private void makeDefault(int primaryCode) {
        if (!isSum) {
            builder.append(primaryCode);
        } else {
            sumBuilder.append(primaryCode);
            if (sumBuilder.charAt(0) == '+')
                sumBuilder.deleteCharAt(0);

            isAddSum = false;

        }
    }

    private void makeCase81() {
        isSum = true;
        if (!isAddSum) {
            sumBuilder.append("+");
            if (sumBuilder.charAt(0) == '+') {
                sumBuilder.deleteCharAt(0);
                if (!builder.toString().equalsIgnoreCase("0")){
                    sumBuilder.append(builder);
                    sumBuilder.append("+");
                }
            }
            isAddSum = true;
        }
    }

    private void makeCaseFive(int lastPosSum, int lastPosBuilder) {
        if (lastPosSum > -1) {
            sumBuilder.deleteCharAt(lastPosSum);
            if (sumBuilder.toString().isEmpty())
                isSum = false;
        } else {

            if (lastPosBuilder > 0) {
                builder.deleteCharAt(lastPosBuilder);
            } else {
                builder = new StringBuilder();
                builder.append("0");
            }

        }
    }

    private String makeSum(){
        try {
            String auxt = "0";
            if (!builder.toString().isEmpty())
                auxt = builder.toString();

            String[] lisString = sumBuilder.toString().split("\\+");
            BigInteger totalSum = BigInteger.valueOf(0);
            for (String s : lisString) {
                if (!s.isEmpty())
                    totalSum = totalSum.add(new BigInteger(s));
            }
            if (sumBuilder.toString().equalsIgnoreCase(auxt)) {
                sumBuilder = new StringBuilder();
                return builder.toString();
            }
            if (totalSum.intValue() == 0){
                return builder.toString();
            } else {
                return String.valueOf((totalSum));
            }


        } catch (ParseException e){
            return "0";
        }
    }


    @Override
    public void onRelease(int primaryCode) {
        Log.d(tag,"onRelease: " + primaryCode);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Log.d(tag,"onKey: " + primaryCode);
    }

    @Override
    public void onText(CharSequence text) {
        Log.d(tag,"onText: " + text);
    }

    @Override
    public void swipeLeft() {
        Log.d(tag,"swipeLeft");
    }

    @Override
    public void swipeRight() {
        Log.d(tag,"swipeRight");
    }

    @Override
    public void swipeDown() {
        Log.d(tag,"swipeDown");
    }

    @Override
    public void swipeUp() {
        Log.d(tag,"swipeUp");
    }


}
