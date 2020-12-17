package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mis_datos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pagatodo.sigmalib.ApiData;
import com.pagatodo.sigmalib.SigmaBdManager;
import com.pagatodo.sigmalib.listeners.OnFailureListener;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodo.sigmalib.util.StorageUtil;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuMisDatosBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.AbstractConfigMenu;
import com.pagatodoholdings.posandroid.secciones.registro.registro4l.RegistroRecibePagosTarjetaFragment;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosPersonales;
import com.pagatodoholdings.posandroid.secciones.retrofit.MisDatosService;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.MediaColumns.DATA;
import static com.pagatodo.sigmalib.util.Constantes.Preferencia.DONGLE_VINCULADO;
import static com.pagatodoholdings.posandroid.utils.Constantes.PREFIJOIMAGEN;
import static com.pagatodoholdings.posandroid.utils.Utilities.getProfilePic;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigMenuMisDatosFragment extends AbstractConfigMenu {
    private static final int PERMISSION_CODE = 1000;
    Uri imageUri;
    private int IMAGE_CAPTURE_CODE = 1001;

    private FragmentConfigMenuMisDatosBinding binding;
    private static final int RESULT_LOAD_IMAGE = 1;
    private  ConfigMenuPagerAdapter adapter;
    private ConfigMenuMisDatosPersonalFragment tabPersonal;
    private ConfigMenuMisDatosNegocioFragment tabNegocio;
    private RegistroRecibePagosTarjetaFragment tabShopperBusiness;
    private MisDatosService misDatosService;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        initUI(inflater, container);
        return binding.getRoot();
    }


    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        binding = DataBindingUtil.inflate(infalter, R.layout.fragment_config_menu_mis_datos,container, false);
        misDatosService = new MisDatosService();
        adapter = new ConfigMenuPagerAdapter(getFragmentManager());

        if (StorageUtil.validarArchivo(StorageUtil.getStoragePath() + PREFIJOIMAGEN)) {
            binding.profileCircle.setVisibility(View.VISIBLE);
            Picasso.with(getActivity().getApplicationContext()).load(imageUri)
                    .resize(74, 74)
                    .into(binding.profileCircle);
        }


        binding.getRoot().setOnTouchListener((view, motionEvent) -> {
            reiniciarContador();
            return false;
        });
        binding.ivClose.setOnClickListener(v -> {
            if(tabNegocio!=null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(tabNegocio).commit();
            }
            if(tabPersonal!=null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(tabPersonal).commit();
            }
            if(tabShopperBusiness!=null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(tabShopperBusiness).commit();
            }
            loadMiCuenta(ConfigMenuMisDatosFragment.this);
        });

        binding.profileCircle.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
        });

        inicializarTabs();

    }


    private void inicializarTabs()
    {
        getListener().muestraProgressDialog("Cargando Datos");

        //Cargar Datos Personales
        misDatosService.getDatosPersonales(new RetrofitListener<DatosPersonales>() {
            @Override
            public void showMessage(String message) {
                //No implementation
            }

            @Override
            public void onFailure(Throwable throwable) {
                Logger.LOGGER.throwing(
                        "DATOS PERSONALES",
                        1,
                        throwable,
                        "Error al consultar los Datos Personales"
                );
                //Cargar Datos Negocio
                consumirServicioDatosNegocio();
            }

            @Override
            public void onSuccess(DatosPersonales datos) {
                //Cargar Datos Negocio
                DatosNegocio datosNegocioPreferencia =
                        MposApplication.getInstance().getDatosNegocio();


                if(datosNegocioPreferencia!=null){
 					if(preferenceManager.isExiste(DONGLE_VINCULADO)
                            && datosNegocioPreferencia.getTipnegocio()!=0) {

                        getListener().ocultaProgressDialog();
                        cargarTabDatosPersonales(datos);
                        cargarTabDatosNegocio(datosNegocioPreferencia);
                        //Setup
                        binding.viewpager.setAdapter(adapter);
                        binding.tablayout.setupWithViewPager(binding.viewpager);
                    } else if((preferenceManager.isExiste(DONGLE_VINCULADO) || !preferenceManager.isExiste(DONGLE_VINCULADO))
                            && datosNegocioPreferencia.getTipnegocio()==0) {

                        getListener().ocultaProgressDialog();
                        cargarTabDatosPersonales(datos);
                        loadShopperData();
                        //Setup
                        binding.viewpager.setAdapter(adapter);
                        binding.tablayout.setupWithViewPager(binding.viewpager);
                    }else if(!preferenceManager.isExiste(DONGLE_VINCULADO)
                            && datosNegocioPreferencia.getTipnegocio()!=0) {

                        getListener().ocultaProgressDialog();
                        cargarTabDatosPersonales(datos);
                        cargarTabDatosNegocio(datosNegocioPreferencia);
                        //Setup
                        binding.viewpager.setAdapter(adapter);
                        binding.tablayout.setupWithViewPager(binding.viewpager);
                    }
                }else{
                    cargarTabDatosPersonales(datos);
                    consumirServicioDatosNegocio();
                    binding.viewpager.setAdapter(adapter);
                    binding.tablayout.setupWithViewPager(binding.viewpager);
                }//end if datosNegocio Validation
            }
        },      MposApplication.getInstance().getDatosLogin().getCliente().getEmail(),
                MposApplication.getInstance().getDatosLogin().getToken(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());
    }

    private void consumirServicioDatosNegocio()
    {

        //Cargar Datos Negocio
        misDatosService.getDatosNegocio(new RetrofitListener<DatosNegocio>() {
            @Override
            public void showMessage(String message) {
                getListener().ocultaProgressDialog();
                //No implementation
            }


            @Override
            public void onFailure(Throwable throwable) {
                getListener().ocultaProgressDialog();
                Logger.LOGGER.throwing("DATOS NEGOCIO", 1, throwable, "Error al consultar los Datos Negocio");
                //Hardcode datosNegocio
                DatosNegocio datosNegocio = null;
                getListener().despliegaModal(true,false,"¡Error!", "No se pudieron cargar los Datos del Negocio", () -> {
                    if(tabNegocio!=null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(tabNegocio).commit();
                    }
                    if(tabPersonal!=null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(tabPersonal).commit();
                    }
                    if(tabShopperBusiness!=null) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(tabShopperBusiness).commit();
                    }
                    loadMiCuenta(ConfigMenuMisDatosFragment.this);
                });
                cargarTabDatosNegocio(datosNegocio);

                //Setup
                binding.viewpager.setAdapter(adapter);
                binding.tablayout.setupWithViewPager(binding.viewpager);
            }

            @Override
            public void onSuccess(DatosNegocio datosNegocio) {
                getListener().ocultaProgressDialog();
                cargarTabDatosNegocio(datosNegocio);

                //Setup
                binding.viewpager.setAdapter(adapter);
                binding.tablayout.setupWithViewPager(binding.viewpager);
            }
        }, ApiData.APIDATA.getDatosSesion().getIdSesion(),
                MposApplication.getInstance().getDatosLogin().getDatosTpv().getTpvcod());

    }

    private void cargarTabDatosPersonales(DatosPersonales datosPersonales)
    {

        if(datosPersonales !=null)
        {
            tabPersonal = new ConfigMenuMisDatosPersonalFragment();
            tabPersonal.setListener(datosPersonales);
            //Add Tabs
            adapter.addFragment(tabPersonal, getString(R.string.title_mi_cuenta_tipo_personal));
        }

    }

    private void cargarTabDatosNegocio(DatosNegocio datosNegocio) {
        // Add Tab Negocio si es PCI.
        Boolean isPCI =
                SigmaBdManager.isPCI(
                        new OnFailureListener.BasicOnFailureListener(
                                "MENÜ MIS DATOS",
                                "Validación Menú Mis Datos"
                        )
                );

        if(isPCI != null && isPCI.booleanValue() && datosNegocio !=null) {
            tabNegocio = new ConfigMenuMisDatosNegocioFragment();
            tabNegocio.setListener(datosNegocio);
            adapter.addFragment(tabNegocio,getString(R.string.title_mi_cuenta_tipo_negocio));
        }
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
                Logger.LOGGER.throwing("MENÜ MIS DATOS",1,exe,exe.getMessage());

            }
            binding.profileCircle.setVisibility(View.VISIBLE);
            binding.profileCircle.setImageBitmap(getProfilePic());
        }
    }

    private void reiniciarContador() {
        if (getActivity() instanceof HomeActivity) {
            final HomeActivity activity = (HomeActivity) getActivity();
            activity.iniciarContador();
        }
    }


    public static class ConfigMenuPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList =null;
        private List<String> fragmentListTitles=null;



        public ConfigMenuPagerAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
            fragmentList = new ArrayList<>();
            fragmentListTitles = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return fragmentListTitles.size();
        }

        @Override
        public Fragment getItem(final int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentListTitles.get(position);
        }

        public void addFragment(Fragment fragment, String title)
        {
            fragmentList.add(fragment);
            fragmentListTitles.add(title);
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

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        imageUri =
                getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openCamera();
                } else {
                    Toast.makeText(getActivity(), "Permissiondenied...", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            default:
                break;
        }
    }

    private void loadShopperData(){
        tabShopperBusiness = new RegistroRecibePagosTarjetaFragment(Constantes.REQUEST_COMPRA_KIT_BY_MINEGOCIO);
        adapter.addFragment(tabShopperBusiness,getString(R.string.title_mi_cuenta_tipo_negocio));
    }
}
