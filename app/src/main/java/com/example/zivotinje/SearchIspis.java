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

import com.example.zivotinje.Adapter.IspisAdapterZiv;
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
    private IspisAdapterZiv mAdapter;
    private List<ZivUpload> mUploads;
    private ZivUpload dohvati;
private Query query;
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
            if(getArguments().getFloat("tezina")!=0.0) {
                tezina = getArguments().getFloat("tezina");
            }
            if (getArguments().getFloat("starost")!=0.0) {
                starost = getArguments().getFloat("starost");
            }
            spol = getArguments().getString("spol");
            vrsta = getArguments().getString("vrsta");
            status = getArguments().getString("status");
           /* Log.d("getArg:", "0"+getArguments().getString("skl"));
            Log.d("getArg:", "1"+getArguments().getString("oznaka"));
            Log.d("getArg:", "2"+getArguments().getString("pasmina"));
            Log.d("getArg:", "3"+getArguments().getFloat("tezina"));
            Log.d("getArg:", "4"+getArguments().getFloat("starost"));
            Log.d("getArg:", "5"+getArguments().getString("spol"));
            Log.d("getArg:", "6"+getArguments().getString("vrsta"));
            Log.d("getArg:", "7"+getArguments().getString("status"));*/



            odaberi_query();
        }
        odaberi_query();


    }

    @SuppressLint("RestrictedApi")
    private void odaberi_query() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Ziv");
        Query query = null;
        //Log.d("odaberi_query():", String.valueOf(starost));
        if (oznaka!= null) {
            Log.d("odaberi_query():", "1");
            query = ref.orderByChild("oznaka").startAt(naz_skl).endAt(oznaka+ '\uf8ff');
        } else if (naz_skl != null) {
            Log.d("odaberi_query():", "2");
            query = ref.orderByChild("naz_skl").startAt(naz_skl).endAt(naz_skl + '\uf8ff');
        } else if (vrsta != null) {
            Log.d("odaberi_query():", "3");
            query = ref.orderByChild("vrsta").startAt(vrsta).endAt(vrsta + '\uf8ff');
        } else if (spol != null) {
            Log.d("odaberi_query():", "4");
            query = ref.orderByChild("spol").startAt(spol).endAt(spol + '\uf8ff');
        } else if (starost != null) {
            Log.d("odaberi_query():", "5");
            query = ref.orderByChild("godine").endAt(starost);
        } else if (tezina != null) {
            Log.d("odaberi_query():", "6");
            query = ref.orderByChild("tezina").endAt(tezina);
        } else if (pasmina != null) {
            Log.d("odaberi_query():", "7");
            query = ref.orderByChild("pasmina").startAt(pasmina).endAt(pasmina + '\uf8ff');
            //Log.d("Odaberi:",query.getPath().toString());
        } else if (status != null) {
            Log.d("odaberi_query():", "8");
            query = ref.orderByChild("status").startAt(status).endAt(status + '\uf8ff');
            //Log.d("Odaberi:",query.getPath().toString());
        }else {
            Log.d("odaberi_query():", "9");
            query = ref.orderByChild("last_date");

        }

        Log.d("odaberi_query():10", query.toString());
        dohvati(query);
    }
    public static Float convertToFloat(Double doubleValue) {
        return doubleValue == null ? null : doubleValue.floatValue();
    }
    public void dohvati(Query query){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("Dohvati_p1:",postSnapshot.toString());
                    if(oznaka!=null){
                        if(postSnapshot.child("oznaka").getValue().equals(oznaka)){
                            dohvati = postSnapshot.getValue(ZivUpload.class);
                            assert dohvati != null;
                            mUploads.add(dohvati);
                        }else{
                            Log.d("T","smo");
                        }
                    }
                    else if (naz_skl != null) {
                        Log.d("Dohvati_p2:",postSnapshot.toString());
                        //trazi se odredeno skl
                        if(Objects.equals(postSnapshot.child("naz_skl").getValue(), naz_skl)){
                            Log.d("Dohvati_p2.2:",postSnapshot.toString());
                            if (vrsta != null) {
                                Log.d("Dohvati_p2.3:",postSnapshot.toString());
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
                                                if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                                    Log.d("Dohvati_p6:",postSnapshot.toString());
                                                    //nadena odredena vrsta ziv
                                                    if(tezina!=null){
                                                        if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                            if(pasmina!=null){
                                                                if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                                    if(status!=null){
                                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                            assert dohvati != null;
                                                                            mUploads.add(dohvati);
                                                                        }else{
                                                                            //ne postoji ziv s odabranim pasmina+status
                                                                            Log.d("T","smo");
                                                                        }
                                                                    }else{
                                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                        assert dohvati != null;
                                                                        mUploads.add(dohvati);
                                                                    }
                                                                }else{//ne postoji ziv s odabranom pasminom
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                if(status!=null){
                                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                        assert dohvati != null;
                                                                        mUploads.add(dohvati);
                                                                    }else{
                                                                        //ne postoji ziv s odabranim pasmina+status
                                                                        Log.d("T","smo");
                                                                    }
                                                                }else{
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }
                                                            }
                                                        }else{
                                                            //nije nadena ziv po zadanim zahtjevima

                                                            Log.d("T","smo");
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
                                                                        Log.d("T","smo");
                                                                    }
                                                                }else{
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }
                                                            }else{//ne postoji ziv s odabranom pasminom
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
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
                                                    if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                        if(pasmina!=null){
                                                            if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                                if(status!=null){
                                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                        assert dohvati != null;
                                                                        mUploads.add(dohvati);
                                                                    }else{
                                                                        //ne postoji ziv s odabranim pasmina+status
                                                                        Log.d("T","smo");
                                                                    }
                                                                }else{
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }
                                                            }else{//ne postoji ziv s odabranom pasminom
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }
                                                    }else{
                                                        //nije nadena ziv po zadanim zahtjevima
                                                        Log.d("T","smo");
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
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }
                                                }
                                            }//do ovuda je dobro

                                        }else {
                                            //ne postoji ziv s trazenim spol nema nista
                                            Log.d("T","smo");
                                        }
                                    } else{//provjeri starost pa nadalje
                                        if (starost != null) {
                                            Log.d("Dohvati_p5:",postSnapshot.toString());
                                            //trazi se odredena vrsta
                                            if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                                Log.d("Dohvati_p6:",postSnapshot.toString());
                                                //nadena odredena vrsta ziv
                                                if(tezina!=null){
                                                    if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                        if(pasmina!=null){
                                                            if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                                if(status!=null){
                                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                        assert dohvati != null;
                                                                        mUploads.add(dohvati);
                                                                    }else{
                                                                        //ne postoji ziv s odabranim pasmina+status
                                                                        Log.d("T","smo");
                                                                    }
                                                                }else{
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }
                                                            }else{//ne postoji ziv s odabranom pasminom
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }
                                                    }else{
                                                        //nije nadena ziv po zadanim zahtjevima
                                                        Log.d("T","smo");
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
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
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
                                                if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                    if(pasmina!=null){
                                                        if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");

                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }
                                                }else{
                                                    //nije nadena ziv po zadanim zahtjevima
                                                    Log.d("T","smo");
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
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
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
                                    Log.d("T","smo");
                                }
                            }else {
                                Log.d("Dohvati_p2.4:",postSnapshot.toString());
                                //ostale provjerit jer vrsta je null,a naz_skl nije znaci,spol,starost,tezina,pasmina,status
                                if (spol != null) {
                                    //trazi se odreden status
                                    if(postSnapshot.child("spol").getValue().equals(spol)){
                                        Log.d("Dohvati_p4:",postSnapshot.toString());
                                        //naden odreden spol ziv
                                        if (starost != null) {
                                            Log.d("Dohvati_p5:",postSnapshot.toString());
                                            //trazi se odredena vrsta
                                            if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                                Log.d("Dohvati_p6:",postSnapshot.toString());
                                                //nadena odredena vrsta ziv
                                                if(tezina!=null){
                                                    if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                        if(pasmina!=null){
                                                            if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                                if(status!=null){
                                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                        assert dohvati != null;
                                                                        mUploads.add(dohvati);
                                                                    }else{
                                                                        //ne postoji ziv s odabranim pasmina+status
                                                                        Log.d("T","smo");
                                                                    }
                                                                }else{
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }
                                                            }else{//ne postoji ziv s odabranom pasminom
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }
                                                    }else{
                                                        //nije nadena ziv po zadanim zahtjevima
                                                        Log.d("T","smo");
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
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
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
                                                if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                    if(pasmina!=null){
                                                        if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }
                                                }else{
                                                    //nije nadena ziv po zadanim zahtjevima
                                                    Log.d("T","smo");
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
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
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
                                        Log.d("T","smo");
                                    }
                                } else{//provjeri starost pa nadalje
                                    Log.d("Dohvati_p2.5:",postSnapshot.toString());
                                    if (starost != null) {
                                        Log.d("Dohvati_p5:",postSnapshot.toString());
                                        //trazi se odredena vrsta
                                        //Float d= Float.parseFloat(postSnapshot.child("godine").getValue().toString());
                                        //Float f= (Float) postSnapshot.child("godine").getValue();
                                        if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                            Log.d("Dohvati_p6:",postSnapshot.toString());
                                            //nadena odredena vrsta ziv
                                            if(tezina!=null){
                                                if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                    if(pasmina!=null){
                                                        if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }
                                                }else{
                                                    //nije nadena ziv po zadanim zahtjevima
                                                    Log.d("T","smo");
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
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
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
                                        Log.d("Dohvati_p2.6:",postSnapshot.toString());
                                        //nije zadana starost ostale provjeriti
                                        if(tezina!=null){
                                            Log.d("Dohvati_p30", String.valueOf((Float.parseFloat(Objects.requireNonNull(postSnapshot.child("tezina").getValue()).toString()))<=tezina));
                                            if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                if(pasmina!=null){
                                                    if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }
                                            }else{
                                                //nije nadena ziv po zadanim zahtjevima
                                                Log.d("T","smo");
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
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Log.d("T","smo");
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
                            }
                        }else{
                            //nema rezultata
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
                                        if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                            Log.d("Dohvati_p6:",postSnapshot.toString());
                                            //nadena odredena vrsta ziv
                                            if(tezina!=null){
                                                if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                    if(pasmina!=null){
                                                        if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                            if(status!=null){
                                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                    assert dohvati != null;
                                                                    mUploads.add(dohvati);
                                                                }else{
                                                                    //ne postoji ziv s odabranim pasmina+status
                                                                    Log.d("T","smo");
                                                                }
                                                            }else{
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }
                                                        }else{//ne postoji ziv s odabranom pasminom
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }
                                                }else{
                                                    //nije nadena ziv po zadanim zahtjevima
                                                    Log.d("T","smo");
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
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
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
                                            if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                if(pasmina!=null){
                                                    if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }
                                            }else{
                                                //nije nadena ziv po zadanim zahtjevima
                                                Log.d("T","smo");
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
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Log.d("T","smo");
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
                                    Log.d("T","smo");
                                }
                            } else{//provjeri starost pa nadalje
                                if (starost != null) {
                                    Log.d("Dohvati_p5:",postSnapshot.toString());
                                    //trazi se odredena vrsta
                                    if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                        Log.d("Dohvati_p6:",postSnapshot.toString());
                                        //nadena odredena vrsta ziv
                                        if(tezina!=null){
                                            if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                                if(pasmina!=null){
                                                    if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                        if(status!=null){
                                                            if(postSnapshot.child("status").getValue().equals(status)){
                                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                                assert dohvati != null;
                                                                mUploads.add(dohvati);
                                                            }else{
                                                                //ne postoji ziv s odabranim pasmina+status
                                                                Log.d("T","smo");
                                                            }
                                                        }else{
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }
                                                    }else{//ne postoji ziv s odabranom pasminom
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }
                                            }else{
                                                //nije nadena ziv po zadanim zahtjevima
                                                Log.d("T","smo");
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
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Log.d("T","smo");
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
                                        if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                            if(pasmina!=null){
                                                if(postSnapshot.child("pasmina").getValue().equals(pasmina)){
                                                    if(status!=null){
                                                        if(postSnapshot.child("status").getValue().equals(status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(postSnapshot.child("status").getValue().equals(status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }
                                        }else{
                                            //nije nadena ziv po zadanim zahtjevima
                                            Log.d("T","smo");
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
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }else{//ne postoji ziv s odabranom pasminom
                                                Log.d("T","smo");
                                            }
                                        }else{
                                            if(status!=null){
                                                if(postSnapshot.child("status").getValue().equals(status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Log.d("T","smo");
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
                            Log.d("T","smo");
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
                                if((Float.parseFloat(postSnapshot.child("godine").getValue().toString()))<=starost){
                                    Log.d("Dohvati_p6:",postSnapshot.toString());
                                    //nadena odredena vrsta ziv
                                    if(tezina!=null){
                                        if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                            if(pasmina!=null){
                                                if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                                    if(status!=null){
                                                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                                            assert dohvati != null;
                                                            mUploads.add(dohvati);
                                                        }else{
                                                            //ne postoji ziv s odabranim pasmina+status
                                                            Log.d("T","smo");
                                                        }
                                                    }else{
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }
                                                }else{//ne postoji ziv s odabranom pasminom
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                if(status!=null){
                                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Log.d("T","smo");
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
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }else{//ne postoji ziv s odabranom pasminom
                                                Log.d("T","smo");
                                            }
                                        }else{
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Log.d("T","smo");
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
                                    if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                        if(pasmina!=null){
                                            if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                                if(status!=null){
                                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                                        assert dohvati != null;
                                                        mUploads.add(dohvati);
                                                    }else{
                                                        //ne postoji ziv s odabranim pasmina+status
                                                        Log.d("T","smo");
                                                    }
                                                }else{
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }
                                            }else{//ne postoji ziv s odabranom pasminom
                                                Log.d("T","smo");
                                            }
                                        }else{
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }
                                    }else{
                                        //nije nadena ziv po zadanim zahtjevima
                                        Log.d("T","smo");
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
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }else{//ne postoji ziv s odabranom pasminom
                                            Log.d("T","smo");
                                        }
                                    }else{
                                        if(status!=null){
                                            if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }else{
                                                //ne postoji ziv s odabranim pasmina+status
                                                Log.d("T","smo");
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
                            Log.d("T","smo");
                        }
                    }
                    else if (starost != null) {
                        Log.d("Dohvati_p5:",postSnapshot.toString());
                        //trazi se odredena vrsta
                        if(convertToFloat((Double) postSnapshot.child("godine").getValue())<=starost){
                            Log.d("Dohvati_p6:",postSnapshot.toString());
                            //nadena odredena vrsta ziv
                            if(tezina!=null){
                                if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                                    if(pasmina!=null){
                                        if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                            if(status!=null){
                                                if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                                    assert dohvati != null;
                                                    mUploads.add(dohvati);
                                                }else{
                                                    //ne postoji ziv s odabranim pasmina+status
                                                    Log.d("T","smo");
                                                }
                                            }else{
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }
                                        }else{//ne postoji ziv s odabranom pasminom
                                            Log.d("T","smo");
                                        }
                                    }else{
                                        if(status!=null){
                                            if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                                assert dohvati != null;
                                                mUploads.add(dohvati);
                                            }else{
                                                //ne postoji ziv s odabranim pasmina+status
                                                Log.d("T","smo");
                                            }
                                        }else{
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }
                                    }
                                }else{
                                    //nije nadena ziv po zadanim zahtjevima
                                    Log.d("T","smo");
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
                                                Log.d("T","smo");
                                            }
                                        }else{
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }
                                    }else{//ne postoji ziv s odabranom pasminom
                                        Log.d("T","smo");
                                    }
                                }else{
                                    if(status!=null){
                                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }else{
                                            //ne postoji ziv s odabranim pasmina+status
                                            Log.d("T","smo");
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
                        if((Float.parseFloat(postSnapshot.child("tezina").getValue().toString()))<=tezina){
                            if(pasmina!=null){
                                if(Objects.equals(postSnapshot.child("pasmina").getValue(), pasmina)){
                                    if(status!=null){
                                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                            dohvati = postSnapshot.getValue(ZivUpload.class);
                                            assert dohvati != null;
                                            mUploads.add(dohvati);
                                        }else{
                                            //ne postoji ziv s odabranim pasmina+status
                                            Log.d("T","smo");
                                        }
                                    }else{
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;
                                        mUploads.add(dohvati);
                                    }
                                }else{//ne postoji ziv s odabranom pasminom
                                    Log.d("T","smo");
                                }
                            }else{
                                if(status!=null){
                                    if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                                        dohvati = postSnapshot.getValue(ZivUpload.class);
                                        assert dohvati != null;
                                        mUploads.add(dohvati);
                                    }else{
                                        //ne postoji ziv s odabranim pasmina+status
                                        Log.d("T","smo");
                                    }
                                }else{
                                    dohvati = postSnapshot.getValue(ZivUpload.class);
                                    assert dohvati != null;
                                    mUploads.add(dohvati);
                                }
                            }
                        }else{
                            Log.d("T","smo");
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
                                    Log.d("T","smo");
                                }
                            }else{
                                dohvati = postSnapshot.getValue(ZivUpload.class);
                                assert dohvati != null;
                                mUploads.add(dohvati);
                            }
                        }else{//ne postoji ziv s odabranom pasminom
                            Log.d("T","smo");
                             }

                    }
                    else if(status!=null){
                        if(Objects.equals(postSnapshot.child("status").getValue(), status)){
                            dohvati = postSnapshot.getValue(ZivUpload.class);
                            assert dohvati != null;
                            mUploads.add(dohvati);
                        }else{
                        //ne postoji ziv s odabranim statusom
                            Log.d("T","smo");
                        }
                    }
                    else {
                        dohvati = postSnapshot.getValue(ZivUpload.class);
                        assert dohvati != null;
                        mUploads.add(dohvati);
                    }
                }

                mAdapter=new IspisAdapterZiv(getActivity(),mUploads);
                mRecyclerView.setAdapter(mAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Neuspjesno dohvacanje iz baze.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}

