package com.example.zivotinje;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    private TextView log,ime,email;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new MapActivity());
        ft.addToBackStack("tag_back");
        ft.commit();

        View headerView = navigationView.getHeaderView(0);

        log=(TextView) headerView.findViewById(R.id.log_click);
        log.setOnClickListener(this);
        email=(TextView)headerView.findViewById(R.id.email);
        ime=(TextView)headerView.findViewById(R.id.ime);

        prefs = getSharedPreferences("shared_pref_name", MODE_PRIVATE);
        if(prefs.contains("hasLogin")) {
            //Log.d("test","ima podatke za prikazat");
            email.setText(prefs.getString("email", ""));
            ime.setText(prefs.getString("username", ""));
            log.setText("Log out");
            //Log.d("UID",prefs.getString("uid",""));
        }else{
            log.setText("Log in");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
            ft.replace(R.id.fragment_container, new MapActivity());
            ft.addToBackStack("tag_back2");
            ft.commit();        }
        else if (id == R.id.nav_gallery) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new EditSkl());
            ft.addToBackStack("tag_back3");
            ft.commit();

        } else if (id == R.id.nav_slideshow) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container,new Ispis());
            ft.addToBackStack("tag_back4");
            ft.commit();

        } else if (id == R.id.nav_tools) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, new EditZiv());
            ft.addToBackStack("tag_back4");
            ft.commit();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if(log.getText().toString().equals("Log in")) {
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            if(prefs.contains("hasLogin")) {
                log.setText("Log out");
            }else{
                log.setText("Log in");
            }
        }else if(log.getText().toString().equals("Log out")){
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);
            log.setText("Log in");


        }


    }





}
