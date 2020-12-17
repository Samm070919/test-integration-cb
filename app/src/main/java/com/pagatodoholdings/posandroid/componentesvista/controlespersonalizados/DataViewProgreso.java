package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataViewProgreso {

    private final TextView titulo;
    private final TextView porcentaje;
    private final TextView restantes;
    private final ProgressBar progresoHorizontal;
    private final ProgressBar progresoVertical;

    public DataViewProgreso(final View view) {

        titulo = view.findViewById(R.id.progreso_data_view_titulo);
        progresoHorizontal = view.findViewById(R.id.progreso_data_view_progreso_determinado);
        progresoVertical = view.findViewById(R.id.progreso_data_view_progreso_indeterminado);
        porcentaje = view.findViewById(R.id.progreso_data_view_porcentaje);
        restantes = view.findViewById(R.id.progreso_data_view_restantes);
    }

    public boolean contieneNumeros(final String string) {

        final Pattern pattern = Pattern.compile("[0-9]");
        final Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    public String limpiarPorRegex(final String paramStr, final String paramRegex) {
        return paramStr.replaceAll(paramRegex, "");
    }

    public void establecerRestantes(final String paramRestantes) {
        restantes.setText(paramRestantes);
    }

    public void establecerTitulo(final String paramTitulo) {
        titulo.setText(paramTitulo);
    }

    public void establecerPorcentaje(final String paramPorcentaje) {
        porcentaje.setText(paramPorcentaje);
    }

    public void establecerProgreso(final int progreso) {
        progresoHorizontal.setProgress(progreso);
    }

    public int obtenerPMax() {
        return progresoHorizontal.getMax();
    }

    public void mostrarProgresoIndeterminado(final boolean mostrar) {
        if (mostrar) {
            progresoVertical.setVisibility(View.VISIBLE);
            progresoHorizontal.setVisibility(View.GONE);
            porcentaje.setVisibility(View.GONE);
            restantes.setVisibility(View.GONE);
        } else {
            progresoVertical.setVisibility(View.GONE);
            progresoHorizontal.setVisibility(View.VISIBLE);
            porcentaje.setVisibility(View.VISIBLE);
            restantes.setVisibility(View.VISIBLE);
        }
    }
}
