package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.SigmaUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.general.models.CurrencyImport;

import java.math.BigDecimal;
import java.text.NumberFormat;


public class MontoViewController extends FrameLayout {
    private static final String TAG = MontoViewController.class.getSimpleName();

    String monto;
    String inferior;
    int colorText;
    boolean line, showLowText;
    TextView simboloMonedaLeft;
    TextView simboloMonedaRight;
    TextView simboloMoneda;
    TextView montoEntero;
    TextView montoCentavos;
    TextView txtInferior;
    ConstraintLayout clContenedor;
    View lineOverMonto;

    private static final int FONT_NORMAL = 0;
    private static final int FONT_MEDIUM = 1;
    private static final int FONT_LARGE = 2;
    private static final int FONT_MINIMUN = 3;


    private NumberFormat dbnumberformat;

    public MontoViewController(final Context context, final String Monto, final String underTitle, final int colortext) {
        super(context);
        monto = Monto;
        inferior = underTitle;
        colorText = colortext;
        initUI(context, null);
    }

    public MontoViewController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    public MontoViewController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    public MontoViewController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI(context, attrs);
    }

    private void initUI(final Context context, @Nullable AttributeSet attributeSet) {
        inflate(context, R.layout.monto_view, this);
        dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener numberFormat"));
        simboloMonedaRight = findViewById(R.id.Symbolo_moneda_right);
        simboloMonedaLeft = findViewById(R.id.Symbolo_moneda_left);
        montoEntero = findViewById(R.id.txt_monto_entento);
        montoCentavos = findViewById(R.id.txt_Cents);
        txtInferior = findViewById(R.id.Text_Title);
        clContenedor = findViewById(R.id.cl_contenedor);
        lineOverMonto = findViewById(R.id.vLineOverMonto);
        final String parametroFijo = SigmaBdManager.getParametroFijo("0024", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametroFijo"));
        final String parametroMoneda = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametroMoneda"));
        final String posicionMoneda  = SigmaBdManager.getParametroFijo("0022",new OnFailureListener.BasicOnFailureListener(TAG,"Error al obtener posicionMoneda"));

        simboloMoneda = selectPosicionMoneda(posicionMoneda);


       if(dbnumberformat.getMaximumFractionDigits() == 0 ){
           montoCentavos.setVisibility(GONE);
       }


        if (attributeSet == null) {
            defineAttr(parametroMoneda,parametroFijo);
        } else {
            defineStyleComponents(attributeSet,parametroMoneda,parametroFijo);
        }
    }

    private void defineStyleComponents(AttributeSet attributeSet, String parametroMoneda, String parametroFijo){
        TypedArray ta = getContext().obtainStyledAttributes(attributeSet, R.styleable.MontoViewController);
        colorText = ta.getColor(R.styleable.MontoViewController_colorTexto, ContextCompat.getColor(getContext(),R.color.coloproducttext2));
        inferior = ta.getString(R.styleable.MontoViewController_TextoInferior);
        line = ta.getBoolean(R.styleable.MontoViewController_showLineOver, false);
        showLowText = ta.getBoolean(R.styleable.MontoViewController_mostrarTextoInferior, true);

        simboloMoneda.setTextColor(colorText);

        montoEntero.setTextColor(colorText);

        montoCentavos.setTextColor(colorText);

        lineOverMonto.setVisibility(line? VISIBLE: GONE);

        txtInferior.setVisibility(showLowText ? VISIBLE : GONE);


        setFotSize(ta.getInt(R.styleable.MontoViewController_size, 0));

        monto = ta.getString(R.styleable.MontoViewController_Importe);
        txtInferior.setText(inferior);
        if (monto != null) {
            final BigDecimal importe = SigmaUtil.cleanImporteInput(monto, dbnumberformat);
            final String formatted = dbnumberformat.format(importe);
            final String monto_decimal = formatted.indexOf(parametroFijo) != -1 ? formatted.substring(formatted.indexOf(parametroFijo)) : "00";
            montoCentavos.setText(monto_decimal);
            montoEntero.setText(formatted.indexOf(parametroFijo)!=-1?formatted.replace(monto_decimal, ""):formatted);
            simboloMoneda.setText(!parametroMoneda.trim().equals("") ? parametroMoneda : "S/");
            txtInferior.setText(ta.getString(R.styleable.MontoViewController_TextoInferior));
        }
        if (!ta.getBoolean(R.styleable.MontoViewController_mostrarTextoInferior, true)) {
            ocultarInferior();
        }
    }

    private void defineAttr(String parametroMoneda, String parametroFijo){
        simboloMoneda.setTextColor(colorText);

        montoEntero.setTextColor(colorText);

        montoCentavos.setTextColor(colorText);

        final BigDecimal importe = SigmaUtil.cleanImporteInput(monto, dbnumberformat);
        final String formatted = dbnumberformat.format(importe);
        final String monto_decimal = formatted.substring(formatted.indexOf(parametroFijo));
        montoCentavos.setText(monto_decimal);
        simboloMoneda.setText(!parametroMoneda.trim().equals("") ? parametroMoneda : "S/");
        montoEntero.setText(formatted.indexOf(parametroFijo)!=-1?formatted.replace(monto_decimal, ""):formatted);
        txtInferior.setText(inferior);
    }

    private TextView selectPosicionMoneda(final String posicionMoneda ) {
        if("0".equals(posicionMoneda)){
            simboloMonedaRight.setVisibility(GONE);
            simboloMonedaLeft.setVisibility(VISIBLE);
            return simboloMonedaLeft;
        }else {
            simboloMonedaRight.setVisibility(VISIBLE);
            simboloMonedaLeft.setVisibility(GONE);
            return simboloMonedaRight;
        }

    }

    public void ocultarInferior() {
        findViewById(R.id.viewGraund).setVisibility(GONE);
        findViewById(R.id.Text_Title).setVisibility(GONE);
    }

    public void setMonto(String monto) {
        final String parametroFijo = SigmaBdManager.getParametroFijo("0024", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametro fijo"));
        final String parametroMoneda = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametro fijo"));

        final BigDecimal importe = SigmaUtil.cleanImporteInput(monto, dbnumberformat);
        final String formatted = dbnumberformat.format(importe);
        final String monto_decimal = formatted.indexOf(parametroFijo) != -1 ? formatted.substring(formatted.indexOf(parametroFijo)) : "00";
        final String monto_entero = formatted.indexOf(parametroFijo) != -1 ? formatted.substring(0, formatted.indexOf(parametroFijo)) : formatted;
        montoCentavos.setText(monto_decimal);
        montoEntero.setText(monto_entero);
        simboloMoneda.setText(
                !parametroMoneda.trim().equals("") ?
                        parametroMoneda :
                        Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getCurrency()
        );
    }

    public void setMonto(CurrencyImport monto) {
        montoCentavos.setText(monto.getCentavos());
        montoEntero.setText(monto.getImporte());
        simboloMoneda.setText(
                !monto.getMoneda().trim().equals("") ?
                        monto.getMoneda() :
                        Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getCurrency()
        );
    }

    public void setBoldTextMonto(final boolean bold){
        if (bold) {
            simboloMoneda.setTypeface(null, Typeface.BOLD);
            montoEntero.setTypeface(null, Typeface.BOLD);
            montoCentavos.setTypeface(null, Typeface.BOLD);
        } else {
            simboloMoneda.setTypeface(null, Typeface.NORMAL);
            montoEntero.setTypeface(null, Typeface.NORMAL);
            montoCentavos.setTypeface(null, Typeface.NORMAL);
        }

    }

    private void setFotSize(int anInt) {
        switch (anInt){

            case FONT_MINIMUN:
                this.setNewTextSizeIndividualMonto(14f, 20f, 14f);
                break;

            case FONT_NORMAL:
                this.setNewTextSizeIndividualMonto(12f, 18f, 12f);
                break;

            case FONT_MEDIUM:
                this.setNewTextSizeIndividualMonto(15f, 34f, 17f);
                break;

            case FONT_LARGE:
                this.setNewTextSizeIndividualMonto(18f, 50f, 22f);
                break;

            default:
                break;

        }
    }


    //percent 0.1 = 10%, 1.0 = 100%, 10.0 = 1000%
    public void setNewTextSizeMonto(final float percent){
        if (percent > 0.0 && percent <= 10.0){
            simboloMoneda.setTextSize(simboloMoneda.getTextSize() * percent );
            montoEntero.setTextSize(montoEntero.getTextSize() * percent );
            montoCentavos.setTextSize(montoCentavos.getTextSize() * percent );
        }
    }

    public void setNewTextSizeIndividualMonto(final float textSizeMoneda, final float textSizeEntero, final float textSizeDecimales){
            simboloMoneda.setTextSize(textSizeMoneda );
            montoEntero.setTextSize(textSizeEntero);
            montoCentavos.setTextSize(textSizeDecimales );
    }

    public void setMarginsMonto(final int topMargin, final int bottomMargin, final int leftMargin , final int rightMargin){
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) clContenedor.getLayoutParams();
        newLayoutParams.topMargin = topMargin;
        newLayoutParams.bottomMargin = bottomMargin;
        newLayoutParams.leftMargin = leftMargin;
        newLayoutParams.rightMargin = rightMargin;
        clContenedor.setLayoutParams(newLayoutParams);
    }

    public void setColorMonto(int setTextColor) {
        simboloMoneda.setTextColor(setTextColor);
        montoEntero.setTextColor(setTextColor);
        montoCentavos.setTextColor(setTextColor);
    }
}
