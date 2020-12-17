package com.pagatodoholdings.posandroid.secciones.money_in.retrofit;

import android.location.Location;
import com.pagatodo.sigmalib.listeners.RetrofitListener;
import com.pagatodoholdings.posandroid.MposApplication;
import com.pagatodoholdings.posandroid.R;
import com.pagatodoholdings.posandroid.secciones.money_in.models.google_places.NearbyPlaces;
import com.pagatodoholdings.posandroid.utils.Logger;
import com.pagatodoholdings.posandroid.utils.OkHttpClientUtil;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyPlacesInteractor {

    private static final String TAG = NearbyPlacesInteractor.class.getSimpleName();
    private static final String RADIUS_SEARCH = "500";
    private static final String TYPE_SEARCH = "bank";
    private static final String BASE_URL = "https://maps.googleapis.com/";

    private GoogleApiPlacesInterface service;


    public NearbyPlacesInteractor() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClientUtil.getUnsafeOkHttpClient())
                .build();
        service = retrofit.create(GoogleApiPlacesInterface.class);
    }

    public void searchNearbyPlaces(final String searchText, final Location location, final RetrofitListener interactorListener) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String sLocation = latitude + "," + longitude;

        Call<NearbyPlaces> nearbyPlacesCall = service.getNearbyPlaces(sLocation, RADIUS_SEARCH, TYPE_SEARCH, searchText, MposApplication.getInstance().getString(R.string.google_api_key));

        nearbyPlacesCall.enqueue(new Callback<NearbyPlaces>() {
            @Override
            public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {

                if (response.body() != null && response.isSuccessful()) {
                    interactorListener.onSuccess(response.body());
                } else {
                    try {
                        interactorListener.onFailure(new Throwable(response.errorBody().string()));
                        Logger.LOGGER.throwing(TAG, 1, new Throwable(response.message()), response.message());
                    }
                    catch ( IOException exe ) {
                        Logger.LOGGER.throwing(TAG, 1, exe, exe.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<NearbyPlaces> call, Throwable exc) {
                interactorListener.onFailure(exc);
                Logger.LOGGER.throwing(TAG, 1, exc, exc.getMessage());
            }
        });
    }


}
