package com.pagatodoholdings.posandroid.secciones.formularios;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.pagatodo.qposlib.abstracts.AbstractDongle;
import com.pagatodo.qposlib.dongleconnect.PosInterface;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.EmvManager;
import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.emv.PerfilEmvApp;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentFormularioBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.general.interfaces.RetrocesoListener;
import com.pagatodoholdings.posandroid.secciones.dongleconnect.PciLoginManager;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuDongleVinculadoFragment;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.ManejadorFragments;
import com.pagatodoholdings.posandroid.utils.Utilities;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.LiteralesOperacion;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.formulario.Formato;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static net.fullcarga.android.api.oper.TipoOperacion.CONSULTA_X;
import static net.fullcarga.android.api.oper.TipoOperacion.CONSULTA_Z;

public abstract class AbstractFormularioPCIFragment extends AbstractFragment implements OnFailureListener {

    //----------UI-------------------------------------------------------
    protected FragmentFormularioBinding binding;

    //-----Inst ----------------------------------------------------------
    protected Operaciones operacion;
    protected Productos producto;
    protected int indiceOperacion;
    protected ManejadorFragments manejadorFragments;
    protected NivelMenu nivelMenu;
    protected Menu menu;
    protected Drawable icono;
    protected AbstractDongle qposDongle;

    //----- Var ----------------------------------------------------------

    public void setArgs(final NivelMenu nivelMenu, final Menu menu, final Operaciones operacion) {
        this.nivelMenu = nivelMenu;
        this.menu = menu;
        this.operacion = operacion;
    }

    public void setArgs(final Operaciones operacion) {
        this.operacion = operacion;
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        binding = FragmentFormularioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    private void initUI() {
        activity = (AbstractActivity) getActivity();
        if (PciLoginManager.isPciLoggedIn()) {
            qposDongle = MposApplication.getInstance().getPreferedDongle().getQpos(PosInterface.Tipodongle.DSPREAD);
            manejadorFragments = new ManejadorFragments(activity.getSupportFragmentManager());

            this.producto = SigmaBdManager.getProducto(operacion, this);

            if (CONSULTA_X.getTipo().equals(operacion.getOperacion()) || CONSULTA_Z.getTipo().equals(operacion.getOperacion())) {
                activity.despliegaModal(true, false, getString(R.string.operacion_pci), getString(R.string.error_tipoOperacion), this::onAccept);
                return;
            }

            View formularioLayout = null;

            if (SigmaBdManager.getFormulario(operacion, this) != null && !SigmaBdManager.getFormulario(operacion, this).getParametros().isEmpty() && SigmaBdManager.getFormulario(operacion, this).getParametros().get(0).getFormato().getTipo() != Formato.Tipo.COSTO) {

                formularioLayout = FormularioFactory.build(getActivity(), SigmaBdManager.getFormulario(operacion, this));
                binding.llFormulario.addView(formularioLayout);

            }
            if (producto.getPci() == 1 && producto.getPerfilEmv() != null) {
                formularioLayout = FormularioFactory.build(getActivity(), producto.getPerfilEmv());
                binding.llFormulario.addView(formularioLayout);
            }

            this.icono = Utilities.getIcono(SigmaBdManager.obtenIcono(menu, new BasicOnFailureListener(TAG, getString(R.string.error_obtener_icono))));
            binding.iconProduct.setImageDrawable(icono);


            setHeaderInfo();


            final LiteralesOperacion literal = SigmaBdManager.getLiteralOperacion(operacion, this);

            binding.btnSiguienteFormulario.setText(literal.getValor());
            binding.btnSiguienteFormulario.setOnClickListener(view -> {
                try {
                    executeOperacion(producto, indiceOperacion);
                } catch (RuntimeException exe) {
                    Logger.LOGGER.throwing(TAG, 1, exe, getString(R.string.error_operacion));
                }
            });

        } else {
            activity.despliegaModal(true, false, getString(R.string.dongle_error), getString(R.string.dongle_error_msg), () -> {
                ConfigMenuDongleVinculadoFragment fragment = new ConfigMenuDongleVinculadoFragment();
                fragment.setListener((HomeActivity) getActivity());
                manejadorFragments = new ManejadorFragments(activity.getSupportFragmentManager());
                manejadorFragments.sobreponerFragment(R.id.fl_main_pantalla_completa, fragment);
            });
        }
    }

    private void setHeaderInfo() {

        final String textPCI = "Venta de %s a %s con Incremento de %s";

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);


        if (producto.getPerfilEmv() != null) {

            final PerfilEmvApp fullPerfil = EmvManager.getFullPerfil(producto.getPerfilEmv(), new BasicOnFailureListener(TAG, "Error al Consultar el Perfil"));

            if (!operacion.getOperacion().equals("X") && !operacion.getOperacion().equals("D")) {
                binding.txtFooterFormulario.setText(String.format(textPCI,

                        ApiData.APIDATA.getDatosSesionPCI().getDatosTPV().convertirImporte(fullPerfil.getEmvMonedas().getImporteMin().toString()) // Importe Minimo
                        , ApiData.APIDATA.getDatosSesionPCI().getDatosTPV().convertirImporte(fullPerfil.getEmvMonedas().getImporteMax().toString()), // Importe Maximo
                        ApiData.APIDATA.getDatosSesionPCI().getDatosTPV().convertirImporte(fullPerfil.getEmvMonedas().getIncremento().toString()))); // Incremento
            }



            if (fullPerfil.getEmvCashBackMonedas() != null && fullPerfil.getEmvCashBackMonedas().getCodigo() > 0 && !operacion.getOperacion().equals("X") && !operacion.getOperacion().equals("D")) {
                binding.txtFooterFormulario.setText(String.format(textPCI, producto.getImporteMin().toString(), producto.getImporteMax(), producto.getIncremento()) +
                        String.format(" %n Retiro de %s a %s con Incremento de %s",
                                ApiData.APIDATA.getDatosSesionPCI().getDatosTPV().convertirImporte(fullPerfil.getEmvCashBackMonedas().getImporteMin().toString())
                                , ApiData.APIDATA.getDatosSesionPCI().getDatosTPV().convertirImporte(fullPerfil.getEmvCashBackMonedas().getImporteMax().toString())
                                , ApiData.APIDATA.getDatosSesionPCI().getDatosTPV().convertirImporte(fullPerfil.getEmvCashBackMonedas().getIncremento().toString())));
            }
        }else{

            if (!operacion.getOperacion().equals("X") && !operacion.getOperacion().equals("D")) {
                binding.txtFooterFormulario.setText(String.format(textPCI, producto.getImporteMin().toString(), producto.getImporteMax(), producto.getIncremento()));
            }
        }

    }

    @Override
    public void setRetrocesoListener(final RetrocesoListener retrocesoListener) {
        this.retrocesoListener = retrocesoListener;
    }

    @Override
    public void onFailure(final Throwable throwable) {
        Logger.LOGGER.throwing(throwable.getMessage(), 1, throwable, getString(R.string.error_con_bd));
    }

    @Override
    protected boolean isTomandoBack() {
        retrocesoListener.onRetroceso(nivelMenu);
        activity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        return true;
    }

    abstract void executeOperacion(final Productos producto, final int indiceOperacion);

    private void onAccept() {
        ((HomeActivity) getActivity()).regresaMenu();
    }
}
