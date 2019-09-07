package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class PrikazSkl extends Fragment {
    ArrayList<Parcelable> path;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_prikaz_ziv, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

            FishBun.with(PrikazSkl.this).setImageAdapter(new GlideAdapter())
                    .setMaxCount(5)
                    .setMinCount(1)
                    .setActionBarColor(Color.parseColor("#795548"), Color.parseColor("#5D4037"), false)
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .setAlbumSpanCount(2, 3)
                    .setButtonInAlbumActivity(false)
                    .setCamera(true)
                    .exceptGif(true)
                    .setReachLimitAutomaticClose(true)
                    .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_back_white))
                    .setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                    .setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                    .setAllViewTitle("All")
                    .setMenuAllDoneText("All Done")
                    .textOnNothingSelected("Odaberi jednu do najviše 5")
                    .startAlbum();

    }
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<String>) on <0.6.2

                    path = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<Uri>) on 0.6.2 and later
                    //Log.d("Proba",path.toString());


                    break;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}
