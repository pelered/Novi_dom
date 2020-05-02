package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Ispis extends Fragment {
    private RecyclerView mRecyclerView;
    private IspisAdapter mAdapter;
    private ProgressBar mProgressCircle;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private List<Root> mUploads;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ispis,container,false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProgressCircle=view.findViewById(R.id.progress_circle);
        //TODO-progress cirle napraviti
        mUploads = new ArrayList<>();

        mProgressCircle.setVisibility(View.VISIBLE);

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Sklonista");
        //Log.d("Usao",mDatabaseRef.toString());

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //mProgressCircle.getProgress();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("Usao sam", String.valueOf(mProgressCircle.));
                    //Log.d("Usao sam2",postSnapshot.toString());
                    Root upload = postSnapshot.getValue(Root.class);
                    assert upload != null;
                    //Log.d("Lose",upload.toString());
                    //Log.d("Lose", String.valueOf(upload.getUrl().values()));
                    mUploads.add(upload);
                    //Log.d("Lose2",upload.toString());
                }
                mAdapter = new IspisAdapter(getActivity(), mUploads);
                mRecyclerView.setAdapter(mAdapter);

                mProgressCircle.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }
    }

