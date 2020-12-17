package com.pagatodoholdings.posandroid.utils.cardutils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CreditCardFormattingTextWatcher implements TextWatcher {

    private EditText etCard;
    private EditText hidden;
    private TextView tvCard;
    private ImageView ivType;
    private boolean isDelete;
    CreditCardType creditCardType;

    public CreditCardFormattingTextWatcher(EditText etcard,TextView tvcard) {
        this.etCard=etcard;
        this.tvCard=tvcard;
    }

    public CreditCardFormattingTextWatcher(EditText etcard,TextView tvcard,CreditCardType creditCardType) {
        this.etCard=etcard;
        this.tvCard=tvcard;
        this.creditCardType=creditCardType;
    }

    public CreditCardFormattingTextWatcher(EditText hidden, EditText etcard, TextView tvcard, ImageView ivType,CreditCardType creditCardType) {
        this.hidden = hidden;
        this.etCard=etcard;
        this.tvCard=tvcard;
        this.ivType=ivType;
        this.creditCardType=creditCardType;
    }


    public CreditCardFormattingTextWatcher(EditText etcard) {
        this.etCard=etcard;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /*No implementation*/}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(before==0)
            isDelete=false;
        else
            isDelete=true;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String source = s.toString();
        String last = source.length() > 0? source.substring(source.length()-1, source.length()): "";
        String source2 = hidden.getText().toString();
        if(!isDelete)
            source2 = hidden.getText().toString() + last;
        else
            if (source2 != null && source2.length() > 0) {
                source2 = source2.substring(0, source2.length() - 1);
            }
        int length= source2.length();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(source2);
        hidden.setText(source2);

        if(length>0 && length%5==0)
        {
            if(isDelete)
                stringBuilder.deleteCharAt(length-1);
            else
                stringBuilder.insert(length-1," ");
            hidden.setText(stringBuilder);
            etCard.setSelection(etCard.getText().length());
            hidden.setSelection(etCard.getText().length());
        }


        if(length>=4&&creditCardType!=null) {
            creditCardType.setCardType(CreditCardUtils.getCardType(source.trim()));
        }

       setTvCardNumber(length,stringBuilder);

        if(ivType!=null&&length==0)
            ivType.setImageResource(android.R.color.transparent);

    }

    private void setTvCardNumber(int length, StringBuilder stringBuilder){
        if(tvCard!=null)
        {
            if(length==0)
                tvCard.setText("XXXX XXXX XXXX XXXX");
            else
                tvCard.setText(stringBuilder);

        }
    }

    public interface CreditCardType
    {
        public void setCardType(int type);
    }

}
