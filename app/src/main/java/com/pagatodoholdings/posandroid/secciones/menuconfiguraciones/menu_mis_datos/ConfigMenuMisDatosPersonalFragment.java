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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.databinding.FragmentConfigMenuMisDatosPersonalBinding;
import com.pagatodoholdings.posandroid.secciones.Preference;
import com.pagatodoholdings.posandroid.secciones.retrofit.DatosPersonales;
import com.pagatodoholdings.posandroid.utils.Constantes;
import com.squareup.picasso.Picasso;

import java.net.URI;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfigMenuMisDatosPersonalFragment extends androidx.fragment.app.Fragment {
    private static final int PERMISSION_CODE = 1000;
    Uri imageUri;
    private int IMAGE_CAPTURE_CODE = 1001;
    private Preference prf;


    private FragmentConfigMenuMisDatosPersonalBinding binding;
    private DatosPersonales datosPersonalesBean;

    public ConfigMenuMisDatosPersonalFragment() {
        // Required empty public constructor
    }

    public void setListener(final DatosPersonales datosPersonalesBean) {
        this.datosPersonalesBean =datosPersonalesBean;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        prf = new Preference(getActivity());

        initUI(inflater, container);
        return binding.getRoot();
    }

    private void initUI(final LayoutInflater infalter, final ViewGroup container) {
        binding = DataBindingUtil.inflate(infalter, R.layout.fragment_config_menu_mis_datos_personal, container, false);
        cargarDatosMiCuenta(); //Quitar Hardcode y consumir servicio para obtener datos de mi Cuenta

        binding.profileImagePersonal.setOnClickListener(v -> {
            takePicture();
        });
    }

    private void cargarDatosMiCuenta()
    {
        //Deshabilidar edición
        binding.etdNombre1.setEnabled(false);
        binding.etdPrimerApellido.setEnabled(false);
        binding.etdSegundoApellido.setEnabled(false);
        binding.etdCelular.setEnabled(false);
        binding.etdCorreo.setEnabled(false);

        binding.etdNombre1.setText(datosPersonalesBean.getNombre());
        binding.etdPrimerApellido.setText(datosPersonalesBean.getApellido1());
        binding.etdSegundoApellido.setText(datosPersonalesBean.getApellido2());
        binding.etdCelular.setText(datosPersonalesBean.getTelefono());
        binding.etdCorreo.setText(datosPersonalesBean.getEmail());
        if(prf.getProfileImage("personal") == null){
            binding.profileImagePersonal.setImageResource(R.drawable.ic_agregar_foto);
        }else{
            Picasso.with(getActivity().getApplicationContext()).load(prf.getProfileImage("personal"))
                    .resize(74, 74)
                    .into(binding.profileImagePersonal);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode != Constantes.REQUEST_ADD_CARD_BY_MENU && requestCode != Constantes.REQUEST_DATOS_NEGOCIO_BY_MENU) {
                //binding.profileImagePersonal.setImageURI(imageUri);
                Picasso.with(getActivity().getApplicationContext()).load(imageUri)
                        .resize(74, 74)
                        .into(binding.profileImagePersonal);
                prf.setProfileImage("personal", imageUri.toString());
            }
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

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        } else {
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        }
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

}
