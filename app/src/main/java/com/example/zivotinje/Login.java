package com.example.zivotinje;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    private SignInButton googlebtn;

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private  Button log_skl;
    private TextView reg_skl;
    AppCompatCheckBox checkbox;
    private EditText email,lozinka;

    FirebaseDatabase database;
    DatabaseReference myRef ;
    String naziv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        //za google
        googlebtn=(SignInButton) findViewById(R.id.googlebtn);

        googlebtn.setOnClickListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //za google

        //zasebno
        email=findViewById(R.id.email_log);
        lozinka=findViewById(R.id.lozinka_log);
        log_skl=findViewById(R.id.log_skl);
        log_skl.setOnClickListener(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sklonista");

        checkbox =findViewById(R.id.checkbox);

        checkbox.setOnCheckedChangeListener(this);
        //

        //facebook

        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        //loginButton.setReadPermissions("email", "public_profile");
        loginButton.setPermissions("email", "public_profile");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Tag", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        reg_skl=findViewById(R.id.reg_skl);
        reg_skl.setOnClickListener(this);

    }
    //google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        //za google log in
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("tag", "Google sign in failed", e);
                // ...
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI(currentUser);

        }
       /* final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            myRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     UploadSkl skl=dataSnapshot.getValue(UploadSkl.class);
                     naziv=skl.getNaziv();
                     updateUI(currentUser);
                     Log.d("naziv",naziv);

                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             }
            );
            //updateUI(currentUser);

        }*/
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Tag", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Tag", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    String username;
    if(user.getDisplayName()==null){
        username=naziv;
    }else{
        username=user.getDisplayName();
    }
    String email=user.getEmail();
    Uri url=user.getPhotoUrl();
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
    intent.putExtra("url",String.valueOf(url));
    startActivity(intent);
    finish();


    }


    @Override
    public void onClick(View view) {
        if(view.equals(googlebtn)){
            signIn();

        }else if(view.equals(reg_skl)){
            Intent intent=new Intent(Login.this,RegistrationActivity.class);
            startActivity(intent);
            //finish();
        }else if(view.equals(log_skl)){
            if(!(email.getText().toString().trim().equals("")) && !(lozinka.getText().toString().trim().equals(""))){
                log();
            }else{
                Toast.makeText(Login.this, "email ili lozinka prazni.",
                        Toast.LENGTH_SHORT).show();
            }

            //finish();
        }
    }

    //facebook

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Tag", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            // show password
            lozinka.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            // hide password
            lozinka.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
    public void log(){
        mAuth.signInWithEmailAndPassword(email.getText().toString(), lozinka.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("log s emailom i pass", "signInWithEmail:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                      @Override
                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                          UploadSkl skl=dataSnapshot.getValue(UploadSkl.class);
                                          Log.d("evoooo",dataSnapshot.toString());
                                          naziv=skl.getNaziv();
                                          updateUI(user);
                                          Log.d("naziv",naziv);

                                      }

                                      @Override
                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                      }
                                  }
                            );
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("log s emailom i pas", "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(Login.this,Login.class);
                            startActivity(intent);
                            finish();

                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
