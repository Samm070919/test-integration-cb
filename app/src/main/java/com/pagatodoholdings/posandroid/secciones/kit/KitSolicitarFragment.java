package com.pagatodoholdings.posandroid.secciones.kit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.google.gson.Gson;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.FragmentKitSolicitarBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCoF;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosLogin;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.DialogAutorizaCVV;
import com.pagatodoholdings.posandroid.secciones.retrofit.CompraKitService;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosTarjetaCoFBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.MisDatosKitService;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroCoFInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKitCoF;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.ImporteBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.ImporteKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.Productos;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.NetworkUtils;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import net.fullcarga.android.api.sesion.DatosSesion;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import static com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity.DESHABILITA_BACK;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;
import static net.fullcarga.android.api.oper.TipoOperacion.LOGIN;

public class KitSolicitarFragment extends AbstractStepFragment {

    //Bundle variable
    public static final String KITDATA_SEND = "KITDATA_SEND";
    private static final String PAIS = "PAIS";
    private static final String IMPORTE_SELECCIONADO = "IMPORTE_SELECCIONADO";
    //

    final String TAG = this.getClass().getSimpleName();
    FragmentKitSolicitarBinding binding;
    private AbstractActivity homeActivity;
    private MisDatosKitService misDatosKitService;
    private DatosCompraKit datosCompraKit;
    private DatosCompraKitCoF datosCompraKitCoF = new DatosCompraKitCoF();
    private SolicitaKitInfo solicitaKitInfoListener;
    BigDecimal importe = new BigDecimal(0.0);
    BigDecimal total = new BigDecimal(0.0);
    boolean buyByCard = false;
    private int idCallingActivity = 0;
    AlertDialog alertDialog;

    public KitSolicitarFragment() {
    }

    public KitSolicitarFragment(SolicitaKitInfo solicitaKitInfo, int idCallingActivity) {
        this.solicitaKitInfoListener = solicitaKitInfo;
        this.idCallingActivity = idCallingActivity;
    }

    public void setDatosCompraKit(DatosCompraKit datosCompraKit, AbstractActivity activity) {
        this.datosCompraKit = datosCompraKit;
        this.homeActivity = activity;
        datosCompraKit.getProductos().add(new Productos());
        if (homeActivity instanceof KitSolicitarActivity) {
            obtenerCostoKit();
        }
    }

    public void setDatosCompraKit(DatosCompraKit datosCompraKit) {
        this.datosCompraKit = datosCompraKit;
        datosCompraKit.getProductos().add(new Productos());
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kit_solicitar, container, false);
        //initUI(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeActivity = (AbstractActivity) getActivity();

