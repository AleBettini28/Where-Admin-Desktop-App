/**
 * La classe {@code LogoutPage} rappresenta l'interfaccia utente della pagina di logout dell'applicazione.
 * Contiene un'immagine di sfondo, una casella grigia centrata e un pulsante di logout.
 * Cliccando sul pulsante di logout, l'applicazione viene riavviata e si ritorna alla pagina di login.
 */
package com.example.wheredesktop.Panels;

import com.example.wheredesktop.StartApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

public class LogoutPage {

    /**
     * Il layout principale della pagina di logout, rappresentato da un {@link VBox}.
     */
    private final VBox root;

    /**
     * Il pulsante di logout che avvia l'azione di logout.
     */
    private Button logoutButton;

    /**
     * Costruisce un'istanza di {@code LogoutPage} e inizializza i componenti dell'interfaccia utente.
     */
    public LogoutPage() {
        // Layout principale (VBox per la barra superiore + area dei contenuti)
        root = new VBox();
        root.setPrefSize(800, 600); // Imposta le dimensioni preferite per il layout principale
        root.setFillWidth(true);   // Assicura che il layout si estenda in larghezza

        // Area dei contenuti (StackPane con immagine di sfondo e casella grigia)
        StackPane contentPane = new StackPane();
        contentPane.prefWidthProperty().bind(root.widthProperty());
        contentPane.prefHeightProperty().bind(root.heightProperty());
        VBox.setVgrow(contentPane, Priority.ALWAYS); // Permette allo StackPane di riempire il VBox

        // Imposta immagine di sfondo
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/background.jpg")));
            BackgroundImage background = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(1.0, 1.0, true, true, false, true)
            );
            contentPane.setBackground(new Background(background));
        } catch (NullPointerException e) {
            System.err.println("Immagine di sfondo non trovata: /background.jpg");
            contentPane.setStyle("-fx-background-color: #333333;"); // Colore di fallback se l'immagine manca
        }

        // Casella grigia
        VBox grayBox = new VBox(20);
        grayBox.setAlignment(Pos.CENTER);
        grayBox.setMaxWidth(400);
        grayBox.setMaxHeight(300);
        grayBox.setPadding(new Insets(15, 20, 15, 20));
        grayBox.setStyle("-fx-background-color: #4E4D4D; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 3);");

        // Etichetta del titolo
        Label titleLabel = new Label("Vuoi effettuare il logout\ndall'applicazione?");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Pulsante di Logout
        logoutButton = new Button("LOGOUT");
        logoutButton.setPrefWidth(150);
        logoutButton.setMaxWidth(200);
        logoutButton.setPrefHeight(25);
        logoutButton.setMaxHeight(25);
        logoutButton.getStyleClass().add("glow-button");

        // Collega la logica di logout al pulsante di logout
        logoutButton.setOnAction(event -> onLogout());

        // Aggiungi elementi alla casella grigia
        grayBox.getChildren().addAll(titleLabel, logoutButton);

        // Aggiungi la casella grigia al contenitore dei contenuti
        contentPane.getChildren().add(grayBox);

        // Assicura che il contenitore dei contenuti riempia tutto lo spazio disponibile
        StackPane.setAlignment(grayBox, Pos.CENTER); // Assicura che la casella grigia sia centrata

        // Combina la barra superiore e il contenitore dei contenuti nel layout principale
        root.getChildren().add(contentPane);
    }

    /**
     * Restituisce il layout principale della pagina di logout.
     *
     * @return un {@link VBox} che rappresenta il layout principale.
     */
    public VBox getContent() {
        return root;
    }

    /**
     * Gestisce l'azione di logout chiudendo la finestra corrente e riavviando l'applicazione.
     */
    private void onLogout() {
        Stage currentStage = (Stage) root.getScene().getWindow();
        currentStage.close();

        // Riavvia l'applicazione e torna alla pagina di login
        StartApplication startApplication = new StartApplication();
        startApplication.start(new Stage());
    }
}
