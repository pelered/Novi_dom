package com.example.zivotinje;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ZivUpload {
    private String name;
    private String oznaka;
    private String vrsta;
    private String opis;
    private String pasmina;
    private String id_skl;
    private Float tezina;
    private Float godine;
    public Map<String,String> slike_map =new HashMap<>();


    private ArrayList<String> slike=new ArrayList<String>();
    private Map<String,String> slike_skinute=new HashMap<>();
    public ZivUpload(){

    }
    public ZivUpload(String name, String oznaka, String vrsta,String pasmina, String opis, Float tezina,Float godine,String id_skl, HashMap<String,String> slike_map)
    {
        this.slike_map=slike_map;
        this.id_skl=id_skl;
        this.name=name;
        this.oznaka=oznaka;
        this.vrsta=vrsta;
        this.opis=opis;
        this.pasmina=pasmina;
        this.tezina=tezina;
        this.godine=godine;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("naziv",name);
        result.put("id_skl",id_skl);
        result.put("oznaka",oznaka);
        result.put("vrsta",vrsta);
        result.put("opis",opis);
        result.put("pasmina",pasmina);
        result.put("tezina",tezina);
        result.put("godine",godine);
        result.put("url",slike_map);

        return result;
    }
}
