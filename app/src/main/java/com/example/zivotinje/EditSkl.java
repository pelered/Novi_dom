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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zivotinje.Adapter.SliderAdapterExample;
import com.example.zivotinje.Model.Root;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import java.util.Arrays;
import java.util.HashMap;
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
    private AutocompleteSupportFragment autocompleteFragment;
    //spremljeni veze na slike odabrane s mobitela
    private ArrayList<Uri> ImageList = new ArrayList<>();
    private String adresa;
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
        ////za adresu dohvatiti

        // Initialize Places.
        Places.initialize(getContext(), "AIzaSyAyVddfVCAcVub30s1xsJLiaRCMx70EbtA");
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG,Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));
        //autocompleteFragment.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE)
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setHint("Adresa");
        //mice search icon
        ImageView searchIcon = (ImageView)((LinearLayout)autocompleteFragment.getView()).getChildAt(0);
        searchIcon.setVisibility(View.GONE);

        //ako odabaremo adresu,spremi ju
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                autocompleteFragment.setText(place.getLatLng().toString());
                adresa=place.getAddress();
                //Log.d("pod adresa",adresa);
            }
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getActivity(),"Neuspjeli odabir adrese ", Toast.LENGTH_SHORT).show();
            }
        });
        ///ako fragment je ucitan
        if(autocompleteFragment!=null){
            ucitajPodatke();
        }
    }

    private void obrisi_sliku() {
        Toast.makeText(getActivity(),"Brisem sliku:",Toast.LENGTH_SHORT).show();
    }

    //pokrene se pri ucitavanju fragmenta za dohvacanje podataka
    private void ucitajPodatke(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Log.d("pod",postSnapshot.toString());
                    if(postSnapshot.getKey().equals(id)){
                        //Log.d("neuspjeh", String.valueOf(postSnapshot.hasChild("url")));
                        //ako se jos nisu url,opis postavili
                        if(postSnapshot.hasChild("url") & postSnapshot.hasChild("opis")){
                            dohvaceno=postSnapshot.getValue(Root.class);
                        }else if(postSnapshot.hasChild("url")) {
                            dohvaceno=postSnapshot.getValue(Root.class);
                        }else{
                            dohvaceno=postSnapshot.getValue(Root.class);
                        }
                        if(postSnapshot.hasChild("url")){
                            for(Map.Entry<String, String> entry :dohvaceno.getUrl().entrySet())
                            {
                                slike_ucitavanje.add(entry.getValue());
                            }
                        }
                        naziv.setText(dohvaceno.getNaziv());
                        if(dohvaceno.getAdresa()!=null){
                            if(autocompleteFragment!=null){
                                autocompleteFragment.setText(dohvaceno.getAdresa());
                            }
                            adresa=dohvaceno.getAdresa();
                        }
                        if(!postSnapshot.hasChild("opis")){
                            opis.setText(R.string.Opis);
                        }else{
                            opis.setText(dohvaceno.getOpis());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "Skidanje nije uspjelo", Toast.LENGTH_SHORT).show();
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
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(0);
                            }
                        }, 0);
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
        dohvaceno.setAdresa(adresa);
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
                Log.d("Ispisuje*****","TU sam dosao");
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
                .exceptGif(true)
                .setReachLimitAutomaticClose(true)
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_back_white))
                //.setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                //.setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                .setAllViewTitle("All")
                .setMenuAllDoneText("All Done")
                .textOnNothingSelected("Odaberi jednu do najviše 8")
                .startAlbum();

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
                    final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
                    //ako skloniste nema slika prikazat ce samo odabrane trenutno,prije uploda
                    if(slike_ucitavanje.isEmpty()){
                        adapter.setCount(ImageList.size());
                        adapter.slike(ImageList);
                        adapter.broj(ImageList.size());

                    }else{
                        adapter.setCount(slike_ucitavanje.size());
                        adapter.slike2(slike_ucitavanje);
                        adapter.broj(slike_ucitavanje.size());
                    }
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

