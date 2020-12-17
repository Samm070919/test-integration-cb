package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentAjustesCambioContraseABinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.retrofit.ContrasenaServiceInteractor;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigMenuCambioContrasenaFragment extends AbstractConfigMenu {

    public static final String ARG_EMAIL = "arg-email";
    private FragmentAjustesCambioContraseABinding binding;
    private String nuevaContrasena;
    private String codigo;
    private AbstractActivity abstractActivity;
    private String correoElectronico;
    private boolean notFirstSent;

    public static ConfigMenuCambioContrasenaFragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);

        ConfigMenuCambioContrasenaFragment fragment = new ConfigMenuCambioContrasenaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        if (getArguments() != null) {
            correoElectronico = getArguments().getString(ARG_EMAIL);
        }

        abstractActivity = (AbstractActivity) getActivity();
        binding = FragmentAjustesCambioContraseABinding.inflate(infalter, container, false);
        detenerContador();

        binding.btnAceptar.setOnClickListener(v -> cambiarContrasena());
        binding.TextViewFooterIntentarDeNuevo.setOnClickListener(v -> {
            if (notFirstSent) {
                renovarContrasena();
            } else {
                showDialog(R.layout.layout_confirmar_contrasena, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                    @Override
                    public void onCancel() {
//                        if (getActivityListener() == null) {
//                            loadMiCuenta(ConfigMenuCambioContrasenaFragment.this);
//                        } else {
//                            getActivityListener().finish();
//                        }
                    }

                    @Override
                    public void onAccept() {
                        renovarContrasena();
                        notFirstSent = true;
                        binding.TextViewFooterIntentarDeNuevo.setText("Enviar Código de Nuevo");
                    }
                });
            }
        });

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            detenerContador();
            return false;
        });
        binding.etContraseAActual.obtenEtCampo().setOnEditorActionListener((v, imeAction, event) -> {
            if (imeAction == EditorInfo.IME_ACTION_NEXT) {
                binding.etContraseAActual.clearFocus();
                binding.etNuevaContraseA.obtenEtCampo().requestFocus();
                return true;
            }
            return false;
        });
        binding.etNuevaContraseA.estableceAccionIme((textView, imeAction, keyEvent) -> {

            if (imeAction == EditorInfo.IME_ACTION_DONE) {
                binding.etNuevaContraseA.clearFocus();
                binding.etConfNuevaContraseA.obtenEtCampo().requestFocus();
                return true;
            }

            return false;
        });
        binding.etConfNuevaContraseA.estableceAccionIme((textView, imeAction, keyEvent) -> {

            if (imeAction == EditorInfo.IME_ACTION_DONE) {
                Utilities.hideSoftKeyboard(getActivity());
                cambiarContrasena();
                return true;
            }

            return false;
        });
    }

    private void renovarContrasena() {
        binding.TextViewFooterIntentarDeNuevo.setEnabled(false);
        binding.TextViewFooterIntentarDeNuevo.setTextColor(ContextCompat.getColor(this.getContext(), R.color.colorGrey));

        if (correoElectronico == null) {
            correoElectronico = MposApplication
                    .getInstance()
                    .getPreferenceManager()
                    .getValue(Constantes.Preferencia.EMAIL, "");
        }

        final ContrasenaServiceInteractor renovarContrasena = new ContrasenaServiceInteractor();
        renovarContrasena.renovarService(correoElectronico,
                new RetrofitListener() {
                    @Override
                    public void showMessage(String message) {
                        //No implementation
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        binding.TextViewFooterIntentarDeNuevo.setEnabled(true);
                        binding.TextViewFooterIntentarDeNuevo.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAzulSolid1));
                        obtenerMensajeError(throwable.getMessage());
                        showDialogContrasena(R.layout.layout_cambio_contrasena,
                                getString(R.string.menu_ajustes_opcion_contraseña),
                                obtenerMensajeError(throwable.getMessage()),
                                new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                                    @Override
                                    public void onCancel() {
                                        //No implementation
                                    }

                                    @Override
                                    public void onAccept() {
                                        //not implemented yet
                                    }
                                });
                    }

                    @Override
                    public void onSuccess(Object result) {
                        binding.TextViewFooterIntentarDeNuevo.setEnabled(true);
                        binding.TextViewFooterIntentarDeNuevo.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAzulSolid1));
                        showDialogContrasena(R.layout.layout_cambio_contrasena,
                                "Cambio de Contraseña",
                                getString(R.string.message_envio_correo),
                                new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                                    @Override
                                    public void onCancel() {
                                        //No implementation
                                    }

                                    @Override
                                    public void onAccept() {
                                        //No implementation
                                    }
                                });
                    }
                }
        );
    }

    private boolean coincideContrasena() {
        String confirmaContrasena;
        nuevaContrasena = binding.etNuevaContraseA.obtenEtCampo().getText().toString().toLowerCase();
        confirmaContrasena = binding.etConfNuevaContraseA.obtenEtCampo().getText().toString().toLowerCase();
        codigo = binding.etContraseAActual.obtenEtCampo().getText().toString().toLowerCase();

        if (codigo == null || codigo.length() == 0) {
            abstractActivity.despliegaModal(true, false, getResources().getString(R.string.write_code_tittle), getResources().getString(R.string.write_code), null);
            return false;
        }

        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            abstractActivity.despliegaModal(true, false, getResources().getString(R.string.new_password_title), getResources().getString(R.string.new_password_length), null);
            return false;
        }

        if (confirmaContrasena == null || confirmaContrasena.length() < 6) {
            abstractActivity.despliegaModal(true, false, getResources().getString(R.string.new_password_confirm_tittle), getResources().getString(R.string.new_password_confirm), null);
            return false;
        }

        if (nuevaContrasena.contains(confirmaContrasena)) {
            return true;
        } else {
            abstractActivity.despliegaModal(true, false, getResources().getString(R.string.password_confirm_error_tittle), getResources().getString(R.string.password_confirm_error), null);
            return false;
        }

    }

    private void cambiarContrasena() {
        if (coincideContrasena()) {
            if (correoElectronico == null) {
                correoElectronico = MposApplication
                        .getInstance()
                        .getPreferenceManager()
                        .getValue(Constantes.Preferencia.EMAIL, "");
            }

            final ContrasenaServiceInteractor cambiarContrasena = new ContrasenaServiceInteractor();
            cambiarContrasena.cambiarService(correoElectronico,
                    codigo.trim(), nuevaContrasena.trim(),
                    new RetrofitListener() {
                        @Override
                        public void showMessage(String message) {
                            //No implementation
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            obtenerMensajeError(throwable.getMessage());
                            showDialogContrasena(R.layout.layout_cambio_contrasena,
                                    getString(R.string.menu_ajustes_opcion_contraseña),
                                    obtenerMensajeError(throwable.getMessage()),
                                    new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                                        @Override
                                        public void onCancel() {
                                            //No implementation
                                        }

                                        @Override
                                        public void onAccept() {
                                            //No implementation
                                        }
                                    });


                        }

                        @Override
                        public void onSuccess(Object result) {
                            showDialogContrasena(R.layout.layout_cambio_contrasena,
                                    getString(R.string.menu_title_ajustes_cambio_contraseña),
                                    getString(R.string.message_cambio_contrasena_success_body),
                                    new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                                        @Override
                                        public void onCancel() {
                                            //No implementation
                                        }

                                        @Override
                                        public void onAccept() {
                                            cerrarSessionApp();
                                        }
                                    });

                        }
                    }
            );
        }
    }

    private String obtenerMensajeError(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString("message");
        } catch (JSONException e) {
            Logger.LOGGER.throwing(TAG, 1, new Throwable("Error"), "JSONException: " + e.getMessage());
            return response;
        }
    }

    private void cerrarSessionApp() {
        ((AbstractActivity) getActivity()).finishApp();
    }

    private void detenerContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.detenerTemporizador();
        }
    }

    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btnConfirmacion);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        final BotonClickUnico btnCancelar = view.findViewById(R.id.btnCancel);
        btnCancelar.setText(getString(R.string.cancelar));
        btnCancelar.setTextSize(14);
        btnCancelar.setOnClickListener(view12 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    public void showDialogContrasena(int layout,
                                     String title,
                                     String body,
                                     ModalFragment.CommonDialogFragmentCallBackWithCancel callback
    ) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btnConfirmacion);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        final TextView textTitle = view.findViewById(R.id.title_toolbar);
        textTitle.setText(title);
        final TextView textBody = view.findViewById(R.id.texto_body);
        textBody.setText(body);
        alertDialog.show();
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No definition
    }
}
