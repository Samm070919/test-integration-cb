package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroRegistraTarjetaStep1Binding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;

import net.fullcarga.android.api.sesion.DatosSesion;

import java.sql.SQLException;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;

public class RegistroRegistraTarjetaStep1 extends AbstractStepFragment {

    private static final String TAG = RegistroRegistraTarjetaStep1.class.getSimpleName();
    public static final String DESHABILITA_BACK = "DESHABILITA_BACK";

    public RegistroRegistraTarjetaStep1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRegisterActions(getActivity());
        FragmentRegistroRegistraTarjetaStep1Binding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_registra_tarjeta_step1, container, false);

        mBinding.btnContinuarRegistro.setOnClickListener(v -> advanceToNextStep());

        mBinding.tvNoTarjeta.setOnClickListener(v -> {
            if(ApiData.APIDATA.getDatosSesion() == null){
                iniciaSesionSigma();
            }else
                cambiaDeActividad();
        });

        return mBinding.getRoot();
    }

    private void iniciaSesionSigma() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
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
        ApiData.APIDATA.setDatosSesion(datosSesion);
        cambiaDeActividad();
    }

    private int getIsVisibleValue(boolean isEmpty) {
        return isEmpty ? View.VISIBLE : View.GONE;
    }

    public void cambiaDeActividad() {
        final Intent intent = new Intent(getActivity(), KitSolicitarActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(DESHABILITA_BACK, true);
        startActivity(intent);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI(final LayoutInflater inflater, final ViewGroup container) {

    }

    private void cerrar() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        }

    }

    private void reiniciarContador() {

        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

}
