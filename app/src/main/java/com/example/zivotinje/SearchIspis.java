package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zivotinje.Model.ZivUpload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchIspis extends Fragment {
    private String skl,oznaka,pasmina,spol,status,vrsta;
    private Float tezina,starost;
private Query query;
    private ZivUpload dohvaceno;
        private DatabaseReference ref;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search_ispis,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ref= FirebaseDatabase.getInstance().getReference("Ziv");
        if(getArguments()!=null) {
            skl = getArguments().getString("skl");
            oznaka = getArguments().getString("oznaka");
            pasmina = getArguments().getString("pasmina");
            tezina = getArguments().getFloat("tezina");
            starost = getArguments().getFloat("starost");
            spol = getArguments().getString("spol");
            status = getArguments().getString("status");
            vrsta = getArguments().getString("vrsta");
        }else{
            query=ref.orderByChild("last_date");
        }
        if(skl.equals("") && oznaka.equals("") && pasmina.equals("") && spol.equals("") && status.equals("") && vrsta.equals("")){
            query=ref.orderByChild("last_date");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("TAG*",dataSnapshot.toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(!oznaka.equals("")){
            query=ref.orderByChild("oznaka").startAt(oznaka).endAt(oznaka+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("TAG*",dataSnapshot.toString());
                    dohvaceno=dataSnapshot.getValue(ZivUpload.class);
                    
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(!skl.equals("")){
            query=ref.orderByChild("naz_skl").startAt(skl).endAt(skl+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Log.d("TAG*",dataSnapshot.toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(!pasmina.equals("")){
            query=ref.orderByChild("pasmina").startAt(pasmina).endAt(pasmina+"\uf8ff");
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("TAG*",dataSnapshot.toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(!spol.equals("")){
            query=ref.orderByChild("spol").equalTo(spol);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("TAG*",dataSnapshot.toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if(!vrsta.equals("")){
            query=ref.orderByChild("vrsta").equalTo(vrsta);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("TAG*",dataSnapshot.toString());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }



    }
    public void dohvati(){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TAG*",dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

