package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private String TAG = "placeautocomplete";

    private Button gumb;
    private AutocompleteSupportFragment autocompleteFragment;
    private TextView skriven,naziv;
    private AppCompatCheckBox checkbox;
    private EditText email,lozinka,potvrda;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//////////////////////////gooogle
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyAyVddfVCAcVub30s1xsJLiaRCMx70EbtA");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
         database = FirebaseDatabase.getInstance();


        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME));
        //autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE)
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setHint("Adresa");

        //mice search icon
        ImageView searchIcon = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);
        searchIcon.setVisibility(View.GONE);

        skriven=findViewById(R.id.skriven);
        skriven.setVisibility(View.INVISIBLE);

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

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                autocompleteFragment.setText(place.getLatLng().toString());
                skriven.setText(place.getName());
                //Log.i(TAG, "Place: " + place.getLatLng() + ", " + place.getName());
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
    @Override
    public void onClick(View view) {
        if(provjeri()){
            Log.d("Naziv",skriven.getText().toString());
            try{
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), lozinka.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user);
                                    UploadSkl skl=new UploadSkl(user.getUid(),naziv.getText().toString(),skriven.getText().toString());
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
                                // ...
                            }
                        });
            }catch (Exception e){

            }
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
