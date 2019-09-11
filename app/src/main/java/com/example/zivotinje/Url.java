package com.example.zivotinje;

import java.util.ArrayList;

class Url {
    ArrayList<Url> url;

    public ArrayList<Url> getUrl() {
        return url;
    }

    public void setUrl(ArrayList<Url> url) {
        this.url = url;
    }
    @Override
    public String toString()
    {
        return "ClassPojo [url = "+url.toString()+"]";
    }
}
