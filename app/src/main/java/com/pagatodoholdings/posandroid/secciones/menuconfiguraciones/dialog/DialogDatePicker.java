package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.dialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import android.view.Gravity;
import android.widget.DatePicker;

public class DialogDatePicker extends DatePickerDialog {

    public DialogDatePicker(@NonNull final Context context, @StyleRes final int resTheme, final OnDateSetListener callback, final int year, final int mes, final int dia) {
        super(context, resTheme, callback, year, mes, dia);
        setTitle("Seleccionar fecha");
        setButton(BUTTON_POSITIVE, "Aceptar", this);
        final DatePicker datePicker = getDatePicker();
        datePicker.setBackground(new ColorDrawable(Color.TRANSPARENT));
        datePicker.setForegroundGravity(Gravity.CENTER);
    }
}
