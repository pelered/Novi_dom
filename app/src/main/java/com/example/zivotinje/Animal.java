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
import android.os.Parcelable;
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

public class Animal  extends Fragment {

    private ProgressBar mProgressBar;

    private SharedPreferences prefs;
    private String adresa;


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    //private StorageTask uploadTask;

    private AutocompleteSupportFragment autocompleteFragment;


    private ArrayList<Uri> ImageList = new ArrayList<>();
    //private DatabaseReference databaseReference;
    private EditText naziv;
    private EditText opis;


    //ArrayList<Parcelable> path2=new ArrayList<>();


    private SliderView sliderView;
    private ArrayList<String> slike= new ArrayList<>();
    private ArrayList<String> slike2= new ArrayList<>();
    private ArrayList<String> slike_ucitavanje= new ArrayList<>();


    private HashMap<String,String> slike_map= new HashMap<>();
    //private Map<String,String> slike_skinute=new HashMap<String,String>();
    private int i=0;




    String id;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_animal,container,false);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button mButtonChooseImage = view.findViewById(R.id.button_choose_image);
        Button mButtonUpload = view.findViewById(R.id.button_upload);
        TextView mTextViewShowUploads = view.findViewById(R.id.text_view_show_uploads);
        mProgressBar = view.findViewById(R.id.progress_bar);
        prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        id=prefs.getString("uid","");
        mStorageRef = FirebaseStorage.getInstance().getReference("Sklonista");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sliderView = view.findViewById(R.id.imageSlider);

        mDatabaseRef = database.getReference("Sklonista");
        //Log.d("referenca",mDatabaseRef.toString());
        naziv=view.findViewById(R.id.naziv_sk);
        opis=view.findViewById(R.id.opis);
        EditText email = view.findViewById(R.id.email_skl);
        Log.d("Email",prefs.getString("email",""));
        //if(!prefs.getString("email","").isEmpty()){
            email.setText(prefs.getString("email",""));



        //mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

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
                    uploadFile();
                }

            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                autocompleteFragment.setText(place.getLatLng().toString());
                adresa=place.getAddress();
                Log.d("pod adresa",adresa);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
        ///za adresu uhvatiti
        if(autocompleteFragment!=null){

            ucitajPodatke();

        }


    }

