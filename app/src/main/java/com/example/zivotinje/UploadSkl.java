package com.example.zivotinje;

public class UploadSkl {
    private String naziv;
    private String adresa;
    private String id;

    public UploadSkl(){
        //prazan
    }
    public UploadSkl(String id,String naziv,String adresa){
        this.id=id;
        this.naziv=naziv;
        this.adresa=adresa;
    }

    public UploadSkl(String naziv,String adresa){
        this.naziv=naziv;
        this.adresa=adresa;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
}
