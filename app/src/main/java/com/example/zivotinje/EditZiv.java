package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zivotinje.Adapter.AutoCompletePasminaAdapter;
import com.example.zivotinje.Adapter.SliderAdapterExample;
import com.example.zivotinje.Model.MinMaxFilter;
import com.example.zivotinje.Model.PasminaItem;
import com.example.zivotinje.Model.ZivUpload;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.MimeType;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.google.android.gms.common.util.CollectionUtils.listOf;

public class EditZiv extends Fragment{
    private static final int REQUEST_CODE_CHOOSE = 105;
    private EditText ime,godine,tezina,opis,oznaka,mjesec;
    private RadioGroup vrsta,spol,status;
    private RadioButton id_vrste,id_spol,id_status;
    private SliderView sliderView;
    private Button upload,odaberi_sliku;
    private TextView obrisi_sl;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private StorageTask<com.google.firebase.storage.UploadTask.TaskSnapshot> mUploadTask;
    private SharedPreferences prefs;
    private View ve;
    private ArrayList<PasminaItem> pasmine;
    private ArrayList<Uri> slike;
    private HashMap<String,String> slike2=new HashMap<>();
    private HashMap<String,String> slike_map=new HashMap<>();
    private ArrayList<String> slike_ucitavanje=new ArrayList<>();
    private ArrayList<Uri> ImageList = new ArrayList<>();
    private int i=0;
    private int uploads = 0;
    private SliderAdapterExample adapter;
    private String id_skl,oznaka_ziv;
    private ZivUpload dohvaceno;
    private ProgressBar progressBar;
    private AutoCompleteTextView pasmina;
    private AutoCompletePasminaAdapter ispis_pasmina;
    private TextView izabrana_pasmina;
    String slika_brisanja;
    int pozicija;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_edit_ziv, container, false);
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ime=view.findViewById(R.id.naziv_ziv);
        godine=view.findViewById(R.id.godine);
        tezina=view.findViewById(R.id.tezina);
        opis=view.findViewById(R.id.opis_ziv);
        vrsta=view.findViewById(R.id.vrsta);
        spol=view.findViewById(R.id.spol);
        mjesec=view.findViewById(R.id.mjeseci);
        mjesec.setFilters( new InputFilter[]{ new MinMaxFilter( "0" , "12" )}) ;
        godine.setFilters( new InputFilter[]{ new MinMaxFilter( "0" , "30" )}) ;
        tezina.setFilters( new InputFilter[]{ new MinMaxFilter( "0" , "100" )}) ;
        status=view.findViewById(R.id.status);
        sliderView =view.findViewById(R.id.imageSlider);
        upload=view.findViewById(R.id.button_upload);
        odaberi_sliku=view.findViewById(R.id.button_choose_image);
        oznaka=view.findViewById(R.id.oznaka);
        progressBar=view.findViewById(R.id.progress_bar);
        pasmina =view.findViewById(R.id.pasmina);
        izabrana_pasmina=view.findViewById(R.id.text_view_name);
        obrisi_sl=view.findViewById(R.id.obrisi_sliku);

        mStorageRef = FirebaseStorage.getInstance().getReference("Ziv");
        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Ziv");

        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        id_skl=prefs.getString("uid",null);
        ve=view;
        adapter= new SliderAdapterExample(getActivity());

        id_vrste = view.findViewById(vrsta.getCheckedRadioButtonId());
        id_spol=view.findViewById(spol.getCheckedRadioButtonId());
        id_status=view.findViewById(status.getCheckedRadioButtonId());

        progressBar.setVisibility(View.INVISIBLE);

        if (getArguments()==null){
            Toast.makeText(getContext(),"Nisi smio ovo uspjet,javi mi kako",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Oznaka: "+getArguments().getString("oznaka"),Toast.LENGTH_SHORT).show();
            oznaka_ziv= getArguments().getString("oznaka");
        }
        obrisi_sl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obrisi();
                //Log.d("Tu sam", String.valueOf(sliderView.getCurrentPagePosition()));
            }
        });
        odaberi_sliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    //prvo provjeri dal upisana oznaka vec postoji,samo ucitaj podatke i spremi u true ili false
                    if(TextUtils.isEmpty(ime.getText().toString()) || TextUtils.isEmpty(pasmina.getText().toString()) || TextUtils.isEmpty(godine.getText().toString()) || TextUtils.isEmpty(tezina.getText().toString()) || TextUtils.isEmpty(oznaka.getText().toString()) ){
                        Toast.makeText(getActivity(), "Jedino opis može ostat prazan", Toast.LENGTH_LONG).show();
                        Log.d("vrsta", id_vrste.getText().toString());
                    }else {
                        progressBar.setVisibility(View.VISIBLE);
                        uploadFile();
                    }
                }
            }
        });
        pasmine=new ArrayList<>();
        //ucita podatke samo ako dode s ulogiranog sklonista,inace ne nego imamo prazna stranicu za popunit
        if(TextUtils.isEmpty(getArguments().getString("id_skl_prikaz"))){
            ucitajPodatke();
        }

    }

    private void obrisi() {
        pozicija=sliderView.getCurrentPagePosition();
        Log.d("Pozicija", String.valueOf(pozicija));
        slika_brisanja=slike_ucitavanje.get(pozicija);
        Log.d("Pozicija*",slika_brisanja);
        Log.d("Pozicija***Image",ImageList.toString());
        if(!ImageList.isEmpty()){
            if(ImageList.contains(slika_brisanja)){
                Log.d("pozicija**", String.valueOf(ImageList.indexOf(slika_brisanja)));
            }
        }
        slike_ucitavanje.remove(pozicija);

    }

    //pokrene se pri ucitavanju fragmenta za dohvacanje podataka
    private void ucitajPodatke(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //dodajemo sve vrste pasmina
                    pasmine.add(new PasminaItem(postSnapshot.child("pasmina").getValue().toString()));
                    if(postSnapshot.child("id_skl").getValue().equals(id_skl)){
                        if(postSnapshot.child("oznaka").getValue().equals(oznaka_ziv)) {
                            dohvaceno = postSnapshot.getValue(ZivUpload.class);
                            postavi_vrijednosti();
                            if(postSnapshot.hasChild("url")){
                                //spremamo hash mapu
                                slike2=new HashMap<>(dohvaceno.getUrl());
                                //ovo radimo da mozemo prikazati slike lijepo,potreban samo url
                                for(Map.Entry<String, String> entry :dohvaceno.getUrl().entrySet())
                                {
                                    slike_ucitavanje.add(entry.getValue());
                                }
                            }else {
                                //stvara se prazna lista,da ne dode do greske
                                    slike_ucitavanje.clear();
                                    //if(slike2==null){
                                    slike2.clear();//}
                            }
                        }
                    }
                }
                //popis svih pasmina koji se stavljaju u adapter za autofill
                ispis_pasmina=new AutoCompletePasminaAdapter(getContext(),pasmine);
                pasmina.setAdapter( ispis_pasmina);
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Skidanje nije uspjelo", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.d("pod slike2",slike2.toString());
                if(!slike_ucitavanje.isEmpty()){
                    inicijalizirajSlider(slike_ucitavanje);
                }
            }
        }, 1000);
    }
    //postavlja vrijednosti varijabli
    private void postavi_vrijednosti() {
        ime.setText(dohvaceno.getNaziv());
        ime.setText(dohvaceno.getNaziv());
        godine.setText(dohvaceno.getGodine().toString());
        tezina.setText(dohvaceno.getTezina().toString());
        opis.setText(dohvaceno.getOpis());
        oznaka.setText(dohvaceno.getOznaka());
        pasmina.setText(dohvaceno.getPasmina());
        if(dohvaceno.getSpol()!=null) {
            if (dohvaceno.getSpol().equals("M")) {
                RadioButton id = ve.findViewById(R.id.M);
                id.setChecked(true);
            } else if (dohvaceno.getSpol().equals("Ž")) {
                RadioButton id = ve.findViewById(R.id.Z);
                id.setChecked(true);
            }
        }
        if(dohvaceno.getStatus()!=null) {
            if (dohvaceno.getStatus().equals("Udomljen")) {
                RadioButton id = ve.findViewById(R.id.Udomljen);
                id.setChecked(true);
            } else if (dohvaceno.getStatus().equals("Neudomljen")) {
                RadioButton id = ve.findViewById(R.id.Neudomljen);
                id.setChecked(true);
            }
        }
        if(dohvaceno.getVrsta()!=null) {
            if (dohvaceno.getVrsta().equals("Pas")) {
                RadioButton id = ve.findViewById(R.id.pas);
                id.setChecked(true);
            } else if (dohvaceno.getVrsta().equals("Macka")) {
                RadioButton id = ve.findViewById(R.id.macka);
                id.setChecked(true);
            }
        }
    }
    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    private void inicijalizirajSlider( ArrayList<String> slike_slider){
        //SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
        //adapter.setCount(slike_slider.size());
        Log.d("POzicijaslider",slike_slider.toString());
        adapter.setCount(slike_slider.size());
        //adapter.broj(slike_slider.size());
        adapter.slike2(slike_slider);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(15);
        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
                Log.d("Brisem: ", String.valueOf(position));
            }
        });
    }
    //uplodamo slike i dohvacamorl njihov
    private void uploadFile() {
        //prvo provjeri dal upisana oznaka vec postoji
        //provjeravamo dal postoje odabrane slike iz galerije
        if (!ImageList.isEmpty()) {
            //slike_ucitavanja
            if (!slike_ucitavanje.isEmpty()) {
                //prvo spremamo vec skinute slike s baze podataka
                slike_map=new HashMap<>(slike2);
                //Todo provjeri dal je s -1 ili bez
                i =slike_map.size();
            }
            for (uploads = 0; uploads < ImageList.size(); uploads++) {
                final Uri Image = ImageList.get(uploads);
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(Image));
                mUploadTask = fileReference.putFile(Image);
                // Register observers to listen for when the download is done or if it fails
                mUploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), "Upload nije uspio " + exception.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            //mUploadTask = fileReference.putFile(Image);
                            /*Task<Uri> urlTask =*/ mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                   // Log.d("podaci1", task.toString());
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    // Continue with the task to get the download URL
                                    return fileReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        //Log.d("trazeni", downloadUri.toString());
                                        //spremam u hash listu
                                        slike_map.put(i + "_key", downloadUri.toString());
                                        slike2.put(i + "_key", downloadUri.toString());
                                        i++;
                                        //Log.d("trazeni key", slike_map.toString());
                                        Toast.makeText(getActivity(), "Upload.Dohvacen url", Toast.LENGTH_LONG).show();
                                        //spremam samo url-ove
                                        //slike.add(downloadUri.toString());
                                    } else {
                                        Toast.makeText(getActivity(), "Upload nije uspio", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

                }
            } else{
            //ako ne postoje odabrane slike iz galerije samo ponovo zapisujemo već skinute,tj uplodane slike
                Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
                slike_map=new HashMap<>(slike2);

            }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("pod slike",slike.toString());
                dodajSliku();
            }
        }, 10000);
    }
    //slika se dodajeu bazu podataka , kao i podaci o zivotinji
    private void dodajSliku(){
        id_vrste =ve.findViewById(vrsta.getCheckedRadioButtonId());
        id_status=ve.findViewById(status.getCheckedRadioButtonId());
        id_spol=ve.findViewById(spol.getCheckedRadioButtonId());
        //da imamo listu sa svim urlovim do sad uplodanih slika, ili hash mapu
        slike_ucitavanje.clear();
        slike2.clear();
        for (Map.Entry<String, String> entry : slike_map.entrySet()) {
            slike_ucitavanje.add(entry.getValue());
        }
        slike2=new HashMap<>(slike_map);
        ZivUpload upload2 = new ZivUpload(ime.getText().toString(),oznaka.getText().toString(), id_vrste.getText().toString(),pasmina.getText().toString(),opis.getText().toString(),Float.parseFloat(tezina.getText().toString()),Float.parseFloat(godine.getText().toString()),prefs.getString("uid",""),slike_map,id_spol.getText().toString(),id_status.getText().toString());
        Map<String, Object> postValues2=upload2.toMap();
        mDatabaseRef.child(oznaka.getText().toString()).updateChildren(postValues2).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"Uplodano/Ažurirano",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Neuspjel pokušaj uplodanja/ažuriranja",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        //resetira se jer smo uplodali slike
        ImageList.clear();

    }
    //dohvacamo koja vrsta je slika
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //za otvaranje galerije na klik gumba
    private void openFileChooser() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);
        Button btn_video = (Button)mView.findViewById(R.id.video);
        Button btn_slike = (Button)mView.findViewById(R.id.slike);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
               //Jednog dana za video
                Toast.makeText(getActivity(),"Jos nije implementirano",Toast.LENGTH_SHORT).show();
            }
        });
        btn_slike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                FishBun.with(EditZiv.this)
                        .setImageAdapter(new GlideAdapter())
                        .setMaxCount(10)
                        .setMinCount(1)
                        .setPickerSpanCount(5)
                        .setActionBarColor(Color.parseColor("#466deb"), Color.parseColor("#466deb"), false)
                        .setActionBarTitleColor(Color.parseColor("#ffffff"))
                        //.setArrayPaths(path)
                        .setAlbumSpanCount(2, 3)
                        .setButtonInAlbumActivity(false)
                        .setCamera(true)
                        //.exceptGif(true)
                        .setReachLimitAutomaticClose(true)
                        .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_back_white))
                        .setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                        .setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                        //.setIsUseAllDoneButton(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                        .setAllViewTitle("All")
                        .setMenuAllDoneText("All Done")
                        .setActionBarTitle("FishBun Dark")
                        .textOnNothingSelected("Please select 1 or more!")
                        .exceptMimeType(listOf(MimeType.GIF))
                        .textOnImagesSelectionLimitReached("Limit Reached!")
                        .setSelectCircleStrokeColor(Color.BLACK)
                        .startAlbum();
            }
        });
        alertDialog.show();

    }
    //pokrene se kad dode neki rezultat npr od galerije
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        Log.d("Slike*",imageData.toString());
         switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //svaki put spremi odabrane slike s mobitela, nakon svakog poziva galeriji, uvijek se resetira
                    slike=new ArrayList<>(imageData.getParcelableArrayListExtra(Define.INTENT_PATH));
                    if(ImageList.isEmpty()){
                        //ako ne postoji vec odabrana slika s mobitela
                        ImageList=new ArrayList<>(imageData.getParcelableArrayListExtra(Define.INTENT_PATH));
                    }else{
                        //ako smo vec prije odabrali slike,a nismo uplodali ih, spremamo ih u ImageList
                        for (int i=0;i<slike.size();i++){
                            ImageList.add(slike.get(i));
                        }
                    }
                    //ovdje spremamo sve trenutne slike koje imamo prikazane,nakon svakog odabira u galeriji se dodaju najnovije slike na vec postojece
                    for (int i = 0; i < slike.size(); i++) {
                        slike_ucitavanje.add(slike.get(i).toString());
                    }
                    Log.d("Pozicija**/", String.valueOf(slike_ucitavanje.toString()));
                    final SliderAdapterExample adapter = new SliderAdapterExample(getActivity());
                    //ovdje prikazujemo sve trenutne slike
                    adapter.setCount(slike_ucitavanje.size());
                    adapter.slike2(slike_ucitavanje);
                    //adapter.broj(slike_ucitavanje.size());
                    //postavljanje slidera
                    sliderView.setSliderAdapter(adapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                    sliderView.setIndicatorSelectedColor(Color.WHITE);
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.setScrollTimeInSec(15);

                }
        }
    }
}
