package com.example.zivotinje;

import java.util.HashMap;
import java.util.List;

public class Upload1 {

        private HashMap<String,String> mapa=new HashMap<>();
        private String mName;
        private String mImageUrl;
        private String id;
        private String email;
        private String opis;
        private String naziv;
        private String adresa;
        List<values> vri;

        private HashMap<String,String> map=new HashMap<>();

        public Upload1() {
            //empty constructor needed
        }
        public Upload1(HashMap<String,String> mapa){
            this.mapa = mapa;

        }

        public Upload1(String name, HashMap<String,String> mapa) {
            if (name.trim().equals("")) {
                name = "No Name";
            }

            mName = name;
            this.mapa=mapa;
        }

    public void setMapa(HashMap<String, String> mapa) {
        this.mapa = mapa;
    }

    public HashMap<String, String> getMapa() {
        return mapa;
    }

    public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getNaziv() {
        return naziv;
    }
}
class values{
    

        }
