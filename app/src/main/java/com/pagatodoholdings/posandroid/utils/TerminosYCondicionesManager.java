package com.pagatodoholdings.posandroid.utils;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import com.github.pdfviewer.PDFView;
import com.pagatodoholdings.posandroid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Usa esta clase para obtener el texto que va en Términos y condiciones. Esta clase ya te provee
 * con los listeners necesarios para que el usuario haga click sin que tengas que encargarte de nada.
 * <p>
 *     <font color="red">
 *         SI SE CAMBIA EL TEXTO DE "Acepto los Términos y Condiciones y el Aviso de Privacidad"
 *         DEBES MODIFICAR EL INDEX DE DONDE SE COLOCA EL LISTENER DE LAS LETRAS CLICKEABLES {@link #S_START}
 *         y {@link #S_END}
 *     </font>*
 * </p>
 */
public final class TerminosYCondicionesManager {

    private static final int S_START = 11;
    private static final int S_END = 33;
    private static final int S_A_START = 39;
    private static final int S_A_END = 58;
    private static final String TAG = TerminosYCondicionesManager.class.getSimpleName();


    private TerminosYCondicionesManager() {
        //Clase de utilería
    }

    /**
     * En tu vista (xml) crea un text view donde quieras desplegar el texto para aceptar términos y
     * condiciones. Obtén el spannable con este método y colócaselo al text view
     * con {@link android.widget.TextView#setText(int)}. Al text view donde pongas el spannable, debes
     * de llamar {@link android.widget.TextView#setMovementMethod(MovementMethod) setMovementMethod}
     * , por default, puedes usar {@link LinkMovementMethod#getInstance()} como parámetro
     *
     * @param activity Se necesita para obtener el string de los recursos
     * @return el spannable que debes colocar al text view
     */
    public static SpannableStringBuilder createSpannableForTermsAndCond(@NonNull Activity activity) {
        String termsString = activity.getResources().getString(R.string.registro_cb_t_c_aviso);
        SpannableStringBuilder sBuilder = new SpannableStringBuilder(termsString);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showTermsAndCoditions(activity);
            }
        };
        sBuilder.setSpan(
                clickableSpan,
                S_START,
                S_A_END,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        ClickableSpan clickableSpanAviso = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showTermsAndCoditions(activity);
            }
        };





        return sBuilder;
    }

    private static void showTermsAndCoditions(final Activity activity ) {

        File file = new File(activity.getCacheDir(), "terminos_condicionesv1.pdf");
        if (!file.exists()) {
            FileOutputStream output = null;
            try (InputStream asset = activity.getAssets().open("terminos_condicionesv1.pdf")){
                output = new FileOutputStream(file);
                final byte[] buffer = new byte[1024];
                int size;
                while ((size = asset.read(buffer)) != -1) {
                    output.write(buffer, 0, size);
                }
            } catch (IOException exe) {
                Logger.LOGGER.throwing(TAG,1,exe,exe.getMessage());
            }finally {
                if(output != null){
                    try {
                        output.close();
                    } catch (IOException e) {
                        Logger.LOGGER.throwing(TAG,1, e, e.getMessage());
                    }
                }
            }

        }

        PDFView.with(activity)
                .setfilepath(file.getAbsolutePath())
                .setSwipeOrientation(0)
                .start();
    }


}
