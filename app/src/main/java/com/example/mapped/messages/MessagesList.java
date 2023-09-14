package com.example.mapped.messages;

public class MessagesList {

    private String name, mobile, lastMassage, chatKey;
    private String profilePic;
    private int unseenMassages;

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public MessagesList(String name, String mobile, String lastMassage, String profilePic, int unseenMassages, String chatKey) {
        this.name = name;
        this.mobile = mobile;
        this.lastMassage = lastMassage;
        this.profilePic = profilePic;
        this.unseenMassages = unseenMassages;
        this.chatKey = chatKey;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getLastMassage() {
        return lastMassage;
    }
    public void setLastMassage(String lastMassage) {
        this.lastMassage = lastMassage;
    }
    public int getUnseenMassages() {
        return unseenMassages;
    }
    public void setUnseenMassages(int unseenMassages) {
        this.unseenMassages = unseenMassages;
    }





}
