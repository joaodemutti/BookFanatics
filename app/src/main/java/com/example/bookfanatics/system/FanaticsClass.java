package com.example.bookfanatics.system;

import java.io.Serializable;

public class FanaticsClass implements Serializable {
    public Library library;
    public Login login;
    public int isFavorite, hasShared, locationOpened;
    public int id,libraryID,loginID;

    public FanaticsClass(int loginID,int libraryID)
    {
        id =0;
        this.libraryID = libraryID;
        this.loginID = loginID;
    }

    public FanaticsClass(int id,int libraryID,int loginID){
        this.id = id;
        this.libraryID = libraryID;
        this.loginID = loginID;
    }
    public FanaticsClass(){
        libraryID = 0;
        id = 0;
        loginID = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLibraryID() {
        return libraryID;
    }

    public void setLibraryID(int libraryID) {
        this.libraryID = libraryID;
    }

    public int getLoginID() {
        return loginID;
    }

    public void setLoginID(int loginID) {
        this.loginID = loginID;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getHasShared() {
        return hasShared;
    }

    public void setHasShared(int hasShared) {
        this.hasShared = hasShared;
    }

    public int getLocationOpened() {
        return locationOpened;
    }

    public void setLocationOpened(int locationOpened) {
        this.locationOpened = locationOpened;
    }
}
