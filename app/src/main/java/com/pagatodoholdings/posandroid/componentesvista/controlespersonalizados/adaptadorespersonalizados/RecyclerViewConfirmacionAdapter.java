package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import net.fullcarga.android.api.formulario.Parametro;

import java.util.List;

public class RecyclerViewConfirmacionAdapter extends RecyclerView.Adapter<RecyclerViewConfirmacionAdapter.MyViewHolder> {

    private final List<Parametro> parametros;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public final TextView literalTextView;
        public final TextView valorTextView;

        public MyViewHolder(final View view) {
            super(view);

            literalTextView = view.findViewById(R.id.literalTextView);
            valorTextView = view.findViewById(R.id.valorTextView);
        }
    }

    public RecyclerViewConfirmacionAdapter(final List<Parametro> parametros) {

        this.parametros = parametros;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Parametro parametro = parametros.get(position);
        holder.literalTextView.setText(parametro.getLiteral());
        holder.valorTextView.setText(parametro.getValue());
    }

    @Override
    public int getItemCount() {

        return parametros.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_confirmation_item, parent, false);
        return new MyViewHolder(view);
    }
}