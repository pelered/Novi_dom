package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zivotinje.Adapter.SliderAdapterExample;
import com.example.zivotinje.Model.Fav;
import com.example.zivotinje.Model.Root;
import com.example.zivotinje.Model.ZivUpload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrikazZiv extends Fragment {

    private String oznaka_ziv;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private SliderView sliderView1;
    private ZivUpload odabrana_ziv;
    private TextView ime,opis,oznaka,kg,starost,status,vrsta,pasmina,spol;
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
            Toast.makeText(getContext(),"Oznaka: "+getArguments().getString("oznaka"),Toast.LENGTH_SHORT).show();
            oznaka_ziv= getArguments().getString("oznaka");
        }
        database=FirebaseDatabase.getInstance();
        mDatabaseRef=database.getReference("Ziv");
        ime=view.findViewById(R.id.ime);
        opis=view.findViewById(R.id.opis);
        oznaka=view.findViewById(R.id.oznaka);
        kg=view.findViewById(R.id.tezina);
        starost=view.findViewById(R.id.starost);
        status=view.findViewById(R.id.status);
        vrsta=view.findViewById(R.id.vrsta);
        pasmina=view.findViewById(R.id.pasmina);
        email=view.findViewById(R.id.email);
        spol=view.findViewById(R.id.spol);
        sliderView1=view.findViewById(R.id.imageSlider);
        favorite=view.findViewById(R.id.favorite);
        favorite.setVisibility(View.INVISIBLE);
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
                Log.d("dadaj:0", dataSnapshot.getValue().toString());
                if (dataSnapshot.hasChild("url")) {
                    for (Map.Entry<String, String> entry : odabrana_ziv.getUrl().entrySet()) {
                        slike.add(entry.getValue());
                    }
                }
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Fav").child(uid);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        fav1=dataSnapshot.getValue(Fav.class);

                        if(fav1!=null) {
                            for(Map.Entry<String, String> entry :fav1.getFav().entrySet()){
                                favo.add(entry.getValue());
                            }
                            if (fav1.getFav().containsValue(odabrana_ziv.getOznaka())) {
                                //Log.d("dadaj:2", String.valueOf(fav1));
                                favorite.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                                oznacen_fav=true;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("onCancelled:fav: ",databaseError.getMessage());
                    }
                });

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
        inicijalizirajSlider();

    }

    private void inicijalizirajSlider() {
        final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}



