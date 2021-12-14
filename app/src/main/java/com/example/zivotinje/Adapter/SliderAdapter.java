package com.example.zivotinje.Adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import com.example.zivotinje.Helper.OnSwipeListener;
import com.example.zivotinje.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {
    private Context context;
    private int mCount;
    private ArrayList<String> path2;

    public SliderAdapter(FragmentActivity context) {

        this.context = context;
    }
    //velicina polja slika
    public void setCount(int count) {
        this.mCount = count;
    }
    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mCount;
    }
    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_slider_adapter_example, null);

        return new SliderAdapterVH(inflate);
    }
    public void deleteItem(int position) {
        this.path2.remove(position);
        setCount(path2.size());
        notifyDataSetChanged();
    }
    public void renewItems(ArrayList<String> list) {
        this.path2 = list;
        setCount(path2.size());
        notifyDataSetChanged();
    }
    public void addItem(ArrayList<String> list,int position) {
        this.path2.removeAll(list);
        this.path2.addAll(position+1,list);
        setCount(path2.size());
        notifyDataSetChanged();
    }
    public ArrayList<String> getList(){
        return path2;
    }
    public String getImage(int position){
        return path2.get(position);
    }
    //ovo je slike uzete s mobitela
    /*public void slike(ArrayList<Uri> path){
        this.path=path;
    }*/
    public void slike2(ArrayList<String> slike2) {
        this.path2=slike2;
    }

    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        String link2= path2.get(position);
        if(link2.contains("mp4")) {
            viewHolder.imageViewBackground.setVisibility(View.INVISIBLE);
            viewHolder.videoViewBackground.setVisibility(View.VISIBLE);

            viewHolder.videoViewBackground.setVideoPath(link2);
            //viewHolder.videoViewBackground.seekTo(1);
            viewHolder.controller.show();
        }else{
            viewHolder.controller.hide();
            viewHolder.videoViewBackground.stopPlayback();
            viewHolder.videoViewBackground.setVisibility(View.INVISIBLE);
            viewHolder.imageViewBackground.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.itemView)
                    .load(link2)
                    .fitCenter()
                    .centerInside()
                    .into(viewHolder.imageViewBackground);
        }
        //viewHolder.itemView.setOnClickListener(v -> Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show());
    }
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;
        VideoView videoViewBackground;
        MediaController controller;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            videoViewBackground=itemView.findViewById(R.id.iv_auto_video_slider);
            controller=new MediaController(context);
            videoViewBackground.setMediaController(controller);

            this.itemView = itemView;

        }
    }
}
