package com.pagatodoholdings.posandroid.secciones.money_in;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank;
import com.pagatodoholdings.posandroid.utils.PicassoTrustAll;

import java.util.List;

public class BankListAdapter extends RecyclerView.Adapter<BankListAdapter.BankViewHolder> {

    private List<Bank> bankList;
    private BankOnClickListener bankOnClickListener;
    private Context context;

    private static final String NO_DISPONIBLE = "NO DISPONIBLE";

    public BankListAdapter(List<Bank> bankList, BankOnClickListener bankOnClickListener) {
        this.bankList = bankList;
        this.bankOnClickListener = bankOnClickListener;
    }


    public List<Bank> getBankList() {
        return bankList;
    }

    public void setBankList(List<Bank> bankList) {
        this.bankList = bankList;
    }


    @NonNull
    @Override
    public BankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        context = parent.getContext();
        View vista = LayoutInflater.from(context).inflate(R.layout.item_rv_money_in_bank_list, parent, false);

        return new BankViewHolder(vista, this.bankOnClickListener);
    }

    @Override
    public int getItemCount() {
        return getBankList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull BankViewHolder bankViewHolder, int position) {

        final Bank bank = getBankList().get(position);

        if (bank.getIcono() != null && !bank.getIcono().equals(NO_DISPONIBLE) ) {
            PicassoTrustAll.getInstance(context)
                    .load(bank.getIcono())
                    .into(bankViewHolder.imageViewIcon);

        } else {
            bankViewHolder.txtName.setText(bank.getDescripcion());
        }
    }

    class BankViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtName;
        private final ImageView imageViewIcon;
        private final BankOnClickListener bankOnClickListener;

        public BankViewHolder(@NonNull View itemView, BankOnClickListener bankOnClickListener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.tv_bank_name);
            imageViewIcon = itemView.findViewById(R.id.iv_bank_icon);
            this.bankOnClickListener = bankOnClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.bankOnClickListener.onClick(view,  getAdapterPosition());
        }
    }

    interface BankOnClickListener {
        void onClick(final View vista, final int position);
    }
}
