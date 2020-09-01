package com.example.zivotinje.Adapter;

import android.content.Context;
import android.os.Bundle;
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
import com.example.zivotinje.Model.Skl;
import com.example.zivotinje.PrikazSkl;
import com.example.zivotinje.R;

import java.util.List;

public class IspisAdapter extends RecyclerView.Adapter<IspisAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Skl> mUploads;
    public IspisAdapter(Context context, List<Skl> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_view_skl, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {
        final Skl uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getNaziv());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                if(uploadCurrent.getId()!=null){
                    PrikazSkl fragment=new PrikazSkl();
                    Bundle args = new Bundle();
                    args.putString("marker", uploadCurrent.getId());
                    fragment.setArguments(args);
                    //FragmentTransaction ft=
                    FragmentTransaction ft =((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.addToBackStack("tag_back1_adapter");
                    ft.commit();
                }

            }
        });
        if(uploadCurrent.getUrl()!=null){
            Glide.with(mContext)
                    .load(uploadCurrent.getUrl().get("0_key"))
                    .centerCrop()
                    .into(holder.imageView);
        }
    }
    @Override
    public int getItemCount() {
        return mUploads.size();
    }
    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView_name);
            imageView = itemView.findViewById(R.id.image_view_upload);
        }
    }
}