//pokrene se pri ucitavanju fragmenta
    private void ucitajPodatke(){
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("pod",postSnapshot.child("naziv").toString());
                    if(postSnapshot.getKey().equals(id)){
                        String broj= String.valueOf(postSnapshot.child("url").getChildrenCount());
                        int br=Integer.parseInt(broj);

                        for(int j=0; j<br;j++){
                            slike2.add(postSnapshot.child("url").child(j+"_key").getValue().toString());
                            slike_ucitavanje.add(postSnapshot.child("url").child(j+"_key").getValue().toString());
                            Log.d("put0",slike_ucitavanje.toString());
                            Log.d("pod slika url2",slike2.toString());

                            Log.d("pod slika url", postSnapshot.child("url").child(j+"_key").getValue().toString());

                        }
                        /*
                        Upload up=postSnapshot.child("url").getValue(Upload.class);
                        String myString=postSnapshot.child("url").getValue().toString();
                        HashMap<String, Integer> map = convertToHashMap(myString);
*/

                        naziv.setText(postSnapshot.child("naziv").getValue().toString());
                        if(postSnapshot.child("adresa").getValue()!=null){
                            if(autocompleteFragment!=null){
                                autocompleteFragment.setText(postSnapshot.child("adresa").getValue().toString());
                            }
                            adresa=postSnapshot.child("adresa").getValue().toString();
                        }
                        if(postSnapshot.child("opis").getValue()==null){
                            opis.setText("Opis");

                        }else{
                            opis.setText(postSnapshot.child("opis").getValue().toString());
                        }

                    }
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("pod slike2",slike2.toString());
                if(!slike2.isEmpty()){
                    inicijalizirajSlider(slike2);
                }

            }
        }, 1000);

    }
    //inicijalizacija slidera nakon sto se slike skinu,ako ih ima
    public void inicijalizirajSlider( ArrayList<String> slike2){
        final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
        adapter.setCount(slike2.size());
        adapter.slike2(slike2);
        adapter.broj(slike2.size());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(15);
        //sliderView.startAutoCycle();

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
//uplodamo slike i dohvacamo rl njihov
    private void uploadFile() {

        if (!ImageList.isEmpty()) {
            if(!slike2.isEmpty()){
                int j;
                for(j=0;j<slike2.size();j++){
                    slike_map.put(j+"_key",slike2.get(j));
                    Log.d("slike_map"+j,slike_map.toString());

                }
                i=j;
            }
            int uploads = 0;
            for (uploads =0; uploads < ImageList.size(); uploads++) {
                final Uri Image  = ImageList.get(uploads);
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        + "." + getFileExtension(Image));

                 Log.d("reference",fileReference.toString());
                Log.d("naziv slike",System.currentTimeMillis()+ "." + getFileExtension(Image));

                //uploadTask = fileReference.putFile(Image);
                mUploadTask = fileReference.putFile(Image);

                // Register observers to listen for when the download is done or if it fails
                mUploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(),"Upload nije uspio "+ exception.toString(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(0);
                            }
                        }, 0);
                        Log.d("podaci",taskSnapshot.getMetadata().toString());
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
                                    Log.d("url trazeni",downloadUri.toString());
                                    slike_map.put(i+"_key",downloadUri.toString());
                                    i++;
                                    Log.d("mapa key",slike_map.toString());
                                    Toast.makeText(getActivity(), "Upload uspio", Toast.LENGTH_LONG).show();
                                    slike.add(downloadUri.toString());
                                } else {
                                    Toast.makeText(getActivity(), "Upload nije uspio", Toast.LENGTH_LONG).show();

                                }
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgressBar.setProgress((int) progress);
                    }
                });;

            }
        } else {
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
    //slika se dodaje u bazu podataka tj njezin url
    private void dodajSliku(){
        //String id=prefs.getString("uid","");
        Upload upload2 = new Upload(naziv.getText().toString(),id,adresa,prefs.getString("email",""),opis.getText().toString(),slike_map);
        //Upload upload = new Upload(naziv.getText().toString(),id,adresa,opis.getText().toString(),slike);
        //Map<String, Object> postValues =upload.toMap();
        Map<String, Object> postValues2=upload2.toMap();
        Log.d("mapa slike",slike_map.toString());
        Log.d("mapa",postValues2.toString());
        //Map<String, Object> childUpdates = new HashMap<>();
        //String uploadId = mDatabaseRef.child(prefs.getString("uid","")).push().getKey();
        //childUpdates.put(uploadId,postValues);

        Log.d("ID uploda",prefs.getString("uid",""));
        mDatabaseRef.child(prefs.getString("uid","")).updateChildren(postValues2);
        slike.clear();
        ImageList.clear();
    }
//za otvaranje galerije na klik gumba
    private void openFileChooser() {
        FishBun.with(Animal.this).setImageAdapter(new GlideAdapter())
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
        /*
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
        //startActivityForResult(intent, PICK_IMAGE_REQUEST);

         */
    }
//pokrene se kad dode neki rezultat npr od galerije
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    // path = imageData.getStringArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<String>) on <0.6.2
                    ImageList=imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    ArrayList<Parcelable> path = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);

                    //path=imageData.getStringArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<Uri>) on 0.6.2 and later
                    Log.d("Proba", path.toString());
                    if(!slike_ucitavanje.isEmpty()) {
                        for (int i = 0; i < path.size(); i++) {
                            slike_ucitavanje.add(path.get(i).toString());

                            //Log.d("put2",path.get(i).toString());

                        }
                        Log.d("put",slike_ucitavanje.toString());

                    }
                    final SliderAdapterExample adapter= new SliderAdapterExample(getActivity());
                    if(slike_ucitavanje.isEmpty()){
                        adapter.setCount(path.size());
                        adapter.slike(path);
                        adapter.broj(path.size());
                        Log.d("usao sam", path.toString());

                    }else{
                        adapter.setCount(slike_ucitavanje.size());
                        adapter.slike2(slike_ucitavanje);
                        adapter.broj(slike_ucitavanje.size());
                    }

                    sliderView.setSliderAdapter(adapter);
                    sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                    sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                    sliderView.setIndicatorSelectedColor(Color.WHITE);
                    sliderView.setIndicatorUnselectedColor(Color.GRAY);
                    sliderView.setScrollTimeInSec(15);
                    //sliderView.startAutoCycle();

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

