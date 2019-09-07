package com.example.zivotinje;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;

import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class MapActivity extends Fragment implements OnMapReadyCallback {
    @Nullable
    private GoogleMap mMap;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    FirebaseDatabase database;
    DatabaseReference myRef ;
    private final float DEFAULT_ZOOM=18;
    Geocoder mGeocoder;
    LatLng zagreb;
    private List adrese;
    List<Address> addresses;
   // ArrayList<MarkerData> markersArray = new ArrayList<MarkerData>();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_activity,container,false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapView = mapFragment.getView();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sklonista");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //mUploads = new HashMap();
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setMyLocationEnabled(true);
        zagreb = new LatLng( 45.815399, 15.966568);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zagreb,6));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(zagreb));

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });


        postavi_markere();




    }
    private void postavi_markere(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                mGeocoder=new Geocoder(getActivity(), Locale.getDefault());
                //Log.d("Podaciii", dataSnapshot.getValue().toString());
                //Log.d("proba", dataSnapshot.getValue(key));
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    //Log.d("podatakpopodatak",postSnapshot.child("adresa").getValue().toString());

                    ///Log.d("adresica",adrese.toString());
                    try {
                        addresses=mGeocoder.getFromLocationName(adrese.toString(),1);
                        Log.d("adresica",addresses.toString());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(addresses.get(i).getLatitude(), addresses.get(i).getLongitude())).title(postSnapshot.child("naziv").getValue().toString()));



                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Log.d("Podatakadrese", String.valueOf(ds.getValue()));
                    //Upload upload = postSnapshot.getValue(Upload.class);
                    //upload.getMapa();
                    //Log.d("proba1",postSnapshot.getChildren().toString());
                    //Adresa adresa=postSnapshot.getValue(Adresa.class);
                    //adresa.setMapa(postSnapshot.getKey(),postSnapshot);

                   // Log.d("proba2",adresa.getMapa().toString());
                   // mUploads.put(upload);
                    //Log.d("podatakpopodatak",postSnapshot.getChildren());
                   /* for (DataSnapshot ps : postSnapshot.getChildren()){
                        Log.d("podatakpopodatak",ps.child("adresa").getValue().toString());
                    }*/
                }
                i++;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
/*
        for (int i=0; i<addresses.size();i++){


            mMap.addMarker(new MarkerOptions().position(new LatLng(addresses.get(i).getLatitude(), addresses.get(i).getLongitude())).title("Marker in Zagreb"));
        }*/
        //mMap.addMarker(new MarkerOptions().position(zagreb).title("Marker in Zagreb"));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else
                            Toast.makeText(getActivity(), "unable to get last location", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
