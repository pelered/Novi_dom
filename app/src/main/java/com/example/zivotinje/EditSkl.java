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
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
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
import com.example.zivotinje.Adapter.PlaceAutoSuggestAdapter;
import com.example.zivotinje.Adapter.SliderAdapter;
import com.example.zivotinje.Model.PasminaItem;
import com.example.zivotinje.Model.Skl;
import com.example.zivotinje.Model.ZivUpload;
import com.google.android.gms.maps.model.LatLng;
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
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
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

public class EditSkl extends Fragment {

    private ProgressBar mProgressBar;

    private SharedPreferences prefs;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase database;
    private StorageTask<UploadTask.TaskSnapshot> mUploadTask;

    private EditText naziv,opis,email,broj;
    private String id;
    private SliderView sliderView;
    //koristi se za prikaz slika poslije dodavanja s mobitela, no prvo se spremi u listu slike iz baze ako ih ima
    private ArrayList<String> slike_ucitavanje= new ArrayList<>();
    private ArrayList<String> slike_priprema_upload= new ArrayList<>();
    //potreban da se ne uplodaju iste slike,pa ako se nije dodala slika s mobitela ovo će biti prazno i nijedna slika se neće uplodati
    private HashMap<String,String> slike_map= new HashMap<>();
    //TODO prouciti kako i radi tocno
    private int i=0;
    private int count=0;
    private Skl dohvaceno;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private SliderAdapter adapter;
    private AutoCompleteTextView autoCompleteTextView;
    //dodano treba obrisat
    private HashMap<String,Uri> ImageList = new HashMap<>();





    private ArrayList<String> slike_ucitavanje_url =new ArrayList<>();


