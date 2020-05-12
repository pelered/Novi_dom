package com.example.zivotinje.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
//import com.esafirm.imagepicker.model.Image;
import com.bumptech.glide.request.RequestOptions;
import com.example.zivotinje.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
    private Context context;
    private int mCount;
    private int pos2;
    ArrayList<String> path2;
    ArrayList<Uri> path;
    //List<Image> path3;
    public SliderAdapterExample(FragmentActivity context) {
        this.context = context;
    }

    //velicina polja slika
    public void setCount(int count) {
        this.mCount = count;
        Log.d("Pozicija()*", String.valueOf(mCount));
    }
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_slider_adapter_example, null);
        return new SliderAdapterVH(inflate);
    }/*
    public void deleteItem(int position) {
        this.path2.remove(position);
        notifyDataSetChanged();
    }*/
    //ovo je slike uzete s mobitela
    public void slike(ArrayList<Uri> path){
        this.path=path;
        //Log.d("Pozicija()**", String.valueOf(path));
    }
    public void slike2(ArrayList<String> slike2) {
        this.path2=slike2;
        Log.d("Pozicija()**", String.valueOf(path2));
    }
    //velicina polja slika
    public void broj(int pos2){
        this.pos2=pos2;
    }
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
        ArrayList<Uri> link;
        ArrayList<String> link2;
        Log.d("Pozicijabroj: ", String.valueOf(mCount));
        Log.d("Pozicijabroj*", String.valueOf(path2.size()));
        Log.d("Pozicijabroj**: ", String.valueOf(position));
        if(path==null){
             link2=path2;
            for (int i = 0; i < mCount; i++) {
                Glide.with(viewHolder.itemView)
                        .load(link2.get(position))
                        //.centerInside()
                        .fitCenter()
                        //.optionalCenterCrop()
                        //.optionalFitCenter()
                        //.optionalCenterInside()
                        //.apply(new RequestOptions().override(400, 300))
                        .centerInside()

                        .into(viewHolder.imageViewBackground);
            }
        }/*else if(path2==null){
            link=path;
            for (int i = 0; i < pos2; i++) {
                Glide.with(viewHolder.itemView)
                        .load(link.get(position))
                        .fitCenter()
                        .into(viewHolder.imageViewBackground);
            }
        }*/
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mCount;
    }
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
