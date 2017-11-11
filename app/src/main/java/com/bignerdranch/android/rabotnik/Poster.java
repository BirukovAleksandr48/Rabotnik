package com.bignerdranch.android.rabotnik;

public class Poster {
    private int id;
    private int idCreator;
    private String title;
    private String body;
    private String city;
    private String sallary;
    private String Category;
    private boolean isFavorite;

    public Poster(int id, int idCreator, String title, String body,
                  String city, String sallary, String Category, boolean isFavorite) {
        super();

        this.id = id;
        this.idCreator = idCreator;
        this.title = title;
        this.body = body;
        this.city = city;
        this.sallary = sallary;
        this.Category = Category;
        this.isFavorite = isFavorite;
    }

    public Poster() {  }


    @Override
    public String toString() {
        return "Poster [id=" + id + ", idCreator=" + idCreator + ", title="
                + title + ", body=" + body + ", city=" + city + ", sallary="
                + sallary + ", Category=" + Category + ", isFavorite="
                + isFavorite + "]";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIdCreator() {
        return idCreator;
    }
    public void setIdCreator(int idCreator) {
        this.idCreator = idCreator;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getSallary() {
        return sallary;
    }
    public void setSallary(String sallary) {
        this.sallary = sallary;
    }
    public String getCategory() {
        return Category;
    }
    public void setCategory(String Category) {
        this.Category = Category;
    }
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}