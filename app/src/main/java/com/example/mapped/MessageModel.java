package com.example.mapped;

public class MessageModel {

    private String from, message, type;
    private boolean isseen;

    public MessageModel () {

    }

    public MessageModel(String from, String message, String type, boolean isseen) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.isseen = isseen;
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
}
