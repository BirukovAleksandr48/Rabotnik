package com.bignerdranch.android.rabotnik;

public class Poster {
    private int id;
    private int idCreator;
    private String title;
    private String body;
    private String city;
    private String sallary;
    private int idCategory;

    public Poster(int id, int idCreator, String title, String body,
                  String city, String sallary, int idCategory) {
        super();
        this.id = id;
        this.idCreator = idCreator;
        this.title = title;
        this.body = body;
        this.city = city;
        this.sallary = sallary;
        this.idCategory = idCategory;
    }
    public Poster() {
        super();
        this.id = 0;
        this.idCreator = 0;
        this.title = "";
        this.body = "";
        this.city = "";
        this.sallary = "";
        this.idCategory = 0;
    }
    @Override
    public String toString() {
        return "Resume [id=" + id + ", idCreator=" + idCreator + ", title="
                + title + ", body=" + body + ", city=" + city + ", sallary="
                + sallary + ", idCategory=" + idCategory + "]";
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
    public int getIdCategory() {
        return idCategory;
    }
    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }
}
