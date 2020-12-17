package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import java.util.List;

public class GenericAdaptadorSpinner<T> extends ArrayAdapter<T> {

    private final List<T> objects;

    public GenericAdaptadorSpinner(@NonNull final Context context, @NonNull final List<T> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        this.objects = objects;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        final View mView = convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.content_spinner, parent, false) : convertView;
        final TextView txtNombre = mView.findViewById(R.id.txtData);

        txtNombre.setText(getItem(position).toString());
        return mView;
    }

    /**
     * Necesario ya que la clase padre no lo implementa
     *
     * @param position
     * @return
     */
    @Nullable
    @Override
    public T getItem(final int position) {
        return objects.get(position);
    }
}
