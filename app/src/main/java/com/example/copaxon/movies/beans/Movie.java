package com.example.copaxon.movies.beans;

import java.io.Serializable;

/**
 * Created by CoPaXoN on 26/07/2018.
 */

public class Movie implements Serializable{
    private int id;
    private String title;
    private String body;
    private String url;
    private float rating;
    private boolean isWatched;

    public Movie(int id, String title, String body, String url, float rating, boolean isWatched) {
        this.id = id;
        setTitle(title);
        setBody(body);
        setUrl(url);
        this.rating = rating;
        this.isWatched = isWatched;
    }

    public Movie(String title, String body, String url, float rating, boolean isWatched) {
        setTitle(title);
        setBody(body);
        setUrl(url);
        this.rating = rating;
        this.isWatched = isWatched;
    }

    public Movie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title.replaceAll("\"","");
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body.replaceAll("\"","");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.replaceAll("\"","");
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }



    @Override
    public String toString() {
        return title;

    }
}
