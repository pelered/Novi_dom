package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zivotinje.Adapter.MyAdapter;
import com.example.zivotinje.Helper.MyButtonClickListener;
import com.example.zivotinje.Helper.MySwipeHelper;
import com.example.zivotinje.Model.Item;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    String username,email,photo;
    Uri url;
    TextView u,e;
    Button logout,vrati;
    ImageView i;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    RecyclerView recyclerView;
    MyAdapter adapter;
    LinearLayoutManager layoutManager;

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
            Glide.with(this).load(url).fitCenter().centerCrop().into(i);
        }
       //potrebno da se moze odlogirat i s google,da mozes kasnije i druge accounte birati
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Init
        recyclerView=findViewById(R.id.recycler_test);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MySwipeHelper swipeHelper = new MySwipeHelper(this,recyclerView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MySwipeHelper.MyButton> buffer) {
                buffer.add(new MyButton(ProfileActivity.this,"Delete",30,R.drawable.ic_delete_black, Color.parseColor("#FFFFFF"),
                        new MyButtonClickListener(){
                            @Override
                            public void onClick(int pos) {
                                Toast.makeText(ProfileActivity.this,"Delete click",Toast.LENGTH_SHORT).show();
                                Log.d("KLiK: ", String.valueOf(pos));
                            }
                        }));
                buffer.add(new MyButton(ProfileActivity.this,"Ažuriraj",30, R.drawable.ic_mode_edit,Color.parseColor("#FFFFFF"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                Log.d("KLiK2: ", String.valueOf(pos));
                                Toast.makeText(ProfileActivity.this,"Ažuriraj",Toast.LENGTH_SHORT).show();

                            }
                        }));
            }
        };
        generateItem();

    }

    private void generateItem() {
        List<Item> itemList =new ArrayList<>();
        for (int i=0; i<7;i++){

            itemList.add(new Item("Pie 0","100000",photo));
            Log.d("Lista:",itemList.toString());
        }
        adapter= new MyAdapter(this,itemList);
        recyclerView.setAdapter(adapter);
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
            //Log.d("dal izbrise","usao sam");
            editor.commit();
        }else if(view.equals(vrati)){
            Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
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
