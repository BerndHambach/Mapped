package com.example.mapped;

//import com.google.firebase.database.DatabaseReference;

import android.net.Uri;

import java.io.Serializable;

public class Place implements Serializable {
    public String title;
    public String description;
    public Double latitude;
    public Double longitude;
    public String categorie;
    public String startTime;
    public String endTime;
    public String date;
    public String imageUrl;

    public String userID;
    public String placeID;


    public Place() {

    }

    public Place(String title, String description, Double latitude, Double longitude, String categorie, String imageUrl, String startTime, String endTime, String date, String userID) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.categorie = categorie;
       // this.startTime = startTime;
       // this.endTime = endTime;
        this.imageUrl = imageUrl;
        //this.imageUri = imageUri;
        this.startTime = startTime;
        this.endTime = endTime;
        this.date = date;
        this.userID = userID;
    }
    public Place(String title, String description, Double latitude, Double longitude, String imageUrl, String startTime) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }



    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

   /* public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }*/

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


}
