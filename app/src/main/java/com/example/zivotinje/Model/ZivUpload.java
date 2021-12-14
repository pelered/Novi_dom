package com.example.zivotinje.Model;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ZivUpload {
    private String naziv;
    private String oznaka;
    private String vrsta;
    private String opis;
    private String pasmina;
    private String id_skl;
    private Float tezina;
    private Float godine;
    private String spol;
    private String status;
    private String naz_skl;
    private String email;
    private String date;
    private String last_date;
    public Map<String,String> url =new HashMap<>();


    public ZivUpload(){

    }


    public ZivUpload(String naziv, String oznaka, String vrsta, String pasmina, String opis, String id_skl, Float tezina, Float godine, String spol, String status, String naz_skl, String email, String date, String last_date, Map<String, String> url) {
        this.naziv = naziv;
        this.oznaka = oznaka;
        this.vrsta = vrsta;
        this.opis = opis;
        this.pasmina = pasmina;
        this.id_skl = id_skl;
        this.tezina = tezina;
        this.godine = godine;
        this.spol = spol;
        this.status = status;
        this.naz_skl = naz_skl;
        this.email = email;
        this.date = date;
        this.last_date = last_date;
        this.url = url;
    }

    @Override
    public String toString() {
        return "ZivUpload{" +
                "naziv='" + naziv + '\'' +
                ", oznaka='" + oznaka + '\'' +
                ", vrsta='" + vrsta + '\'' +
                ", opis='" + opis + '\'' +
                ", pasmina='" + pasmina + '\'' +
                ", id_skl='" + id_skl + '\'' +
                ", tezina=" + tezina +
                ", godine=" + godine +
                ", spol='" + spol + '\'' +
                ", status='" + status + '\'' +
                ", naz_skl='" + naz_skl + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", last_date='" + last_date + '\'' +
                ", url=" + url +
                '}';
    }

    public String getLast_date() {
        return last_date;
    }

    public void setLast_date(String last_date) {
        this.last_date = last_date;
    }

    public String getNaz_skl() {
        return naz_skl;
    }

    public void setNaz_skl(String naz_skl) {
        this.naz_skl = naz_skl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSpol() {
        return spol;
    }

    public String getStatus() {
        return status;
    }

    public Float getGodine() {
        return godine;
    }

    public Float getTezina() {
        return tezina;
    }

    public Map<String, String> getUrl() {
        return url;
    }

    public String getId_skl() {
        return id_skl;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getOpis() {
        return opis;
    }

    public String getOznaka() {
        return oznaka;
    }

    public String getPasmina() {
        return pasmina;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setGodine(Float godine) {
        this.godine = godine;
    }

    public void setId_skl(String id_skl) {
        this.id_skl = id_skl;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public void setPasmina(String pasmina) {
        this.pasmina = pasmina;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
    }

    public void setTezina(Float tezina) {
        this.tezina = tezina;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public void setSpol(String spol) {
        this.spol = spol;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    /*@Override
    public String toString() {
        return super.toString();
    }
*/

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("naziv", naziv);
        result.put("id_skl",id_skl);
        result.put("oznaka",oznaka);
        result.put("vrsta",vrsta);
        result.put("opis",opis);
        result.put("pasmina",pasmina);
        result.put("tezina",tezina);
        result.put("godine",godine);
        result.put("status",status);
        result.put("spol",spol);
        result.put("naz_skl",naz_skl);
        result.put("email",email);
        result.put("date",date);
        result.put("last_date",last_date);
        result.put("url", url);
        return result;
    }
}
