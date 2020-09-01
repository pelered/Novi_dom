package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zivotinje.Model.ZivUpload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchIspis extends Fragment {
    private String naz_skl,oznaka,pasmina,spol, vrsta, status;
    private Float tezina,starost;
    private RecyclerView mRecyclerView;
    private IspisZiv mAdapter;
    private List<ZivUpload> mUploads;
    private ZivUpload dohvati;
private Query query;
    private ZivUpload dohvaceno;
        private DatabaseReference ref;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search_ispis,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mUploads = new ArrayList<>();

        ref= FirebaseDatabase.getInstance().getReference("Ziv");
        if(getArguments()!=null) {
            naz_skl = getArguments().getString("skl");
            oznaka = getArguments().getString("oznaka");
            pasmina = getArguments().getString("pasmina");
            tezina = getArguments().getFloat("tezina");
            starost = getArguments().getFloat("starost");
            spol = getArguments().getString("spol");
            vrsta = getArguments().getString("vrsta");
            status = getArguments().getString("status");
        }else{
            query=ref.orderByChild("last_date");
        }
        /*
        if(naz_skl.equals("") && oznaka.equals("") && pasmina.equals("") && spol.equals("") && status.equals("") && vrsta.equals("")){
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
        }else if(!naz_skl.equals("")){
            query=ref.orderByChild("naz_skl").startAt(naz_skl).endAt(naz_skl+"\uf8ff");
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

*/

    }

    @SuppressLint("RestrictedApi")
    private void odaberi_query() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ziv");
        Query query = null;

        dohvati(query);
    }
    public void dohvati(Query query){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Log.d("Dohvati_p1:",postSnapshot.toString());
                    if (naz_skl != null) {
                        Log.d("Dohvati_p2:",postSnapshot.toString());
                        //trazi se odredeno skl
                        if (vrsta !=null) {
                            Log.d("Dohvati_p3:",postSnapshot.child("vrsta").toString());
                            //trazi se odreden vrsta
                            if(postSnapshot.child("vrsta").getValue().equals(vrsta)){
                                Log.d("Dohvati_p4:",postSnapshot.toString());
                                //naden odreden vrsta ziv
                                if(spol !=null) {
                                    Log.d("Dohvati_p5:", postSnapshot.toString());
                                    //trazi se odredena spol
                                    if (postSnapshot.child("spol").getValue().equals(spol)) {
                                        Log.d("Dohvati_p6:", postSnapshot.toString());
                                        //nadena odredena spol
                                        if (starost != null) {
                                            Log.d("Dohvati_p7:", postSnapshot.toString());
                                            //razi se odredeno starost
                                            if(postSnapshot.child("starost").getValue().equals(starost)){

                                            if (tezina != null) {
                                                //todo starost ne moze equal biti mora equals ili manje
                                                Log.d("Dohvati_p8:", postSnapshot.toString());
                                                //trazi se tezina
                                                if (postSnapshot.child("tezina").getValue().equals(tezina)) {
                                                    //nadena odredena tezina
                                                    if (pasmina != null) {
                                                        //trazi se pasmina
                                                        if (postSnapshot.child("pasmina").getValue().equals(pasmina)) {
                                                            //nadena odredena pasmina
                                                            if (status != null) {
                                                                //trazi se status
                                                                if (postSnapshot.child("status").getValue().equals(status)) {
                                                                    //nadena odredena status
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                } else {
                                                                    //ne postiji ziv po trazenim zahtjevima naz_skl+vrtsa+spol+starost+tezina+pasmina+status
                                                                }
                                                            } else {
                                                                //nije dan status,naz_skl+vrtsa+spol+starost+tezina+pasmina
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        } else {
                                                            //ne postoji trazena pasmina,naz_skl+vrtsa+spol+starost+tezina+pasmina

                                                        }
                                                    } else {
                                                        //nije zadana pasmina,naz_skl+vrtsa+spol+starost+tezina
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                } else {
                                                    //nije nadena tezina,naz_skl+vrtsa+spol+starost+tezina
                                                }
                                            } else {
                                                Log.d("Dohvati_p9:", postSnapshot.toString());
                                                //nije zadana tezina
                                                //Toast.makeText(getActivity(),"Nije nadeno po parametrima:"+naz_skl+vrsta+spol+starost,Toast.LENGTH_SHORT);
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        } else {
                                            //nije nadena starost,naz_skl+vrtsa+spol+starost
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                        }
                                    } else {
                                            Log.d("Dohvati_p9:", postSnapshot.toString());
                                            //nije odredeno starost
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                    }
                                }else{
                                        //nije nadena spol,+naz_skl+vrsta+spol
                                    }

                                }else{
                                    //nije odredena spol
                                    if(starost!=null){
                                        //trazi se odredeno starost
                                        if(postSnapshot.child("starost").getValue().equals(starost)){
                                            //nadeno odredeno starost
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);

                                        }else{
                                            Log.d("Dohvati_p9:",postSnapshot.toString());
                                            // Toast.makeText(getActivity(),"Nije nadeno po parametrima:"+grad+status+stanje,Toast.LENGTH_LONG);
                                        }
                                    } else{
                                        //nije drendeno starost
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;

                                        mUploads.add(dohvati);
                                    }
                                }
                                //ako status nije naden a postavljen je nema nijedne zivotinje i nista se ne prikaze
                            }else {
                                Log.d("Dohvati_p9:",postSnapshot.toString());
                                //Toast.makeText(getActivity(),"Nije nadeno po parametrima:"+naz_skl+vrsta,Toast.LENGTH_LONG);
                            }
                            //nije odreden status
                        }
                        else if (spol !=null) {
                            //trazi se odredena vrsta
                            if(postSnapshot.child("spol").getValue().equals(spol)){
                                //nadena je odredena vrsta
                                if(starost!=null){
                                    //trazi se odredeno stanje
                                    if(postSnapshot.child("stanje").getValue().equals(starost)){
                                        //nadeno odredeno stanje
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;

                                        mUploads.add(dohvati);
                                    }else{
                                        Log.d("Dohvati_p9:",postSnapshot.toString());
                                        //nije nadena ziv po zadanim zahtjevima
                                        //Toast.makeText(getActivity(),"Nije nadeno po parametrima:"+ grad +vrsta +stanje,Toast.LENGTH_LONG);
                                    }
                                }else{
                                    //sva stanja trazim ziv
                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                    assert dohvati != null;

                                    mUploads.add(dohvati);
                                }
                            }else{
                                //nije nadeda odredena vrsta
                                Log.d("Dohvati_p9:",postSnapshot.toString());
                                //Toast.makeText(getActivity(),"Nije nadeno po parametrima:"+grad+vrsta,Toast.LENGTH_LONG);
                            }
                        } else if (starost!=null) {
                            if(postSnapshot.child("stanje").getValue().equals(starost)){
                                //nadeno odredeno stanje
                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                assert dohvati != null;

                                mUploads.add(dohvati);
                            }else{
                                //nije nadena ziv po zadanim zahtjevima
                                Log.d("Dohvati_p9:",postSnapshot.toString());
                                //Toast.makeText(getActivity(),"Nije nadeno po parametrima:"+ grad +stanje,Toast.LENGTH_LONG);
                            }
                        }else{
                            //provjeri tezina,pasmina,status
                            Log.d("Dohvati_pgrad:",postSnapshot.toString());
                            dohvati = postSnapshot.getValue(ZivUpload.class);
                            assert dohvati != null;

                            mUploads.add(dohvati);
                        }
                    }
                    else if (vrsta != null) {
                        if(postSnapshot.child("vrsta").getValue().equals(vrsta)){
                            Log.d("Dohvati_p3:",postSnapshot.child("status").toString());
                            //trazi se odredena vrsta
                            if (spol != null) {
                                //trazi se odreden status
                                if(postSnapshot.child("spol").getValue().equals(spol)){
                                    Log.d("Dohvati_p4:",postSnapshot.toString());
                                    //naden odreden spol ziv
                                    if (starost != null) {
                                        Log.d("Dohvati_p5:",postSnapshot.toString());
                                        //trazi se odredena vrsta
                                        if(postSnapshot.child("starost").getValue().equals(starost)){
                                            Log.d("Dohvati_p6:",postSnapshot.toString());
                                            //nadena odredena vrsta ziv
                                            if(tezina!=null){
                                                if(postSnapshot.child("tezina").getValue().equals(tezina)){
                                                    if(pasmina!=null){
                                                        if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }
                                                }else{
                                                    //nije nadena ziv po zadanim zahtjevima
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                if(pasmina!=null){
                                                    if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }
                                            }
                                        }else{
                                            Log.d("Dohvati_p9:",postSnapshot.toString());
                                        }
                                    }else{
                                        //nije zadana starost ostale provjeriti
                                        if(tezina!=null){
                                            if(postSnapshot.child("tezina").getValue().equals(tezina)){
                                                if(pasmina!=null){
                                                    if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }
                                            }else{
                                                //nije nadena ziv po zadanim zahtjevima
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            if(pasmina!=null){
                                                if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }
                                        }
                                    }

                                }else {
                                    //ne postoji ziv s trazenim spol nema nista
                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                }
                            } else{//provjeri starost pa nadalje
                                if (starost != null) {
                                    Log.d("Dohvati_p5:",postSnapshot.toString());
                                    //trazi se odredena vrsta
                                    if(postSnapshot.child("starost").getValue().equals(starost)){
                                        Log.d("Dohvati_p6:",postSnapshot.toString());
                                        //nadena odredena vrsta ziv
                                        if(tezina!=null){
                                            if(postSnapshot.child("tezina").getValue().equals(tezina)){
                                                if(pasmina!=null){
                                                    if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }
                                            }else{
                                                //nije nadena ziv po zadanim zahtjevima
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            if(pasmina!=null){
                                                if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }
                                        }
                                    }else{
                                        Log.d("Dohvati_p9:",postSnapshot.toString());
                                    }
                                }else{
                                    //nije zadana starost ostale provjeriti
                                    if(tezina!=null){
                                        if(postSnapshot.child("tezina").getValue().equals(tezina)){
                                            if(pasmina!=null){
                                                if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }
                                        }else{
                                            //nije nadena ziv po zadanim zahtjevima
                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        if(pasmina!=null){
                                            if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }else{//ne postoji ziv s odabranom pasminom
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            if(status!=null){
                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }
                                    }
                                }
                            }

                        }else{
                            //ne postoji ziv s zadanom vrstom
                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (spol != null) {
                        //trazi se odreden status
                        if(Objects.equals(postSnapshot.child("spol").getValue(), spol)){
                            Log.d("Dohvati_p4:",postSnapshot.toString());
                            //naden odreden spol ziv
                            if (starost != null) {
                                Log.d("Dohvati_p5:",postSnapshot.toString());
                                //trazi se odredena vrsta
                                if(postSnapshot.child("starost").getValue().equals(starost)){
                                    Log.d("Dohvati_p6:",postSnapshot.toString());
                                    //nadena odredena vrsta ziv
                                    if(tezina!=null){
                                        if(postSnapshot.child("tezina").getValue().equals(tezina)){
                                            if(pasmina!=null){
                                                if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                                    if(status!=null){
                                                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }
                                        }else{
                                            Log.d("Dohvati_p9:",postSnapshot.toString());
                                            //nije nadena ziv po zadanim zahtjevima
                                        }
                                    }else {
                                        if(pasmina!=null){
                                            if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                                if(status!=null){
                                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }else{//ne postoji ziv s odabranom pasminom
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }
                                    }
                                }else{
                                    Log.d("Dohvati_p9:",postSnapshot.toString());
                                }
                            }else{
                                //nije zadana starost ostale provjeriti
                                if(tezina!=null){
                                    if(postSnapshot.child("tezina").getValue().equals(tezina)){
                                        if(pasmina!=null){
                                            if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                                if(status!=null){
                                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }else{//ne postoji ziv s odabranom pasminom
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }
                                    }else{
                                        //nije nadena ziv po zadanim zahtjevima
                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    if(pasmina!=null){
                                        if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }else{//ne postoji ziv s odabranom pasminom
                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        if(status!=null){
                                            if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }else{
                                                //ne postoji ziv s odabranim pasmina+status
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }
                                    }
                                }
                            }

                        }else {
                            //ne postoji ziv s trazenim spol nema nista
                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else if (starost != null) {
                        Log.d("Dohvati_p5:",postSnapshot.toString());
                        //trazi se odredena vrsta
                        if(Objects.equals(postSnapshot.child("starost").getValue(), starost)){
                            Log.d("Dohvati_p6:",postSnapshot.toString());
                            //nadena odredena vrsta ziv
                            if(tezina!=null){
                                if(Objects.equals(postSnapshot.child("tezina").getValue(), tezina)){
                                    if(pasmina!=null){
                                        if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }else{//ne postoji ziv s odabranom pasminom
                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        if(status!=null){
                                            if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }else{
                                                //ne postoji ziv s odabranim pasmina+status
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }
                                    }
                                }else{
                                    //nije nadena ziv po zadanim zahtjevima
                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                if(pasmina!=null){
                                    if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                        if(status!=null){
                                            if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }else{
                                                //ne postoji ziv s odabranim pasmina+status
                                                Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }
                                    }else{//ne postoji ziv s odabranom pasminom
                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    if(status!=null){
                                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }else{
                                            //ne postoji ziv s odabranim pasmina+status
                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;
                                        mUploads.add(dohvati);
                                    }
                                }
                            }
                        }else{
                            Log.d("Dohvati_p9:",postSnapshot.toString());
                        }
                    }
                    else if (tezina != null) {
                        Log.d("Dohvati_p7:",postSnapshot.toString());
                        //razi se odredeno stanje
                        if(Objects.equals(postSnapshot.child("tezina").getValue(), tezina)){
                            if(pasmina!=null){
                                if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                    if(status!=null){
                                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }else{
                                            //ne postoji ziv s odabranim pasmina+status
                                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                        }
                                    }else{
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;
                                        mUploads.add(dohvati);
                                    }
                                }else{//ne postoji ziv s odabranom pasminom
                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                if(status!=null){
                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;
                                        mUploads.add(dohvati);
                                    }else{
                                        //ne postoji ziv s odabranim pasmina+status
                                        Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                    assert dohvati != null;
                                    mUploads.add(dohvati);
                                }
                            }
                        }else{
                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                            //nije nadena ziv po zadanim zahtjevima
                        }
                    }
                    else if(pasmina!=null){
                        if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                            if(status!=null){
                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                    assert dohvati != null;
                                    mUploads.add(dohvati);
                                }else{
                                    //ne postoji ziv s odabranim pasmina+status
                                    Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                assert dohvati != null;
                                mUploads.add(dohvati);
                            }
                        }else{//ne postoji ziv s odabranom pasminom
                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                             }

                    }
                    else if(status!=null){
                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                            dohvati = postSnapshot.getValue(ZivUpload.class);
                            assert dohvati != null;
                            mUploads.add(dohvati);
                        }else{
                        //ne postoji ziv s odabranim statusom
                            Toast.makeText(getActivity(),"Nije nađena.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        dohvati = postSnapshot.getValue(ZivUpload.class);
                        assert dohvati != null;
                        mUploads.add(dohvati);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

