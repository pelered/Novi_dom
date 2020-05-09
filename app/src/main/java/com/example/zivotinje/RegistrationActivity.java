package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.example.zivotinje.Adapter.PlaceAutoSuggestAdapter;
import com.example.zivotinje.Model.Root;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private String TAG = "Tag";

    private Button gumb;
    //private AutocompleteSupportFragment autocompleteFragment;
    private TextView skriven,naziv;
    private AppCompatCheckBox checkbox;
    private EditText email,lozinka,potvrda;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private AutoCompleteTextView autoCompleteTextView;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        autoCompleteTextView=findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(RegistrationActivity.this,android.R.layout.simple_list_item_1));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

            }
        });

        //////////////////////////gooogle
        // Initialize Places.
      /*  Places.initialize(getApplicationContext(), getString(R.string.google_api));
        // Create a new Places client instance.
        //PlacesClient placesClient = Places.createClient(this);
         database = FirebaseDatabase.getInstance();


        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME));
        //autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE)
        // Set up a PlaceSelectionListener to handle the response.
        //mice search icon
        ImageView searchIcon = (ImageView) ((LinearLayout) Objects.requireNonNull(autocompleteFragment.getView())).getChildAt(0);
        searchIcon.setVisibility(View.GONE);*/
/*
        skriven=findViewById(R.id.skriven);
        skriven.setVisibility(View.INVISIBLE);
*/
        gumb=findViewById(R.id.button2);
        gumb.setOnClickListener(this);


        //
        email=findViewById(R.id.email);
        lozinka=findViewById(R.id.lozinka);
        potvrda=findViewById(R.id.potvrdi);
        checkbox = findViewById(R.id.checkbox);
        naziv=findViewById(R.id.naziv);

        checkbox.setOnCheckedChangeListener(this);

        mAuth = FirebaseAuth.getInstance();

       /* autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                autocompleteFragment.setText(Objects.requireNonNull(place.getLatLng()).toString());
                skriven.setText(place.getName());
                Log.d(TAG, "Place: " + place.getLatLng() + ", " + place.getName());
            }
            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.d(TAG, "An error occurred: " + status);
            }
        });*/
    }
    @Override
    public void onClick(View view) {
        if(provjeri()){
            //Log.d("Naziv",skriven.getText().toString());
            try{
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), lozinka.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    updateUI(user);
                                    Root skl=new Root(user.getUid(),naziv.getText().toString(),autoCompleteTextView.getText().toString());
                                    //UploadSkl skl=new UploadSkl(user.getUid(),naziv.getText().toString(),skriven.getText().toString());
                                    myRef = database.getReference("Sklonista");
                                    myRef.child(user.getUid()).setValue(skl);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                    Intent intent=new Intent(RegistrationActivity.this,RegistrationActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }
                        });
            }catch (Exception e){
                Log.w(TAG, "Neuspjeh: ", e);
            }
        }
    }
    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(RegistrationActivity.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
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
        Geocoder geocoder=new Geocoder(RegistrationActivity.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
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
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            // show password
            lozinka.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            potvrda.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // hide password
            lozinka.setTransformationMethod(PasswordTransformationMethod.getInstance());
            potvrda.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
    }
    public boolean provjeri(){
        if(!(lozinka.getText().toString().equals(potvrda.getText().toString()))){
            Toast.makeText(RegistrationActivity.this,"Lozinke nisu iste",Toast.LENGTH_LONG).show();
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
        SharedPreferences prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email",email);
        editor.putString("username", username);
        editor.putString("uid",uid);
        editor.putBoolean("hasLogin",true);
        editor.apply();
        //image with glide
        Intent intent=new Intent(this,ProfileActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("email",email);
        intent.putExtra("uid",uid);
        startActivity(intent);
        finish();
    }
}
