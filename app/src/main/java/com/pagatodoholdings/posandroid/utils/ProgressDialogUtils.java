package com.pagatodoholdings.posandroid.utils;

import android.app.Activity;
import android.app.ProgressDialog;

public final class ProgressDialogUtils {
    private static ProgressDialog progressDialog;

    private ProgressDialogUtils() {
    }

    public static void mostrarDialogoSimple(final Activity activity) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public static void establecerTitulo(final String titulo) {
        progressDialog.setMessage(titulo);
    }

    public static void establecerProgreso(final int progreso) {
        progressDialog.setProgress(progreso);
    }

    public static void ocultarProgreso() {
        progressDialog.dismiss();
    }
}
