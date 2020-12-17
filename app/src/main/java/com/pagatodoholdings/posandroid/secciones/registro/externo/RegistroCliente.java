package com.pagatodoholdings.posandroid.secciones.registro.externo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.databinding.ActivityRegistroExternoBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.registro.ConfigPaisListener;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.DatosUsuarioRegistroCliente;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.PaisConfig;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroCreaCuentaNuevaFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroPaisFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroQueremosSaberMasDeTiFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroReceivedCodeFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep1;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroTelefonoFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroPaisesInteractor;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.PAIS;

public class RegistroCliente extends AbstractActivity
        implements AbstractStepFragment.RegisterActions, ConfigPaisListener {//NOSONAR

    private ActivityRegistroExternoBinding binding;
    private final ConstraintSet constraintSet = new ConstraintSet();
    private Map<Integer, Integer> pageEtapaMap;
    private static final int MIN_STEPS = 3;
    private static final String DESHABILITA_BACK = "DESHABILITA_BACK";
    private PaisConfig paisConfig;
    private Country country;
    private static RegistroTelefonoFragment registroTelefonoFragment;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPageEtapaMap();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        obtenerUbicacion();
    }

    private void initPageEtapaMap() {
        pageEtapaMap = new HashMap<>();
        pageEtapaMap.put(0, 0);
        pageEtapaMap.put(1, 0);
        pageEtapaMap.put(2, 1);
        pageEtapaMap.put(3, 2);
        pageEtapaMap.put(4, 0);
        pageEtapaMap.put(5, 1);
        pageEtapaMap.put(6, 2);
    }

    @Override
    protected void initUi() {

        preferenceManager.setValue(NUMERO_SERIE, tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registro_externo);
        binding.onBack.setOnClickListener(v -> backArrowBtn());
        constraintSet.clone(binding.cvContainer);

        binding.onBack.setVisibility(View.GONE);
        binding.tvTitleRegister.setVisibility(View.VISIBLE);
        binding.tvTitleRegister.setText(getString(R.string.registro_header_select_pais));

        registroTelefonoFragment = new RegistroTelefonoFragment();
        /**      * @deprecated      */
        @Deprecated final StepsFragmentPagerAdapter stepsFragmentPagerAdapter = new StepsFragmentPagerAdapter(getSupportFragmentManager());

        binding.vpSteps.setAdapter(stepsFragmentPagerAdapter);
        binding.vpSteps.setOffscreenPageLimit(2);
        binding.vpSteps.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                //no necesario
            }

            @Override
            public void onPageSelected(final int position) {


                Fragment page = getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + binding.vpSteps.getId() + ":" + position);

                if(position == 1){
                    registroTelefonoFragment.setCountry(country);
                    binding.vpSteps.getAdapter().notifyDataSetChanged();
                }
                if (page instanceof RegistroTelefonoFragment) {
                    binding.tvTitleRegister.setVisibility(View.GONE);

                    ((RegistroTelefonoFragment) page).setCountryCode();
                } else if (page instanceof RegistroPaisFragment || page instanceof RegistroReceivedCodeFragment) {

                    if (page instanceof RegistroPaisFragment) {
                        binding.onBack.setVisibility(View.VISIBLE);
                        binding.tvTitleRegister.setVisibility(View.VISIBLE);
                        binding.tvTitleRegister.setText(getString(R.string.registro_header_select_pais));
                    } else {
                        binding.tvTitleRegister.setVisibility(View.GONE);
                    }

                } else if (page instanceof RegistroQueremosSaberMasDeTiFragment) {
                    binding.tvTitleRegister.setVisibility(View.VISIBLE);
                    binding.tvTitleRegister.setText(getString(R.string.registro_title_personal_information));
                } else {
                    binding.tvTitleRegister.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                //no necesario
            }
        });

        obtenerUbicacion();
    }

    void obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            habilitarUbicacion();
        } else {
            final FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        //NOT IMPLEMENTED
                    });
        }
    }

    public void habilitarUbicacion() {
        Toast.makeText(this, "Habilite los permisos de ubicación", Toast.LENGTH_SHORT).show();
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(intent);
    }

    public void backArrowBtn() {
        prevStep();
    }

    public void setBackArrowVisibility(int visibility) {
        binding.onBack.setVisibility(visibility);
    }

    public void nextStep() {
        final int lastStep = StepsFragmentPagerAdapter.ITEM_COUNT - 1;
        final int currentStep = binding.vpSteps.getCurrentItem();
        final int nextStep = currentStep + 1;

        if (nextStep <= lastStep) {
            binding.vpSteps.setCurrentItem(nextStep, true);
        } else {
            cambiaDeActividadSinCerrar(RegistroCoF.class);
            binding.onBack.setVisibility(View.GONE);

        }
    }

    @Override
    public void cambiaDeActividad(final Class<? extends Activity> claseNuevaActividad) {
        final Intent intent = new Intent(this, claseNuevaActividad);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DESHABILITA_BACK, true);
        startActivity(intent);
        finish();
    }

    public void prevStep() {
        int currentStep = binding.vpSteps.getCurrentItem();
        final int step = currentStep != 0 ? currentStep - 1 : 0;
        binding.vpSteps.setCurrentItem(step, true);
    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Collections.emptyList();
    }

    @Override
    protected void realizaAlPresionarBack() {
        if (binding.vpSteps.getCurrentItem() == 0) {
            finish();
        } else {
            prevStep();
        }
    }

    // region Country Listener

    @Override
    public void onFetchUrlForCountry(@NotNull Country country) {
        new RegistroPaisesInteractor().configPais(new RetrofitListener<PaisConfig>() {
            @Override
            public void showMessage(String s) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("RegistroCliente", throwable.getMessage());
            }

            @Override
            public void onSuccess(PaisConfig paisConfig) {
                RegistroCliente.this.paisConfig = paisConfig;
            }
        }, country.getItemIsoCode());

        MposApplication.getInstance().getPreferenceManager().setValue(PAIS, country.getItemCode());
        DatosUsuarioRegistroCliente.getInstace().setPais(country.getItemCode());
        DatosUsuarioRegistroCliente.getInstace().setLatitud(0.00);
        DatosUsuarioRegistroCliente.getInstace().setLongitud(0.00);
        this.country = country;
        nextStep();
    }

    @Override
    public PaisConfig onReturnConfig() {
        return paisConfig;
    }

    @Override
    public Country onReturnCountry() {
        return country;
    }

    // endregion

    public static class StepsFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int ITEM_COUNT = 6;

        public StepsFragmentPagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public Fragment getItem(final int position) {
            switch (position) {
                case 0:
                    return RegistroPaisFragment.newInstance();
                case 1:
                    return registroTelefonoFragment;
                case 2:
                    return new RegistroReceivedCodeFragment();
                case 3:
                    return RegistroCreaCuentaNuevaFragment.newInstance();
                case 4:
                    return RegistroQueremosSaberMasDeTiFragment.newInstance();
                case 5:
                    return new RegistroRegistraTarjetaStep1();
                default:
                    return null;
            }

        }
    }


}
