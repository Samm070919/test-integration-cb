package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroRecibePagosTarjetaBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.utils.Constantes;

import net.fullcarga.android.api.sesion.DatosSesion;
import java.sql.SQLException;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DONGLE_VINCULADO;
import static com.pagatodoholdings.posandroid.utils.Constantes.NON_ACTIVITY_IS_CALLING;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_CARD_BY_COMPRA_KIT;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_COBRAR;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_MENU;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_MINEGOCIO;
import static com.pagatodoholdings.posandroid.utils.Constantes.SKIP_FIRST_PAGE;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;
import static net.fullcarga.android.api.oper.TipoOperacion.LOGIN;

public class RegistroRecibePagosTarjetaFragment extends AbstractStepFragment {

    private static final String TAG = RegistroRecibePagosTarjetaFragment.class.getSimpleName();
    private int idCallingActivity = 0;
    public static final String DESHABILITA_BACK = "DESHABILITA_BACK";

    public RegistroRecibePagosTarjetaFragment(int idCallingActivity) {
        this.idCallingActivity = idCallingActivity;
    }

    public RegistroRecibePagosTarjetaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRegisterActions(getActivity());
        FragmentRegistroRecibePagosTarjetaBinding mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_recibe_pagos_tarjeta, container, false);

        mBinding.botonUnicoSiQuiero.setOnClickListener(v ->/* advanceToNextStep()*/{
            if(getActivity() instanceof HomeActivity){
                PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
                if (preferenceManager.isExiste(DONGLE_VINCULADO)) {
                    final Intent intent = new Intent(getActivity(), RegistroDatosNegocio.class);
                    startActivity(intent);
                } else {
                    callSolicitarKitFrom(REQUEST_COMPRA_KIT_BY_MINEGOCIO);
                }
            }else if (idCallingActivity != Constantes.NON_ACTIVITY_IS_CALLING) {
                callSolicitarKitFrom(idCallingActivity);
            }else {
                final Intent intent = new Intent(getActivity(), KitSolicitarActivity.class);
                intent.putExtra(DESHABILITA_BACK, true);
                intent.putExtra(SKIP_FIRST_PAGE, true);
                startActivity(intent);
            }
        });

        mBinding.textViewOtroMomento.setOnClickListener(v -> {
            DatosNegocio datosNegocioPreferencia =
                    MposApplication.getInstance().getDatosNegocio();
            if (idCallingActivity == REQUEST_COMPRA_KIT_BY_MINEGOCIO) {/*Se llama desde datoa de negocio a si que en este boton se llama a registrar negocio*/
                final Intent intent = new Intent(getActivity(), RegistroDatosNegocio.class);
                intent.putExtra(Constantes.ACTIVITY_CODE_KEY, idCallingActivity);
                startActivityForResult(intent, 1);
                startActivity(intent);
            } else if(idCallingActivity != NON_ACTIVITY_IS_CALLING ){
                ((AbstractActivity) requireActivity()).restauraHome();
            }else if(datosNegocioPreferencia == null) {/*En caso de que aún no se haya registrado datos de negocio pasamos a*/
                final Intent intent = new Intent(getActivity(), RegistroDatosNegocio.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(DESHABILITA_BACK, true);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return mBinding.getRoot();
    }

    private void callSolicitarKitFrom(int requestActivity) {
        final Intent intent = new Intent(getActivity(), KitSolicitarActivity.class);
        intent.putExtra(SKIP_FIRST_PAGE, true);
        intent.putExtra(Constantes.ACTIVITY_CODE_KEY, requestActivity);
        startActivityForResult(intent, 1);// Activity is started with requestCode 2
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && MposApplication.getInstance().getDatosLogin().getCliente() == null) {
            iniciaSesionSigma();
        }
    }

    private void iniciaSesionSigma() {
        DatosSesion datosSesion = null;
        //Si no hay datos de sesión, es imposible hacer login
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        if (datosLogin == null) {
            return;
        }

        try {
            datosSesion = build();
        } catch (SQLException exe) {
            LOGGER.throwing(TAG,1,exe,exe.getMessage());
        }

        enviarLogin(datosSesion);
    }

    private void enviarLogin(final DatosSesion datosSesion) {//NOSONAR
        ApiData.APIDATA.setDatosSesion(datosSesion);
        TransaccionFactory.crearTransacion(LOGIN, abstractRespuesta -> {
            hideProgressDialog();
            if (abstractRespuesta.isCorrecta()) {
                if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                    operSiguiente();
                } else {

                        if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())) {
                            ApiData.APIDATA.setNotificationsID (getString(R.string.wallet_notificacion_path, BuildConfig.APPLICATION_ID,
                                    MposApplication.getInstance().getDatosLogin().getCliente().getPais()));
                        }
                    }
                }
        }, throwable -> {

        }).realizarOperacion();
        showProgressDialog();
    }


    private void operSiguiente() {
        showProgressDialog();
        TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                .realizarOperacion();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQUEST_COMPRA_KIT_BY_MINEGOCIO) {
            final Intent intent = new Intent(getActivity(), RegistroDatosNegocio.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(DESHABILITA_BACK, true);
            startActivity(intent);

        }
    }
}