        //  obtenerCostoKit();
        binding.ivClose.setOnClickListener(v -> closeFragment(KitSolicitarFragment.this));
        if(!Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).isPSE()){
            binding.btnMediox.setVisibility(View.GONE);
        }

        binding.btnMediox.setOnClickListener(v -> {
            buyByCard = false;
            datosCompraKit.setFormapago(Constantes.FORMA_PAGO_PSE_CAD);
            checkSesion();
        });
        binding.btnComprarkit.setOnClickListener(v -> {
            if(datosCompraKit != null && datosCompraKit.getImporte() != null) {
                buyByCard = true;
                datosCompraKit.setFormapago(Constantes.FORMA_PAGO_COF);
                checkCards();
            }else{
                getActivity().onBackPressed();
            }
        });

        binding.tvDespues.setOnClickListener(v -> {
            if (idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
               advanceToDatosNegocio();
            } else {
                getActivity().finish();
            }
        });

        binding.ivClose.setOnClickListener(view1 -> {
            if (idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
                closeFragment(this);
            } else {
                getActivity().finish();
            }

        });


    }

    private void advanceToDatosNegocio() {
        final Intent intent = new Intent(getActivity(), RegistroDatosNegocio.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(DESHABILITA_BACK, true);
        startActivity(intent);
        getActivity().finish();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (homeActivity instanceof HomeActivity)
            obtenerCostoKit();
    }


    private void obtenerCostoKit() {
        homeActivity.muestraProgressDialog(getResources().getString(R.string.cargando));

        misDatosKitService = new MisDatosKitService();
        //Validar si existen los datos de compra del kit
        if (datosCompraKit != null) {
            ImporteBean importeBean = new ImporteBean();

            importeBean.setNivel2(datosCompraKit.getNivel2());
            importeBean.setNivel3(datosCompraKit.getNivel3());
            importeBean.getProductos().add(new Productos());

            misDatosKitService.consultarImporteKit(new RetrofitListener<ImporteKit>() {
                @Override
                public void showMessage(String s) {
                    //No implementation
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Logger.LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                    homeActivity.ocultaProgressDialog();

                    homeActivity.despliegaModal(true, false, getResources().getString(R.string.generic_error),
                            UtilsKt.capitalizeWords(throwable.getMessage()), () -> {
                             getActivity().onBackPressed();
                            });

                }

                @Override
                public void onSuccess(ImporteKit importeKit) {
                    homeActivity.ocultaProgressDialog();
                    //Formatear el precio del Kit
                    importe = new BigDecimal(importeKit.getImporte());
                    datosCompraKit.setImporte(importeKit.getImporte());
                    datosCompraKit.setGastosEnvio(importeKit.getGastosEnvio());
                    if (importe.compareTo(new BigDecimal(0.0)) > 0) {
                        if(MposApplication.getInstance().getDatosLogin().getPais().getCodigo().equals("170")) {
                            binding.tvPromoEnvio.setMonto("49000");
                            total = importe.add(new BigDecimal(importeKit.getGastosEnvio()));
                            String totalStr = String.valueOf(Math.round(total.doubleValue()));
                            totalStr = addPoint(totalStr, '.');
                            binding.tvNuevoDispositivo.setMonto("199000");
                            binding.tvCostoDispositivo.setMonto("199000");
                            binding.tvEnvioDispositivo.setMonto(importeKit.getGastosEnvio());
                            binding.tvTotalDispositivo.setMonto(totalStr);
                        }else{
                            BigDecimal envio = (new BigDecimal(importeKit.getGastosEnvio()).setScale(0, RoundingMode.CEILING));
                            BigDecimal promo = (new BigDecimal("76"));
                            binding.tvPromoEnvio.setMonto(UtilsKt.cleanAmount(SigmaBdManager.formatoSaldo(promo, new BasicOnFailureListener(TAG, "Error de formato"))));
                            BigDecimal costo = importe.add(promo);
                            total = importe.add(new BigDecimal(importeKit.getGastosEnvio()));
                            binding.tvNuevoDispositivo.setMonto(UtilsKt.cleanAmount(SigmaBdManager.formatoSaldo(costo, new BasicOnFailureListener(TAG, "Error de formato"))));
                            binding.tvCostoDispositivo.setMonto(UtilsKt.cleanAmount(SigmaBdManager.formatoSaldo(costo, new BasicOnFailureListener(TAG, "Error de formato"))));
                            binding.tvEnvioDispositivo.setMonto(envio.toString());
                            binding.tvTotalDispositivo.setMonto(UtilsKt.cleanAmount(SigmaBdManager.formatoSaldo(total, new BasicOnFailureListener(TAG, "Error de formato"))));
                        }

                    } else {
                        if (homeActivity instanceof HomeActivity) {
                            homeActivity.despliegaModal(true, false, getResources().getString(R.string.generic_error),
                                    getResources().getString(R.string.kit_error_datos_negocio), () -> ((HomeActivity) homeActivity).regresaMenu());
                        }
                    }

                }
            }, importeBean);
        } else {
            homeActivity.ocultaProgressDialog();
            if (homeActivity instanceof HomeActivity)
                homeActivity.despliegaModal(true, false, getResources().getString(R.string.generic_error), getResources().getString(R.string.kit_error_datos_negocio), () -> ((HomeActivity) homeActivity).regresaMenu());

        }
    }

    private void regresaKitDatosEnvioFragment() {
        final KitDatosEnvioEmptyFragment kitDatosEnvioFragment = new KitDatosEnvioEmptyFragment(null);
        if (homeActivity instanceof HomeActivity) {
            homeActivity.getSupportFragmentManager().beginTransaction().
                    replace(((HomeActivity) homeActivity).getBinding().flMainPantallaCompleta.getId(), kitDatosEnvioFragment).commit();
        } else {

        }
    }


    void checkSesion() {
        if (ApiData.APIDATA.getDatosSesion() != null) {
            comprakit();
        } else {
            iniciaSesionSigma();
        }
    }

    private void iniciaSesionSigma() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        homeActivity.muestraProgressDialog(getResources().getString(R.string.Operando));
        DatosSesion datosSesion = null;
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        if (datosLogin == null) {
            return;
        }

        try {
            datosSesion = build();
        } catch (SQLException exe) {
            LOGGER.throwing(TAG, 1, exe, exe.getMessage());
        }

        enviarLogin(datosSesion);

    }

    private void enviarLogin(final DatosSesion datosSesion) {//NOSONAR
        ApiData.APIDATA.setDatosSesion(datosSesion);
        TransaccionFactory.crearTransacion(LOGIN, abstractRespuesta -> {
            if (abstractRespuesta.isCorrecta()) {
                comprakit();
            }
        }, throwable -> {
            homeActivity.ocultaProgressDialog();
        }).realizarOperacion();
    }


    private void comprakit() {
        homeActivity.muestraProgressDialog(getResources().getString(R.string.Operando));
        final CompraKitService kitService = new CompraKitService();
        kitService.comptraImporteKit(datosCompraKit,
                ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod()
                , new RetrofitListener<InfoCompraKit>() {
                    @Override
                    public void showMessage(String s) {
                        // None
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Logger.LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                        homeActivity.ocultaProgressDialog();
                        homeActivity.despliegaModal(true, false, "Error en la Operacion", UtilsKt.capitalizeWords(throwable.getMessage()), () -> {
                            //No implementation
                            if(idCallingActivity != Constantes.NON_ACTIVITY_IS_CALLING)
                                ((AbstractActivity) getActivity()).restauraHome();
                            else{
                                advanceToDatosNegocio();
                            }

                        });
                    }

                    @Override
                    public void onSuccess(InfoCompraKit infoCompraKit) {
                        homeActivity.ocultaProgressDialog();
                        Logger.LOGGER.fine(TAG, new Gson().toJson(infoCompraKit));
                        datosCompraKitCoF.setIdPago(infoCompraKit.getIdPago());
                        AdjustEvent adjustEvent = new AdjustEvent("pt8lxn");
                        Adjust.trackEvent(adjustEvent);

                        if (buyByCard) {
                            DialogAutorizaCVV dialogAutorizaCvv = new DialogAutorizaCVV(dialogListener, "");
                            dialogAutorizaCvv.show(getFragmentManager(), "");
                        } else {
                            solicitaKitInfoListener.kitSolicitadoPSE(datosCompraKit, infoCompraKit, idCallingActivity);
                        }/*else if(idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
                                finalizeTransaction(infoCompraKit);
                            }else{
                                finalizeTransaction(infoCompraKit, idCallingActivity);
                            }*/
                    }
                });
    }

    private void comprakitCoF() {
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlcnp();
        final RegistroCoFInteractor kitService = new RegistroCoFInteractor(url);

        kitService.comptraImporteCoF(datosCompraKitCoF,
                MposApplication.getInstance().getDatosLogin().getPais().getCodigo(),
                MposApplication.getInstance().getDatosLogin().getToken(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(),
                MposApplication.getInstance().getDatosLogin().getCliente().getEmail()
                , new RetrofitListener<InfoCompraKitCoF>() {
                    @Override
                    public void showMessage(String s) {
                        // None
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Logger.LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                        homeActivity.ocultaProgressDialog();
                        homeActivity.despliegaModal(true, false, "Error en la Operacion", UtilsKt.capitalizeWords(throwable.getMessage()), () -> {
                            //No implementation

                        });
                    }

                    @Override
                    public void onSuccess(InfoCompraKitCoF infoCompraKitCof) {
                        homeActivity.ocultaProgressDialog();
                        Logger.LOGGER.fine(TAG, new Gson().toJson(infoCompraKitCof));
                        switch (infoCompraKitCof.getDescripcion()) {
                            case "APROBADO":
                                AdjustEvent adjustEvent = new AdjustEvent("rrr9vu");
                                Adjust.trackEvent(adjustEvent);
                                Utilities.guardarSigmaTransacciones(String.valueOf(infoCompraKitCof.getImportesaldo()),
                                        infoCompraKitCof.getTransactionid(),
                                        "**** " + infoCompraKitCof.getNumero4ultimos(),
                                        infoCompraKitCof.getAuthorizationcode(),
                                        "CompraKit",
                                        "Tarjeta COF");
                                if (homeActivity instanceof KitSolicitarActivity) {
                                    if(solicitaKitInfoListener != null) {
                                        solicitaKitInfoListener.kitSolicitado(datosCompraKit, infoCompraKitCof);
                                    }else{
                                        finalizeTransaction(datosCompraKit, infoCompraKitCof);
                                    }
                                } else {
                                    finalizeTransaction(datosCompraKit, infoCompraKitCof);
                                }
                                break;
                            case "RECHAZADO":
                                AdjustEvent adjustEventFail = new AdjustEvent("1qypeq");
                                Adjust.trackEvent(adjustEventFail);
                                ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.generic_error), "La Operación se Rechazó", () -> {
                                    if(idCallingActivity != Constantes.NON_ACTIVITY_IS_CALLING)
                                        ((AbstractActivity) getActivity()).restauraHome();
                                    else{
                                        advanceToDatosNegocio();
                                    }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });
    }


    private void finalizeTransaction(DatosCompraKit datosCompraKit, InfoCompraKitCoF infoCompraKitCoF) {
        final KitTransaccionResult kitSolicitarFragment = new KitTransaccionResult(0);
        kitSolicitarFragment.setInfoResult(datosCompraKit, infoCompraKitCoF, homeActivity, false);
        getActivity().getSupportFragmentManager().beginTransaction().
                replace(((KitSolicitarActivity) homeActivity).getBinding().cvContainer.getId(), kitSolicitarFragment).commit();
    }

    void checkCards() {

        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlcnp();
        String pais = MposApplication.getInstance().getDatosLogin().getPais().getCodigo();
        String usuario = MposApplication.getInstance().getDatosLogin().getCliente().getEmail();
        final String tpvcod = MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod();
        final String token = MposApplication.getInstance().getDatosLogin().getToken();
        RegistroCoFInteractor registroRetrofit = new RegistroCoFInteractor(url);
        registroRetrofit.getTarjetas(token, tpvcod, pais, usuario, new RetrofitListener<List<DatosTarjetaCoFBean>>() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<DatosTarjetaCoFBean> result) {
                if (result.size() > 0) {
                    datosCompraKitCoF.setIdtarjeta(result.get(0).getIdtarjeta());
                    BigDecimal costoKit = new BigDecimal(String.valueOf(datosCompraKit.getImporte()));
                    BigDecimal envio = new BigDecimal(datosCompraKit.getGastosEnvio());
                    BigDecimal total = costoKit.add(envio);
                    datosCompraKitCoF.setImporte(total.toString());
                    checkSesion();
                } else {
                    showDialog(R.layout.layout_alta_tarjeta, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                        @Override
                        public void onCancel() {
                            //No implementation
                            if (idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
                                ((AbstractActivity) getActivity()).restauraHome();
                            } else {
                                if (alertDialog != null)
                                    alertDialog.dismiss();
                            }
                        }

                        @Override
                        public void onAccept() {
                            //if (idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING)
                              //  ((AbstractActivity) getActivity()).cambiaDeActividadSinCerrar(RegistroCoF.class);
                            //else {
                                Intent intent = new Intent(getActivity(), RegistroCoF.class);
                                intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_ADD_CARD_BY_COMPRA_KIT);
                                startActivityForResult(intent, 2);// Activity is started with requestCode 2
                            //}
                        }
                    });
                }
            }
        });
    }

    public void showDialog(int layout,
                           ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(true);
        alert.setView(view);
        alertDialog = alert.create();
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final ImageView ivClose = view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(view1 -> alertDialog.dismiss());
        final BotonClickUnico btnVincular = view.findViewById(R.id.btnConfirmacion);
        btnVincular.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });

        final BotonClickUnico btnCancelar = view.findViewById(R.id.btnCancel);
        btnCancelar.setOnClickListener(view1 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private DialogAutorizaCVV.OnCVVGiven dialogListener = new DialogAutorizaCVV.OnCVVGiven() {
        @Override
        public void cvvGiven(@NotNull String cvv) {
            homeActivity.muestraProgressDialog(getResources().getString(R.string.Operando));
            datosCompraKitCoF.setCvv(cvv);
            String ip = NetworkUtils.getLocalIpAddress();
            datosCompraKitCoF.setIp(ip);
            comprakitCoF();
        }
    };


    protected void closeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else if (homeActivity instanceof HomeActivity) {
            ((HomeActivity) homeActivity).regresaMenu();
        } else {
            homeActivity.onBackPressed();
        }
    }

    public String addPoint(String str, char ch) {
        if (str.length() > 3) {
            int position = str.length() - 3;
            return str.substring(0, position) + ch + str.substring(position);
        } else
            return str;
    }
}