package com.pagatodoholdings.posandroid.secciones.registro.registro4l;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.fragment.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractActivity;

public abstract class AbstractStepFragment extends DialogFragment {

    private RegisterActions registerActions;

    /**
     * Tiene que mandarse a llamar desde el OnCreateView para poder usarse en el RegistroExternoActivity
     *
     * @param context
     */
    protected void setRegisterActions(Context context) {
        if (context instanceof RegisterActions) {
            registerActions = (RegisterActions) context;
        }
    }

    public void advanceToNextStep() {
        //Aqui va el código para continuar al siguiente fragmento
        registerActions.nextStep();
    }


    public void setBackArrowVisibility(int visibility) {
        registerActions.setBackArrowVisibility(visibility);
    }

    public ModalFragment despliegaModal(
            final String cuerpo,
            final String icono) {

        return new ModalFragment.DialogBuilder()
                .setBody(cuerpo)
                .setAccept("Si")
                .setCancel("No")
                .setCustomImage(icono)
                .setCanceledOnTouchOutside(false)
                .build();
    }

    protected void despliegaModalDeError(
            String errorTitle,
            String cuerpo,
            ModalFragment.CommonDialogFragmentCallBack callback) {
        new ModalFragment.DialogBuilder()
                .setTitle(errorTitle)
                .setBody(cuerpo)
                .setSingleButton(true)
                .setAccept(getString(R.string.aceptar))
                .setCancel(getString(R.string.cancelar))
                .setCanceledOnTouchOutside(false)
                .ponEsError(true)
                .ponInterfaceCallBack(callback)
                .build()
                .show(getFragmentManager(), "Modal");
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
        btnCancelar.setText(getString(R.string.no_por_ahora));
        btnCancelar.setTextSize(14);
        btnCancelar.setOnClickListener(view12 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });
        final ImageView dismiss = view.findViewById(R.id.cancel_dialog);
        dismiss.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    protected void showProgressDialog() {

        ((AbstractActivity) getActivity()).muestraProgressDialog("Registrando");
    }

    protected void hideProgressDialog() {
        ((AbstractActivity) getActivity()).ocultaProgressDialog();
    }

    public interface RegisterActions {
        void setBackArrowVisibility(int visibility);

        void nextStep();

        void prevStep();
    }
}
