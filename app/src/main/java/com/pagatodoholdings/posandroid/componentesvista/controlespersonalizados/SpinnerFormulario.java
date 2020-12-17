package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.SpinnerFormularioAdapter;

import net.fullcarga.android.api.formulario.FormatoEnum;
import net.fullcarga.android.api.formulario.Parametro;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class SpinnerFormulario extends LinearLayout {

    private Pair<String, String> selectedPair;

    public final Parametro parametro;

    public Pair<String, String> getSelectedPair() {
        return selectedPair;
    }

    public SpinnerFormulario(final Context context, final Parametro parametro) {
        super(context, null);

        this.parametro = parametro;

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.spinner_formulario, this);

        final TextView titulo = findViewById(R.id.tituloTextView);
        titulo.setText(parametro.getLiteral());

        final FormatoEnum formatoEnum = (FormatoEnum) parametro.getFormato();

        final List<Pair<String, String>> opciones = new ArrayList(formatoEnum.getMapValues().values());

        final SpinnerFormularioAdapter spinnerFormularioAdapter = new SpinnerFormularioAdapter(context, android.R.layout.simple_list_item_1, opciones);

        final Spinner spinner = findViewById(R.id.opcionesSpinner);
        spinner.setAdapter(spinnerFormularioAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long idSpinner) {

                selectedPair = (Pair<String, String>) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

                selectedPair = null;
            }
        });
    }
}
