package com.unidawgs.le5.clubdawgs.objects;

public class User {
    private String username;
    private String idToken;
    private String localId;
    private int currency;

    public User(String idToken, String localId, String username, int currency) {
        this.idToken = idToken;
        this.localId = localId;
        this.username = username;
        this.currency = currency;
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

    public int getCurrency() {
        return this.currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }
}
