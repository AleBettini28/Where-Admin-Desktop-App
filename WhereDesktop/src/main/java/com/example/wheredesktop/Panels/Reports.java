package com.example.wheredesktop.Panels;

import com.example.wheredesktop.Objects.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * La classe {@code Reports} gestisce la visualizzazione e la gestione degli utenti bannati e non bannati.
 * Consente agli amministratori di visualizzare tabelle di utenti, eseguire ban o rimuovere ban.
 */
public class Reports {

    /**
     * Radice dell'interfaccia utente, rappresentata da un {@link AnchorPane}.
     */
    private final AnchorPane root;
    /**
     * Tabella che mostra gli utenti non bannati.
     */
    private final TableView<User> notBannedTable = new TableView<>();
    /**
     * Tabella che mostra gli utenti bannati.
     */
    private final TableView<User> bannedTable = new TableView<>();
    /**
     * Lista di utenti non bannati.
     */
    private ArrayList<User> notBannedUsers = new ArrayList<>();
    /**
     * Lista di utenti bannati.
     */
    private ArrayList<User> bannedUsers = new ArrayList<>();
    /**
     * ObservableList per aggiornare dinamicamente la tabella degli utenti non bannati.
     */
    private ObservableList<User> notBannedObservable;
    /**
     * ObservableList per aggiornare dinamicamente la tabella degli utenti bannati.
     */
    private ObservableList<User> bannedObservables;

    /**
     * Token di autenticazione utilizzato per effettuare richieste protette.
     */
    private String token;

    /**
     * Costruttore della classe {@code Reports}. Inizializza l'interfaccia e carica gli utenti dal database.
     *
     * @param token Token di autenticazione utilizzato per effettuare richieste.
     */
    public Reports(String token) {
        this.token = token;
        root = new AnchorPane();

        readUsers();

        //Create two TableViews
        VBox openTableBox = createNotBannedUsersTable();
        VBox closeTableBox = createBannedUsersTable();

        //Arrange tables in an HBox
        HBox tableContainer = new HBox(50); // 10px spacing between tables
        tableContainer.getChildren().addAll(openTableBox, closeTableBox);

        //Ensure the VBox elements are positioned correctly
        HBox.setHgrow(openTableBox, Priority.ALWAYS);
        HBox.setHgrow(closeTableBox, Priority.ALWAYS);

        //Align the VBox elements within the HBox
        openTableBox.setAlignment(Pos.CENTER_LEFT);
        closeTableBox.setAlignment(Pos.CENTER_RIGHT);

        //Add padding to push them to their respective sides
        tableContainer.setPadding(new Insets(10));

        //Set the HBox size to fill the AnchorPane
        tableContainer.setAlignment(Pos.CENTER);

        //Add the HBox to the AnchorPane
        root.getChildren().add(tableContainer);
        AnchorPane.setTopAnchor(tableContainer, 0.0);
        AnchorPane.setLeftAnchor(tableContainer, 0.0);
        AnchorPane.setRightAnchor(tableContainer, 0.0);
        AnchorPane.setBottomAnchor(tableContainer, 0.0);
    }

    /**
     * Restituisce la radice dell'interfaccia.
     *
     * @return un {@link AnchorPane} che rappresenta la radice.
     */
    public AnchorPane getRoot()
    {
        return root;
    }

