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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import java.util.ArrayList;
import java.util.List;

public class EditTextSeparateChar extends RelativeLayout implements View.OnClickListener {
    final EditText etContenidoChar;
    final CheckBox checkShowPassword;
    boolean sePresionoDelete;
    boolean esContrasena=false;
    boolean isEnabledChar=true;
    int maximoCaracteres = 6;
    List<TextView> digitos = new ArrayList<>();

    public EditTextSeparateChar(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.edit_text_separate_char, this);
        etContenidoChar = findViewById(R.id.edit_contenido_char);
        checkShowPassword = findViewById(R.id.iv_icono_visible_char);
        if (attrs != null) {
            esContrasena = attrs.getAttributeBooleanValue(R.styleable.EditTextSeparateChar_iscontrasenach, false);
            isEnabledChar = attrs.getAttributeBooleanValue(R.styleable.EditTextSeparateChar_is_enabled_char, false);
        }
        for (int x = 1; x <= 6; x++){
            int res = getResources().getIdentifier("txt_digit_" + x+ "_char", "id", context.getPackageName());
            digitos.add((TextView) findViewById(res));

        }

            findViewById(R.id.layout_root_char).requestFocus();
            etContenidoChar.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            etContenidoChar.setCursorVisible(false);
            etContenidoChar.setOnClickListener(this);
            etContenidoChar.setOnFocusChangeListener((View view, boolean b)->defineOnFocusChange(b));

            etContenidoChar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence charSequence, final int start, final int count, final int after) {
                    //Nothing
                }

                @Override
                public void onTextChanged(final CharSequence charSequence, final int start, final int before, final int count) {
                    defineOnTextChanged(before,count);
                }

                @Override
                public void afterTextChanged(final Editable editable) {
                    //Nothing
                    if(!isEnabledChar)
                    {
                        etContenidoChar.setEnabled(isEnabledChar);
                        return;
                    }
                        if (!sePresionoDelete) {
                            agregaCaracter();
                        } else {
                            remueveCaracter();
                        }
                        sePresionoDelete = false;
                    }
            });

            etContenidoChar.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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

        init(context.obtainStyledAttributes(attrs, R.styleable.EditTextSeparateChar));
        initListenerShowPassword();
    }

    private void defineOnFocusChange(boolean b){
        if(!isEnabledChar)
        {
            etContenidoChar.setEnabled(isEnabledChar);
            return;
        }

        if (b) {
            findViewById(R.id.text_hint_char).setVisibility(GONE);
            findViewById(R.id.layout_asteriscos_char).setVisibility(VISIBLE);
        } else {
            if (etContenidoChar.getText().length() == 0) {
                findViewById(R.id.text_hint_char).setVisibility(VISIBLE);
                findViewById(R.id.layout_asteriscos_char).setVisibility(INVISIBLE);
            }
        }
    }

    private void defineOnTextChanged(final int before, final int count){
        if(!isEnabledChar)
        {
            etContenidoChar.setEnabled(isEnabledChar);
            return;
        }
        if (before >= count) {
            sePresionoDelete = true;
        }
    }

    private void init(final TypedArray atributos) {
        if (atributos.hasValue(R.styleable.EditTextSeparateChar_hint_char)) {
            ((TextView)findViewById(R.id.text_hint_char)).setText(atributos.getString(R.styleable.EditTextSeparateChar_hint_char));
        }
    }


    public void setEnabledChar(boolean enabledChar) {
        isEnabledChar = enabledChar;
    }

    private void initListenerShowPassword() {
        checkShowPassword.setOnCheckedChangeListener((CompoundButton compoundButton, boolean isChecked)->{
                String input = etContenidoChar.getText().toString();
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


    private void agregaCaracter() {
        final int numeroCaracteres = etContenidoChar.getText().length();
        if (numeroCaracteres > 0 && numeroCaracteres <= maximoCaracteres) {
                int posicion = numeroCaracteres - 1;
                digitos.get(posicion).setText(String.valueOf(etContenidoChar.getText().toString().charAt(posicion)));
        }
    }

    private void remueveCaracter() {
        if (getTexto().length() < maximoCaracteres) {
            digitos.get(getTexto().length()).setText("");
        }
    }

    public String getTexto() {
        return etContenidoChar.getText().toString();
    }

    private void removeAllCharacter(){
        for(int i=0;i<maximoCaracteres;i++)
        {
            digitos.get(i).setText("");
        }
    }

    public void setTexto(String text){
        etContenidoChar.setText(text);
        etContenidoChar.setEnabled(isEnabledChar);
        if(text.length()>0)
        {
            findViewById(R.id.text_hint_char).setVisibility(GONE);
            findViewById(R.id.layout_asteriscos_char).setVisibility(VISIBLE);
        }else{
            removeAllCharacter();
            findViewById(R.id.text_hint_char).setVisibility(VISIBLE);
            findViewById(R.id.layout_asteriscos_char).setVisibility(INVISIBLE);
        }

        int tam = etContenidoChar.getText().toString().length();
        for (int x = 0; x < tam; x++){
                digitos.get(x).setText(String.valueOf(etContenidoChar.getText().toString().charAt(x)));
        }

    }

    public boolean validate() {
        final CharSequence textoValidar = etContenidoChar.getText();
        CharSequence newTextValidar = "";
        if (textoValidar.toString().startsWith(" ")) {
            newTextValidar = textoValidar.toString().trim();
        } else {
            newTextValidar = textoValidar;
        }

        final boolean isValid = !TextUtils.isEmpty(newTextValidar);
        if (!isValid) {
            etContenidoChar.setError(etContenidoChar.getContext().getString(R.string.campo_requerido));
            etContenidoChar.requestFocus();
        }
        return isValid;
    }

    public boolean validatePassEquals(EditText editText){
        final CharSequence textValidar = etContenidoChar.getText();
        final boolean isValid = !TextUtils.isEmpty(textValidar) && String.valueOf(textValidar).contentEquals(editText.getText());
        if (!isValid) {
            etContenidoChar.setError(TextUtils.isEmpty(textValidar) ? etContenidoChar.getContext().getString(R.string.campo_requerido) : etContenidoChar.getContext().getString(R.string.contrasen_a_no_coinciden));
            etContenidoChar.requestFocus();
        }
        return isValid;
    }

    public EditText obtenEtCampo() {
        return etContenidoChar;
    }

    @Override
    public void onClick(View v) {
        etContenidoChar.setSelection(etContenidoChar.length());
    }
}
