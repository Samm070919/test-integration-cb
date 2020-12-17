package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.pagatodo.sigmalib.EmvManager;
import com.pagatodo.sigmalib.emv.PerfilEmvApp;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.enums.CamposPCI;
import net.fullcarga.android.api.formulario.Formato;
import net.fullcarga.android.api.formulario.Parametro;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FormularioPCILayout extends RelativeLayout {

    private static final String TAG = FormularioPCILayout.class.getSimpleName();
    private LinearLayout llContenidoFormulario;
    private Map<String, String> layoutParams = new HashMap<>();
    private final Context context;

    public FormularioPCILayout(final Context context) {
        super(context);
        this.context = context;
    }

    public FormularioPCILayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public FormularioPCILayout(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void initUI() {
        llContenidoFormulario = findViewById(R.id.ll_contenido_formulario);
    }

    public void initData(final int idPerfil) {

        if (idPerfil != 0) {

            layoutParams = getCamposIn(idPerfil);
        }

        addParams();
    }

    private void addParams() {

        final Iterator iterator = layoutParams.entrySet().iterator();
        EditTextFormulario paramView;
        final List<Parametro> mListParametros = new ArrayList<>();

        while (iterator.hasNext()) {

            final Map.Entry pair = (Map.Entry) iterator.next();

            final Formato formato = getFormato(pair);

            final Parametro parametro = getParametro(pair, formato);
            mListParametros.add(parametro);
            paramView = getEditTextFormulario();
            paramView.initData(parametro);
            llContenidoFormulario.addView(paramView);
        }
    }

    @NonNull
    private EditTextFormulario getEditTextFormulario() {
        return new EditTextFormulario(context, null);
    }

    @NonNull
    private Parametro getParametro(final Map.Entry pair, final Formato formato) {

        return new Parametro((String) pair.getKey(), formato);
    }

    @NonNull
    private Formato getFormato(final Map.Entry pair) {
        return () -> Formato.Tipo.forCodigo(pair.getValue().toString());
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getCamposIn(final int perfil) {

        final Map<String, String> listCamposIn = new HashMap<>();

        try {

            final PerfilEmvApp perfilEmv = EmvManager.getFullPerfil(perfil, new OnFailureListener.BasicOnFailureListener(TAG, "Error al Consultar el Perfil"));

            if (perfilEmv.getPerfilesEmv() != null) {
                if (perfilEmv.getPerfilesEmv().getInImporte() == 1) {
                    listCamposIn.put(CamposPCI.IMPORTE.campo(), "03");
                }
                if (perfilEmv.getPerfilesEmv().getInImpuesto() == 1) {
                    listCamposIn.put(CamposPCI.IMPUESTO.campo(), "03");
                }
                if (perfilEmv.getPerfilesEmv().getInPropina() == 1) {
                    listCamposIn.put(CamposPCI.PROPINA.campo(), "03");
                }

                if (perfilEmv.getPerfilesEmv().getInRetiroEfectivo() == 1) {
                    listCamposIn.put(CamposPCI.CASHBACK.campo(), "03");

                }
                if (perfilEmv.getPerfilesEmv().getInCodigoPostal() == 1) {
                    listCamposIn.put(CamposPCI.CP.campo(), "01");
                }
                if (perfilEmv.getPerfilesEmv().getInCvv() == 1) {
                    listCamposIn.put(CamposPCI.CVV.campo(), "01");
                }
            }

            return listCamposIn;
        } catch (Exception exe) {
            Logger.LOGGER.info(TAG, exe.getMessage());
            return Collections.emptyMap();
        }
    }
}
