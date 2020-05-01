package com.example.zivotinje;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
public class PrikazSkl extends Fragment implements Serializable {
    private static final long serialVersionUID = -2163051469151804394L;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private TextView naziv1,opis1,email1, adresa1;
    private SliderView sliderView1;
    private ArrayList<String> slike2=new ArrayList<>();
    private ImageView salji;
    private String value;
    private Root odabrano_skl;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_prikaz_skl, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        database=FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Sklonista");
        sliderView1 = view.findViewById(R.id.imageSlider2);
        naziv1=view.findViewById(R.id.naziv_skl);
        opis1=view.findViewById(R.id.opis_skl);
        email1=view.findViewById(R.id.email_sk);
        adresa1 =view.findViewById(R.id.adresa_skl);
        //Bundle bundle = this.getArguments();
        salji=view.findViewById(R.id.salji);
        salji.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        if (getArguments()==null){
            Toast.makeText(getContext(),"Nisi smio ovo uspjet,javi mi kako",Toast.LENGTH_SHORT).show();
        }else{
            value= getArguments().getString("marker");
        }
         ucitaj_podatke();
    }
    @SuppressLint("IntentReset")
    private void sendEmail() {
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

    private void ucitaj_podatke(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("skida","podatke");
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Log.d("pod",postSnapshot.child("id").getValue().toString());
                    if(postSnapshot.child("id").getValue().toString().equals(value)){
                        if(postSnapshot.hasChild("url") & postSnapshot.hasChild("opis")){
                            odabrano_skl=postSnapshot.getValue(Root.class);
                        }else if(postSnapshot.hasChild("url")) {
                            odabrano_skl=postSnapshot.getValue(Root.class);
                        }else{
                            odabrano_skl=postSnapshot.getValue(Root.class);
                        }
                        if(postSnapshot.hasChild("url")) {
                            for (Map.Entry<String, String> entry : odabrano_skl.getUrl().entrySet()) {
                                slike2.add(entry.getValue());
                            }
                        }
                        naziv1.setText(odabrano_skl.getNaziv());
                        email1.setText(odabrano_skl.getEmail());
                        opis1.setText(odabrano_skl.getOpis());
                        adresa1.setText(odabrano_skl.getAdresa());
                    }
                }
                inicijalizirajSlider(slike2);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Neuspješno povezivanje s bazom podataka", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    private void inicijalizirajSlider( ArrayList<String> slike2){
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
        sliderView1.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView1.setCurrentPagePosition(position);
            }
        });
    }
    /*
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

    }*/
}
