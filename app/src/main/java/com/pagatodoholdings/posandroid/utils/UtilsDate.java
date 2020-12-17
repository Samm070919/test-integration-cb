package com.pagatodoholdings.posandroid.utils;

import android.util.Log;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilsDate {

    private UtilsDate() {
        //Private
    }

    public static DatePickerDialog getMaterialDatePicker(DatePickerDialog.OnDateSetListener callback, int yearsAgo) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, now.get(Calendar.YEAR) - yearsAgo);
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                callback,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMaxDate(now);

        return dpd;
    }

    public static String formatDatePickerDialog(int dayOfMonth, int monthOfYear, int year) {

        String day = "";
        String month = "";
        int iMonth = ++monthOfYear;
        if (dayOfMonth < 10) {
            day = String.format("0%s", dayOfMonth);
        } else {
            day = String.format("%s", dayOfMonth);
        }

        if (iMonth < 10) {
            month = String.format("0%s", iMonth);
        } else {
            month = String.format("%s", iMonth);
        }

        return String.format("%s-%s-%s", year, month, day);

    }

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String getDateFormat(String dateString) {
        Date date = null;
        try {
            date = DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
        try {
            return DATE_FORMAT.format(date);
        }catch (Exception e){
            Log.d("UtilsDate", e.getMessage());
        }
        return dateString;
    }

    public static String getDateNewFormat() {
        String date;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        date = "" + day + " " + getMonthText(month) + " " + year;
        return date;
    }

    public static String getDateNewFormat(Date dateToFormat){
        String date;
        Calendar c = Calendar.getInstance();
        c.setTime(dateToFormat);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        date = "" + day + " " + getMonthText(month) + " " + year;
        return date;
    }

    //obtener el mes
    public static String getMonthText(int monthNumber) {
        String monthText = "";

        switch (monthNumber) {
            case 0:
                monthText = "Ene";
                break;
            case 1:
                monthText = "Feb";
                break;
            case 2:
                monthText = "Mar";
                break;
            case 3:
                monthText = "Abr";
                break;
            case 4:
                monthText = "May";
                break;
            case 5:
                monthText = "Jun";
                break;
            case 6:
                monthText = "Jul";
                break;
            case 7:
                monthText = "Ago";
                break;
            case 8:
                monthText = "Sep";
                break;
            case 9:
                monthText = "Oct";
                break;
            case 10:
                monthText = "Nov";
                break;
            case 11:
                monthText = "Dic";
                break;
        }
        return monthText;
    }
}
