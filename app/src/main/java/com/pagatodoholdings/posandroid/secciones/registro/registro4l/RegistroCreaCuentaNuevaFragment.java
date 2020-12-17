package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentRegistroPaso2Binding;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;
import com.pagatodoholdings.posandroid.utils.Utilities;

public class RegistroCreaCuentaNuevaFragment extends AbstractStepFragment {

    private FragmentRegistroPaso2Binding mBinding;
    private AbstractActivity activity;

    public static RegistroCreaCuentaNuevaFragment newInstance() {
        final RegistroCreaCuentaNuevaFragment fragment = new RegistroCreaCuentaNuevaFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRegisterActions(getActivity());
        activity = (AbstractActivity) getActivity();
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_paso2, container, false);
        setBackArrowVisibility(View.VISIBLE);
        mBinding.btnValidar.setOnClickListener(view -> nextStep());

        mBinding.etCorreo.obtenEtCampo().setOnEditorActionListener((v, imeAction, event) -> {
            if (imeAction == EditorInfo.IME_ACTION_NEXT) {
                mBinding.etCorreo.clearFocus();
                mBinding.etContraseA.obtenEtCampo().requestFocus();
                return true;
            }
            return false;
        });

        mBinding.etCorreo.setFocusableInTouchMode(true);


     mBinding.etCorreo.setOnFocusChangeListener((view, hasFocus) -> {
         if (hasFocus) {
             InputMethodManager imm = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
         }
     });

        mBinding.etContraseA.obtenEtCampo().setOnEditorActionListener((v, imeAction, event) -> {
            if (imeAction == EditorInfo.IME_ACTION_NEXT) {
                mBinding.etContraseA.clearFocus();
                mBinding.etConfContraseA.obtenEtCampo().requestFocus();
                return true;
            }
            return false;
        });
        mBinding.etConfContraseA.obtenEtCampo().setOnEditorActionListener((v, imeAction, event) -> {
            if (imeAction == EditorInfo.IME_ACTION_DONE) {
                Utilities.hideSoftKeyboard(getActivity());
                nextStep();
                return true;
            }
            return false;
        });

        return mBinding.getRoot();
    }

    private boolean validateFields() {

        String contrasena = mBinding.etContraseA.obtenEtCampo().getText().toString().toLowerCase();
        String confirmaContrasena = mBinding.etConfContraseA.obtenEtCampo().getText().toString().toLowerCase();

        if (!mBinding.etCorreo.validateEmail()){
            activity.despliegaModal(true, false, getResources().getString(R.string.email), getString(R.string.emal_ivalid),null);
            return false;
        }
        if(contrasena == null || contrasena.length() < 6)
        {
            activity.despliegaModal(true, false, getResources().getString(R.string.new_password_title), getResources().getString(R.string.new_password_length),null);
            return false;
        }

        if(confirmaContrasena == null || confirmaContrasena.length() < 6)
        {
            activity.despliegaModal(true, false, getResources().getString(R.string.new_password_confirm_tittle), getResources().getString(R.string.new_password_confirm_tittle),null);
            return false;
        }

        if(contrasena.contains(confirmaContrasena))
        {
            return true;
        }else{
            activity.despliegaModal(true, false, getResources().getString(R.string.password_confirm_error_tittle), getResources().getString(R.string.password_confirm_error),null);
            return false;
        }
    }

    private void nextStep(){
        if (validateFields()) {
            DatosUsuarioRegistroCliente.getInstace().setEmail(mBinding.etCorreo.obtenEtCampo().getText().toString());
            DatosUsuarioRegistroCliente.getInstace().setPassword(mBinding.etConfContraseA.obtenEtCampo().getText().toString());
            advanceToNextStep();
        } else {
            if (MposApplication.getInstance().isBuildDebug()) {
                advanceToNextStep();
            }
        }
    }

}
