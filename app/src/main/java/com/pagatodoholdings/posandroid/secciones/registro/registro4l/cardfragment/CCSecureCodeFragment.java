package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentCcsecureCodeBinding;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;
import com.pagatodoholdings.posandroid.utils.cardutils.CreditCardEditText;

import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class CCSecureCodeFragment extends Fragment {

    TextView tv_cvv;
    FragmentCcsecureCodeBinding view;
    RegistroRegistraTarjetaStep2 activity;
    StringBuilder cvv = new StringBuilder();

    public CCSecureCodeFragment() {
        // Required empty public constructor
    }

    public CCSecureCodeFragment(RegistroRegistraTarjetaStep2 parent) {
        activity = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = DataBindingUtil.inflate(inflater, R.layout.fragment_ccsecure_code, container, false);

        view.etCvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cvv = new StringBuilder();
                for(int i = 0; i < editable.toString().length(); i++){
                    cvv.append("·");
                }

                if (tv_cvv != null) {
                    if (TextUtils.isEmpty(editable.toString().trim()))
                        tv_cvv.setText("···");
                    else
                        tv_cvv.setText(cvv);

                } else
                    Log.d(TAG, "afterTextChanged: cvv null");

            }
        });

        view.etCvv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if(activity!=null)
                    {
                        activity.nextClick();
                        return true;
                    }

                }
                return false;
            }
        });

        view.etCvv.setOnBackButtonListener(new CreditCardEditText.BackButtonListener() {
            @Override
            public void onBackClick() {
                hideKeyBoard(getView());
            }
        });

        return view.getRoot();

    }

    public void setCvv(TextView tv) {
        tv_cvv = tv;
    }

    public String getValue() {

        String getValue = "";

        if (view.etCvv != null) {
            getValue = view.etCvv.getText().toString().trim();
        }
        return getValue;
    }
    protected void hideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
