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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.util.DateUtil;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentKitTransaccionResultBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsDate;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import java.math.BigDecimal;

public class KitTransaccionResult extends AbstractStepFragment {
    private FragmentKitTransaccionResultBinding binding;
    protected static final String TAG = AbstractFragment.class.getSimpleName();
    private boolean byCard = false;
    private DatosCompraKit datosCompraKit;
    private InfoCompraKitCoF infoCompraKitCoF;
    private InfoCompraKit infoCompraKit;

    private AbstractActivity homeActivity;
    private int idCallingActivity = 0;

    public static KitTransaccionResult instance;

    public KitTransaccionResult(int idCallingActivity) {
        this.idCallingActivity = idCallingActivity;
    }

    public void setIdCallingActivity(int idCallingActivity) {
        this.idCallingActivity = idCallingActivity;
    }

    public void setInfoResult(DatosCompraKit datosCompraKit, InfoCompraKit infoCompraKit, AbstractActivity activity) {
        this.infoCompraKit = infoCompraKit;
        this.homeActivity = activity;
        this.datosCompraKit = datosCompraKit;
        //cargarDatosTransaccion();
    }


    public void setInfoResult(DatosCompraKit datosCompraKit, InfoCompraKitCoF infoCompraKitCoF, AbstractActivity p5, boolean doProcess) {
        this.datosCompraKit = datosCompraKit;
        this.infoCompraKitCoF = infoCompraKitCoF;
        this.homeActivity = p5;
        byCard = true;
        //if (doProcess)
            //cargarDatosTransaccion();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRegisterActions(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kit_transaccion_result, container, false);
        initUI();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarDatosTransaccion();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    private void initUI() {
        binding.btnAceptar.setOnClickListener(v -> {
            if(idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
                if (homeActivity instanceof HomeActivity) {
                    homeActivity.restauraHome();
                } else {
                    advanceToNextStep();
                }
            }else if(idCallingActivity == Constantes.REQUEST_COMPRA_KIT_BY_MENU || idCallingActivity == Constantes.REQUEST_COMPRA_KIT_BY_COBRAR){
                advanceToNextStep();
            }else{
                Intent output = new Intent();
                output.putExtra("DECODED", idCallingActivity);
                getActivity().setResult(Constantes.REQUEST_COMPRA_KIT_BY_MENU, output);
                getActivity().finish();
            }
        });

        binding.btnTerminar.setOnClickListener(v -> {
            if(idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
                if (homeActivity instanceof HomeActivity) {
                    homeActivity.restauraHome();
                } else {
                    advanceToNextStep();
                }
            }else if(idCallingActivity == Constantes.REQUEST_COMPRA_KIT_BY_MENU || idCallingActivity == Constantes.REQUEST_COMPRA_KIT_BY_COBRAR || idCallingActivity == Constantes.REQUEST_COMPRA_KIT_BY_MINEGOCIO){
                advanceToNextStep();
            }else {
                Intent output = new Intent();
                output.putExtra("DECODED", idCallingActivity);
                getActivity().setResult(Constantes.REQUEST_COMPRA_KIT_BY_MENU, output);
                getActivity().finish();
            }

        });

        binding.ivCompartir.setOnClickListener(v -> {
            Utilities.takeScreenshot(getView());
        });

        binding.ivShare.setOnClickListener(v -> {
            Utilities.takeScreenshot(getView());
        });
    }


    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btn_aceptar_screenshot);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void cargarDatosTransaccion() {
        if (byCard && infoCompraKitCoF != null) {
            BigDecimal imp = BigDecimal.valueOf(infoCompraKitCoF.getAmount());
            binding.clPSE.setVisibility(View.GONE);
            binding.clCard.setVisibility(View.VISIBLE);
            binding.ivShare.setVisibility(View.GONE);
            binding.tvReferenciaUnica.setText("Lector MPos");
            binding.tvMontoPagado.setMonto(UtilsKt.cleanAmount(SigmaBdManager.formatoSaldo(imp, new OnFailureListener.BasicOnFailureListener(TAG, "Error de formato"))));
            binding.tvFechaPago.setText(UtilsDate.getDateNewFormat());
            binding.tvAutorization.setText(infoCompraKitCoF.getAuthorizationcode());
            binding.tvFolio.setText(infoCompraKitCoF.getTransactionid());
            binding.tvDireccionEnvio.setText(datosCompraKit.getDireccionCompleta());

        } else if (infoCompraKit != null) {
            BigDecimal imp = new BigDecimal(infoCompraKit.getImporte());
            binding.clPSE.setVisibility(View.VISIBLE);
            binding.clCard.setVisibility(View.GONE);
            binding.lImporte.setMonto(UtilsKt.cleanAmount(SigmaBdManager.formatoSaldo(imp, new OnFailureListener.BasicOnFailureListener(TAG, "Error de formato"))));

            binding.tvConcepto.setText(infoCompraKit.getEstadodesc());
            binding.tvReferencia.setText(infoCompraKit.getIdPago());
            binding.tvFechaResult.setText(DateUtil.getDateNow());
        }
    }

    public void showResult()
    {
        cargarDatosTransaccion();
    }
}
