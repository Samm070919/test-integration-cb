package com.pagatodoholdings.posandroid.secciones.formularios;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.NivelMenu;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.reportetrx.TransaccionesBD;
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextFormulario;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.FormularioLayout;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.FormularioLayoutNew;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.InputNumeroServicioUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.InputPhoneNumber;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.SpinnerFormulario;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.FragmentFormularioBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.dialogs.PDSErrorDialog;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.recargas.RecargasAdapter;
import com.pagatodoholdings.posandroid.secciones.servicios.DialogTaeConfirmacion;
import com.pagatodoholdings.posandroid.secciones.servicios.PagoServiciosFragment;
import com.pagatodoholdings.posandroid.secciones.servicios.PaySucessfulPDSFragment;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.ManejadorFragments;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.enums.MediosPago;
import com.pagatodoholdings.posandroid.utils.enums.ProductCategory;
import com.santalu.maskedittext.MaskEditText;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.LiteralesOperacion;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.data.respuesta.AbstractRespuesta;
import net.fullcarga.android.api.data.respuesta.RespuestaTrx;
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno;
import net.fullcarga.android.api.formulario.Formato;
import net.fullcarga.android.api.formulario.Formulario;
import net.fullcarga.android.api.formulario.Parametro;
import net.fullcarga.android.api.oper.TipoOperacion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_X;
import static com.pagatodoholdings.posandroid.utils.Constantes.CONSULTA_Z;
import static com.pagatodoholdings.posandroid.utils.Constantes.DEVOLUCION;
import static com.pagatodoholdings.posandroid.utils.Constantes.VENTA;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

@SuppressWarnings("PMD.GodClass")
public class FormularioGenerico extends AbstractFragment {

    //----------UI-------------------------------------------------------
    protected FragmentFormularioBinding binding;

    //-----Inst ----------------------------------------------------------
    protected ManejadorFragments manejadorFragments;
    protected Operaciones operacion;
    protected Productos producto;
    protected Formulario formulario;
    protected LiteralesOperacion literal;
    protected NivelMenu nivelMenu;

    //----- Var ----------------------------------------------------------
    protected Drawable icono;
    private TransaccionesBD datosOperacion;

    private String campoReferenciaEdit = "";
    private BigDecimal cargoServicio = BigDecimal.ZERO;
    private boolean isRecarga;
    private String campoInputMonto = "";
    private String campoInputNumero = "";
    private Menu menu;
    private String campoSpinner = "";
    private List<String> parametros;

    public static FormularioGenerico newInstance(final Operaciones operacion, final NivelMenu nivelMenu) {
        final FormularioGenerico fragment = new FormularioGenerico();
        fragment.setArgs(operacion, nivelMenu);
        return fragment;
    }

    public static FormularioGenerico newInstance(final Operaciones operacion, final NivelMenu nivelMenu, final Menu menu) {
        final FormularioGenerico fragment = new FormularioGenerico();
        fragment.setArgs(operacion, nivelMenu, menu);
        return fragment;
    }

    public static FormularioGenerico newInstance(final Productos producto) {
        final FormularioGenerico fragment = new FormularioGenerico();
        fragment.setArgs(producto);
        return fragment;
    }

    public static FormularioGenerico newInstance(final Operaciones operacion, final NivelMenu nivelMenu, final Formulario formulario) {
        final FormularioGenerico fragment = new FormularioGenerico();
        fragment.setArgs(operacion, nivelMenu, formulario);
        return fragment;
    }

    public void setArgs(final Operaciones operacion, NivelMenu nivelMenu) {
        this.operacion = operacion;
        this.nivelMenu = nivelMenu;
    }

    public void setArgs(final Operaciones operacion, NivelMenu nivelMenu, Menu menu) {
        this.operacion = operacion;
        this.nivelMenu = nivelMenu;
        this.menu = menu;
    }

    public void setArgs(final Operaciones operacion, NivelMenu nivelMenu, Formulario formulario) {
        this.operacion = operacion;
        this.nivelMenu = nivelMenu;
        this.formulario = formulario;
    }

