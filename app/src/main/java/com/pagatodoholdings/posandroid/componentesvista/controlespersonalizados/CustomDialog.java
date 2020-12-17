package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.DialogCustomBinding;
import com.pagatodoholdings.posandroid.utils.Logger;
import java.util.Objects;

@SuppressLint("ValidFragment")
public class CustomDialog extends DialogFragment {

    private DialogCustomBinding binding;
    private String titulo;
    private String contenido;
    private int logo;
    private boolean closeVisible;
    private boolean cancelVisible;
    private boolean logoVisible;
    private ModalFragment.CommonDialogFragmentCallBack callBack = null;

    public CustomDialog() {}

    public CustomDialog(String titulo, String contenido, int logo, boolean closeVisible, boolean cancelVisible, boolean logoVisible, ModalFragment.CommonDialogFragmentCallBack callBack) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.logo = logo;
        this.closeVisible = closeVisible;
        this.cancelVisible = cancelVisible;
        this.logoVisible = logoVisible;
        this.callBack = callBack;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog().getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        binding = DialogCustomBinding.inflate(infalter, container, false);
        binding.tvTituloCustom.setText(titulo);
        binding.tvContenidoCustom.setText(contenido);
        if (logoVisible){
            binding.logoCustom.setImageResource(logo);
        }
        if (closeVisible){
            binding.btnCloseCustom.setVisibility(View.VISIBLE);
            binding.btnCloseCustom.setOnClickListener((View v) -> dismiss());
        }else{
            binding.btnCloseCustom.setVisibility(View.GONE);
        }
        binding.btnAceptarCustom.setOnClickListener((View v) ->{
                if (callBack != null) {
                    callBack.onAccept();
                }
                dismiss();
        });
        if (cancelVisible){
            binding.btnCancelarCustom.setOnClickListener((View v) ->{
                    if (callBack instanceof ModalFragment.CommonDialogFragmentCallBackWithCancel) {
                        ((ModalFragment.CommonDialogFragmentCallBackWithCancel) callBack).onCancel();
                    }
                    dismiss();
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.AnimacionModal;
    }

    @Override
    public void show(final FragmentManager manager, final String tag) {
        try {

            final FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(this, tag);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Logger.LOGGER.throwing(ModalFragment.class.getSimpleName(), 1, e, e.getMessage());
        }
    }

}
