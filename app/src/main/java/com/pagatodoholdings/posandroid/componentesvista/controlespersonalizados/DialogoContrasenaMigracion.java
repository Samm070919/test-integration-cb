package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import androidx.fragment.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.DialogoContrasenaMigracionBinding;

public class DialogoContrasenaMigracion extends DialogFragment {
    private Context context;
    private DialogoContrasenaMigracionBinding binding;
    private CambioContrasenaListener callBack;

    public DialogoContrasenaMigracion() {
        //Nothing
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        context = MposApplication.getInstance();
        binding = DialogoContrasenaMigracionBinding.inflate(inflater, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
        this.setRetainInstance(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.AnimacionModal;
        setCancelable(false);
    }

    private void initView(){
        binding.migracionClaveUsuario.obtenEtCampo().setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        binding.migracionValidateClaveUsuario.obtenEtCampo().setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        binding.btnMigracionAceptar.setOnClickListener((View v)-> {
                final String clave = binding.migracionClaveUsuario.obtenEtCampo().getText().toString();
                final String claveValidate = binding.migracionValidateClaveUsuario.obtenEtCampo().getText().toString();
                if (validaClave(clave) && validaConfirmacionClave(clave, claveValidate)){
                    dismiss();
                    callBack.onAccept(claveValidate);
                }
        });
    }

    private boolean validaClave(final String clave){
        if (!clave.isEmpty() && clave.length() == 4){
            return true;
        }else {
            binding.migracionClaveUsuario.establecerError(context.getString(R.string.campo_requerido));
            return false;
        }
    }

    private boolean validaConfirmacionClave(final String clave, final String claveValidate){
        if (clave.equals(claveValidate)){
            return true;
        }else {
            binding.migracionValidateClaveUsuario.establecerError(context.getString(R.string.campo_no_iguales));
            return false;
        }
    }

    public void setCallBack(final CambioContrasenaListener callBack){
        this.callBack = callBack;
    }

    public interface CambioContrasenaListener {
        void onAccept(String clave);
    }

}
