package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pagatodoholdings.posandroid.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditTextEspecial extends ConstraintLayout {
    final EditText etContenido;

    final ViewBlink tvDigito1;
    final ViewBlink tvDigito2;
    final ViewBlink tvDigito3;
    final ViewBlink tvDigito4;
    final ViewBlink tvDigito5;
    final ViewBlink tvDigito6;
    final View separadorUltimo;
    final View separadorPenultimo;
    final View indicadorError;

    boolean sePresionoDelete;
    boolean tieneAccionIme;
    static final int NUMERO_TOTEM = 6;
    static final int NUMERO_MPOS = 4;
    int maximoCaracteres = 4;
    List<ViewBlink> digitos = new ArrayList<>();

    public EditTextEspecial(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.edit_text_especial, this);
        etContenido = findViewById(R.id.et_contenido);
        tvDigito1 = findViewById(R.id.tv_digito_1);
        tvDigito2 = findViewById(R.id.tv_digito_2);
        tvDigito3 = findViewById(R.id.tv_digito_3);
        tvDigito4 = findViewById(R.id.tv_digito_4);
        tvDigito5 = findViewById(R.id.tv_digito_5);
        tvDigito6 = findViewById(R.id.tv_digito_6);
        indicadorError = findViewById(R.id.view_line);
        separadorPenultimo = findViewById(R.id.view_separador_3x);
        separadorUltimo = findViewById(R.id.view_separador_4);
        initRefDigitos();

        etContenido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start, final int count, final int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start, final int before, final int count) {

                establecerColorError(R.color.colorWhite);

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

        digitos.get(0).anima();
        init(context.obtainStyledAttributes(attrs, R.styleable.EditTextEspecial));
    }

    private void initRefDigitos() {
        digitos.add(tvDigito1);
        digitos.add(tvDigito2);
        digitos.add(tvDigito3);
        digitos.add(tvDigito4);
        digitos.add(tvDigito5);
        digitos.add(tvDigito6);
    }

    private void agregaCaracter() {
        final int numeroCaracteres = etContenido.getText().length();
        if (numeroCaracteres > 0) {
            digitos.get(numeroCaracteres - 1).setBackgroundColor(Color.WHITE);
            digitos.get(numeroCaracteres - 1).detenAnimacion();
        }
        if (numeroCaracteres < maximoCaracteres) {
            digitos.get(numeroCaracteres).anima();
        }
    }

    private void remueveCaracter() {
        if (getTexto().length() + 1 < maximoCaracteres) {
            digitos.get(getTexto().length() + 1).detenAnimacion(Color.TRANSPARENT);
        }
        if (getTexto().length() >= 0) {
            digitos.get(getTexto().length()).anima();
        }
    }

    private void init(final TypedArray atributos) {
        final ImageView icono = findViewById(R.id.iv_icono_especial);
        final TextView hint = findViewById(R.id.tv_especial);

        if (atributos.hasValue(R.styleable.EditTextEspecial_icono_s)) {
            icono.setBackground(getContext().getDrawable(atributos.getResourceId(R.styleable.EditTextEspecial_icono_s, 0)));
        }

        if (atributos.hasValue(R.styleable.EditTextEspecial_hint_s)) {
            hint.setText(atributos.getString(R.styleable.EditTextEspecial_hint_s));
        }
        if (atributos.hasValue(R.styleable.EditTextEspecial_ime_s)) {
            if (Objects.equals(atributos.getString(R.styleable.EditTextEspecial_ime_s), "actionDone")) {
                etContenido.setImeOptions(EditorInfo.IME_ACTION_DONE);
                tieneAccionIme = true;
            } else {
                tieneAccionIme = false;
            }
        }

        if (atributos.hasValue(R.styleable.EditTextEspecial_ime_s)) {
            if (Objects.equals(atributos.getString(R.styleable.EditTextEspecial_ime_s), "actionDone")) {
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

    public void establecerColorError(final int color){
        indicadorError.setBackgroundResource(color);
    }

    public boolean isVacio() {
        return etContenido.length() == 0;
    }

    public String getTexto() {
        return etContenido.getText().toString();
    }

    public boolean laLongitudEsValida() {
        return getTexto().length() == maximoCaracteres;
    }

    public void setNumeroCampos(final boolean isTotem) {
        if (isTotem) {
            etContenido.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NUMERO_TOTEM)});
            maximoCaracteres = NUMERO_TOTEM;
            tvDigito5.setVisibility(VISIBLE);
            tvDigito6.setVisibility(VISIBLE);
            separadorPenultimo.setVisibility(VISIBLE);
            separadorUltimo.setVisibility(VISIBLE);

        } else {
            etContenido.setFilters(new InputFilter[]{new InputFilter.LengthFilter(NUMERO_MPOS)});
            maximoCaracteres = NUMERO_MPOS;

            tvDigito5.setVisibility(GONE);
            tvDigito6.setVisibility(GONE);
            separadorPenultimo.setVisibility(GONE);
            separadorUltimo.setVisibility(GONE);
        }
    }
}
