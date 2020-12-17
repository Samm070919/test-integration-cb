package com.pagatodoholdings.posandroid.secciones.sales.insertcard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.qposlib.QPosManager;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.emv.DecodeData;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.reportetrx.TransaccionesBD;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.InsertCardFragmentBinding;
import com.pagatodoholdings.posandroid.manager.AdqTransactionCallback;
import com.pagatodoholdings.posandroid.manager.DongleTransactionManager;
import com.pagatodoholdings.posandroid.secciones.acquiring.charge.ChargeDataSingleton;
import com.pagatodoholdings.posandroid.secciones.acquiring.deferred.DeferredPaymentPicker;
import com.pagatodoholdings.posandroid.secciones.dialogs.DniDialog;
import com.pagatodoholdings.posandroid.secciones.sales.PciSalesFragmentSupport;
import com.pagatodoholdings.posandroid.secciones.sales.binding.BreakdownData;
import com.pagatodoholdings.posandroid.secciones.ticket.EmailTicketFragment;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.enums.MediosPago;

import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Menu;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Operaciones;
import net.fullcarga.android.api.bd.sigma.generated.tables.pojos.Productos;
import net.fullcarga.android.api.data.respuesta.Respuesta;
import net.fullcarga.android.api.data.respuesta.RespuestaTrxCierreTurno;

import java.util.List;
import java.util.Objects;

public class InsertCardFragment extends PciSalesFragmentSupport<InsertCardFragmentBinding> implements AdqTransactionCallback, View.OnClickListener {

