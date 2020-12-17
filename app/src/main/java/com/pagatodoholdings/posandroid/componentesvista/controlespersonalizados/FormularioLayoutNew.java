package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.utils.UtilsKt;
import com.santalu.maskedittext.MaskEditText;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.FormatosLongitud;
import net.fullcarga.android.api.formulario.Formato;
import net.fullcarga.android.api.formulario.FormatoBasico;
import net.fullcarga.android.api.formulario.FormatoConLongitud;
import net.fullcarga.android.api.formulario.Formulario;
import net.fullcarga.android.api.formulario.Parametro;

public class FormularioLayoutNew extends RelativeLayout {

    private final Context context;
    private LinearLayout llContenidoFormulario;
    private Formulario formulario;
    //longitud param
    int min = 0;
    static int max = 50;

    public FormularioLayoutNew(final Context context) {
        super(context);
        this.context = context;
    }

    public FormularioLayoutNew(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FormularioLayoutNew(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void initUI() {
        llContenidoFormulario = findViewById(R.id.ll_contenido_formulario);
    }

    public void initData(final Formulario formulario, String tipo) {
        this.formulario = formulario;
        if (formulario != null) {
            addParams(tipo);
        }
    }

    private void addParams(String tipo) {

        for (final Parametro param : formulario.getParametros()) {

            //Log.e("tipo",param.getFormato().getTipo().toString());
            //Log.e("literal",param.getLiteral());

            if (param.getFormato().getTipo() == Formato.Tipo.NUMERICO  ) {

                if(param.getLiteral().toLowerCase().contains("referencia de pago")){
                    final TextView textView;
                    textView = new TextView(context, null);
                    textView.setText(R.string.txt_referencia_unica);
                    llContenidoFormulario.addView(textView);

                    final InputNumeroServicioUnico view;
                    view = getEditTextNumeroServicio(context, null);
                    view.initData(param);
                    llContenidoFormulario.addView(view);
                }

                else if (param.getLiteral().equals("PROMO CODE") || param.getLiteral().equalsIgnoreCase("DNI") ||
                        param.getLiteral().equals("VALE") || param.getLiteral().equals("DNI CANJEADOR") || param.getLiteral().equals("COMPROBANTE")
                        || param.getLiteral().equals("ID PAGO") || param.getLiteral().equals("SUMINISTRO") || param.getLiteral().equals("COD. CLIENTE")
                        || param.getLiteral().equals("COD CLIENTE") || param.getLiteral().equals("ID PAGO") || param.getLiteral().equals("Planes")
                        || param.getLiteral().toLowerCase().contains("numero celular") || param.getLiteral().equalsIgnoreCase("TELEFONO")) {

                    final EditTextFormulario view;
                    view = getEditTextFormulario(context, null);
                    view.initData(param);
                    llContenidoFormulario.addView(view);


                }else {

                    final EditTextFormulario view;
                    view = getEditTextFormulario(context, null);
                    view.initData(param);
                    llContenidoFormulario.addView(view);

                }

            }else if(param.getFormato().getTipo() == Formato.Tipo.TELEFONO  ){

                final TextView textView;
                textView = new TextView(context, null);
                textView.setText(R.string.numero_telefonico);
                textView.setTextColor(getResources().getColor(R.color.grey_lines));
                llContenidoFormulario.addView(textView);
                //view.initData(param);
                if(Country.PERU.getItemIsoCode().equals(MposApplication.getInstance().getDatosLogin().getPais().getCodigo())){
                    final MaskEditText view;
                    view = getMaskEditText(context, null);
                    view.setInputType(InputType.TYPE_CLASS_PHONE);
                    view.setMask(Country.PERU.getItemMask());
                    maskSetLongitud(param);
                    llContenidoFormulario.addView(view);
                }else if(Country.COLOMBIA.getItemIsoCode().equals(MposApplication.getInstance().getDatosLogin().getPais().getCodigo())){
                    final MaskEditText view;
                    view = getMaskEditText(context, null);
                    view.setInputType(InputType.TYPE_CLASS_PHONE);
                    view.setMask(Country.COLOMBIA.getItemMask());
                    maskSetLongitud(param);
                    llContenidoFormulario.addView(view);
                }

                final RecyclerView recyclerView;
                recyclerView = new RecyclerView(context, null);
                llContenidoFormulario.addView(recyclerView);

            }else if(param.getFormato().getTipo() == Formato.Tipo.TELEFONO_USUARIO){

                final TextView textView;
                textView = new TextView(context, null);
                textView.setText(R.string.numero_telefonico);
                llContenidoFormulario.addView(textView);

                if(Country.PERU.getItemIsoCode().equals(MposApplication.getInstance().getDatosLogin().getPais().getCodigo())){
                    final MaskEditText view;
                    view = getMaskEditText(context, null);
                    view.setInputType(InputType.TYPE_CLASS_PHONE);
                    view.setMask(Country.PERU.getItemMask());
                    maskSetLongitud(param);
                    llContenidoFormulario.addView(view);
                }else if(Country.COLOMBIA.getItemIsoCode().equals(MposApplication.getInstance().getDatosLogin().getPais().getCodigo())){
                    final MaskEditText view;
                    view = getMaskEditText(context, null);
                    view.setInputType(InputType.TYPE_CLASS_PHONE);
                    view.setMask(Country.COLOMBIA.getItemMask());
                    maskSetLongitud(param);
                    llContenidoFormulario.addView(view);
                }

            }
            else if (param.getFormato().getTipo() == Formato.Tipo.ENUMERACION) {

                final SpinnerFormulario spinnerFormulario = getSpinnerFormulario(context, param);
                llContenidoFormulario.addView(spinnerFormulario);
            }
            else if (param.getFormato().getTipo() == Formato.Tipo.ALFANUMERICO) {

                final EditTextFormulario view;
                view = getEditTextFormulario(context, null);
                view.initData(param);
                llContenidoFormulario.addView(view);

                final RecyclerView recyclerView;
                recyclerView = new RecyclerView(context, null);
                llContenidoFormulario.addView(recyclerView);
            }

        }

        if(tipo.equals("recarga")){
            final RecyclerView recyclerView;
            recyclerView = new RecyclerView(context, null);
            llContenidoFormulario.addView(recyclerView);
        }
    }

    private String validateInputPhone() {
        if (MposApplication
                .getInstance()
                .getDatosLogin()
                .getCliente()
                .getTelefono()
                .contains("+")
        ) {
            return MposApplication
                    .getInstance()
                    .getDatosLogin()
                    .getCliente()
                    .getTelefono()
                    .substring(3);
        } else {
            return MposApplication.getInstance().getDatosLogin().getCliente().getTelefono();
        }
    }

    private SpinnerFormulario getSpinnerFormulario(final Context context, final Parametro param) {
        return new SpinnerFormulario(context, param);
    }

    private InputPhoneNumber getEditTextPhoneNumber(
            final Context context,
            final AttributeSet attrs
    ) {
        return new InputPhoneNumber(context, attrs);
    }

    private InputNumeroServicioUnico getEditTextNumeroServicio(
            final Context context,
            final AttributeSet attrs
    ) {
        return new InputNumeroServicioUnico(context, attrs);
    }

    private EditTextFormulario getEditTextFormulario(
            final Context context,
            final AttributeSet attrs
    ) {
        return new EditTextFormulario(context, attrs);
    }

    private MaskEditText getMaskEditText(
            final Context context,
            final AttributeSet attrs
    ) {
        return new MaskEditText(context, attrs);
    }

    private void maskSetLongitud(Parametro parametro){

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

        Log.e("maximo",""+max);
    }

    public static int getMax(){
        return max;
    }

}
