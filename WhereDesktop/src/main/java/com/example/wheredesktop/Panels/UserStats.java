/**
 * La classe {@code UserStats} fornisce funzionalità per la gestione degli utenti all'interno dell'applicazione.
 * Include funzionalità per visualizzare, creare, modificare ed eliminare gli utenti.
 */
package com.example.wheredesktop.Panels;

import com.example.wheredesktop.Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.StageStyle;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

class UserStats {

    /**
     * Radice dell'interfaccia utente, rappresentata da un {@link AnchorPane}.
     */
    private final AnchorPane root;

    /**
     * Tabella che mostra gli utenti presenti.
     */
    private final TableView<User> userTable = new TableView<>();

    /**
     * Lista di utenti gestita come {@link ArrayList}.
     */
    public ArrayList<User> users = new ArrayList<>();

    /**
     * Lista osservabile per aggiornare dinamicamente la tabella degli utenti.
     */
    private ObservableList<User> userObservableList;

    /**
     * Token di autenticazione utilizzato per effettuare richieste protette.
     */
    private String token;

    /**
     * Costruttore della classe {@code UserStats}. Configura la tabella degli utenti e i pulsanti per la gestione.
     *
     * @param token Token di autenticazione utilizzato per le richieste.
     */
    public UserStats(String token) {
        this.token = token;
        root = new AnchorPane();

        // Creazione dei pulsanti
        Button addUserButton = new Button("Crea Utente");
        addUserButton.getStyleClass().add("glow-button-normal");
        addUserButton.setOnAction(event -> addUser());

        // Creazione dell'HBox per i pulsanti
        HBox buttonBox = new HBox(15);
        buttonBox.getChildren().addAll(addUserButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        // Creazione della tabella utenti
        VBox graybox = createUsersTable();

        // Layout principale
        VBox layout = new VBox(50);
        layout.setPadding(new Insets(20, 10, 0, 10));
        layout.getChildren().addAll(buttonBox, graybox);
        layout.setAlignment(Pos.CENTER);

        root.getChildren().add(layout);
        AnchorPane.setTopAnchor(layout, 0.0);
        AnchorPane.setLeftAnchor(layout, 0.0);
        AnchorPane.setRightAnchor(layout, 0.0);
    }

    /**
     * Restituisce la radice dell'interfaccia.
     *
     * @return un {@link AnchorPane} che rappresenta la radice dell'interfaccia.
     */
    public AnchorPane getRoot() {
        return root;
    }

    /**
     * Legge gli utenti dal database remoto e li memorizza nella lista di utenti.
     */
    public void readUsers() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, "{}");

        Request request = new Request.Builder()
                .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/getUsers.php")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);

