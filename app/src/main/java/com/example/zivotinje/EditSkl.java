package com.example.zivotinje;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.zivotinje.Adapter.PlaceAutoSuggestAdapter;
import com.example.zivotinje.Adapter.SliderAdapterExample;
import com.example.zivotinje.Model.Root;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    //spremljeni veze na slike odabrane s mobitela
    private ArrayList<Uri> ImageList = new ArrayList<>();
    //private String adresa;
    private EditText naziv;
    private EditText opis;
    private EditText email;
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
    private Root dohvaceno;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private SliderAdapterExample adapter;
    private AutoCompleteTextView autoCompleteTextView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_edit_skl,container,false);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mButtonChooseImage = view.findViewById(R.id.button_choose_image);
        mButtonUpload = view.findViewById(R.id.button_upload);
        mTextViewShowUploads = view.findViewById(R.id.obrisi_sliku);
        mProgressBar = view.findViewById(R.id.progress_bar);
        naziv=view.findViewById(R.id.naziv_sk);
        opis=view.findViewById(R.id.opis);
        email = view.findViewById(R.id.email_skl);
        autoCompleteTextView=view.findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));

        mProgressBar.setVisibility(View.VISIBLE);

        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        //dohvati id sklonista iz sharedpref
        id=prefs.getString("uid","");

        //baza podataka
        mStorageRef = FirebaseStorage.getInstance().getReference("Sklonista");
        database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Sklonista");

        sliderView = view.findViewById(R.id.imageSlider);
        adapter= new SliderAdapterExample(getActivity());
        email.setText(prefs.getString("email",""));
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
        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getActivity(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    //moramo ovdje spremiti vec skinute slike radi brzine kojom google api radi,jer inace zna spremiti link za lokani storage gdje se nalazi
                    int j = 0;
                    //TODO otkriti zasto ako ne odaberemo nijednu sliku ne posalje linkove samo na postojece
                    if(!ImageList.isEmpty()){
                        for(j=0; j< (slike_ucitavanje.size()-ImageList.size()); j++){
                            slike_map.put(j+"_key", slike_ucitavanje.get(j));
                            Log.d("Ispisujem prvo"+j,slike_map.toString());
                        }
                    }

                    upload(j);
                }
            }
        });
        //obrisi sliku
        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obrisi_sliku();
            }
        });

            ucitajPodatke();
    }

    private void obrisi_sliku() {
        Toast.makeText(getActivity(),"Brisem sliku:",Toast.LENGTH_SHORT).show();
    }

    //pokrene se pri ucitavanju fragmenta za dohvacanje podataka
    private void ucitajPodatke(){
        mDatabaseRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d("Skoniste samo", dataSnapshot.toString());
                if(dataSnapshot.hasChild("url") & dataSnapshot.hasChild("opis")){
                    dohvaceno=dataSnapshot.getValue(Root.class);
                }else if(dataSnapshot.hasChild("url")) {
                    dohvaceno=dataSnapshot.getValue(Root.class);
                }else{
                    dohvaceno=dataSnapshot.getValue(Root.class);
                }
                if(dataSnapshot.hasChild("url")){
                    //zamijenit listu s mapom, radi jednostavnosti
                    for(Map.Entry<String, String> entry :dohvaceno.getUrl().entrySet())
                    {
                        slike_ucitavanje.add(entry.getValue());
                    }
                }
                naziv.setText(dohvaceno.getNaziv());
                if(dohvaceno.getAdresa()!=null){
                    autoCompleteTextView.setText(dohvaceno.getAdresa());
                }
                if(!dataSnapshot.hasChild("opis")){
                    opis.setText(R.string.Opis);
                }else{
                    opis.setText(dohvaceno.getOpis());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Skidanje nije uspjelo.", Toast.LENGTH_SHORT).show();

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
    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    private void inicijalizirajSlider( ArrayList<String> slike_slider){
        //final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
        adapter.setCount(slike_slider.size());
        adapter.slike2(slike_slider);
        adapter.broj(slike_slider.size());
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
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //uplodamo slike i dohvacamo url njihov,jedino ako smo odabrali neku sliku s mobitela
    private void upload(int j) {
        //ulazi u if ako smo odabrali neki file/slike
        if (!ImageList.isEmpty()) {
            if(!slike_ucitavanje.isEmpty()){
                i=j;
            }
            for (int uploads =0; uploads < ImageList.size(); uploads++) {
                mProgressBar.setVisibility(View.VISIBLE);
                final Uri Image  = ImageList.get(uploads);
                //sprema u storage sliku s nazivom prema milisekundama
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+ "." + getFileExtension(Image));
                mUploadTask = fileReference.putFile(Image);
                // Register observers to listen for when the download is done or if it fails
                final int finalUploads = uploads;
                mUploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(),"Upload nije uspio "+ exception.toString(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("Ispisujem*: ",taskSnapshot.toString());

                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                         mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                Log.d("Ispisujem**: ",task.toString());
                                // Continue with the task to get the download URL
                                return fileReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    slike_map.put(i+"_key",downloadUri.toString());
                                    i++;
                                    //Toast.makeText(getActivity(), "Upload slike"+i+" uspio", Toast.LENGTH_LONG).show();
                                    Log.d("Ispisujem***: ",slike_map.toString());
                                    //Log.d("Ispisujem****: ", String.valueOf(count));
                                    if (count==ImageList.size()-1){
                                        update_podatke();
                                        count=0;
                                    }
                                    count++;
                                } else {
                                    Toast.makeText(getActivity(), "Upload slike nije uspio", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        //TODO napraviti da prikazuje tocno kolko je ostalo
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                        //TODO srediti progress
                        //Log.d("Napredak", String.valueOf(progress));
                    }

                });
            }
        } else {
            Toast.makeText(getActivity(), "Slika nije bila odabrana.Ostali izmjeneni podaci će se uplodati", Toast.LENGTH_LONG).show();
            //potrebno spremit u slike map, makar nema nista zbog naredbe za update. inace dijete url potpuno nestalo
            if(!slike_ucitavanje.isEmpty()){
                int k;
                for(k=0; k< slike_ucitavanje.size(); k++){
                    slike_map.put(j+"_key", slike_ucitavanje.get(k));
                    //Log.d("slike_map"+j,slike_map.toString());
                    //Log.d("Ispisuje*****","TU sam dosao");
                }
                i=k;
            }
            update_podatke();
        }
        //problem za mozda rjesit->trebao bi ici na upload ostali podataka tek kad sve slike rjesi,ovo je privremeno


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
//za otvaranje galerije na klik gumba
    private void openFileChooser() {
        FishBun.with(EditSkl.this).setImageAdapter(new GlideAdapter())
                .setMaxCount(8)
                .setMinCount(1)
                .setActionBarColor(Color.parseColor("#795548"), Color.parseColor("#5D4037"), true)
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
                .textOnNothingSelected("Odaberi jednu do najviše 8")
                .startAlbum();

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        Log.d("Slike*",imageData.toString());
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ImageList=imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    //Log.d("proba2",ImageList.toString());
                    //ako trenutno dohvacena lista slika nije prazna, slike se dodaju na kraj liste i tako prikazuju
                    if(!slike_ucitavanje.isEmpty()) {
                        for (int i = 0; i < ImageList.size(); i++) {
                            slike_ucitavanje.add(ImageList.get(i).toString());
                        }
                        //Log.d("put",slike_ucitavanje.toString());

                    }

                    adapter= new SliderAdapterExample(getActivity());
                    //ako skloniste nema slika prikazat ce samo odabrane trenutno,prije uploda
                    if(slike_ucitavanje.isEmpty()){
                        adapter.setCount(ImageList.size());
                        adapter.slike(ImageList);

                    }else{
                        adapter.setCount(slike_ucitavanje.size());
                        adapter.slike2(slike_ucitavanje);
                    }
                    Log.d("Slider_rez: ",slike_ucitavanje.toString());

                    //dodati da prima Klasu sa svim podacima, umjesto da setamo s setcount i .slike2()
                    //postavljanje slidera
                    sliderView.setSliderAdapter(adapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                    sliderView.setIndicatorSelectedColor(Color.WHITE);
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.setScrollTimeInSec(15);

                    //indikator na kojoj smo slici rednim broj u slideru
                    sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
                        @Override
                        public void onIndicatorClicked(int position) {
                            sliderView.setCurrentPagePosition(position);
                        }
                    });
                    break;
                }
        }
    }
}

