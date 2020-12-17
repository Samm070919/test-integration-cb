package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mi_cuenta;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.BotonClickUnico;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuMiCuentaBinding;
import com.pagatodoholdings.posandroid.secciones.Preference;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.kit.KitSolicitarActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ConfigMenuConfiguracionFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.help.HelpMainMenuFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.logout.LogoutFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_info_bancaria.ConfigMenuInfoBancariaAndCardsFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mis_datos.ConfigMenuMisDatosFragment;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.ya_ganaste_cards.YaGanasteCardsFragment;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.MediaColumns.DATA;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.NOMBRE;
import static com.pagatodoholdings.posandroid.utils.Constantes.PREFIJOIMAGEN;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigMenuMiCuentaFragment extends AbstractConfigMenu {

    private FragmentConfigMenuMiCuentaBinding binding;
    CircleImageView circleImage;
    private static final int RESULT_LOAD_IMAGE = 1;
    private HomeActivity homeActivity;
    private Preference prf;
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        prf = new Preference(getActivity());
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        homeActivity = (HomeActivity) getActivity();
        binding = DataBindingUtil.inflate(infalter, R.layout.fragment_config_menu_mi_cuenta, container, false);
        circleImage = binding.profileImage;


        if(prf.getProfileImage("personal") == null){
            circleImage.setImageResource(R.drawable.ic_agregar_foto);
        }else{
            circleImage.setImageURI(android.net.Uri.parse(
                    URI.create(
                            prf.getProfileImage("personal")
                    ).toString()));
        }


        binding.TextViewNameProfile.setText(
                MposApplication
                        .getInstance()
                        .getPreferenceManager()
                        .getValue(NOMBRE, "") + " " +
                        MposApplication
                                .getInstance()
                                .getDatosLogin()
                                .getCliente()
                                .getApellido1() + " " +
                        MposApplication
                                .getInstance()
                                .getDatosLogin()
                                .getCliente()
                                .getApellido2()
        );
        binding.TextViewAgent.setText(getResources().getString(R.string.title_codigo_cliente)+": "+ MposApplication.getInstance().getDatosLogin().getDatosTpv().getClicod()); //Obtener Agente

        binding.tvCuentaTpvCode.setText("TPV code: " + MposApplication
                .getInstance()
                .getDatosLogin()
                .getDatosTpv()
                .getTpvcod()
        );

        binding.tvCuentaTpvCode.setVisibility(View.GONE);

        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
        binding.ivClose.setOnClickListener(v -> cerrar());


        binding.btnMisDatos.setOnClickListener(v -> cargarFragmentMisDatos());

        binding.btnInfoBancaria.setOnClickListener(v -> cargarFragmentInformacionBancaria());

        binding.btnAyuda.setOnClickListener(v -> cargarFragmentAyuda());

        binding.btnConfiguracion.setOnClickListener(v -> cargarFragmentConfiguracion());

        binding.btnComprarDispositivo.setOnClickListener(v -> cargarFragmentKit());

        binding.btnCerrarSesion.setOnClickListener(v ->  sendToLogoutFragment());

        binding.btnYaganasteCards.setOnClickListener(v -> loadYaGansteCards());
    }

    /*
     * Dialogo creado para Mostrar la Ventana de Cerrar Sesión
     * */

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
        btnCancelar.setText(getString(R.string.btnActualizarProductos));
        btnCancelar.setTextSize(14);
        btnCancelar.setOnClickListener(view12 -> {
            callBack.onCancel();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                StorageUtil.copyFile(new File(picturePath), new File(StorageUtil.getStoragePath() + PREFIJOIMAGEN));
            }catch ( IOException exe ){
                Logger.LOGGER.throwing(TAG,1,exe,exe.getMessage());

            }
        }
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activityHome = (HomeActivity) getActivity();
            activityHome.iniciarContador();
        }
    }

    private void cargarFragmentMisDatos() {
        final ConfigMenuMisDatosFragment fragmentMisDatos = new ConfigMenuMisDatosFragment();
        fragmentMisDatos.setListener(getListener());
        homeActivity.cargarFragmentsCuenta(View.GONE, homeActivity.generaListener(fragmentMisDatos));
    }

    private void cargarFragmentInformacionBancaria() {
        final ConfigMenuInfoBancariaAndCardsFragment fragmentInfoBancaria =
                new ConfigMenuInfoBancariaAndCardsFragment();
        fragmentInfoBancaria.setListener(getListener());
        homeActivity.cargarFragmentCuentaBancariaAndCards(fragmentInfoBancaria);
    }

    private void cargarFragmentKit(){
        Intent intent = new Intent(getActivity(), KitSolicitarActivity.class);
        intent.putExtra(Constantes.ACTIVITY_CODE_KEY, Constantes.REQUEST_COMPRA_KIT_BY_MENU);
        startActivityForResult(intent, 4);
    }


    private void cargarFragmentAyuda() {
        final HelpMainMenuFragment fragment = new HelpMainMenuFragment();
        fragment.setListener(getListener());
        homeActivity.cargarFragments(View.GONE, homeActivity.generaListener(fragment));
    }

    private void cargarFragmentConfiguracion(){
        final ConfigMenuConfiguracionFragment fragmentConfiguracion = new ConfigMenuConfiguracionFragment();
        fragmentConfiguracion.setListener(getListener());
        homeActivity.cargarFragmentsCuenta(View.GONE, homeActivity.generaListener(fragmentConfiguracion));
    }

    private void cerrar() {
        binding.fragmentCuenta.setVisibility(View.GONE);
        getActivity().getSupportFragmentManager().beginTransaction().remove(ConfigMenuMiCuentaFragment.this).commit();
        if (getParentFragment() != null) {
            ((DialogFragment) getParentFragment()).dismiss();
        } else {
            closeFragment(ConfigMenuMiCuentaFragment.this);
        }
    }

    @Override
    protected boolean isTomandoBack() {
        return false;
    }

    @Override
    public void onFailure(Throwable throwable) {
        //No definido
    }

    private void sendToLogoutFragment(){
        final LogoutFragment fragment = new LogoutFragment();
        fragment.setListener(homeActivity);
        homeActivity.cargarFragments(View.GONE, homeActivity.generaListener(fragment));
    }

    private void loadYaGansteCards(){
        final YaGanasteCardsFragment fragment = new YaGanasteCardsFragment();
        fragment.setListener(getListener());
        homeActivity.cargarFragments(View.GONE, homeActivity.generaListener(fragment));
    }
}
