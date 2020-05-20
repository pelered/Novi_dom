package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EditZiv extends Fragment{
    private EditText ime,godine,tezina,opis,oznaka,mjesec,gram;
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
    private HashMap<String,String> slike_map=new HashMap<>();
    private ArrayList<String> slike_ucitavanje=new ArrayList<>();
    private HashMap<String,Uri> ImageList = new HashMap<>();
    private int count=0;
    private SliderAdapterExample adapter;
    private String id_skl,oznaka_ziv;
    private ZivUpload dohvaceno;
    private ProgressBar progressBar;
    private AutoCompleteTextView pasmina;
    private AutoCompletePasminaAdapter ispis_pasmina;
    private int pozicija;
    private ArrayList<String> brisi_slike;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_edit_ziv, container, false);
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
        pasmina =view.findViewById(R.id.pasmina_auto);
        obrisi_sl=view.findViewById(R.id.obrisi_sliku);
        gram=view.findViewById(R.id.gram);
        mStorageRef = FirebaseStorage.getInstance().getReference("Ziv");

        //iz SharedPref se uzima id jer jedino skloniste logirano može imat ovdje pristup
        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        id_skl=prefs.getString("uid",null);
        gram.setFilters( new InputFilter[]{ new MinMaxFilter( "0" , "1000" )}) ;

        ve=view;
        adapter= new SliderAdapterExample(getActivity());

        id_vrste = view.findViewById(vrsta.getCheckedRadioButtonId());
        id_spol=view.findViewById(spol.getCheckedRadioButtonId());
        id_status=view.findViewById(status.getCheckedRadioButtonId());

        progressBar.setVisibility(View.INVISIBLE);

        brisi_slike=new ArrayList<>();

        if (getArguments()==null){
            Toast.makeText(getContext(),"Nisi smio ovo uspjet,javi mi kako",Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(getContext(),"Oznaka: "+getArguments().getString("oznaka"),Toast.LENGTH_SHORT).show();
            oznaka_ziv= getArguments().getString("oznaka");
        }
        obrisi_sl.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
            obrisi();
            }
        });
        odaberi_sliku.setOnClickListener(v -> openFileChooser());
        upload.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                //prvo provjeri dal upisana oznaka vec postoji,samo ucitaj podatke i spremi u true ili false
                if(TextUtils.isEmpty(ime.getText().toString()) ||TextUtils.isEmpty(gram.getText().toString()) || TextUtils.isEmpty(pasmina.getText().toString()) || TextUtils.isEmpty(godine.getText().toString()) || TextUtils.isEmpty(tezina.getText().toString()) || TextUtils.isEmpty(oznaka.getText().toString()) ){
                    Toast.makeText(getActivity(), "Jedino opis može ostat prazan", Toast.LENGTH_LONG).show();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    uploadFile();
                }
            }
        });
        pasmine=new ArrayList<>();
        //ucita podatke samo ako dode s ulogiranog sklonista,inace ne nego imamo prazna stranicu za popunit
        if(TextUtils.isEmpty(getArguments().getString("id_skl_prikaz"))){
            database= FirebaseDatabase.getInstance();
            mDatabaseRef = database.getReference("Ziv");
            ucitajPodatke();
        }else{
            ucitaj_pasmine();

        }

    }

    private void ucitaj_pasmine() {
        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Pasmine");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,String > privremeno = (HashMap<String, String>) dataSnapshot.getValue();
                assert privremeno != null;
                Log.d("ucitajPasmi(): ",privremeno.toString());

                for (Map.Entry<String, String> entry : privremeno.entrySet()) {
                    Log.d("ucitajPasmi()2: ",entry.getValue());

                    pasmine.add(new PasminaItem(entry.getValue()));
                    Log.d("ucitajPasmi()3: ",pasmine.toString());

                }
                ispis_pasmina=new AutoCompletePasminaAdapter(Objects.requireNonNull(getContext()),pasmine);
                pasmina.setAdapter(ispis_pasmina);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),"Neuspjelo dohvacanje pasmina",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void obrisi() {
        pozicija=sliderView.getCurrentPagePosition();
        if(adapter.getImage(pozicija).contains("http")) {
            brisi_slike.add(adapter.getImage(pozicija));
        }
        adapter.deleteItem(pozicija);
        adapter.notifyDataSetChanged();
    }
    //pokrene se pri ucitavanju fragmenta za dohvacanje podataka
    private void ucitajPodatke(){
        //Todo dodati child(oznaka_ziv)
        mDatabaseRef.child(oznaka_ziv).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //dodajemo sve vrste pasmina
                    dohvaceno = dataSnapshot.getValue(ZivUpload.class);
                    postavi_vrijednosti();
                    if(dataSnapshot.hasChild("url")){
                        //spremamo hash mapu
                        //ovo radimo da mozemo prikazati slike lijepo,potreban samo url
                        for(Map.Entry<String, String> entry :dohvaceno.getUrl().entrySet())
                        {
                            slike_ucitavanje.add(entry.getValue());
                        }
                    }else {
                        //stvara se prazna lista,da ne dode do greske
                        slike_ucitavanje.clear();
                    }
                ucitaj_pasmine();
                //popis svih pasmina koji se stavljaju u adapter za autofill
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Skidanje nije uspjelo", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }

        });
        new Handler().postDelayed(() -> {
            if(!slike_ucitavanje.isEmpty()){
                inicijalizirajSlider(slike_ucitavanje);
            }
        }, 1000);
    }
    //postavlja vrijednosti varijabli pri ucitvanju iz baze
    @SuppressLint("SetTextI18n")
    private void postavi_vrijednosti() {
        ime.setText(dohvaceno.getNaziv());
        ime.setText(dohvaceno.getNaziv());
        godine.setText(dohvaceno.getGodine().toString().split("\\.")[0]);
        mjesec.setText(dohvaceno.getGodine().toString().split("\\.")[1]);
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
        adapter.setCount(slike_slider.size());
        adapter.slike2(slike_slider);
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(15);
        sliderView.setOnIndicatorClickListener(position -> sliderView.setCurrentPagePosition(position));
    }
    //uplodamo slike i dohvacamorl njihov
    private void uploadFile() {
        //dohvaca se trenutno prikazan popis slika
        slike_ucitavanje=adapter.getList();
        //da spremimo u hasmapu s hashem vec ucitane slike
        HashMap<String,String> vec_ucitane=new HashMap<>();
        ImageList=new HashMap<>();
        HashMap<String,String> slike_iz_baze=new HashMap<>();
        List ImageList_key= new ArrayList();
        List vec_ucitane_key= new ArrayList();
        if (!slike_ucitavanje.isEmpty()) {
            for (int i = 0; i < slike_ucitavanje.size(); i++) {
                if (slike_ucitavanje.get(i).contains("http")) {
                    vec_ucitane.put((i + "_key"), slike_ucitavanje.get(i));
                    vec_ucitane_key.add(Integer.toString(i));
                } else {
                    ImageList.put(Integer.toString(i), Uri.parse(slike_ucitavanje.get(i)));
                    ImageList_key.add(Integer.toString(i));
                }
            }
        }
        //TODO prvo provjeri dal upisana oznaka vec postoji
        //provjeravamo dal postoje odabrane slike iz galerije
        if (!ImageList.isEmpty()) {
            count=0;
            for (int uploads = 0; uploads < ImageList.size(); uploads++) {
                final Uri Image = ImageList.get(ImageList_key.get(uploads));
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(Image));
                assert Image != null;
                mUploadTask = fileReference.putFile(Image);
                // Register observers to listen for when the download is done or if it fails
                mUploadTask.addOnFailureListener(exception -> Toast.makeText(getActivity(), "Upload nije uspio " + exception.toString(), Toast.LENGTH_LONG).show()) .addOnSuccessListener(taskSnapshot -> { ;
                        /*Task<Uri> urlTask =*/ mUploadTask.continueWithTask(task -> {
                           //
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            // Continue with the task to get the download URL
                            return fileReference.getDownloadUrl();
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Log.d("Upload()uploads ", String.valueOf(count));
                                    //spremam u hash mapu
                                    assert downloadUri != null;
                                    slike_iz_baze.put(ImageList_key.get(count).toString(),downloadUri.toString());
                                    count++;
                                    Toast.makeText(getActivity(), "Upload.Dohvacen url", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(getActivity(), "Upload nije uspio", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }).addOnProgressListener(taskSnapshot -> {
                    });

                }
            } else{
            //ako ne postoje odabrane slike iz galerije samo ponovo zapisujemo već skinute,tj uplodane slike
                Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
                slike_map=new HashMap<>(vec_ucitane);
            }
        new Handler().postDelayed(() -> dodajSliku(vec_ucitane,slike_iz_baze), 10000);
    }

    //slika se dodajeu bazu podataka , kao i podaci o zivotinji
    private void dodajSliku(HashMap<String, String> vec_ucitane, HashMap<String, String> imageList){
        slike_map=new HashMap<>();
        for(int i=0; i<(vec_ucitane.size()+imageList.size());i++){
            if(vec_ucitane.containsKey((i+"_key"))){
                slike_map.put((i+"_key"),vec_ucitane.get(i+"_key"));

            }else if(imageList.containsKey(Integer.toString(i))){
                slike_map.put((i+"_key"),(imageList.get(Integer.toString(i))));
            }
        }
        id_vrste =ve.findViewById(vrsta.getCheckedRadioButtonId());
        id_status=ve.findViewById(status.getCheckedRadioButtonId());
        id_spol=ve.findViewById(spol.getCheckedRadioButtonId());
        //pripremamo za upload
        ZivUpload upload2 = new ZivUpload(ime.getText().toString(),oznaka.getText().toString(), id_vrste.getText().toString(),pasmina.getText().toString(),opis.getText().toString(),Float.parseFloat(tezina.getText().toString()),Float.parseFloat(godine.getText().toString()+"."+mjesec.getText().toString()),prefs.getString("uid",""),slike_map,id_spol.getText().toString(),id_status.getText().toString());
        Map<String, Object> postValues2=upload2.toMap();
        Log.d("ucitajSlike():",pasmine.toString());
        for (int i=0; i<pasmine.size();i++){
            String s=pasmine.get(i).getPasminaName();
            Log.d("ucitajSlike()1."+i,s);
            if(!s.equals(pasmina.getText().toString())){
                Log.d("ucitajSlike()2."+i,s);
                PasminaItem pasminaItem=new PasminaItem(pasmina.getText().toString());
                Map<String,Object> postV=pasminaItem.toMap(pasmine.size());
                database= FirebaseDatabase.getInstance();
                mDatabaseRef = database.getReference("Pasmine");
                mDatabaseRef.updateChildren(postV).addOnSuccessListener(aVoid -> Log.d("Uspjel ", "upload")).addOnFailureListener(e -> {
                    Log.d("Error_pasmina: ", Objects.requireNonNull(e.getMessage()));
                });
                }
        }

        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Ziv");
        if(!brisi_slike.isEmpty()) {
            for(int i=0;i<brisi_slike.size();i++) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(brisi_slike.get(i));
                int finalI = i;
                storageReference.delete().addOnSuccessListener(aVoid -> {
                    //Toast.makeText(getContext(), "Slika izbrisana", Toast.LENGTH_SHORT).show();
                    if((finalI+1)==brisi_slike.size()) {
                        Log.d("dodajSliku_count:", String.valueOf(finalI));
                        mDatabaseRef.child(oznaka.getText().toString()).updateChildren(postValues2).addOnSuccessListener(aVoidd -> {
                            Toast.makeText(getContext(), "Uplodano/Ažurirano", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Neuspjel pokušaj uplodanja/ažuriranja", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                });
            }
        }else{
            mDatabaseRef.child(oznaka.getText().toString()).updateChildren(postValues2).addOnSuccessListener(aVoidd -> {
                Toast.makeText(getContext(), "Uplodano/Ažurirano", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Neuspjel pokušaj uplodanja/ažuriranja", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            });
        }
        brisi_slike.clear();
        //resetira se jer smo uplodali slike

    }
    //dohvacamo koja vrsta je slika
    private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //za otvaranje galerije na klik gumba
    private void openFileChooser() {
        /*final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
                        .setAlbumSpanCount(2, 3)
                        .setButtonInAlbumActivity(false)
                        .setCamera(true)
                        .setReachLimitAutomaticClose(true)
                        .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_back_white))
                        .setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                        .setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                        .setAllViewTitle("All")
                        .setMenuAllDoneText("All Done")
                        .setActionBarTitle("Odaberi slike")
                        .textOnNothingSelected("Please select 1 or more!")
                        .textOnImagesSelectionLimitReached("Limit Reached!")
                        .setSelectCircleStrokeColor(Color.BLACK)
                        .startAlbum();
            }
        });
        alertDialog.show();*/
        //ne radi  u alert dialogu,zbog with(this)
        FishBun.with(this)
                .setImageAdapter(new GlideAdapter())
                .setMaxCount(10)
                .setMinCount(1)
                .setPickerSpanCount(5)
                .setActionBarColor(Color.parseColor("#466deb"), Color.parseColor("#466deb"), false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                .setAlbumSpanCount(2, 3)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                .setReachLimitAutomaticClose(true)
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(Objects.requireNonNull(getActivity()), R.drawable.ic_custom_back_white))
                .setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                .setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                .setAllViewTitle("All")
                .setMenuAllDoneText("All Done")
                .setActionBarTitle("Odaberi slike")
                .textOnNothingSelected("Please select 1 or more!")
                .textOnImagesSelectionLimitReached("Limit Reached!")
                .setSelectCircleStrokeColor(Color.BLACK)
                .startAlbum();

    }
    //pokrene se kad dode neki rezultat npr od galerije
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
         switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    if (imageData != null) {
                        //svaki put spremi odabrane slike s mobitela, nakon svakog poziva galeriji, uvijek se resetira
                        ArrayList<Uri> slike = new ArrayList<>(Objects.requireNonNull(imageData.getParcelableArrayListExtra(Define.INTENT_PATH)));
                        //ovdje spremamo sve trenutne slike koje imamo prikazane,nakon svakog odabira u galeriji se dodaju najnovije slike na vec postojece
                        for (int i = 0; i < slike.size(); i++) {
                            slike_ucitavanje.add(slike.get(i).toString());
                        }
                        ArrayList<String> targetList = new ArrayList<>();
                        //radimo privremenu listu u koju spremamo slike kooje smo dobili iz galerije
                        slike.forEach(uri -> targetList.add(uri.toString()));
                        //dodajemo novo odabrane slike u adapter na mjesto na kojem je sliderview bio
                        //kada se odabralo Odaberi slike
                        if (sliderView.getSliderAdapter()!=null){
                           // Log.d("result():", String.valueOf(sliderView.getCurrentPagePosition()));
                            //adapter.addItem(targetList, sliderView.getCurrentPagePosition());
                            //adapter.notifyDataSetChanged();
                            //targetList.clear();
                        }else{
                            inicijalizirajSlider(targetList);
                        }

                    }
                }
        }
    }
}
