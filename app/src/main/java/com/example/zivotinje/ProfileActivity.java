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
import com.example.zivotinje.Model.Fav;
import com.example.zivotinje.Model.Item;
import com.example.zivotinje.Model.ZivUpload;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String username,email,photo;
    private String url;
    private TextView u,e;
    private Button logout,vrati;
    private ImageView i;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private LinearLayoutManager layoutManager;
    private String uid;
    private Fav fav1;
    private ArrayList <String> favo=new ArrayList<>();
    ZivUpload ziv= new ZivUpload();
    List<Item> itemList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferences prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        //prefs.getString("uid",null);
        //Log.d("OnCreate:",prefs.toString());
        //Log.d("OnCreate:1",prefs.getString("url",null));
        username=prefs.getString("username",null);
        email=prefs.getString("email",null);
        url=prefs.getString("url",null);
        uid=prefs.getString("uid",null);
        //trebam iz preff dobivat
        /*email=getIntent().getStringExtra("email");
        username=getIntent().getStringExtra("username");
        if(getIntent().getStringExtra("url")!=null){
            photo=getIntent().getStringExtra("url");
            url=Uri.parse(photo);
        }*/

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

        if(url!=null){
            Glide.with(this).load(url).fitCenter().centerCrop().into(i);
        }
       //potrebno da se moze odlogirat i s google,da mozes kasnije i druge accounte birati
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //swiper
        recyclerView=findViewById(R.id.recycler_test);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        MySwipeHelper swipeHelper = new MySwipeHelper(this,recyclerView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MySwipeHelper.MyButton> buffer) {
                buffer.add(new MyButton(ProfileActivity.this,"Delete",30,R.drawable.ic_delete_black, Color.parseColor("#FFFFFF"),
                        pos -> {
                            Toast.makeText(ProfileActivity.this,"Delete click",Toast.LENGTH_SHORT).show();

                            Log.d("KliK: ", String.valueOf(pos));
                        }));
            }
        };
        //ovdje treba firebase staviti
        generateItem();

    }

    private void generateItem() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Fav").child(uid);
        fav1=new Fav();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                fav1=dataSnapshot.getValue(Fav.class);
                if(fav1!=null) {
                    for(Map.Entry<String, String> entry :fav1.getFav().entrySet()){
                        favo.add(entry.getValue());
                        DatabaseReference reff= FirebaseDatabase.getInstance().getReference("Ziv").child(entry.getValue());
                        reff.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ziv=dataSnapshot.getValue(ZivUpload.class);
                                Log.d("generateItem",ziv.toString());
                                itemList.add(new Item(ziv.getNaziv(),ziv.getPasmina(),ziv.getUrl().get("0_key")));
                                Log.d("generateItem1",itemList.toString());
                                Log.d("generateItem2", String.valueOf(itemList.size()));
                                Log.d("generateItem3", String.valueOf(fav1.getFav().size()));
                                if(itemList.size()==fav1.getFav().size()){
                                    adapter= new MyAdapter(getApplicationContext(),itemList);
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("onCancelled:ziv dohvati",databaseError.getMessage());
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled:fav: ",databaseError.getMessage());
            }
        });

    }
    @Override
    public void onClick(View view) {
        if(view.equals(logout)){
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> {
                        Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
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
