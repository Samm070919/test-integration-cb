package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.pagatodo.qposlib.dongleconnect.AplicacionEmv;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.DialogoSeleccionBinding;
import com.pagatodoholdings.posandroid.databinding.ItemDatosAgenteBinding;

import java.util.List;

public class DialogoSeleccionString extends DialogFragment {

    private DialogoSeleccionBinding binding;
    private Context context;
    private List<String> aplicacionesList;
    private AplicacionEmv aplicacionEmv;

    public DialogoSeleccionString() {
        //Constructor vacio
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        context = MposApplication.getInstance();
        binding = DialogoSeleccionBinding.inflate(inflater, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
        this.setRetainInstance(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimacionModal;
        setCancelable(false);
    }

    public void setAplicaciones(final List<String> aplicacionesList, final AplicacionEmv aplicacionEmv){
        this.aplicacionesList = aplicacionesList;
        this.aplicacionEmv = aplicacionEmv;
    }

    private void initView() {

        binding.ivIconoStatus.setImageDrawable( ResourcesCompat.getDrawable(getResources(), R.drawable.ic_logo_blue, null)) ;
        binding.ivIconoStatus.getLayoutParams().height = 150;
        binding.ivIconoStatus.getLayoutParams().width = 150;
        binding.tvTituloModal.setText(context.getResources().getString(R.string.seleccion_operacion));
        binding.tvTituloModal.setTextSize(26);
        final DialogoSeleccionString.StringAdapter adapter = new DialogoSeleccionString.StringAdapter(aplicacionesList, aplicacionEmv);
        final LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvOperaciones.setHasFixedSize(true);
        binding.rvOperaciones.setLayoutManager(llm);
        binding.rvOperaciones.setAdapter(adapter);
    }

    public class StringAdapter extends RecyclerView.Adapter<StringAdapter.Holder> {

        List<String> aplicaciones;
        AplicacionEmv aplicacionEmv;

        public StringAdapter(final List<String> aplicaciones, final AplicacionEmv aplicacionEmv) {
            this.aplicaciones = aplicaciones;
            this.aplicacionEmv = aplicacionEmv;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            return new Holder(ItemDatosAgenteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {
            holder.binding.ivAgente.setImageDrawable(context.getDrawable(R.drawable.ic_shopping_kart));
            holder.binding.ivAgente.getLayoutParams().height = 100;
            holder.binding.ivAgente.getLayoutParams().width = 100;
            holder.binding.tvAgente.setText(aplicaciones.get(position));
            holder.binding.tvAgente.setTextSize(20);
            holder.binding.llSaldoButton.setOnClickListener((View v)-> {
                    dismiss();
                    aplicacionEmv.seleccionAppEmv(position);
            });
        }

        @Override
        public int getItemCount() {
            return aplicaciones.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ItemDatosAgenteBinding binding;

            Holder(final ItemDatosAgenteBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
