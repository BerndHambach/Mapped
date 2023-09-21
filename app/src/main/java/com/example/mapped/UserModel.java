package com.example.mapped;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String userId;
    private String userName;
    private String userEmail;
    private String userPassword;

    private String userInfo;

    private String userProfilPhotoUrl;

    private String userStatus;
    private String search;
    private String device_token;

    public UserModel() {

    }

    public UserModel(String userId, String userName, String userEmail, String userPassword, String userInfo, String userProfilPhotoUrl, String userStatus, String search, String device_token) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userInfo = userInfo;
        this.userProfilPhotoUrl = userProfilPhotoUrl;
        this.userStatus = userStatus;
        this.search = search;
        this.device_token = device_token;
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

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
