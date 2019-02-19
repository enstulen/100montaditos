package com.uc3m.a100montaditos;

public class MenuItem {
    String name;
    String description_spanish;
    String description_english;
    double price;
    String imageUrl;
    int favorites;
    String type;

    public MenuItem( ){

    }

    public MenuItem(String name, String description_spanish, String description_english, double price, String imageUrl, int favorites, String type) {
        this.name = name;
        this.description_spanish = description_spanish;
        this.description_english = description_english;
        this.price = price;
        this.imageUrl = imageUrl;
        this.favorites = favorites;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription_spanish() {
        return description_spanish;
    }

    public void setDescription_spanish(String description_spanish) {
        this.description_spanish = description_spanish;
    }

    public String getDescription_english() {
        return description_english;
    }

    public void setDescription_english(String description_english) {
        this.description_english = description_english;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
