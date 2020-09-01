package com.example.zivotinje.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zivotinje.Model.ZivUpload;
import com.example.zivotinje.R;


import java.util.List;
import java.util.Random;

public class ProfileMyAdapter extends RecyclerView.Adapter<ProfileMyAdapter.MyViewHolder> {
    private Context context;
    private List<ZivUpload> itemList;
    public ProfileMyAdapter(Context context, List<ZivUpload> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.card_view_ziv,parent,false);

        return new MyViewHolder(itemView);
    }
    public ZivUpload getItem(int pos){
        return itemList.get(pos);
    }
    public void removeItem(int pos){
        itemList.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Log.d("ProfileMy:",itemList.toString());
        holder.status_ziv.setText("Status: "+itemList.get(position).getStatus());
        holder.ime_ziv.setText("Ime: "+itemList.get(position).getNaziv());
        //holder.grad.setText("Grad: " +itemList.get(position).getGrad());
        if(itemList.get(position).getUrl()!=null){
            Glide.with(context)
                    .load(itemList.get(position).getUrl().get("0_key"))
                    //.centerCrop()
                    .optionalFitCenter()
                    .into(holder.slika_ziv);

        }

        holder.itemView.setOnClickListener(v ->{
            final int random = new Random().nextInt(100);
           /* PrikazZivFragment fragment=new PrikazZivFragment();
            Bundle args = new Bundle();
            args.putString("oznaka", itemList.get(position).getKey());
            fragment.setArguments(args);
            FragmentTransaction ft =((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.addToBackStack("tag_profil_ispis"+random);
            ft.commit();*/
            //Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ime_ziv, status_ziv;
        ImageView slika_ziv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            slika_ziv =itemView.findViewById(R.id.image_view_upload);
            ime_ziv =itemView.findViewById(R.id.textView_name);
            status_ziv =itemView.findViewById(R.id.card_grad);
        }
    }

}
