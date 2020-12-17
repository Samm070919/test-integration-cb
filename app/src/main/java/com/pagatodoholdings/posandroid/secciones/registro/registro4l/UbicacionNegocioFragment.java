package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentUbicacionNegocioBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.registro.externo.RegistroDatosNegocio;
import com.pagatodoholdings.posandroid.secciones.retrofit.NivelBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroDatosNegocioInteractor;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.TerminosYCondicionesManager;
import com.pagatodoholdings.posandroid.utils.Utilities;

import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIPO_USUARIO;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.validateInputFields;

public class UbicacionNegocioFragment extends AbstractStepFragment {

    private FragmentUbicacionNegocioBinding binding;
    private RegistroInteractor registroInteractor;
    private static final String TAG = UbicacionNegocioFragment.class.getSimpleName();
    private String messageError = "";
    private List<String> nivelesList;
    private RegistroDatosNegocio activity;
    private int idCallingActivity = 0;
    private final ModalFragment.CommonDialogFragmentCallBackWithCancel conectionErrorHandler = new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
        @Override
        public void onCancel() {
            activity.finish();
        }

        @Override
        public void onAccept() {
            cargarNiveles();
        }
    };

    public UbicacionNegocioFragment(int idCallingActivity) {
        // Required empty public constructor
        this.idCallingActivity = idCallingActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());

        activity = (RegistroDatosNegocio) getActivity();
        registroInteractor = new RegistroInteractor();
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ubicacion_negocio, container, false);
        binding.textViewOtroMomento.setOnClickListener( v -> {
            if(idCallingActivity == 0) {
                String registroCuenta = MposApplication.getInstance().getPreferenceManager()
                        .getValue(com.pagatodo.sigmalib.util.Constantes.Preferencia.REGISTRO_CUENTA, null);
                if (registroCuenta != null && registroCuenta.equalsIgnoreCase("Registrada")) {
                    ((AbstractActivity) getActivity()).goToLogin();
                }else{
                    ((AbstractActivity) getActivity()).cambiaDeActividad(RegistroAgregaCuentaActivity.class);
                }
            }else{
                Intent output = new Intent();
                output.putExtra("DECODED", idCallingActivity);
                getActivity().setResult(Constantes.REQUEST_ADD_CARD_BY_MENU, output);
                getActivity().finish();
            }
        });
        binding.tvTerminosCondiciones.setText(TerminosYCondicionesManager.createSpannableForTermsAndCond(getActivity()));
        binding.tvTerminosCondiciones.setMovementMethod(LinkMovementMethod.getInstance());
        setWidgetReferences();
        return binding.getRoot();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            registroInteractor = new RegistroInteractor();
            cargarNiveles();
        }
    }

    private void setWidgetReferences() {

        binding.spinnerNivel0.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long mId) {
                if (nivelesList.size() > 1) {
                    cargarNivel1(((GenericAdaptadorSpinner<NivelBean>) adapterView.getAdapter()).getItem(index).getNivelcod());
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                // nothing..
            }
        });

        binding.spinnerNivel1.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long rowId) {
                if (nivelesList.size() > 2) {
                    cargarNivel2(((GenericAdaptadorSpinner<NivelBean>) adapterView.getAdapter()).getItem(index).getNivelcod());
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                /* No usada actualmente*/
            }
        });

        binding.spinnerNivel2.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> adapterView, final View view, final int index, final long rowId) {
                if (nivelesList.size() > 3) {
                    cargarNivel3(((GenericAdaptadorSpinner<NivelBean>) adapterView.getAdapter()).getItem(index).getNivelcod());
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> adapterView) {
                /* No usada actualmente*/
            }
        });

        //el botón de continuar desencadena la validación de edit texts
        binding.bContinuar.setOnClickListener(v -> realizarRegistro());

        binding.etCodigoPostal.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Utilities.hideSoftKeyboard(getActivity());
                realizarRegistro();
                return true;
            }
            return false;
        });
    }

    public static Fragment newInstance(int idCallingActivity) {
        final UbicacionNegocioFragment fragment = new UbicacionNegocioFragment(idCallingActivity);
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    void cargarNiveles() {
        activity.muestraProgressDialog(getString(R.string.registro_dialog_cargando_niveles));
        registroInteractor.getNiveles(new RetrofitListener<List<String>>() {
            @Override
            public void onSuccess(final List<String> tList) {
                activity.ocultaProgressDialog();
                nivelesList = tList;
                if (tList != null && !tList.isEmpty()) {
                    cargarNivel0();
                }
            }

            @Override
            public void onFailure(final Throwable thr) {
                activity.ocultaProgressDialog();
                despliegaModal();
                LOGGER.throwing(TAG, 1, thr, "Error al cargar combo niveles");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    private void despliegaModal() {
        activity.despliegaModal(
                true,
                false,
                getString(R.string.registro_dialog_cargando_error),
                getString(R.string.registro_dialog_cargando_error_internet),
                conectionErrorHandler);
    }

    void cargarNivel0() {
        binding.spinnerNivel0.obtenTvTitulo().setText(nivelesList.get(0));
        activity.muestraProgressDialog(getString(R.string.registro_dialog_cargando_estados, nivelesList.get(0)));
        registroInteractor.cargarNivel0(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                activity.ocultaProgressDialog();
                tList.add(0, new NivelBean(0,""));
                binding.spinnerNivel0.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(activity, tList));
            }

            @Override
            public void onFailure(final Throwable thr) {
                activity.ocultaProgressDialog();
                despliegaModal();
                LOGGER.throwing(TAG, 2, thr, "Error al cargar combo nivel 0");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    void cargarNivel1(final int idNivel1) {
        binding.spinnerNivel1.obtenTvTitulo().setText(nivelesList.get(1));
        activity.muestraProgressDialog(getString(R.string.registro_dialog_cargando_municipios, nivelesList.get(1)));
        registroInteractor.getNivel1(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                activity.ocultaProgressDialog();
                binding.spinnerNivel1.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(activity, tList));
            }

            @Override
            public void onFailure(final Throwable thr) {
                activity.ocultaProgressDialog();
                despliegaModal();
                LOGGER.throwing(TAG, 3, thr, "Error al cargar combo nivel 1");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        }, idNivel1);
    }

    void cargarNivel2(final int idNivel2) {
        binding.spinnerNivel2.obtenTvTitulo().setText(nivelesList.get(2));
        activity.muestraProgressDialog(getString(R.string.registro_dialog_cargando_colonias, nivelesList.get(2)));
        registroInteractor.getNivel2(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                activity.ocultaProgressDialog();
                tList.add(0, new NivelBean(0,""));
                binding.spinnerNivel2.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(activity, tList));
            }

            @Override
            public void onFailure(final Throwable thr) {
                activity.ocultaProgressDialog();
                despliegaModal();
                LOGGER.throwing(TAG, 4, thr, "Error al cargar combo nivel 2");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        }, idNivel2);
    }

    void cargarNivel3(final int idNivel3) {
        binding.spinnerNivel3.obtenTvTitulo().setText(nivelesList.get(3));
        activity.muestraProgressDialog(getString(R.string.registro_dialog_cargando_comunas, nivelesList.get(3)));
        registroInteractor.getNivel3(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                activity.ocultaProgressDialog();
                if (tList.isEmpty()) {
                    binding.spinnerNivel3.setVisibility(View.GONE);
                } else {
                    binding.spinnerNivel3.setVisibility(View.VISIBLE);
                    tList.add(0, new NivelBean(0,""));
                    binding.spinnerNivel3.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(activity, tList));
                }
            }

            @Override
            public void onFailure(final Throwable thr) {
                activity.ocultaProgressDialog();
                despliegaModal();
                LOGGER.throwing(TAG, 5, thr, "Error al cargar combo nivel 3");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        }, idNivel3);
    }

    private void realizarRegistro() {
        if(nivelesList.size() > 0) {
            final NivelBean nivel0Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel0.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel0.obtenSpinnerContenido().getSelectedItemPosition());
            if (nivel0Bean.getDescripcion().isEmpty()) {
                despliegaModalError(getString(R.string.error_direccion), MposApplication.getInstance().getString(R.string.error_direccion_nivel_empty, nivelesList.get(0)));
                return;
            }
        }

        if(nivelesList.size() > 1) {
            final NivelBean nivel1Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel1.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel1.obtenSpinnerContenido().getSelectedItemPosition());
            if (nivel1Bean.getDescripcion().isEmpty()) {
                despliegaModalError(getString(R.string.error_direccion), MposApplication.getInstance().getString(R.string.error_direccion_nivel_empty, nivelesList.get(1)));
                return;
            }
        }

        if(nivelesList.size() > 2) {
            final NivelBean nivel2Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel2.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel2.obtenSpinnerContenido().getSelectedItemPosition());
            if (nivel2Bean.getDescripcion().isEmpty()) {
                despliegaModalError(getString(R.string.error_direccion), MposApplication.getInstance().getString(R.string.error_direccion_nivel_empty, nivelesList.get(2)));
                return;
            }
        }

        if(!validateInputFields(binding.etDireccionComercial)) {
            despliegaModalError(getString(R.string.error_direccion), getString(R.string.direccion_incorrecta));
        } else if (!binding.cbTerminosCondiciones.isChecked()) {
            despliegaModalError(getString(R.string.config_menu_legal_terminos_y_condiciones), getString(R.string.aceptar_terminos_condiciones));
        } else {
            DatosNegocio datosNegocio = DatosNegocio.getInstance();
            datosNegocio.setDireccion(binding.etDireccionComercial.getEditText().getText().toString());
            datosNegocio.setTipdoc(MposApplication.getInstance().getTipodoc());

            if (nivelesList.size() > 1) {
                final NivelBean nivel2Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel2.obtenAdaptadorNivlesPoliticos())
                        .getItem(binding.spinnerNivel2.obtenSpinnerContenido().getSelectedItemPosition());
                datosNegocio.setNivel2(nivel2Bean.getNivelcod());
            } else {
                return;
            }

            showProgressDialog ();

            RetrofitListener retrofitListener = new RetrofitListener() {
                @Override
                public void showMessage(String message) {
                    hideProgressDialog();
                    despliegaModalError("Error Registro", message);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    hideProgressDialog();
                    messageError = Utilities.obtenerMensajeError(throwable);
                    despliegaModalError("Error Registro", messageError);
                }

                @Override
                public void onSuccess(Object result) {
                    hideProgressDialog();
                    MposApplication.getInstance().setDatosLogin((DatosLogin) result);
                    PreferenceManager preferenceManager =  MposApplication.getInstance().getPreferenceManager();
                    preferenceManager.setValue(TIPO_USUARIO,Utilities.TipoUsuario.PROF.getTipo());
                    activity.nextStep();
                }
            };

            registrarDatosNegocio(
                    datosNegocio,
                    MposApplication.getInstance().getDatosLogin().getToken(),
                    retrofitListener
            );
        }
    }

    private void registrarDatosNegocio(DatosNegocio datosNegocio, String token, RetrofitListener retrofitListener) {
        final RegistroDatosNegocioInteractor registroDatosnegocioInteractor = new RegistroDatosNegocioInteractor();
        registroDatosnegocioInteractor.registroDatosNegocio(datosNegocio, token, retrofitListener);
    }

    private void despliegaModalError(String s, String message) {
        ((AbstractActivity) getActivity()).despliegaModal(true, false, s, message, () -> {});
    }
}
