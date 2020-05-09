package com.example.zivotinje.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.zivotinje.Model.PasminaItem;
import com.example.zivotinje.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompletePasminaAdapter extends ArrayAdapter<PasminaItem> {
    //sadrzi sve pasmine koje smo napocetku primili
    private List<PasminaItem> pasminaListFull;

    public AutoCompletePasminaAdapter(@NonNull Context context,  @NonNull List<PasminaItem> pasminaList) {
        super(context, 0, pasminaList);
        //sprema se primljen objekt iz Fragmenta
        pasminaListFull =new ArrayList<>(pasminaList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return pasminaFilter;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.pasmina_autocomplete_row, parent, false
            );
        }
        TextView textViewName = convertView.findViewById(R.id.text_view_name);

        PasminaItem pasminaItem =getItem(position);

        if(pasminaItem !=null){
            textViewName.setText(pasminaItem.getPasminaName());
        }
        //Log.d("Pasmina_unos_zapisan",textViewName.getText().toString());
        return convertView;
    }

    private Filter pasminaFilter =new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results= new FilterResults();
            List<PasminaItem> suggestions = new ArrayList<>();
            if(constraint ==null || constraint.length()==0){
                suggestions.addAll(pasminaListFull);
            }else{
                String filterPattern =constraint.toString().toLowerCase().trim();
                for(PasminaItem item: pasminaListFull){
                    if(item.getPasminaName().toLowerCase().contains((filterPattern))){
                        suggestions.add(item);
                    }
                }
            }
            //Log.d("Pasmina sugg",suggestions.toString());
            results.values = suggestions;
            results.count = suggestions.size();
            //Log.d("Pasmina_result",results.toString());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            //ovaj dio stavlja rezultat u box
            return ((PasminaItem) resultValue).getPasminaName();
        }

    };
}
