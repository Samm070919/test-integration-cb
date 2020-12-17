package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.login.LoginActivity;
import com.pagatodoholdings.posandroid.utils.FingerprintUiHelper;
import com.pagatodoholdings.posandroid.utils.Logger;
import java.util.Objects;

public class DialogoBiometrico extends DialogFragment implements FingerprintUiHelper.Callback, TextView.OnEditorActionListener {

    private Button btnContinuar;
    private View view;
    private View vFingerPrint;
    private View vPassword;
    private EditTextDatosUsuarios edtPassword;


    private Stage mStage = Stage.FINGERPRINT;

    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUiHelper mFingerprintUiHelper;

    private InputMethodManager mInputMethodManager;

    public DialogoBiometrico() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @TargetApi(28)
    @Override
    public void onResume() {
        super.onResume();
        if (mStage == Stage.FINGERPRINT) {
            mFingerprintUiHelper.startListening(mCryptoObject);
        }
    }

    public void setStage(Stage stage) {
        mStage = stage;
    }

    @TargetApi(28)
    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_fingerprint_container, container, false);
        Objects.requireNonNull(getDialog().getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);

        initView();

        return view;
    }


    @TargetApi(28)
    public void initView(){

        ImageButton btnCerrar = view.findViewById(R.id.btn_close);
        btnCerrar.setOnClickListener(v -> dismiss());

        btnContinuar = view.findViewById(R.id.btn_validar);
        if (getActivity() instanceof LoginActivity){
            btnContinuar.setVisibility(View.GONE);
        }
        btnContinuar.setOnClickListener(v -> {
            if (mStage == Stage.FINGERPRINT) {
                goToBackup();
            } else {
                verifyPassword();
            }
        });

        vFingerPrint = view.findViewById(R.id.ll_fingerprintCnt);
        vPassword = view.findViewById(R.id.ll_passwordCtn);
        edtPassword =  view.findViewById(R.id.et_contrase_a);
        edtPassword.obtenEtCampo().setOnEditorActionListener(this);
        mFingerprintUiHelper = new FingerprintUiHelper(
                getActivity().getSystemService(FingerprintManager.class),
                (ImageView) view.findViewById(R.id.iv_huella),
                (TextView) view.findViewById(R.id.tv_contenido), this);
        updateStage();

        // If fingerprint authentication is not available, switch immediately to the backup
        // (password) screen.
        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            goToBackup();
        }
    }

    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    private boolean checkPassword(String password) {
        // Assume the password is always correct.
        // In the real world situation, the password needs to be verified in the server side.
        return password.length() == 6;
    }

    private void verifyPassword() {
        if (!checkPassword(edtPassword.obtenEtCampo().getText().toString())) {
            return;
        }
        edtPassword.obtenEtCampo().setText("");
        dismiss();
    }

    @TargetApi(28)
    private void goToBackup() {
        mStage = Stage.PASSWORD;
        updateStage();
        edtPassword.requestFocus();

        // Show the keyboard.
        edtPassword.postDelayed(mShowKeyboardRunnable, 500);

        // Fingerprint is not used anymore. Stop listening for it.
        mFingerprintUiHelper.stopListening();
    }

    private void updateStage() {
        switch (mStage) {
            case FINGERPRINT:
                btnContinuar.setText(getString(R.string.cancel_usar_contraseña_fingerprint));
                vPassword.setVisibility(View.GONE);
                break;
            case NEW_FINGERPRINT_ENROLLED:
                // Intentional fall through
            case PASSWORD:
                btnContinuar.setText(R.string.continuar);
                vFingerPrint.setVisibility(View.GONE);
                vPassword.setVisibility(View.VISIBLE);
                break;
        }
    }

    private final Runnable mShowKeyboardRunnable = () -> mInputMethodManager.showSoftInput(edtPassword, 0);

    @Override
    public void onAuthenticated() {
        dismiss();
        if(getActivity() instanceof LoginActivity){
            //SE OCULTA FUNCIONALIDAD DE HUELLA PARA SIGUIENTE ENTREGA
        }

    }

    @Override
    public void onError() {
        if(getActivity() instanceof HomeActivity){
            goToBackup();
        }

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        return false;
    }

    public enum Stage {
        FINGERPRINT,
        NEW_FINGERPRINT_ENROLLED,
        PASSWORD
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.AnimacionModal;
    }



    @Override
    public void show(FragmentManager manager, String tag) {
        try {

            final FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(this, tag);
            fragmentTransaction.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Logger.LOGGER.throwing(ModalFragment.class.getSimpleName(), 1, e, e.getMessage());
        }
    }
}
