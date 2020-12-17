package com.pagatodoholdings.posandroid.secciones.qrcode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentPagoQrBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.secciones.retrofit.PagoQrData;
import com.pagatodoholdings.posandroid.secciones.retrofit.PagoQrServiceInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.QrWalletPago;
import com.pagatodoholdings.posandroid.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NOMBRE;

public class PagoQrFragment extends AbstractConfigMenu {

    private FragmentPagoQrBinding binding;
    private HomeActivity homeActivity;
    private String qrString;

    public static PagoQrFragment newInstance(String data) {
        PagoQrFragment f = new PagoQrFragment();
        Bundle args = new Bundle();
        args.putString("qrData", data);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pago_qr, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    private void initUI() {
        homeActivity = (HomeActivity) getActivity();
        binding.toolbar2.setNavigationIcon(R.drawable.ic_icono_back);
        getListener().setSupportActionBar(binding.toolbar2);
        getListener().getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.toolbar2.setNavigationOnClickListener(v -> getListener().cargarFragmentQr());
        if (getArguments() != null) {
            qrString = getArguments().getString("qrData");
        }
        binding.qrUserName.setText(MposApplication.getInstance().getPreferenceManager().getValue(NOMBRE, ""));

        binding.botonEnviarQr.setOnClickListener(v -> {
            if (binding.montoViewController.getEditText().getText().toString().isEmpty()) {
                binding.montoViewController.getEditText().requestFocus();
                binding.montoViewController.getEditText().setError("Campo vacio");
                return;
            }
            enviarTrxQr();
        });

        binding.montoViewController.estableceAccionIme((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Utilities.hideSoftKeyboard(getActivity());
                binding.edReferenciaQr.requestFocus();
                return true;
            }
            return false;
        });
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    private void enviarTrxQr() {
        homeActivity.muestraProgressDialog("Enviando Pago...");
        PagoQrServiceInteractor interactor = new PagoQrServiceInteractor();
        interactor.enviarPagoQr(
                new RetrofitListener<PagoQrData>() {
                    @Override
                    public void showMessage(String message) {
                        homeActivity.ocultaProgressDialog();
                        homeActivity.despliegaModal(true, false, "Error al Enviar Pago", message, null);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        homeActivity.ocultaProgressDialog();
                        homeActivity.despliegaModal(true, false, "Error al Enviar Pago", Utilities.obtenerMensajeError(throwable), null);
                    }

                    @Override
                    public void onSuccess(PagoQrData result) {
                        Utilities.guardarSigmaTransacciones(binding.montoViewController.getEditText().getText().toString(),
                                result.getReferenceNumber(),
                                result.getNombre(),
                                result.getMerchant(),
                                "PAGO_QR",
                                "Pago a QR");
                        homeActivity.ocultaProgressDialog();
                        QrConfimacionFragment fragment = QrConfimacionFragment.newInstance(binding.montoViewController.getEditText().getText().toString(), result.getReferenceNumber(), result.getMerchant(), result.getNombre());
                        fragment.setListener(getListener());
                        homeActivity.cargarFragmentsCuenta(View.GONE, homeActivity.generaListener(fragment));
                    }
                }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(), new QrWalletPago(Integer.parseInt(binding.montoViewController.getEditText().getText().toString()),
                        binding.edReferenciaQr.obtenEtCampo().getText().toString(), qrString));
    }
}