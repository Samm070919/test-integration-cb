package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;


import com.pagatodoholdings.posandroid.R;

import java.util.Objects;

public class InputCardNumber extends LinearLayout implements Input, View.OnFocusChangeListener,TextWatcher {

    private TextView textHint;
    private AppCompatEditText editTextInput;
    private InputSecretListener listener;
    private LinearLayout textLayer;
    private String fixedText;

    public InputCardNumber(Context context) {
        super(context);
        initView(null);
    }

    public InputCardNumber(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public InputCardNumber(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    @Override
    public void initView(AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rootView = inflater.inflate(R.layout.input_card,this,false);
        textHint = rootView.findViewById(R.id.text_hint);
        editTextInput = rootView.findViewById(R.id.edit_text_input);
        //viewMain = rootView.findViewById(R.id.main);
        textLayer = rootView.findViewById(R.id.text_card_layer);

        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.InputCardNumber,
                    0, 0);
            try {
                String resText = a.getString(R.styleable.InputCardNumber_labelHintInput);
                setHint(resText);
            } finally {
                a.recycle();
            }
        }
        bind();
        this.addView(rootView);
    }

    private void bind(){
        editTextInput.setOnFocusChangeListener(this);
        editTextInput.addTextChangedListener(this);
        setRequest();

    }

    public void setRequest(){
        editTextInput.requestFocus();
    }

    public AppCompatEditText getEditText(){
        return this.editTextInput;
    }

    @Override
    public void active() {

    }

    @Override
    public void desactive() {

    }

    @Override
    public void setHint(String hintText) {
        textHint.setText(hintText);
    }

    @Override
    public void setText(String text) {
        char[] textparser = text.toCharArray();
        if (textparser.length < textLayer.getChildCount()){
            for (int i = 0;i < textparser.length;i++){
                ((TextView) textLayer.getChildAt(i)).setText(String.valueOf(textparser[i]));
            }
            editTextInput.setText(text);
        }
        fixedText = text;
        editTextInput.setSelection(Objects.requireNonNull(editTextInput.getText()).toString().length());
    }

    @Override
    public String getText() {
        return Objects.requireNonNull(editTextInput.getText()).toString();
    }

    @Override
    public void withEye(Boolean active) {

    }

    @Override
    public void isError() {
        this.setBackgroundResource(R.drawable.input_text_error);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            active();
            editTextInput.setSelection(Objects.requireNonNull(editTextInput.getText()).toString().length());
        } else {
            desactive();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int lengh = Objects.requireNonNull(editTextInput.getText()).toString().length();
        String text = editTextInput.getText().toString();
        if (lengh >= 0 && lengh < 16 ) {

            if(lengh==0){
                lengh=1;
            }

            ((TextView) textLayer.getChildAt(lengh)).setText("");
            ((TextView) textLayer.getChildAt(lengh - 1)).setText(text.substring(lengh - 1));
            if (this.listener != null) this.listener.inputListenerBegin();
        } else if (lengh == 16) {
            ((TextView) textLayer.getChildAt(15)).setText(text.substring(15));
            if (this.listener != null) this.listener.inputListenerFinish(this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void setInputSecretListener(InputSecretListener listener) {
        this.listener = listener;
    }
}
