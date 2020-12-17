package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pagatodoholdings.posandroid.R;

import net.fullcarga.android.api.formulario.Parametro;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InputNumeroServicioUnico extends RelativeLayout implements View.OnClickListener {

    private EditText etContenido;
    boolean tieneAccionIme;
    boolean sePresionoDelete;
    int maximoCaracteres = 20;
    private View view1,view2,view3,view4;
    List<TextView> digitos = new ArrayList<>();

    public InputNumeroServicioUnico(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.input_service_number, this);

        etContenido = findViewById(R.id.edit_contenido);
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);

        for (int x = 1; x <= 20; x++) {
            int res = getResources().getIdentifier("txt_digit_" + x, "id", context.getPackageName());
            digitos.add((TextView) findViewById(res));
        }

        etContenido.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        etContenido.setCursorVisible(false);
        etContenido.setOnClickListener(this);
        etContenido.setOnFocusChangeListener((View view, boolean b) -> {
            if (b) {
                //findViewById(R.id.text_hint).setVisibility(GONE);
                findViewById(R.id.layout_asteriscos).setVisibility(VISIBLE);
            } else {
                if (etContenido.getText().length() == 0) {
                    //findViewById(R.id.text_hint).setVisibility(VISIBLE);
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
    }

    private void init(final TypedArray atributos) {
        if (atributos.hasValue(R.styleable.EditTextPassword_hint_psd)) {
            ((TextView) findViewById(R.id.text_hint)).setText(atributos.getString(R.styleable.EditTextPassword_hint_psd));
        }
        if (atributos.hasValue(R.styleable.EditTextPassword_ime_psd)) {
            if (Objects.equals(atributos.getString(R.styleable.EditTextPassword_ime_psd), "actionDone")) {
                etContenido.setImeOptions(EditorInfo.IME_ACTION_DONE);
                tieneAccionIme = true;
            } else {
                tieneAccionIme = false;
            }
        }
    }


    public void estableceAccionIme(final TextView.OnEditorActionListener editorListener) {
        if (tieneAccionIme) {
            etContenido.setOnEditorActionListener(editorListener);
        }
    }

    private void agregaCaracter() {
        final int numeroCaracteres = etContenido.getText().length();
        if (numeroCaracteres > 0 && numeroCaracteres <= maximoCaracteres) {
            if(numeroCaracteres == 4)
                view1.setVisibility(VISIBLE);
            if(numeroCaracteres == 8)
                view2.setVisibility(VISIBLE);
            if(numeroCaracteres == 12)
                view3.setVisibility(VISIBLE);
            if(numeroCaracteres == 16)
                view4.setVisibility(VISIBLE);
            int posicion = numeroCaracteres - 1;
            digitos.get(posicion).setText(String.valueOf(etContenido.getText().toString().charAt(posicion)));
        }
    }

    private void remueveCaracter() {
        if(getTexto().length() == 3)
            view1.setVisibility(INVISIBLE);
        if(getTexto().length() == 7)
            view2.setVisibility(INVISIBLE);
        if(getTexto().length() == 11)
            view3.setVisibility(INVISIBLE);
        if(getTexto().length() == 15)
            view4.setVisibility(INVISIBLE);
        if (getTexto().length() < maximoCaracteres) {
            digitos.get(getTexto().length()).setText("");
        }
    }

    public String getTexto() {
        return etContenido.getText().toString();
    }

    public void setTexto(String text) {
        etContenido.setText(text);
        findViewById(R.id.layout_asteriscos).setVisibility(VISIBLE);
        int tam = etContenido.getText().toString().length();
        for (int x = 0; x < tam; x++) {
            digitos.get(x).setText(String.valueOf(etContenido.getText().toString().charAt(x)));
        }
    }

    public EditText obtenEtCampo() {
        return etContenido;
    }

    @Override
    public void onClick(View v) {
        etContenido.setSelection(etContenido.length());
    }

    public void initData(final Parametro parametro) {

    }
}
