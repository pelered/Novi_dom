package com.example.zivotinje.Model;

public class Item {
    private String name, price, image,oznaka;

    public Item(String name, String price, String image,String oznaka) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.oznaka=oznaka;
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", oznaka='" + oznaka + '\'' +
                '}';
    }
}
