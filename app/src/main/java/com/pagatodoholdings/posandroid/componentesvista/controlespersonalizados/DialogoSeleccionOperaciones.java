package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import androidx.fragment.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.DialogoSeleccionBinding;
import com.pagatodoholdings.posandroid.databinding.ItemOperacionesDialogoBinding;
import com.pagatodoholdings.posandroid.general.interfaces.SeleccionOperacionListener;
import com.pagatodoholdings.posandroid.utils.Utilities;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.LiteralesOperacion;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import java.util.List;


public class DialogoSeleccionOperaciones extends DialogFragment {

    private static final String TAG = DialogoSeleccionOperaciones.class.getName();
    private DialogoSeleccionBinding binding;
    private Context context;
    private List<Operaciones> operaciones;
    private SeleccionOperacionListener listener;

    public DialogoSeleccionOperaciones() {
        //Constructor vacio
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
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

    private void initView() {

        binding.ivIconoStatus.setBackground(ResourcesCompat.getDrawable(getResources(), Utilities.getAttributeReference(R.attr.fullcarga), null));
        binding.tvTituloModal.setText(context.getResources().getString(R.string.seleccion_operacion));
        binding.tvTituloModal.setTextSize(26);
        final OperacionesAdapter adapter = new OperacionesAdapter(operaciones);
        final LinearLayoutManager llm = new LinearLayoutManager(context);
        binding.rvOperaciones.setHasFixedSize(true);
        binding.rvOperaciones.setLayoutManager(llm);
        binding.rvOperaciones.setAdapter(adapter);
    }

    public void setOperaciones(final List<Operaciones> operaciones, final SeleccionOperacionListener listener) {
        this.operaciones = operaciones;
        this.listener = listener;
    }

    public class OperacionesAdapter extends RecyclerView.Adapter<OperacionesAdapter.Holder> {

        List<Operaciones> operaciones;

        public OperacionesAdapter(final List<Operaciones> operaciones) {
            this.operaciones = operaciones;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            return new Holder(ItemOperacionesDialogoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int position) {
            final LiteralesOperacion literal = SigmaBdManager.getLiteralOperacion(operaciones.get(position), new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener literal"));
            holder.binding.ivAgente.setImageDrawable(context.getDrawable(R.drawable.ic_shopping_kart));
            holder.binding.ivAgente.getLayoutParams().height = 100;
            holder.binding.ivAgente.getLayoutParams().width = 100;
            holder.binding.tvAgente.setText(literal.getValor());
            holder.binding.tvAgente.setTextSize(20);
            holder.binding.llSaldoButton.setOnClickListener((View v)-> {
                    dismiss();
                    listener.getOperacionSeleccionada(operaciones.get(position));
            });
        }

        @Override
        public int getItemCount() {
            return operaciones.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ItemOperacionesDialogoBinding binding;

            Holder(final ItemOperacionesDialogoBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}

