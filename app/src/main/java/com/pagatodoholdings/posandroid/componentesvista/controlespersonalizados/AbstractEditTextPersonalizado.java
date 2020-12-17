package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.R;
import net.fullcarga.android.api.formulario.Formato;
import net.fullcarga.android.api.formulario.Parametro;
import java.util.Objects;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_TEXT_VARIATION_NORMAL;

public abstract class AbstractEditTextPersonalizado extends LinearLayout {

    protected EditText etCampo;
    protected MontoViewCustom montoView;
    protected TextView textViewHint;
    protected LinearLayout edContainer;
    protected boolean esOpcional;
    protected boolean opcional;
    protected int idVista = R.layout.campo_validado;

    protected abstract void setIdVista();

    public Parametro getParametro() {
        return parametro;
    }

    public void setParametro(final Parametro parametro) {
        this.parametro = parametro;
    }

    protected Parametro parametro;

    public AbstractEditTextPersonalizado(final Context context) {
        super(context);
    }

    public AbstractEditTextPersonalizado(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context.obtainStyledAttributes(attrs, R.styleable.EditTextDatosUsuarios));
    }

    public AbstractEditTextPersonalizado(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void init(final TypedArray atributos) {
        setIdVista();
        inflate(getContext(), idVista, this);
        ImageView ivIcono = findViewById(R.id.iv_icono_campo);

        edContainer = findViewById(R.id.et_container);
        textViewHint = findViewById(R.id.tv_hint);
        etCampo = findViewById(R.id.et_hint_campo);
        etCampo.setId(-1);
        montoView = findViewById(R.id.et_monto_view);

        etCampo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //No implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //No implementation
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (findViewById(R.id.text_hint) != null){
                    if (s.length() == 0){
                        findViewById(R.id.text_hint).setVisibility(VISIBLE);
                    }else{
                        findViewById(R.id.text_hint).setVisibility(INVISIBLE);
                    }
                }
            }
        });

        if (atributos.hasValue(R.styleable.EditTextDatosUsuarios_android_maxLength)) {
            final int maxLength = atributos.getInt(R.styleable.EditTextDatosUsuarios_android_maxLength, 8);
            etCampo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }

        if (atributos.hasValue(R.styleable.EditTextDatosUsuarios_icono)) {
            ivIcono.setBackground(getContext().getDrawable(atributos.getResourceId(R.styleable.EditTextDatosUsuarios_icono, 0)));
        }
        if (atributos.hasValue(R.styleable.EditTextDatosUsuarios_hint)) {
            ((TextView)findViewById(R.id.text_hint)).setText(atributos.getString(R.styleable.EditTextDatosUsuarios_hint));
        }

        //Definir InputType del Control
        defineInputType(atributos);

        //Definir Atributos Generales del Texto del Control
        defineAttrText(atributos);


        if (Objects.equals(atributos.getString(R.styleable.EditTextDatosUsuarios_imeOption), "actionDone")) {
            etCampo.setImeOptions(EditorInfo.IME_ACTION_DONE);
        } else if(Objects.equals(atributos.getString(R.styleable.EditTextDatosUsuarios_imeOption), "actionNext")){
            etCampo.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
    }

    private void defineInputType(TypedArray atributos) {
        if (atributos.hasValue(R.styleable.EditTextDatosUsuarios_android_inputType)) {
            int inputType = atributos.getInt(R.styleable.EditTextDatosUsuarios_android_inputType, TYPE_TEXT_VARIATION_NORMAL);

            if (inputType == TYPE_CLASS_NUMBER) {
                setEditTextAsOnlyDigits();
            } else {
                etCampo.setInputType(inputType);
            }
        }
    }

    private void defineAttrText(TypedArray atributos){
        if (atributos.getBoolean(R.styleable.EditTextDatosUsuarios_habilitado, false)) {
            etCampo.setEnabled(false);
        }

        if (atributos.hasValue(R.styleable.EditTextDatosUsuarios_tamano_letra_sp)) {
            float myTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, atributos.getInt(R.styleable.EditTextDatosUsuarios_tamano_letra_sp, 12), getContext().getResources().getDisplayMetrics());
            etCampo.setTextSize(myTextSize);
        }
        if (atributos.getBoolean(R.styleable.EditTextDatosUsuarios_centrado, false)) {
            etCampo.setGravity(Gravity.CENTER);
        } else {
            etCampo.setGravity(Gravity.LEFT);
        }
    }

    public void estableceAccionIme(final TextView.OnEditorActionListener editorListener) {
        if (editorListener != null)
            etCampo.setOnEditorActionListener(editorListener);
    }

    private void setEditTextAsOnlyDigits() {
        etCampo.setInputType(TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etCampo.setTransformationMethod(null);
    }

    public boolean estaVacio() {
        return (etCampo.getText().toString().length() == 0 && !esOpcional);
    }

    public boolean isEmpty() {
        return (montoView.getEditText().getText().toString().length() == 0 && !esOpcional);
    }

    public void establecerError(String errorDescripcion) {
        etCampo.setError(errorDescripcion);
    }

    public EditText obtenEtCampo() {
        return etCampo;
    }

    public MontoViewCustom obtenMontoView() {
        return montoView;
    }

    public TextView obtentvHint() {
        return textViewHint;
    }

    public LinearLayout obtenEdContainer() {
        return edContainer;
    }

    public EditText obtenIlCampo() {
        return etCampo;
    }

    public boolean esOpcional() {
        return esOpcional;
    }

    public void ponEsOpcional(boolean esOpcional) {
        this.esOpcional = esOpcional;
    }

    public boolean esCorreo() {
        final CharSequence textoValidar = etCampo.getText();
        return (!TextUtils.isEmpty(textoValidar) && Patterns.EMAIL_ADDRESS.matcher(textoValidar).matches());
    }

    public boolean validate() {
        final CharSequence textoValidar = etCampo.getText();
        CharSequence newTextValidar = "";
        if (textoValidar.toString().startsWith(" ")) {
            newTextValidar = textoValidar.toString().trim();
        } else {
            newTextValidar = textoValidar;
        }

        final boolean isValid = opcional || !TextUtils.isEmpty(newTextValidar);
        if (!isValid) {
            establecerError(etCampo.getContext().getString(R.string.campo_requerido));
            etCampo.requestFocus();
        }else{
            etCampo.setError(null);
        }
        return isValid;
    }

    public boolean validateEmail() {
        final CharSequence textoValidar = etCampo.getText();
        final boolean isValid = opcional || (!TextUtils.isEmpty(textoValidar) && Patterns.EMAIL_ADDRESS.matcher(textoValidar).matches());
        if (!isValid) {
            etCampo.requestFocus();
        }
        return isValid;
    }

    public boolean validatePassWord() {
        final CharSequence textoValidar = etCampo.getText();
        final boolean isValid = opcional || (!TextUtils.isEmpty(textoValidar) && textoValidar.length() == 6);
        if (!isValid) {
            establecerError(TextUtils.isEmpty(textoValidar) ? etCampo.getContext().getString(R.string.campo_requerido) : etCampo.getContext().getString(R.string.contrasen_a_valida));
            etCampo.requestFocus();
        }
        return isValid;
    }

    public boolean validatePassEquals(EditText editText) {
        final CharSequence textValidar = etCampo.getText();
        final boolean isValid = opcional || (!TextUtils.isEmpty(textValidar) && String.valueOf(textValidar).contentEquals(editText.getText()));
        if (!isValid) {
            establecerError(TextUtils.isEmpty(textValidar) ? etCampo.getContext().getString(R.string.campo_requerido) : etCampo.getContext().getString(R.string.contrasen_a_no_coinciden));
            etCampo.requestFocus();
        }
        return isValid;
    }

    public void setDataType(final Formato.Tipo dataType) {
        switch (dataType) {
            case NUMERICO:
                setEditTextAsOnlyDigits();
                break;
            case NUMERICO_CON_VALIDACION:
                etCampo.setInputType(TYPE_CLASS_NUMBER);
                break;
            case IMPORTE:
            case IMPORTE_SIN_VAL:
                etCampo.setRawInputType(TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case ALFANUMERICO:
                etCampo.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case FECHA:
                etCampo.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
                break;
            case PASSWORD:
                etCampo.setInputType(TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return false;
    }
}