                if ("success".equals(jsonResponse.getString("status"))) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    JSONArray usersArray = data.getJSONArray("users");

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);

                        int id = user.getInt("id");
                        String username = user.getString("username");
                        String email = user.getString("email");
                        String role = user.getString("role");
                        int isBanned = user.getInt("is_banned");
                        int score = user.getInt("score");
                        String createdAt = user.getString("created_at");

                        users.add(new User(username, email, "", id, createdAt, role, score, isBanned));
                    }
                } else {
                    System.out.println("Failed to fetch users: " + jsonResponse.getString("message"));
                }
            } else {
                System.err.println("HTTP request failed with code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        userObservableList = FXCollections.observableArrayList(users);
    }

    /**
     * Crea una tabella per visualizzare gli utenti e aggiunge colonne personalizzate.
     *
     * @return Un {@link VBox} contenente la tabella degli utenti.
     */
    public VBox createUsersTable()
    {
        //CREATE BOX THAT CONTAINS USERS-TABLE
        VBox grayBox = new VBox(15);
        grayBox.setAlignment(Pos.CENTER);
        grayBox.setMaxWidth(1600);
        grayBox.setMaxHeight(1200);
        grayBox.setPadding(new Insets(25, 30, 25, 30));
        grayBox.setStyle("-fx-background-color: #4E4D4D; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        userTable.setPrefHeight(800);

        //BOTTONE
        TableColumn<User, String> usernameColumn = new TableColumn<>("Nome Utente");
        usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        usernameColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        emailColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        TableColumn<User, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        idColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        TableColumn<User, String> createdAtColumn = new TableColumn<>("Created At");
        createdAtColumn.setCellValueFactory(cellData -> cellData.getValue().createdAtProperty());
        createdAtColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        TableColumn<User, Boolean> bannedColumn = new TableColumn<>("Banned");
        bannedColumn.setCellValueFactory(cellData -> cellData.getValue().bannedProperty());
        bannedColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().ruoloProperty());
        roleColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        TableColumn<User, String> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
        scoreColumn.setStyle("-fx-alignment: CENTER;"); // Center content

        // Apply the same style for custom buttons
        TableColumn<User, Void> modifyButton = new TableColumn<>("Modifica Utente");
        modifyButton.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<User, Void>() {
                private final Button btn = new Button("Modifica");

                {
                    btn.getStyleClass().add("glow-button-table");
                    btn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        modificaUser(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            };
            cell.setStyle("-fx-alignment: CENTER;"); // Center button
            return cell;
        });

        TableColumn<User, Void> deleteButton = new TableColumn<>("Elimina Utente");
        deleteButton.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<User, Void>() {
                private final Button btn = new Button("Elimina");

                {
                    btn.getStyleClass().add("glow-button-table");
                    btn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        eliminaUser(user);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            };
            cell.setStyle("-fx-alignment: CENTER;"); // Center button
            return cell;
        });

        // Add the columns to the table
        userTable.getColumns().addAll(idColumn, emailColumn, usernameColumn, createdAtColumn, bannedColumn, roleColumn, scoreColumn, modifyButton, deleteButton);

        //RESIZE DELLE COLONNE
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usernameColumn.setResizable(true);
        emailColumn.setResizable(true);
        idColumn.setResizable(true);
        createdAtColumn.setResizable(true);
        roleColumn.setResizable(true);
        scoreColumn.setResizable(true);

        readUsers();

        userTable.setItems(userObservableList);
        userTable.setFixedCellSize(45);

        userTable.setId("customTableView");

        grayBox.getChildren().addAll(userTable);

        return grayBox;
    }

    /**
     * Mostra una finestra di dialogo per creare un nuovo utente.
     */
    private void addUser()
    {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
        dialogStage.setTitle("Crea User");

        // Create a layout for the pop-up
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add UI elements
        Label title = new Label("Crea User");
        title.getStyleClass().add("where-label");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("input-field");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("input-field");

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("input-field");

        Button addButton = new Button("Crea");
        addButton.getStyleClass().add("glow-button");
        addButton.setAlignment(Pos.CENTER);

        gridPane.add(usernameLabel, 0, 1);
        gridPane.add(usernameField, 1, 1);

        gridPane.add(emailLabel, 0, 2);
        gridPane.add(emailField, 1, 2);

        gridPane.add(passwordLabel, 0, 3);
        gridPane.add(passwordField, 1, 3);

        gridPane.add(addButton, 1, 4);


        //Add button clicked
        addButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String creationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            //EMAIL VALIDATION
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);

            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (pattern.matcher(email).matches()) {

                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();

                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

                    String bodyContent = String.format("username=%s&email=%s&password=%s",
                            username, email, password);
                    RequestBody body = RequestBody.create(mediaType, bodyContent);

                    Request request = new Request.Builder()
                            .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/auth/register.php")
                            .method("POST", body)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();

                    try (Response response = client.newCall(request).execute()) {
                        if (response.isSuccessful()) {
                            System.out.println("Response: " + response.body().string());
                        } else {
                            System.err.println("Error: " + response.code() + " - " + response.message());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    dialogStage.close(); // Close the dialog

                    //create user and add it to the table
                    User user = new User(username, email, password, 1, creationDate, "user", 0, 0);
                    users.add(user);
                    userObservableList.add(user);

                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Indirizzo email non valido!");
                    alert.setHeaderText(null);
                    alert.setContentText("Inserire un indirizzo email valido!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Tutti i campi devono essere completati!");
                alert.showAndWait();
            }
        });

        //Set up the scene and display the dialog
        VBox vBox = new VBox(20, title, gridPane);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #333333;");

        Scene scene = new Scene(vBox, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/com/example/wheredesktop/styles.css").toExternalForm());
        dialogStage.setScene(scene);

        // Center the dialog on the screen
        dialogStage.centerOnScreen();

        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.DECORATED);

        // Show the dialog and wait for it to close
        dialogStage.showAndWait();
    }

    /**
     * Modifica i dettagli di un utente esistente.
     *
     * @param user L'utente da modificare.
     */
    public void modificaUser(User user)
    {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
        dialogStage.setTitle("Modifica User");

        // Create a layout for the pop-up
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add UI elements
        Label title = new Label("Modifica User");
        title.getStyleClass().add("where-label");

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.getStyleClass().add("input-field");
        usernameField.setText(user.getName());

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        emailField.getStyleClass().add("input-field");
        emailField.setText(user.getEmail());

        Label ruoloLabel = new Label("Ruolo (User/Admin):");
        TextField ruoloField = new TextField();
        ruoloField.getStyleClass().add("input-field");
        ruoloField.setText(user.getRuolo());

        Button saveButton = new Button("Salva");
        saveButton.getStyleClass().add("glow-button");
        saveButton.setAlignment(Pos.CENTER);

        // Add elements to the grid pane
        gridPane.add(usernameLabel, 0, 0);
        gridPane.add(usernameField, 1, 0);

        gridPane.add(emailLabel, 0, 1);
        gridPane.add(emailField, 1, 1);

        gridPane.add(ruoloLabel, 0, 2);
        gridPane.add(ruoloField, 1, 2);

        gridPane.add(saveButton, 1, 3);

        //Add button clicked
        saveButton.setOnAction(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String role = ruoloField.getText();
            String creationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            //EMAIL VALIDATION
            String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);

            if (!username.isEmpty() && !email.isEmpty() && !role.isEmpty()) {
                if (pattern.matcher(email).matches()) {
                    if(role.equals("user") || role.equals("admin")){
                        //username call
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();

                        MediaType mediaType = MediaType.parse("text/plain");
                        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", String.valueOf(user.getID()))
                                .addFormDataPart("username", username)
                                .build();

                        Request request = new Request.Builder()
                                .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/updateUser.php")
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + token)
                                .build();

                        // Execute the request
                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful() && response.body() != null) {
                                System.out.println("Response succesfull");
                            } else {
                                System.out.println("HTTP Error: " + response.code() + " - " + response.message());
                            }
                        } catch(IOException ex){
                            System.out.println("Error IOException");
                        }

                        //email call
                        client = new OkHttpClient().newBuilder()
                                .build();

                        mediaType = MediaType.parse("text/plain");
                        body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", String.valueOf(user.getID()))
                                .addFormDataPart("email", email)
                                .build();

                        request = new Request.Builder()
                                .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/updateUser.php")
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + token)
                                .build();

                        // Execute the request
                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful() && response.body() != null) {
                                System.out.println("Response succesfull");
                            } else {
                                System.out.println("HTTP Error: " + response.code() + " - " + response.message());
                            }
                        } catch(IOException ex){
                            System.out.println("Error IOException");
                        }

                        //role call
                        client = new OkHttpClient().newBuilder()
                                .build();

                        mediaType = MediaType.parse("text/plain");
                        body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("user_id", String.valueOf(user.getID()))
                                .addFormDataPart("role", role)
                                .build();

                        request = new Request.Builder()
                                .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/updateUser.php")
                                .method("POST", body)
                                .addHeader("Authorization", "Bearer " + token)
                                .build();

                        // Execute the request
                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful() && response.body() != null) {
                                System.out.println("Response succesfull");
                            } else {
                                System.out.println("HTTP Error: " + response.code() + " - " + response.message());
                            }
                        } catch(IOException ex){
                            System.out.println("Error IOException");
                        }

                        dialogStage.close(); // Close the dialog

                        //modify user and save it into the table
                        user.setUsername(username);
                        user.setEmail(email);
                        user.setRuolo(role);
                        userTable.refresh();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ruolo non valido!");
                        alert.setHeaderText(null);
                        alert.setContentText("Inserire un ruolo valido! (user/admin)");
                        alert.showAndWait();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Indirizzo email non valido!");
                    alert.setHeaderText(null);
                    alert.setContentText("Inserire un indirizzo email valido!");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText(null);
                alert.setContentText("Tutti i campi devono essere completati!");
                alert.showAndWait();
            }
        });

        //Set up the scene and display the dialog
        VBox vBox = new VBox(20, title, gridPane);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #333333;");

        Scene scene = new Scene(vBox, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/com/example/wheredesktop/styles.css").toExternalForm());
        dialogStage.setScene(scene);

        // Center the dialog on the screen
        dialogStage.centerOnScreen();

        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.DECORATED);

        // Show the dialog and wait for it to close
        dialogStage.showAndWait();
    }

    /**
     * Elimina un utente dalla tabella e dal database.
     *
     * @param user L'utente da eliminare.
     */
    public void eliminaUser(User user)
    {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
        dialogStage.setTitle("Elimina User");

        // Create a layout for the pop-up
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add UI elements
        Label title = new Label("Sei sicuro di voler eliminare questo utente?\n        Quest'azione non è cancellabile.");
        title.getStyleClass().add("where-label");

        Button deleteButton = new Button("Elimina");
        deleteButton.getStyleClass().add("glow-button");
        deleteButton.setAlignment(Pos.CENTER);

        //Add button clicked
        deleteButton.setOnAction(e -> {

            OkHttpClient client = new OkHttpClient().newBuilder().build();

            // Define the media type for the request
            MediaType mediaType = MediaType.parse("text/plain");

            // Build the request body
            RequestBody body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user_id", String.valueOf(user.getID())) // Replace "1" with the desired user ID
                    .build();

            // Build the request
            Request request = new Request.Builder()
                    .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/deleteUser.php")
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + token) // Replace with your authorization token
                    .build();

            // Execute the request and handle the response
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    users.remove(user);
                    userObservableList.remove(user);
                    userTable.refresh();
                    dialogStage.close();
                } else {
                    System.out.println("Request failed. Response Code: " + response.code());
                }
            } catch (IOException ex) {
                System.out.println("An error occurred while making the request.");
            }
        });

        //Set up the scene and display the dialog
        VBox vBox = new VBox(20, title, deleteButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #333333;");

        Scene scene = new Scene(vBox, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/com/example/wheredesktop/styles.css").toExternalForm());
        dialogStage.setScene(scene);

        // Center the dialog on the screen
        dialogStage.centerOnScreen();

        dialogStage.setResizable(false);
        dialogStage.initStyle(StageStyle.DECORATED);

        // Show the dialog and wait for it to close
        dialogStage.showAndWait();
    }
}
