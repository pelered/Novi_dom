package com.example.zivotinje;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class SklonisteAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;

    private String[] imageUrls;
    private String name;
    ArrayList<String> imageUrl;
    ArrayList<String> namee;

    public SklonisteAdapter(Context context, ArrayList<String> imageUrls, ArrayList<String> name) {
        super(context, R.layout.customlayout, imageUrls);
        this.namee=name;
        this.context = context;
        this.imageUrl = imageUrls;

        inflater = LayoutInflater.from(context);
    }
    public SklonisteAdapter(Context context, String[] imageUrls,String name) {
        super(context, R.layout.customlayout, imageUrls);
        this.name=name;
        this.context = context;
        this.imageUrls = imageUrls;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.customlayout, parent, false);
        }
        TextView naziv=convertView.findViewById(R.id.textView_name);
        ImageView slika=convertView.findViewById(R.id.imageView2);

        naziv.setText(name);

        Glide   .with(context)
                .load(imageUrls[position])

                .into( slika);

        return convertView;
    }
}
