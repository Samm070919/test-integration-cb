package com.pagatodoholdings.posandroid.secciones.menuconfiguraciones.menu_mis_datos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.BuildConfig;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.Country;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.adaptadorespersonalizados.GenericAdaptadorSpinner;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuMisDatosNegocioBinding;
import com.pagatodoholdings.posandroid.secciones.Preference;
import com.pagatodoholdings.posandroid.secciones.registro.RegistroInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoDocBean;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoDocumentoInteractor;
import com.pagatodoholdings.posandroid.secciones.retrofit.datos_negocio_entity.DatosNegocio;
import com.pagatodoholdings.posandroid.secciones.retrofit.TipoNegocioBean;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.pagatodoholdings.posandroid.utils.Logger.LOGGER;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigMenuMisDatosNegocioFragment extends androidx.fragment.app.Fragment {
    private static final int PERMISSION_CODE = 1000;
    Uri imageUri;
    private int IMAGE_CAPTURE_CODE = 1001;
    private Preference prf;

    private FragmentConfigMenuMisDatosNegocioBinding binding;
    private DatosNegocio datosNegocio;
    private String tipoNegocio;
    private RegistroInteractor registroInteractor;
    private static final String TAG = ConfigMenuMisDatosNegocioFragment.class.getSimpleName();

    public ConfigMenuMisDatosNegocioFragment() {
        // Required empty public constructor
    }

    public void setListener(final DatosNegocio datosNegocio) {
        this.datosNegocio =datosNegocio;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(
            final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState
    ) {
        prf = new Preference(getActivity());
        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        registroInteractor = new RegistroInteractor();
        binding = DataBindingUtil.inflate(
                infalter,
                R.layout.fragment_config_menu_mis_datos_negocio,
                container,
                false
        );
        cargarDatosMiCuenta();

        binding.profileImageBusiness.setOnClickListener(v -> {
            takePicture();
        });
    }


    private void cargarDatosMiCuenta() {
        //Deshabilidar edición
        binding.etdNombreNegocio.setEnabled(false);
        binding.etdActividad.setEnabled(false);
        binding.etdDireccionNegocio.setEnabled(false);

        binding.etdNombreNegocio.setText(datosNegocio.getRs());
        binding.etdDireccionNegocio.setText(datosNegocio.getDireccion());

        binding.inputNumberFormat.initComponent(datosNegocio.getDocid().length());
        binding.inputNumberFormat.setCode(datosNegocio.getDocid());

        if(Country.PERU.getItemIsoCode().equals(
                MposApplication
                        .getInstance()
                        .getDatosLogin()
                        .getPais()
                        .getCodigo()
        )){
            setDocType();
        }

        if(prf.getProfileImage("business") == null){
            binding.profileImageBusiness.setImageResource(R.drawable.ic_agregar_foto);
        }else{
            binding.profileImageBusiness.setImageURI(android.net.Uri.parse(
                    URI.create(
                            prf.getProfileImage("business")
                    ).toString()));
        }

        /*binding.profileImageBusiness.setImageURI(
                ((MposApplication) getActivity()
                        .getApplication())
                        .getBusinessImage()
        );*/

        registroInteractor.getTipoNegocio(new RetrofitListener<List<TipoNegocioBean>>() {
            @Override
            public void showMessage(String message) {
                //No implementation
            }

            @Override
            public void onFailure(Throwable throwable) {
                Logger.LOGGER.throwing("DATOS NEGOCIO", 1, throwable, "Error al consultar los Tipo de Negocio");
                tipoNegocio= String.valueOf(datosNegocio.getTipnegocio());
                binding.etdActividad.setText(tipoNegocio);
            }

            @Override
            public void onSuccess(List<TipoNegocioBean> result) {
                for(TipoNegocioBean n : result)
                {
                    if(n.getTipnegocio() == datosNegocio.getTipnegocio())
                    {
                        binding.etdActividad.setText(n.getDescripcion());
                        return;
                    }
                }

                binding.etdActividad.setText(result.get(0).getDescripcion());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            prf.setProfileImage("business", imageUri.toString());
            //binding.profileImageBusiness.setImageURI(imageUri);
            Picasso.with(getActivity().getApplicationContext()).load(imageUri)
                    .resize(74, 74)
                    .into(binding.profileImageBusiness);
        }
    }


    private void takePicture(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)  ==
                    PackageManager.PERMISSION_GRANTED
                /*PackageManager.PERMISSION_GRANTED(Manifest.permission.CAMERA).PERMISSION_DENIED*/){
                String[] permission =
                        {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission,PERMISSION_CODE);
            } else{
                openCamera();
            }
        }
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
        //((MposApplication) getActivity().getApplication()).setBusinessImage(imageUri);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

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
            }
        }
    }

    private void setDocType(){
        String baseUrl = (MposApplication.getInstance().getDatosLogin().getPais() != null
                && MposApplication.getInstance().getDatosLogin().getPais().getUrlwalletpublica() != null)
                ? MposApplication.getInstance().getDatosLogin().getPais().getUrlwalletpublica()
                : BuildConfig.URL_REGISTRO;

        new TipoDocumentoInteractor(baseUrl).getTiposDocNegocio(new RetrofitListener<List<TipoDocBean>>() {
            @Override
            public void onSuccess(final List<TipoDocBean> tList) {
                if(tList!=null && !tList.isEmpty()) {
                    binding.tvNegocioDocType.setText(getTipoDocDescription(tList));
                }
            }

            @Override
            public void onFailure(final Throwable thr) {

                LOGGER.throwing(TAG, 3, thr, "Error al cargar giro");
            }

            @Override
            public void showMessage(final String message) {
                //Nothing
            }
        });
    }

    private String cleanDocType(List<TipoDocBean> tList){
        int start, end;

        start = tList.get(2).getDescripcion().indexOf("(");
        end = tList.get(2).getDescripcion().indexOf(")");
        return tList.get(2).getDescripcion().substring(start + 1, end);
    }

    private String getTipoDocDescription(List<TipoDocBean> list){
        String result = "";
        int localInt = datosNegocio.getTipdoc().intValue();
        for(int i = 0; i < list.size(); i++){
            if(localInt == list.get(i).getTipodoc().intValue()){
                result = list.get(i).getDescripcion();
            }
        }

        return result;
    }
}
