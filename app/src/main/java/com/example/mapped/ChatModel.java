package com.example.mapped;

public class ChatModel {

    private String sender;
    private String seceiver;
    private String message;

    public ChatModel(String seceiver, String message) {
        this.seceiver = seceiver;
        this.message = message;
    }

    public ChatModel() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSeceiver() {
        return seceiver;
    }

    public void setSeceiver(String seceiver) {
        this.seceiver = seceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
