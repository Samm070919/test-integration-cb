package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pagatodoholdings.posandroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditTextPassword extends RelativeLayout implements View.OnClickListener {
    final EditText etContenido;
    final CheckBox showPassword;
    boolean tieneAccionIme;
    boolean sePresionoDelete;
    int maximoCaracteres = 6;
    List<TextView> digitos = new ArrayList<>();

    public EditTextPassword(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.edit_text_password, this);
        etContenido = findViewById(R.id.edit_contenido);
        showPassword = findViewById(R.id.iv_icono_visible);
        for (int x = 1; x <= getMaximoCaracteres(); x++){
            int res = getResources().getIdentifier("txt_digit_" + x, "id", context.getPackageName());
            digitos.add(findViewById(res));

        }


        init(context.obtainStyledAttributes(attrs, R.styleable.EditTextPassword));

        int maxLength = maximoCaracteres;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        etContenido.setFilters(fArray);

        findViewById(R.id.layout_root).requestFocus();
        etContenido.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etContenido.setCursorVisible(false);
        etContenido.setOnClickListener(this);
        etContenido.setOnFocusChangeListener((View view, boolean b)->{
                if (b){
                    findViewById(R.id.text_hint).setVisibility(GONE);
                    findViewById(R.id.layout_asteriscos).setVisibility(VISIBLE);
                }else{
                    if (etContenido.getText().length() == 0) {
                        findViewById(R.id.text_hint).setVisibility(VISIBLE);
                        findViewById(R.id.layout_asteriscos).setVisibility(INVISIBLE);
                    }
                }
        });

        etContenido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start, final int count, final int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start, final int before, final int count) {

                if (before >= count) {
                    sePresionoDelete = true;
                }
            }

            @Override
            public void afterTextChanged(final Editable editable) {
                //Nothing
                if (!sePresionoDelete && etContenido.length() <= maximoCaracteres) {
                    agregaCaracter();
                } else if(sePresionoDelete){
                    remueveCaracter();
                }
                sePresionoDelete = false;
            }
        });

        etContenido.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(final ActionMode actionMode) {
                //Vacio necesario para prevenir copiado pointY pegado
            }
        });
        initListenerShowPassword();
    }

    private void init(final TypedArray atributos) {
        if (atributos.hasValue(R.styleable.EditTextPassword_hint_psd)) {
            ((TextView)findViewById(R.id.text_hint)).setText(atributos.getString(R.styleable.EditTextPassword_hint_psd));
        }
        if (atributos.hasValue(R.styleable.EditTextPassword_ime_psd)) {
            if (Objects.equals(atributos.getString(R.styleable.EditTextPassword_ime_psd), "actionDone")) {
                etContenido.setImeOptions(EditorInfo.IME_ACTION_DONE);
                tieneAccionIme = true;
            } else {
                tieneAccionIme = false;
            }
        }
        if (atributos.hasValue(R.styleable.EditTextPassword_maximoCaracteres)) {
            maximoCaracteres = atributos.getInt(R.styleable.EditTextPassword_maximoCaracteres, 6);
        }
    }

    private void initListenerShowPassword() {
        showPassword.setOnCheckedChangeListener((CompoundButton compoundButton, boolean isChecked)->{
                String input = etContenido.getText().toString();
                if (input.length() > 0){
                    if (isChecked){
                        for (int x = 0; x < input.length(); x++){
                            digitos.get(x).setText(String.valueOf(input.charAt(x)));
                        }
                    }else{
                        for (int x = 0; x < input.length(); x++){
                            digitos.get(x).setText("*");
                        }
                    }
                }
        });
    }

    public void estableceAccionIme(final TextView.OnEditorActionListener editorListener) {
        if (tieneAccionIme) {
            etContenido.setOnEditorActionListener(editorListener);
        }
    }

    private void agregaCaracter() {
        final int numeroCaracteres = etContenido.getText().length();
        if (numeroCaracteres > 0 && numeroCaracteres <= maximoCaracteres) {
            if (showPassword.isChecked()){
                int posicion = numeroCaracteres - 1;
                digitos.get(posicion).setText(String.valueOf(etContenido.getText().toString().charAt(posicion)));
            }else {
                digitos.get(numeroCaracteres - 1).setText("*");
            }
        }
    }

    private void remueveCaracter() {
        if (getTexto().length() < getMaximoCaracteres()) {
            digitos.get(getTexto().length()).setText("");
        }
    }

    public String getOnlySubSecuentText() {
        return etContenido.getText().subSequence(0,getMaximoCaracteres()).toString();
    }

    public String getOnlySubSecuentTextCVV() {
        return etContenido.getText().toString();
    }

    public String getTexto() {
        return etContenido.getText().toString();
    }

    public void setTexto(String text){
        etContenido.setText(text);
        findViewById(R.id.layout_asteriscos).setVisibility(VISIBLE);
        int tam = etContenido.getText().toString().length();
        for (int x = 0; x < tam; x++){
            if (showPassword.isChecked()){
                digitos.get(x).setText(String.valueOf(etContenido.getText().toString().charAt(x)));
            }else {
                digitos.get(x).setText("*");
            }
        }

    }

    public boolean validate() {
        final CharSequence textoValidar = etContenido.getText();
        CharSequence newTextValidar = "";
        if (textoValidar.toString().startsWith(" ")) {
            newTextValidar = textoValidar.toString().trim();
        } else {
            newTextValidar = textoValidar;
        }

        final boolean isValid = !TextUtils.isEmpty(newTextValidar);
        if (!isValid) {
            etContenido.setError(etContenido.getContext().getString(R.string.campo_requerido));
            etContenido.requestFocus();
        }
        return isValid;
    }

    public String getText(){
        return etContenido.getText().toString();
    }

    public boolean validatePassEquals(EditText editText){
        final CharSequence textValidar = etContenido.getText();
        final boolean isValid = !TextUtils.isEmpty(textValidar) && String.valueOf(textValidar).contentEquals(editText.getText());
        if (!isValid) {
            etContenido.setError(TextUtils.isEmpty(textValidar) ? etContenido.getContext().getString(R.string.campo_requerido) : etContenido.getContext().getString(R.string.contrasen_a_no_coinciden));
            etContenido.requestFocus();
        }
        return isValid;
    }

    public EditText obtenEtCampo() {
        return etContenido;
    }

    @Override
    public void onClick(View v) {
        etContenido.setSelection(etContenido.length());
    }

    public int getMaximoCaracteres() {
        return maximoCaracteres;
    }

    public void setMaximoCaracteres(int maximoCaracteres) {
        this.maximoCaracteres = maximoCaracteres;
    }
}
