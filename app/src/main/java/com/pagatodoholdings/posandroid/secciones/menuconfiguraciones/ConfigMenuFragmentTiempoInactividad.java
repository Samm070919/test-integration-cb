package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuFragmentTiempoInactividadBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import static android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIEMPO_INACTIVIDAD;

public class ConfigMenuFragmentTiempoInactividad extends AlertDialog {

    private FragmentConfigMenuFragmentTiempoInactividadBinding mBinding;
    private static final int MILLIS_IN_SECOND = 1000;
    private static final int SECONDS_IN_MINUTE = 60;
    private String mTimeOfInactivity;
    private HomeActivity mListener;

    public ConfigMenuFragmentTiempoInactividad(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setListener((HomeActivity) context);
    }

    private void setFirstTimeText() {
        int actualTime = mBinding.numberPicker.getValue();
        setSelectedTimeText(actualTime);
    }

    private void setSelectedTimeText(final int newValue) {
        String newSelectedTime = newValue + " minutos " + mTimeOfInactivity;
        //mBinding.tiempoSeleccionado.setText(newSelectedTime);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initUI() {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        mBinding = FragmentConfigMenuFragmentTiempoInactividadBinding.inflate(layoutInflater, null, false);
        setView(mBinding.getRoot());
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mTimeOfInactivity = getContext().getString(R.string.tiempo_inactivdad_tiempo_seleccionado);

        mBinding.numberPicker.setMinValue(3);
        mBinding.numberPicker.setMaxValue(30);
        mBinding.numberPicker.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        mBinding.numberPicker.setWrapSelectorWheel(false);
        mBinding.numberPicker.setValue(MposApplication.getInstance().getPreferenceManager().getValue(TIEMPO_INACTIVIDAD) / SECONDS_IN_MINUTE / MILLIS_IN_SECOND);

        mBinding.numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> cambioTiempo(newVal));
        //mBinding.closeButton.setOnClickListener(v -> dismiss());
        mBinding.btnModalAceptar.setOnClickListener(v -> dismiss());

        setFirstTimeText();

        this.show();
    }

    private void cambioTiempo(final int tiempo) {
        setSelectedTimeText(tiempo);
        final Integer finalTime = tiempo * SECONDS_IN_MINUTE * MILLIS_IN_SECOND;
        MposApplication.getInstance().getPreferenceManager().setValue(TIEMPO_INACTIVIDAD, finalTime);

        mListener.detenerTemporizador();
        mListener.instanciarContadorLogOut(finalTime);
        mListener.iniciarContador();
    }

    public ConfigMenuFragmentTiempoInactividad setListener(final HomeActivity listener) {
        this.mListener = listener;
        return this;
    }
}
