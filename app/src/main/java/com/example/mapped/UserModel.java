package com.example.mapped;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;

    private String userInfo;

    private String userProfilPhotoUrl;

    public UserModel() {

    }

    public UserModel(String userId, String userName, String userEmail, String userPassword, String userInfo, String userProfilPhotoUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userInfo = userInfo;
        this.userProfilPhotoUrl = userProfilPhotoUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserProfilPhotoUrl() {
        return userProfilPhotoUrl;
    }

    public void setUserProfilPhotoUrl(String userProfilPhotoUrl) {
        this.userProfilPhotoUrl = userProfilPhotoUrl;
    }
}
