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

/**
 * Created by jmhernandez on 05/09/2017.
 */
public class AdaptadorSpinnerTotemTipos extends ArrayAdapter<String> {

    private final List<String> items;

    public AdaptadorSpinnerTotemTipos(@NonNull final Context context, final List<String> objects) {
        super(context, android.R.layout.simple_spinner_dropdown_item, objects);
        this.items = objects;
    }

    @Override
    public View getDropDownView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        View mView;
        if (convertView == null) {
            mView = LayoutInflater.from(getContext())
                    .inflate(R.layout.content_spinner, parent, false);
        } else {
            mView = convertView;
        }
        final TextView txtNombre = mView.findViewById(R.id.txtData);

        txtNombre.setText(items.get(position));
        return mView;
    }
}
