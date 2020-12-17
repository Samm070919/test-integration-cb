package com.pagatodoholdings.posandroid.utils.cardutils;

import android.text.TextUtils;
import android.util.Log;

import com.pagatodoholdings.posandroid.utils.Logger;

import java.util.Calendar;

public class CreditCardUtils {
    public static final int NONE = 0;

    public static final int VISA = 1;
    public static final int MASTERCARD = 2;
    public static final int DISCOVER = 3;
    public static final int AMEX = 4;
    public static final int DINNER = 5;

    public static final String VISA_PREFIX = "4";
    public static final String MASTERCARD_PREFIX = "51,52,53,54,55,";
    public static final String DISCOVER_PREFIX = "6011";
    public static final String AMEX_PREFIX = "34,37,";
    public static final String DINNER_PREFIX = "30,36,38,39,";

    public static int getCardType(String cardNumber) {

        try{
            if (cardNumber.substring(0, 1).equals(VISA_PREFIX))
                return VISA;
            else if (MASTERCARD_PREFIX.contains(cardNumber.substring(0, 2) + ","))
                return MASTERCARD;
            else if (AMEX_PREFIX.contains(cardNumber.substring(0, 2) + ","))
                return AMEX;
            else if (cardNumber.substring(0, 4).equals(DISCOVER_PREFIX))
                return DISCOVER;
            else if (DINNER_PREFIX.contains(cardNumber.substring(0, 2)))
                return DINNER;

            return NONE;
        }catch (Exception e){
            Logger.LOGGER.throwing("EXCEPTION", 1, e, e.getMessage());
            return NONE;
        }
    }

    public static boolean isValid(String cardNumber) {
        if (!TextUtils.isEmpty(cardNumber) && cardNumber.length() >= 4)
            if (getCardType(cardNumber) == VISA && ((cardNumber.length() == 13 || cardNumber.length() == 16)))
                return true;
            else if (getCardType(cardNumber) == MASTERCARD && cardNumber.length() == 16)
                return true;
            else if (getCardType(cardNumber) == AMEX && cardNumber.length() == 15)
                return true;
            else if (getCardType(cardNumber) == DISCOVER && cardNumber.length() == 16)
                return true;
            else if (getCardType(cardNumber) == DINNER && ((cardNumber.length() == 14 || cardNumber.length() == 16)))
                return true;
        return false;
    }

    public static boolean isValidDate(String cardValidity) {
        if (!TextUtils.isEmpty(cardValidity) && cardValidity.length() == 5)
        {
            String month=cardValidity.substring(0,2);
            String year=cardValidity.substring(3,5);

            int monthpart=-1,yearpart=-1;

            try
            {
                monthpart=Integer.parseInt(month)-1;
                yearpart=Integer.parseInt(year);

                Calendar current = Calendar.getInstance();
                current.set(Calendar.DATE,1);
                current.set(Calendar.HOUR,12);
                current.set(Calendar.MINUTE,0);
                current.set(Calendar.SECOND,0);
                current.set(Calendar.MILLISECOND,0);

                Calendar validity=Calendar.getInstance();
                validity.set(Calendar.DATE,1);
                validity.set(Calendar.HOUR,12);
                validity.set(Calendar.MINUTE,0);
                validity.set(Calendar.SECOND,0);
                validity.set(Calendar.MILLISECOND,0);

                if(monthpart>-1&&monthpart<12&&yearpart>-1)
                {
                    validity.set(Calendar.MONTH,monthpart);
                    validity.set(Calendar.YEAR,yearpart+2000);
                }
                else
                    return false;

                Log.d("Util", "isValidDate: "+current.compareTo(validity));

                if(current.compareTo(validity)<=0)
                    return true;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        return false;
    }

}