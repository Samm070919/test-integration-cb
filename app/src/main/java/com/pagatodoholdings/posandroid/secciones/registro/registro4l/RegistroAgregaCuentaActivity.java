package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.EditTextDatosUsuarios;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.MontoVerificacionEditText;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroAgregaCuentaBinding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.AltaCuentaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.CuentaBancariaBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.RegistroCuentaBancariaInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoCuentaBancaria;
import com.pagatodoholdings.posandroid.utils.Constantes;

import net.fullcarga.android.api.sesion.DatosSesion;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.REGISTRO_CUENTA;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;
import static com.pagatodoholdings.posandroid.utils.Utilities.build;

public class RegistroAgregaCuentaActivity extends AbstractActivity {//NOSONAR

    private static final String TAG = RegistroAgregaCuentaActivity.class.getSimpleName();

    private FragmentRegistroAgregaCuentaBinding mBinding;
    private AltaCuentaBean beanCuenta;
    //    Si el servicio se consumió exitosamente y ya está cargando en el adapter, no tiene sentido
//    volver a llamar el servicio
    private boolean isBankAccountAdded = false;
    //    Variable declarada para evitar que el servicio se mande a llamar múltipes veces sin esperar
//    a que termine primero
    private boolean isTryingToGetBanks = false;
    private List<CuentaBancariaBean> mCuentaBancariaBeans;
    private List<TipoCuentaBancaria> listAccountsTypes;
    private Integer mSelectedBancocod;
    private Integer mSelectesAccountType =0;
    private int idActivityCalling = 0;

    public RegistroAgregaCuentaActivity() {
        // Required empty public constructor
    }

