package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuConfiguracionBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.cambio_contraseña.ChangePasswordFragment;
import com.pagatodoholdings.posandroid.utils.Utilities;

import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DONGLE_VINCULADO;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.TIPO_USUARIO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigMenuConfiguracionFragment extends AbstractConfigMenu {

    private FragmentConfigMenuConfiguracionBinding binding;
    private HomeActivity hActivity;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        hActivity = (HomeActivity) getActivity();

        binding = FragmentConfigMenuConfiguracionBinding.inflate(infalter, container, false);

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });

        binding.btnChangePassword.setOnClickListener(view -> {
            cargaChangePasswordFragment();
        });

        /*binding.btnActualizarProductos.setOnClickListener(view -> {
            if(getActivity() instanceof HomeActivity) {
                final HomeActivity activity = (HomeActivity) getActivity();
                activity.onActualizarProductos();
            }
        });*/

        binding.btnVincularTarjeta.setOnClickListener(v -> cargarFragmentVincularTarjeta());

        binding.btnBloqueoAutomatico.setOnClickListener(v -> cargarFragmentBloqueoAutomatico());

        binding.btnVelocidadConexion.setOnClickListener(v -> cargarFragmentVelocidadConexion());

        binding.ivClose.setOnClickListener(v -> loadMiCuenta(ConfigMenuConfiguracionFragment.this));

    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }

    private void cargarFragmentBloqueoAutomatico() {
        ConfigMenuFragmentTiempoInactividad tiempoInactividadAlert =
                new ConfigMenuFragmentTiempoInactividad(
                        getActivity(),
                        true,
                        DialogInterface::dismiss
                );

        tiempoInactividadAlert.initUI();
    }

    private void cargarFragmentVelocidadConexion() {
        ConfigMenuWifiFragment wifiAlert = new ConfigMenuWifiFragment(getActivity(), true, DialogInterface::dismiss);

        wifiAlert.initUI();
    }

    private void cargarFragmentVincularTarjeta() {

        if(!preferenceManager
                .getValue(TIPO_USUARIO,"")
                .equals(Utilities.TipoUsuario.SHOP.getTipo())
        ) {
            if (preferenceManager.isExiste(DONGLE_VINCULADO)) {
                final ConfigMenuDongleVinculadoFragment dongleVinculadoFragment =
                        new ConfigMenuDongleVinculadoFragment();
                dongleVinculadoFragment.setListener(getListener());
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                getListener()
                                        .getBinding()
                                        .flMainPantallaCompleta
                                        .getId(),
                                dongleVinculadoFragment
                        ).commit();
            } else {
                final ConfigMenuConfiguracionSubVincular fragmentSubvincular =
                        new ConfigMenuConfiguracionSubVincular();
                fragmentSubvincular.setListener(getListener());
                getActivity().getSupportFragmentManager().beginTransaction().replace(
                        getListener().getBinding().flMainPantallaCompleta.getId(),
                        fragmentSubvincular
                ).commit();
            }
        }else{
            //Registrar negocio
        }
    }

    @Override
    protected boolean isTomandoBack() {
        loadMiCuenta(this);

        return true;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No definido
    }

    private void cargaChangePasswordFragment(){
        final ChangePasswordFragment fragment = new ChangePasswordFragment();
        fragment.setListener(getListener());
        hActivity.cargarFragments(View.GONE, hActivity.generaListener(fragment));
    }
}
