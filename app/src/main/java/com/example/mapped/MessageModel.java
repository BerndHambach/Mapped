package com.example.mapped;

import java.util.Date;

public class MessageModel {

    private String from, message, type, photoUri,  audioUrl;
    private boolean isseen;

    private Date time;

    public MessageModel () {

    }

    public MessageModel(String from, String message, String type, boolean isseen, Date time) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.isseen = isseen;
        this.time = time;

    }

    public MessageModel(String from, String message, String type, boolean isseen, String photoUri, Date time) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.isseen = isseen;
        this.photoUri = photoUri;
        this.time = time;

    }
    public MessageModel(String from, String type, boolean isseen, String photoUri, Date time) {
        this.from = from;
        this.type = type;
        this.isseen = isseen;
        this.photoUri = photoUri;
        this.time = time;

    }



    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
