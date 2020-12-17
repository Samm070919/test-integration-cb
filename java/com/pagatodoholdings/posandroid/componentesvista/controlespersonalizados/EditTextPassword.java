package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditTextPassword extends RelativeLayout {
    final EditText etContenido;
    final CheckBox showPassword;
    boolean sePresionoDelete;
    int maximoCaracteres = 6;
    private String hint;
    List<TextView> digitos = new ArrayList<>();

    public EditTextPassword(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.edit_text_password, this);
        etContenido = findViewById(R.id.edit_contenido);
        showPassword = findViewById(R.id.iv_icono_visible);
        for (int x = 1; x <= 6; x++){
            int res = getResources().getIdentifier("txt_digit_" + x, "id", context.getPackageName());
            digitos.add((TextView) findViewById(res));

        }

        findViewById(R.id.layout_root).requestFocus();
        etContenido.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etContenido.setCursorVisible(false);
        etContenido.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    etContenido.setHint("");
                    findViewById(R.id.layout_asteriscos).setVisibility(VISIBLE);
                }else{
                    if (etContenido.getText().length() == 0) {
                        if (hint != null && hint.length() > 0)
                            etContenido.setHint(hint);
                        findViewById(R.id.layout_asteriscos).setVisibility(INVISIBLE);
                    }
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
                if (!sePresionoDelete) {
                    agregaCaracter();
                } else {
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
        init(context.obtainStyledAttributes(attrs, R.styleable.EditTextPassword));
        initListenerShowPassword();
    }

    private void init(final TypedArray atributos) {
        if (atributos.hasValue(R.styleable.EditTextPassword_hint_psd)) {
            etContenido.setHint(atributos.getString(R.styleable.EditTextPassword_hint_psd));
        }
        hint = etContenido.getHint().toString();
    }

    private void initListenerShowPassword() {
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
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
            }
        });
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
        if (getTexto().length() < maximoCaracteres) {
            digitos.get(getTexto().length()).setText("");
        }
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
}
