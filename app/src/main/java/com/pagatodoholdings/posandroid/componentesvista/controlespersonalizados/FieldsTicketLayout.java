package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

public class FieldsTicketLayout extends ConstraintLayout {

    public FieldsTicketLayout(Context context, @Nullable AttributeSet attrs, final String fieldName, final String field) {
        super(context, attrs);

        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.layout_campos_ticket, this);

        final TextView textView = findViewById(R.id.nombre_campo);
        final TextView textView1 = findViewById(R.id.valor_campo);

        textView.setText(fieldName);
        textView1.setText(field);
    }
}
