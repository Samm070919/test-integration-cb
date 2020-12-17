package com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.InputCardNumber;
import com.pagatodoholdings.posandroid.databinding.FragmentCcnumberBinding;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;
import com.pagatodoholdings.posandroid.utils.cardutils.CreditCardFormattingTextWatcher;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class CCNumberFragment extends Fragment implements View.OnClickListener,
        EditText.OnEditorActionListener{

    TextView tvNumber;
    FragmentCcnumberBinding view;

    RegistroRegistraTarjetaStep2 activity;
    CardFrontFragment cardFrontFragment;

    InputCardNumber inputNumberCard;
    private int typeCard;

    public CCNumberFragment() {
        // Required empty public constructor
    }

    public CCNumberFragment(RegistroRegistraTarjetaStep2 parent) {
        activity = parent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = DataBindingUtil.inflate(inflater, R.layout.fragment_ccnumber, container, false);

        cardFrontFragment = activity.cardFrontFragment;

        tvNumber = cardFrontFragment.getNumber();


        view.inputCard.setRequest();

        view.inputCard.getEditText().setOnEditorActionListener(this);
        //Do your stuff
        view.inputCard.getEditText().addTextChangedListener(new CreditCardFormattingTextWatcher(view.etNumber, view.inputCard.getEditText(), tvNumber,cardFrontFragment.getCardType(),new CreditCardFormattingTextWatcher.CreditCardType() {
            @Override
            public void setCardType(int type) {
                Log.d("Card", "setCardType: "+type);
                setTypeCard(type);
                cardFrontFragment.setCardType(type);
            }
        }));

        view.inputCard.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (activity != null) {
                        activity.nextClick();
                        return true;
                    }

                }
                return false;
            }
        });

        return view.getRoot();
    }

    public String getCardNumber() {
        if (view.etNumber != null)
            return view.etNumber.getText().toString().trim();

        return null;
    }


    protected void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onClick(View v) { /*No implementation*/ }

    @Override
    public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE
                || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            hideKeyBoard(inputNumberCard.getEditText());
            return true;
        }
        return false;
    }

    protected void hideKeyBoard(View view){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public int getTypeCard() {
        return typeCard;
    }

    public void setTypeCard(int typeCard) {
        this.typeCard = typeCard;
    }
}
