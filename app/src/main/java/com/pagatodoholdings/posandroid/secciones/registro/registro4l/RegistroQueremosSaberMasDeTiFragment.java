package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.transacciones.AbstractTransaccion;
import com.pagatodo.sigmalib.transacciones.TransaccionFactory;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroPaso3Binding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.general.interfaces.PreferenceManager;
import com.pagatodoholdings.posandroid.manager.FirebaseManager;
import com.pagatodoholdings.posandroid.secciones.registro.ConfigPaisListener;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegisterFirebaseService;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroClienteInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoDocBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoDocumentoInteractor;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.TerminosYCondicionesManager;
import com.pagatodoholdings.posandroid.utils.Utilities;
import com.pagatodoholdings.posandroid.utils.UtilsDate;
import com.pagatodoholdings.posandroid.utils.UtilsKt;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import net.fullcarga.android.api.sesion.DatosSesion;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIPO_USUARIO;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;
import static com.pagatodoholdings.posandroid.utils.Utilities.validateInputFields;
import static net.fullcarga.android.api.oper.TipoOperacion.INIT_LIST;
import static net.fullcarga.android.api.oper.TipoOperacion.LOGIN;

public class RegistroQueremosSaberMasDeTiFragment extends AbstractStepFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = RegistroQueremosSaberMasDeTiFragment.class.getSimpleName();
    private FragmentRegistroPaso3Binding binding;
    private ConfigPaisListener configPaisListener;
    private String messageError = "Error desconocido";
    private List<TipoDocBean> listDocumentsTypes;
    private Integer tipoDocCod;

    public static RegistroQueremosSaberMasDeTiFragment newInstance() {
        final RegistroQueremosSaberMasDeTiFragment fragment = new RegistroQueremosSaberMasDeTiFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ConfigPaisListener) {
            configPaisListener = (ConfigPaisListener) context;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_paso3, container, false);

        SpannableStringBuilder termsAndConds = TerminosYCondicionesManager.
                createSpannableForTermsAndCond(getActivity());

        binding.cbTerminosCondiciones.setText(termsAndConds);
        binding.cbTerminosCondiciones.setMovementMethod(LinkMovementMethod.getInstance());
        DatePickerDialog datePicker = UtilsDate.getMaterialDatePicker(this, 18);
        datePicker.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        datePicker.setOnDateSetListener(this);
        datePicker.setOnDismissListener(dialogInterface -> binding.etDni.performClick());
        configureFiltersEdittext();

        binding.btnValidar.setOnClickListener(view -> realizaRegistro());

        binding.etPrimerApellido.getEditText().setOnEditorActionListener((v, imeAction, event) -> {
            if (imeAction == EditorInfo.IME_ACTION_NEXT) {
                binding.etPrimerApellido.clearFocus();
                binding.etSegundoApellido.getEditText().requestFocus();
                return true;
            }
            return false;
        });

        binding.etFechaNacimiento.getEditText().setOnFocusChangeListener((view, hasFocus) -> {
            showDatePickerIfHasFocus(hasFocus, datePicker);
        });

        binding.etFechaNacimiento.getEditText().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (binding.etFechaNacimiento.getEditText().hasFocus()) {
                    if (!datePicker.isAdded()) {
                        datePicker.show(getActivity().getFragmentManager(), datePicker.getClass().getSimpleName());
                    }
                } else {
                    binding.etFechaNacimiento.getEditText().requestFocus();
                }
            }
            return true;
        });

        binding.tipoDocumento.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                binding.etDni.setHint(listDocumentsTypes.get(position).toString());
                setSelectedDocType(position);

                if(listDocumentsTypes.get(position).getDescripcion().contains("DNI")){
                    binding.editDNI.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
                    binding.etDni.setCounterMaxLength(8);
                }else if (listDocumentsTypes.get(position).getDescripcion().contains("Carnet")){
                    binding.editDNI.setFilters(new InputFilter[] {new InputFilter.LengthFilter(9)});
                    binding.etDni.setCounterMaxLength(9);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /*Not implemented*/}
        });

        binding.etCp.getEditText().setOnEditorActionListener((v, imeAction, event) -> {
            if (imeAction == EditorInfo.IME_ACTION_NEXT) {
                Utilities.hideSoftKeyboard(getActivity());
                realizaRegistro();
                return true;
            }
            return false;
        });

        binding.etFechaNacimiento.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //EMPTY
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputFields(binding.etFechaNacimiento);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //EMPTY
            }
        });

        bindDocumentsType();

        return binding.getRoot();
    }

    private void setSelectedDocType(final int position) {
        TipoDocBean tipoDocBean = listDocumentsTypes.get(position);
        tipoDocCod = tipoDocBean.getTipodoc();
    }

    private void configureFiltersEdittext() {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isLetterOrDigit(c) || Character.isSpaceChar(c);
            }
        };
        binding.etNombre.getEditText().setFilters(new InputFilter[]{filter});
        binding.etPrimerApellido.getEditText().setFilters(new InputFilter[]{filter});
        binding.etSegundoApellido.getEditText().setFilters(new InputFilter[]{filter});
    }

    private void showDatePickerIfHasFocus(boolean hasFocus, DatePickerDialog datePicker) {
        if (hasFocus) {
            datePicker.show(getActivity().getFragmentManager(), datePicker.getClass().getSimpleName());
        }
    }

    private boolean validateFields() {
        if (!binding.rbFemenino.isChecked() && !binding.rbMasculino.isChecked()) {
            ((AbstractActivity) getActivity()).despliegaModal(true, false, "Género", "Selecciona Género", null);
            return false;
        } else {
            return
                    validateInputFields(binding.etDni)
                            && validateInputFields(binding.etNombre)
                            && validateInputFields(binding.etPrimerApellido)
                            && validateInputFields(binding.etSegundoApellido)
                            && validateInputFields(binding.etFechaNacimiento);

        }
    }

    private void obtenerMensajeError(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            messageError = jsonObject.getString("message");
        } catch (JSONException e) {
            messageError = response;
            Logger.LOGGER.throwing(TAG, 1, new Throwable("Error"), "JSONException: " + e.getMessage());
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = UtilsDate.formatDatePickerDialog(dayOfMonth, monthOfYear, year);
        binding.etFechaNacimiento.getEditText().setText(date);
        binding.etFechaNacimiento.clearFocus();
        binding.etDni.getEditText().requestFocus();
    }

    private void realizaRegistro() {
        if (validateFields()) {
            if (binding.cbTerminosCondiciones.isChecked()) {
                saveUserInfo();
                showProgressDialog();
                registerUser(DatosUsuarioRegistroCliente.getInstace());
            } else {
                hideProgressDialog();
                ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.config_menu_legal_terminos_y_condiciones), getString(R.string.aceptar_terminos_condiciones), () -> {
                });
            }
        }
    }

    private void bindDocumentsType(){
        String baseUrl = (configPaisListener != null
                && configPaisListener.onReturnConfig() != null)
                ? configPaisListener.onReturnConfig().getUrlwalletpublica()
                : BuildConfig.URL_REGISTRO;

        new TipoDocumentoInteractor(baseUrl).getTiposDoc(new RetrofitListener<List<TipoDocBean>>() {
            @Override
            public void onSuccess(final List<TipoDocBean> tList) {
                if(tList!=null && !tList.isEmpty()) {
                    listDocumentsTypes = tList;
                    binding.etDni.setHint(listDocumentsTypes.get(0).toString());
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

    private void registerUser(final DatosUsuarioRegistroCliente datosUsuario) {
        String baseUrl = BuildConfig.URL_REGISTRO;

        new RegistroClienteInteractor(baseUrl).registroCliente(new RetrofitListener() {
                        @Override
                        public void showMessage(String message) {
                            hideProgressDialog();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            obtenerMensajeError(throwable.getMessage());
                            hideProgressDialog();
                            ((AbstractActivity) getActivity()).despliegaModal(true, false,
                                    "Error Registro", UtilsKt.capitalizeWords(messageError), () -> {
                                    });
                        }

                        @Override
                        public void onSuccess(Object result) {
                            DatosLogin dLogin = (DatosLogin) result;
                            PreferenceManager preferenceManager = MposApplication.getInstance().getPreferenceManager();
                            preferenceManager.guardarDatosUsuarioWallet(dLogin);
                            preferenceManager.setValue(TIPO_USUARIO, Utilities.TipoUsuario.SHOP.getTipo());

                            hideProgressDialog();
                            try {
                                loginSigma();
                            } catch (RuntimeException exc) {
                                hideProgressDialog();
                                ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.app_name), getString(R.string.contrasena_usuario_incorrecta), () -> {
                                });
                                LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                            } catch (SQLException exc) {
                                hideProgressDialog();
                    ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.app_name), getString(R.string.error_conexion_bd), () -> {
                    });
                    LOGGER.throwing(TAG, 1, exc, exc.getMessage());
                }
            }
        }, datosUsuario);
    }

    private void saveUserInfo() {
        //Guardando Datos
        DatosUsuarioRegistroCliente datos = DatosUsuarioRegistroCliente.getInstace();
        datos.setGenero(binding.rbFemenino.isChecked() ? "F" : "M");
        datos.setId(binding.etDni.getEditText().getText().toString());
        datos.setTipdoc(tipoDocCod);
        datos.setNombre(binding.etNombre.getEditText().getText().toString());
        datos.setApellido1(binding.etPrimerApellido.getEditText().getText().toString());
        datos.setApellido2(binding.etSegundoApellido.getEditText().getText().toString());
        datos.setFechaNacimiento(binding.etFechaNacimiento.getEditText().getText().toString());
        datos.setCodigopostal(binding.etCp.getEditText().getText().toString());
    }


    private void loginSigma() throws SQLException {
        final DatosSesion datosSesion = build();
        enviarLogin(datosSesion);
    }


    private void enviarLogin(final DatosSesion datosSesion) {//NOSONAR
        ApiData.APIDATA.setDatosSesion(datosSesion);
        showProgressDialog();
        AbstractTransaccion transaccion = TransaccionFactory.crearTransacion(LOGIN, abstractRespuesta -> {
            if (abstractRespuesta.isCorrecta()) {
                FirebaseManager.enablePushNotifications(MposApplication.getInstance().getDatosLogin().getCliente().getPais(), ApiData.APIDATA.getDatosSesion().getDatosTPV().getTpvcod());
                if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                    operSiguiente();
                } else {
                    if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())
                            && MposApplication.getInstance().getPreferenceManager().isExiste(Constantes.Preferencia.ICONZIP_NAME)
                    ) {
                        hideProgressDialog();
                        registerFirebase();
                        ApiData.APIDATA.setNotificationsID(getString(R.string.wallet_notificacion_path, BuildConfig.APPLICATION_ID,
                                MposApplication.getInstance().getDatosLogin().getCliente().getPais()));
                    } else {
                        hideProgressDialog();
                        showProgressDialog();
                        TransaccionFactory.crearTransacion(INIT_LIST,
                                abstractRespuesta1 -> {
                                    if (ApiData.APIDATA.getDatosOperacionSiguiente() != null) {
                                        operSiguiente();
                                    } else {
                                        if (StorageUtil.validarArchivo(StorageUtil.getSigmaDbPath())
                                                && MposApplication.getInstance().getPreferenceManager().isExiste(Constantes.Preferencia.ICONZIP_NAME)
                                        ) {
                                            hideProgressDialog();
                                            registerFirebase();
                                            ApiData.APIDATA.setNotificationsID(getString(R.string.wallet_notificacion_path, BuildConfig.APPLICATION_ID,
                                                    MposApplication.getInstance().getDatosLogin().getCliente().getPais()));
                                        }
                                    }
                                }
                                , throwable -> {
                                    hideProgressDialog();


                                    ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.generic_error), throwable.getMessage(), () -> {
                                    });
                                }).realizarOperacion();
                    }
                }
            } else {
                final String errorMsj = abstractRespuesta.getMsjError();
                getActivity().runOnUiThread(() -> {
                    hideProgressDialog();
                    ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.generic_error), errorMsj, () -> {
                    });
                });
            }
        }, throwable -> {

            getActivity().runOnUiThread(() -> {
                hideProgressDialog();
                ((AbstractActivity) getActivity()).despliegaModal(true, false, getString(R.string.generic_error), throwable.getMessage(), () -> {
                });
            });
        });
        transaccion.realizarOperacion();
    }


    private void operSiguiente() {
        TransaccionFactory.crearTransacion(ApiData.APIDATA.getDatosOperacionSiguiente().getTipoOperacion(),
                ApiData.APIDATA.getDatosOperacionSiguiente().getOnSuccessListener(), ApiData.APIDATA.getDatosOperacionSiguiente().getOnFailureListener())
                .realizarOperacion();
    }


    private void registerFirebase() {

        //Validar si existen los datos de compra del kit
        RegisterFirebaseService service = new RegisterFirebaseService();

        service.registerFirebase(ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(), new RetrofitListener<Boolean>() {
                    @Override
                    public void showMessage(String s) {
                        //No implements
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                        advanceToNextStep();

                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        LOGGER.fine(TAG, "ALTA FIREBASE SIGMA SUCCESS");
                        advanceToNextStep();
                    }
                });
    }


}
