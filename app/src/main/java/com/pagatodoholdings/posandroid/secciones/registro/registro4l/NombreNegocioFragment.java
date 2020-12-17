package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentNombreNegocioBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarActivity;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoDocBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoDocumentoInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoNegocioBean;
import com.pagatodoholdings.posandroid.utils.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.EMAIL;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.validateInputFields;

public class NombreNegocioFragment extends AbstractStepFragment {

    private DatosNegocio mDatosNegocio =  DatosNegocio.getInstance();

    private FragmentNombreNegocioBinding binding;
    private AbstractActivity activity;
    private static final String TAG = NombreNegocioFragment.class.getSimpleName();
    private RegistroInteractor registroInteractor;
    int idCallingActivity = 0;
    private List<TipoDocBean> listDocumentsTypes;
    private Integer tipoDocCod;
    private final ModalFragment.CommonDialogFragmentCallBackWithCancel conectionErrorHandler = new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
        @Override
        public void onCancel() {
            activity.finish();
        }

        @Override
        public void onAccept() {
            if (!MposApplication.getInstance().isBuildDebug()) {
                cargarTipoNegocio();
            }
        }
    };

    public NombreNegocioFragment(int idCallingActivity) {
        // Required empty public constructor
        this.idCallingActivity = idCallingActivity;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        activity = (AbstractActivity) getActivity();

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nombre_negocio, container, false);
        binding.textViewOtroMomento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idCallingActivity == 0) {
                    String registroCuenta = MposApplication.getInstance().getPreferenceManager()
                            .getValue(Constantes.Preferencia.REGISTRO_CUENTA, null);

                    if (registroCuenta != null && registroCuenta.equalsIgnoreCase("Registrada")) {
                        ((AbstractActivity) getActivity()).cambiaDeActividad(KitSolicitarActivity.class);
                    } else {
                        ((AbstractActivity) getActivity()).cambiaDeActividad(RegistroAgregaCuentaActivity.class);
                    }
                } else {
                    Intent output = new Intent();
                    output.putExtra("DECODED", idCallingActivity);
                    getActivity().setResult(com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_CARD_BY_MENU, output);
                    getActivity().finish();
                }
            }
        });
        binding.etRuc.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        binding.etRuc.getEditText().setTransformationMethod(null);
        int maxLengthofEditText = 20;
        binding.etRuc.getEditText().setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLengthofEditText)});

        binding.tipoDocumento.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.etRuc.setHint(listDocumentsTypes.get(position).toString());
                setSelectedDocType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /*Not implemented*/}
        });

        bindDocumentsType();
        setWidgetReferences();
        return binding.getRoot();
    }



    private void setWidgetReferences() {
        registroInteractor = new RegistroInteractor();
        cargarTipoNegocio();

        binding.etGiroComercial.obtenSpinnerContenido().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                cargarTipoNegocio();
            }
            return true;
        });

        //el botón de continuar desencadena la validación de edit texts
        binding.bContinuar.setOnClickListener(v -> next());

        binding.etRuc.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT){
                Utilities.hideSoftKeyboard(getActivity());
                next();
                return true;
            }
            return false;
        });
    }

    private boolean validateFields() {

        return validateInputFields(binding.tilDireccion) &&
                validateInputFields(binding.etRuc);

    }

    public static Fragment newInstance(int idCallingActivity) {
        final NombreNegocioFragment fragment = new NombreNegocioFragment(idCallingActivity);
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void setSelectedDocType(final int position) {
        TipoDocBean tipoDocBean = listDocumentsTypes.get(position);
        tipoDocCod = tipoDocBean.getTipodoc();
    }

    private void bindDocumentsType(){
        String baseUrl = (MposApplication.getInstance().getDatosLogin().getPais() != null
                && MposApplication.getInstance().getDatosLogin().getPais().getUrlwalletpublica() != null)
                ? MposApplication.getInstance().getDatosLogin().getPais().getUrlwalletpublica()
                : BuildConfig.URL_REGISTRO;

        new TipoDocumentoInteractor(baseUrl).getTiposDocNegocio(new RetrofitListener<List<TipoDocBean>>() {
            @Override
            public void onSuccess(final List<TipoDocBean> tList) {
                if(tList!=null && !tList.isEmpty()) {
                    listDocumentsTypes = tList;
                        binding.etRuc.setHint(listDocumentsTypes.get(0).toString());
                    binding.tipoDocumento.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(getActivity().getApplicationContext(), listDocumentsTypes));
                }
            }

            @Override
            public void onFailure(final Throwable thr) {

                LOGGER.throwing(TAG, 3, thr, "Error al cargar giro");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }


    void cargarTipoNegocio() {
        activity.muestraProgressDialog(getString(R.string.cargando));
        registroInteractor.getTipoNegocio(new RetrofitListener<List<TipoNegocioBean>>() {
            @Override
            public void onSuccess(final List<TipoNegocioBean> tList) {
                activity.ocultaProgressDialog();
                binding.etGiroComercial.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(activity, tList));
                binding.etGiroComercial.obtenSpinnerContenido().setOnTouchListener(null);
            }

            @Override
            public void onFailure(final Throwable thr) {
                activity.ocultaProgressDialog();
                activity.despliegaModal(true, false, getString(R.string.registro_dialog_cargando_error), getString(R.string.registro_dialog_cargando_error_internet), conectionErrorHandler);
                LOGGER.throwing(TAG, 3, thr, "Error al cargar giro");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    private void next(){
        if (validateFields()) {
            if (binding.etGiroComercial.obtenSpinnerContenido().getAdapter() == null){
                ((AbstractActivity)getActivity()).despliegaModal(true, false,
                        getString(R.string.title_nombre_negocio_giro_empty), getString(R.string.msg_nombre_negocio_giro_empty),null);
                return;
            }

            MposApplication.getInstance().setTipodoc(tipoDocCod);

            final TipoNegocioBean tipoNegocioBean =
                    ((GenericAdaptadorSpinner<TipoNegocioBean>) binding.etGiroComercial.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.etGiroComercial.obtenSpinnerContenido().getSelectedItemPosition());
            mDatosNegocio.setTiponegocio(tipoNegocioBean.getTipnegocio());
            mDatosNegocio.setRs(binding.tilDireccion.getEditText().getText().toString());
            mDatosNegocio.setTipdoc(tipoDocCod);
            mDatosNegocio.setEmail(MposApplication.getInstance().getPreferenceManager().getValue(EMAIL,""));
            mDatosNegocio.setId(binding.etRuc.getEditText().getText().toString());//Revisar con david ID
            mDatosNegocio.setClicod(MposApplication.getInstance().getDatosLogin().getDatosTpv().getClicod());
            advanceToNextStep();
        } else {
            if (MposApplication.getInstance().isBuildDebug()) {
                advanceToNextStep();
            }
        }
    }
}
