package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.zivotinje.Adapter.AutoCompletePasminaAdapter;
import com.example.zivotinje.Model.PasminaItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Search extends Fragment {
    private AutoCompleteTextView idpasmina;
    private AutoCompletePasminaAdapter ispis_pasmina;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private ArrayList<PasminaItem> pasmine,sve_pasmine;
    private EditText idskl,idtezina,idstarost,idoznaka;
    private RadioGroup vrsta, spol, status;
    private RadioButton idvrsta,idspol,idstatus;
    private ImageButton search,refresh;

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
        vrsta =view.findViewById(R.id.vrsta);
        spol =view.findViewById(R.id.idspol);
        status =view.findViewById(R.id.idstatus);
        search=view.findViewById(R.id.idsearch);
        refresh=view.findViewById(R.id.refresh_s);
        ucitaj_pasmine();
        refresh.setOnClickListener(v -> {
            idskl.getText().clear();
            idpasmina.getText().clear();
            idtezina.getText().clear();
            idstarost.getText().clear();
            spol.clearCheck();
            status.clearCheck();
            vrsta.clearCheck();
        });
        search.setOnClickListener(v -> {
            //Toast.makeText(getActivity(),"klik",Toast.LENGTH_SHORT).show();
            SearchIspis fragment=new SearchIspis();
            Bundle args = new Bundle();

            if(vrsta.getCheckedRadioButtonId()!=-1){
                idvrsta =view.findViewById(vrsta.getCheckedRadioButtonId());
                args.putString("vrsta", idvrsta.getText().toString());
            }
            if(status.getCheckedRadioButtonId()!=-1){
                idstatus=view.findViewById(status.getCheckedRadioButtonId());
                args.putString("status",  idstatus.getText().toString());
            }
            if(spol.getCheckedRadioButtonId()!=-1){
                idspol=view.findViewById(spol.getCheckedRadioButtonId());
                args.putString("spol", idspol.getText().toString());
            }
            if (!TextUtils.isEmpty(idoznaka.getText().toString())){
                args.putString("grad",idoznaka.getText().toString());
            }
            if (!TextUtils.isEmpty(idpasmina.getText().toString())){
                args.putString("zup",idpasmina.getText().toString() );
            }
            if (!TextUtils.isEmpty(idstarost.getText().toString())){
                args.putFloat("starost", Float.parseFloat(String.valueOf(idstarost.getText())));
            }
            if (!TextUtils.isEmpty(idtezina.getText().toString())){
                args.putFloat("tezina", Float.parseFloat(idtezina.getText().toString()));
            }

            if(!idtezina.getText().toString().equals("")){
                args.putFloat("tezina", Float.parseFloat(idtezina.getText().toString()));
            }
            if(!idstarost.getText().toString().equals("")){
                args.putFloat("starost", Float.parseFloat(String.valueOf(idstarost.getText())));
            }

            Toast.makeText(getActivity(),"",Toast.LENGTH_SHORT).show();
            /*
            fragment.setArguments(args);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack("tag_search");
            ft.commit();*/
        });
    }
    private void ucitaj_pasmine() {
        pasmine=new ArrayList<>();
        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Pasmine");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,String > privremeno = (HashMap<String, String>) dataSnapshot.getValue();
                  assert privremeno != null;
                if(privremeno!=null) {
                    for (Map.Entry<String, String> entry : privremeno.entrySet()) {
                        pasmine.add(new PasminaItem(entry.getValue()));
                    }
                    sve_pasmine=new ArrayList<>(pasmine);
                    ispis_pasmina = new AutoCompletePasminaAdapter(Objects.requireNonNull(getContext()), pasmine);
                    idpasmina.setAdapter(ispis_pasmina);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Neuspjelo dohvacanje pasmina",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
