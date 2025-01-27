/**
 * La classe {@code MainStage} rappresenta la finestra principale del pannello di amministrazione dell'applicazione.
 * Consente agli amministratori di gestire statistiche, rapporti e utenti, e include la funzionalità di logout.
 */
package com.example.wheredesktop.Panels;

import com.example.wheredesktop.*;
import com.example.wheredesktop.Objects.AdminManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;

public class MainStage {

    /**
     * Contenitore dinamico per il contenuto principale che cambia in base alla sezione selezionata.
     */
    private StackPane contentPane;

    /**
     * Lista dei pulsanti nella barra superiore per gestire lo stato selezionato.
     */
    private final List<Button> topBarButtons = new ArrayList<>();

    /**
     * Manager per gestire le informazioni dell'amministratore.
     */
    private AdminManager adminManager;

    /**
     * Token di autenticazione utilizzato per le operazioni amministrative.
     */
    private String token;

    /**
     * Costruttore della classe {@code MainStage}. Inizializza l'interfaccia utente e la finestra principale.
     *
     * @param adminManager Istanza di {@link AdminManager} per gestire i dettagli dell'account amministrativo.
     * @param token Token di autenticazione per effettuare richieste protette.
     */
    public MainStage(AdminManager adminManager, String token) {
        this.token = token;
        this.adminManager = adminManager;

        // Creazione della barra superiore
        BorderPane topBar = new BorderPane();
        topBar.setStyle("-fx-padding: 10; -fx-background-color: #1F1F1F;");

        // Etichetta del titolo a sinistra
        Label appLabel = new Label("Where");
        appLabel.getStyleClass().add("where-label");
        BorderPane.setAlignment(appLabel, Pos.CENTER_LEFT);
        topBar.setLeft(appLabel);

        // Pulsanti centrali nella barra superiore
        Button userStatsButton = createTopBarButton("Users", this::showUserStats);
        Button statisticsButton = createTopBarButton("Statistics", this::showStatistics);
        Button reportsButton = createTopBarButton("Ban", this::showBans);
        Button logoutButton = createTopBarButton("Logout", this::showLogout);

        // Aggiunta dei pulsanti alla lista per gestione dello stato
        topBarButtons.add(userStatsButton);
        topBarButtons.add(statisticsButton);
        topBarButtons.add(reportsButton);
        topBarButtons.add(logoutButton);

        HBox buttonBar = new HBox(10, userStatsButton, statisticsButton, reportsButton, logoutButton);
        buttonBar.setAlignment(Pos.CENTER);
        topBar.setCenter(buttonBar);

        // Informazioni amministrative sulla destra
        Label adminNameLabel = new Label(adminManager.getActiveAdminName());
        adminNameLabel.setStyle("-fx-font-size: 14px;");

        ImageView adminIcon = new ImageView();
        adminIcon.setFitWidth(30);
        adminIcon.setFitHeight(30);
        adminIcon.setPreserveRatio(true);

        HBox adminInfo = new HBox(10, adminNameLabel, adminIcon);
        adminInfo.setAlignment(Pos.CENTER_RIGHT);
        topBar.setRight(adminInfo);

        // Area dei contenuti
        contentPane = new StackPane();
        Label welcomeLabel = new Label("Benvenuto nel pannello di amministrazione di Where!\n   Qui potrai gestire gli utenti e i loro post sul Social.");
        contentPane.getChildren().add(welcomeLabel);
        contentPane.setStyle("-fx-background-color: #333333; -fx-padding: 10;");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-font-style: italic;");

        // Configurazione della finestra principale
        Stage stage = new Stage();
        contentPane.prefWidthProperty().bind(stage.widthProperty());
        contentPane.prefHeightProperty().bind(stage.heightProperty());

        // Barra superiore personalizzata
        TopBarStyle topBarStyle = new TopBarStyle();
        HBox bar = topBarStyle.createTopBar(stage);

        // Layout principale
        VBox mainLayout = new VBox(bar, topBar, contentPane);

        // Creazione della scena
        Scene scene = new Scene(mainLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/com/example/wheredesktop/styles.css").toExternalForm());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        topBarStyle.addResizeListener(mainLayout, stage);
        stage.setTitle("Admin Panel");
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Crea un pulsante per la barra superiore.
     *
     * @param text Testo visualizzato sul pulsante.
     * @param onClick Azione eseguita quando il pulsante viene cliccato.
     * @return Istanza del pulsante configurato.
     */
    private Button createTopBarButton(String text, Runnable onClick) {
        Button button = new Button(text);
        button.getStyleClass().add("top-bar-button");
        button.setOnAction(e -> {
            updateSelectedButton(button);
            onClick.run();
        });
        return button;
    }

    /**
     * Aggiorna lo stato visivo dei pulsanti nella barra superiore.
     *
     * @param clickedButton Pulsante selezionato.
     */
    private void updateSelectedButton(Button clickedButton) {
        for (Button button : topBarButtons) {
            button.getStyleClass().remove("selected");
        }
        clickedButton.getStyleClass().add("selected");
    }

    /**
     * Mostra le statistiche degli utenti nell'area dei contenuti.
     */
    private void showUserStats() {
        contentPane.getChildren().clear();
        UserStats userStats = new UserStats(token);
        contentPane.getChildren().add(userStats.getRoot());
    }

    /**
     * Mostra le statistiche generali nell'area dei contenuti.
     */
    private void showStatistics() {
        contentPane.getChildren().clear();
        Statistics statistics = new Statistics(token);
        contentPane.getChildren().add(statistics.getRoot());
    }

    /**
     * Mostra l'area di gestione dei ban nell'area dei contenuti.
     */
    private void showBans() {
        contentPane.getChildren().clear();
        Reports reports = new Reports(token);
        contentPane.getChildren().add(reports.getRoot());
    }

    /**
     * Mostra la schermata di logout nell'area dei contenuti.
     */
    private void showLogout() {
        contentPane.getChildren().clear();
        LogoutPage logoutPage = new LogoutPage();
        contentPane.getChildren().add(logoutPage.getContent());
    }
}
