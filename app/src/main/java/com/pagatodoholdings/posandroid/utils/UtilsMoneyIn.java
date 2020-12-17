package com.pagatodoholdings.posandroid.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.money_in.models.BankDetailExtraField;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UtilsMoneyIn {

    private UtilsMoneyIn(){
        //Not Implemented
    }

    public static String obtenerUrlPais (final String pais) {
        String baseUrl = "";
        switch (pais) {
            case "032": //ARGENTINA
                baseUrl =  "wswalletar/";
                break;
            case "170": //COLOMBIA
                baseUrl =  "wswalletco/";
                break;
            case "152": //CHILE
                baseUrl =  "wswalletcl/";
                break;
            case "218": //ECUADOR
                baseUrl =  "wswalletec/";
                break;
            case "484": //MEXICO
                baseUrl =  "wswalletmx/";
                break;
            case "604": //PERU
                baseUrl =  "wswalletpe/";

                break;
            default:
                break;
        }
        return MposApplication.getInstance().getDatosLogin().getPais().getUrlwalletpublica() + baseUrl;
    }

    /*
   Obtener Montos de botones personalizables Menú Rellena tu Saldo dependiendo del país
   */
    public static String[] getMontosPersonalizadosPais (final String pais) {
        String[] montos;

        switch (pais) {
            case "032": //ARGENTINA
                montos = new String[] {"5000", "7500", "10000", "15000"};
                break;
            case "170": //COLOMBIA
                montos = new String[] {"200000", "300000", "500000", "1000000"};
                break;
            case "152": //CHILE
                montos = new String[] {"20000", "40000", "60000", "100000"};
                break;
            case "218": //ECUADOR
                montos = new String[] {"50", "100", "200", "300"};
                break;
            case "484": //MEXICO
                montos = new String[] {"50", "1500", "2000", "3000"};
                break;
            case "604": //PERU
                montos = new String[] {"300", "500", "1000", "2000"};
                break;
            default:
                montos = new String[]{};
                break;
        }
        return montos;
    }

    /*
    Obtener campos extra Detalle Banco dependiendo del país
    */
    public static List<BankDetailExtraField> getCamposExtraDetalleBancoPais (final String pais) {
        List<BankDetailExtraField> campos = new ArrayList();
        switch (pais) {
            case "032": //ARGENTINA
                campos = new ArrayList();
                break;
            case "170": //COLOMBIA
                campos = new ArrayList();
                break;
            case "152": //CHILE
                campos = new ArrayList();
                break;
            case "218": //ECUADOR
                campos = new ArrayList();
                break;
            case "484": //MEXICO
                campos = new ArrayList();
                break;
            case "604": //PERU
                campos.add( new BankDetailExtraField("Código:", "codigo", "") );
                break;
            default:
                campos = new ArrayList();
                break;
        }
        return campos;
    }

    /*
    Obtener texto Titulo de instrucciones Detalle Banco dependiendo el país
    */
    public static String getTituloInstruccionesDetalleBancoPais (final String pais) {
        String titulo;
        switch (pais) {
            case "032": //ARGENTINA
                titulo = "Sigue Estos Pasos para Realizar tu Depósito:";
                break;
            case "170": //COLOMBIA
                titulo = "Sigue Estos Pasos para Realizar tu Depósito:";
                break;
            case "152": //CHILE
                titulo = "Sigue Estos Pasos para Realizar tu Depósito:";
                break;
            case "218": //ECUADOR
                titulo = "Sigue Estos Pasos para Realizar tu Depósito:";
                break;
            case "484": //MEXICO
                titulo = "Sigue Estos Pasos para Realizar tu Depósito:";
                break;
            case "604": //PERU
                titulo = "Sigue Estos Pasos para Realizar tu Depósito:";
                break;
            default:
                titulo = "";
                break;
        }
        return titulo;
    }

    /*
    Obtener texto instrucciones Detalle Banco dependiendo el país
    */
    public static String getInstruccionesDetalleBancoPais (final String pais, String codRecaudo) {
        String instrucciones;
        switch (pais) {
            case "032": //ARGENTINA
                instrucciones = "";
                break;
            case "170": //COLOMBIA
                instrucciones = "1.- Menciona que Quieres Abonar a la Cuenta Fullcarga\n" +
                        "2.- Muestra el Código de Barras Anexo\n" +
                        "3.- Menciona al Cajero el Monto a Depositar";
                break;
            case "152": //CHILE
                instrucciones = "";
                break;
            case "218": //ECUADOR
                instrucciones = "";
                break;
            case "484": //MEXICO
                instrucciones = "";
                break;
            case "604": //PERU
                instrucciones = "\nDepósitos en Agentes\n\n" +
                        "1.- Ingresa Código de Recaudo " + codRecaudo + "\n" +
                        "2.- Ingresa tu Código de Cliente\n\n" +
                        "Depósitos en Sitio Web ó App Bancaria\n\n" +
                        "1.- Ingresa en la Web/App del Banco\n" +
                        "2.- Ingresa en la Sección Pagos / Servicios\n" +
                        "3.- Selecciona \" Ya Ganaste \"\n" +
                        "4.- Ingresa tu Código de Cliente\n" +
                        "5.- Ingresa el Monto a Depositar";
                break;
            default:
                instrucciones = "";
                break;
        }
        return instrucciones;
    }

    /*
    Determinar si se obtiene Código de Barras dependiendo el país
    */
    public static SpannableString getEstiloInstruccionesPais (final String pais, final String texto) {
        SpannableString spannable = new SpannableString(texto);
        String text2Span  = "";
        switch (pais) {
            case "032": //ARGENTINA
                break;
            case "170": //COLOMBIA
//                text2Span = " a la Cuenta";
//                spannable.setSpan(
//                        new ForegroundColorSpan(MposApplication.getInstance().getApplicationContext().getResources().getColor(R.color.colorPrimary)),
//                        texto.indexOf(text2Span) + 1,
//                        texto.indexOf(text2Span) + 1 + String.valueOf(text2Span).trim().length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                spannable.setSpan(
//                        new StyleSpan(Typeface.ITALIC),
//                        texto.indexOf(text2Span) + 1,
//                        texto.indexOf(text2Span) + 1 + String.valueOf(text2Span).trim().length(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case "152": //CHILE
                break;
            case "218": //ECUADOR
                break;
            case "484": //MEXICO
                break;
            case "604": //PERU
                text2Span = "\nDepósitos en Agentes";
                spannable.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        texto.indexOf(text2Span) + 1,
                        texto.indexOf(text2Span) + 1 + String.valueOf(text2Span).trim().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                text2Span = "\nDepósitos en Sitio Web ó App Bancaria";
                spannable.setSpan(
                        new StyleSpan(Typeface.BOLD),
                        texto.indexOf(text2Span) + 1,
                        texto.indexOf(text2Span) + 1 + String.valueOf(text2Span).trim().length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                break;
        }
        return spannable;
    }

    /*
    Determinar si se obtiene Código de Barras dependiendo el país
    */
    public static Boolean debeObtenerCodeBarPais (final String pais) {
        Boolean debe = false;
        switch (pais) {
            case "032": //ARGENTINA
                debe =  false;
                break;
            case "170": //COLOMBIA
                debe =  true;
                break;
            case "152": //CHILE
                debe =  false;
                break;
            case "218": //ECUADOR
                debe =  false;
                break;
            case "484": //MEXICO
                debe =  false;
                break;
            case "604": //PERU
                debe =  false;
                break;
            default:
                debe =  false;
                break;
        }
        return debe;
    }

    /*
    Obtener texto Código de Cliente dependiendo el país
    */
    public static String getTextoCodigoClientePais (final String pais) {
        String texto;
        switch (pais) {
            case "032": //ARGENTINA
                texto = "";
                break;
            case "170": //COLOMBIA
                texto = "Código de Cliente";
                break;
            case "152": //CHILE
                texto = "";
                break;
            case "218": //ECUADOR
                texto = "";
                break;
            case "484": //MEXICO
                texto = "";
                break;
            case "604": //PERU
                texto = "Código de Cliente";
                break;
            default:
                texto = "";
                break;
        }
        return texto;
    }

    /*
   Determinar tamaño de texto botones de monto personalizados dependiendo el país
   */
    public static float getImportesTextSizePais (final String pais) {
        float textSize = 0;
        switch (pais) {
            case "032": //ARGENTINA
                textSize = MposApplication.getInstance().getApplicationContext().getResources().getDimension(R.dimen.moneyin_dimen_txt_amount_ar) ;
                break;
            case "170": //COLOMBIA
                textSize = MposApplication.getInstance().getApplicationContext().getResources().getDimension(R.dimen.moneyin_dimen_txt_amount_co) ;
                break;
            case "152": //CHILE
                textSize = MposApplication.getInstance().getApplicationContext().getResources().getDimension(R.dimen.moneyin_dimen_txt_amount_cl) ;
                break;
            case "218": //ECUADOR
                textSize = MposApplication.getInstance().getApplicationContext().getResources().getDimension(R.dimen.moneyin_dimen_txt_amount_ec) ;
                break;
            case "484": //MEXICO
                textSize = MposApplication.getInstance().getApplicationContext().getResources().getDimension(R.dimen.moneyin_dimen_txt_amount_mx) ;
                break;
            case "604": //PERU
                textSize = MposApplication.getInstance().getApplicationContext().getResources().getDimension(R.dimen.moneyin_dimen_txt_amount_pe) ;
                break;
            default:
                textSize =  14;
                break;
        }
        return textSize;
    }

    /*
    * Obtener el Bitmap para X formato de Código de Barras
    */
    public static Bitmap encodeAsBitmap(final String contents, final BarcodeFormat format) {
        if (contents.isEmpty() || format == null) {
            return null;
        }

        try {
            Map<EncodeHintType, Object> hints = null;
            hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 0);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            final MultiFormatWriter writer = new MultiFormatWriter();
            final BitMatrix bitMatrix = writer.encode(contents, format, 500, 100, hints);
            final Bitmap bitmap = Bitmap.createBitmap(bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bitMatrix.getWidth(); i++) {
                for (int j = 0; j < bitMatrix.getHeight(); j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            Logger.getLogger(e.getMessage());
            return null;
        }
    }
}
