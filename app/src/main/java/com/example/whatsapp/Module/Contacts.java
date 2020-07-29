package com.example.whatsapp.Module;

public class Contacts {

    public String userName, userStatus, image;

    public Contacts() {
    }

    public Contacts(String userName, String userStatus, String image) {
        this.userName = userName;
        this.userStatus = userStatus;
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
