package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.animaciones.AnimacionBlink;
import com.pagatodoholdings.posandroid.utils.Utilities;

public class ViewBlink extends View {

    final AnimacionBlink animacionBlink;

    public ViewBlink(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        final int color = Utilities.getAttributeReference(R.attr.primaryDark);
        animacionBlink = new AnimacionBlink(this, color,Color.WHITE,color);
    }

    public void detenAnimacion(final int color) {
        animacionBlink.deten();
        setBackgroundColor(color);
    }

    public void detenAnimacion() {
        animacionBlink.deten();
    }

    public void anima() {
        animacionBlink.inicia();
    }
}
