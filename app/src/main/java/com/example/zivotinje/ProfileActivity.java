package com.example.zivotinje;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.zivotinje.Adapter.ViewPagerAdapter;
import com.example.zivotinje.Model.User;
import com.example.zivotinje.Service.MyService;
import com.example.zivotinje.Tab.FavFragment;
import com.facebook.login.LoginFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class ProfileActivity extends Fragment implements View.OnClickListener {

    private String username,email;
    private String url;
    private EditText u,e,broj;
    private AutoCompleteTextView add,grad,zup;
    private Button upload;
    private ImageButton edit_profile;
    private ImageView profile_photo,photo_nav,tel_sl,email_sl;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private String uid;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FavFragment favFragment;
    private TextView log,ime_nav,email_nav;
    private List<EditText> lista_polja;
    private boolean edit_mode =false;
    private StorageTask<UploadTask.TaskSnapshot> mUploadTask;
    private User user_dohvaceno;
    private Switch aSwitch;


    Intent mTimerServiceIntent;
    private MyService mService;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_profile,container,false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        //prefs.getString("uid",null);
        //Log.d("OnCreate:",prefs.toString());
        //Log.d("OnCreate:1",prefs.getString("url",null));
        u=view.findViewById(R.id.username);
        e=view.findViewById(R.id.email_nav);

        profile_photo=view.findViewById(R.id.photo);

        email_sl=view.findViewById(R.id.send_email);
        edit_profile=view.findViewById(R.id.edit_profile);
        upload=view.findViewById(R.id.uplodaj_profil);
        aSwitch=view.findViewById(R.id.prati_not);
        ///
        System.out.println("****** [MainActivity] onCreate: initializing service intent...");
        mService = new MyService();
        mTimerServiceIntent = new Intent(getActivity(), mService.getClass());
        if(getArguments()==null){
            //ne dolazimo s stranice zivotinje, ali smo registrirani,prikazi nam nas profil
           // Log.d("onViewCre::","Argumentnull");
            postavi_vrijednosti(view);
        }else{
            //Log.d("onViewCre::pref", Objects.requireNonNull(prefs.getString("uid", null)));
            //dolazimo s stranice zivotinje, ali smo registrirani, ujedno provjeravamo jel nas profil to ili tudi
            if(Objects.equals(getArguments().getString("id_vlasnik"), prefs.getString("uid", null))){
                //uid=getArguments().getString("id_vlasnik");
                //Log.d("onViewCre::",getArguments().getString("id_vlasnik")+";"+prefs.getString("uid",null));
                postavi_vrijednosti(view);
            }else{
                //prikazi tudi profil
                //Log.d("onViewCre::", Objects.requireNonNull(getArguments().getString("id_vlasnik")));
                ucitaj_podatke(getArguments().getString("id_vlasnik"));
            }
        }
        ///
        if (!isMyServiceRunning(mService.getClass())) {
            aSwitch.setChecked(false);
        }else
        {
            aSwitch.setChecked(true);
        }
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //Log.d("ProfileF","On");
                if (!isMyServiceRunning(mService.getClass())) {
                    System.out.println("****** [MainActivity] Starting service...");
                    getActivity().startService(mTimerServiceIntent);
                }
            } else {
                //Log.d("ProfileF","Off");
                System.out.println("****** [MainActivity] Stopping service...");
                getActivity().stopService(mTimerServiceIntent);
                // The toggle is disabled
            }
        });

    }

    // Metoda koja provjerava je li doticni servis aktivan
    // (pretragom svih aktivnih servisa i usporedjivanjem parametra ClassName):
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void ucitaj_podatke(String id_vlasnik) {
        aSwitch.setVisibility(View.GONE);
        DatabaseReference  myRef;
        myRef= FirebaseDatabase.getInstance().getReference("Kor");
        myRef.child(id_vlasnik).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user_dohvaceno = dataSnapshot.getValue(User.class);
                prikazi_korisnika();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Error:"+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prikazi_korisnika() {
        TabLayout tab=getView().findViewById(R.id.tab_layout);
        tab.setVisibility(View.GONE);
        edit_profile.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);
        u.setText(user_dohvaceno.getIme());
        e.setText(user_dohvaceno.getEmail());
        email_sl.setOnClickListener(v -> sendEmail());

        if(user_dohvaceno.getUrl().contains("http")){
            Glide.with(getActivity()).load(user_dohvaceno.getUrl()).apply(RequestOptions.circleCropTransform()).into(profile_photo);
        }else{
            Glide.with(getActivity()).load(R.mipmap.ic_launcher_round).apply(RequestOptions.circleCropTransform()).into(profile_photo);
        }

    }

    private void zovi() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + broj.getText().toString()));
        startActivity(dialIntent);
    }

    @SuppressLint("IntentReset")
    private void sendEmail() {
        Log.i("Send email", "");
        // String[] TO = {""};
        // String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        //Log.d("saljem",e.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{e.getText().toString()});
        // emailIntent.putExtra(Intent.EXTRA_CC, CC);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            //finish();
            //Log.d("Finished sending emai.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void postavi_vrijednosti(View view){
        SharedPreferences prefs = requireContext().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        username=prefs.getString("username",null);
        email=prefs.getString("email",null);
        url=prefs.getString("url","");
        uid=prefs.getString("uid",null);
        //Log.d("onView_postavi:",uid);
        String add_privremeno=prefs.getString("add","");
        u.setText(username);
        e.setText(email);

        upload.setVisibility(View.INVISIBLE);
        lista_polja=new ArrayList();
        lista_polja.add(u);
        lista_polja.add(e);


        mAuth=FirebaseAuth.getInstance();
        if(uid!=null){
            edit_profile.setVisibility(View.VISIBLE);
            edit_profile.setOnClickListener(this);
        }else{
            edit_profile.setVisibility(View.INVISIBLE);
        }
        if(!url.equals("")){
            Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(profile_photo);
        }else{
            Glide.with(this).load(R.mipmap.ic_launcher_round).apply(RequestOptions.circleCropTransform()).into(profile_photo);
        }
        edit_profile.setOnClickListener(this);

        //potrebno da se moze odlogirat i s google,da mozes kasnije i druge accounte birati
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        //todo dodati toolbar jer ne nestaje ponekad kad treba
        SharedPreferences prefss = requireContext().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);

       // Log.d("Profile:",prefss.getString("uid",""));

        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        favFragment=new FavFragment();
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);

        viewPagerAdapter.addFragment(favFragment, "Pratim");


        viewPager.setAdapter(viewPagerAdapter);
    }
    @Override
    public void onClick(View view) {
        if(view.equals(edit_profile)){
            if(edit_mode){
                upload.setVisibility(View.INVISIBLE);
                upload.setClickable(false);
                profile_photo.setClickable(false);
                for (int i=0; i<lista_polja.size();i++){
                    lista_polja.get(i).setFocusable(false);
                    lista_polja.get(i).setInputType(InputType.TYPE_NULL);
                    lista_polja.get(i).setFocusableInTouchMode(false);
                }
                edit_mode=false;
            }else{
                upload.setVisibility(View.VISIBLE);
                upload.setOnClickListener(this);
                //upload.setClickable(true);
                profile_photo.setOnClickListener(this);
                for (int i=0; i<lista_polja.size();i++){
                    lista_polja.get(i).setFocusable(true);
                    lista_polja.get(i).setEnabled(true);
                    if(lista_polja.get(i).equals(e)) {
                        lista_polja.get(i).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                    }
                    lista_polja.get(i).setInputType(InputType.TYPE_CLASS_TEXT);
                    lista_polja.get(i).setFocusableInTouchMode(true);
                }

                edit_mode=true;
            }
        }else if(view.equals(profile_photo)){
            FishBun.with(ProfileActivity.this)
                    .setImageAdapter(new GlideAdapter())
                    .setMaxCount(1)
                    .setMinCount(1)
                    .setPickerSpanCount(5)
                    .setActionBarColor(Color.parseColor("#466deb"), Color.parseColor("#466deb"), false)
                    .setActionBarTitleColor(Color.parseColor("#ffffff"))
                    .setAlbumSpanCount(2, 3)
                    .setButtonInAlbumActivity(false)
                    .setCamera(true)
                    .setReachLimitAutomaticClose(true)
                    .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_custom_back_white))
                    .setDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                    .setAllDoneButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_check))
                    .setAllViewTitle("All")
                    .setMenuAllDoneText("All Done")
                    .setActionBarTitle("Odaberi slike")
                    .textOnNothingSelected("Please select 1 or more!")
                    .textOnImagesSelectionLimitReached("Limit Reached!")
                    .setSelectCircleStrokeColor(Color.BLACK)
                    .startAlbum();
        }else if(view.equals(upload)){
            if(!TextUtils.isEmpty(grad.getText().toString()) || !TextUtils.isEmpty(zup.getText().toString()) || !TextUtils.isEmpty(u.getText().toString())
                    || !TextUtils.isEmpty(e.getText().toString()) ) {
                if(!url.contains("http")) {
                    final Uri Image = Uri.parse(url);

                    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("Kor");
                    final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(Image));
                    mUploadTask = fileReference.putFile(Image);
                    mUploadTask.addOnFailureListener(exception -> Toast.makeText(getActivity(), "Upload nije uspio " + exception.toString(), Toast.LENGTH_LONG).show())
                            .addOnSuccessListener(taskSnapshot -> {
                                /*Task<Uri> urlTask =*/
                                mUploadTask.continueWithTask(task -> {
                                    if (!task.isSuccessful()) {
                                        throw Objects.requireNonNull(task.getException());
                                    }
                                    // Continue with the task to get the download URL
                                    return fileReference.getDownloadUrl();
                                }).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        assert downloadUri != null;
                                        url = downloadUri.toString();
                                        azuriraj();
                                    } else {
                                        Toast.makeText(getActivity(), "Upload slike nije uspio", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }).addOnProgressListener(taskSnapshot -> {
                    });
                }else{
                    azuriraj();
                }

            }else {
                Toast.makeText(getActivity(),"Samo adresa i broj mogu ostat prazni",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void azuriraj() {
        DatabaseReference myRef;
        // SharedPreferences prefs = Objects.requireNonNull(getContext()).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        User user_up=new User(uid,u.getText().toString(),url,e.getText().toString());
        //Log.d("onClick::",user_up.toString());
        myRef= FirebaseDatabase.getInstance().getReference("Kor");
        Map<String, Object> postValues2=user_up.toMap();
        myRef.child(uid).updateChildren(postValues2).addOnSuccessListener(aVoid -> {
            Toast.makeText(getActivity(),"Ažuriranje uspjelo",Toast.LENGTH_LONG).show();
            SharedPreferences prefs = requireContext().getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email",e.getText().toString());
            editor.putString("username", u.getText().toString());
            editor.putString("uid",uid);
            editor.putString("url",url);
            editor.putBoolean("hasLogin",true);
            editor.apply();

            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            ime_nav=headerView.findViewById(R.id.ime_nav);
            email_nav=headerView.findViewById(R.id.email_nav);
            photo_nav=headerView.findViewById(R.id.slika_nav);
            ime_nav.setText(username);
            email_nav.setText(email);
            Glide.with(getActivity()).load(url).apply(RequestOptions.circleCropTransform()).into(photo_nav);
        }).addOnCanceledListener(() -> Toast.makeText(getActivity(),"Neuspjelo ažuriranje",Toast.LENGTH_LONG).show());

    }

    //dohvacamo koja vrsta je slika
    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    public void onStart() {
        super.onStart();
        //ako ne dolazi s stranice zivotinje i nije regitriran treba se registrirat jer nema sto vidjeti
        if(getArguments()==null){
            //Log.d("onStart::","argument null");
            FirebaseUser user=mAuth.getCurrentUser();
            if(user == null){
                //Log.d("onStart::","argument null user isto");
                FragmentTransaction ft =(requireActivity()).getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new LoginFragment());
                //ft.addToBackStack("tag_back1_profile");
                ft.commit();
            }
            if (!isMyServiceRunning(mService.getClass())) {
                aSwitch.setChecked(false);
            }else
            {
                aSwitch.setChecked(true);
            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);

        if (requestCode == Define.ALBUM_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (imageData != null) {
                    ArrayList<Uri> slike = new ArrayList<>(Objects.requireNonNull(imageData.getParcelableArrayListExtra(Define.INTENT_PATH)));
                    url = slike.get(0).toString();
                    Glide.with(this).load(slike.get(0).toString()).apply(RequestOptions.circleCropTransform()).into(profile_photo);

                }
            }
        }
    }
}
