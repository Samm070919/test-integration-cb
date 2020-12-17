package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.pagatodoholdings.posandroid.databinding.FragmentCcnameBinding;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;
import com.pagatodoholdings.posandroid.utils.cardutils.CreditCardEditText;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class CCNameFragment extends Fragment {


    TextView tv_Name;

    RegistroRegistraTarjetaStep2 activity;
    CardFrontFragment cardFrontFragment;
    FragmentCcnameBinding view;

    public CCNameFragment() {
        // Required empty public constructor
    }

    public CCNameFragment(RegistroRegistraTarjetaStep2 parent) {
        activity = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = DataBindingUtil.inflate(inflater, R.layout.fragment_ccname, container, false);

        cardFrontFragment = activity.cardFrontFragment;

        tv_Name = cardFrontFragment.getName();

        configureFiltersEdittext();
        view.etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(tv_Name!=null)
                {
                    if (TextUtils.isEmpty(editable.toString().trim()))
                        tv_Name.setText("CARD HOLDER");
                    else
                        if(editable.toString().length()<27) {
                            tv_Name.setText(editable.toString());
                        }

                }

            }
        });

        view.etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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


        view.etName.setOnBackButtonListener(new CreditCardEditText.BackButtonListener() {
            @Override
            public void onBackClick() {
                hideKeyBoard(getView());
            }
        });

        return view.getRoot();
}

    protected void hideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void configureFiltersEdittext(){
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isLetter(c) || Character.isSpaceChar(c);
            }
        };
        view.etName.setFilters(new InputFilter[] { filter });
    }

    public String getName()
    {
        if(view.etName!=null)
            return view.etName.getText().toString().trim();

        return null;
    }


}
