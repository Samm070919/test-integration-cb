package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;

public abstract class AbstractConfigMenu extends AbstractFragment {

    private HomeActivity mListener;
    private Activity activityListener;

    /**
     * Este método es obligatorio para generar la instancia del activity
     * @param listener
     * @return
     */
    public Fragment setListener(final HomeActivity listener) {
        this.mListener = listener;
        return this;
    }

    public Fragment setListenerActivity(final Activity activityListener) {
        this.activityListener = activityListener;
        return this;
    }

    public HomeActivity getListener() {
        return mListener;
    }


    public Activity getActivityListener() {
        return activityListener;
    }

    /**
     *
     * @param fragment Es la instancia del fragmento que quieres closeFragment
     */
    protected void closeFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            mListener.regresaMenu();
        }
    }
    /**
     *
     * @param fragment Es la instancia del fragmento que quieres closeFragment
     */
    protected void loadMiCuenta(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            mListener.cargarFragmentMiCuenta();
        }

    }

    /**
     * sepa dios que fregados haga esto
     * @return
     */
    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    /**
     * overriden because perfect programming
     * @param throwable
     */
    @Override
    public void onFailure(Throwable throwable) {
        //No definido
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
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btn_aceptar_screenshot);
        btnAceptar.setText(getString(R.string.aceptar));
        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }
}
