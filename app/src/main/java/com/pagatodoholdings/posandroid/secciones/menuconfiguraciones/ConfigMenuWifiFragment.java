package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuWifiBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.interactor.InternetStatusInteractor;

public class ConfigMenuWifiFragment extends AlertDialog {

    //----------Inst-------------------------------------------------------
    protected static final String TAG = ConfigMenuWifiFragment.class.getSimpleName();

    //----------UI-------------------------------------------------------
    private FragmentConfigMenuWifiBinding binding;

    protected ConfigMenuWifiFragment(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }

    @SuppressLint("ClickableViewAccessibility")
    public void initUI() {
        final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        binding = FragmentConfigMenuWifiBinding.inflate(layoutInflater, null, false);
        setView(binding.getRoot());

        final TypedValue typedValue = new TypedValue();
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getContext().getTheme().resolveAttribute(R.attr.wifi_gift, typedValue, true);
        //binding.imgWifi.setGifResource(typedValue.resourceId);
        InternetStatusInteractor wifi = new InternetStatusInteractor(getContext(), new InternetStatusInteractor.InteractorListener() {

            @Override
            public void onWifiStatusUpdate(final Integer mbps, final Integer stateconn, final Integer typeconn) {
                String velocidad = "No Disponible";
                if (mbps != 0) {
                    velocidad = mbps.toString();
                }
                binding.wifiMbpsData.setText(velocidad);
                binding.wifiStateData.setText(getContext().getString(stateconn));
                binding.wifiTypeData.setText(getContext().getString(typeconn));

            }

            @Override
            public void onErrorWifi(final String message) {
                ((HomeActivity) getContext()).despliegaModal(true, false, "Conexión de Red", message, () -> {
                });
            }

            @Override
            public void finish() {
                //NOT IMPLEMENTED
            }
        });
        wifi.execute();

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });

        this.show();

        binding.btnConfirmacion.setOnClickListener(view -> dismiss());

        //binding.cancelDialog.setOnClickListener(view -> dismiss());
    }


    private void reiniciarContador() {
        if (getContext() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getContext();
            activity.iniciarContador();
        }
    }

}
