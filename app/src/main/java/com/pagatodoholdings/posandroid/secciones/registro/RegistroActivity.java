package com.pagatodoholdings.posandroid.secciones.registro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.analytics.Event;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.ActivityRegistroBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.desbloqueo.DesbloqueoActivity;
import com.pagatodoholdings.posandroid.secciones.retrofit.NivelBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroBean;
import com.pagatodoholdings.posandroid.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.ESTADO_REGISTRO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NUMERO_SERIE;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

@SuppressWarnings("PMD.GodClass")
public class RegistroActivity extends AbstractActivity { //NOSONAR
    //----------UI-------------------------------------------------------
    private ActivityRegistroBinding binding;
    //----- Var ----------------------------------------------------------
    String ubicacionActual = "";
    String messageError = "Error desconocido";
    private static final String TAG = RegistroActivity.class.getSimpleName();
    //-----Inst ----------------------------------------------------------
    private FusedLocationProviderClient mFusedLocationClient;

    private RegistroInteractor registroInteractor;
    private final ModalFragment.CommonDialogFragmentCallBackWithCancel conectionErrorHandler = new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
        @Override
        public void onCancel() {
            finish();
        }

        @Override
        public void onAccept() {
            cargarNiveles();
        }
    };

    //----- Var ----------------------------------------------------------
    private List<String> nivelesList;

    protected void initUi() {
        registroInteractor = new RegistroInteractor();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_registro);
        preferenceManager.setValue(NUMERO_SERIE, tipoConfiguracion.getNumeroSerie(preferenceManager, buildManager));
        binding.btnRegistro.setOnClickListener(view -> {
            if (validaCampos()) {
                registrar();
            }
        });
        binding.radioTipoPersonal.setChecked(true);
        binding.etdCodigoClienteFullcarga.ponEsOpcional(true);
        binding.etdOtraDireccion.ponEsOpcional(true);
        binding.etdTelefonoAlternativo.ponEsOpcional(true);
        binding.etdComentarios.ponEsOpcional(true);

        binding.checkboxCodigo.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            final int visibilidad = isChecked ? View.VISIBLE : View.GONE;
            binding.etdCodigoClienteFullcarga.ponEsOpcional(!isChecked);
            binding.etdCodigoClienteFullcarga.setVisibility(visibilidad);
            findViewById(R.id.tv_ayuda_codigo_cliente).setVisibility(visibilidad);
            if (!isChecked) {
                binding.etdCodigoClienteFullcarga.obtenEtCampo().setText("");
            }
        });

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
        cargarNiveles();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerUbicacion();
    }

    void obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            habilitarUbicacion();
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            ubicacionActual = Double.toString(location.getLatitude()) + "," +
                                    Double.toString(location.getLongitude());
                        }
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

    void cargarNiveles() {
        muestraProgressDialog(getString(R.string.registro_dialog_cargando_niveles));
        registroInteractor.getNiveles(new RetrofitListener<List<String>>() {
            @Override
            public void onSuccess(final List<String> tList) {
                ocultaProgressDialog();
                RegistroActivity.this.nivelesList = tList;
                if (tList != null && !tList.isEmpty()) {
                    cargarNivel0();
                }
            }

            @Override
            public void onFailure(final Throwable thr) {
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_dialog_cargando_error), getString(R.string.registro_dialog_cargando_error_internet), conectionErrorHandler);
                LOGGER.throwing(TAG, 1, thr, "Error al cargar combo niveles");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    void cargarNivel0() {
        binding.spinnerNivel0.obtenTvTitulo().setText(nivelesList.get(0));
        muestraProgressDialog(getString(R.string.registro_dialog_cargando_estados, nivelesList.get(0)));
        registroInteractor.cargarNivel0(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                ocultaProgressDialog();
                binding.spinnerNivel0.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(RegistroActivity.this, tList));
            }

            @Override
            public void onFailure(final Throwable thr) {
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_dialog_cargando_error), getString(R.string.registro_dialog_cargando_error_internet), conectionErrorHandler);
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
        muestraProgressDialog(getString(R.string.registro_dialog_cargando_municipios, nivelesList.get(1)));
        registroInteractor.getNivel1(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                ocultaProgressDialog();
                binding.spinnerNivel1.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(RegistroActivity.this, tList));
            }

            @Override
            public void onFailure(final Throwable thr) {
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_dialog_cargando_error), getString(R.string.registro_dialog_cargando_error_internet), conectionErrorHandler);
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
        muestraProgressDialog(getString(R.string.registro_dialog_cargando_colonias, nivelesList.get(2)));
        registroInteractor.getNivel2(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                ocultaProgressDialog();
                binding.spinnerNivel2.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(RegistroActivity.this, tList));
            }

            @Override
            public void onFailure(final Throwable thr) {
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_dialog_cargando_error), getString(R.string.registro_dialog_cargando_error_internet), conectionErrorHandler);
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
        muestraProgressDialog(getString(R.string.registro_dialog_cargando_comunas, nivelesList.get(3)));
        registroInteractor.getNivel3(new RetrofitListener<List<NivelBean>>() {
            @Override
            public void onSuccess(final List<NivelBean> tList) {
                ocultaProgressDialog();
                if (tList.isEmpty()) {
                    binding.spinnerNivel3.setVisibility(View.GONE);
                } else {
                    binding.spinnerNivel3.setVisibility(View.VISIBLE);
                    binding.spinnerNivel3.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(RegistroActivity.this, tList));
                }
            }

            @Override
            public void onFailure(final Throwable thr) {
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_dialog_cargando_error), getString(R.string.registro_dialog_cargando_error_internet), conectionErrorHandler);
                LOGGER.throwing(TAG, 5, thr, "Error al cargar combo nivel 3");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        }, idNivel3);
    }

    void registrar() {
        muestraProgressDialog(getString(R.string.cargando));
        registroInteractor.registro(crearRegistroBean(), new RetrofitListener<RegistroBean>() {
            @Override
            public void onSuccess(final RegistroBean registroBeanResponse) {
                despliegaModal(false, false, getString(R.string.exito), getString(R.string.registro_exitoso), () -> {
                    firebaseAnalytics.logEvent(Event.EVENT_COMPLETE_REGISTRO.key, null);
                    preferenceManager.setValue(ESTADO_REGISTRO, Constantes.EstadosRegistro.ENVIADO.name());
                    cambiaDeActividad(DesbloqueoActivity.class);
                });
            }

            @Override
            public void onFailure(final Throwable thr) {
                obtenerMensajeError(thr.getMessage());
                firebaseAnalytics.logEvent(Event.EVENT_FAIL_REGISTRO.key, null);
                ocultaProgressDialog();
                despliegaModal(true, false, getString(R.string.registro_fallido), messageError, null);
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    private void obtenerMensajeError(String response)
    {
        try {
            JSONObject jsonObject = new JSONObject(response);
            messageError = jsonObject.getString("message");
        } catch (JSONException e) {
            messageError= response;
            Logger.LOGGER.throwing(TAG, 1, new Throwable("Error"), "JSONException: " + e.getMessage());
        }
    }

    RegistroBean crearRegistroBean() {
        final RegistroBean registroBean = new RegistroBean();

        final String serialNumber = preferenceManager.getValue(NUMERO_SERIE, "");
        registroBean.setSerie(serialNumber);
        if (binding.etdCodigoClienteFullcarga.getVisibility() == View.VISIBLE) {
            registroBean.setClicod(Long.parseLong(binding.etdCodigoClienteFullcarga.obtenEtCampo().getText().toString()));
        } else {
            registroBean.setClicod(0L);
        }
        registroBean.setNombre(binding.etdNombre.obtenEtCampo().getText().toString());
        registroBean.setApellido1(binding.etdPrimerApellido.obtenEtCampo().getText().toString());
        registroBean.setApellido2(binding.etdSegundoApellido.obtenEtCampo().getText().toString());
        registroBean.setTipodoc(0);
        registroBean.setDocid(binding.etdDocumento.obtenEtCampo().getText().toString());

        if (!nivelesList.isEmpty()) {
            final NivelBean nivel1Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel0.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel0.obtenSpinnerContenido().getSelectedItemPosition());
            registroBean.setLocalidad1(nivel1Bean.getDescripcion());
            registroBean.setCodLocalidad1(nivel1Bean.getNivelcod());
        } else {
            registroBean.setLocalidad1("");
            registroBean.setCodLocalidad1(0);
        }

        if (nivelesList.size() > 1) {
            final NivelBean nivel2Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel1.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel1.obtenSpinnerContenido().getSelectedItemPosition());
            registroBean.setLocalidad2(nivel2Bean.getDescripcion());
            registroBean.setCodLocalidad2(nivel2Bean.getNivelcod());
        } else {
            registroBean.setLocalidad2("");
            registroBean.setCodLocalidad2(0);
        }

        if (nivelesList.size() > 2) {
            final NivelBean nivel3Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel2.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel2.obtenSpinnerContenido().getSelectedItemPosition());
            registroBean.setLocalidad3(nivel3Bean.getDescripcion());
            registroBean.setCodLocalidad3(nivel3Bean.getNivelcod());
        } else {
            registroBean.setLocalidad3("");
            registroBean.setCodLocalidad3(0);
        }

        if (nivelesList.size() > 3) {
            final NivelBean nivel3Bean = ((GenericAdaptadorSpinner<NivelBean>) binding.spinnerNivel3.obtenAdaptadorNivlesPoliticos())
                    .getItem(binding.spinnerNivel3.obtenSpinnerContenido().getSelectedItemPosition());
            registroBean.setLocalidad4(nivel3Bean.getDescripcion());
            registroBean.setCodLocalidad4(nivel3Bean.getNivelcod());
        } else {
            registroBean.setLocalidad4("");
            registroBean.setCodLocalidad4(0);
        }

        registroBean.setDireccion(binding.etdDireccion.obtenEtCampo().getText().toString());
        registroBean.setCelular(binding.etdCelular.obtenEtCampo().getText().toString());
        registroBean.setTelefono1(binding.etdTelefonoAlternativo.obtenEtCampo().getText().toString());
        registroBean.setEmail(binding.etdCorreo.obtenEtCampo().getText().toString());
        registroBean.setComentarios(binding.etdComentarios.obtenEtCampo().getText().toString());
        registroBean.setModelo(registroInteractor.getDeviceInfo());
        obtenerUbicacion();
        registroBean.setLocalizacion(ubicacionActual);
        registroBean.setAppInstalacion(configManager.getVersionBdApp() + "(" + configManager.getVersionCode() + ")" + "|" + serialNumber);
        return registroBean;
    }

    //----------Override Methods-------------------------------------------------------

    @Override
    protected boolean validaCampos() {
        for (final EditTextDatosUsuarios campoValidar : registraCamposValidar()) {
            if (campoValidar.estaVacio() && !campoValidar.esOpcional()) {
                campoValidar.obtenEtCampo().setError(getString(R.string.campo_requerido));
                campoValidar.obtenEtCampo().requestFocus();
                return false;
            }
        }
        if (!binding.etdCorreo.esCorreo()) {
            binding.etdCorreo.obtenEtCampo().setError(getString(R.string.email_invalido));
            binding.etdCorreo.obtenEtCampo().requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected List<EditTextDatosUsuarios> registraCamposValidar() {
        return Arrays.asList(
                binding.etdNombre,
                binding.etdPrimerApellido,
                binding.etdSegundoApellido,
                binding.etdDocumento,
                binding.etdDireccion,
                binding.etdOtraDireccion,
                binding.etdCelular,
                binding.etdTelefonoAlternativo,
                binding.etdCorreo,
                binding.etdComentarios,
                binding.etdCodigoClienteFullcarga
        );
    }

    @Override
    protected void realizaAlPresionarBack() {
        finish();
    }
}