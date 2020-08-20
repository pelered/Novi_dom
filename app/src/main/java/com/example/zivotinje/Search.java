package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class Search extends Fragment {
    private EditText idskl,idpasmina,idtezina,idstarost,idoznaka;
    private RadioGroup idvrsta,idspol,idstatus;
    private RadioButton idM,idZ,idpas,idmacka,idudomlje,idneudomljen;
    private ImageButton search;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        idskl=view.findViewById(R.id.idskl);
        idpasmina=view.findViewById(R.id.idpasmina);
        idoznaka=view.findViewById(R.id.idoznaka);
        idtezina=view.findViewById(R.id.idtezina);
        idstarost=view.findViewById(R.id.idstarost);
        idvrsta=view.findViewById(R.id.vrsta);
        idspol=view.findViewById(R.id.idspol);
        idstatus=view.findViewById(R.id.idstatus);
        search=view.findViewById(R.id.idsearch);
        search.setOnClickListener(v -> {
            //Toast.makeText(getActivity(),"klik",Toast.LENGTH_SHORT).show();
            SearchIspis fragment=new SearchIspis();
            Bundle args = new Bundle();
            args.putString("skl", idskl.getText().toString());
            args.putString("oznaka", idoznaka.getText().toString());
            args.putString("pasmina", idpasmina.getText().toString());
            if(!idtezina.getText().toString().equals("")){
                args.putFloat("tezina", Float.parseFloat(idtezina.getText().toString()));
            }
            if(!idstarost.getText().toString().equals("")){
                args.putFloat("starost", Float.parseFloat(String.valueOf(idstarost.getText())));
            }
            //
            //
            args.putString("spol", String.valueOf(idspol.getCheckedRadioButtonId()));
            args.putString("status", String.valueOf(idstatus.getCheckedRadioButtonId()));
            args.putString("vrsta", String.valueOf(idvrsta.getCheckedRadioButtonId()));
            Toast.makeText(getActivity(),args.toString(),Toast.LENGTH_SHORT).show();
            fragment.setArguments(args);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack("tag_search");
            ft.commit();
        });
    }
}
