package com.example.zivotinje;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.zivotinje.Service.Service;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    private TextView log,ime,email;
    private SharedPreferences prefs;
    private CircleImageView slika;
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* DrawerLayout */drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        ;
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        String menuFragment = getIntent().getStringExtra("notifikacija");
        String oznaka=getIntent().getStringExtra("oznakan");

        FragmentTransaction fttt = getSupportFragmentManager().beginTransaction();
        // If menuFragment is defined, then this activity was launched with a fragment selection
        if (menuFragment!=null) {
            // Log.d("Main:",menuFragment);
            //Log.d("Main2",oznaka);
            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (menuFragment.equals("not")) {
                //Log.d("Main3", String.valueOf(menuFragment.equals("not")));
                PrikazZiv prikazZivFragment = new PrikazZiv();
                Bundle args = new Bundle();
                //Log.d("PrikazZivvlas:",vlasnik.getText().toString());
                args.putString("oznaka", oznaka);
                prikazZivFragment.setArguments(args);
                fttt.replace(R.id.fragment_container, prikazZivFragment);
                fttt.addToBackStack("tag_back_main");
                fttt.commit();
            }
        } else {
            // Activity was not launched with a menuFragment selected -- continue as if this activity was opened from a launcher (for example)
            MapFragment standardFragment = new MapFragment();
            fttt.replace(R.id.fragment_container, standardFragment);
            fttt.commit();
        }

       /* FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new MapFragment());
        //ft.addToBackStack("tag_back");
        ft.commit();*/

        View headerView = navigationView.getHeaderView(0);
        View view=navigationView.getHeaderView(0);

        log=headerView.findViewById(R.id.log_click);
        log.setOnClickListener(this);
        email=headerView.findViewById(R.id.email_nav);
        ime=headerView.findViewById(R.id.ime_nav);
        slika=headerView.findViewById(R.id.slika_nav);



        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        if(prefs.getString("username",null)!=null){
            TextView ime_nav,email_nav;
            ImageView profile;
            profile=view.findViewById(R.id.slika_nav);
            ime_nav = view.findViewById(R.id.ime_nav);
            email_nav = view.findViewById(R.id.email_nav);
            ime_nav.setText(prefs.getString("username",null));
            email_nav.setText(prefs.getString("email",null));
            String url=prefs.getString("url",null);
            log.setText(R.string.log_out);
            if (url!=null) {
                Glide.with(this).load(url).apply(RequestOptions.circleCropTransform()).into(profile);
            }else if(prefs.getString("uid",null)==null){
                Glide.with(this).load(R.mipmap.ic_launcher_round).apply(RequestOptions.circleCropTransform()).into(profile);
            }
            Menu menu=navigationView.getMenu();
            MenuItem item ;
            item =menu.findItem(R.id.nav_profil);
            item.setVisible(true);
            if(prefs.getBoolean("skl",false)){
                item =menu.findItem(R.id.nav_dodaj_ziv);
                item.setVisible(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new Search());
            ft.addToBackStack("tag_back_search");
            ft.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new MapFragment());
            ft.addToBackStack("tag_back1");
            ft.commit();        }
        else if (id == R.id.nav_profil) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new ProfileActivity());
            ft.addToBackStack("tag_back2");
            ft.commit();


        } else if (id == R.id.nav_popis_skl) {
            final int random = new Random().nextInt(100);
            IspisSkl fragment=new IspisSkl();
            Bundle args = new Bundle();
            args.putString("skl_ispis", "skl");
            fragment.setArguments(args);
            FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack("tag_main"+random);
            ft.commit();


        }else if (id == R.id.nav_dodaj_ziv) {
            final int random = new Random().nextInt(100);
            EditZiv fragment=new EditZiv();
            Bundle args = new Bundle();
            args.putString("ziv_dodaj", "skl");
            fragment.setArguments(args);
            FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack("tag_main"+random);
            ft.commit();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        if(log.getText().equals("Log in")){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new Login());
            ft.addToBackStack("tag_log");
            ft.commit();
            DrawerLayout drawerr = findViewById(R.id.drawer_layout);
            drawerr.closeDrawer(GravityCompat.START);
        }else{
            Intent mTimerServiceIntent;
            Service mService;
            mService = new Service();
            mTimerServiceIntent = new Intent(this, mService.getClass());
            if (isMyServiceRunning(mService.getClass())) {
                System.out.println("****** [MainActivity] Stopping service...");
                stopService(mTimerServiceIntent);
            }
            //potrebno da se moze odlogirat i s google,da mozes kasnije i druge accounte birati
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(
                    task -> {
                        FragmentTransaction ft =getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, new MapFragment());
                        //ft.addToBackStack("tag_back1_profile");
                        ft.commit();
                    });
            LoginManager.getInstance().logOut();
            SharedPreferences prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            TextView ime_nav,email_nav;
            ImageView photo_nav;
            ime_nav=headerView.findViewById(R.id.ime_nav);
            email_nav=headerView.findViewById(R.id.email_nav);
            //log=headerView.findViewById(R.id.log_click);
            photo_nav=headerView.findViewById(R.id.slika_nav);
            ime_nav.setText(R.string.nav_header_title);
            email_nav.setText(R.string.nav_header_subtitle);
            Glide.with(this).load(R.mipmap.ic_launcher_round).apply(RequestOptions.circleCropTransform()).into(photo_nav);
            log.setText(R.string.log_in);
            Menu menu=navigationView.getMenu();
            MenuItem item ;
            item =menu.findItem(R.id.nav_profil);
            item.setVisible(false);

            //Log.d("dal izbrise","usao sam");
            if(prefs.getBoolean("skl",false)){
                item =menu.findItem(R.id.nav_dodaj_ziv);
                item.setVisible(false);
            }
            editor.clear();
            editor.apply();

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

    }

    }

    // Metoda koja provjerava je li doticni servis aktivan
    // (pretragom svih aktivnih servisa i usporedjivanjem parametra ClassName):
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



}