    private int pozicija;
    private ArrayList<String> brisi_slike;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_edit_skl,container,false);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mButtonChooseImage = view.findViewById(R.id.button_choose_image);
        mButtonUpload = view.findViewById(R.id.edit_skl_spremi);
        mTextViewShowUploads = view.findViewById(R.id.obrisi_sliku);
        mProgressBar = view.findViewById(R.id.progress_bar);
        naziv=view.findViewById(R.id.naziv_sk);
        opis=view.findViewById(R.id.opis);
        email = view.findViewById(R.id.email_skl);
        broj=view.findViewById(R.id.broj_tel_edit);
        autoCompleteTextView=view.findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));

        mProgressBar.setVisibility(View.VISIBLE);
        brisi_slike=new ArrayList<>();


        prefs= Objects.requireNonNull(getActivity()).getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        //dohvati id sklonista iz sharedpref
        id=prefs.getString("uid","");

        //baza podataka
        mStorageRef = FirebaseStorage.getInstance().getReference("Sklonista");
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Sklonista");

        sliderView = view.findViewById(R.id.imageSlider);
        adapter= new SliderAdapter(getActivity());
        //email.setText(prefs.getString("email",""));
        mProgressBar.setVisibility(View.GONE);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address : ",autoCompleteTextView.getText().toString());
                LatLng latLng=getLatLngFromAddress(autoCompleteTextView.getText().toString());
                if(latLng!=null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address=getAddressFromLatLng(latLng);
                    if(address!=null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ",""+address.getAddressLine(0));
                        Log.d("Phone : ",""+address.getPhone());
                        Log.d("Pin Code : ",""+address.getPostalCode());
                        Log.d("Feature : ",""+address.getFeatureName());
                        Log.d("More : ",""+address.getLocality());
                    }
                    else {
                        Log.d("Adddress","Address Not Found");
                    }
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });
        //gumb za uplodanje podataka
        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        mButtonUpload.setOnClickListener(v -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                //moramo ovdje spremiti vec skinute slike radi brzine kojom google api radi,jer inace zna spremiti link za lokani storage gdje se nalazi
                if(TextUtils.isEmpty(naziv.getText().toString()) ||TextUtils.isEmpty(autoCompleteTextView.getText().toString()) || TextUtils.isEmpty(broj.getText().toString()) || TextUtils.isEmpty(email.getText().toString())  ){
                    Toast.makeText(getActivity(), "Jedino opis može ostat prazan", Toast.LENGTH_LONG).show();
                }else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    uploadFile();
                }


            }
        });
        //obrisi sliku
        mTextViewShowUploads.setOnClickListener(v -> obrisi_sliku());

            ucitajPodatke();
    }

    private void obrisi_sliku() {
        pozicija=sliderView.getCurrentPagePosition();
        if(adapter.getImage(pozicija).contains("http")) {
            brisi_slike.add(adapter.getImage(pozicija));
        }
        adapter.deleteItem(pozicija);
        adapter.notifyDataSetChanged();
        //Toast.makeText(getActivity(),"Brisem sliku:",Toast.LENGTH_SHORT).show();
    }

    //pokrene se pri ucitavanju fragmenta za dohvacanje podataka
    private void ucitajPodatke(){
        mDatabaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("Skoniste samo", dataSnapshot.toString());
                dohvaceno=dataSnapshot.getValue(Skl.class);
                naziv.setText(dohvaceno.getNaziv());
                if(dohvaceno.getAdresa()!=null){
                    autoCompleteTextView.setText(dohvaceno.getAdresa());
                }
                if(!dataSnapshot.hasChild("opis")){
                    opis.setText(R.string.Opis);
                }else{
                    opis.setText(dohvaceno.getOpis());
                }
                if(!dataSnapshot.hasChild("broj")){
                    broj.setText("Broj telefona");
                }else{
                    broj.setText(dohvaceno.getBroj());
                }
                email.setText(dohvaceno.getEmail());
                if(dataSnapshot.hasChild("url")){
                    Log.d("ucitajPodatke()",dataSnapshot.toString());
                    //spremamo hash mapu
                    //ovo radimo da mozemo prikazati slike lijepo,potreban samo url
                    for(Map.Entry<String, String> entry :dohvaceno.getUrl().entrySet())
                    {
                        slike_ucitavanje_url.add(entry.getValue());
                    }
                    if(!slike_ucitavanje_url.isEmpty()){
                        inicijalizirajSlider(slike_ucitavanje_url);
                    }
                }else {
                    //stvara se prazna lista,da ne dode do greske
                    slike_ucitavanje_url.clear();
                    if(!slike_ucitavanje_url.isEmpty()){
                        inicijalizirajSlider(slike_ucitavanje_url);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Skidanje nije uspjelo.", Toast.LENGTH_SHORT).show();

            }
        });

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Log.d("pod slike2",slike2.toString());
                if(!slike_ucitavanje.isEmpty()){
                    inicijalizirajSlider(slike_ucitavanje);
                }
            }
        }, 1000);*/
    }
    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    private void inicijalizirajSlider( ArrayList<String> slike_slider){
        Log.d("inicijaliziraj():",slike_slider.toString());
        //final SliderAdapter adapter= new SliderAdapter(getActivity());
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
        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });
    }


    //uplodamo slike i dohvacamorl njihov
    private void uploadFile() {
        //dohvaca se trenutno prikazan popis slika
        slike_ucitavanje_url =adapter.getList();
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
    //slika(kao i ostatak podataka) se dodaje u bazu podataka tj njezin url
    private void update_podatke(){
        //jos izmjeniti adresu
        //sprema u objekt podatke koje zelimo uplodati
        dohvaceno.setAdresa(autoCompleteTextView.getText().toString());
        dohvaceno.setEmail(email.getText().toString());
        dohvaceno.setNaziv(naziv.getText().toString());
        dohvaceno.setOpis(opis.getText().toString());
        dohvaceno.setUrl(slike_map);
        //Upload upload2 = new Upload(naziv.getText().toString(),id,adresa,email.getText().toString(),opis.getText().toString(),slike_map);
        //preko objekta se dohvacaju podaci spremljeni za upload i spremaju mapu
        //Map<String, Object> postValues2=upload2.toMap();
        Map<String, Object> postValues2=dohvaceno.toMap();
        //dohvaca se prema id i updata podaci
        mDatabaseRef.child(id).updateChildren(postValues2).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getActivity(),"Upload nije uspio "+ exception.toString(), Toast.LENGTH_LONG).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),"Upload svih podataka je uspio ", Toast.LENGTH_LONG).show();
                mProgressBar.setVisibility(View.INVISIBLE);

            }
        });
        ImageList.clear();
        //slike_map.clear();

    }
    //slika se dodajeu bazu podataka , kao i podaci o zivotinji
    private void dodajSliku(HashMap<String, String> vec_ucitane, HashMap<String, String> imageList){
        slike_map=new HashMap<>();

        //Log.d("uploadFile()2",vec_ucitane.toString());
        //Log.d("uploadFile()3",imageList.toString());
        for(int i=0; i<(vec_ucitane.size()+imageList.size());i++){
            if(vec_ucitane.containsKey((i+"_key"))){
                slike_map.put((i+"_key"),vec_ucitane.get(i+"_key"));

            }else if(imageList.containsKey(Integer.toString(i))){
                slike_map.put((i+"_key"),(imageList.get(Integer.toString(i))));
            }
        }


        //pripremamo za upload
        Skl upload2 = new Skl(id,naziv.getText().toString(),email.getText().toString(),autoCompleteTextView.getText().toString(),broj.getText().toString(),opis.getText().toString(),slike_map);
        Map<String, Object> postValues2=upload2.toMap();

        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Sklonista");
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
                        mDatabaseRef.child(prefs.getString("uid","")).updateChildren(postValues2).addOnSuccessListener(aVoidd -> {
                            Toast.makeText(getContext(), "Uplodano/Ažurirano", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                            brisi_slike.clear();
                            PrikazSkl fragment =new PrikazSkl();
                            Bundle args = new Bundle();
                            args.putString("marker", upload2.getId());
                            fragment.setArguments(args);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                            ft.replace(R.id.fragment_container, fragment);
                            ft.commit();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Neuspjel pokušaj uplodanja/ažuriranja", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.INVISIBLE);
                        });
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                });
            }
        }else{
            mDatabaseRef.child(prefs.getString("uid","")).updateChildren(postValues2).addOnSuccessListener(aVoidd -> {
                Toast.makeText(getContext(), "Uplodano/Ažurirano", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
                PrikazSkl fragment =new PrikazSkl();
                Bundle args = new Bundle();
                args.putString("marker", upload2.getId());
                fragment.setArguments(args);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commit();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Neuspjel pokušaj uplodanja/ažuriranja", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            });
        }


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
        btn_video.setOnClickListener(v -> {
            ImagePicker.create(EditSkl.this)
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
            FishBun.with(EditSkl.this)
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
    //dohvaca alat i lanf od adrese
    private LatLng getLatLngFromAddress(String address){
        Geocoder geocoder=new Geocoder(getContext());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                LatLng latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
//dohvaca adresu od lat i lang
    private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(getContext());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
//pokrene se kad dode neki rezultat npr od galerije


    //novo dodano
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

