package com.unidawgs.le5.clubdawgs;


import java.io.FileNotFoundException;
import java.lang.reflect.Array;
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
    private static HttpClient client = HttpClient.newHttpClient();
    private static String webAPIKey = "AIzaSyBMw_SytjgVmBXtWYVjzApoeLU2QRZzyoA";
    private static String databaseURL = "https://clubdawgs-462c4-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private static String signUpURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/signupNewUser?key=" + webAPIKey;
    private static String signInURL = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/verifyPassword?key=" + webAPIKey;
    private static String storageURL = "https://firebasestorage.googleapis.com/v0/b/clubdawgs-462c4.appspot.com/";

    public static User signUp(String email, String password, String username) throws FirebaseSignUpException{
        JsonObject signUpData = new JsonObject();
        signUpData.addProperty("email", email);
        signUpData.addProperty("password", password);
        signUpData.addProperty("returnSecureToken", true);

        JsonObject databaseData = new JsonObject();
        databaseData.addProperty("username", username);

        try {
            HttpRequest registerReq = HttpRequest.newBuilder().uri(URI.create(signUpURL)).POST(HttpRequest.BodyPublishers.ofString(signUpData.toString())).build();
            HttpResponse<String> registerRes = client.sendAsync(registerReq, HttpResponse.BodyHandlers.ofString()).get();
            System.out.println("Status Code: " + registerRes.statusCode());
            if (registerRes.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(registerRes.body()).getAsJsonObject();
                String idToken = resultData.get("idToken").getAsString();
                String localId = resultData.get("localId").getAsString();
                HttpRequest databaseReq = HttpRequest.newBuilder()
                        .uri(URI.create(databaseURL + "users/" + localId + ".json?auth=" + idToken))
                        .PUT(HttpRequest.BodyPublishers.ofString(databaseData.toString()))
                        .build();
                HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
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

    public static User signIn(String email, String password) {
        JsonObject signInData = new JsonObject();
        signInData.addProperty("email", email);
        signInData.addProperty("password", password);
        signInData.addProperty("returnSecureToken", true);

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(signInURL)).POST(HttpRequest.BodyPublishers.ofString(signInData.toString())).build();
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();

            System.out.println("Status Code: " + response.statusCode());
            if (response.statusCode() == 200) {
                JsonObject resultData = JsonParser.parseString(response.body()).getAsJsonObject();
                String idToken = resultData.get("idToken").getAsString();
                String localId = resultData.get("localId").getAsString();
                HttpRequest databaseReq = HttpRequest.newBuilder()
                        .uri(URI.create(databaseURL + "users/" + localId + ".json?auth=" + idToken))
                        .GET()
                        .build();
                HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
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

    public static void updateChats(String idToken, String roomId, ArrayList<JsonObject> chats) {
        JsonObject info = new JsonObject();
        for (int i = 0; i < chats.size(); i++) {
            info.add(i + "", chats.get(i));
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId +"/chats.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(info.toString()))
                    .build();
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            if (response.statusCode() == 200) {
                System.out.println("Data successfully added");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static ArrayList<JsonObject> getChats(String idToken, String roomId) {
        ArrayList<JsonObject> chats = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/chats.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
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

    public static String getUsername(String localId, String idToken) {
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "users/" + localId + "/username.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                return databaseRes.body().substring(1, databaseRes.body().length()-1);
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return "";
    }

    public static void updateLocation(Player player,String localId, String idToken, String roomId) {
        JsonObject info = new JsonObject();
        info.addProperty("xPos", player.getLeft());
        info.addProperty("yPos", player.getTop());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(databaseURL + "rooms/" + roomId + "/players/" + localId + ".json?auth=" + idToken))
                .PUT(HttpRequest.BodyPublishers.ofString(info.toString()))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public static void getPlayers(String localId, String idToken, String roomId, ArrayList<Player> players) {
        ArrayList<Player> updatedPlayers = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/players.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();
            if (response.statusCode() == 200 && !response.body().contentEquals("null")) {
                JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonObject pos;
                for (String key : result.keySet()) {
                    if (!key.contentEquals(localId)) {
                        pos = result.get(key).getAsJsonObject();
                        updatedPlayers.add(new Player(pos.get("xPos").getAsInt(), pos.get("yPos").getAsInt(), getUsername(key, idToken)));
                    }

                }
                players.clear();
                players.addAll(updatedPlayers);
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static void quitPlayer(String localId, String idToken, String roomId) {
        try {
            JsonObject details = new JsonObject();
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/players.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(details.toString()))
                    .build();
            HttpResponse<String>databaseRes = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                System.out.println("Deleted successfully");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static void postDropBox(String idToken, String roomId, DropBox dropBox) {
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/items/" + dropBox.getFileName() + ".json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(dropBox.getJson().toString()))
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            System.err.println(databaseRes.body());
            if (databaseRes.statusCode() == 200) {
                System.out.println("DropBox successfully added!");
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static void getDropBoxes(String idToken, String roomId, ArrayList<DropBox> dropBoxes) {
        ArrayList<DropBox> updatedDropBoxes = new ArrayList<>();
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/items.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if ((databaseRes.statusCode() == 200)) {
                JsonObject resultData;
                if (databaseRes.body().contentEquals("null")) {
                    resultData = new JsonObject();
                }else {
                    resultData = JsonParser.parseString(databaseRes.body()).getAsJsonObject();
                }
                JsonObject details;
                for (String key: resultData.keySet()) {
                    details = resultData.get(key).getAsJsonObject();
                    updatedDropBoxes.add(new DropBox(
                            "DropBox",
                            key.replace("-", "."),
                            details.get("downloadToken").getAsString(),
                            details.get("xPos").getAsInt(),
                            details.get("yPos").getAsInt())
                    );
                }
                dropBoxes.clear();
                dropBoxes.addAll(updatedDropBoxes);
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static String sendFile(String idToken, String roomId, String fileName, Path filePath) {
        try {
            HttpRequest storageReq = HttpRequest.newBuilder()
                    .uri(URI.create(storageURL + "o?name="+ roomId + "/" + fileName))
                    .POST(HttpRequest.BodyPublishers.ofFile(filePath))
                    .header("Authorization", "Firebase " + idToken)
                    .build();
            HttpResponse<String> storageRes = client.sendAsync(storageReq, HttpResponse.BodyHandlers.ofString()).get();
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

    public static void getFile(String idToken, String roomId, String fileName, String downloadToken) {
        try {
            HttpRequest storageReq = HttpRequest.newBuilder()
                    .uri(URI.create(storageURL + "o/" + roomId + "%2F" + fileName + "?alt=media&token=" + downloadToken))
                    .GET()
                    .header("Authorization", "Firebase " + idToken)
                    .build();
            String resoursePath = Paths.get(Main.class.getResource("").toURI()).toString();
            Path filePath = Paths.get(resoursePath, "files", fileName);
            HttpResponse<Path> storageRes = client.sendAsync(storageReq, HttpResponse.BodyHandlers.ofFile(filePath)).get();
            if (storageRes.statusCode() == 200) {
                System.out.println("Successfully downloaded");
            }
        }catch (ExecutionException | InterruptedException | URISyntaxException err) {
            err.printStackTrace();
        }
    }

    public static void sendRequest(String localId, String idToken, String roomId) {
        JsonObject request = new JsonObject();
        request.addProperty(localId, "Pending");
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/requests.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(request.toString()))
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                System.out.println(databaseRes.body());
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static void acceptRequest(String idToken, String roomId, String toAcceptLocalId) {
        JsonObject accept = new JsonObject();
        accept.addProperty(toAcceptLocalId, "Accepted");
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/requests.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(accept.toString()))
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                databaseRes.body();
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static void rejectRequest(String idToken, String roomId, String toRejectLocalId) {
        JsonObject accept = new JsonObject();
        accept.addProperty(toRejectLocalId, "Rejected");
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/requests.json?auth=" + idToken))
                    .PUT(HttpRequest.BodyPublishers.ofString(accept.toString()))
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                databaseRes.body();
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
    }

    public static ArrayList<String> getRequests(String idToken, String roomId) {
        ArrayList<String> requests = new ArrayList<>();
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/requests.json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                if (!databaseRes.body().contentEquals("null")) {
                    JsonObject requestsJSON = JsonParser.parseString(databaseRes.body()).getAsJsonObject();
                    for (String key : requestsJSON.keySet()) {
                        if (requestsJSON.get(key).getAsString().contentEquals("Pending")) {
                            requests.add(key);
                        }
                    }
                }
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return requests;
    }

    public static String checkRequest(String localId, String idToken, String roomId) {
        try {
            HttpRequest databaseReq = HttpRequest.newBuilder()
                    .uri(URI.create(databaseURL + "rooms/" + roomId + "/requests/" + localId + ".json?auth=" + idToken))
                    .GET()
                    .build();
            HttpResponse<String> databaseRes = client.sendAsync(databaseReq, HttpResponse.BodyHandlers.ofString()).get();
            if (databaseRes.statusCode() == 200) {
                if (!databaseRes.body().contentEquals("null")) {
                    return databaseRes.body().substring(1, databaseRes.body().length()-1);
                }
            }
        }catch (ExecutionException | InterruptedException err) {
            err.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        //User user = firebase.signIn("audrizecruz@gmail.com", "audrizecruz1209");
        User user = Firebase.signIn("johnlloydunida0@gmail.com", "45378944663215");
        System.out.println(Firebase.getRequests(user.getIdToken(), "xabi-r"));
        //System.out.println(firebase.getRequests(user.getIdToken(), "xabi-r"));


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
