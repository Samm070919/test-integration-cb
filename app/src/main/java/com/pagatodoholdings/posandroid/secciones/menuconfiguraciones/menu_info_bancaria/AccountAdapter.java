package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosCuentaBancaria;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.CardViewHolder> {

    private List<DatosCuentaBancaria> accountList;
    private CardOnClickListener cardOnClickListener;
    private Context context;

    private static final String NO_DISPONIBLE = "NO DISPONIBLE";

    public AccountAdapter(List<DatosCuentaBancaria> accountList, CardOnClickListener cardOnClickListener) {
        this.accountList = accountList;
        this.cardOnClickListener = cardOnClickListener;
    }


    public List<DatosCuentaBancaria> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<DatosCuentaBancaria> bankList) {
        this.accountList = bankList;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        context = parent.getContext();
        View vista = LayoutInflater.from(context).inflate(
                R.layout.config_menu_account_item,
                parent,
                false
        );

        vista.setLayoutParams(
                new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                )
        );

        return new CardViewHolder(vista, this.cardOnClickListener);
    }

    @Override
    public int getItemCount() {
        return getAccountList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int position) {

        final DatosCuentaBancaria card = getAccountList().get(position);
            cardViewHolder.txtBank.setText(card.getBanco());
            cardViewHolder.txtAccountNumber.setText(card.getCtabancaria());
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtBank;
        private final TextView txtAccountNumber;
        private final ImageView imageViewIcon;
        private final CardOnClickListener bankOnClickListener;
        public RelativeLayout viewBackground, viewForeground;


        public CardViewHolder(@NonNull View itemView, CardOnClickListener bankOnClickListener) {
            super(itemView);
            txtBank = itemView.findViewById(R.id.tvBank);
            txtAccountNumber = itemView.findViewById(R.id.tvAccountNumber);
            imageViewIcon = itemView.findViewById(R.id.ivBank);

            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);

            this.bankOnClickListener = bankOnClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            this.bankOnClickListener.onClick(view,  getAdapterPosition());
        }
    }

    interface CardOnClickListener {
        void onClick(final View vista, final int position);
    }

    public void deleteItem(int position) {
        DatosCuentaBancaria mRecentlyDeletedItem = accountList.get(position);
        int mRecentlyDeletedItemPosition = position;
        accountList.remove(position);
        notifyItemRemoved(position);
        // showUndoSnackbar();
    }
}
