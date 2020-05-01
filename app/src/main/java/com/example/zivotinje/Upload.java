package com.example.zivotinje;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mId;
    private String adresa;
    private String opis;
    private String email;
    private int count=0;
    private Map<String,String> slike_map;

    public Upload() {
        //empty constructor needed
    }
    Upload(String name, String id, String adresa, String email, String opis, HashMap<String, String> slike_map)
    {
        this.email=email;
        this.slike_map=slike_map;
        this.mName=name;
        mId=id;
        this.adresa=adresa;
        this.opis=opis;
    }
    public Upload(String name, String id, String adresa,String email, String opis)
    {
        this.email=email;
        this.mName=name;
        mId=id;
        this.adresa=adresa;
        this.opis=opis;
    }
    public Upload(String name, String id, String adresa,String email )
    {
        this.email=email;
        this.mName=name;
        mId=id;
        this.adresa=adresa;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setSlike_map(Map<String, String> slike_map) {
        this.slike_map = slike_map;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId(){return mId;}

    public void setId(String id) {
        mId = id;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public void count(){
        count =count+1;
    }

    //za upload na bazu podataka
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("naziv",mName);
        result.put("id",mId);
        result.put("adresa",adresa);
        result.put("email",email);
        result.put("opis",opis);
        result.put("url",slike_map);
        return result;
    }



}