    public void setArgs(final Productos producto) {
        this.producto = producto;
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
        activity = (AbstractActivity) getActivity();//instancia AbstractActivity
        manejadorFragments = new ManejadorFragments(activity.getSupportFragmentManager());//Manejador Fragment
        parametros = new ArrayList<>();
        initUI();
    }

    @SuppressLint("ResourceAsColor")
    private void initUI() {

        //binding.etMontoServicio.getEditText().setEnabled(false);
        //obtiene literal y producto
        if (producto != null) {
            operacion = getOperationByProducto(producto);
            literal = SigmaBdManager.getLiteralOperacion(operacion, this);
        } else {
            literal = SigmaBdManager.getLiteralOperacion(operacion, this);
            producto = SigmaBdManager.getProducto(operacion, this);
        }

        if (formulario == null || formulario.getParametros().isEmpty()) {
            formulario = SigmaBdManager.getFormulario(operacion, this);
        }

        if (formularioHasInput(formulario)) {
            binding.etMontoServicio.setVisibility(View.VISIBLE);
        } else {
            binding.etMontoServicio.setVisibility(View.GONE);
        }

        isRecarga = formularioHasInput(formulario);

        View formularioLayout;
        //verifica si es venta y paquetes para ocultar el edittext del monto
        if (!isRecarga && operacion.getOperacion().equals(VENTA) && producto.getCategoria() == ProductCategory.PDS) {

            //binding.txtFooterFormulario.setText("Consulta imagen");
            //binding.txtFooterFormulario
            //       .setTextColor(getResources().getColor(R.color.blue_dialog));
        }
        //se cargan los elementos con el formulario
        if (producto.getPci() == 1) {
            formularioLayout = FormularioFactory.build(activity, producto.getPerfilEmv());
        } else {
            if (producto.getCategoria() == ProductCategory.PDS) {
                formularioLayout = FormularioFactory.build(activity, formulario, "con");
                //se agreg el footer para las consultas
                //binding.txtFooterFormulario.setText("Consulta imagen");
                //binding.txtFooterFormulario
                //        .setTextColor(getResources().getColor(R.color.blue_dialog));
            } else if (producto.getCategoria() == ProductCategory.TAE
                    || producto.getCategoria() == ProductCategory.OTRAS_RECARGAS) {
                if (isRecarga) {
                    formularioLayout = FormularioFactory.build(activity, formulario, "recarga");
                } else {
                    binding.formularioHeader.tvTitulo.setText("Recargas de Tiempo Aire");
                    binding.txtFooterFormulario.setText(R.string.seguro_pagar_paquete);
                    formularioLayout = FormularioFactory.build(activity, formulario, "paquetes");
                }

            } else {
                formularioLayout =
                        FormularioFactory.build(activity, formulario, getSubmenuType(formulario, producto));
            }

            if (operacion.getOperacion().equals(CONSULTA_X)) {

                if (producto.getImporte().doubleValue() > 1)
                    binding.etMontoServicio.setMonto(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(producto.getImporte().toString()));
                else
                    binding.etMontoServicio.setMonto(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte("0"));


            } else if (formularioHasInput(formulario) && operacion.getOperacion().equals(VENTA)) {
                binding.etMontoServicio.getClContenedor().setVisibility(View.VISIBLE);

                if (producto.getImporte().doubleValue() > 1) {
                    binding.etMontoServicio.setMonto(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(producto.getImporte().toString()));
                    // binding.etMontoServicio.setMonto("0");
                } else {
                    binding.etMontoServicio.setMonto(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte("0"));
                }
            }
        }
        Menu menu = new Menu();
        menu.setIcono(producto.getIcono());
        icono = Utilities.getIcono(SigmaBdManager.obtenIcono(
                menu,
                new OnFailureListener.BasicOnFailureListener(
                        TAG,
                        getString(R.string.error_obtener_icono)
                )
        ));

        binding.iconProduct.setBackground(icono);
        setToolbar();

        binding.llFormulario.addView(formularioLayout);
        if (isRecarga) {
            obtenerRecycler();
        }

        binding.btnSiguienteFormulario.setOnClickListener(view -> {
            cargarConfirmacion();
        });

        binding.formularioHeader
                .toolbar4
                .setNavigationOnClickListener(v -> isTomandoBack());

    }

