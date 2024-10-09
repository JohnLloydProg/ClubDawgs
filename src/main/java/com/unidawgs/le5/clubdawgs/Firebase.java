package com.unidawgs.le5.clubdawgs;


import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Firebase {
    HttpClient client = HttpClient.newHttpClient();
    String webAPIKey = "AIzaSyBMw_SytjgVmBXtWYVjzApoeLU2QRZzyoA";
    String databaseURL = "https://clubdawgs-462c4-default-rtdb.asia-southeast1.firebasedatabase.app/";
    String signUpURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/signupNewUser?key=" + this.webAPIKey;
    String signInURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=" + this.webAPIKey;
    String storageURL = "https://firebasestorage.googleapis.com/v0/b/clubdawgs-462c4.appspot.com/";

    public User signUp(String email, String password, String username) throws FirebaseSignUpException{
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
                // Parse error code from response
                JsonObject errorObj = JsonParser.parseString(registerRes.body()).getAsJsonObject();
                String errorCode = errorObj.getAsJsonObject("error").get("message").getAsString();
                throw new FirebaseSignUpException(errorCode);  // Throw the custom exception
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
                    // Split the string using the pattern "},"
                    String[] splitData = result.split("\\},");
                    // Clean up the results by adding back the braces
                    for (int i = 0; i < splitData.length; i++) {
                        if(i != splitData.length-1){
                            splitData[i] = splitData[i].replaceFirst("$", "}");
                        }
                        chats.add(JsonParser.parseString(splitData[i]).getAsJsonObject());
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
                        .POST(HttpRequest.BodyPublishers.ofString(players.toString()))
                        .build();
                this.client.sendAsync(request2, HttpResponse.BodyHandlers.ofString()).get();
            }


        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public void postDropBox(String idToken, String roomId, DropBox dropBox) {
        try {
            System.out.println(dropBox.getJson().toString());
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/items.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(dropBox.getJson().toString()))
                    .build();
            HttpResponse<String> databaseRes = this.client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                System.out.println("DropBox successfully added!");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public ArrayList<DropBox> getDropBoxes(String idToken, String roomId) {
        ArrayList<DropBox> dropBoxes = new ArrayList<>();
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(this.databaseURL + "rooms/" + roomId + "/items.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> databaseRes = this.client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(databaseRes.body()).getAsJsonObject();
                JsonObject details;
                for (String key: resultData.keySet()) {
                    details = resultData.get(key).getAsJsonObject();
                    dropBoxes.add(new DropBox(
                            "DropBox",
                            key.replace("-", "."),
                            details.get("downloadToken").getAsString(),
                            details.get("xPos").getAsInt(),
                            details.get("yPos").getAsInt())
                    );
                }
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return dropBoxes;
    }

    public String sendFile(String idToken, String roomId, String fileName, String filePath) {
        try {
            HttpRequest storageReq = HttpRequest.newBuilder()
                    .uri(URI.create(this.storageURL + "o?name="+ roomId + "/" + fileName))
                    .POST(HttpRequest.BodyPublishers.ofFile(Path.of(URI.create("file://" + filePath))))
                    .header("Authorization", "Firebase " + idToken)
                    .build();
            HttpResponse<String> storageRes = this.client.sendAsync(storageReq, HttpResponse.BodyHandlers.ofString()).get();
            System.out.println(storageRes.statusCode());
            if (storageRes.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(storageRes.body()).getAsJsonObject();
                return resultData.get("downloadTokens").getAsString();
            }
            System.out.println(storageRes.body());
        }catch (ExecutionException | InterruptedException | FileNotFoundException err) {
            err.printStackTrace();
        }
        return "";
    }

    public void getFile(String idToken, String roomId, String fileName, String downloadToken) {
        try {
            HttpRequest storageReq = HttpRequest.newBuilder()
                    .uri(URI.create(this.storageURL + "o/" + roomId + "%2F" + fileName + "?alt=media&token=" + downloadToken))
                    .GET()
                    .header("Authorization", "Firebase " + idToken)
                    .build();
            String resoursePath = Paths.get(Main.class.getResource("").toURI()).toString();
            Path filePath = Paths.get(resoursePath, "files", fileName);
            HttpResponse<Path> storageRes = this.client.sendAsync(storageReq, HttpResponse.BodyHandlers.ofFile(filePath)).get();
            if (storageRes.statusCode() == 200) {
                System.out.println("Successfully downloaded");
            }
        }catch (ExecutionException | InterruptedException | URISyntaxException err) {
            err.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Firebase firebase = new Firebase();
        User user = firebase.signIn("audrizecruz@gmail.com", "audrizecruz1209");

        // Upload Creating a DropBox in the system
        //String dowloadToken = firebase.sendFile(user.getIdToken(), "hotdog", "refreshToken.json", Main.class.getResource("refreshToken.json").getPath());
        //DropBox dropBox = new DropBox("DropBox", "refreshToken.json", dowloadToken, 200, 200);
        //firebase.postDropBox(user.getIdToken(), "hotdog", dropBox);

        // Getting DropBoxes in the system
        //ArrayList<DropBox> dropBoxes = firebase.getDropBoxes(user.getIdToken(), "hotdog");
        //System.out.println(dropBoxes.getFirst().getJson().toString());

        // Chatting
        //ArrayList<JsonObject> chats = firebase.getChats(user.getIdToken(), "hotdog"); // Gets a list of chats
        //System.out.println(chats); // chats is a list of JsonObjects
        //JsonObject chat = new JsonObject();
        //chat.addProperty(user.getLocalId(), "Minecraft"); // We create JsonObject for a single chat
        //chats.add(chat);
        //firebase.updateChats(user.getIdToken(), "hotdog", chats);

    }
}
