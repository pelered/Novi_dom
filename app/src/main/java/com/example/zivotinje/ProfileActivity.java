package com.example.zivotinje;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.zivotinje.Adapter.ProfileMyAdapter;
import com.example.zivotinje.Helper.MySwipeHelper;
import com.example.zivotinje.Model.Fav;
import com.example.zivotinje.Model.Item;
import com.example.zivotinje.Model.ZivUpload;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends Fragment implements View.OnClickListener {

    private String username,email,photo;
    private String url;
    private TextView u,e;
    private Button logout,vrati;
    private ImageView i;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private ProfileMyAdapter adapter;
    private LinearLayoutManager layoutManager;
    private String uid;
    private Fav fav1;
    private ArrayList <String> favo=new ArrayList<>();
    private ZivUpload ziv= new ZivUpload();
    private List<Item> itemList =new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        //prefs.getString("uid",null);
        //Log.d("OnCreate:",prefs.toString());
        //Log.d("OnCreate:1",prefs.getString("url",null));
        username=prefs.getString("username",null);
        email=prefs.getString("email",null);
        url=prefs.getString("url",null);
        uid=prefs.getString("uid",null);

        u=view.findViewById(R.id.username);
        e=view.findViewById(R.id.email);
        logout=view.findViewById(R.id.logout);
        vrati=view.findViewById(R.id.vrati);
        i=view.findViewById(R.id.photo);
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
        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);


        //swiper
        recyclerView=view.findViewById(R.id.recycler_test);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        MySwipeHelper swipeHelper = new MySwipeHelper(getContext(),recyclerView,200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MySwipeHelper.MyButton> buffer) {
                buffer.add(new MyButton(Objects.requireNonNull(getContext()),"Delete",30,R.drawable.ic_delete_black, Color.parseColor("#FFFFFF"),
                        pos -> {
                            String key= null;
                            //Log.d("delete:",fav1.toString());
                            for(Map.Entry<String, String> entry :fav1.getFav().entrySet()){
                                //Log.d("delete1:",entry.getValue());
                                if(adapter.getItem(pos).getOznaka().equals(entry.getValue())){
                                    //Log.d("delete2:",entry.getKey());
                                    key = entry.getKey();
                                    break; //breaking because its one to one map
                                }
                            }
                            Toast.makeText(getContext(),"Delete click",Toast.LENGTH_SHORT).show();
                            DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Fav");
                            //Log.d("delete3:",ref.toString());
                            String finalKey = key;
                            ref.child(uid).child("fav").child(key).removeValue((databaseError, databaseReference) -> {
                                adapter.removeItem(pos);
                                fav1.getFav().remove(finalKey);
                            });
                            //Log.d("KliK: ", adapter.getItem(pos).getOznaka().toString());
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
                                ziv = dataSnapshot.getValue(ZivUpload.class);
                                if (ziv != null) {
                                    //Log.d("generateItem", ziv.toString());
                                    itemList.add(new Item(ziv.getNaziv(), ziv.getPasmina(), ziv.getUrl().get("0_key"),ziv.getOznaka()));
                                    //Log.d("generateItem1", itemList.toString());
                                    //Log.d("generateItem2", String.valueOf(itemList.size()));
                                    //Log.d("generateItem3", String.valueOf(fav1.getFav().size()));
                                    if (itemList.size() == fav1.getFav().size()) {
                                        adapter = new ProfileMyAdapter(getContext(), itemList);
                                        recyclerView.setAdapter(adapter);
                                    }
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
            mGoogleSignInClient.signOut().addOnCompleteListener(Objects.requireNonNull(getActivity()),
                    task -> {
                        MapActivity fragment=new MapActivity();
                        /*Bundle args = new Bundle();
                        args.putString("oznaka", uploadCurrent.getId());
                        fragment.setArguments(args);
                        //FragmentTransaction ft=*/
                        FragmentTransaction ft =(getActivity()).getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, fragment);
                        //ft.addToBackStack("tag_back1_profile");
                        ft.commit();


                    });
            LoginManager.getInstance().logOut();
            SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            //Log.d("dal izbrise","usao sam");
            editor.commit();
        }else if(view.equals(vrati)){
            //todo
            MapActivity fragment=new MapActivity();
            FragmentTransaction ft =(Objects.requireNonNull(getActivity())).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            //ft.addToBackStack("tag_back1_profile");
            ft.commit();

        }
    }

    public void onStart() {
        super.onStart();
        FirebaseUser user=mAuth.getCurrentUser();
        if(user == null){
            ProfileActivity fragment=new ProfileActivity();
            FragmentTransaction ft =(Objects.requireNonNull(getActivity())).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            //ft.addToBackStack("tag_back1_profile");
            ft.commit();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
