package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.zivotinje.Model.Skl;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
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
import java.util.Objects;


public class Login extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private SignInButton googlebtn;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button log_skl;
    private TextView reg_skl,lozinka_reset;
    private AppCompatCheckBox checkbox;
    private EditText email,lozinka;
    private FirebaseDatabase database;
    private DatabaseReference myRef ;
    private String naziv;
    private String profilePicUrl;
    private Skl skl;
    private TextView log,ime_nav,email_nav;
    private ImageView profile_photo;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_login,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        skl=null;
        profilePicUrl=null;
        mAuth = FirebaseAuth.getInstance();
        //za google
        googlebtn=view.findViewById(R.id.googlebtn);
        googlebtn.setOnClickListener(this);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);
        //za google

        //zasebno za skl
        email=view.findViewById(R.id.email_log);
        lozinka=view.findViewById(R.id.lozinka_log);

        log_skl=view.findViewById(R.id.log_skl);
        log_skl.setOnClickListener(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Sklonista");

        checkbox =view.findViewById(R.id.checkbox);
        checkbox.setOnCheckedChangeListener(this);
        lozinka_reset=view.findViewById(R.id.lozinka_reset);
        lozinka_reset.setOnClickListener(this);

        //facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setPermissions("email", "public_profile");
        loginButton.setFragment(this);
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
        reg_skl=view.findViewById(R.id.reg_skl);
        reg_skl.setOnClickListener(this);
    }
    //google
    private void signIn() {
        //Log.d("Probam :", String.valueOf(4));
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Tag", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Tag", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Tag", "signInWithCredential:failure", task.getException());
                        Toast.makeText(getContext(),"Autentikacija neuspjela",Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
    Log.d("Probam3 :", String.valueOf(user));
    if(user!=null) {
        Log.d("updateUI() :", user.toString());
        if(skl!=null){
            Log.d("updateUI()2 :", skl.toString());
            //ako se logiras kao skloniste
            //Log.d("updateUIskl ", skl.toString());
            SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", skl.getEmail());
            editor.putString("username", skl.getNaziv());
            editor.putString("uid", skl.getId());
            editor.putBoolean("hasLogin", true);
            editor.putBoolean("skl",true);
            editor.putString("url", skl.getUrl().get("0_key"));
            editor.apply();

            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            ime_nav = headerView.findViewById(R.id.ime_nav);
            email_nav = headerView.findViewById(R.id.email_nav);
            ime_nav.setText(skl.getNaziv());
            email_nav.setText(skl.getEmail());
            profile_photo=headerView.findViewById(R.id.slika_nav);
            if(skl.getUrl().get("0_key") !=null){
                Glide.with(getActivity()).load(skl.getUrl().get("0_key")).apply(RequestOptions.circleCropTransform()).into(profile_photo);
            }
            log=headerView.findViewById(R.id.log_click);
            log.setText(R.string.log_out);
            Menu menu=navigationView.getMenu();
            MenuItem item ;
            item =menu.findItem(R.id.nav_profil);
            item.setVisible(true);

            //image with glide

            FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new ProfileActivity());
            //ft.addToBackStack("tag_back2");
            ft.commit();

        }else if (FirebaseDatabase.getInstance().getReference("Kor").child(user.getUid()) == null) {
            Log.d("updateUI()3:", user.toString());
            //ako korisnik ne postoji da ga se doda
            String email = user.getEmail();
            String uid = user.getUid();
            String url = null;
            //profilePicUrl="https://graph.facebook.com/"+token.getUserId()+"/picture?type=large";
            if (profilePicUrl == null) {
                if (user.getPhotoUrl() != null) {
                    url = user.getPhotoUrl().toString();
                }
            } else {
                url = profilePicUrl;
            }
            User user1;
            user1 = new User(uid, user.getDisplayName(), url, email);
            Task<Void> mDatabaseRef;
            Map<String, Object> postValues2 = user1.toMap();
            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Kor").child(uid).updateChildren(postValues2);
            String finalUsername = user.getDisplayName();
            String finalUrl = url;
            mDatabaseRef.addOnSuccessListener(aVoid -> {
                SharedPreferences prefs = requireContext().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("email", email);
                editor.putString("username", finalUsername);
                editor.putString("uid", uid);
                editor.putBoolean("hasLogin", true);
                editor.putString("url", user1.getUrl());
                editor.apply();
                NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                ime_nav = headerView.findViewById(R.id.ime_nav);
                email_nav = headerView.findViewById(R.id.email_nav);
                ime_nav.setText(finalUsername);
                email_nav.setText(email);
                profile_photo=headerView.findViewById(R.id.slika_nav);
                log=headerView.findViewById(R.id.log_click);
                log.setText(R.string.log_out);
                Menu menu=navigationView.getMenu();
                MenuItem item;
                item =menu.findItem(R.id.nav_profil);
                item.setVisible(true);
                if(finalUrl !=null){
                    Glide.with(getActivity()).load(finalUrl).apply(RequestOptions.circleCropTransform()).into(profile_photo);
                }

                ProfileActivity fragment=new ProfileActivity();
                FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }).addOnFailureListener(e -> Log.d("Neuspjeli ", "upload"));
        }else if(FirebaseDatabase.getInstance().getReference("Kor").child(user.getUid()) != null ){
            Log.d("updateUI()4:", user.toString());
            FirebaseDatabase.getInstance().getReference("Kor").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user_dohvati=dataSnapshot.getValue(User.class);
                    SharedPreferences prefs = requireContext().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", user_dohvati.getEmail());
                    editor.putString("username", user_dohvati.getIme());
                    editor.putString("uid", user_dohvati.getUid());
                    editor.putBoolean("hasLogin", true);
                    editor.putString("url", user_dohvati.getUrl());
                    editor.apply();
                    NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    ime_nav = headerView.findViewById(R.id.ime_nav);
                    email_nav = headerView.findViewById(R.id.email_nav);
                    ime_nav.setText(user_dohvati.getIme());
                    email_nav.setText(user_dohvati.getEmail());
                    profile_photo=headerView.findViewById(R.id.slika_nav);
                    log=headerView.findViewById(R.id.log_click);
                    log.setText(R.string.log_out);
                    Menu menu=navigationView.getMenu();
                    MenuItem item;
                    item =menu.findItem(R.id.nav_profil);
                    item.setVisible(true);
                    ProfileActivity fragment=new ProfileActivity();
                    FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.commit();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Korisnik nije naden.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }else{
        FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new Login());
        ft.commit();
    }

    }
    @Override
    public void onClick(View view) {
        if(view.equals(googlebtn)){
            signIn();
        }else if(view.equals(reg_skl)){
            Intent intent=new Intent(getActivity(),RegistrationActivity.class);
            startActivity(intent);
            //finish();
        }else if(view.equals(log_skl)){
            if(!(email.getText().toString().trim().equals("")) && !(lozinka.getText().toString().trim().equals(""))){
                log();
            }else{
                Toast.makeText(getContext(), "email ili lozinka prazni.",
                        Toast.LENGTH_SHORT).show();
            }
        }else if(view.equals(lozinka_reset)){
            ResetPasword fragment2 = new ResetPasword();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment2);
            fragmentTransaction.addToBackStack("tag_login");
            fragmentTransaction.commit();
        }
    }
    //facebook
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("Tag", "signInWithCredential:success");
                        //Log.d("Tagfacebook" +                                    "1", token.getUserId().toString());;
                        //Log.d("Tagfacebook" +                                    "2", String.valueOf(task.getResult().getUser().getPhotoUrl()));
                        //Log.d("Tagfacebook" +                                    "3", task.getResult().getAdditionalUserInfo().getProfile().toString());;
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        Log.d("Tag_user", user.toString());
                        try {
                            profilePicUrl="https://graph.facebook.com/"+token.getUserId()+"/picture?type=large";
                            Log.d("Facebook:slika",profilePicUrl);
                        }catch (Exception e)
                        {
                            Log.d("Facebook:error:catch", Objects.requireNonNull(e.getMessage()));
                        }
                        updateUI(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Tag", "signInWithCredential:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
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
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("log s emailom i pass", "signInWithEmail:success");
                        final FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                      //dodala mozda maknem kasnije
                                      skl=dataSnapshot.getValue(Skl.class);
                                      //Log.d("evoooo",dataSnapshot.toString());
                                      assert skl != null;
                                      naziv=skl.getNaziv();
                                      updateUI(user);

                                      //Log.d("naziv",naziv);
                                  }

                                  @Override
                                  public void onCancelled(@NonNull DatabaseError databaseError) {
                                      Toast.makeText(getContext(),"Otkazan log in error: "+databaseError,Toast.LENGTH_SHORT).show();
                                  }
                              }
                        );
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("log s emailom i pas", "signInWithEmail:failure", task.getException());
                        //Toast.makeText(Login.this, "Authentication failed. error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, new Login());
                        //ft.addToBackStack("tag_back2");
                        ft.commit();
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        //Log.d("Probam :", String.valueOf(1));
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            //updateUI(currentUser);
            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new ProfileActivity());
            ft.commit();
        }
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
                Toast.makeText(getContext(),"Nespjesan log in error: "+e,Toast.LENGTH_SHORT).show();
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
