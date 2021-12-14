package com.example.zivotinje.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Skl
{
    private String adresa;
    private String naziv;
    private String id;
    private String email;
    private Map<String,String> url=new HashMap<>();
    private String opis;
    private String broj;

    public Skl(String uid, String mnaziv,String email, String madresa, String broj) {
        this.id=uid;
        this.naziv=mnaziv;
        this.adresa=madresa;
        this.email=email;
        this.broj = broj;
    }
    public Skl(String uid, String mnaziv,String email, String madresa, String broj,String opis,Map<String,String> url) {
        this.id=uid;
        this.naziv=mnaziv;
        this.adresa=madresa;
        this.email=email;
        this.broj = broj;
        this.opis=opis;
        this.url=url;
    }

    public Skl(){}

    public String getBroj() {
        return broj;
    }

    public void setBroj(String broj) {
        this.broj = broj;
    }

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
        return "ClassPojo [adresa = "+adresa+", naziv = "+naziv+", id = "+id+", email = "+email+", broj ="+broj+",url = "+url+", opis = "+opis+"]";
    }


    //za upload na bazu podataka
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("naziv",naziv);
        result.put("id",id);
        result.put("adresa",adresa);
        result.put("email",email);
        result.put("broj",broj);
        result.put("opis",opis);
        result.put("url",url);
        return result;
    }
}