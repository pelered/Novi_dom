package com.example.zivotinje;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Root
{
    private String adresa;

    private String naziv;

    private String id;

    private String email;

    private Map<String,String> url;

    private String opis;

    public String getAdresa ()
    {
        return adresa;
    }

    public void setAdresa (String adresa)
    {
        this.adresa = adresa;
    }

    public String getNaziv ()
    {
        return naziv;
    }

    public void setNaziv (String naziv)
    {
        this.naziv = naziv;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }

    public Map<String, String> getUrl() {
        return url;
    }

    public String getOpis ()
    {
        return opis;
    }

    public void setOpis (String opis)
    {
        this.opis = opis;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [adresa = "+adresa+", naziv = "+naziv+", id = "+id+", email = "+email+", url = "+url+", opis = "+opis+"]";
    }

    ///proba
    /*
    public Root(String name, String id, String adresa, String email, String opis, HashMap<String, String> slike_map)
    {
        this.email=email;
        this.url=slike_map;
        this.naziv=name;
        this.id=id;
        this.adresa=adresa;
        this.opis=opis;
    }
    public Root(String name, String id, String adresa,String email, String opis)
    {
        this.email=email;
        this.naziv=name;
        this.id=id;
        this.adresa=adresa;
        this.opis=opis;
    }
    public Root(String name, String id, String adresa,String email )
    {
        this.email=email;
        this.naziv=name;
        this.id=id;
        this.adresa=adresa;
    }
*/
    //proba
    //za upload na bazu podataka
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("naziv",naziv);
        result.put("id",id);
        result.put("adresa",adresa);
        result.put("email",email);
        result.put("opis",opis);
        result.put("url",url);
        return result;
    }
}