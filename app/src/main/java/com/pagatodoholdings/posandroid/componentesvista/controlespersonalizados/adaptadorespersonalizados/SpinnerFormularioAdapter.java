package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class SpinnerFormularioAdapter extends ArrayAdapter<Pair<String, String>> {

    final List<Pair<String, String>> opciones;

    public SpinnerFormularioAdapter(@NonNull final Context context, final int resource, @NonNull final List<Pair<String, String>> opciones) {
        super(context, resource, opciones);

        this.opciones = opciones;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        final TextView text = view.findViewById(android.R.id.text1);
        text.setText(opciones.get(position).getKey());

        return view;
    }

    @Override
    public View getDropDownView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
       final View view = super.getDropDownView(position, convertView, parent);
       final TextView text = view.findViewById(android.R.id.text1);
        text.setText(opciones.get(position).getKey());

        return view;
    }
}
