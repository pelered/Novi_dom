package com.example.zivotinje;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
    private Context context;
    private int mCount;
    private int pos2;
    ArrayList<String> path2;
    ArrayList<Uri> path;
    public SliderAdapterExample(FragmentActivity context) {
        this.context = context;
    }

    //velicina polja slika
    public void setCount(int count) {
        this.mCount = count;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_slider_adapter_example, null);
        return new SliderAdapterVH(inflate);
    }
    public void deleteItem(int position) {
        this.path2.remove(position);
        notifyDataSetChanged();
    }
    //ovo je slike uzete s mobitela
    public void slike(ArrayList<Uri> path){
        this.path=path;
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
        if(path==null){
             link2=path2;
            for (int i = 0; i < pos2; i++) {
                Glide.with(viewHolder.itemView)
                        .load(link2.get(position))
                        //.centerInside()
                        //.fitCenter()
                        //.optionalCenterCrop()
                        //.optionalFitCenter()
                        .optionalCenterInside()
                        .into(viewHolder.imageViewBackground);
            }
        }else{
            link=path;
            for (int i = 0; i < pos2; i++) {
                Glide.with(viewHolder.itemView)
                        .load(link.get(position))
                        .fitCenter()
                        .into(viewHolder.imageViewBackground);
            }
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mCount;
    }

    public void slike2(ArrayList<String> slike2) {
        this.path2=slike2;
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
