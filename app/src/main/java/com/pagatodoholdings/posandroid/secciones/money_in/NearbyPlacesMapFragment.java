package com.pagatodoholdings.posandroid.secciones.money_in;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.componentesvista.controlespersonalizados.ModalFragment;
import com.pagatodoholdings.posandroid.databinding.FragmentMoneyInCashNearbyPlacesMapBinding;
import com.pagatodoholdings.posandroid.secciones.home.HomeActivity;
import com.pagatodoholdings.posandroid.secciones.money_in.models.google_places.NearbyPlaces;
import com.pagatodoholdings.posandroid.secciones.money_in.models.google_places.Result;
import com.pagatodoholdings.posandroid.secciones.money_in.retrofit.NearbyPlacesInteractor;
import com.pagatodoholdings.posandroid.utils.Utilities;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class NearbyPlacesMapFragment extends AbstractMoney implements OnMapReadyCallback {

    private LocationManager locationManager;
    private Location actualLocation;
    private String searchNearbyPlacesText = "";
    private NearbyPlaces nearbyPlaces;
    private Handler mHandler = new Handler();
    private Context myContext;
    private Resources myResources;

    protected FragmentMoneyInCashNearbyPlacesMapBinding binding;

    private GoogleMap gMap;

    private static final String NEARBY_PLACE = "NEARBY_PLACE";
    private static final int GPS_REQUEST = 1111;
    private static final int GPS_ON = 0;

    public static NearbyPlacesMapFragment getInstance(final String nearbyPlace) {
        final NearbyPlacesMapFragment fragment = new NearbyPlacesMapFragment();
        final Bundle args = new Bundle();
        args.putSerializable(NEARBY_PLACE, nearbyPlace);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchNearbyPlacesText = getArguments().getString(NEARBY_PLACE);
        }

        myContext = getActivity().getApplicationContext();
        myResources = getResources();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMoneyInCashNearbyPlacesMapBinding.inflate(inflater, container, false);

        initUI();

        return binding.rlToolbar.getRootView();
    }


    @SuppressLint("NewApi")
    private void initUI() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        binding.ivOnBack.setOnClickListener(view -> backPreviousFragment());


        if (binding.mvNearbyPlaces != null) {
            binding.mvNearbyPlaces.onCreate(null);
            binding.mvNearbyPlaces.onResume();
            binding.mvNearbyPlaces.getMapAsync(this);
//            binding.ivCompartir.setOnClickListener(view ->{
//                takeScreenshot();
//            });
        }

    }


    private void checkGpsEnabled() {
        if (isGpsEnabled()) {
            obtenerUbicacion();
        } else {
            ((HomeActivity) getActivity()).ocultaProgressDialog();
            showInfoAlert();
        }
    }

    private boolean isGpsEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showInfoAlert() {
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setTitle("GPS")
                .setMessage("Habilite los permisos de ubicación")
                .setPositiveButton("Aceptar", (dialogInterface, i) -> habilitarUbicacion())
                .show();

    }

    public void habilitarUbicacion() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, GPS_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GPS_REQUEST) {
            if (resultCode == GPS_ON) {
                obtenerUbicacion();
            } else {
                checkGpsEnabled();
            }
        }

    }

    @Override
    public void onFailure(Throwable throwable) {
        //NOT IMPLEMENTED
    }

    @Override
    protected boolean isTomandoBack() {
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        checkGpsEnabled();

        binding.mvNearbyPlaces.setOnClickListener(view -> detenerContador());
    }


    private void obtenerUbicacion() {

        mHandler.postDelayed(() -> {
            actualLocation = devolverUbicacion();
            if (actualLocation != null) {
                markLocationAndNearbyPaces(actualLocation);
            } else {
                Toast.makeText(getActivity(), "No se pudo obtener geolocalización, intente más tarde", Toast.LENGTH_LONG).show();
            }
        }, 1000);

    }

    private Location devolverUbicacion() {
       Location location = null;
        LocationListener mlocListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onProviderEnabled(String provider) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onProviderDisabled(String provider) {
                //NOT IMPLEMENTED
            }
        };


        if (checkSelfPermission(
                myContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(
                        myContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            habilitarUbicacion();
        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            //Existe GPS_PROVIDER obtiene ubicación
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) { //Trata con NETWORK_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                //Existe NETWORK_PROVIDER obtiene ubicación
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location == null) { //Trata con PASSIVE_PROVIDER
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, mlocListener);
                //Existe PASSIVE_PROVIDER obtiene ubicación
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        if (location != null) {
            return location;
        } else {
            return null;
        }
    }

    private void markLocationAndNearbyPaces(final Location location) {
        if (location != null) {
            Marker marker;
            actualLocation = location;

            LatLng myPosition = new LatLng( actualLocation.getLatitude(), actualLocation.getLongitude());
            marker = gMap.addMarker(new MarkerOptions().position(myPosition));
            marker.setTitle("Mi Ubicación");
            marker.setIcon(vectorToBitmap(R.drawable.ic_my_location_marker_map));

            CameraPosition camera = new CameraPosition.Builder()
                    .target(myPosition)
                    .zoom(15) // LIMIT 21, 1 mundo 5 masa continental 10 ciudades 15 vista de calles 20 vista de edificios
                    .bearing(0) //0 - 365°, Orientación hacia el este en grados
                    .tilt(0) //0 - 90°,  inclinacion 3d edificios
                    .build();

            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
            getNearbyPlaces(searchNearbyPlacesText, actualLocation );
        }
    }


    private void getNearbyPlaces(final String searchText, final Location myLocation) {
        final NearbyPlacesInteractor interactor = new NearbyPlacesInteractor();

        interactor.searchNearbyPlaces(searchText, myLocation, new RetrofitListener() {
            @Override
            public void showMessage(String message) {
                //NOT IMPLEMENTED
            }

            @Override
            public void onFailure(Throwable throwable) {
                nearbyPlaces.getResults().clear();
                ((HomeActivity) getActivity()).ocultaProgressDialog();
            }

            @Override
            public void onSuccess(Object result) {
                nearbyPlaces = (NearbyPlaces) result;

                createNearbyPlacesMarkers(nearbyPlaces);
                ((HomeActivity) getActivity()).ocultaProgressDialog();
            }
        });

    }

    private void createNearbyPlacesMarkers(NearbyPlaces places) {
        Marker marker;
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng;

        for (Result place: places.getResults()) {
            latLng = new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLng());
            markerOptions.position(latLng);

            marker = gMap.addMarker(markerOptions);
            marker.setTitle(place.getName());
            marker.setSnippet(place.getName());
            marker.setIcon(vectorToBitmap(R.drawable.ic_bank_marker_map));
        }

    }


    private BitmapDescriptor vectorToBitmap(@DrawableRes int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(myResources, id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void takeScreenshot() {
        if (Utilities.takeScreenshot(getActivity().getWindow().getDecorView().getRootView())) {
            showDialog(R.layout.layout_money_in_screenshot, new ModalFragment.CommonDialogFragmentCallBack() {
                @Override
                public void onAccept() {
                    //none
                }
            }, getString(R.string.txt_close));
        }
    }
}
