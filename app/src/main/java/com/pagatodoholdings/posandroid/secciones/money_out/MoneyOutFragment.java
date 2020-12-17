package com.pagatodoholdings.posandroid.secciones.money_out;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoViewController;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyOutBinding;
import com.pagatodoholdings.posandroid.secciones.money_in.AbstractMoney;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroAgregaCuentaActivity;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosCuentaBancaria;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosDesfogue;
import com.pagatodoholdings.posandroid.secciones.retrofit.MisDatosService;
import com.pagatodoholdings.posandroid.secciones.retrofit.MoneyOutImporteTransaction;
import com.pagatodoholdings.posandroid.secciones.retrofit.TransactionService;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoneyOutFragment extends AbstractMoney {


    private FragmentMoneyOutBinding binding;
    private MisDatosService misDatosService;
    private BigDecimal saldoDisponible;
    private DatosCuentaBancaria datosCuenta;
    private BigDecimal importeRetirar;
    private TransactionService transactionService;

    public MoneyOutFragment() {
        // Required empty public constructor
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater inflater, final ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_money_out, container, false);
        misDatosService = new MisDatosService();
        transactionService = new TransactionService();
        cargarDatosCuenta();
        cargarSaldoDisponible();
        setMonto("0");
        binding.ivOnBack.setOnClickListener(v -> close());

        binding.btnContinuar.setOnClickListener(v -> confirmMoneyOut());
    }

    private void setMonto(String total)
    {
        binding.mvcMontoRetiro.setMonto(total); ;
    }

    private void cargarSaldoDisponible()
    {
        if(getListener().getSaldo()!=null)
        {
            saldoDisponible = getListener().getSaldo();
        }else {
            saldoDisponible = new BigDecimal("0");
        }

        TextView tvFooterSaldo = binding.lImporte.findViewById(R.id.tv_subtitle);
        tvFooterSaldo.setText(getResources().getString(R.string.wallet_Saldo));
        tvFooterSaldo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorGreyFormulario));

        MontoViewController mvSaldoDisponible = binding.lImporte.findViewById(R.id.mv_importe);
        mvSaldoDisponible.setNewTextSizeIndividualMonto(15f,30f,15f);
        mvSaldoDisponible.setColorMonto(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mvSaldoDisponible.setMonto(SigmaBdManager.formatoSaldo(saldoDisponible, new BasicOnFailureListener(TAG,"Error de formato")));
    }

    private void cargarDatosCuenta(){
        getListener().muestraProgressDialog("Verificando Datos Bancarios");
        misDatosService.getDatosCuentaBancaria(new RetrofitListener<DatosCuentaBancaria>() {
                                                   private void onAccept() {
                                                       close();
                                                   }

                                                   @Override
                                                   public void showMessage(String message) {
                                                       //No implementation
                                                   }

                                                   @Override
                                                   public void onFailure(Throwable throwable) {
                                                       Logger.LOGGER.throwing("DATOS Cuenta Bancaria", 1, throwable, "Error al consultar los Datos de Cuenta Bancaria");
                                                       getListener().ocultaProgressDialog();
                                                       getListener().despliegaModal(true, false, getResources().getString(R.string.generic_error),
                                                               "Error al obtener los Datos de cuenta Bancaria", this::onAccept);                                                   }

                                                   @Override
                                                   public void onSuccess(DatosCuentaBancaria datosCuentaBancaria) {
                                                       if(datosCuentaBancaria!=null)
                                                       {

                                                           if(datosCuentaBancaria.getCtabancaria() != null && !datosCuentaBancaria.isCtavalidada())
                                                           {
                                                               getListener().ocultaProgressDialog();
                                                               getListener().despliegaModal(true, false, getResources().getString(R.string.generic_error),
                                                                       "La Cuenta Bancaria No Se Encuentra Validada.\nVerificar en Información Bancaria", () ->
                                                                               loadMiCuenta(MoneyOutFragment.this));
                                                           }else if(datosCuentaBancaria.getCtabancaria() == null) {
                                                               getListener().ocultaProgressDialog();
                                                               Intent intent = new Intent(getActivity(), RegistroAgregaCuentaActivity.class);
                                                               getActivity().startActivity(intent);
                                                               onAccept();
                                                               return;
                                                           }

                                                           datosCuenta = datosCuentaBancaria;
                                                           binding.tvBanco.setText("SIN DATO BANCO");
                                                           if(datosCuentaBancaria.getBanco()!=null) {
                                                               binding.tvBanco.setText(UtilsKt.capitalizeWords(datosCuentaBancaria.getBanco()));
                                                           }

                                                           binding.tvCtaContent.setText("SIN DATO CUENTA");
                                                           if(datosCuentaBancaria.getCtabancaria()!=null && datosCuentaBancaria.getCtabancaria().length()>7) {
                                                               binding.tvCtaTitle.setText(getResources().getString(R.string.money_out_title_cta) + " " +datosCuentaBancaria.getTipoctadescripcion());
                                                               binding.tvCtaContent.setText(mascaraCuentaBancaria(datosCuentaBancaria.getCtabancaria()));
                                                           }

                                                           getListener().ocultaProgressDialog();
                                                       }else{
                                                           final Intent intent = new Intent(activity, RegistroAgregaCuentaActivity.class);
                                                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                           startActivity(intent);
                                                       }
                                                   }
                                               }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());

    }

    private String mascaraCuentaBancaria(String cuentaBancaria)
    {
       String ctaMascara;
       ctaMascara = "*" + cuentaBancaria.substring(cuentaBancaria.length()-4);
       return ctaMascara;
    }

    private void confirmMoneyOut() {
        String monto = binding.mvcMontoRetiro.getEditText().getText().toString();
        monto = UtilsKt.cleanAmount(monto);
        BigDecimal num = new BigDecimal(monto);
        importeRetirar = num;

        if(importeRetirar.compareTo(BigDecimal.ZERO) > 0 )
        {
                confirmTransaction();
        }else{
            BigDecimal importeMin = new BigDecimal("0");
            getListener().despliegaModal(true, false, getResources().getString(R.string.generic_error), "El monto debe ser mayor a "
                    + SigmaBdManager.formatoSaldo(importeMin, new BasicOnFailureListener(TAG,"No se puede mostrar Importe")), () -> {
                        //No implementation
            });
        }
    }

    private void confirmTransaction()
    {
        MoneyOutImporteTransaction objectImporte=new MoneyOutImporteTransaction();
        objectImporte.setImporte(importeRetirar.toPlainString());
        getListener().muestraProgressDialog("Confirmando Transacción");
        transactionService.postTransactionMoneyOutResponse(new RetrofitListener() {
                                                               @Override
                                                               public void showMessage(String message) {
                                                                   //No implementation
                                                               }

                                                               @Override
                                                               public void onFailure(Throwable throwable) {
                                                                   Logger.LOGGER.throwing("ResponseTransaction", 1, throwable, "Error Response Transaction MoneyOut");
                                                                   getListener().ocultaProgressDialog();
                                                                   getListener().despliegaModal(true, false, "Error", "Error al confirmar la transacción: "
                                                                           + throwable.getMessage(), () -> {
                                                                       //No implementation
                                                                   });
                                                               }

                                                               @Override
                                                               public void onSuccess(Object result) {
                                                                   if (result != null) {
                                                                    try  {
                                                                         DatosDesfogue datos = (DatosDesfogue)result;

                                                                        Utilities.guardarSigmaTransacciones(datos.getImporte().toPlainString(),
                                                                                datosCuenta.getDocid() ,
                                                                                mascaraCuentaBancaria(datosCuenta.getCtabancaria()),
                                                                                String.valueOf(datos.getIdreembolso()),
                                                                                "SalidaDinero",
                                                                                "Salida de Dinero");
                                                                    }
                                                                    catch (ClassCastException exe ){
                                                                        getListener().despliegaModal(true, false,
                                                                                "Error", "Error al Guardar la TRX", () -> {
                                                                                    //No implementation
                                                                                });
                                                                    }

                                                                       AdjustEvent adjustEvent = new AdjustEvent("hw4617");
                                                                       Adjust.trackEvent(adjustEvent);
                                                                       getListener().ocultaProgressDialog();
                                                                       loadMoneyOutTransaction();

                                                                   } else {
                                                                       getListener().despliegaModal(true, false,
                                                                               "Error", "Error al confirmar la transacción: Result NULL", () -> {
                                                                                   //No implementation
                                                                               });
                                                                   }
                                                               }
                                                           }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(),
                objectImporte);
        }



    private void loadMoneyOutTransaction()
    {
        final MoneyOutTransactionFragment fragmentMoneyOutTransaction = new MoneyOutTransactionFragment();
        fragmentMoneyOutTransaction.setListener(getListener());
        fragmentMoneyOutTransaction.setDatosConfirmacion(datosCuenta, importeRetirar.toString());
        getListener().cargarFragments(View.GONE,getListener().generaListener(fragmentMoneyOutTransaction));
    }

    private void close() {
        hideViewElements((ViewGroup) binding.getRoot());
        closeFragment(MoneyOutFragment.this);
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No definido
    }




}
