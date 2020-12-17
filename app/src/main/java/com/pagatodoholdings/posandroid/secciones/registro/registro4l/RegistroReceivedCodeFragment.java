package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroReceivedCodeBinding;
import com.pagatodoholdings.posandroid.manager.FirebaseManager;
import com.pagatodoholdings.posandroid.utils.TimerUtil;
import com.pagatodoholdings.posandroid.utils.Utilities;
import java.util.concurrent.TimeUnit;

import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistroReceivedCodeFragment extends AbstractStepFragment {
    private static final String TAG = RegistroReceivedCodeFragment.class.getSimpleName();

    private FragmentRegistroReceivedCodeBinding mBinding;
    private TimerUtil mTimerUtil;

    public RegistroReceivedCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRegisterActions(getActivity());
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_received_code, container, false);
        setEnabledControls(false);
        mBinding.buttonUnicoContinue.setOnClickListener(v -> {
            if (MposApplication.getInstance().isBuildDebug() || mBinding.inputPhoneCode.isCodeSet()) {//Sonar reduce if and else if by one if statement
                advanceToNextStep();
            }
        });
        mBinding.buttonUnicoCodeNotReceived.setOnClickListener(v -> {
            Log.d(TAG, "Boton código no recibido oprimido");
            resendCode();
        });
        return mBinding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            subscribeToPhoneVerification();
//            setEnabledControls(true);
        }
    }

    private void setEnabledControls(boolean enabled) {
        if (enabled) {
            mBinding.buttonUnicoCodeNotReceived.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSaldoAzul));
        } else {
            mBinding.buttonUnicoCodeNotReceived.setTextColor(ContextCompat.getColor(getContext(), R.color.disabledFlatButtonTextColor));
        }
        mBinding.buttonUnicoCodeNotReceived.setEnabled(enabled);
    }

    private void setEnableContinueButton(boolean enabled) {
        mBinding.buttonUnicoContinue.setEnabled(enabled);
    }

    private void resendCode() {
        String countryCode = FirebaseManager.getLastCountryCodeUsed();
        String phoneNumber = FirebaseManager.getLastPhoneNumberUsed();
        FirebaseManager.startPhoneVerification(
                countryCode,
                phoneNumber,
                60L,
                getActivity());
        subscribeToPhoneVerification();
    }

    private void subscribeToPhoneVerification() {
        startCountDownTimer();
        FirebaseManager.subscribeToPhoneVerification(
                phoneCode -> {
                    try {
                        disposeTimer();
                        mBinding.inputPhoneCode.setCode(phoneCode);
                        savePhoneNumberToBean(FirebaseManager.getLastPhoneNumberUsed());
                        setEnableContinueButton(true);
                    } catch (Exception exe) {
                        LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                },
                throwable -> {
                    Log.e(TAG, "subscribeToPhoneVerification: ", throwable);
                    if (getActivity() != null) {
                        despliegaModalDeError("Error", Utilities.obtenerMensajeError(throwable), null);
                    }
                });
    }

    private void savePhoneNumberToBean(String phoneNumber) {
        DatosUsuarioRegistroCliente
                .getInstace()
                .setTelefono(phoneNumber);
    }

    private void startCountDownTimer() {
        Log.d(TAG, "timer comenzado");
        setEnabledControls(false);
        setEnableContinueButton(false);
        mTimerUtil = new TimerUtil();
        mTimerUtil.createCountdownTimer(60, TimeUnit.SECONDS, remainingTime -> onTimer(remainingTime));
    }

    /**
     * Maneja el tiempo que le queda al time out
     *
     * @param remainingTime
     */
    private void onTimer(Long remainingTime) {
        int remainingTimeInt = remainingTime.intValue();

        if (remainingTimeInt == 0) {
            setEnabledControls(true);
        }
    }

    @Override
    public void onDestroy() {
        disposeTimer();
        super.onDestroy();
    }

    private void disposeTimer() {
        if (mTimerUtil != null) {
            Log.d(TAG, "Timer terminado");
            mTimerUtil.disposeTimer();
            mTimerUtil = null;
        }
    }
}
