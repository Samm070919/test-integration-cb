package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pagatodoholdings.posandroid.R;

import javax.annotation.Nullable;

public class InputNumberFormat extends LinearLayout {

    private boolean mIsCodeSet = false;
    private int nDigits = 0;
    private TextView[] mCodes;

    private TextView tvCode0;
    private TextView tvCode1;
    private TextView tvCode2;
    private TextView tvCode3;
    private TextView tvCode4;
    private TextView tvCode5;
    private TextView tvCode6;
    private TextView tvCode7;
    private TextView tvCode8;
    private TextView tvCode9;
    private TextView tvCode10;
    private TextView tvCode11;
    private TextView tvCode12;
    private TextView tvCode13;
    private TextView tvCode14;
    private TextView tvCode15;
    private TextView tvCode16;
    private TextView tvCode17;
    private TextView tvCode18;
    private TextView tvCode19;

    private View view0;
    private View view1;
    private View view2;
    private View view3;
    private View view4;
    private View view5;
    private View view6;
    private View view7;
    private View view8;
    private View view9;
    private View view10;
    private View view11;
    private View view12;
    private View view13;
    private View view14;
    private View view15;
    private View view16;
    private View view17;
    private View view18;
    private View view19;

    public InputNumberFormat(Context context) {
        super(context);
    }

