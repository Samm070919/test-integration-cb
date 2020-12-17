package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.Constantes;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.CustomProgressLoader;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoVerificacionEditText;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuInfoBancariaBinding;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroAgregaCuentaActivity;
import com.pagatodoholdings.posandroid.secciones.retrofit.AltaCuentaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.CuentaBancariaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosCuentaBancaria;
import com.pagatodoholdings.posandroid.secciones.retrofit.MisDatosService;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroCuentaBancariaInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoCuentaBancariaBean;
import com.pagatodoholdings.posandroid.utils.UtilsKt;

import net.fullcarga.android.api.sesion.DatosSesion;

import java.util.ArrayList;
import java.util.List;

import io.grpc.okhttp.internal.Util;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.REGISTRO_CUENTA;
import static com.pagatodoholdings.posandroid.utils.Constantes.ACTIVITY_CODE_KEY;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_ACCOUNT_BY_MENU;
import static com.pagatodoholdings.posandroid.utils.Constantes.REQUEST_ADD_CARD_BY_MENU;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

public class ConfigMenuInfoBancariaFragment extends AbstractConfigMenu {

    private FragmentConfigMenuInfoBancariaBinding binding;
    private MisDatosService misDatosService;
    private DatosCuentaBancaria datosCuenta;
    private int montoVerificarCuenta = 0;

    private CustomProgressLoader loader;
    private List<CuentaBancariaBean> mCuentaBancariaBeans;
    private List<TipoCuentaBancariaBean> mTipoCuentaBancariaBeans;
    private AccountUpdate listener;

    private DatosCuentaBancaria datosCuentaBancaria;

    public ConfigMenuInfoBancariaFragment(DatosCuentaBancaria datosCuentaBancaria, AccountUpdate listener) {
        this.datosCuentaBancaria = datosCuentaBancaria;
        this.listener = listener;
    }

    private static final String paramCountry = "param-country";

    public static ConfigMenuInfoBancariaFragment newInstance(int country) {
        Bundle args = new Bundle();
        args.putInt(paramCountry, country);
        ConfigMenuInfoBancariaFragment fragment = new ConfigMenuInfoBancariaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ConfigMenuInfoBancariaFragment() {
        // Required empty public constructor
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        misDatosService = new MisDatosService();
        binding = DataBindingUtil.inflate(infalter, R.layout.fragment_config_menu_info_bancaria, container, false);
        cargarDatosCuenta();

        binding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuInfoBancariaFragment.this));
        binding.botonUnicoRegresar.setOnClickListener(v -> {
            binding.ivDeleteIcon.setVisibility(View.GONE);
            loadMiCuenta(ConfigMenuInfoBancariaFragment.this);
        });
        binding.btnEditarBancaria.setOnClickListener(onBtnEditEvent);
        loader = new CustomProgressLoader(requireActivity());

