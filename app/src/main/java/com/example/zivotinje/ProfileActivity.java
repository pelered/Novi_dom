package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String username,email,photo;
    Uri url;

    TextView u,e;
    Button logout,vrati;
    ImageView i;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        email=getIntent().getStringExtra("email");
        username=getIntent().getStringExtra("username");
        if(getIntent().getStringExtra("url")!=null){
            photo=getIntent().getStringExtra("url");
            url=Uri.parse(photo);
        }


        u=findViewById(R.id.username);
        e=findViewById(R.id.email);
        logout=(Button) findViewById(R.id.logout);
        vrati=(Button) findViewById(R.id.vrati);
        i=findViewById(R.id.photo);
        mAuth=FirebaseAuth.getInstance();

        logout.setOnClickListener(this);
        vrati.setOnClickListener(this);
        u.setText(username);
        e.setText(email);

        if(getIntent().getStringExtra("url")!=null){
            Picasso.get().load(url).into(i);

        }

       //potrebno da se moze odlogirat i s google,da mozes kasnije i druge accounte birati
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onClick(View view) {
        if(view.equals(logout)){
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            LoginManager.getInstance().logOut();
            SharedPreferences prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            Log.d("dal izbrise","usao sam");
            editor.commit();
        }else if(view.equals(vrati)){
            Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Makni z+izbaze mobitela

    }


    public void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user == null){
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
        }
    }


}
