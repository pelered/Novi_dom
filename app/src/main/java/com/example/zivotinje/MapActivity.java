package com.example.zivotinje;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.Toast;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static android.app.Activity.RESULT_OK;

public class MapActivity extends Fragment implements Serializable, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    @Nullable
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private View mapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseDatabase database;
    private DatabaseReference myRef ;
    private final float DEFAULT_ZOOM=18;
    private Geocoder mGeocoder;
    private LatLng zagreb;
    private Double lan,lon;
    private List<Address> addresses;
    private String id;
    private String ide;
    private String requiredPermission;
    private ArrayList<Marker> markerr;
    private static final long serialVersionUID = -2163051469151804394L;
    private HashMap<String,String> popis=new HashMap<>();
    private Root upload;
    private ArrayList<Root> lista_sklonista;
    int count=0;
    private IconGenerator iconFactory;
    boolean doubleBackToExitPressedOnce = false;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_activity,container,false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        MultiDex.install(getContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mapView = mapFragment.getView();
        //povezivanje s bazom
        database = FirebaseDatabase.getInstance();
        //refernca na bazu podataka
        myRef = database.getReference("Sklonista");
        mGeocoder=new Geocoder(getActivity(), Locale.getDefault());
        //za spremanje markera
        markerr = new ArrayList<>();
        lista_sklonista=new ArrayList<>();
        iconFactory = new IconGenerator(getContext());

    }
//TODO PROBLEM- brzi dupli klik prblizi, a sporiji dupli otvori skloniste bolje rjesenje?
    public boolean onMarkerClick(Marker marker) {
        if (doubleBackToExitPressedOnce) {
            for(Map.Entry<String, String> entry : popis.entrySet())
            {
                if (marker.getId().equals(entry.getKey())){
                    Log.d("Imamo",entry.getValue());
                    ide=entry.getValue();
                    break;
                }else if(ide==null) {
                    ide = "1";
                }
            }
            // sluzi da se id posalje na sljedeci fragment da zna koje skloniste prikazati
            PrikazSkl fragment=new PrikazSkl();
            Bundle args = new Bundle();
            args.putString("marker", ide);
            fragment.setArguments(args);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack("tag_back1");
            ft.commit();
            return  true;
        } else {
            this.doubleBackToExitPressedOnce = true;
            //Log.d("Delay", String.valueOf(doubleBackToExitPressedOnce));
            marker.showInfoWindow();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 500);
        }
        return true;
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        requiredPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int checkVal = getContext().checkCallingOrSelfPermission(requiredPermission);
        if (checkVal== PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }
        //
        //postavi sredisnu lokaciju na zg
        zagreb = new LatLng( 45.815399, 15.966568);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zagreb,6));
        postavi_markere();
        //mMap.getUiSettings().setZoomGesturesEnabled(false);
        //postavka location gumba
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
    }
// dohvaca podatke iz baze da bi prikazao sklonista na mapi
    private void postavi_markere(){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("Podaciii", dataSnapshot.getValue().toString());
                //Log.d("proba", dataSnapshot.getValue(key));
                for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Log.d("problem",postSnapshot.toString());
                   upload = postSnapshot.getValue(Root.class);
                   lista_sklonista.add(upload);
                    //Log.d("mape***",lista_sklonista.toString());
                    //Log.d("mape****", String.valueOf(lista_sklonista.size()));
                    //provjerit zkj postoji, mozda jer ne bi prikazalo nista radi brzine ucitavanja?
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            odgodi_geoc(lista_sklonista);
                        }
                    }, 0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Neuspjesno dohvacanje podataka",Toast.LENGTH_SHORT).show();

            }
        });
    }
    //dohvaca podatke o sklonistu ako postoji ista pod adresa
    public void odgodi_geoc(ArrayList<Root> lista){
        try {
            if(mGeocoder!=null){
                if(!lista.isEmpty()) {
                    for (int i=0; i<lista.size();i++) {
                        id = lista.get(i).getId();
                        addresses = mGeocoder.getFromLocationName(lista.get(i).getAdresa(), 1);
                        lan = addresses.get(0).getLatitude();
                        lon = addresses.get(0).getLongitude();
                        //Log.d("mape*", lista.toString());
                        //Log.d("mape***",markerr.toString());
                        if(!markerr.isEmpty()){
                            //Log.d("mape**", String.valueOf(popis.containsValue((lista.get(i).getId()))));
                            if (!popis.containsValue((lista.get(i).getId()))){
                                count++;
                                markerr.add(mMap.addMarker(new MarkerOptions().position(new LatLng(lan, lon)).title(lista.get(i).getNaziv())));
                                popis.put(markerr.get(i).getId(), id);
                            }
                        }else{
                            count++;
                            markerr.add(mMap.addMarker(new MarkerOptions().position(new LatLng(lan, lon)).title(lista.get(i).getNaziv())));
                            //markerr.get(i).showInfoWindow();
                            popis.put(markerr.get(i).getId(), id);
                        }
                        //Log.d("mapee*", String.valueOf(count));
                        //Log.d("marker*",markerr.toString());
                       // Log.d("mape****", popis.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }
    //dohvaca podaatke o lokacije ako je ukljucena
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            //ako je poznata zadnja lokacija
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                //dohvacamo trenutnu lokaciju jer nema nijedne prijasnje
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
                            Toast.makeText(getActivity(), "Neuspješan pronalazak lokacije", Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
