package com.example.zivotinje;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PrikazZiv extends Fragment implements View.OnClickListener {
    ArrayList<Parcelable> path;
    private Button odaberi;

    SliderView sliderView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_prikaz_ziv, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sliderView = view.findViewById(R.id.imageSlider);
        /*
        final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
        adapter.setCount(3);
        adapter.slike(path);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(15);
        //sliderView.startAutoCycle();


        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });

         */
        odaberi=view.findViewById(R.id.odaberi);
        odaberi.setOnClickListener(this);






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
                    //path=imageData.getStringArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<Uri>) on 0.6.2 and later
                    //Log.d("Proba",path.toString());
                    final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
                    adapter.setCount(path.size());
                    adapter.slike(path);
                    adapter.broj(path.size());
                    sliderView.setSliderAdapter(adapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                    sliderView.setIndicatorSelectedColor(Color.WHITE);
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.setScrollTimeInSec(15);
                    //sliderView.startAutoCycle();

                    sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
                        @Override
                        public void onIndicatorClicked(int position) {
                            sliderView.setCurrentPagePosition(position);
                        }
                    });
                    break;
                }
        }
    }



    @Override
    public void onClick(View view) {


        FishBun.with(PrikazZiv.this).setImageAdapter(new GlideAdapter())
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
}