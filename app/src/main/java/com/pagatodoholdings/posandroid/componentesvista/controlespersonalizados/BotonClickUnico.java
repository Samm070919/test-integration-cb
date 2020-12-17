package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.utils.Constantes;

public class BotonClickUnico extends androidx.appcompat.widget.AppCompatButton {

    private long tiempoUltimoClick ;
    private long delay = Constantes.TIEMPO_ENTRE_CLICKS;

    public BotonClickUnico(final Context context) {
        super(context);
    }

    public BotonClickUnico(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context.obtainStyledAttributes(attrs, R.styleable.EditTextDatosUsuarios));
    }

    private void init(final TypedArray atributos) {
        if (atributos.hasValue(R.styleable.BotonClickUnico_delay)) {
            delay = atributos.getResourceId(R.styleable.BotonClickUnico_delay, 600);
        }
    }

    @Override
    public boolean performClick() {
        if (SystemClock.elapsedRealtime() - tiempoUltimoClick < delay) {
            return false;
        }
        tiempoUltimoClick = SystemClock.elapsedRealtime();
        return super.performClick();
    }
}

