package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroTelefonoBinding;
import com.pagatodoholdings.posandroid.manager.FirebaseManager;
import com.pagatodoholdings.posandroid.utils.AppExecutors;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegistroTelefonoFragment extends AbstractStepFragment {

    private FragmentRegistroTelefonoBinding binding;

    private static final String SMS_DENDED_KEY = "sms_dended_key";
    private boolean smsSended;
    private ScheduledFuture<?> scheduledFuture;
    private long timeout = 60;
    private Country country;

    public RegistroTelefonoFragment() {
        // Required empty public constructor
    }

    public void setCountry(Country country){
        this.country = country;
        binding.etNumeroCelular.removeTextChangedListener(binding.etNumeroCelular.getMaskTextWatcher());
        binding.etNumeroCelular.setMask(country.getItemMask());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_telefono, container, false);
        binding.countryCodePicker.setClickable(false);
        binding.countryCodePicker.setEnabled(false);
        binding.countryCodePicker.setCcpDialogShowFlag(false);


        binding.countryCodePicker.registerCarrierNumberEditText(binding.etNumeroCelular);

        binding.btnRegistrarTelefono.setOnClickListener(v -> {

            if (MposApplication.getInstance().isBuildDebug()) {
                advanceToNextStep();
                return;
            }

            String phoneNumber = validatePhoneNumber();
            String countryCode = binding.countryCodePicker.getSelectedCountryCodeWithPlus();

            if (phoneNumber != null) {
                startPhoneVerification(countryCode, phoneNumber);
                advanceToNextStep();
            }
        });

        return binding.getRoot();
    }

    private void startPhoneVerification(String countryCode, String phoneNumber) {
        FirebaseManager.startPhoneVerification(
                countryCode,
                phoneNumber,
                timeout,
                getActivity());
    }

    private String validatePhoneNumber() {
        String phoneNumber = binding.etNumeroCelular.getRawText().replace(" ", "");
        Pattern pattern = compile("^[0-9]{8,10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            binding.btnRegistrarTelefono.setText(R.string.continuar);
            return phoneNumber;
        } else {
            binding.etNumeroCelular.setError("El Número no es Valido");
            return null;
        }
    }

    private void initPhoneVerification() {
        setBackArrowVisibility(View.GONE);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        long countDown = timeout;
        scheduledFuture = AppExecutors.getInstance().scheduled().scheduleAtFixedRate(() -> AppExecutors.getInstance().mainThread().execute(() -> {
            if (countDown <= 0) {
                setBackArrowVisibility(View.VISIBLE);
                scheduledFuture.cancel(true);
            }
        }), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SMS_DENDED_KEY, smsSended);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            smsSended = savedInstanceState.getBoolean(SMS_DENDED_KEY);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (smsSended) {
            initPhoneVerification();
        }
    }

    public static androidx.fragment.app.Fragment newInstance() {
        final RegistroTelefonoFragment fragment = new RegistroTelefonoFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setCountryCode() {
        if("release".equals(BuildConfig.BUILD_TYPE)) {
            binding.countryCodePicker.setCustomMasterCountries(DatosUsuarioRegistroCliente.getInstace().getPais());
            binding.etNumeroCelular.setMask(country.getItemMask());
        }
        binding.countryCodePicker.setCountryForNameCode(DatosUsuarioRegistroCliente.getInstace().getPais());
    }


}
