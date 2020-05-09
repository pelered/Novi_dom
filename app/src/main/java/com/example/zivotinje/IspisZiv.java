package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.zivotinje.Adapter.IspisAdapterZiv;
import com.example.zivotinje.Model.ZivUpload;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IspisZiv extends Fragment {
    private String id_skl;
    private RecyclerView mRecyclerView;
    private IspisAdapterZiv mAdapter;
    private ProgressBar mProgressCircle;
    private List<ZivUpload> mUploads;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ispis, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressCircle=view.findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();

        mProgressCircle.setVisibility(View.VISIBLE);
        if(getArguments()==null){
            Toast.makeText(getContext(),"Nisi smio ovo uspjet,javi mi kako",Toast.LENGTH_SHORT).show();
        }else {
            id_skl=getArguments().getString("id_skl");
        }
        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Ziv");

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("Ispisujem", String.valueOf(postSnapshot.child("id_skl")));
                    //Log.d("Ispisujem*",id_skl);
                    if(postSnapshot.child("id_skl").getValue().equals(id_skl)){
                        Log.d("Ispisujem",postSnapshot.getValue().toString());
                        ZivUpload upload = postSnapshot.getValue(ZivUpload.class);
                        assert upload != null;
                        mUploads.add(upload);                    }

                }
                mAdapter=new IspisAdapterZiv(getActivity(),mUploads);
                mAdapter = new IspisAdapterZiv(getActivity(), mUploads);
                mRecyclerView.setAdapter(mAdapter);
                //mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Neuspjela autorizacija " , Toast.LENGTH_SHORT).show();
                //mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }
}
