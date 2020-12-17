package com.pagatodoholdings.posandroid.secciones.kit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityKitSolicitarBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment.RegisterActions;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroAnaPresentationFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroPaisFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroReceivedCodeFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRecibePagosTarjetaFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroTelefonoFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.DatosCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKit;
import com.pagatodoholdings.posandroid.secciones.retrofit.kit_entity.InfoCompraKitCoF;
import com.pagatodoholdings.posandroid.utils.Constantes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pagatodoholdings.posandroid.utils.Constantes.NON_ACTIVITY_IS_CALLING;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_CARD_BY_COMPRA_KIT;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_COBRAR;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_MENU;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_COMPRA_KIT_BY_MINEGOCIO;
import static com.pagatodoholdings.posandroid.utils.Constantes.SKIP_FIRST_PAGE;

public class KitSolicitarActivity extends AbstractActivity implements RegisterActions {

    private ActivityKitSolicitarBinding binding;
    private final ConstraintSet constraintSet = new ConstraintSet();
    private Map<Integer, Integer> pageEtapaMap;
    private static final int MIN_STEPS = 4;
    private static KitDatosEnvioEmptyFragment kitDatosEnvioEmptyFragment;
    private static KitSolicitarFragment kitSolicitarFragment;
    private static KitTransaccionResult kitTransaccionResult;
    private int idCallingActivity = 0;
    private InfoCompraKit infoCompraKit;
    public static final String KITDATA_SEND = "KITDATA_SEND";
    private static RegistroRecibePagosTarjetaFragment registroRecibePagosTarjetaFragment;
    private boolean skipFirstPage;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        if(getCallingActivity() != null){
            if(getIntent().getExtras() != null)
                idCallingActivity = getIntent().getIntExtra(Constantes.ACTIVITY_CODE_KEY, 0);
        }
        if (getIntent().getExtras() != null) {
            botonBackDeshabilitado = getIntent().getBooleanExtra(DESHABILITA_BACK, false);
            skipFirstPage = getIntent().getBooleanExtra(SKIP_FIRST_PAGE, false);
        } else {
            botonBackDeshabilitado = false;
        }

        super.onCreate(savedInstanceState);


        if (botonBackDeshabilitado) {
            setBackArrowVisibility(View.INVISIBLE);
        } else {
            setBackArrowVisibility(View.VISIBLE);
        }
        setBackArrowVisibility(View.INVISIBLE);
        binding.onBack.setOnClickListener(v -> backArrowBtn());

        initPageEtapaMap();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void initPageEtapaMap() {
        pageEtapaMap = new HashMap<>();
        pageEtapaMap.put(0, 0);
        pageEtapaMap.put(1, 1);
        pageEtapaMap.put(2, 2);
        pageEtapaMap.put(3, 3);
    }
    @Override
    protected void initUi() {
        setBinding(DataBindingUtil.setContentView(this, R.layout.activity_kit_solicitar));
        getBinding().onBack.setOnClickListener(v -> backArrowBtn());
        constraintSet.clone(getBinding().cvContainer);
        registroRecibePagosTarjetaFragment = new RegistroRecibePagosTarjetaFragment(idCallingActivity);
        kitTransaccionResult = new KitTransaccionResult(idCallingActivity);
        kitSolicitarFragment = new KitSolicitarFragment(new SolicitaKitInfo() {
            @Override
            public void kitSolicitado(DatosCompraKit envio, InfoCompraKitCoF infoCompraKit) {
                kitTransaccionResult.setInfoResult(envio, infoCompraKit, KitSolicitarActivity.this, true);
                kitTransaccionResult.showResult();
                nextStep();
            }

            @Override
            public void kitSolicitadoPSE(DatosCompraKit envio, InfoCompraKit infoCompraKitPSE, int idCallingActivity) {
                kitTransaccionResult.setIdCallingActivity(idCallingActivity);
                kitTransaccionResult.setInfoResult(envio, infoCompraKitPSE, KitSolicitarActivity.this);
                infoCompraKit = infoCompraKitPSE;
                kitTransaccionResult.showResult();
                nextStep();
            }
        }, idCallingActivity);
        kitDatosEnvioEmptyFragment = new KitDatosEnvioEmptyFragment(new CapturedDatosEnvio() {
            @Override
            public void updatedData(DatosCompraKit data) {
                kitSolicitarFragment.setDatosCompraKit(data, KitSolicitarActivity.this);
                nextStep();
            }
        });

        getBinding().flHeader.setVisibility(View.GONE);

        StepsFragmentPagerAdapter stepsFragmentPagerAdapter = new StepsFragmentPagerAdapter(getSupportFragmentManager());
        getBinding().vpSteps.setAdapter(stepsFragmentPagerAdapter);
        getBinding().vpSteps.setOffscreenPageLimit(4);
        if(skipFirstPage)
            nextStep();
        getBinding().vpSteps.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                //no necesario
            }

