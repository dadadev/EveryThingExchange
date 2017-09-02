package com.dadabit.everythingexchange.utils.geocode;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dadabit.everythingexchange.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeocodeManager {


    private LocationManager locationManager;
    private LocationResponseCallback mCallback;
    private Context context;
    private double latitude = 0;
    private double longitude = 0;

    public GeocodeManager(Context context, LocationResponseCallback mCallback) {
        Log.d("@@@", "GeocodeManager.create");
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.mCallback = mCallback;

        getCoordinates();
    }

    private void getCoordinates() {


        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
//                ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (locationGPS != null){
            latitude = locationGPS.getLatitude();
            longitude = locationGPS.getLongitude();
        } else {
            Log.d("@@@", "GeocodeManager.locationGPS == null");

            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (locationNet != null){
                latitude = locationNet.getLatitude();
                longitude = locationNet.getLongitude();
            } else {
                Log.d("@@@", "GeocodeManager.locationNetwork == null");

            }
        }

        //Jerusalem
//        latitude = 31.762682;
//        longitude = 35.212486;

        if (latitude != 0 && longitude != 0){
            Log.d("@@@", "GeocodeManager.sendCoordinates: "+ latitude+","+longitude);

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl("https://maps.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create());

            Retrofit retrofit = builder.build();

            GoogleMapsClient client = retrofit.create(GoogleMapsClient.class);
            Call<GmapLocation> call = client.getLocation(
                    latitude+","+longitude,
                    context.getResources().getString(R.string.apiResultType),
                    context.getResources().getString(R.string.geoApiKey));

            call.enqueue(new Callback<GmapLocation>() {
                @Override
                public void onResponse(@NonNull Call<GmapLocation> call, @NonNull retrofit2.Response<GmapLocation> response) {

                    try {

                        GmapLocation location = response.body();

                        if (location != null) {
                            String[] fullLocation = location.getAdress().split(", ");

                            if (fullLocation[0].length() != 0 && fullLocation[1].length() != 0){

                                mCallback.onResponse(fullLocation);

                            }

                        }


                    } catch (Exception e){
                        Log.d("@@@", "MainActivityPresenter.GetGeolocationException (!): "+e.getMessage());

                    }


                }

                @Override
                public void onFailure(Call<GmapLocation> call, Throwable t) {
                    Log.d("@@@", "MainActivityPresenter.onErrorResponse: "+t.getMessage());
                }
            });

        }

    }

}
