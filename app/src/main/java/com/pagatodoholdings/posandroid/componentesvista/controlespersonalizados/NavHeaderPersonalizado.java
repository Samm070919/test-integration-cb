package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.pagatodoholdings.posandroid.R;

public class NavHeaderPersonalizado extends LinearLayout {


    public NavHeaderPersonalizado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navigationheader, this, true);


    }





}
