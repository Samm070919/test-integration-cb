package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCardFrontBinding;

import static com.pagatodoholdings.posandroid.utils.cardutils.CreditCardUtils.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFrontFragment extends Fragment {

    FragmentCardFrontBinding view;


    public CardFrontFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = DataBindingUtil.inflate(inflater, R.layout.fragment_card_front, container, false);

        return view.getRoot();
    }

    public TextView getNumber() {
        return view.tvCardNumber;
    }

    public TextView getName() {
        return view.tvMemberName;
    }

    public TextView getValidity() {
        return view.tvValidity;
    }

    public ImageView getCardType() {
        return view.ivType;
    }


    public void setCardType(int type) {
        switch (type) {
            case VISA:
                view.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visa));
                view.ivTypeCard.setVisibility(View.INVISIBLE);
                break;
            case MASTERCARD:
                view.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mastercard));
                view.ivTypeCard.setVisibility(View.INVISIBLE);
                break;
            case AMEX:
                view.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_amex_bg));
                view.ivTypeCard.setVisibility(View.INVISIBLE);
                break;
            case DINNER:
                view.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dinner_bg));
                view.ivTypeCard.setVisibility(View.INVISIBLE);
                break;
            default:
                view.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bg_card_step1));
                view.ivTypeCard.setVisibility(View.VISIBLE);
                view.ivTypeCard.setImageResource(android.R.color.transparent);

        }

    }


}