    private Operaciones getOperationByProducto(Productos producto) {
        List<Operaciones> listaOperaciones = SigmaBdManager.getOperacionesVisiblesPorProducto(producto.getCodigo(), this);

        if (listaOperaciones != null && !listaOperaciones.isEmpty()) {
            return listaOperaciones.get(0);
        } else {
            return null;
        }
    }

    private void setToolbar() {

        activity.setSupportActionBar(binding.formularioHeader.toolbar4);
        binding.formularioHeader.toolbar4.setNavigationIcon(R.drawable.ic_icono_back);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (operacion.getOperacion().equals(CONSULTA_X) && producto.getCategoria() == ProductCategory.PDS) {
            binding.formularioHeader.tvTitulo.setText("Pago de Servicios");
            binding.etMontoServicio.setTitulo("Monto del Servicio");
        } else if (isRecarga && operacion.getOperacion().equals(VENTA) && producto.getCategoria() == ProductCategory.TAE) {
            binding.formularioHeader.tvTitulo.setText("Recargas de Tiempo Aire");
            binding.etMontoServicio.setTitulo("Monto de Recarga");
            binding.etMontoServicio.setMaxLenght(6);
            //binding.etMontoServicio.getEditText().setEnabled(false);

        } else if (producto.getCategoria() == ProductCategory.OTRAS_RECARGAS) {
            if(Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()) == Country.PERU){
                binding.formularioHeader.tvTitulo.setText("Recargas de Tiempo Aire");
            }else{
                binding.formularioHeader.tvTitulo.setText("Pines");
            }
            binding.etMontoServicio.setTitulo("Monto de Recarga");
            binding.etMontoServicio.setMaxLenght(6);
        }

