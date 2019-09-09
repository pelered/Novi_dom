package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class PrikazSkl extends Fragment implements Serializable {

    SharedPreferences prefs;
    private StorageReference mStorageRef;
    FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private StorageTask uploadTask;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private int uploads = 0;
    private DatabaseReference databaseReference;
    private TextView naziv1,opis1,email1,autocomplete_fragment1;
    ArrayList<Parcelable> path;
    ArrayList<Parcelable> path2=new ArrayList<>();
    SliderView sliderView1;
    private ArrayList<String> slike=new ArrayList<String>();
    private ArrayList<String> slike2=new ArrayList<String>();
    private ArrayList<String> slike_ucitavanje=new ArrayList<String>();
    private HashMap<String,String> slike_map=new HashMap<String,String>();
    private Map<String,String> slike_skinute=new HashMap<String,String>();
    int i=0;
    private ImageView salji;
    String value;
    String naziv,email,adresa,opis;

    String uid;

    private static final long serialVersionUID = -2163051469151804394L;
    private int id;
    private String created;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_prikaz_skl, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        mStorageRef = FirebaseStorage.getInstance().getReference("Sklonista");
        database=FirebaseDatabase.getInstance();
        sliderView1 = view.findViewById(R.id.imageSlider2);
        mDatabaseRef = database.getReference("Sklonista");
        naziv1=view.findViewById(R.id.naziv_skl);
        opis1=view.findViewById(R.id.opis_skl);
        email1=view.findViewById(R.id.email_sk);
        autocomplete_fragment1=view.findViewById(R.id.adresa_skl);
        //Bundle bundle = this.getArguments();
        salji=view.findViewById(R.id.salji);
        salji.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        if (getArguments()==null){
            value=prefs.getString("uid","");
        }else{
            value= getArguments().getString("marker");

        }
        Log.d("frag",value);

        /*
        if (getArguments() != null) {
            value= getArguments().getString("marker");

            naziv.setText(value);
            //int myInt = bundle.getInt(key, defaultValue);
        }
        */

        ///ovo treba izmjeniti privremeno


Log.d("ovdje sam","jesssam");
            ucitaj_podatke();



    }
    protected void sendEmail() {
        Log.i("Send email", "");
       // String[] TO = {""};
       // String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        Log.d("saljem",email1.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email1.getText().toString()});
       // emailIntent.putExtra(Intent.EXTRA_CC, CC);


        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.d("Finished sending emai.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void ucitaj_podatke(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("skida","podatke");

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("pod",postSnapshot.child("naziv").toString());
                    if(postSnapshot.getKey().equals(value)){
                        String broj= String.valueOf(postSnapshot.child("url").getChildrenCount());
                        int br=Integer.parseInt(broj);
                        for(int j=0; j<br;j++){
                            //dovacam url s keyima slika
                            slike2.add(postSnapshot.child("url").child(j+"_key").getValue().toString());
                            //takoder isto ko gore radim
                            slike_ucitavanje.add(postSnapshot.child("url").child(j+"_key").getValue().toString());

                            Log.d("put0",slike_ucitavanje.toString());
                            Log.d("pod slika url2",slike2.toString());
                            Log.d("pod slika url", postSnapshot.child("url").child(j+"_key").getValue().toString());

                        }

                        //Upload up=postSnapshot.child("url").getValue(Upload.class);
                       // String myString=postSnapshot.child("url").getValue().toString();
                       // HashMap<String, Integer> map = convertToHashMap(myString);


                        naziv=postSnapshot.child("naziv").getValue().toString();
                        //treba promijeniti da se email sprema
                        //email1=prefs.getString("email","");
                        email=postSnapshot.child("email").getValue().toString();

                        //opis.setText(postSnapshot.child("opis").getValue().toString());
                        adresa=postSnapshot.child("adresa").getValue().toString();
                        if(postSnapshot.child("adresa").getValue()!=null){
                            if(autocomplete_fragment1!=null){
                                autocomplete_fragment1.setText(postSnapshot.child("adresa").getValue().toString());
                            }
                        }
                        if(postSnapshot.child("opis").getValue()==null){
                            opis1.setText("Opis");

                        }else{
                            naziv1.setText(naziv);
                            email1.setText(email);
                            opis1.setText(postSnapshot.child("opis").getValue().toString());
                        }
                        opis=postSnapshot.child("opis").getValue().toString();

                    }
                }

            }





            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("pod slike2",slike2.toString());
                if(!slike2.isEmpty()){
                    inicijalizirajSlider(slike2);
                }

            }
        }, 1000);

    }

    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    public void inicijalizirajSlider( ArrayList<String> slike2){
        final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
        adapter.setCount(slike2.size());
        adapter.slike2(slike2);
        adapter.broj(slike2.size());
        sliderView1.setSliderAdapter(adapter);
        sliderView1.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView1.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView1.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView1.setIndicatorSelectedColor(Color.WHITE);
        sliderView1.setIndicatorUnselectedColor(Color.GRAY);
        sliderView1.setScrollTimeInSec(15);
        //sliderView.startAutoCycle();

        sliderView1.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView1.setCurrentPagePosition(position);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

    }
}
