package com.example.zivotinje;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class IspisAdapterZiv extends RecyclerView.Adapter<IspisAdapterZiv.ImageViewHolder>{
    private Context mContext;
    private List<ZivUpload> mUploads;
    public IspisAdapterZiv(Context context, List<ZivUpload> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    public IspisAdapterZiv.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_view_skl, parent, false);
        return new IspisAdapterZiv.ImageViewHolder(v);
    }

    public void onBindViewHolder(@NonNull final IspisAdapterZiv.ImageViewHolder holder, final int position) {
        final ZivUpload uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getNaziv());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "This is item in position " + position, Toast.LENGTH_SHORT).show();
                /*PrikazSkl fragment=new PrikazSkl();
                Bundle args = new Bundle();
                args.putString("marker", uploadCurrent.getOznaka());
                fragment.setArguments(args);
                //FragmentTransaction ft=
                FragmentTransaction ft =((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack("tag_back1_adapter");
                ft.commit();*/
            }
        });
        Log.d("Pisem",uploadCurrent.getUrl().toString());
        if(uploadCurrent.getUrl()!=null){
            Glide.with(mContext)
                    .load(uploadCurrent.getUrl().get("0_key"))
                    //.centerCrop()
                    .optionalFitCenter()
                    .into(holder.imageView);

        }
    }
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