            @Override
            public void onPageSelected(final int position) {

                getBinding().flHeader.setVisibility(View.VISIBLE);


                Fragment page = getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:" + getBinding().vpSteps.getId() + ":" + position);
                if (page instanceof RegistroTelefonoFragment) {
                    ((RegistroTelefonoFragment) page).setCountryCode();
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                //no necesario
            }
        });
    }

    @Override
    protected boolean validaCampos() {
        return false;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return null;
    }

    @Override
    protected void realizaAlPresionarBack() {
        if (binding.vpSteps.getCurrentItem() == 0) {
            finish();
        } else {
            prevStep();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void backArrowBtn() {
        if(idCallingActivity == Constantes.NON_ACTIVITY_IS_CALLING) {
            prevStep();
        }else{
            if (binding.vpSteps.getCurrentItem() == 0)
                finish();
            else{
                prevStep();
            }
        }
    }

    public void setBackArrowVisibility(int visibility) {
        binding.onBack.setVisibility(visibility);
    }

    @Override
    public void nextStep() {
        final int lastStep = StepsFragmentPagerAdapter.ITEM_COUNT - 1;
        final int currentStep = getBinding().vpSteps.getCurrentItem();
        final int nextStep = currentStep + 1;

        if (nextStep <= lastStep) {
            if(infoCompraKit != null) {
                Intent intentKitPSE = new Intent(this,OnlineKitPayActivity.class);
                Gson gson = new Gson();
                String jsonKitPSE = gson.toJson(infoCompraKit);
                intentKitPSE.putExtra(KITDATA_SEND, jsonKitPSE );       //putExtra(KITDATA_SEND, new Gson. infoCompraKit);
                startActivityForResult(intentKitPSE, 1);
            } else {
                getBinding().vpSteps.setCurrentItem(nextStep, true);
            }
        } else {
            if(idCallingActivity == REQUEST_COMPRA_KIT_BY_MINEGOCIO || idCallingActivity ==  NON_ACTIVITY_IS_CALLING)
                cambiaDeActividad(RegistroDatosNegocio.class);
            else
                restauraHome();
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

    public void cambiaDeActividadSinCerrar(final Class<? extends Activity> claseNuevaActividad) {
        final Intent intent = new Intent(this, claseNuevaActividad);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void prevStep() {
        int currentStep = binding.vpSteps.getCurrentItem();
        final int step = currentStep != 0 ? currentStep - 1 : 0;
        binding.vpSteps.setCurrentItem(step, true);
    }

    public ActivityKitSolicitarBinding getBinding() {
        return binding;
    }

    public void setBinding(ActivityKitSolicitarBinding binding) {
        this.binding = binding;
    }

     static class StepsFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int ITEM_COUNT = 4;


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
                    return registroRecibePagosTarjetaFragment;
                case 1:
                    return kitDatosEnvioEmptyFragment;
                case 2:
                    return kitSolicitarFragment;
                case 3:
                    return kitTransaccionResult;
                case 4:
                    return new RegistroRecibePagosTarjetaFragment();
                default:
                    return null;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         if (resultCode == REQUEST_ADD_CARD_BY_COMPRA_KIT) {
            //restauraHome();
        }
    }
}

interface CapturedDatosEnvio {
    public void updatedData(DatosCompraKit data);
}

interface SolicitaKitInfo{
    void kitSolicitado(DatosCompraKit envio, InfoCompraKitCoF infoCompraKit);
    void kitSolicitadoPSE(DatosCompraKit envio, InfoCompraKit infoCompraKit, int idCallingActivity);
}


