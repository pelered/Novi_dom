package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.zivotinje.Adapter.SliderAdapter;
import com.example.zivotinje.Model.Fav;
import com.example.zivotinje.Model.ZivUpload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrikazZiv extends Fragment {

    private String oznaka_ziv;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private SliderView sliderView1;
    private ZivUpload odabrana_ziv;
    private TextView ime,opis,oznaka,kg,starost,status,vrsta,pasmina,spol,created,last_updated;
    private ImageView email;
    private ArrayList<String> slike= new ArrayList<>();
    private ImageView favorite;
    private boolean oznacen_fav=false;
    private String uid;
    ArrayList<String> favo;
    Fav fav1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_prikaz_ziv, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments()==null){
            Toast.makeText(getContext(),"Nisi smio ovo uspjet,javi mi kako",Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(getContext(),"Oznaka: "+getArguments().getString("oznaka"),Toast.LENGTH_SHORT).show();
            oznaka_ziv= getArguments().getString("oznaka");
        }
        database=FirebaseDatabase.getInstance();
        mDatabaseRef=database.getReference("Ziv");
        ime=view.findViewById(R.id.ime_nav);
        opis=view.findViewById(R.id.opis);
        oznaka=view.findViewById(R.id.oznaka);
        kg=view.findViewById(R.id.tezina);
        starost=view.findViewById(R.id.starost);
        status=view.findViewById(R.id.status);
        vrsta=view.findViewById(R.id.vrsta);
        pasmina=view.findViewById(R.id.pasmina);
        email=view.findViewById(R.id.email_nav);
        spol=view.findViewById(R.id.spol);
        sliderView1=view.findViewById(R.id.imageSlider);
        favorite=view.findViewById(R.id.favorite);
        favorite.setVisibility(View.INVISIBLE);
        last_updated=view.findViewById(R.id.last_updated);
        created=view.findViewById(R.id.created);
        favo=new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        if(prefs.getString("uid",null)!=null) {
            uid=prefs.getString("uid",null);
            favorite.setVisibility(View.VISIBLE);
            favorite.setOnClickListener(v -> {
                dodaj_favorita();
            });
        }
        ucitaj_podatke();
    }

    private void dodaj_favorita() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Fav").child(uid);
        if(!oznacen_fav) {
            //oznacimo da nam se svida, oznaka je na true;
            if(fav1==null){
                favo=new ArrayList<>();
                favo.add(odabrana_ziv.getOznaka());
                fav1=new Fav();
            }else {
                favo.add(odabrana_ziv.getOznaka());
                fav1.getFav().clear();
            }

            //Log.d("dodaj_fav0:",favo.toString());
            if(fav1!=null) {
                for (int i = 0; i < favo.size(); i++) {
                    fav1.getFav().put(i + "_k", favo.get(i));
                }
            }else{
                HashMap<String,String> pr=new HashMap<>();
                pr.put((0 + "_k"),favo.get(0));
                fav1.setFav(pr);
            }
            //Log.d("dodaj_fav:",fav1.toString());
            Fav fav_up = new Fav(fav1.getFav());
            //Log.d("dodaj_fav:1",fav_up.toString());
            Map<String, Object> postValues2=fav_up.toMap();
            //Log.d("dodaj_fav:2",postValues2.toString());
            ref.updateChildren(postValues2).addOnSuccessListener(aVoid -> {
                favorite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                oznacen_fav=true;
            }).addOnFailureListener(e -> {
                fav1.getFav().remove(fav1.getFav().size()+"_k",odabrana_ziv.getOznaka());
                favo.remove(odabrana_ziv.getOznaka());
                Toast.makeText(getActivity(),"Neuspjelo dodavanje",Toast.LENGTH_SHORT);
            });

        }else{
            //oznacimo da nam se ne sviđa,oznaka false
            String key= null;
            //String value="somename";
            for(Map.Entry<String, String> entry :fav1.getFav().entrySet()){
                if(odabrana_ziv.getOznaka().equals(entry.getValue())){
                    key = entry.getKey();
                    break; //breaking because its one to one map
                }
            }
            //Log.d("dodaj_fav5.5", String.valueOf(ref.child("fav").child(key)));
            ref.child("fav").child(key).removeValue((databaseError, databaseReference) -> {
                favo.remove(odabrana_ziv.getOznaka());
                for (int i=0;i<favo.size();i++){
                    fav1.getFav().put(i+"_k",favo.get(i));
                }
               // Log.d("dodaj_fav:6",fav1.toString());
                favorite.setBackgroundResource(R.drawable.ic_favorite_border_yellow);
                oznacen_fav=false;
            });

        }
    }

    private void ucitaj_podatke() {
        mDatabaseRef.child(oznaka_ziv).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                odabrana_ziv = dataSnapshot.getValue(ZivUpload.class);
                //todo dodat da ako ne nade ziv da odvede na home page
                //Log.d("dadaj:0", dataSnapshot.getValue().toString());
                if (dataSnapshot.hasChild("url")) {
                    for (Map.Entry<String, String> entry : odabrana_ziv.getUrl().entrySet()) {
                        slike.add(entry.getValue());
                    }
                }
                if (uid != null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Fav").child(uid);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            fav1 = dataSnapshot.getValue(Fav.class);
                            if (fav1 != null) {
                                for (Map.Entry<String, String> entry : fav1.getFav().entrySet()) {
                                    favo.add(entry.getValue());
                                }
                                if (fav1.getFav().containsValue(odabrana_ziv.getOznaka())) {
                                    //Log.d("dadaj:2", String.valueOf(fav1));
                                    favorite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                                    oznacen_fav = true;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("onCancelled:fav: ", databaseError.getMessage());
                        }
                    });
                }
                postavi_podatke();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("onCancelled:ziv: ",databaseError.getMessage());
                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void postavi_podatke() {
        ime.setText(odabrana_ziv.getNaziv());
        oznaka.setText(odabrana_ziv.getOznaka());
        pasmina.setText(odabrana_ziv.getPasmina());
        kg.setText(odabrana_ziv.getTezina().doubleValue()+" kg");
        status.setText(odabrana_ziv.getStatus());
        spol.setText(odabrana_ziv.getSpol());
        vrsta.setText(odabrana_ziv.getVrsta());
        opis.setText(odabrana_ziv.getOpis());
        starost.setText(odabrana_ziv.getGodine().toString()+" god");
        created.setText("Dodan: " +odabrana_ziv.getDate());
        last_updated.setText("Ažuriran: " +odabrana_ziv.getLast_date());
        inicijalizirajSlider();
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmail();
            }
        });

    }

    private void getEmail() {
        //String email1;
        mDatabaseRef=database.getReference("Sklonista");
        mDatabaseRef.child(odabrana_ziv.getId_skl()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Toast.makeText(getActivity(), dataSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
                sendEmail(dataSnapshot.getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Greska s dohvacanjem emaila.", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @SuppressLint("IntentReset")
    private void sendEmail(String e) {
        Log.i("Send email", "");
        // String[] TO = {""};
        // String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        Log.d("saljem",e);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{e});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Upit vezan za ljubimca s oznakom "+odabrana_ziv.getOznaka()+" u skloništu.");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            Log.d("Finished sending emai.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void inicijalizirajSlider() {
        final SliderAdapter adapter= new SliderAdapter(getActivity());
        adapter.setCount(slike.size());
        adapter.slike2(slike);
        sliderView1.setSliderAdapter(adapter);
        sliderView1.setIndicatorAnimation(IndicatorAnimations.SLIDE);
        sliderView1.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView1.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView1.setIndicatorSelectedColor(Color.WHITE);
        sliderView1.setIndicatorUnselectedColor(Color.GRAY);
        sliderView1.setScrollTimeInSec(15);
        sliderView1.setOnIndicatorClickListener(position -> sliderView1.setCurrentPagePosition(position));
    }


}



