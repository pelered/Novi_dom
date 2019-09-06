package com.example.zivotinje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Upload {
    private String mName;
    private String mImageUrl;
    private String mId;
    public Map<String, Boolean> stars = new HashMap<>();
    private int count=0;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String name,String id,String imageUrl){
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mImageUrl = imageUrl;
        mId=id;
    }
    public Upload(String imageUrl){
        mImageUrl=imageUrl;
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
    @Exclude
    public Map<String, Object> toMap() {
        count =count+1;
        HashMap<String, Object> result = new HashMap<>();
        result.put("imageUrl"+count, mImageUrl);

        result.put("stars", stars);

        return result;
    }



}