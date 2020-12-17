package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.general.models.CurrencyImport;

import java.util.Objects;

public class MontoViewCustom extends FrameLayout {

    private static final int FONT_NORMAL = 0;
    private static final int FONT_MEDIUM = 1;
    private static final int FONT_LARGE = 2;

    private EditText editText;
    private MontoViewController montoView;
    private TextView tvTitulo;
    private String titulo;
    private LinearLayout lineaInferior;
    private int maxLenght;
    private boolean tieneAccionIme;
    private ConstraintLayout cl_contenedor;

    public MontoViewCustom(@NonNull Context context) {
        super(context);
        initUI(context, null);
    }

    public MontoViewCustom(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(context, attrs);
    }

    public MontoViewCustom(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context, attrs);
    }

    public MontoViewCustom(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI(context, attrs);
    }

    public EditText getEditText() {
        return editText;
    }

    public String getMontoFromEditText(){
        String monto = editText.getText().toString();
        int posDecimales = Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getNumDecimales();
        if(monto.length() > posDecimales) {
            return addPoint(monto, '.', monto.length() - posDecimales);
        }
        return "0";
    }

    public String addPoint(String str, char ch, int position) {
        StringBuilder sb = new StringBuilder(str);
        sb.insert(position, ch);
        return sb.toString();
    }

    public void setMonto(String monto){
        montoView.setMonto(monto);
        editText.setText(monto);
    }

    public void setMonto(CurrencyImport monto){
        montoView.setMonto(monto);
        editText.setText(monto.getImporte() + monto.getCentavos());
    }

    private void initUI(Context context, AttributeSet attrs) {
        inflate(context, R.layout.monto_view_custom, this);

        editText = findViewById(R.id.inner_ed);
        montoView = findViewById(R.id.monto_importe);
        tvTitulo = findViewById(R.id.text_input);
        lineaInferior = findViewById(R.id.linea_inferior);
        cl_contenedor = findViewById(R.id.cl_contenedor);

        if (attrs != null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.MontoViewCustom);
            String textBelow = ta.getString(R.styleable.MontoViewCustom_textoInferior);
            int color = ta.getColor(R.styleable.MontoViewCustom_android_textColor, ContextCompat.getColor(getContext(),R.color.coloproducttext));
            maxLenght = ta.getInt(R.styleable.MontoViewCustom_maxLenght, 9);
            setFotSize(ta.getInt(R.styleable.MontoViewCustom_fontSize, 0));
            montoView.setColorMonto(color);
            setTitulo(textBelow);
            if (ta.hasValue(R.styleable.MontoViewCustom_imeMvcc)) {
                if (Objects.equals(ta.getString(R.styleable.MontoViewCustom_imeMvcc), "actionNext")) {
                    editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    tieneAccionIme = true;
                } else {
                    tieneAccionIme = false;
                }
            }
            ta.recycle();
        }
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLenght)});
        editText.setCursorVisible(false);
        getScreenSize(context);



        editText.setOnFocusChangeListener((v, hasFocus) -> onfocusChange(hasFocus));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //NOT IMPLEMENTED
            }

            @Override
            public void afterTextChanged(Editable s) {
                montoView.setMonto(s.toString());
            }
        });
    }

    private void onfocusChange(boolean hasFocus) {
        lineaInferior.setBackgroundColor(hasFocus ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : ContextCompat.getColor(getContext(), R.color.colorGrey));
        tvTitulo.setText(titulo);
        tvTitulo.setTextColor(hasFocus ? ContextCompat.getColor(getContext(), R.color.colorPrimary) : ContextCompat.getColor(getContext(), R.color.coloproducttext));
    }

    private void setFotSize(int anInt) {
        switch (anInt){
            case FONT_NORMAL:
                montoView.setNewTextSizeIndividualMonto(12f, 18f, 12f);
                break;

            case FONT_MEDIUM:
                montoView.setNewTextSizeIndividualMonto(15f, 34f, 17f);
                break;

            case FONT_LARGE:
                montoView.setNewTextSizeIndividualMonto(18f, 50f, 22f);
                break;

            default:
                break;

        }
    }

    public void setTextColor(@ColorInt int color){
        montoView.setColorMonto(color);
    }

    public void setMaxLenght(final int lenght) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(lenght)});
    }

    public int getMaxLenght() {
        return maxLenght;
    }

    public void estableceAccionIme(final TextView.OnEditorActionListener editorListener) {
        if (tieneAccionIme) {
            editText.setOnEditorActionListener(editorListener);
        }
    }

    public void setTitulo(final String s) {
        titulo = s;
        tvTitulo.setText(s);
    }

    private String getScreenSize(Context context) {
        int screenLayout = context.getResources().getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
//                montoView.setNewTextSizeIndividualMonto(18f, 40f, 22f);
                montoView.setNewTextSizeIndividualMonto(12f, 18f, 12f);
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
//                montoView.setNewTextSizeIndividualMonto(18f, 50f, 22f);
                montoView.setNewTextSizeIndividualMonto(15f, 34f, 17f);
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                montoView.setNewTextSizeIndividualMonto(18f, 50f, 22f);
                return "large";
            case 4: // Configuration.SCREENLAYOUT_SIZE_XLARGE is API >= 9
                montoView.setNewTextSizeIndividualMonto(18f, 50f, 22f);
                return "xlarge";
            default:
                return "undefined";
        }
    }

    public void setError(String mensaje) {
        lineaInferior.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSoftRed));
        tvTitulo.setText(mensaje);
        tvTitulo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSoftRed));
    }

    public void setEdittextEditable(final Boolean boleanEditable){
        editText.setClickable(boleanEditable);
        editText.setFocusableInTouchMode(boleanEditable);
    }

    public ConstraintLayout getClContenedor(){
        return cl_contenedor;
    }
}