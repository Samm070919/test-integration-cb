package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInCardDataBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroCoF;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.cardfragment.DialogAutorizaCVV;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosTarjetaCoFBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.InfoPagoRegistrado;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroCoFInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TransactionService;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKitCoF;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.NetworkUtils;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


public class MoneyInByCardFragment extends AbstractMoney {

    private FragmentMoneyInCardDataBinding binding;
    private final String TAG = getClass().getSimpleName();
    DatosCompraKitCoF datosCompra = new DatosCompraKitCoF();
    public static final String OBJECT_NAME = "infoCompra";
    private String importe = "0";

    public MoneyInByCardFragment() {
    }

    public MoneyInByCardFragment(String importe) {
        this.importe = importe;
    }


    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NotNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_money_in_card_data, container, false);
        return binding.getRoot();
    }

    private void initUI() {
        BigDecimal bigDecimal = new BigDecimal(0.00);
        try {
            bigDecimal = new BigDecimal(importe);
        }catch (Exception e){
            Logger.LOGGER.info(TAG, e.getMessage());
        }

        binding.etMontoServicio.setMonto(Utilities.getFormatedImportObject(bigDecimal));
        int numDecimales = Country.getCountry(MposApplication.getInstance().getDatosLogin().getPais().getCodigo()).getNumDecimales();
        binding.etMontoServicio.setMaxLenght( binding.etMontoServicio.getMaxLenght() + numDecimales);
        checkCards();
        binding.btnMoneyInCard.setOnClickListener(v -> {
            String monto = binding.etMontoServicio.getEditText().getText().toString();
            monto = UtilsKt.cleanAmount(monto);
            BigDecimal num = new BigDecimal(monto);
            if(num.compareTo(new BigDecimal("0")) > 0) {
                registraPago(monto);
            }else{
                ((HomeActivity) getActivity()).despliegaModal(true, false, "Error en la Operacion", getString(R.string.empty), () -> {
                    //No implementation

                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    void checkCards() {
        ((HomeActivity) getActivity()).muestraProgressDialog("Procesando");
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
                ((HomeActivity) getActivity()).ocultaProgressDialog();
                activity.despliegaModal(true, false, getString(R.string.operacion_pci), getString(R.string.error_tipoOperacion), () -> ((HomeActivity) getActivity()).restauraHome());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(getActivity(), "message error", Toast.LENGTH_SHORT).show();
                ((HomeActivity) getActivity()).ocultaProgressDialog();

                activity.despliegaModal(true, false, getString(R.string.operacion_pci), getString(R.string.error_tipoOperacion), () -> ((HomeActivity) getActivity()).restauraHome());
            }

            @Override
            public void onSuccess(List<DatosTarjetaCoFBean> result) {
                if (result.size() > 0) {
                    binding.tvCardNumber.setText("XXXX XXXX XXXX " + result.get(0).getNumero4ultimos());
                    if (result.get(0).getMarca().toLowerCase().equals(Constantes.TARJETA_VISA.toLowerCase())) {
                        binding.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_visa));
                    } else if (result.get(0).getMarca().toLowerCase().equals(Constantes.TARJETA_MASTER_CARD.toLowerCase())){
                        binding.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_mastercard));
                    }else if (result.get(0).getMarca().toLowerCase().equals(Constantes.TARJETA_AMEX.toLowerCase())){
                        binding.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_amex_bg));
                    }else {
                        binding.rlCard.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_bg_card_step1));
                    }

                    datosCompra.setIdtarjeta(result.get(0).getIdtarjeta());
                } else {
                    showDialog(R.layout.layout_alta_tarjeta, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                        @Override
                        public void onCancel() {
                            ((HomeActivity) getActivity()).restauraHome();
                        }

                        @Override
                        public void onAccept() {

                            Intent intent = new Intent(getActivity(), RegistroCoF.class);
                            intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_ADD_CARD_BY_MENU);
                            startActivityForResult(intent, 2);// Activity is started with requestCode 2
                        }
                    });
                }
                ((HomeActivity) getActivity()).ocultaProgressDialog();
            }
        });
    }


    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(true);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
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


    private void registraPago(String importe) {
        ((HomeActivity) getActivity()).muestraProgressDialog(getResources().getString(R.string.Operando));
        final TransactionService registraService = new TransactionService();
        registraService.postTransactionMoneyInBycard(
                ApiData.APIDATA.getDatosSesion().getIdSesion(),
                ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod(),
                importe,
                Constantes.FORMA_PAGO_COF
                , new RetrofitListener<InfoPagoRegistrado>() {
                    @Override
                    public void showMessage(String s) {
                        // None
                        ((HomeActivity) getActivity()).ocultaProgressDialog();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Logger.LOGGER.throwing("", 1, throwable, throwable.getMessage());
                        ((HomeActivity) getActivity()).ocultaProgressDialog();
                        ((HomeActivity) getActivity()).despliegaModal(true, false, "Error en la Operacion", throwable.getMessage(), () -> {
                            //No implementation

                        });
                    }

                    @Override
                    public void onSuccess(InfoPagoRegistrado infoPagoRegistrado) {
                        datosCompra.setIdPago(String.valueOf(infoPagoRegistrado.getIdPago()));
                        datosCompra.setImporte(String.valueOf(infoPagoRegistrado.getImporte()));
                        ((HomeActivity) getActivity()).ocultaProgressDialog();
                        DialogAutorizaCVV dialogAutorizaCvv = new DialogAutorizaCVV(dialogListener, getString(R.string.cvv_to_autorice));
                        dialogAutorizaCvv.show(getFragmentManager(), "");
                    }
                });
    }

    private void cargarFragmentPagoStatus(InfoCompraKitCoF infoCompra) {
        final MoneyInByCardStatusFragment fragmentResult = new MoneyInByCardStatusFragment();
        //fragmentPse.setListener(getListener());
        Bundle args = new Bundle();
        args.putSerializable(OBJECT_NAME, infoCompra);
        fragmentResult.setArguments(args);
        ((HomeActivity) getActivity()).cargarFragmentsCuenta(View.GONE, ((HomeActivity) getActivity()).generaListener(fragmentResult));

    }

    private void pagoCoF() {
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlcnp();
        final RegistroCoFInteractor kitService = new RegistroCoFInteractor(url);

        kitService.comptraImporteCoF(datosCompra,
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
                        ((HomeActivity) getActivity()).ocultaProgressDialog();
                        ((HomeActivity) getActivity()).despliegaModal(true, false, "Error en la Operacion", throwable.getMessage(), () -> {
                            //No implementation

                        });
                    }

                    @Override
                    public void onSuccess(InfoCompraKitCoF infoCompraKitCof) {
                        ((HomeActivity) getActivity()).ocultaProgressDialog();
                        Logger.LOGGER.fine(TAG, new Gson().toJson(infoCompraKitCof));
                        switch (infoCompraKitCof.getDescripcion()) {
                            case "APROBADO":
                                Utilities.guardarSigmaTransacciones(String.valueOf(infoCompraKitCof.getImportesaldo()),
                                        infoCompraKitCof.getTransactionid(),
                                        "**** " + infoCompraKitCof.getNumero4ultimos(),
                                        infoCompraKitCof.getAuthorizationcode(),
                                        "RecargaTarjeta",
                                        "Tarjeta COF");
                                cargarFragmentPagoStatus(infoCompraKitCof);
                                break;
                            case "RECHAZADO":
                                ((HomeActivity) getActivity()).despliegaModal(true, false, getString(R.string.generic_error), "La Operación se Rechazó", () -> {
                                    backPreviousFragment();
                                });
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private DialogAutorizaCVV.OnCVVGiven dialogListener = new DialogAutorizaCVV.OnCVVGiven() {
        @Override
        public void cvvGiven(@NotNull String cvv) {
            ((HomeActivity) getActivity()).muestraProgressDialog(getResources().getString(R.string.Operando));
            datosCompra.setCvv(cvv);
            String ip = NetworkUtils.getLocalIpAddress();
            datosCompra.setIp(ip);

            pagoCoF();
        }
    };
}
