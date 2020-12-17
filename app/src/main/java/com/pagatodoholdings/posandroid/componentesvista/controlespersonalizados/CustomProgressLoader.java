package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

import java.util.Objects;

public class CustomProgressLoader extends ProgressDialog {//NOSONAR

    //----- Var ----------------------------------------------------------
    private String mensaje;

    public CustomProgressLoader(final Context context) {
        super(context);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void show() {
        super.show();
        setContentView(R.layout.layout_loader);
        final ProgressBar progressBarIndeterminado = findViewById(R.id.loader_progress_bar_indeterminado);
        final TextView titleTextView = findViewById(R.id.layout_loader_title);
        titleTextView.setText(mensaje);

        progressBarIndeterminado.setOnTouchListener((final View v,final MotionEvent motionEvent) -> {
            reiniciarContador();
            return false;});
    }

    public void mostrarActualizacionProductos(){
        super.show();
        setContentView(R.layout.layout_loader);
        final TextView titleTextView = findViewById(R.id.layout_loader_title);
        final TextView subtitleTextView = findViewById(R.id.tv_productos_progress);
        titleTextView.setText(mensaje);
        subtitleTextView.setText(R.string.actualizacion_productos);
    }

    private void reiniciarContador() {
        if (getContext() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getContext();
            activity.iniciarContador();
        }
    }

    public void setMessage(final String mensaje) {
        this.mensaje = mensaje;
    }



    private void init() {
        setCancelable(false);
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

}