    /**
     * Crea la tabella per gli utenti non bannati.
     *
     * @return un {@link VBox} contenente la tabella.
     */
    public VBox createNotBannedUsersTable()
    {
        //CREATE BOX THAT CONTAINS USERS-TABLE
        VBox grayBox = new VBox(15);
        grayBox.setAlignment(Pos.CENTER_LEFT);
        grayBox.setMaxWidth(800);
        grayBox.setMaxHeight(600);
        grayBox.setPadding(new Insets(25, 30, 25, 30));
        grayBox.setStyle("-fx-background-color: #4E4D4D; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        Label tableLabel = new Label("Utenti non bannati");
        tableLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        tableLabel.setAlignment(Pos.CENTER_LEFT);

        notBannedTable.setPrefHeight(700);

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

        //ban column
        TableColumn<User, Void> banButton = new TableColumn<>("Ban User");
        banButton.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<User, Void>() {
                private final Button btn = new Button("Ban");

                {
                    btn.getStyleClass().add("glow-button-table");
                    btn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        banUser(user);
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

        //Add the columns to the table
        notBannedTable.getColumns().addAll(usernameColumn, emailColumn, idColumn, createdAtColumn, bannedColumn, banButton);

        //RESIZE DELLE COLONNE
        notBannedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        idColumn.setResizable(true);
        usernameColumn.setResizable(true);
        emailColumn.setResizable(true);
        createdAtColumn.setResizable(true);
        bannedColumn.setResizable(true);

        notBannedTable.setItems(notBannedObservable);
        notBannedTable.setFixedCellSize(45);

        notBannedTable.setId("customTableView");

        grayBox.getChildren().addAll(tableLabel, notBannedTable);

        return grayBox;
    }

    /**
     * Legge gli utenti dal database remoto tramite una chiamata HTTP.
     */
    public void readUsers()
    {
        //read from database
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

                // Parse the response as JSON
                JSONObject jsonResponse = new JSONObject(responseBody);

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
                        String username = user.getString("username");
                        String email = user.getString("email");
                        String role = user.getString("role");
                        int isBanned = user.getInt("is_banned");
                        int score = user.getInt("score");
                        String createdAt = user.getString("created_at");

                        if(isBanned == 1){
                            bannedUsers.add(new User(username, email, "", id, createdAt, role, score, isBanned));
                        }
                        else if(isBanned == 0){
                            notBannedUsers.add(new User(username, email, "", id, createdAt, role, score, isBanned));
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

        System.out.println(bannedUsers.size());
        System.out.println(notBannedUsers.size());
        notBannedObservable = FXCollections.observableArrayList(notBannedUsers);
        bannedObservables = FXCollections.observableArrayList(bannedUsers);
    }

    /**
     * Crea la tabella per gli utenti bannati.
     *
     * @return un {@link VBox} contenente la tabella.
     */
    public VBox createBannedUsersTable()
    {
        //CREATE BOX THAT CONTAINS USERS-TABLE
        VBox grayBox = new VBox(15);
        grayBox.setAlignment(Pos.CENTER_RIGHT);
        grayBox.setMaxWidth(800);
        grayBox.setMaxHeight(600);
        grayBox.setPadding(new Insets(25, 30, 25, 30));
        grayBox.setStyle("-fx-background-color: #4E4D4D; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        Label tableLabel = new Label("Utenti Bannati");
        tableLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        tableLabel.setAlignment(Pos.CENTER_LEFT);

        bannedTable.setPrefHeight(700);

        //COLONNE
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

        TableColumn<User, Void> unbanButton = new TableColumn<>("Unban User");
        unbanButton.setCellFactory(col -> {
            TableCell<User, Void> cell = new TableCell<User, Void>() {
                private final Button btn = new Button("Unban");

                {
                    btn.getStyleClass().add("glow-button-table");
                    btn.setOnAction(event -> {
                        User user = getTableView().getItems().get(getIndex());
                        unbanUser(user);
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

        //Add the columns to the table
        bannedTable.getColumns().addAll(usernameColumn, emailColumn, idColumn, createdAtColumn, bannedColumn, unbanButton);

        //RESIZE DELLE COLONNE
        bannedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        usernameColumn.setResizable(true);
        emailColumn.setResizable(true);
        createdAtColumn.setResizable(true);
        idColumn.setResizable(true);
        bannedColumn.setResizable(true);

        bannedTable.setItems(bannedObservables);
        bannedTable.setFixedCellSize(45);

        bannedTable.setId("customTableView");

        grayBox.getChildren().addAll(tableLabel, bannedTable);

        return grayBox;
    }

    /**
     * Gestisce la rimozione del ban di un utente.
     *
     * @param report L'oggetto {@link User} che rappresenta l'utente da sbannare.
     */
    private void unbanUser(User report){
        //logic to unban user
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
        dialogStage.setTitle("Sbanna User");

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label title = new Label("Sei sicuro di voler rimuovere il ban a questo utente?");
        title.getStyleClass().add("where-label");

        Button deleteButton = new Button("Rimuovi ban");
        deleteButton.getStyleClass().add("glow-button");

        deleteButton.setAlignment(Pos.CENTER);

        //Add button clicked
        deleteButton.setOnAction(e -> {
            try {
                //get the user ID to ban
                int userId = report.getID();

                //HTTP request to ban the user
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id", String.valueOf(userId)) // Convert int to String
                        .addFormDataPart("action", "unban")
                        .build();
                Request request = new Request.Builder()
                        .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/updateBan.php")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + token) // Replace with the actual token
                        .build();

                // Execute the request and check the response
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    // Optionally parse the JSON response to confirm success
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if ("success".equals(jsonResponse.getString("status"))) {
                        // Update the local state only if the ban is successful
                        bannedUsers.remove(report);
                        bannedObservables.remove(report);
                        bannedTable.refresh();

                        // Set user banned state to true
                        report.setBanned(false);

                        // Add report to the other table
                        notBannedUsers.add(report);
                        notBannedObservable.add(report);
                        notBannedTable.refresh();

                        dialogStage.close();
                    } else {
                        // Handle failure in response message
                        System.err.println("Failed to unban user: " + jsonResponse.getString("message"));
                    }
                } else {
                    // Handle unsuccessful HTTP response
                    System.err.println("HTTP request failed: " + response.code());
                }
            } catch (Exception ex) {
                // Handle exceptions (network issues, JSON parsing errors, etc.)
                ex.printStackTrace();
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

    /**
     * Gestisce il ban di un utente.
     *
     * @param report L'oggetto {@link User} che rappresenta l'utente da bannare.
     */
    private void banUser(User report){
        //logic to ban user
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL); // Make it a modal window
        dialogStage.setTitle("Banna User");

        // Create a layout for the pop-up
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Add UI elements
        Label title = new Label("Sei sicuro di voler bannare questo utente?");
        title.getStyleClass().add("where-label");

        Button deleteButton = new Button("Banna");
        deleteButton.getStyleClass().add("glow-button");

        deleteButton.setAlignment(Pos.CENTER);

        //Add button clicked
        deleteButton.setOnAction(e -> {
            try {
                // Get the user ID to ban
                int userId = report.getID();

                // Make the HTTP request to ban the user
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("user_id", String.valueOf(userId)) // Convert int to String
                        .addFormDataPart("action", "ban")
                        .build();
                Request request = new Request.Builder()
                        .url("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/moderation/updateBan.php")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + token) // Replace with the actual token
                        .build();

                // Execute the request and check the response
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    // Optionally parse the JSON response to confirm success
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    if ("success".equals(jsonResponse.getString("status"))) {
                        // Update the local state only if the ban is successful
                        notBannedUsers.remove(report);
                        notBannedObservable.remove(report);
                        notBannedTable.refresh();

                        // Set user banned state to true
                        report.setBanned(true);

                        // Add report to the other table
                        bannedUsers.add(report);
                        bannedObservables.add(report);
                        bannedTable.refresh();

                        dialogStage.close();
                    } else {
                        // Handle failure in response message
                        System.err.println("Failed to ban user: " + jsonResponse.getString("message"));
                    }
                } else {
                    // Handle unsuccessful HTTP response
                    System.err.println("HTTP request failed: " + response.code());
                }
            } catch (Exception ex) {
                // Handle exceptions (network issues, JSON parsing errors, etc.)
                ex.printStackTrace();
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