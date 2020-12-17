package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;


import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCardBackBinding;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardBackFragment extends androidx.fragment.app.Fragment {

    RegistroRegistraTarjetaStep2 activity;
    CCSecureCodeFragment securecode;

    public CardBackFragment() {
        // Required empty public constructor
    }

    public CardBackFragment(RegistroRegistraTarjetaStep2 parent) {
        activity = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FragmentCardBackBinding view = DataBindingUtil.inflate(inflater, R.layout.fragment_card_back, container, false);
        Log.d("BF", "onCreateView: ");

        securecode = activity.secureCodeFragment;
        securecode.setCvv(view.tvCvv);

        if(!TextUtils.isEmpty(securecode.getValue()))
            view.tvCvv.setText(securecode.cvv);

        return view.getRoot();
    }

}