        bindListeners();
    }

    private void cargarDatosCuenta() {
        getListener().muestraProgressDialog(getResources().getString(R.string.cta_bca_load));
        misDatosService.getDatosCuentaBancaria(
                new RetrofitListener<DatosCuentaBancaria>() {
                    @Override
                    public void showMessage(String message) {
                        //No implementation
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.throwing(TAG, 1, throwable, getResources().getString(R.string.cta_bca_title_error_consult_data));
                        getListener().ocultaProgressDialog();
                        getListener().despliegaModal(true, false, getResources().getString(R.string.generic_error),
                                "No se pudieron cargar los Datos Bancarios", () ->
                                        loadMiCuenta(ConfigMenuInfoBancariaFragment.this));
                    }

                    @Override
                    public void onSuccess(DatosCuentaBancaria datosCuentaBancaria) {
                       
                        if (datosCuentaBancaria != null) {
                            validarCuentaBancaria(datosCuentaBancaria);
                            //Inicializamos el objeto para mantenerlo en la vista
                            datosCuenta = datosCuentaBancaria;
                            cargarCuentaBancaria(datosCuentaBancaria);

                            //Si la Cuenta esta validada se modifica TextView y se Cambia el Icono del Botón, de lo contrario se deshabilita
                            if (datosCuentaBancaria.isCtavalidada()) {
                                showCuentaActivada();
                            } else {
                                binding.tvCtaVerificada.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorSoftRed));
                                binding.tvCtaVerificada.setText(getResources().getString(R.string.cta_bca_title_no_validate));
                            }

                            binding.tilTipoCuenta.setEnabled(false);
                            binding.tilBanco.setEnabled(false);
                            getListener().ocultaProgressDialog();
                        } else {
                            //Enviar al Activity de Registro de Cuentas
                            getListener().ocultaProgressDialog();
                            Intent intent = new Intent(getActivity(), RegistroAgregaCuentaActivity.class);
                            intent.putExtra(ACTIVITY_CODE_KEY, REQUEST_ADD_ACCOUNT_BY_MENU);
                            getActivity().startActivityForResult(intent, 2);
                        }
                    }
                }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());
    }

    private void showCuentaActivada() {
        String registroCuenta = MposApplication.getInstance().getPreferenceManager()
                .getValue(Constantes.Preferencia.REGISTRO_CUENTA, null);

        if (registroCuenta != null && registroCuenta.equalsIgnoreCase("Registrada")) {

                binding.tvCtaVerificada.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreyMiddle));
                binding.tvCtaVerificada.setText(getResources().getString(R.string.cta_bca_title_validate));

                binding.imageViewAna.setVisibility(View.GONE);
                binding.textViewCuentaBancaria.setVisibility(View.GONE);
                binding.textViewContenido.setVisibility(View.GONE);
                binding.botonAceptar.setVisibility(View.GONE);
        } else {
            binding.tvCtaVerificada.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreyMiddle));
            binding.tvCtaVerificada.setText(getResources().getString(R.string.cta_bca_title_validate));
        }
    }

    private void validarCuentaBancaria(DatosCuentaBancaria datosCuentaBancaria) {
        if (datosCuentaBancaria.getCtabancaria() == null) {
            getListener().ocultaProgressDialog();
            Intent intent = new Intent(getActivity(), RegistroAgregaCuentaActivity.class);
            intent.putExtra(ACTIVITY_CODE_KEY, REQUEST_ADD_CARD_BY_MENU);
            getActivity().startActivityForResult(intent, 2);
            loadMiCuenta(ConfigMenuInfoBancariaFragment.this);
            return;
        }else{
            binding.botonUnicoContinuar.setVisibility(View.GONE);
            binding.btnEditarBancaria.setVisibility(View.VISIBLE);
        }

        //Inicializamos el objeto para mantenerlo en la vista
        datosCuenta = datosCuentaBancaria;

        if (datosCuentaBancaria.getTipoctadescripcion() != null) {
            binding.tilTipoCuenta.getEditText().setText(datosCuentaBancaria.getTipoctadescripcion());
        } else {
            binding.tilTipoCuenta.getEditText().setText(getResources().getString(R.string.cta_bca_title_error_consult_data));
        }

        if (datosCuentaBancaria.getCtabancaria() != null) {
            if (datosCuentaBancaria.getCtabancaria().length() > 0) {
                binding.inputNumberFormat.initComponent(datosCuentaBancaria.getCtabancaria().length());
                binding.inputNumberFormat.setCode(datosCuentaBancaria.getCtabancaria());
            } else {
                binding.inputNumberFormat.initComponent(getResources().getString(R.string.cta_bca_title_error_consult_datanumber).length());
                binding.inputNumberFormat.setCode(getResources().getString(R.string.cta_bca_title_error_consult_datanumber));
            }
        }
    }

    private void cargarCuentaBancaria(DatosCuentaBancaria datosCuentaBancaria) {
        if (datosCuentaBancaria.getTipoctadescripcion() != null) {
            binding.tilTipoCuenta.getEditText().setText(datosCuentaBancaria.getTipoctadescripcion());
        } else {
            binding.tilTipoCuenta.getEditText().setText(getResources().getString(R.string.cta_bca_title_error_consult_data));
        }

        if (datosCuentaBancaria.getCtabancaria() != null) {
            if (datosCuentaBancaria.getCtabancaria().length() > 0) {
                binding.inputNumberFormat.initComponent(datosCuentaBancaria.getCtabancaria().length());
                binding.inputNumberFormat.setCode(datosCuentaBancaria.getCtabancaria());
            } else {
                binding.inputNumberFormat.initComponent(getResources().getString(R.string.cta_bca_title_error_consult_datanumber).length());
                binding.inputNumberFormat.setCode(getResources().getString(R.string.cta_bca_title_error_consult_datanumber));
            }
        }

        if (datosCuentaBancaria.getBanco() != null) {
            binding.tilBanco.getEditText().setText(datosCuentaBancaria.getBanco());
        } else {
            binding.tilBanco.getEditText().setText(getResources().getString(R.string.cta_bca_title_error_consult_data));
        }

        if (!datosCuenta.isCtavalidada()) {
            binding.ivInfoNoCuenta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogVerificarCuenta(R.layout.layout_verificar_cuenta, new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                        @Override
                        public void onCancel() {
                            //No implementation
                        }

                        @Override
                        public void onAccept() {
                            verificarCuentaBancaria();
                        }
                    });
                }
            });
        }
    }

    private void verificarCuentaBancaria() {
        getListener().muestraProgressDialog(getResources().getString(R.string.cta_bca_validating_count));
        //Validar el monto y realizar el consumo del webservices de verificar Cuenta
        if (montoVerificarCuenta > 0) {
            misDatosService.verificarCuentaBancaria(new RetrofitListener() {
                                                        @Override
                                                        public void showMessage(String s) {
                                                            //No implementation
                                                        }

                                                        @Override
                                                        public void onFailure(Throwable throwable) {
                                                            LOGGER.throwing(TAG, 1, throwable, getResources().getString(R.string.cta_bca_title_error_validate_count));
                                                            getListener().ocultaProgressDialog();
                                                            getListener().despliegaModal(true, false, "¡Error!",
                                                                    throwable.getMessage(), new ModalFragment.CommonDialogFragmentCallBack() {
                                                                        @Override
                                                                        public void onAccept() {
                                                                            //No implementation
                                                                        }
                                                                    });
                                                        }

                                                        @Override
                                                        public void onSuccess(Object result) {
                                                            getListener().ocultaProgressDialog();
                                                            getListener().despliegaModal(false, false, "Verificación Cuenta",
                                                                    getResources().getString(R.string.cta_bca_title_success_validate), new ModalFragment.CommonDialogFragmentCallBack() {
                                                                        @Override
                                                                        public void onAccept() {
                                                                            cargarDatosCuenta();
                                                                        }
                                                                    });

                                                            getListener().ocultaProgressDialog();
                                                        }
                                                    }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                    MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod(),
                    montoVerificarCuenta);
        } else {
            getListener().ocultaProgressDialog();
            getListener().despliegaModal(true, false, "Error",
                    getResources().getString(R.string.cta_bca_title_error_monto), new ModalFragment.CommonDialogFragmentCallBack() {
                        @Override
                        public void onAccept() {
                            //No implementation
                        }
                    });
        }//end if montoVerificarcuenta
    }

    public void showDialogVerificarCuenta(int layout, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        final MontoVerificacionEditText editMonto = view.findViewById(R.id.edit_monto_banco);
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btnConfirmacion);
        final ImageView ivClose = view.findViewById(R.id.ivClose);
        btnAceptar.setText(getString(R.string.verificar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String monto = editMonto.getText().toString().trim();
                if (monto.length() == 0) {
                    montoVerificarCuenta = 0;
                } else {
                    montoVerificarCuenta = Integer.parseInt(monto);
                }

                callBack.onAccept();
                alertDialog.dismiss();
            }
        });
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    private View.OnClickListener onBtnEditEvent = view ->{
        bindBankAccountsToSpinner();
        bindTipoCtaAccountsToSpinner();

        binding.textViewNumeroCuenta.setText(datosCuenta.getCtabancaria());
        binding.ivDeleteIcon.setVisibility(View.VISIBLE);
        binding.layoutLinearNotEditable.setVisibility(View.GONE);
        binding.layoutLinearEditable.setVisibility(View.VISIBLE);
        binding.btnEditarBancaria.setVisibility(View.GONE);
        binding.botonUnicoContinuar.setVisibility(View.VISIBLE);
    };

    private void showModalDialog(int layout, @Nullable ModalFragment.CommonDialogFragmentCallBack callback) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btnConfirmacion);
        btnAceptar.setOnClickListener(view1 -> {
            if (callback != null) {
                callback.onAccept();
            }
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void bindListeners() {
        binding.botonUnicoContinuar.setOnClickListener(v -> validateBankAccount());
        binding.tipoDocumento.obtenSpinnerContenido().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bindBankAccountsToSpinner();
            }
            v.performClick();
            return true;
        });
        binding.tipoDocumento.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedBankCod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //NOT IMPLEMENTED
            }
        });

        binding.tipoCuenta.obtenSpinnerContenido().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bindTipoCtaAccountsToSpinner();
            }
            v.performClick();
            return true;
        });

        binding.tipoCuenta.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedTipoCta(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //NOT IMPLEMENTED
            }
        });
    }

    private void bindBankAccountsToSpinner() {
        new RegistroInteractor().getBanksList(
                cuentaBancariaBeans -> {
                    mCuentaBancariaBeans = cuentaBancariaBeans;
                    List<String> bankAccountsTitles = bankAccountToBankNames(cuentaBancariaBeans);
                    binding.tipoDocumento.obtenSpinnerContenido().setAdapter(
                            new GenericAdaptadorSpinner<>(requireActivity(), bankAccountsTitles)
                    );
                    int position = 0;
                    for (int i = 0; i <  cuentaBancariaBeans.size(); i++){
                        if(cuentaBancariaBeans.get(i).getBancocod() == datosCuenta.getBancocod()){
                            position = i;
                            break;
                        }
                    }
                    binding.tipoDocumento.obtenSpinnerContenido().setSelection(position);
                    binding.tipoDocumento.obtenSpinnerContenido().setOnTouchListener(null);
                },
                throwable -> {
                    LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                });
    }

    private void bindTipoCtaAccountsToSpinner() {
        new RegistroInteractor().getTiposCtaList(
                cuentaBancariaBeans -> {
                    mTipoCuentaBancariaBeans = cuentaBancariaBeans;
                    List<String> tipoAccountsTitles = tipoAccountToTipoNames(cuentaBancariaBeans);
                    binding.tipoCuenta.obtenSpinnerContenido().setAdapter(
                            new GenericAdaptadorSpinner<>(requireActivity(), tipoAccountsTitles)
                    );
                    int position = 0;
                    for (int i = 0; i < cuentaBancariaBeans.size(); i++){
                        if(cuentaBancariaBeans.get(i).getTipocta() == datosCuenta.getTipocta()){
                            position = i;
                            break;
                        }
                    }
                    binding.tipoCuenta.obtenSpinnerContenido().setSelection(position);
                    binding.tipoCuenta.obtenSpinnerContenido().setOnTouchListener(null);
                },
                throwable -> {
                    LOGGER.throwing(TAG, 1, throwable, throwable.getMessage());
                });
    }

    private List<String> bankAccountToBankNames(List<CuentaBancariaBean> cuentaBancariaBeans) {
        final List<String> bankAccountNames = new ArrayList<>();
        for (CuentaBancariaBean bean : cuentaBancariaBeans) {
            bankAccountNames.add(bean.getDescripcion());
        }
        return bankAccountNames;
    }

    private List<String> tipoAccountToTipoNames(List<TipoCuentaBancariaBean> cuentaBancariaBeans) {
        final List<String> tipoAccountNames = new ArrayList<>();
        for (TipoCuentaBancariaBean bean : cuentaBancariaBeans) {
            tipoAccountNames.add(bean.getDescripcion());
        }
        return tipoAccountNames;
    }

    private void setSelectedBankCod(final int position) {
        CuentaBancariaBean cuentaBancariaBean = mCuentaBancariaBeans.get(position);
        datosCuenta.setBancocod(cuentaBancariaBean.getBancocod());
        datosCuenta.setBanco(cuentaBancariaBean.getDescripcion());
    }

    private void setSelectedTipoCta(final int position) {
        TipoCuentaBancariaBean cuentaBancariaBean = mTipoCuentaBancariaBeans.get(position);
        datosCuenta.setTipocta(cuentaBancariaBean.getTipocta());
        datosCuenta.setTipoctadescripcion(cuentaBancariaBean.getDescripcion());
    }


    private void validateBankAccount() {
        datosCuenta.setCtabancaria(binding.textViewNumeroCuenta.getText().toString().trim());
        DatosSesion datosSesion = ApiData.APIDATA.getDatosSesion();
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos();

        final String sesionId = datosSesion.getIdSesion();
        final String tpvcod = datosSesion.getDatosTPV().getTpvcod();
        if (TextUtils.isEmpty(datosCuenta.getCtabancaria())) {
            showErrorDialog("Por Favor Agrege el Número de Cuenta");
            return;
        } else if (TextUtils.isEmpty(tpvcod)
                || TextUtils.isEmpty(sesionId)) {
            showErrorDialog("Hubo un Problema, Por Favor Intente de Nuevo");
            return;
        }

        final AltaCuentaBean bean = new AltaCuentaBean(
                datosCuenta.getBancocod(),
                datosCuenta.getCtabancaria(),
                datosCuenta.getTipocta()
        );

        muestraProgressDialog("Realizando Operación");
        RegistroCuentaBancariaInteractor registroRetrofit = new RegistroCuentaBancariaInteractor(url);
        registroRetrofit.registroCuentaBancaria(bean, sesionId, tpvcod, new RetrofitListener<Boolean>() {
            @Override
            public void showMessage(String message) {
                Log.i("RegistroCuentaBancaria", message);
            }

            @Override
            public void onFailure(Throwable throwable) {
                ocultaProgressDialog();
                showErrorDialog(throwable.getMessage());
            }

            @Override
            public void onSuccess(Boolean result) {
                binding.tilBanco.getEditText().setText(datosCuenta.getBanco());
                binding.inputNumberFormat.initComponent(datosCuenta.getCtabancaria().length());
                binding.inputNumberFormat.setCode(datosCuenta.getCtabancaria());
                binding.textViewNumeroCuenta.setText("");

                binding.layoutLinearNotEditable.setVisibility(View.VISIBLE);
                binding.layoutLinearEditable.setVisibility(View.GONE);
                binding.btnEditarBancaria.setVisibility(View.VISIBLE);
                binding.botonUnicoContinuar.setVisibility(View.GONE);
                binding.tvCtaVerificada.setVisibility(View.GONE);

                preferenceManager.setValue(REGISTRO_CUENTA, "Registrada");
                ocultaProgressDialog();
                activity.showDialogButtonAcept(R.layout.dialog_cambio_cta_status, "Cerrar", new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
                    @Override
                    public void onCancel() {
                        //No implementation
                    }

                    @Override
                    public void onAccept() {
                        loadMiCuenta(ConfigMenuInfoBancariaFragment.newInstance(ConfigMenuInfoBancariaFragment.ID));
                        listener.accountUpdated();
                    }
                });
            }
        });
    }

    private void showErrorDialog(String message) {
        despliegaModal(
                true,
                false,
                "Error al Editar Cuenta Bancaria",
                message,
                null);
    }

    public void despliegaModal(
            final boolean esDeError,
            final boolean tieneCancelar,
            final String cabecera,
            final String cuerpo,
            final ModalFragment.CommonDialogFragmentCallBack callback) {
        new ModalFragment.DialogBuilder().setTitle(cabecera)
                .setBody(cuerpo)
                .setSingleButton(!tieneCancelar)
                .setAccept(getString(R.string.aceptar))
                .setCancel(getString(R.string.cancelar))
                .setCanceledOnTouchOutside(false)
                .ponEsError(esDeError)
                .ponInterfaceCallBack(callback)
                .build()
                .show(getChildFragmentManager(),
                        ConfigMenuInfoBancariaFragment.class.getName());
    }

    public void muestraProgressDialog(final String mensaje) {
        if (loader != null) {
            loader.setMessage(mensaje);
            loader.show();
        }
    }

    public void ocultaProgressDialog() {
        if (loader != null && loader.isShowing()) {
            loader.dismiss();
        }
    }


}

interface AccountUpdate{
    void accountUpdated();
}