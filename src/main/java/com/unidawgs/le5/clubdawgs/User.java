package com.unidawgs.le5.clubdawgs;

public class User {
    private String username;
    private String idToken;
    private String localId;
    private String roomId;

    public User(String idToken, String localId) {
        this.idToken = idToken;
        this.localId = localId;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLocalId() {
        return this.localId;
    }
}
