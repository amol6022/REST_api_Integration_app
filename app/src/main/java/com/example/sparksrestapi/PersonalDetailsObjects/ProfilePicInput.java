package com.example.sparksrestapi.PersonalDetailsObjects;

public class ProfilePicInput {
    private String photo;
    private int uid;

    public ProfilePicInput(String photo, int uid) {
        this.photo = photo;
        this.uid = uid;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
