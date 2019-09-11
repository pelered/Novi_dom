package com.example.zivotinje;

import java.util.ArrayList;

public class Skloniste {
    // Store the id of the  movie poster
    private String mImageDrawable;
    // Store the name of the movie
    private String mName;
    // Store the release date of the movie

    private ArrayList<String> slike;
    private ArrayList<String> nazivi;


    // Constructor that is used to create an instance of the Movie object
    public Skloniste(String mImageDrawable, String mName) {
        this.mImageDrawable = mImageDrawable;
        this.mName = mName;
    }
    public Skloniste(ArrayList<String> slike,ArrayList<String> nazivi) {
        this.slike=slike;
        this.nazivi=nazivi;
    }

    public ArrayList<String> getNazivi() {
        return nazivi;
    }

    public ArrayList<String> getSlike() {
        return slike;

    }

    public void setNazivi(ArrayList<String> nazivi) {
        this.nazivi = nazivi;
    }

    public void setSlike(ArrayList<String> slike) {
        this.slike = slike;
    }

    public String getmImageDrawable() {
        return mImageDrawable;
    }

    public void setmImageDrawable(String mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }


}
