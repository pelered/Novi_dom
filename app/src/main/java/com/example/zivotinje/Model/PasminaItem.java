package com.example.zivotinje.Model;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PasminaItem {
    private String pasminaName;


    public PasminaItem(String pasminaName) {
        this.pasminaName = pasminaName;
    }


    public String getPasminaName() {
        return pasminaName;
    }
    @Exclude
    public Map<String, Object> toMap(int pos) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("p"+pos,pasminaName);
        return result;
    }


}