package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCcvalidityBinding;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;
import com.pagatodoholdings.posandroid.utils.cardutils.CreditCardEditText;
import com.pagatodoholdings.posandroid.utils.cardutils.CreditCardExpiryTextWatcher;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CCValidityFragment extends Fragment {

    RegistroRegistraTarjetaStep2 activity;
    CardFrontFragment cardFrontFragment;
    FragmentCcvalidityBinding binding;
    TextView tv_validity;

    public CCValidityFragment() {
        super();
        // Required empty public constructor
    }

    public CCValidityFragment(RegistroRegistraTarjetaStep2 parent) {
        activity = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ccvalidity, container, false);

        cardFrontFragment = activity.cardFrontFragment;

        tv_validity = activity.cardFrontFragment.getValidity();
        binding.etValidity.addTextChangedListener(new CreditCardExpiryTextWatcher(binding.etValidity, tv_validity));

        binding.etValidity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (activity != null && isValidateExpirationDate(v.getText().toString())) {
                        activity.nextClick();
                        return true;
                    }

                }
                return false;
            }
        });


        binding.etValidity.setOnBackButtonListener(new CreditCardEditText.BackButtonListener() {
            @Override
            public void onBackClick() {
               hideKeyBoard(getView());
            }
        });


        return binding.getRoot();
    }

    private boolean isValidateExpirationDate(String validity){
        if(validity.trim().length()>0 && validity.trim().length()>=5){
            int month = Integer.parseInt(validity.substring(0,2));
            int year = Integer.parseInt(validity.substring(3,5));

            if(month <=12 && year <=50){
                return true;
            }else{
                Toast.makeText(getContext(), "La Fecha de Vencimiento es Incorreta. Intenta Nuevamente",Toast.LENGTH_SHORT).show();
                return false;
            }

        }
            Toast.makeText(getContext(), "La Fecha de Vencimiento es Incorreta. Intenta Nuevamente",Toast.LENGTH_SHORT).show();
            return false;
    }

    protected void hideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getValidity()
    {
        if(binding.etValidity!=null)
            return binding.etValidity.getText().toString().trim();

        return null;
    }

}
