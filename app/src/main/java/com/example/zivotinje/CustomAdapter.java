package com.example.zivotinje;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter implements SpinnerAdapter {

    private String[] company;
    private Context context;
//    private String[] colors = {"#13edea","#e20ecd","#15ea0d"};
//    private String[] colorsback = {"#FF000000","#FFF5F1EC","#ea950d"};

    CustomAdapter(Context context, String[] company) {
        this.company = company;
        this.context = context;
    }

    @Override
    public int getCount() {
        return company.length;
    }

    @Override
    public Object getItem(int position) {
        return company[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View view =  View.inflate(context, R.layout.company_main, null);
        TextView textView =view.findViewById(R.id.main);
        textView.setText(company[position]);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view = View.inflate(context, R.layout.company_dropdown, null);
        final TextView textView =view.findViewById(R.id.dropdown);
        textView.setText(company[position]);

        textView.setTextColor(Color.parseColor("#FF000000"));
        textView.setBackgroundColor(Color.parseColor("#FFFFFF"));


        return view;
    }
}
