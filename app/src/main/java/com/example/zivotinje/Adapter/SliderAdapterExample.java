package com.example.zivotinje.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;

import com.example.zivotinje.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {
    private Context context;
    private int mCount;
    private int pos2;
    private ArrayList<String> path2;
    private ArrayList<Uri> path;
    public SliderAdapterExample(FragmentActivity context) {

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
        this.path2.addAll(position,list);
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
    public void slike(ArrayList<Uri> path){
        this.path=path;
    }
    public void slike2(ArrayList<String> slike2) {
        this.path2=slike2;
    }
    //velicina polja slika
    public void broj(int pos2){
        this.pos2=pos2;
    }
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        String link2= path2.get(position);
        Glide.with(viewHolder.itemView)
                .load(link2)
                .fitCenter()
                .centerInside()
                .into(viewHolder.imageViewBackground);
        viewHolder.itemView.setOnClickListener(v -> Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show());
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
