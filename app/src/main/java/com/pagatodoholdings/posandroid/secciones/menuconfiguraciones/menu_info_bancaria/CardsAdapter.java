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

import com.google.android.material.snackbar.Snackbar;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.money_in.models.Bank;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosTarjetaCoFBean;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.PicassoTrustAll;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import java.util.List;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.CardViewHolder> {

    private List<DatosTarjetaCoFBean> cardList;
    private CardOnClickListener cardOnClickListener;

    DatosTarjetaCoFBean mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    private static final String NO_DISPONIBLE = "NO DISPONIBLE";

    public CardsAdapter(List<DatosTarjetaCoFBean> cardList, CardOnClickListener cardOnClickListener, Context context) {
        this.cardList = cardList;
        this.cardOnClickListener = cardOnClickListener;
        this.context = context;
    }


    public List<DatosTarjetaCoFBean> getCardList() {
        return cardList;
    }

    public void setCardList(List<DatosTarjetaCoFBean> bankList) {
        this.cardList = bankList;
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        context = parent.getContext();
        View vista = LayoutInflater.from(context).inflate(R.layout.config_menu_tarjeta_item, parent, false);

        vista.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new CardViewHolder(vista, this.cardOnClickListener);
    }

    @Override
    public int getItemCount() {
        return getCardList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int position) {

        final DatosTarjetaCoFBean card = getCardList().get(position);
            if(card.getMarca().toUpperCase().equals(Constantes.TARJETA_VISA)){
                cardViewHolder.imageViewIcon.setBackgroundResource(R.drawable.ic_visa);
            }else if(card.getMarca().toUpperCase().equals(Constantes.TARJETA_MASTER_CARD)){
                cardViewHolder.imageViewIcon.setBackgroundResource(R.drawable.ic_mastercard);
            } else if(card.getMarca().toUpperCase().equals(Constantes.TARJETA_DINNER)){
                cardViewHolder.imageViewIcon.setBackgroundResource(R.drawable.ic_dinner_bg);
            }else if(card.getMarca().toUpperCase().equals(Constantes.TARJETA_AMEX)){
                cardViewHolder.imageViewIcon.setBackgroundResource(R.drawable.ic_amex_bg);
            }else{
                cardViewHolder.imageViewIcon.setBackgroundResource(R.drawable.ic_bg_card_step1);
            }

            cardViewHolder.txtCardType.setText(UtilsKt.capitalizeWords(card.getMarca()));
            cardViewHolder.txtCardNumber.setText("XXXX " + card.getNumero4ultimos());
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = cardList.get(position);
        mRecentlyDeletedItemPosition = position;
        cardList.remove(position);
        notifyItemRemoved(position);
       // showUndoSnackbar();
    }



    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView txtCardType;
        private final TextView txtCardNumber;
        private final ImageView imageViewIcon;
        private final CardOnClickListener bankOnClickListener;
        public RelativeLayout viewBackground, viewForeground;

        public CardViewHolder(@NonNull View itemView, CardOnClickListener bankOnClickListener) {
            super(itemView);
            txtCardType = itemView.findViewById(R.id.tvCardType);
            txtCardNumber = itemView.findViewById(R.id.tvCardNumber);
            imageViewIcon = itemView.findViewById(R.id.ivCard);
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
}
