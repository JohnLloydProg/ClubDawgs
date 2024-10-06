package com.unidawgs.le5.clubdawgs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Firebase {
    HttpClient client = HttpClient.newHttpClient();
    String webAPIKey = "AIzaSyClLwYzc4b91BZSwH6NzPxD55qKI3I05Xs";
    String databaseURL = "https://clubdawgs-default-rtdb.asia-southeast1.firebasedatabase.app/";
    String signUpURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/signupNewUser?key=" + this.webAPIKey;
    String signInURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=" + this.webAPIKey;

    public User signUp(String email, String password) {
        JsonObject signUpData = new JsonObject();
        signUpData.addProperty("email", email);
        signUpData.addProperty("password", password);
        signUpData.addProperty("returnSecureToken", true);
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.signUpURL)).POST(HttpRequest.BodyPublishers.ofString(signUpData.toString())).build();
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            if (response.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(response.body()).getAsJsonObject();
                return new User(resultData.get("idToken").getAsString(), resultData.get("localId").getAsString());
            }else {
                System.out.println("Something went wrong while signing up");
            }
        }catch (IOException IOErr) {
            System.out.println("IOException occurred");
        }catch (InterruptedException InterruptErr) {
            System.out.println("Request interrupted");
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
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Status Code: " + response.statusCode());
            if (response.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(response.body()).getAsJsonObject();
                return new User(resultData.get("idToken").getAsString(), resultData.get("localId").getAsString());
            }else {
                System.out.println("Something went wrong while signing up");
            }
        }catch (IOException IOErr) {
            System.out.println("IOException occurred");
        }catch (InterruptedException InterruptErr) {
            System.out.println("Request interrupted");
        }
        return null;
    }

    public void postInfo(String localId, String idToken) {
        JsonObject info = new JsonObject();
        info.addProperty("name", "Testing");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "users/" + localId + ".json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(info.toString()))
                    .build();
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Data successfully added");
            }
        }catch (IOException IOErr) {
            System.out.println("IOException occurred");
        }catch (InterruptedException InterruptErr) {
            System.out.println("Request interrupted");
        }
    }

    public static void main(String[] args) {
        Firebase firebase = new Firebase();
        User user = firebase.signIn("johnlloydunida0@gmail.com", "45378944663215");
        firebase.postInfo(user.getLocalId(), user.getIdToken());
    }
}
