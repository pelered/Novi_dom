package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class IspisZiv extends Fragment {
    private String id_skl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_ispis, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if(getArguments()==null){
            Toast.makeText(getContext(),"Nisi smio ovo uspjet,javi mi kako",Toast.LENGTH_SHORT).show();
        }else {
            id_skl=getArguments().getString("id_skl");
        }

    }
}
