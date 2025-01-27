package com.example.wheredesktop.Objects;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe {@code AdminManager} gestisce gli amministratori all'interno dell'applicazione.
 *
 * <p>Questa classe consente di gestire una lista di amministratori e di verificare le credenziali di accesso degli utenti
 * attraverso il metodo {@link #validateCredentials(String, String)}. Inoltre, permette di gestire un amministratore attivo
 * tramite il campo {@link #activeAdminName}.</p>
 */
public class AdminManager {
    public String token;
    /**
     * Classe interna che rappresenta un amministratore.
     * Ogni amministratore ha un nome utente e una password.
     */
    public static class Admin {
        private String username;
        private String password;

        /**
         * Costruttore della classe {@code Admin}.
         *
         * @param username Il nome utente dell'amministratore.
         * @param password La password dell'amministratore.
         */
        public Admin(String username, String password) {
            this.username = username;
            this.password = password;
        }

        /**
         * Restituisce il nome utente dell'amministratore.
         *
         * @return Il nome utente dell'amministratore.
         */
        public String getUsername() {
            return username;
        }

        /**
         * Restituisce la password dell'amministratore.
         *
         * @return La password dell'amministratore.
         */
        public String getPassword() {
            return password;
        }
    }

    private List<Admin> adminList;
    private String activeAdminName;

    /**
     * Imposta il nome dell'amministratore attivo.
     *
     * @param username Il nome utente dell'amministratore da impostare come attivo.
     */
    public void setActiveAdminName(String username){
        activeAdminName = username;
    }

    /**
     * Restituisce il nome dell'amministratore attivo.
     *
     * @return Il nome dell'amministratore attivo.
     */
    public String getActiveAdminName(){
        return activeAdminName;
    }

    /**
     * Costruttore della classe {@code AdminManager}.
     * Inizializza la lista degli amministratori.
     */
    public AdminManager() {

    }

    /**
     * Verifica le credenziali di un amministratore.
     *
     * @param username Il nome utente da verificare.
     * @param password La password da verificare.
     * @return {@code true} se il nome utente e la password corrispondono a un amministratore esistente,
     *         {@code false} altrimenti.
     */
    public boolean validateCredentials(String username, String password) {
        try {
            // Create an OkHttpClient instance
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            // Build the form-urlencoded request body
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            String bodyContent = "username=" + username + "&password=" + password;
            RequestBody body = RequestBody.create(mediaType, bodyContent);

            // Build the request
            Request request = new Request.Builder()
                    .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/auth/login.php")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {

                    String responseBody = response.body().string();

                    // Parse the JSON response
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if (jsonResponse.getString("status").equals("success")) {
                        JSONObject data1 = jsonResponse.getJSONObject("data");
                        token = data1.getString("token");
                    }


                    //now read all users, find user with this username and check if is admin
                    client = new OkHttpClient().newBuilder().build();

                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    body = RequestBody.create(JSON, "{}");

                    request = new Request.Builder()
                            .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/getUsers.php")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer " + token)
                            .build();

                    try {
                        Response responseUsers = client.newCall(request).execute();

                        if (responseUsers.isSuccessful() && responseUsers.body() != null) {
                            responseBody = responseUsers.body().string();

                            // Parse the response as JSON
                            jsonResponse = new JSONObject(responseBody);

                            // Check if the status is success
                            if ("success".equals(jsonResponse.getString("status"))) {
                                System.out.println("Message: " + jsonResponse.getString("message"));

                                // Navigate to the data.users array
                                JSONObject data = jsonResponse.getJSONObject("data");
                                JSONArray usersArray = data.getJSONArray("users");

                                // Iterate over users and print their details
                                for (int i = 0; i < usersArray.length(); i++) {
                                    JSONObject user = usersArray.getJSONObject(i);

                                    int id = user.getInt("id");
                                    String usernameUser = user.getString("username");
                                    String role = user.getString("role");

                                    if(usernameUser.equals(username) && role.equals("admin")){
                                        return true;
                                    }
                                }
                            } else {
                                System.out.println("Failed to fetch users: " + jsonResponse.getString("message"));
                            }
                        } else {
                            System.err.println("HTTP request failed with code: " + response.code());
                            if (response.body() != null) {
                                System.err.println("Response body: " + response.body().string());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                } else {
                    System.out.println("HTTP Error: " + response.code() + " - " + response.message());
                    return false;
                }
            }
        } catch (Exception e) {
            // Catch and log any exceptions
            System.out.println("Exception occurred: " + e.getMessage());
            return false;
        }
    }
}