    public InputNumberFormat(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputNumberFormat(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputNumberFormat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void initComponent(int nDigits) {
        this.nDigits = nDigits;
        initUI();
    }

    private void initUI() {
        inflate(getContext(), R.layout.edit_text_number_format, this);
        clean21Digits();
        bindReferences();
    }

    private void bindReferences() {
        if (nDigits > 0) {
            initViews();

            switch (nDigits) {
                case 20:
                    show20Views();
                    break;
                case 19:
                    show19Views();
                    break;
                case 18:
                    show18Views();
                    break;
                case 17:
                    show17Views();
                    break;
                case 16:
                    show16Views();
                    break;
                case 15:
                    show15Views();
                    break;
                case 14:
                    show14Views();
                    break;
                case 13:
                    show13Views();
                    break;
                case 12:
                    show12Views();
                    break;
                case 11:
                    show11Views();
                    break;
                case 10:
                    show10Views();
                    break;
                case 9:
                    show9Views();
                    break;
                case 8:
                    show8Views();
                    break;
                case 7:
                    show7Views();
                    break;
                case 6:
                    show6Views();
                    break;
                case 5:
                    show5Views();
                    break;
                case 4:
                    show4Views();
                    break;
                case 3:
                    show3Views();
                    break;
                case 2:
                    show2Views();
                    break;
                case 1:
                    show1Views();
                    break;
                    default:

            }

            addNumberViews(nDigits);
        }
    }

    void show20Views(){
        tvCode19.setVisibility(VISIBLE);
        view19.setVisibility(VISIBLE);
        show19Views();
    }

    void show19Views(){
        tvCode18.setVisibility(VISIBLE);
        view18.setVisibility(VISIBLE);
        show18Views();
    }

    private void show18Views() {
        tvCode17.setVisibility(VISIBLE);
        view17.setVisibility(VISIBLE);
        show17Views();
    }

    private void show17Views() {
        tvCode16.setVisibility(VISIBLE);
        view16.setVisibility(VISIBLE);
        show16Views();
    }

    private void show16Views() {
        tvCode15.setVisibility(VISIBLE);
        view15.setVisibility(VISIBLE);
        show15Views();
    }

    private void show15Views() {
        tvCode14.setVisibility(VISIBLE);
        view14.setVisibility(VISIBLE);
        show14Views();
    }

    private void show14Views() {
        tvCode13.setVisibility(VISIBLE);
        view13.setVisibility(VISIBLE);
        show13Views();
    }

    private void show13Views() {
        tvCode12.setVisibility(VISIBLE);
        view12.setVisibility(VISIBLE);
        show12Views();
    }

    private void show12Views() {
        tvCode11.setVisibility(VISIBLE);
        view11.setVisibility(VISIBLE);
        show11Views();
    }

    private void show11Views() {
        tvCode10.setVisibility(VISIBLE);
        view10.setVisibility(VISIBLE);
        show10Views();
    }

    private void show10Views() {
        tvCode9.setVisibility(VISIBLE);
        view9.setVisibility(VISIBLE);
        show9Views();
    }

    private void show9Views() {
        tvCode8.setVisibility(VISIBLE);
        view8.setVisibility(VISIBLE);
        show8Views();
    }

    private void show8Views() {
        tvCode7.setVisibility(VISIBLE);
        view7.setVisibility(VISIBLE);
        show7Views();
    }

    private void show7Views() {
        tvCode6.setVisibility(VISIBLE);
        view6.setVisibility(VISIBLE);
       show6Views();
    }

    private void show6Views() {
        tvCode5.setVisibility(VISIBLE);
        view5.setVisibility(VISIBLE);
        show5Views();
    }

    private void show5Views() {
        tvCode4.setVisibility(VISIBLE);
        view4.setVisibility(VISIBLE);
        show4Views();
    }

    private void show4Views() {
        tvCode3.setVisibility(VISIBLE);
        view3.setVisibility(VISIBLE);
        show3Views();
    }

    private void show3Views() {
        tvCode2.setVisibility(VISIBLE);
        view2.setVisibility(VISIBLE);
        show2Views();
    }

    private void show2Views() {
        tvCode1.setVisibility(VISIBLE);
        view1.setVisibility(VISIBLE);
        show1Views();
    }

    private void show1Views() {
        tvCode0.setVisibility(VISIBLE);
        view0.setVisibility(VISIBLE);
    }

    private void addNumberViews(int nDigits) {
        switch (nDigits) {
            case 20:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13, tvCode14, tvCode15,
                        tvCode16, tvCode17, tvCode18, tvCode19};
                break;
            case 19:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13, tvCode14, tvCode15,
                        tvCode16, tvCode17, tvCode18};
                break;
            case 18:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13, tvCode14, tvCode15,
                        tvCode16, tvCode17};
                break;
            case 17:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13, tvCode14, tvCode15,
                        tvCode16};
                break;
            case 16:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13, tvCode14, tvCode15};
                break;
            case 15:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13, tvCode14};
                break;
            case 14:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12, tvCode13};
                break;
            case 13:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11, tvCode12};
                break;
            case 12:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10, tvCode11};
                break;
            case 11:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9, tvCode10};
                break;
            case 10:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8, tvCode9};
                break;
            case 9:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5,
                        tvCode6, tvCode7, tvCode8};
                break;
            case 8:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5, tvCode6, tvCode7};
                break;
            case 7:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5, tvCode6};
                break;
            case 6:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4, tvCode5};
                break;
            case 5:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3, tvCode4};
                break;
            case 4:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2, tvCode3};
                break;
            case 3:
                mCodes = new TextView[]{tvCode0, tvCode1, tvCode2};
                break;
            case 2:
                mCodes = new TextView[]{tvCode0, tvCode1};
                break;
            case 1:
                mCodes = new TextView[]{tvCode0};
                break;
                default:

        }
    }

    private void initViews() {
        tvCode0 = findViewById(R.id.ediTextNumberFormat0);
        tvCode1 = findViewById(R.id.ediTextNumberFormat1);
        tvCode2 = findViewById(R.id.ediTextNumberFormat2);
        tvCode3 = findViewById(R.id.ediTextNumberFormat3);
        tvCode4 = findViewById(R.id.ediTextNumberFormat4);
        tvCode5 = findViewById(R.id.ediTextNumberFormat5);
        tvCode6 = findViewById(R.id.ediTextNumberFormat6);
        tvCode7 = findViewById(R.id.ediTextNumberFormat7);
        tvCode8 = findViewById(R.id.ediTextNumberFormat8);
        tvCode9 = findViewById(R.id.ediTextNumberFormat9);
        tvCode10 = findViewById(R.id.ediTextNumberFormat10);
        tvCode11 = findViewById(R.id.ediTextNumberFormat11);
        tvCode12 = findViewById(R.id.ediTextNumberFormat12);
        tvCode13 = findViewById(R.id.ediTextNumberFormat13);
        tvCode14 = findViewById(R.id.ediTextNumberFormat14);
        tvCode15 = findViewById(R.id.ediTextNumberFormat15);
        tvCode16 = findViewById(R.id.ediTextNumberFormat16);
        tvCode17 = findViewById(R.id.ediTextNumberFormat17);
        tvCode18 = findViewById(R.id.ediTextNumberFormat18);
        tvCode19 = findViewById(R.id.ediTextNumberFormat19);

        tvCode0.setVisibility(View.GONE);
        tvCode1.setVisibility(View.GONE);
        tvCode2.setVisibility(View.GONE);
        tvCode3.setVisibility(View.GONE);
        tvCode4.setVisibility(View.GONE);
        tvCode5.setVisibility(View.GONE);
        tvCode6.setVisibility(View.GONE);
        tvCode7.setVisibility(View.GONE);
        tvCode8.setVisibility(View.GONE);
        tvCode9.setVisibility(View.GONE);
        tvCode10.setVisibility(View.GONE);
        tvCode11.setVisibility(View.GONE);
        tvCode12.setVisibility(View.GONE);
        tvCode13.setVisibility(View.GONE);
        tvCode14.setVisibility(View.GONE);
        tvCode15.setVisibility(View.GONE);
        tvCode16.setVisibility(View.GONE);
        tvCode17.setVisibility(View.GONE);
        tvCode18.setVisibility(View.GONE);
        tvCode19.setVisibility(View.GONE);

        view0 = findViewById(R.id.view0);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        view5 = findViewById(R.id.view5);
        view6 = findViewById(R.id.view6);
        view7 = findViewById(R.id.view7);
        view8 = findViewById(R.id.view8);
        view9 = findViewById(R.id.view9);
        view10 = findViewById(R.id.view10);
        view11 = findViewById(R.id.view11);
        view12 = findViewById(R.id.view12);
        view13 = findViewById(R.id.view13);
        view14 = findViewById(R.id.view14);
        view15 = findViewById(R.id.view15);
        view16 = findViewById(R.id.view16);
        view17 = findViewById(R.id.view17);
        view18 = findViewById(R.id.view18);
        view19 = findViewById(R.id.view19);

        view0.setVisibility(View.GONE);
        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view3.setVisibility(View.GONE);
        view4.setVisibility(View.GONE);
        view5.setVisibility(View.GONE);
        view6.setVisibility(View.GONE);
        view7.setVisibility(View.GONE);
        view8.setVisibility(View.GONE);
        view9.setVisibility(View.GONE);
        view10.setVisibility(View.GONE);
        view11.setVisibility(View.GONE);
        view12.setVisibility(View.GONE);
        view13.setVisibility(View.GONE);
        view14.setVisibility(View.GONE);
        view15.setVisibility(View.GONE);
        view16.setVisibility(View.GONE);
        view17.setVisibility(View.GONE);
        view18.setVisibility(View.GONE);
        view19.setVisibility(View.GONE);
    }

    private void clean21Digits() {

        TextView tvCode0 = findViewById(R.id.ediTextNumberFormat0);
        TextView tvCode1 = findViewById(R.id.ediTextNumberFormat1);
        TextView tvCode2 = findViewById(R.id.ediTextNumberFormat2);
        TextView tvCode3 = findViewById(R.id.ediTextNumberFormat3);
        TextView tvCode4 = findViewById(R.id.ediTextNumberFormat4);
        TextView tvCode5 = findViewById(R.id.ediTextNumberFormat5);
        TextView tvCode6 = findViewById(R.id.ediTextNumberFormat6);
        TextView tvCode7 = findViewById(R.id.ediTextNumberFormat7);
        TextView tvCode8 = findViewById(R.id.ediTextNumberFormat8);
        TextView tvCode9 = findViewById(R.id.ediTextNumberFormat9);
        TextView tvCode10 = findViewById(R.id.ediTextNumberFormat10);
        TextView tvCode11 = findViewById(R.id.ediTextNumberFormat11);
        TextView tvCode12 = findViewById(R.id.ediTextNumberFormat12);
        TextView tvCode13 = findViewById(R.id.ediTextNumberFormat13);
        TextView tvCode14 = findViewById(R.id.ediTextNumberFormat14);
        TextView tvCode15 = findViewById(R.id.ediTextNumberFormat15);
        TextView tvCode16 = findViewById(R.id.ediTextNumberFormat16);
        TextView tvCode17 = findViewById(R.id.ediTextNumberFormat17);
        TextView tvCode18 = findViewById(R.id.ediTextNumberFormat18);
        TextView tvCode19 = findViewById(R.id.ediTextNumberFormat19);
        TextView tvCode20 = findViewById(R.id.ediTextNumberFormat20);

        View view0 = findViewById(R.id.view0);
        View view1 = findViewById(R.id.view1);
        View view2 = findViewById(R.id.view2);
        View view3 = findViewById(R.id.view3);
        View view4 = findViewById(R.id.view4);
        View view5 = findViewById(R.id.view5);
        View view6 = findViewById(R.id.view6);
        View view7 = findViewById(R.id.view7);
        View view8 = findViewById(R.id.view8);
        View view9 = findViewById(R.id.view9);
        View view10 = findViewById(R.id.view10);
        View view11 = findViewById(R.id.view11);
        View view12 = findViewById(R.id.view12);
        View view13 = findViewById(R.id.view13);
        View view14 = findViewById(R.id.view14);
        View view15 = findViewById(R.id.view15);
        View view16 = findViewById(R.id.view16);
        View view17 = findViewById(R.id.view17);
        View view18 = findViewById(R.id.view18);
        View view19 = findViewById(R.id.view19);


        //Visible
        tvCode0.setVisibility(GONE);
        tvCode1.setVisibility(GONE);
        tvCode2.setVisibility(GONE);
        tvCode3.setVisibility(GONE);
        tvCode4.setVisibility(GONE);
        tvCode5.setVisibility(GONE);
        tvCode6.setVisibility(GONE);
        tvCode7.setVisibility(GONE);
        tvCode8.setVisibility(GONE);
        tvCode9.setVisibility(GONE);
        tvCode10.setVisibility(GONE);
        tvCode11.setVisibility(GONE);
        tvCode12.setVisibility(GONE);
        tvCode13.setVisibility(GONE);
        tvCode14.setVisibility(GONE);
        tvCode15.setVisibility(GONE);
        tvCode16.setVisibility(GONE);
        tvCode17.setVisibility(GONE);
        tvCode18.setVisibility(GONE);
        tvCode19.setVisibility(GONE);
        tvCode20.setVisibility(GONE);

        view0.setVisibility(GONE);
        view1.setVisibility(GONE);
        view2.setVisibility(GONE);
        view3.setVisibility(GONE);
        view4.setVisibility(GONE);
        view5.setVisibility(GONE);
        view6.setVisibility(GONE);
        view7.setVisibility(GONE);
        view8.setVisibility(GONE);
        view9.setVisibility(GONE);
        view10.setVisibility(GONE);
        view11.setVisibility(GONE);
        view12.setVisibility(GONE);
        view13.setVisibility(GONE);
        view14.setVisibility(GONE);
        view15.setVisibility(GONE);
        view16.setVisibility(GONE);
        view17.setVisibility(GONE);
        view18.setVisibility(GONE);
        view19.setVisibility(GONE);
    }

    public void setCode(String text) {

        if (text.length() > 20) {
            text = "00000000000000000000";
            initComponent(text.length());
            Toast.makeText(getContext(), "No. Cuenta excede dimensiones del componente", Toast.LENGTH_SHORT).show();
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