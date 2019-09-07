package com.example.zivotinje;

import android.util.ArrayMap;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.List;

public class Adresa {

    public HashMap<String, List> mapa;
public Adresa(){

}
    public Adresa(HashMap mapa){
        this.mapa=mapa;
    }



    public HashMap getMapa() {
        return mapa;
    }

    public void setMapa(HashMap mapa) {
        this.mapa = mapa;
    }

    public void setMapa(String key, DataSnapshot postSnapshot) {
    }

}
