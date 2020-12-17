package com.pagatodoholdings.posandroid.secciones.money_in;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.general.abstracts.AbstractFragment;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.utils.Constantes;


public abstract class AbstractMoney extends AbstractFragment {

    private HomeActivity mListener;
    private Activity activityListener;
    private ViewGroup fragmentsContainer;
    private int idContainer = R.id.fl_main_moneyin;

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
     *
     * @param fragment Es la instancia del fragmento que quieres closeFragment
     */
    protected void loadMoneyInMenu(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            mListener.cargarFragmentMoneyIn();
        }

    }
    /**
    * Getter/Setter para Contenedor de Fragmentos
    *
    */
    public void setFragmentsContainer(ViewGroup container) {
        this.fragmentsContainer = container;
    }

    public ViewGroup getFragmentsContainer() {
        return this.fragmentsContainer;
    }
    /**
     * Permite cargar fragmentos con transición
     *
     */
    public void loadFragmentsWithTransition(final AbstractFragment fragment) {
        final AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(Constantes.TIEMPO_TRANSICIONES);
        autoTransition.addListener( generaListener(fragment));
        TransitionManager.beginDelayedTransition(getFragmentsContainer(), autoTransition);
    }
    /**
     *
     * Listener Para procesar después de la transición
     */
    public Transition.TransitionListener generaListener(final AbstractFragment fragment) {

        return new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(final Transition transition) {
                //No implementado
            }

            @Override
            public void onTransitionEnd(final Transition transition) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace( getFragmentsContainer().getId(), fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            @Override
            public void onTransitionCancel(final Transition transition) {
                //No implementado
            }

            @Override
            public void onTransitionPause(final Transition transition) {
                //No implementado
            }

            @Override
            public void onTransitionResume(final Transition transition) {
                //No implementado
            }


        };

    }
    /**
     *
     * @param newFragment Es la instancia del fragmento a cargar
     */
    public void loadMoneyInFragments(Fragment newFragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(idContainer, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    /**
     * Permite navegar al fragmento previo de la pila
     *
     */
    public void backPreviousFragment() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
    /**
     * Reiniciar el contador de tiempo de bloqueo
     *
     */
    public void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    public void detenerContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.detenerTemporizador();
        }
    }

    /**
     * sepa dios que fregados haga esto
     * @return
     */
    @Override
    protected boolean isTomandoBack() {
        return true;
    }

    /**
     * overriden because perfect programming
     * @param throwable
     */
    @Override
    public void onFailure(Throwable throwable) {
        //No definido
    }

    public void showDialog(int layout, ModalFragment.CommonDialogFragmentCallBack callback, String textAcceptar) {//NOSONAR
        final AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppTheme));
        final LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(layout, null);
        alert.setCancelable(false);
        alert.setView(view);
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final ModalFragment.CommonDialogFragmentCallBack callBack = callback;
        final BotonClickUnico btnAceptar = view.findViewById(R.id.btn_aceptar_screenshot);
        if (textAcceptar.equals(null) || textAcceptar.isEmpty() ) {
            btnAceptar.setText(getString(R.string.aceptar));
        } else {
            btnAceptar.setText(textAcceptar);
        }

        btnAceptar.setTextSize(14);
        btnAceptar.setOnClickListener(view1 -> {
            callBack.onAccept();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

}
