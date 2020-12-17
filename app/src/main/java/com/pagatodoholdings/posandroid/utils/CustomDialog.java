package com.pagatodoholdings.posandroid.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;

import com.itextpdf.text.Image;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;

import java.util.Objects;

public class CustomDialog {

    public static void showDialog(Context context, int drawable, String btnAcept, String btnCancel, String titulo, String content, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View view = layoutInflater.inflate(R.layout.dialog_custom_image, null);
        alert.setCancelable(true);
        final ImageView image = view.findViewById(R.id.image_custom);
        final BotonClickUnico btnAccept = view.findViewById(R.id.btn_aceptar_custom);
        final BotonClickUnico btnCancelar = view.findViewById(R.id.btn_cancelar_custom);
        final TextView tvTitle = view.findViewById(R.id.tv_titulo_custom);
        final TextView tvContent = view.findViewById(R.id.tv_contenido_custom);
        tvTitle.setText(titulo);
        tvContent.setText(content);
        image.setImageDrawable(context.getResources().getDrawable(drawable));
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;


        btnAccept.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });

        btnCancelar.setOnClickListener(view1 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }
}
