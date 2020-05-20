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
import android.widget.TextView;
import android.widget.Toast;
import com.example.zivotinje.Model.Root;
import com.example.zivotinje.Model.User;
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

import java.util.Map;


public class Login extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SignInButton googlebtn;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button log_skl;
    private TextView reg_skl;
    private AppCompatCheckBox checkbox;
    private EditText email,lozinka;
    private FirebaseDatabase database;
    private DatabaseReference myRef ;
    private String naziv;
    private String profilePicUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        //za google
        googlebtn=findViewById(R.id.googlebtn);
        googlebtn.setOnClickListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //za google

        //zasebno za skl
        email=findViewById(R.id.email_log);
        lozinka=findViewById(R.id.lozinka_log);

        log_skl=findViewById(R.id.log_skl);
        log_skl.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sklonista");

        checkbox =findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(this);

        //facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Tag", "facebook:onSuccess:" + loginResult.toString());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d("Tag", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Tag", "facebook:onError", error);
            }
        });
        reg_skl=findViewById(R.id.reg_skl);
        reg_skl.setOnClickListener(this);
    }
    //google
    private void signIn() {
        //Log.d("Probam :", String.valueOf(4));
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
                //firebaseAuthWithGoogle(account.getIdToken());
                firebaseAuthWithGoogle(account);
                //Toast.makeText(getBaseContext(),"Uspjesan log in",Toast.LENGTH_SHORT).show;
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("tag", "Google sign in failed", e);
                Toast.makeText(this,"Nespjesan log in error: "+e,Toast.LENGTH_SHORT).show();
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        //Log.d("Probam :", String.valueOf(1));
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI(currentUser);
        }
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
                            Toast.makeText(getApplicationContext(),"Autentikacija neuspjela",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
        //Log.d("Probam :", String.valueOf(3));
        User user1;
    String username;
    if(user.getDisplayName()==null){
        username=naziv;
    }else{
        username=user.getDisplayName();
    }
    String url = null;
    //profilePicUrl="https://graph.facebook.com/"+token.getUserId()+"/picture?type=large";
    if(profilePicUrl==null){
        if(user.getPhotoUrl()!=null) {
            url = user.getPhotoUrl().toString();
        }
    }else{
        url=profilePicUrl;
    }
    String email=user.getEmail();
    String uid=user.getUid();

     user1=new User(uid,username,url,email);
     Task<Void> mDatabaseRef;
     Map<String, Object> postValues2=user1.toMap();
     mDatabaseRef=FirebaseDatabase.getInstance().getReference("Kor").child(uid).updateChildren(postValues2);
     mDatabaseRef.addOnSuccessListener(aVoid -> {
         Log.d("Uspjel ", "upload");
         SharedPreferences prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
         SharedPreferences.Editor editor = prefs.edit();
         editor.putString("email",email);
         editor.putString("username", username);
         editor.putString("uid",uid);
         editor.putBoolean("hasLogin",true);
         editor.putString("url",user1.getUrl());
         Log.d("updateUser()1",user1.toString());
         editor.apply();
         //image with glide
         Intent intent=new Intent(this,ProfileActivity.class);
         startActivity(intent);
         finish();

     })
                 .addOnFailureListener(e -> Log.d("Uspjel ", "upload"));


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
        }
    }
    //facebook
    //zbog virusa ne radi
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
                            //Log.d("Tagfacebook" +                                    "1", token.getUserId().toString());;
                            //Log.d("Tagfacebook" +                                    "2", String.valueOf(task.getResult().getUser().getPhotoUrl()));
                            //Log.d("Tagfacebook" +                                    "3", task.getResult().getAdditionalUserInfo().getProfile().toString());;
                            FirebaseUser user = mAuth.getCurrentUser();
                            try {
                                profilePicUrl="https://graph.facebook.com/"+token.getUserId()+"/picture?type=large";
                                Log.d("Facebook:slika",profilePicUrl);
                            }catch (Exception e)
                            {
                                Log.d("Facebook:error:catch",e.getMessage());
                            }
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Tag", "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
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
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("log s emailom i pass", "signInWithEmail:success");
                        final FirebaseUser user = mAuth.getCurrentUser();
                        myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                      //dodala mozda maknem kasnije
                                      Root skl=dataSnapshot.getValue(Root.class);
                                      //Log.d("evoooo",dataSnapshot.toString());
                                      naziv=skl.getNaziv();
                                      updateUI(user);
                                      //Log.d("naziv",naziv);
                                  }
                                  @Override
                                  public void onCancelled(@NonNull DatabaseError databaseError) {
                                      Toast.makeText(getApplicationContext(),"Otkazan log in error: "+databaseError,Toast.LENGTH_SHORT);
                                  }
                              }
                        );
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("log s emailom i pas", "signInWithEmail:failure", task.getException());
                        //Toast.makeText(Login.this, "Authentication failed. error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(Login.this,Login.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }
}