    @Override
    protected void initUi() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_registro_agrega_cuenta);
        if(getCallingActivity() != null){
            if(getIntent().getExtras() != null){
                idActivityCalling = getIntent().getIntExtra(Constantes.ACTIVITY_CODE_KEY, 0);
            }
        }
        bindListeners();
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
        finish();
    }

    private void bindListeners() {
        if(ApiData.APIDATA.getDatosSesion() == null){
            iniciaSesionSigma();
        }

        mBinding.tvOtroMomento.setOnClickListener(v-> {
            restauraHome();

        });

        mBinding.botonUnicoContinuar.setOnClickListener(v -> validateBankAccount());
        mBinding.tipoDocumento.obtenSpinnerContenido().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bindBankAccountsToSpinner();
            }
            v.performClick();
            return true;
        });

        mBinding.tipoCta.obtenSpinnerContenido().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                bindAccountTypesToSpinner();
            }
            v.performClick();
            return true;
        });

        mBinding.tipoDocumento.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedBankCod(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { /*Not implemented*/}
        });

        mBinding.tipoCta.obtenSpinnerContenido().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                setSelectedAccountType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {/*Not implemented*/}
        });

        bindBankAccountsToSpinner();
        bindAccountTypesToSpinner();
    }

    private void setSelectedBankCod(final int position) {
        CuentaBancariaBean cuentaBancariaBean = mCuentaBancariaBeans.get(position);
        mSelectedBancocod = cuentaBancariaBean.getBancocod();
    }

    private void setSelectedAccountType(final int position) {
        TipoCuentaBancaria tipoCuentaBancaria = listAccountsTypes.get(position);
        mSelectesAccountType = tipoCuentaBancaria.getTipoCta();
    }


    private void validateBankAccount() {
        final String cuenta = mBinding.etNumeroCuenta.getRawText().replace(" ", "");
        DatosSesion datosSesion = ApiData.APIDATA.getDatosSesion();
        String url = MposApplication.getInstance().getDatosLogin().getPais().getUrlwsmpos();

       if(mSelectesAccountType ==0){
           showErrorDialog(new Throwable("Por Favor Seleccione el Tipo de Cuenta"));
           return;
       }

        final String sesionId = (datosSesion!= null? datosSesion.getIdSesion(): "");
        final String tpvcod = (datosSesion!= null?datosSesion.getDatosTPV().getTpvcod():"");
        if (cuenta==null && TextUtils.isEmpty(cuenta)) {
            showErrorDialog(new Throwable("Por Favor Agrege el Número de Cuenta"));
            return;
        } else if (mSelectedBancocod == null
                || TextUtils.isEmpty(sesionId)
                || TextUtils.isEmpty(tpvcod)) {
            showErrorDialog(new Throwable("Hubo un Problema, Por Favor Intente de Nuevo"));
            return;
        }

        beanCuenta = new AltaCuentaBean(
                mSelectedBancocod,
                cuenta,
                mSelectesAccountType
        );

        muestraProgressDialog("Registrando");
        RegistroCuentaBancariaInteractor registroRetrofit = new RegistroCuentaBancariaInteractor(url);
        registroRetrofit.registroCuentaBancaria(beanCuenta, sesionId, tpvcod, new RetrofitListener<Boolean>() {
            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onFailure(Throwable throwable) {
                ocultaProgressDialog();
                showErrorDialog(throwable);
            }

            @Override
            public void onSuccess(Boolean result)
            {
                mBinding.tipoDocumento.setVisibility(View.GONE);
                mBinding.tipoCta.setVisibility(View.GONE);
                mBinding.tvNoCta.setVisibility(View.GONE);
                mBinding.etNumeroCuenta.setVisibility(View.GONE);
                mBinding.tvAviso.setVisibility(View.GONE);
                mBinding.tvOtroMomento.setVisibility(View.GONE);

                mBinding.textViewCuentaBancaria.setText(R.string.cta_prevalidada);
                mBinding.textViewContenido.setText(R.string.cta_por_validar);
                mBinding.imageViewAna.setImageResource(R.drawable.verifica_cuenta);

                mBinding.botonUnicoContinuar.setOnClickListener(view -> bankAccountRegisteredSuccessfully());

                preferenceManager.setValue(REGISTRO_CUENTA,"Registrada");
                ocultaProgressDialog();
            }
        });
    }

    private void iniciaSesionSigma() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DatosSesion datosSesion = null;
        DatosLogin datosLogin = MposApplication.getInstance().getDatosLogin();
        if (datosLogin == null) {
            return;
        }

        try {
            datosSesion = build();
        } catch (SQLException exe) {
            LOGGER.throwing(TAG, 1, exe, exe.getMessage());
        }

        ApiData.APIDATA.setDatosSesion(datosSesion);
    }

    private void showErrorDialog(Throwable throwable) {
        showDialogError(R.layout.dialog_insufficient_balance, throwable.getMessage(), new ModalFragment.CommonDialogFragmentCallBackWithCancel() {
            @Override
            public void onCancel() {
                //No implementation
            }

            @Override
            public void onAccept() {}
        });
    }


    private void bankAccountRegisteredSuccessfully() {
        restauraHome();
    }

    private void bindAccountTypesToSpinner(){
        listAccountsTypes = new ArrayList<>();

        TipoCuentaBancaria tipoCta1 = new TipoCuentaBancaria(1,"Cuenta Ahorro");
        TipoCuentaBancaria tipoCta2 = new TipoCuentaBancaria(2, "Cuenta Corriente");

        listAccountsTypes.add(tipoCta1);
        listAccountsTypes.add(tipoCta2);

        List<String> listDescriptions = accountTypesDescriptions(listAccountsTypes);
        mBinding.tipoCta.obtenSpinnerContenido().setAdapter(new GenericAdaptadorSpinner<>(this, listDescriptions));
    }

    private void bindBankAccountsToSpinner() {
        if (isBankAccountAdded || isTryingToGetBanks) {
            return;
        }

        isTryingToGetBanks = true;

        RegistroInteractor registroInteractor = new RegistroInteractor();
        registroInteractor.getBanksList(
                cuentaBancariaBeans -> {
                    mCuentaBancariaBeans = cuentaBancariaBeans;
                    List<String> bankAccountsTitles = bankAccountToBankNames(cuentaBancariaBeans);
                    mBinding.tipoDocumento.obtenSpinnerContenido().setAdapter(
                            new GenericAdaptadorSpinner<>(this, bankAccountsTitles)
                    );
                    mBinding.tipoDocumento.obtenSpinnerContenido().setOnTouchListener(null);
                    isTryingToGetBanks = false;
                    isBankAccountAdded = true;
                },
                throwable -> {
                    isTryingToGetBanks = false;
                    LOGGER.throwing(TAG,1,throwable,throwable.getMessage());
                    failedToBindBankAccounts();
                });
    }

    private List<String> bankAccountToBankNames(List<CuentaBancariaBean> cuentaBancariaBeans) {
        final List<String> bankAccountNames = new ArrayList<>();
        for (CuentaBancariaBean bean : cuentaBancariaBeans) {
            bankAccountNames.add(bean.getDescripcion());
        }
        return bankAccountNames;
    }


    private List<String> accountTypesDescriptions(List<TipoCuentaBancaria> tiposCuentas) {
        final List<String> accountsDescriptions = new ArrayList<>();
        for (TipoCuentaBancaria b : tiposCuentas) {
            accountsDescriptions.add(b.getDescripcion());
        }
        return accountsDescriptions;
    }

    private void failedToBindBankAccounts() {
        //NOT IMPLEMENTED
    }

    public void showDialogError(int layout, String message, ModalFragment.CommonDialogFragmentCallBackWithCancel callback) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBackWithCancel callBack = callback;
        TextView tvMessage = view.findViewById(R.id.dialog_body_txt);
        TextView tvSalir = view.findViewById(R.id.salir_txt);
        tvSalir.setVisibility(View.GONE);

        if(message.contains("obligatoria"))
            tvMessage.setText(R.string.empty_account);
        else{
            tvMessage.setText(message);
        }
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btn_validar);
        btnAceptar.setText(getString(R.string.btn_empty_account));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                callBack.onAccept();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
