package com.pagatodoholdings.posandroid.secciones.registro.externo;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.databinding.ActivityRegistroCofBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.AbstractStepFragment;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRegistraTarjetaStep2;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroBean;
import com.pagatodoholdings.posandroid.utils.Constantes;

import java.util.List;
import java.util.Map;

public class RegistroCoF extends AbstractActivity implements AbstractStepFragment.RegisterActions{//NOSONAR

    private ActivityRegistroCofBinding binding;
    private final ConstraintSet constraintSet = new ConstraintSet();
    private RegistroInteractor registroInteractor;
    private final RegistroBean registroBean = new RegistroBean();
    private String ubicacionActual = "";
    private Map<Integer, Integer> pageEtapaMap;
    String messageError = "Error desconocido";
    private static final String TAG = RegistroDatosNegocio.class.getSimpleName();
    private static final String DESHABILITA_BACK = "DESHABILITA_BACK";
    private FragmentManager fm;

    RegistroRegistraTarjetaStep2 step2;

    private boolean mShowingBack = false;
    private int idActivityForResult = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getCallingActivity() != null){
            if (getIntent().getExtras() != null) {
                idActivityForResult = getIntent().getIntExtra(Constantes.ACTIVITY_CODE_KEY, 0);
            }
        }
        if (getIntent().getExtras() != null) {
            botonBackDeshabilitado = getIntent().getBooleanExtra(DESHABILITA_BACK, false);
        } else {
            botonBackDeshabilitado = false;
        }
        setContentView(R.layout.activity_registro_cof);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registro_cof);

        binding.onBack.setOnClickListener(v -> backArrowBtn());
        constraintSet.clone(binding.cvContainer);


            // Add the fragment to the 'fragment_container' FrameLayout
            step2 = new RegistroRegistraTarjetaStep2(this, idActivityForResult);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, step2);
            transaction.addToBackStack(null);
            transaction.commit();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }


    @Override
    protected void initUi() {
        fm = getSupportFragmentManager();
//        binding.onBack.setOnClickListener(v -> backArrowBtn());
//        constraintSet.clone(binding.cvContainer);

    }

    public void backArrowBtn() {
        realizaAlPresionarBack();
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
        finish();
    }

    public void hiddeToolBars(){
        binding.flHeader.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
        if(step2.isAllowBack())
            super.onBackPressed();
    }

    @Override
    public void setBackArrowVisibility(int visibility) {

    }

    @Override
    public void nextStep() {
        cambiaDeActividadSinCerrar(RegistroDatosNegocio.class);
    }

    @Override
    public void prevStep() {

    }

}
