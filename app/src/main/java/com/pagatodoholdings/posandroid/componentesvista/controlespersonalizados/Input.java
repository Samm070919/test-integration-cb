package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.util.AttributeSet;

public interface Input{
    void initView(AttributeSet attrs);
    void active();
    void desactive();
    void setHint(String hintText);
    void isError();
    void setInputSecretListener(InputSecretListener listener);
    void setText(String text);
    String getText();
    void withEye(Boolean active);
}
