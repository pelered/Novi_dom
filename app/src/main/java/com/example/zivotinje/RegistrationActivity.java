package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.zivotinje.Adapter.PlaceAutoSuggestAdapter;
import com.example.zivotinje.Model.Skl;
import com.google.android.gms.maps.model.LatLng;


import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class RegistrationActivity extends Fragment implements View.OnClickListener {
    private String TAG = "Tag";

    private Button gumb;
    private TextView naziv;
    private EditText email,lozinka,potvrda,broj;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private AutoCompleteTextView autoCompleteTextView;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_registration, container, false);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v=view;
        autoCompleteTextView=view.findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getActivity(),android.R.layout.simple_list_item_1));
        autoCompleteTextView.setOnItemClickListener((parent, vieww, position, id) -> {
            Log.d("Address : ",autoCompleteTextView.getText().toString());
            LatLng latLng=getLatLngFromAddress(autoCompleteTextView.getText().toString());
            if(latLng!=null) {
                Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                Address address=getAddressFromLatLng(latLng);
                if(address!=null) {
                    Log.d("Address : ", "" + address.toString());
                    Log.d("Address Line : ",""+address.getAddressLine(0));
                    Log.d("Phone : ",""+address.getPhone());
                    Log.d("Pin Code : ",""+address.getPostalCode());
                    Log.d("Feature : ",""+address.getFeatureName());
                    Log.d("More : ",""+address.getLocality());
                }
                else {
                    Log.d("Adddress","Address Not Found");
                }
            }
            else {
                Log.d("Lat Lng","Lat Lng Not Found");
            }

        });


        gumb=view.findViewById(R.id.button2);
        gumb.setOnClickListener(this);


        //
        email=view.findViewById(R.id.email_nav);
        lozinka=view.findViewById(R.id.lozinka);
        potvrda=view.findViewById(R.id.potvrdi);
        naziv=view.findViewById(R.id.naziv);
        broj=view.findViewById(R.id.broj_tel);


        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public void onClick(View view) {
        if(provjeri() && prazno()){
            //Log.d("Naziv",skriven.getText().toString());
            try{
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), lozinka.getText().toString())
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                assert user != null;
                                updateUI(user);
                                Skl skl=new Skl(user.getUid(),naziv.getText().toString(),email.getText().toString(),autoCompleteTextView.getText().toString(), broj.getText().toString());
                                database= FirebaseDatabase.getInstance();
                                myRef = database.getReference("Sklonista");
                                myRef.child(user.getUid()).setValue(skl);


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_container, new RegistrationActivity());
                                ft.addToBackStack("tag_reg");
                                ft.commit();

                            }
                        });
            }catch (Exception e){
                Log.w(TAG, "Neuspjeh: ", e);
            }
        }
    }
    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(getActivity());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                return new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(getActivity());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                return addresses.get(0);
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private boolean prazno(){
        if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(broj.getText().toString()) || TextUtils.isEmpty(autoCompleteTextView.getText().toString()) || TextUtils.isEmpty(lozinka.getText().toString()) || TextUtils.isEmpty(naziv.getText().toString())){
            Toast.makeText(getActivity(),"Sva polja moraju biti popunjena",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public boolean provjeri(){
        if(!(lozinka.getText().toString().trim().equals(potvrda.getText().toString().trim()))){
            Toast.makeText(getActivity(),"Lozinke nisu iste",Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }
    private void updateUI(FirebaseUser user) {
        String username;
        if(user.getDisplayName()==null ){
            username=naziv.getText().toString();
        }else{
            username=user.getDisplayName();
        }
        String email=user.getEmail();
        String uid=user.getUid();
        SharedPreferences prefs = requireContext().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email",email);
        editor.putString("username", username);
        editor.putString("uid",uid);
        editor.putString("add",autoCompleteTextView.getText().toString());
        editor.putString("broj",broj.getText().toString());
        editor.putBoolean("hasLogin",true);
        editor.putBoolean("skl",true);
        NavigationView navigationView = v.findViewById(R.id.nav_view);

        Menu menu=navigationView.getMenu();
        MenuItem item ;
        item =menu.findItem(R.id.nav_dodaj_ziv);
        item.setVisible(true);

        editor.apply();
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new ProfileActivity());
        ft.addToBackStack("tag_reg");
        ft.commit();
        //image with glide

    }
}
