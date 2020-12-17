package com.pagatodoholdings.posandroid.secciones.acquiring.charge;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.ButtonCheckerBinding;

import java.util.ArrayList;
import java.util.List;

public class AdqProdAdapter extends RecyclerView.Adapter<BtnCheckHolder> {

    private List<BtnCheckHolder.ItemProdData> listProd;
    private ArrayList<BtnCheckHolder> holders;
    private AdqProdListener listener;

    AdqProdAdapter(AdqProdListener listener) {
        this.listProd = new ArrayList<>();
        this.holders = new ArrayList<>();
        this.listener = listener;
    }

    void setListProd(List<BtnCheckHolder.ItemProdData> listProd) {
        this.listProd = listProd;
    }

    BtnCheckHolder getHolder(BtnCheckHolder.ItemProdData data) {
        return holders.get(this.listProd.indexOf(data));
    }

    @NonNull
    @Override
    public BtnCheckHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ButtonCheckerBinding binding = DataBindingUtil.inflate(inflater, R.layout.button_checker, viewGroup, false);
        binding.btnAhorro.setSelected(true);

        BtnCheckHolder holder = new BtnCheckHolder(binding);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BtnCheckHolder btnCheckHolder, int pos) {
        btnCheckHolder.bind(this.listProd.get(pos), this.listener);
    }

    @Override
    public int getItemCount() {
        return this.listProd.size();
    }
}