        binding.formularioHeader.lyFmSaldo.setNewTextSizeIndividualMonto(22f, 30f, 22f);
        binding.formularioHeader.lyFmSaldo.setMonto(((HomeActivity) getActivity()).getSaldo() != null ? ((HomeActivity) getActivity()).getSaldo().toPlainString() : "0");
    }

    @Override
    public void onFailure(final Throwable throwable) {
        LOGGER.throwing(throwable.getMessage(), 1, throwable, getString(R.string.error_con_bd));
    }

    @Override
    protected boolean isTomandoBack() {
        hideViewElements(binding.clContenedor);

        if (nivelMenu == null && producto != null) {
            ((HomeActivity) getActivity()).goToCalendarFromProducts();
            return true;
        } else if (retrocesoListener != null) {
            if (!nivelMenu.tieneNivelPrevio()) {
                return false;
            } else {
                retrocesoListener.onRetroceso(nivelMenu.getNivelPrevio());
                return true;
            }
        } else {
            return false;
        }
    }

    private TipoOperacion getTipoOperacion(final Operaciones operacion) {
        switch (operacion.getOperacion()) {
            case VENTA:
                return TipoOperacion.VENTA;
            case CONSULTA_X:
                return TipoOperacion.CONSULTA_X;
            case CONSULTA_Z:
                return TipoOperacion.CONSULTA_Z;
            case DEVOLUCION:
                return TipoOperacion.DEVOLUCION;
            default:
                return null;
        }
    }

    private void successVenta(final RespuestaTrxCierreTurno result) { //NOSONAR +Local Se usa correctamente por el Api minima
        datosOperacion.setImporte(result.getCamposCierreTurno().getImporte().toPlainString());
        datosOperacion.setReflocal(result.getCamposCierreTurno().getRefLocal());
        datosOperacion.setRefremota(result.getCamposCierreTurno().getRefRemota());
        datosOperacion.setRefcliente(result.getCamposCierreTurno().getRefCliente());
        datosOperacion.setMedioPago(MediosPago.OTRO);

        Productos mProducto = SigmaBdManager.getProducto(operacion, new BasicOnFailureListener(TAG, ""));
        Utilities.guardarSigmaTransacciones(datosOperacion, operacion, mProducto.getCierreTurno());
        PaySucessfulPDSFragment pagoExitosoFragment =
                new PaySucessfulPDSFragment(
                        "PDS",
                        icono,
                        new BigDecimal(
                                ApiData.APIDATA
                                        .getDatosSesion()
                                        .getDatosTPV()
                                        .rellenarImporte(
                                                result.getCamposCierreTurno()
                                                        .getImporte()
                                                        .toPlainString()
                                        )
                        )
                        ,
                        result.getCamposCierreTurno().getRefLocal().trim(),
                        result.getCamposCierreTurno().getRefLocal().trim(),
                        result.getCamposCierreTurno().getRefRemota().trim(),
                        producto,
                        operacion,
                        result
                );
        manejadorFragments.cargarFragmentPantallaCompleta(pagoExitosoFragment, activity);
    }

    private void successConsulta(final RespuestaTrx result) {
        if (result.getOperacionSiguiente().isOperNextRequired()) {
            if (operacion.getTicketOblig() == 0 || operacion.getTicketOblig() == 1) {
                //cargarTicketFragment(nivelMenu, menu, tipoOperacion, operacion, result, retrocesoListener);
            } else {
                cargarFragmentOperacionSiguiente(
                        nivelMenu,
                        menu,
                        SigmaBdManager.getOperacionPorProducto(
                                producto.getCodigo(),
                                result.getOperacionSiguiente().getTipoOperacionNext().getTipo(),
                                new BasicOnFailureListener(TAG, "Error al Obtener Operacion por Producto")
                        ),
                        result.getOperacionSiguiente().getFormularioNext());
            }
        } else {
            if (operacion.getTicketOblig() == 0 || operacion.getTicketOblig() == 1) {
                //cargarTicketFragment(nivelMenu, menu, tipoOperacion, operacion, result, retrocesoListener);
            } else {
                retrocesoListener.onRetroceso(null);
                activity.getSupportFragmentManager().beginTransaction().remove(FormularioGenerico.this).commit();
            }
        }
    }

    private void cargarFragmentOperacionSiguiente(final NivelMenu nivelMenu, final Menu menu, final Operaciones operacion, final Formulario formulario) {
        final AbstractFragment formularioFragment = FormularioFragmentFactory.build(nivelMenu, menu, operacion, formulario);
        formularioFragment.setRetrocesoListener(retrocesoListener);
        manejadorFragments.cargarFragmentPantallaCompleta(formularioFragment, activity);
    }

    private void cargarConfirmacion() {

        final List<String> fields = new ArrayList<String>();

        //se obtienen los elementos del formulario generico

        final RelativeLayout formularioLayout =
                (RelativeLayout) binding.llFormulario.getChildAt(0);
        final ScrollView scrollView = (ScrollView) formularioLayout.getChildAt(0);
        final LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);


        for (int index = 0; index < linearLayout.getChildCount(); ++index) {
            if (linearLayout.getChildAt(index) instanceof InputNumeroServicioUnico) {

                final InputNumeroServicioUnico editTextFormulario =
                        (InputNumeroServicioUnico) linearLayout.getChildAt(index);
                inputIsEmpty(editTextFormulario.obtenEtCampo().getText().toString());
                campoReferenciaEdit = editTextFormulario.obtenEtCampo().getText().toString();

                if (!editTextFormulario.obtenEtCampo().getText().toString().isEmpty()) {
                    parametros.add(campoReferenciaEdit);
                    fields.add(campoReferenciaEdit);
                } else {
                    showErrorDialog(
                            "Complete Los Datos"
                    );
                    return;
                }
            } else if ((linearLayout.getChildAt(index) instanceof MaskEditText) ||
                    ((linearLayout.getChildAt(index) instanceof InputPhoneNumber))
            ) {
                Object Edittext = linearLayout.getChildAt(index);
                if (Edittext instanceof MaskEditText) {
                    String phoneNumber = Objects.requireNonNull(((MaskEditText) Edittext).getRawText()).replace(" ", "");

                    if (getValidationLimit(phoneNumber.length())) {
                        campoInputNumero = phoneNumber;
                        parametros.add(campoInputNumero);
                        fields.add(campoInputNumero);
                    } else {
                        showErrorDialog(
                                "Complete el Número de Teléfono"
                        );
                        return;
                    }
                }
            } else if (linearLayout.getChildAt(index) instanceof EditTextFormulario) {
                final EditTextFormulario editTextFormulario =
                        (EditTextFormulario) linearLayout.getChildAt(index);
                String campoPromoCode = editTextFormulario.obtenEtCampo().getText().toString();

                if (!campoPromoCode.isEmpty()) {
                    parametros.add(campoPromoCode);
                    fields.add(campoPromoCode);
                } else {
                    showErrorDialog(
                            "Complete Los Datos "
                    );
                    return;
                }
            } else if (linearLayout.getChildAt(index) instanceof SpinnerFormulario) {
                final SpinnerFormulario spinnerFormularioFormulario =
                        (SpinnerFormulario) linearLayout.getChildAt(index);
                    campoSpinner = spinnerFormularioFormulario.getSelectedPair().getLeft();
                campoReferenciaEdit = spinnerFormularioFormulario.getSelectedPair().getRight();
                parametros.add(campoReferenciaEdit);
                fields.add(spinnerFormularioFormulario.getSelectedPair().getRight());
            } else if (linearLayout.getChildAt(index) instanceof EditTextFormulario) {
                final EditTextFormulario editTextFormulario =
                        (EditTextFormulario) linearLayout.getChildAt(index);
                String campoAlfanumerico = editTextFormulario.obtenEtCampo().getText().toString();


                if (!campoAlfanumerico.isEmpty()) {
                    parametros.add(campoAlfanumerico);
                    fields.add(campoAlfanumerico);
                } else {
                    showErrorDialog(
                            "Complete Los Datos"
                    );
                    return;
                }

            }
        }

        if (binding.etMontoServicio != null && binding.etMontoServicio.getVisibility() == View.VISIBLE) {
            if (binding.etMontoServicio.getEditText().getText().length() > 0) {
                campoInputMonto = binding.etMontoServicio.getEditText().getText().toString();
                fields.add(1,campoInputMonto);
            } else {
                showErrorDialog(
                        "Complete Todos Los Campos"
                );
                return;
            }
        }
        parametros.add(binding.etMontoServicio.getEditText().getText().toString());


        Map<String,Parametro> parametrovalor =new LinkedHashMap<>();

        int index = 0;

        if(formulario != null)
        for (final Parametro param : formulario.getParametros()) {

            if(param.getFormato().getTipo().equals(Formato.Tipo.IMPORTE)||param.getFormato().getTipo().equals(Formato.Tipo.IMPORTE_SIN_VAL)){
                parametrovalor.put(ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(binding.etMontoServicio.getMontoFromEditText()) ,param);
                LOGGER.fine(TAG,"Key - Form " +  ApiData.APIDATA.getDatosSesion().getDatosTPV().rellenarImporte(binding.etMontoServicio.getMontoFromEditText()) );
                LOGGER.fine(TAG,"Value - Form " + new Gson().toJson(param));
            }else{
                parametrovalor.put(fields.get(index),param);
                LOGGER.fine(TAG,"Key - Form " + fields.get(index));
                LOGGER.fine(TAG,"Value - Form " + new Gson().toJson(param));
                ++index;

            }

        }

        datosOperacion = new TransaccionesBD();
        TipoOperacion tipoOperacion = getTipoOperacion(operacion);
        Log.e("operacion", producto.getCategoria().toString());
        if (producto.getCategoria() == ProductCategory.CORRESPONSAL_BANCARIO) {
            AbstractTransaccion transaccion =
                    TransaccionFactory.crearTransacion(
                            tipoOperacion,
                            (AbstractRespuesta abstractRespuesta) -> {
                                if (abstractRespuesta.isCorrecta()) {
                                    Utilities.analyticsLogEventVenta(getActivity(), firebaseAnalytics);
                                    activity.ocultaProgressDialog();
                                    if (abstractRespuesta instanceof RespuestaTrxCierreTurno) {
                                        successVenta((RespuestaTrxCierreTurno) abstractRespuesta);
                                    } else {
                                        successConsulta((RespuestaTrx) abstractRespuesta);
                                    }
                                } else {
                                    activity.ocultaProgressDialog();
                                    activity.despliegaModal(
                                            true,
                                            false,
                                            literal.getValor(),
                                            abstractRespuesta.getMsjError(),
                                            () -> {
                                                hideViewElements(binding.clContenedor);
                                                ((HomeActivity) activity).regresaMenu();
                                            });
                                    LOGGER.throwing(
                                            TAG,
                                            1,
                                            new Throwable(abstractRespuesta.getMsjError()),
                                            abstractRespuesta.getMsjError()
                                    );
                                }
                            }, (Throwable throwable) -> {
                                activity.ocultaProgressDialog();
                                activity.despliegaModal(
                                        true,
                                        false,
                                        literal.getValor(),
                                        throwable.getMessage(),
                                        () -> {
                                            hideViewElements(binding.clContenedor);
                                            ((HomeActivity) activity).regresaMenu();
                                        });
                                LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                            });

            transaccion.withFields(fields);
            transaccion.withProcod(operacion.getProducto());
            LOGGER.info(TAG, datosOperacion.toString());
            Logger.LOGGER.info(TAG, datosOperacion.toString());
            activity.muestraProgressDialog(getString(R.string.cargando));
            transaccion.realizarOperacion();
        } else if (tipoOperacion.equals(TipoOperacion.CONSULTA_X) ) {

            AbstractTransaccion transaccion =
                    TransaccionFactory.crearTransacion(
                            tipoOperacion,
                            (AbstractRespuesta abstractRespuesta) -> {
                                if (abstractRespuesta.isCorrecta()) {
                                    Utilities.analyticsLogEventVenta(getActivity(), firebaseAnalytics);
                                    activity.ocultaProgressDialog();
                                    if (abstractRespuesta instanceof RespuestaTrxCierreTurno) {
                                        successVenta((RespuestaTrxCierreTurno) abstractRespuesta);
                                    } else {
                                        successConsulta((RespuestaTrx) abstractRespuesta);
                                    }
                                } else {
                                    activity.ocultaProgressDialog();
                                    activity.despliegaModal(
                                            true,
                                            false,
                                            literal.getValor(),
                                            abstractRespuesta.getMsjError(),
                                            () -> {
                                                hideViewElements(binding.clContenedor);
                                                ((HomeActivity) activity).regresaMenu();
                                            });
                                    LOGGER.throwing(
                                            TAG,
                                            1,
                                            new Throwable(abstractRespuesta.getMsjError()),
                                            abstractRespuesta.getMsjError()
                                    );
                                }
                            }, (Throwable throwable) -> {
                                activity.ocultaProgressDialog();
                                activity.despliegaModal(
                                        true,
                                        false,
                                        literal.getValor(),
                                        throwable.getMessage(),
                                        () -> {
                                            hideViewElements(binding.clContenedor);
                                            ((HomeActivity) activity).regresaMenu();
                                        });
                                LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                            });

            transaccion.withFields(fields);
            transaccion.withProcod(operacion.getProducto());
            LOGGER.info(TAG, datosOperacion.toString());
            Logger.LOGGER.info(TAG, datosOperacion.toString());
            activity.muestraProgressDialog(getString(R.string.cargando));
            transaccion.realizarOperacion();
        } else if (tipoOperacion.equals(TipoOperacion.VENTA) && (producto.getCategoria() == ProductCategory.TAE  || producto.getCategoria() == ProductCategory.OTRAS_RECARGAS )) {
            if (isRecarga) {
                DialogFragment newFragment =
                        new DialogTaeConfirmacion(
                                icono,
                                parametrovalor,
                                operacion,
                                isRecarga
                        );
                newFragment.show(activity.getSupportFragmentManager(), "tae");
            } else {
                DialogFragment newFragment =
                        new DialogTaeConfirmacion(
                                icono,
                                parametrovalor,
                                operacion,
                                isRecarga
                        );
                newFragment.show(activity.getSupportFragmentManager(), "tae");
            }
        } else if (tipoOperacion.equals(TipoOperacion.VENTA) && producto.getCategoria() == ProductCategory.PDS  || tipoOperacion.equals(TipoOperacion.VENTA) && producto.getCategoria() == ProductCategory.PDS_EXTRA ) {
            PagoServiciosFragment pagoServiciosFragment =
                    new PagoServiciosFragment(
                            icono,
                            campoSpinner.replace("$", ""),
                            campoReferenciaEdit,
                            campoInputNumero,
                            cargoServicio,
                            tipoOperacion,
                            operacion,
                            parametrovalor
                    );
            manejadorFragments.cargarFragmentPantallaCompleta(pagoServiciosFragment, activity);
        } else if (producto.getCategoria() == ProductCategory.OTRAS_RECARGAS) {
            AbstractTransaccion transaccion =
                    TransaccionFactory.crearTransacion(
                            tipoOperacion,
                            (AbstractRespuesta abstractRespuesta) -> {
                                if (abstractRespuesta.isCorrecta()) {
                                    Utilities.analyticsLogEventVenta(getActivity(), firebaseAnalytics);
                                    activity.ocultaProgressDialog();
                                    if (abstractRespuesta instanceof RespuestaTrxCierreTurno) {
                                        successVenta((RespuestaTrxCierreTurno) abstractRespuesta);
                                    } else {
                                        successConsulta((RespuestaTrx) abstractRespuesta);
                                    }
                                } else {
                                    activity.ocultaProgressDialog();
                                    activity.despliegaModal(
                                            true,
                                            false,
                                            literal.getValor(),
                                            abstractRespuesta.getMsjError(),
                                            () -> {
                                                hideViewElements(binding.clContenedor);
                                                ((HomeActivity) activity).regresaMenu();
                                            });
                                    LOGGER.throwing(
                                            TAG,
                                            1,
                                            new Throwable(abstractRespuesta.getMsjError()),
                                            abstractRespuesta.getMsjError()
                                    );
                                }
                            }, (Throwable throwable) -> {
                                activity.ocultaProgressDialog();
                                activity.despliegaModal(
                                        true,
                                        false,
                                        literal.getValor(),
                                        throwable.getMessage(),
                                        () -> {
                                            hideViewElements(binding.clContenedor);
                                            ((HomeActivity) activity).regresaMenu();
                                        });
                                LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                            });

            transaccion.withFields(fields);
            transaccion.withProcod(operacion.getProducto());
            LOGGER.info(TAG, datosOperacion.toString());
            Logger.LOGGER.info(TAG, datosOperacion.toString());
            activity.muestraProgressDialog(getString(R.string.cargando));
            transaccion.realizarOperacion();
        } else if (producto.getCategoria() == 17) {
            AbstractTransaccion transaccion =
                    TransaccionFactory.crearTransacion(
                            tipoOperacion,
                            (AbstractRespuesta abstractRespuesta) -> {
                                if (abstractRespuesta.isCorrecta()) {
                                    Utilities.analyticsLogEventVenta(getActivity(), firebaseAnalytics);
                                    activity.ocultaProgressDialog();
                                    if (abstractRespuesta instanceof RespuestaTrxCierreTurno) {
                                        successVenta((RespuestaTrxCierreTurno) abstractRespuesta);
                                    } else {
                                        successConsulta((RespuestaTrx) abstractRespuesta);
                                    }
                                } else {
                                    activity.ocultaProgressDialog();
                                    activity.despliegaModal(
                                            true,
                                            false,
                                            literal.getValor(),
                                            abstractRespuesta.getMsjError(),
                                            () -> {
                                                hideViewElements(binding.clContenedor);
                                                ((HomeActivity) activity).regresaMenu();
                                            });
                                    LOGGER.throwing(
                                            TAG,
                                            1,
                                            new Throwable(abstractRespuesta.getMsjError()),
                                            abstractRespuesta.getMsjError()
                                    );
                                }
                            }, (Throwable throwable) -> {
                                activity.ocultaProgressDialog();
                                activity.despliegaModal(
                                        true,
                                        false,
                                        literal.getValor(),
                                        throwable.getMessage(),
                                        () -> {
                                            hideViewElements(binding.clContenedor);
                                            ((HomeActivity) activity).regresaMenu();
                                        });
                                LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                            });

            transaccion.withFields(fields);
            transaccion.withProcod(operacion.getProducto());
            LOGGER.info(TAG, datosOperacion.toString());
            Logger.LOGGER.info(TAG, datosOperacion.toString());
            activity.muestraProgressDialog(getString(R.string.cargando));
            transaccion.realizarOperacion();
        }
    }


    private void obtenerRecycler() {
        final RelativeLayout formularioLayout =
                (RelativeLayout) binding.llFormulario.getChildAt(0);
        final ScrollView scrollView = (ScrollView) formularioLayout.getChildAt(0);
        final LinearLayout linearLayout = (LinearLayout) scrollView.getChildAt(0);
        RecyclerView recyclerView = new RecyclerView(getContext());

        for (int index = 0; index < linearLayout.getChildCount(); ++index) {
            if (linearLayout.getChildAt(index) instanceof RecyclerView) {

                recyclerView = (RecyclerView) linearLayout.getChildAt(index);

            }
        }

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(new RecargasAdapter(getMontos(), new RecargasAdapter.BtnClickListener() {
            @Override
            public void onBtnClick(String monto) {
                if (monto.equals("Otro Monto")) {
                    binding.etMontoServicio.getEditText().setEnabled(true);
                    binding.etMontoServicio.getEditText().requestFocus();
                    binding.etMontoServicio.getEditText().setFocusableInTouchMode(true);
                    binding.etMontoServicio.setMonto("");

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.etMontoServicio.getEditText(), InputMethodManager.SHOW_FORCED);
                } else {
                    binding.etMontoServicio.setMonto(monto);
                }
            }
        }));
    }

    //se obtiene el tipo de en Recargas
    private String getSubmenuType(Formulario formulario, Productos productos) {
        if (productos.getCategoria() == ProductCategory.OTRAS_RECARGAS) {
            return "pines";
        } else if (formularioHasInput(formulario)) {
            return "recargas";
        } else {
            return "paquetes";
        }
    }

    private ArrayList<String> getMontos() {
        String stringAmounts = SigmaBdManager.getParametroFijo(
                "0028",
                new BasicOnFailureListener(TAG, "ErroAlConsultar")
        );

        return stringToArrayyList(stringAmounts);
    }

    private ArrayList<String> stringToArrayyList(String str) {
        ArrayList<String> result = new ArrayList<>();

        if (str != null) {
            String[] strArray = str.split(",");
            Collections.addAll(result, strArray);
        }

        result.add("Otro Monto");

        return result;
    }

    private void showErrorDialog(final String cuerpo) {
        if (activity != null && activity instanceof HomeActivity) {
            PDSErrorDialog pdsErrorDialog = new PDSErrorDialog(cuerpo, (HomeActivity) activity);
            pdsErrorDialog.show(activity.getSupportFragmentManager(), "Error dialog");
        }
    }

    //verifica si el formularo tiene un elemento para ingresar el importe
    private boolean formularioHasInput(Formulario formulario) {
        if (formulario != null) {
            for (final Parametro param : formulario.getParametros()) {
                if (param.getFormato().getTipo() == Formato.Tipo.IMPORTE) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getdecimalSeparator() {

        return SigmaBdManager.getParametroFijo("0024", new BasicOnFailureListener(TAG, "Error formato"));

    }

    private boolean getValidationLimit(final int lengthPhone) {

        switch (Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo())) {
            case COLOMBIA: {
                return lengthPhone == FormularioLayoutNew.getMax();
            }
            case PERU: {
                return lengthPhone == FormularioLayoutNew.getMax();
            }
            default:
                return false;
        }
    }

    private Boolean inputIsEmpty(String campo){
        if(campo.length() == 0 && campo.equals("")){
            return false;
        }else{
            return true;
        }
    }
}