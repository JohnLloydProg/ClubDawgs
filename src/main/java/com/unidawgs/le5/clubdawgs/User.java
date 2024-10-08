package com.unidawgs.le5.clubdawgs;

public class User {
    private String username;
    private String idToken;
    private String localId;

    public User(String idToken, String localId, String username) {
        this.idToken = idToken;
        this.localId = localId;
        this.username = username;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public String getLocalId() {
        return this.localId;
    }

    public String getUsername() {
        return this.username;
    }
}
