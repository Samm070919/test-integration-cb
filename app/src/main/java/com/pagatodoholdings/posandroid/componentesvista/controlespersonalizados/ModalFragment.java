package com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados;

import android.annotation.SuppressLint;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.Logger;

import java.util.Objects;

public class ModalFragment extends DialogFragment {
    private View view;
    private String titulo;
    private String cuerpo;
    private String cancel = "";
    private String accept = "";
    private CommonDialogFragmentCallBack callBack;
    private boolean isSingle;
    private boolean isCancel;
    private boolean isOnTouch;
    private boolean esError = false;
    private View extraContent;
    private boolean mHasCustomImage = false;
    private String mImage;

    public void setExtraContent(final View extraContent) {
        this.extraContent = extraContent;
    }

    public ModalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        getActivity().setRequestedOrientation(MposApplication.getInstance().getOrientation());
        super.onStart();
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.modal_fragmento, container, false);
        Objects.requireNonNull(getDialog().getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView();
        view.setOnTouchListener((final View v, final MotionEvent motionEvent)-> {
                reiniciarContador();
                return false;
        });
        return view;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(getDialog().getWindow())
                .getAttributes().windowAnimations = R.style.AnimacionModal;
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    private void initView() {
        final LinearLayout linearBotones = view.findViewById(R.id.Bottones_Dialog);
        final Button tvCancelar = view.findViewById(R.id.btn_cancelar);
        tvCancelar.setText(cancel);
        final Button btnAceptar = view.findViewById(R.id.btn_modal_aceptar);
        btnAceptar.setText(accept);

        final ImageView ivIconoStatus = view.findViewById(R.id.iv_icono_status);
        final TextView encabezado = view.findViewById(R.id.tv_titulo_modal);
        final ProgressBar progressBar = view.findViewById(R.id.progress_circular_bar);

        if (isSingle) {
            tvCancelar.setVisibility(View.GONE);
        }
        if (isCancel) {
            tvCancelar.setText(getString(R.string.cancelar));
        }

        btnAceptar.setOnClickListener((final View view1)-> {
                reiniciarContador();
                if (callBack != null) {
                    callBack.onAccept();
                }
                if (!isSingle) {
                    ivIconoStatus.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    linearBotones.setVisibility(View.GONE);
                }
                dismiss();
        });
        tvCancelar.setOnClickListener((final View view2)-> {
                reiniciarContador();
                if (callBack instanceof CommonDialogFragmentCallBackWithCancel) {
                    ((CommonDialogFragmentCallBackWithCancel) callBack).onCancel();
                }
                dismiss();
        });

        setCancelable(isOnTouch);
        getDialog().setCanceledOnTouchOutside(isOnTouch);

        encabezado.setText(titulo);
        final TextView tvCuerpo = view.findViewById(R.id.tv_modal_cuerpo);
        tvCuerpo.setText(cuerpo);
        if (esError) {
            ivIconoStatus.setBackground(view.getContext().getDrawable(R.drawable.ic_sin_valores));
            tvCuerpo.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSoftRed));
        } else {
            ivIconoStatus.setBackground(view.getContext().getDrawable(R.drawable.ic_img_palomita));
        }

        if (mHasCustomImage){
            if ("pagoTarjeta".equals(mImage)) {
                ivIconoStatus.setBackground(view.getContext().getDrawable(R.drawable.icono_pago_tarjeta));
            } else if ("aumentaIngresos".equals(mImage)) {
                ivIconoStatus.setBackground(view.getContext().getDrawable(R.drawable.icono_ingresos));
            }
        }

        if (extraContent != null) {

            final LinearLayout extraContentLinearLayout = view.findViewById(R.id.extraContentLinearLayout);
            extraContentLinearLayout.addView(extraContent);
        }
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public void setCuerpo(final String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public void setCancel(final String cancel) {
        this.cancel = cancel;
    }

    public void setAccept(final String accept) {
        this.accept = accept;
    }

    public void setCallBack(final CommonDialogFragmentCallBack callBack) {
        this.callBack = callBack;
    }

    public void setSingle(final boolean single) {
        isSingle = single;
    }

    public void setIsCancel(final boolean cancel) {
        isCancel = cancel;
    }

    public void setOnTouch(final boolean onTouch) {
        isOnTouch = onTouch;
    }

    public void setEsError(final boolean esError) {
        this.esError = esError;
    }

    public interface CommonDialogFragmentCallBack {
        void onAccept();
    }

    public interface CommonDialogFragmentCallBackWithCancel extends CommonDialogFragmentCallBack {
        void onCancel();
    }

    public static class DialogBuilder {
        private String title = "";
        private String body = "";
        private String cancel = "";
        private String accept = "";
        private CommonDialogFragmentCallBack callBack;
        private boolean isSingle;
        private boolean isCancel;
        private boolean isOnTouch;
        private boolean esError;
        private boolean mHasCustomImage = false;
        private String mImage = "";

        private View extraContent;

        public DialogBuilder setTitle(final String title) {
            this.title = title;
            return this;
        }

        public DialogBuilder setBody(final String body) {
            this.body = body;
            return this;
        }

        public DialogBuilder setCancel(final String cancel) {
            this.cancel = cancel;
            return this;
        }

        public DialogBuilder setAccept(final String accept) {
            this.accept = accept;
            return this;
        }

        public DialogBuilder setSingleButton(final boolean isSingle) {
            this.isSingle = isSingle;
            return this;
        }

        public DialogBuilder setCustomImage(String image){
            this.mImage = image;
            this.mHasCustomImage = true;
            return this;
        }

        public DialogBuilder setCancelLabel(final boolean isCancel) {
            this.isCancel = isCancel;
            return this;
        }

        public DialogBuilder ponInterfaceCallBack(final CommonDialogFragmentCallBack callBack) {
            this.callBack = callBack;
            return this;
        }

        public DialogBuilder setInterfaceCallBackWithCancel(final CommonDialogFragmentCallBackWithCancel callBack) {
            this.callBack = callBack;
            return this;
        }

        public DialogBuilder setCanceledOnTouchOutside(final boolean isOnTouch) {
            this.isOnTouch = isOnTouch;
            return this;
        }

        public DialogBuilder ponEsError(final boolean esError) {
            this.esError = esError;
            return this;
        }

        public DialogBuilder setExtraContent(final View extraContent) {

            this.extraContent = extraContent;
            return this;
        }

        public ModalFragment build() {
            final ModalFragment commonDialogFragment = new ModalFragment();

            estableceParametros(commonDialogFragment);
            return commonDialogFragment;
        }

        private void estableceParametros(final ModalFragment modalFragment) {
            modalFragment.setTitulo(title);
            modalFragment.setCuerpo(body);
            modalFragment.setCancel(cancel);
            modalFragment.setAccept(accept);
            modalFragment.setCallBack(callBack);
            modalFragment.setSingle(isSingle);
            modalFragment.setIsCancel(isCancel);
            modalFragment.setEsError(esError);
            modalFragment.setOnTouch(isOnTouch);
            modalFragment.setCustomImage(mHasCustomImage, mImage);
            modalFragment.setExtraContent(extraContent);
        }
    }

    private void setCustomImage(boolean hasCustomImage, String image) {
        this.mImage = image;
        this.mHasCustomImage = hasCustomImage;
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
