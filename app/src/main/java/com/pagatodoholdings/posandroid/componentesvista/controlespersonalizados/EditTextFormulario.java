package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.SigmaUtil;
import com.pagatodoholdings.posandroid.R;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.FormatosLongitud;
import net.fullcarga.android.api.formulario.Formato;
import net.fullcarga.android.api.formulario.FormatoBasico;
import net.fullcarga.android.api.formulario.FormatoConLongitud;
import net.fullcarga.android.api.formulario.Parametro;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class EditTextFormulario extends AbstractEditTextPersonalizado {

    private static final String TAG = EditTextFormulario.class.getSimpleName();
    private NumberFormat dbnumberformat;

    public EditTextFormulario(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void setIdVista() {
        idVista = R.layout.campo_validado_formulario;
    }

    public void initData(final Parametro parametro) {

        setParametro(parametro);

        dbnumberformat = SigmaBdManager.getInputNumberFormat(new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener numberFormat"));

        obtentvHint().setText(parametro.getLiteral());
        int min = 0;
        int max = 50;
        if (parametro.getFormato() instanceof FormatoConLongitud) {
            final FormatoConLongitud formatoConLongitud = (FormatoConLongitud) parametro.getFormato();
            min = formatoConLongitud.getMin();
            max = formatoConLongitud.getMax();
        } else if (parametro.getFormato() instanceof FormatosLongitud) {
            final FormatosLongitud formatosLongitud = (FormatosLongitud) parametro.getFormato();
            min = formatosLongitud.getMin();
            max = formatosLongitud.getMax();
        } else if (parametro.getFormato() instanceof FormatoBasico) {
            min = 1;
            max = 30;
        }
        ponEsOpcional(min == 0);
        obtenEtCampo().setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        setDataType(parametro.getFormato().getTipo());
        obtenEtCampo().setTag(parametro.getLiteral());
        obtenEtCampo().setLongClickable(false);
        obtenEtCampo().setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        obtenEtCampo().setGravity(Gravity.CENTER);
        if (parametro.getFormato().getTipo() == Formato.Tipo.IMPORTE || parametro.getFormato().getTipo() == Formato.Tipo.IMPORTE_SIN_VAL || parametro.getFormato().getTipo() ==Formato.Tipo.NUMERICO_CON_VALIDACION){
            obtenEdContainer().setVisibility(GONE);
            obtenEtCampo().setVisibility(GONE);
            obtenMontoView().setVisibility(VISIBLE);
            obtenMontoView().setTitulo(parametro.getLiteral());
        }
        addCustomEditText(parametro.getFormato().getTipo());
    }

    private void addCustomEditText(final Formato.Tipo tipo) {
        final WeakReference<EditText> editTextWeakReference = new WeakReference<>(obtenEtCampo());
        final EditText editText = editTextWeakReference.get();

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int start, final int count, final int after) {

                //Nothing to do
            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int start, final int before, final int count) {

                //Nothing to do
            }

            @Override
            public void afterTextChanged(final Editable editable) {

                editText.removeTextChangedListener(this);

                if ((tipo == Formato.Tipo.IMPORTE || tipo == Formato.Tipo.IMPORTE_SIN_VAL || tipo ==Formato.Tipo.NUMERICO_CON_VALIDACION) && editable.length() > 0 && !editable.toString().trim().equals("")) {
                    final String string = editable.toString();
                    if (string.isEmpty()) {
                        return;
                    }
                    final BigDecimal importe = SigmaUtil.cleanImporteInput(string, dbnumberformat);
                    final String formatted = dbnumberformat.format(importe);
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                    final String parametroFijo = SigmaBdManager.getParametroFijo("0021", new OnFailureListener.BasicOnFailureListener(TAG, "Error al obtener parametro fijo"));
                    if (formatted.endsWith(parametroFijo)) {
                        editText.setSelection(formatted.length() - parametroFijo.length());
                    }
                }
                editText.addTextChangedListener(this);
            }
        });
    }

    public void setTextoFijo(String textoFijo) {
        obtenEtCampo().setText(textoFijo);
    }

}
