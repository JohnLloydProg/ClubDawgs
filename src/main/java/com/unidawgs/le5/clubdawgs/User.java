package com.unidawgs.le5.clubdawgs;

public class User {
    private String idToken;
    private String localId;

    public User(String idToken, String localId) {
        this.idToken = idToken;
        this.localId = localId;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public String getLocalId() {
        return this.localId;
    }
}
