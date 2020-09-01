package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.example.zivotinje.Adapter.AutoCompletePasminaAdapter;
import com.example.zivotinje.Adapter.SliderAdapter;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private ArrayList<PasminaItem> pasmine,sve_pasmine;
    private HashMap<String,String> slike_map=new HashMap<>();
    private ArrayList<String> slike_ucitavanje_url =new ArrayList<>();
    private HashMap<String,Uri> ImageList = new HashMap<>();
    private int count=0;
    private SliderAdapter adapter;
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
        upload=view.findViewById(R.id.edit_ziv_spremi);
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
        adapter= new SliderAdapter(getActivity());

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


            Log.d("Upload()", String.valueOf(mDatabaseRef.child(oznaka.getText().toString())));

            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                //prvo provjeri dal upisana oznaka vec postoji,samo ucitaj podatke i spremi u true ili false
                if(TextUtils.isEmpty(ime.getText().toString()) ||TextUtils.isEmpty(gram.getText().toString()) || TextUtils.isEmpty(pasmina.getText().toString()) || TextUtils.isEmpty(godine.getText().toString()) || TextUtils.isEmpty(tezina.getText().toString()) || TextUtils.isEmpty(oznaka.getText().toString()) ){
                    Toast.makeText(getActivity(), "Jedino opis može ostat prazan", Toast.LENGTH_LONG).show();
                }else {
                    mDatabaseRef = database.getReference("Ziv").child(oznaka.getText().toString());
                    mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getValue() == null) {
                                // The child doesn't exist
                                uploadFile();
                            }else{
                                Toast.makeText(getActivity(), "Ljubimac s oznakom: "+oznaka.getText().toString()+" već postoji", Toast.LENGTH_LONG).show();

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

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
                //Log.d("ucitaj_pasmine()",dataSnapshot.getValue().toString());
               // Log.d("ucitaj_pasmine()2",privremeno.toString());

                assert privremeno != null;
                //Log.d("ucitajPasmi(): ",privremeno.toString());
                if(privremeno!=null) {
                    for (Map.Entry<String, String> entry : privremeno.entrySet()) {
                        //Log.d("ucitajPasmi()3: ",entry.getValue());
                        pasmine.add(new PasminaItem(entry.getValue()));

                    }


                    sve_pasmine=new ArrayList<>(pasmine);
                    //Log.d("ucitajPasmi()4: ",sve_pasmine.toString()+","+sve_pasmine.size());
                    ispis_pasmina = new AutoCompletePasminaAdapter(Objects.requireNonNull(getContext()), pasmine);
                    pasmina.setAdapter(ispis_pasmina);
                }
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
                        slike_ucitavanje_url.add(entry.getValue());
                    }
                }else {
                    //stvara se prazna lista,da ne dode do greske
                    slike_ucitavanje_url.clear();
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
            if(!slike_ucitavanje_url.isEmpty()){
                inicijalizirajSlider(slike_ucitavanje_url);
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
        tezina.setText(dohvaceno.getTezina().toString().split("\\.")[0]);
        gram.setText(dohvaceno.getTezina().toString().split("\\.")[1]);
        opis.setText(dohvaceno.getOpis());
        oznaka.setText(dohvaceno.getOznaka());
        pasmina.setText(dohvaceno.getPasmina());

        //todo provjeriti da ne izbaci ako ne postoji ziv
        if (dohvaceno.getSpol().equals("M")) {
            RadioButton id = ve.findViewById(R.id.M);
            id.setChecked(true);
        } else if (dohvaceno.getSpol().equals("Ž")) {
            RadioButton id = ve.findViewById(R.id.Z);
            id.setChecked(true);
        }

        if (dohvaceno.getStatus().equals("Udomljen")) {
            RadioButton id = ve.findViewById(R.id.Udomljen);
            id.setChecked(true);
        } else if (dohvaceno.getStatus().equals("Neudomljen")) {
            RadioButton id = ve.findViewById(R.id.Neudomljen);
            id.setChecked(true);
        }

        if (dohvaceno.getVrsta().equals("Pas")) {
            RadioButton id = ve.findViewById(R.id.pas);
            id.setChecked(true);
        } else if (dohvaceno.getVrsta().equals("Macka")) {
            RadioButton id = ve.findViewById(R.id.macka);
            id.setChecked(true);
        }

    }
    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    private void inicijalizirajSlider( ArrayList<String> slike_slider){
        //TODO izmjeniti al kasnije
        adapter= new SliderAdapter(getActivity());
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
        Log.d("ucitajPasmi()5: ",pasmine.toString()+","+pasmine.size());
        //dohvaca se trenutno prikazan popis slika
        if (adapter!=null){
        slike_ucitavanje_url =adapter.getList();}
        //Log.d("uploadFile()2",slike_ucitavanje_url.toString());
        slike_ucitavanje_url=new ArrayList<>();


        //da spremimo u hasmapu s hashem vec ucitane slike
        HashMap<String,String> vec_ucitane=new HashMap<>();
        ImageList=new HashMap<>();
        HashMap<String,String> slike_iz_baze=new HashMap<>();
        List ImageList_key= new ArrayList();
        List vec_ucitane_key= new ArrayList();
        if (!slike_ucitavanje_url.isEmpty()) {
            for (int i = 0; i < slike_ucitavanje_url.size(); i++) {
                if (slike_ucitavanje_url.get(i).contains("http")) {
                    vec_ucitane.put((i + "_key"), slike_ucitavanje_url.get(i));
                    vec_ucitane_key.add(Integer.toString(i));
                } else {
                    ImageList.put(Integer.toString(i), Uri.parse(slike_ucitavanje_url.get(i)));
                    ImageList_key.add(Integer.toString(i));
                }
            }
        }
        //provjeravamo dal postoje odabrane slike iz galerije
        if (!ImageList.isEmpty()) {
            count=0;
            for (int uploads = 0; uploads < ImageList.size(); uploads++) {
                //zamjena nova
                final Uri Image = ImageList.get(ImageList_key.get(uploads));
                final StorageReference fileReference;
                Boolean video;
                if(Image.toString().contains("mp4")){
                    video=true;
                    fileReference= mStorageRef.child(System.currentTimeMillis()
                            + "."+getFileExtension(new File(String.valueOf(Image))));
                }else{
                    video=false;
                    fileReference = mStorageRef.child(System.currentTimeMillis()
                            + "."+getFileExtension(Image));
                }
                if(video){
                    mUploadTask = fileReference.putFile(Uri.fromFile(new File(String.valueOf(Image))));
                }else{
                    mUploadTask = fileReference.putFile(Image);
                }

                //
               /* final Uri Image = ImageList.get(ImageList_key.get(uploads));
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(Image));
                assert Image != null;
                mUploadTask = fileReference.putFile(Image);*/
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
                                    Toast.makeText(getActivity(), "Upload.Dohvacen url: "+count, Toast.LENGTH_LONG).show();
                                    final int K=0;
                                    if(ImageList.size()==count){
                                       // Log.d("ucitajPasmi()6: ",pasmine.toString()+","+pasmine.size());
                                        dodajSliku(vec_ucitane,slike_iz_baze);
                                        Log.d("uploadFile()",slike_iz_baze.toString());

                                    }



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
                Toast.makeText(getActivity(), "Nijedna nova slika nije odabrana", Toast.LENGTH_SHORT).show();
                slike_map=new HashMap<>(vec_ucitane);
                dodajSliku(vec_ucitane,slike_iz_baze);

            }
        //new Handler().postDelayed(() -> dodajSliku(vec_ucitane,slike_iz_baze), 10000);
    }
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
    //dohvacamo koja vrsta je slika
    private String getFileExtension(Uri uri) {
        Log.d("upload_get",uri.toString());
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));


    }
    //slika se dodajeu bazu podataka , kao i podaci o zivotinji
    private void dodajSliku(HashMap<String, String> vec_ucitane, HashMap<String, String> imageList){
        slike_map=new HashMap<>();
        //Log.d("ucitajPasmi()7: ",sve_pasmine.toString()+","+sve_pasmine.size());
        //Log.d("uploadFile()2",vec_ucitane.toString());
        //Log.d("uploadFile()3",imageList.toString());
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
        String created_at;
        String last_updated;
        if(dohvaceno==null){
            created_at = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }else{
            created_at=dohvaceno.getDate();
        }

        last_updated=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //pripremamo za upload
        ZivUpload upload2 = new ZivUpload(ime.getText().toString(),oznaka.getText().toString(),
                id_vrste.getText().toString(),pasmina.getText().toString(),opis.getText().toString(),prefs.getString("uid",""),Float.parseFloat(tezina.getText().toString()+"."+gram.getText().toString()),
                Float.parseFloat(godine.getText().toString()+"."+mjesec.getText().toString()),id_spol.getText().toString(),id_status.getText().toString(),
                prefs.getString("username",""),prefs.getString("email",""),created_at,last_updated,
                slike_map);
        Map<String, Object> postValues2=upload2.toMap();
        //Log.d("ucitajSlike():",sve_pasmine.toString());
        for (int i=0; i<sve_pasmine.size();i++){
            String s=sve_pasmine.get(i).getPasminaName();
            //Log.d("ucitajSlike()1."+i,s);
            if(!s.equals(pasmina.getText().toString())){
                //Log.d("ucitajSlike()2."+i,s);
                PasminaItem pasminaItem=new PasminaItem(pasmina.getText().toString());
                Map<String,Object> postV=pasminaItem.toMap(pasmine.size());
                database= FirebaseDatabase.getInstance();
                mDatabaseRef = database.getReference("Pasmine");
                mDatabaseRef.updateChildren(postV).addOnSuccessListener(aVoid -> Log.d("Pasmine azurirane ", "upload")).addOnFailureListener(e -> {
                    Log.d("Error_pasmina: ", Objects.requireNonNull(e.getMessage()));
                });
                }
        }

        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Ziv");
        //provjeri da li ima slika za brisat
        if(!brisi_slike.isEmpty()) {
            for(int i=0;i<brisi_slike.size();i++) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(brisi_slike.get(i));
                int finalI = i;
                //proba obrisat slike koje su u storagu
                storageReference.delete().addOnSuccessListener(aVoid -> {
                    //Toast.makeText(getContext(), "Slika izbrisana", Toast.LENGTH_SHORT).show();
                    if((finalI+1)==brisi_slike.size()) {
                        Log.d("dodajSliku_count:", String.valueOf(finalI));
                        mDatabaseRef.child(oznaka.getText().toString()).updateChildren(postValues2).addOnSuccessListener(aVoidd -> {
                            Toast.makeText(getContext(), "Uplodano/Ažurirano", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            brisi_slike.clear();
                            PrikazZiv fragment =new PrikazZiv();
                            Bundle args = new Bundle();
                            args.putString("oznaka", upload2.getOznaka());
                            fragment.setArguments(args);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                            ft.replace(R.id.fragment_container, fragment);
                            //ft.addToBackStack("tag_back2");
                            ft.commit();
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
                PrikazZiv fragment =new PrikazZiv();
                Bundle args = new Bundle();
                args.putString("oznaka", upload2.getOznaka());
                fragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                ft.replace(R.id.fragment_container, fragment);
                //ft.addToBackStack("tag_back2");
                ft.commit();
                progressBar.setVisibility(View.INVISIBLE);
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Neuspjel pokušaj uplodanja/ažuriranja", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            });
        }

        //resetira se jer smo uplodali slike

    }
    //dohvacamo koja vrsta je slika
   /* private String getFileExtension(Uri uri) {
        ContentResolver cR = Objects.requireNonNull(getActivity()).getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }*/
    //za otvaranje galerije na klik gumba
    private void openFileChooser() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.custom_dialog,null);
        Button btn_video = (Button)mView.findViewById(R.id.video);
        Button btn_slike = (Button)mView.findViewById(R.id.slike);
        alert.setView(mView);
        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(false);
        btn_video.setOnClickListener(v -> {
            ImagePicker.create(EditZiv.this)
                    .returnMode(ReturnMode.GALLERY_ONLY) // set whether pick and / or camera action should return immediate result or not.
                    .folderMode(true) // folder mode (false by default)
                    .toolbarFolderTitle("Folder") // folder selection title
                    .toolbarImageTitle("Tap to select") // image selection title
                    .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                    .includeVideo(true) // Show video on image picker
                    .onlyVideo(true) // include video (false by default)
                    .single() // single mode
                    .limit(10) // max images can be selected (99 by default)
                    .showCamera(false) // show camera or not (true by default)
                    .enableLog(false) // disabling log
                    .start(); // start image picker activity with request code
            alertDialog.dismiss();
        });
        btn_slike.setOnClickListener(v -> {
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
            alertDialog.dismiss();
        });
        alertDialog.show();

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
                            slike_ucitavanje_url.add(slike.get(i).toString());
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
                            adapter.addItem(targetList, sliderView.getCurrentPagePosition());
                            adapter.notifyDataSetChanged();
                            targetList.clear();
                        }else{
                            inicijalizirajSlider(targetList);

                        }

                    }
                }
                break;


             case 553:
                 if(imageData!=null) {
                     // Get a list of picked images
                     List<Image> images = ImagePicker.getImages(imageData);
                     // or get a single image only
                     Image image = ImagePicker.getFirstImageOrNull(imageData);
                     Log.d("onAcitivityResult:Video", image.getPath());
                /*for (int i = 0; i < slike.size(); i++) {
                    slike_ucitavanje.add(slike.get(i).toString());
                }*/
                     slike_ucitavanje_url.add(image.toString());
                     ArrayList<String> targetList = new ArrayList<>();
                     targetList.add(image.getPath());
                     //radimo privremenu listu u koju spremamo slike kooje smo dobili iz galerije
                     //slike.forEach(uri -> targetList.add(uri.toString()));
                     //dodajemo novo odabrane slike u adapter na mjesto na kojem je sliderview bio
                     //kada se odabralo Odaberi slike
                     if (sliderView.getSliderAdapter() != null) {
                         // Log.d("result():", String.valueOf(sliderView.getCurrentPagePosition()));
                         adapter.addItem(targetList, sliderView.getCurrentPagePosition());
                         adapter.notifyDataSetChanged();
                         targetList.clear();
                     } else {
                         inicijalizirajSlider(targetList);
                     }
                 }
                 break;
         }


    }
}