    private Menu menu;
    private DongleTransactionManager manager;
    private Productos productos;
    private List<Operaciones> operaciones;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.insert_card_fragment, container, false);
        init();
        return binding.getRoot();
    }

    @Override
    public void init() {
        activity.hideAllToolbar();
        manager = DongleTransactionManager.getInstance();
        manager.setActivity(activity);
        validateTransaction();
    }

    private void validateTransaction() {
        binding.tvTitle.setText(getResources().getString(R.string.insert_label));

        menu = ChargeDataSingleton.getInstance().getMenu();
        if (menu != null) {
            operaciones = SigmaBdManager.getOperacionesVisiblesPorProducto(menu.getProducto(),
                    new OnFailureListener.BasicOnFailureListener(Objects.requireNonNull(getActivity()).getClass().getSimpleName(), "Error al consultar operaciones"));
            productos = SigmaBdManager.getProducto(operaciones.get(0), throwable -> Logger.LOGGER.throwing(throwable.getMessage(), 1, throwable,
                    activity.getString(R.string.error_con_bd)));
            manager.setProducto(productos);
            manager.setListener(this);
        }

        initDues();
        executeTransaction();
    }

    private void resetQposManager() {

        QPosManager qPosManager = (QPosManager) MposApplication.getInstance().getPreferedDongle();
        if (qPosManager != null) {
            qPosManager.resetQPOS();
            qPosManager.closeCommunication();
        }
    }


    private void executeTransaction() {
        BreakdownData data = ChargeDataSingleton.getInstance().getBreakdownData();

        //Si data == null Significa que no se llenaron los campos por tanto es una devolución
        if (data != null) {
            manager.initTransaction(data.getAmount().subtract(data.getPropina()), data.getPropina(),
                    data.getImpuesto(), ChargeDataSingleton.getInstance().getReferenciaDevolucion());
        } else {
            manager.initTransaction(null, null, null, ChargeDataSingleton.getInstance().getReferenciaDevolucion());
        }
    }

    @Override
    public void showSolicitarNip() {
        //No implementation
    }


    @Override
    public void qposNoConectado() {
        activity.setBackEnabled(true);
    }

    @Override
    public void qposDesconectado() {
        resetQposManager();
    }

    @Override
    public void procesandoTransaccion() {
        binding.tvTitle.setText("Procesando");
        binding.progressCircularBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorDeOperacion(String mensaje) {
        getActivity().runOnUiThread(() -> {
            binding.progressCircularBar.setVisibility(View.GONE);
            activity.setBackEnabled(true);
            binding.tvTitle.setText(mensaje);
            binding.tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSoftRed));
        });
    }

    @Override
    public void qposNoResponse() {
        activity.setBackEnabled(true);
        Toast.makeText(activity.getApplicationContext(),
                "Se ha excedido el límite de tiempo para la transacción", Toast.LENGTH_LONG).show();
        activity.restauraHome();
    }

    @Override
    public void operacionCancelada() {
        resetQposManager();
        activity.finish();
    }


    @Override
    public void onSucces(final boolean reqFirma, @Nullable final Object response, @Nullable final DecodeData decodeData) {
        final Operaciones operacion = operaciones.get(0);

        DniDialog.DniInteractionListener listener = dni -> goToAndSendTicket(operacion, (Respuesta) response, reqFirma, dni, decodeData);

        if (reqFirma) {
            DniDialog dniDialog = new DniDialog(listener);
            dniDialog.setCancelable(false);
            dniDialog.show(requireActivity().getSupportFragmentManager(), DniDialog.Companion.getTAG());
        } else {
            listener.onDniRetrieved(null);
        }

        final RespuestaTrxCierreTurno restCierreTurno = (RespuestaTrxCierreTurno) response;
        TransaccionesBD datosOperacion = new TransaccionesBD();

        if (decodeData != null && decodeData.getMaskedPAN().length() > 4) {
            String last4 = decodeData.getMaskedPAN().substring(decodeData.getMaskedPAN().length() - 4);
            datosOperacion.setRefcliente("**" + last4);
        } else {
            datosOperacion.setRefcliente(restCierreTurno.getCamposCierreTurno().getRefCliente());
        }

        datosOperacion.setDescproducto(restCierreTurno.getCamposCierreTurno().getDescripcionProducto());
        datosOperacion.setImporte(restCierreTurno.getCamposCierreTurno().getImporte().toPlainString());
        datosOperacion.setReflocal(restCierreTurno.getCamposCierreTurno().getRefLocal());
        datosOperacion.setRefremota(restCierreTurno.getCamposCierreTurno().getRefRemota());
        datosOperacion.setMedioPago(operacion.getOperacion().equalsIgnoreCase(Constantes.DEVOLUCION)
                ? MediosPago.CANCELACION : MediosPago.TARJETA);
        Utilities.guardarSigmaTransacciones(datosOperacion, operacion, productos.getCierreTurno());
    }

    private void goToAndSendTicket(Operaciones operacion, Respuesta response, boolean reqFirma, String dni, @Nullable final DecodeData decodeData) {
        final EmailTicketFragment ticketFragment = EmailTicketFragment.newInstance(null, menu, operacion,
                response, reqFirma, decodeData, dni);
        ticketFragment.setFavAsSkipped(true);
        activity.getRouter().showEmailTicket(ticketFragment);
    }

    @Override
    public void requestCuotas() {
        if (MposApplication.getInstance().getDatosLogin().getPais().getCodigo()
                .equals(Country.COLOMBIA.getItemIsoCode())) {
            DeferredPaymentPicker deferredPaymentPicker = DeferredPaymentPicker.Companion.newInstance(productos.getPerfilEmv());
            deferredPaymentPicker.setCancelable(false);
            deferredPaymentPicker.setDeferredInteractionListener(fees -> manager.setCuotas(fees));
            deferredPaymentPicker.show(getActivity().getSupportFragmentManager(), "DeferredPaymentPicker");
        } else {
            binding.conDongle.setVisibility(View.GONE);
            binding.conDues.setVisibility(View.VISIBLE);
        }
    }

    public void initDues() {
        binding.includeDues.cardH.setOnClickListener(this);
        binding.includeDues.cardView3.setOnClickListener(this);
        binding.includeDues.cardView4.setOnClickListener(this);
        binding.includeDues.cardView5.setOnClickListener(this);
        binding.includeDues.cardView6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_h:
                ChargeDataSingleton.getInstance().setDues("1");
                manager.setCuotas(1);
                break;
            case R.id.cardView5:
                ChargeDataSingleton.getInstance().setDues("6");
                manager.setCuotas(6);
                break;
            case R.id.cardView4:
                ChargeDataSingleton.getInstance().setDues("9");
                manager.setCuotas(9);
                break;
            case R.id.cardView6:
                ChargeDataSingleton.getInstance().setDues("12");
                manager.setCuotas(12);
                break;
            case R.id.cardView3:
                ChargeDataSingleton.getInstance().setDues("3");
                manager.setCuotas(3);
                break;
            default:
                ChargeDataSingleton.getInstance().setDues("1");
                Log.i("CUOTAS", "DEFAULT");
                manager.setCuotas(1);
                break;
        }
        binding.conDongle.setVisibility(View.VISIBLE);
        binding.conDues.setVisibility(View.GONE);
        binding.progressCircularBar.setVisibility(View.VISIBLE);
    }
}
