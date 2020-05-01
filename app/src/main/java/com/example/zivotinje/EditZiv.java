package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class EditZiv extends Fragment {
    private EditText ime,pasmina,godine,tezina,opis,oznaka;
    private RadioGroup vrsta;
    private RadioButton radioButton;
    private SliderView sliderView;
    private Button upload,odaberi_sliku;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseRef;
    private StorageTask<com.google.firebase.storage.UploadTask.TaskSnapshot> mUploadTask;
    private StorageTask UploadTask;
    private SharedPreferences prefs;
    int selectedId;
    private View ve;

    private ArrayList<Uri> path;
    private ArrayList<String> slike=new ArrayList<String>();
    private ArrayList<String> slike2=new ArrayList<String>();
    private ArrayList<String> slike_ucitavanje=new ArrayList<String>();
    private HashMap<String,String> slike_map=new HashMap<String,String>();
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();

    private int i=0;
    private int uploads = 0;




    public static Spinner spCompany;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_edit_ziv, container, false);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ime=view.findViewById(R.id.naziv_ziv);
        pasmina=view.findViewById(R.id.pasmina);
        godine=view.findViewById(R.id.godine);
        tezina=view.findViewById(R.id.tezina);
        opis=view.findViewById(R.id.opis_ziv);
        vrsta=view.findViewById(R.id.vrsta);
        sliderView =view.findViewById(R.id.imageSlider);
        upload=view.findViewById(R.id.button_upload);
        odaberi_sliku=view.findViewById(R.id.button_choose_image);
        oznaka=view.findViewById(R.id.oznaka);

        mStorageRef = FirebaseStorage.getInstance().getReference("Ziv");
        database= FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference("Ziv");
        prefs = getActivity().getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        ve=view;

        selectedId = vrsta.getCheckedRadioButtonId();
        radioButton = (RadioButton) view.findViewById(selectedId);

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
                    if(ime==null | pasmina==null || godine==null || tezina==null || opis==null || vrsta==null || oznaka==null){
                        Toast.makeText(getActivity(), "Sva polja moraju biti popunjena", Toast.LENGTH_LONG).show();
                        Log.d("vrsta",radioButton.getText().toString());
                    }else {
                        uploadFile();
                    }
                }

            }
        });




    }
    //uplodamo slike i dohvacamorl njihov
    private void uploadFile() {

        if (!ImageList.isEmpty()) {
            if (!slike2.isEmpty()) {
                int j;
                for (j = 0; j < slike2.size(); j++) {
                    slike_map.put(j + "_key", slike2.get(j));
                    Log.d("slike_map" + j, slike_map.toString());

                }
                i = j;
            }
            for (uploads = 0; uploads < ImageList.size(); uploads++) {
                final Uri Image = ImageList.get(uploads);
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(Image));

                Log.d("reference", fileReference.toString());
                Log.d("naziv slike", System.currentTimeMillis() + "." + getFileExtension(Image));

                //uploadTask = fileReference.putFile(Image);
                mUploadTask = fileReference.putFile(Image);

                // Register observers to listen for when the download is done or if it fails
                mUploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getActivity(), "Upload nije uspio " + exception.toString(), Toast.LENGTH_LONG).show();
                        }
                    })
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.d("podaci", taskSnapshot.getMetadata().toString());
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            //mUploadTask = fileReference.putFile(Image);
                            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
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
                                        Log.d("url trazeni", downloadUri.toString());
                                        //spremam u hash listu
                                        slike_map.put(i + "_key", downloadUri.toString());
                                        i++;
                                        Log.d("mapa key", slike_map.toString());
                                        Toast.makeText(getActivity(), "Upload.Dohvacen url", Toast.LENGTH_LONG).show();
                                        //spremam samo url-ove
                                        slike.add(downloadUri.toString());
                                    } else {
                                        Toast.makeText(getActivity(), "Upload nije uspio", Toast.LENGTH_LONG).show();

                                    }
                                }
                            });
                        }
                    })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                        }
                    });

                }
            } else{
                Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
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
        selectedId = vrsta.getCheckedRadioButtonId();
        radioButton = (RadioButton) ve.findViewById(selectedId);
        Log.d("vrsta",radioButton.getText().toString());

        //String id=prefs.getString("uid","");
        ZivUpload upload2 = new ZivUpload(ime.getText().toString(),oznaka.getText().toString(),radioButton.getText().toString(),pasmina.getText().toString(),opis.getText().toString(),Float.parseFloat(tezina.getText().toString()),Float.parseFloat(godine.getText().toString()),prefs.getString("uid",""),slike_map);
        Map<String, Object> postValues2=upload2.toMap();
        Log.d("mapa slike",slike_map.toString());
        Log.d("mapa",postValues2.toString());

        Log.d("ID uploda",prefs.getString("uid",""));
        mDatabaseRef.child(oznaka.getText().toString()).updateChildren(postValues2);
        slike.clear();
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
        FishBun.with(EditZiv.this).setImageAdapter(new GlideAdapter())
                .setMaxCount(8)
                .setMinCount(1)
                .setActionBarColor(Color.parseColor("#795548"), Color.parseColor("#5D4037"), false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                .setAlbumSpanCount(2, 3)
                .setButtonInAlbumActivity(false)
                .setCamera(true)
                .exceptGif(true)
                .setReachLimitAutomaticClose(true)
                .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_back_white))
                .setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                .setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_custom_ok))
                .setAllViewTitle("All")
                .setMenuAllDoneText("All Done")
                .textOnNothingSelected("Odaberi jednu do najviše 8")
                .startAlbum();

    }
    //pokrene se kad dode neki rezultat npr od galerije
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
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
