package com.unidawgs.le5.clubdawgs;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Firebase {
    HttpClient client = HttpClient.newHttpClient();
    String webAPIKey = "AIzaSyBMw_SytjgVmBXtWYVjzApoeLU2QRZzyoA";
    String databaseURL = "https://clubdawgs-462c4-default-rtdb.asia-southeast1.firebasedatabase.app/";
    String signUpURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/signupNewUser?key=" + this.webAPIKey;
    String signInURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=" + this.webAPIKey;
    String storageURL = "https://firebasestorage.googleapis.com/v0/b/clubdawgs-462c4.appspot.com/";

    public User signUp(String email, String password, String username) {
        JsonObject signUpData = new JsonObject();
        signUpData.addProperty("email", email);
        signUpData.addProperty("password", password);
        signUpData.addProperty("returnSecureToken", true);

        JsonObject databaseData = new JsonObject();
        databaseData.addProperty("username", username);

        try {
            HttpRequest registerReq = HttpRequest.newBuilder().uri(URI.create(this.signUpURL)).POST(HttpRequest.BodyPublishers.ofString(signUpData.toString())).build();
            HttpResponse<String> registerRes = this.client.sendAsync(registerReq, HttpResponse.BodyHandlers.ofString()).get();
            System.out.println("Status Code: " + registerRes.statusCode());
            if (registerRes.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(registerRes.body()).getAsJsonObject();
                String idToken = resultData.get("idToken").getAsString();
                String localId = resultData.get("localId").getAsString();
                HttpRequest databaseReq = HttpRequest.newBuilder()
                        .uri(URI.create(this.databaseURL + "users/" + localId + ".json?auth=" + idToken))
                        .PUT(HttpRequest.BodyPublishers.ofString(databaseData.toString()))
                        .build();
                HttpResponse<String> databaseRes = this.client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
                if (databaseRes.statusCode() == 200) {
                    return new User(idToken, localId, username);
                }
            }else {
                System.out.println("Something went wrong while signing up");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return null;
    }

    public User signIn(String email, String password) {
        JsonObject signInData = new JsonObject();
        signInData.addProperty("email", email);
        signInData.addProperty("password", password);
        signInData.addProperty("returnSecureToken", true);

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.signInURL)).POST(HttpRequest.BodyPublishers.ofString(signInData.toString())).build();
            HttpResponse<String> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();

            System.out.println("Status Code: " + response.statusCode());
            if (response.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(response.body()).getAsJsonObject();
                String idToken = resultData.get("idToken").getAsString();
                String localId = resultData.get("localId").getAsString();
                HttpRequest databaseReq = HttpRequest.newBuilder()
                        .uri(URI.create(this.databaseURL + "users/" + localId + ".json?auth=" + idToken))
                        .GET()
                        .build();
                HttpResponse<String> databaseRes = this.client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
                if (databaseRes.statusCode() == 200) {
                    JsonObject databaseResultData = JsonParser.parseString(databaseRes.body()).getAsJsonObject();
                    return new User(idToken, localId, databaseResultData.get("username").getAsString());
                }
            }else {
                System.out.println("Something went wrong while signing up");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return null;
    }

    public void updateChats(String idToken, String roomId, ArrayList<JsonObject> chats) {
        JsonObject info = new JsonObject();
        for (int i = 0; i < chats.size(); i++) {
            info.add(i + "", chats.get(i));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "rooms/" + roomId +"/chats.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(info.toString()))
                    .build();
            HttpResponse<String> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            if (response.statusCode() == 200) {
                System.out.println("Data successfully added");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public ArrayList<JsonObject> getChats(String idToken, String roomId) {
        ArrayList<JsonObject> chats = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/chats.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            if (response.statusCode() == 200) {
                if (!response.body().contentEquals("null")) {
                    String result = response.body().substring(1, response.body().indexOf("]"));
                    for (String chat : result.split(",")) {
                        chats.add(JsonParser.parseString(chat).getAsJsonObject());
                    }
                }
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }

        return chats;
    }

    public String getUsername(String localId, String idToken) {
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "users/" + localId + "/username.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> databaseRes = this.client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                return databaseRes.body();
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return "";
    }

    public void updateLocation(Player player,String localId, String idToken, String roomId) {
        JsonObject info = new JsonObject();
        info.addProperty("xPos", player.getXPos());
        info.addProperty("yPos", player.getYPos());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/players/" + localId + ".json?auth=" + idToken))
                .PUT(HttpRequest.BodyPublishers.ofString(info.toString()))
                .build();
        this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public void getPlayers(ArrayList<Player> players, String localId, String idToken, String roomId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/players.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            if (response.statusCode() == 200) {
                JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject pos;
                players.clear();
                for (String key : result.keySet()) {
                    if (!key.contentEquals(localId)) {
                        pos = result.get(key).getAsJsonObject();
                        players.add(new Player(pos.get("xPos").getAsInt(), pos.get("yPos").getAsInt(), this.getUsername(key, idToken)));
                    }

                }
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public void quitPlayer(String localId, String idToken, String roomId) {
        try {
            HttpRequest request1 = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/players.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> response = this.client.sendAsync(request1, HttpResponse.BodyHandlers.ofString()).get();
            if (response.statusCode() == 200) {
                JsonObject players = JsonParser.parseString(response.body()).getAsJsonObject();
                players.remove(localId);
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/players.json?auth=" + idToken))
                        .PUT(HttpRequest.BodyPublishers.ofString(players.toString()))
                        .build();
                this.client.sendAsync(request2, HttpResponse.BodyHandlers.ofString()).get();
            }


        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public void sendFile(String idToken, String roomId, String fileName, String filePath) {
        try {
            HttpRequest storageReq = HttpRequest.newBuilder()
                    .uri(URI.create(this.storageURL + "o?name="+ roomId + "/" + fileName))
                    .POST(HttpRequest.BodyPublishers.ofFile(Path.of(URI.create("file://" + filePath))))
                    .header("Authorization", "Firebase " + idToken)
                    .build();
            HttpResponse<String> storageRes = this.client.sendAsync(storageReq, HttpResponse.BodyHandlers.ofString()).get();
            System.out.println(storageRes.statusCode());
            System.out.println(storageRes.body());
        }catch (ExecutionException | InterruptedException | FileNotFoundException err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Firebase firebase = new Firebase();
        User user = firebase.signIn("johnlloydunida0@gmail.com", "45378944663215");
        firebase.sendFile(user.getIdToken(), "hotdog", "refreshToken.json", Main.class.getResource("refreshToken.json").getPath());
        //ArrayList<JsonObject> chats = firebase.getChats(user.getIdToken(), "hotdog"); // Gets a list of chats
        //System.out.println(chats); // chats is a list of JsonObjects
        //JsonObject chat = new JsonObject();
        //chat.addProperty(user.getLocalId(), "Minecraft"); // We create JsonObject for a single chat
        //chats.add(chat);
        //firebase.updateChats(user.getIdToken(), "hotdog", chats);

    }
}
