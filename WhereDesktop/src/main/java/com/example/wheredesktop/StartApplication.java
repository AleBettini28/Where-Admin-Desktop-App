package com.example.wheredesktop;

import com.example.wheredesktop.Objects.AdminManager;
import com.example.wheredesktop.Panels.MainStage;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

/**
 * La classe {@code StartApplication} è l'entry point per l'applicazione JavaFX dove viene gestita l'interfaccia utente
 * per il login dell'amministratore. La classe configura la finestra di login, gestisce l'interazione con gli input dell'utente
 * (nome utente e password) e valida le credenziali tramite il gestore degli amministratori {@code AdminManager}.
 * Inoltre, include la funzionalità di ridimensionamento della finestra e l'animazione di errore in caso di credenziali non valide.
 */

public class StartApplication extends Application {

    private TextField adminTextField;
    private PasswordField passwordField;
    private TextField passwordTextField;
    private CheckBox showPasswordCheckBox;
    private Label errorLabel;
    private AdminManager adminManager = new AdminManager(); // Simulate admin management
    private Button loginButton;

    private final int RESIZE_MARGIN = 8; // Margin around edges for resizing
    private boolean isRightResize = false;
    private boolean isBottomResize = false;
    private boolean isTopResize = false;
    private boolean isLeftResize = false;

    private String token;

    /**
     * Avvia l'applicazione e configura la scena di login.
     * Crea la finestra di login, gestisce la disposizione dei controlli grafici
     * e si occupa delle azioni di accesso.
     *
     * @param stage Il palcoscenico principale dell'applicazione.
     */
    @Override
    public void start(Stage stage) {

        // Main StackPane (Root Layout)
        StackPane stackPane = new StackPane();

        // Ensure the StackPane fills the stage
        stackPane.prefWidthProperty().bind(stage.widthProperty());
        stackPane.prefHeightProperty().bind(stage.heightProperty());

        // Set the background image
        try {
            Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/background.jpg")));
            BackgroundImage background = new BackgroundImage(
                    backgroundImage,
                    BackgroundRepeat.NO_REPEAT, // Do not repeat
                    BackgroundRepeat.NO_REPEAT, // Do not repeat
                    BackgroundPosition.CENTER, // Center the image
                    new BackgroundSize(
                            1.0, 1.0, true, true, false, true // Cover the entire StackPane
                    )
            );
            stackPane.setBackground(new Background(background));
        } catch (NullPointerException e) {
            System.err.println("Background image not found: /background.jpg");
            stackPane.setStyle("-fx-background-color: #333333;"); // Fallback color if the image is missing
        }

        // VBox for the gray box
        VBox grayBox = new VBox(13); // Spacing of 17px between elements
        grayBox.setAlignment(Pos.TOP_CENTER); // Center alignment
        grayBox.setMaxWidth(400); // Max width for the gray box
        grayBox.setMaxHeight(300); // Max height for the gray box
        grayBox.setPadding(new Insets(15, 20, 15, 20)); // Padding inside the box
        grayBox.getStyleClass().add("vbox"); // Apply CSS style class

        // Title Label
        Label titleLabel = new Label("Where");
        titleLabel.setStyle("-fx-font-size: 24px;"); // Inline CSS for font size

        // Admin ID TextField
        adminTextField = new TextField();
        adminTextField.setPromptText("Admin");
        adminTextField.setPrefWidth(250);
        adminTextField.setMaxWidth(300);
        adminTextField.getStyleClass().add("input-field"); // Apply CSS style class

        // Password Field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(250);
        passwordField.setMaxWidth(300);
        passwordField.getStyleClass().add("input-field");

        // Visible Password Field (for "Show Password")
        passwordTextField = new TextField();
        passwordTextField.setPromptText("Password");
        passwordTextField.setPrefWidth(250);
        passwordTextField.setMaxWidth(300);
        passwordTextField.getStyleClass().add("input-field");
        passwordTextField.setVisible(false); // Initially hidden
        passwordTextField.setManaged(false); // Exclude from layout calculations

        // Show Password CheckBox
        showPasswordCheckBox = new CheckBox("Show Password");
        showPasswordCheckBox.getStyleClass().add("white-label");
        showPasswordCheckBox.setOnAction(event -> togglePasswordVisibility());

        // Login Button
        loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(150);
        loginButton.setMaxWidth(200);
        loginButton.setPrefHeight(25);
        loginButton.setMaxHeight(25);
        loginButton.getStyleClass().add("glow-button");

        // Attach the onLogin logic to the login button
        loginButton.setOnAction(event -> onLogin(stage));

        // Error Label
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED); // Red color for the error text
        errorLabel.setVisible(false); // Initially hidden
        errorLabel.getStyleClass().add("error-label");

        // Add all elements to the gray box
        grayBox.getChildren().addAll(
                titleLabel,
                adminTextField,
                passwordField,
                passwordTextField,
                showPasswordCheckBox,
                loginButton,
                errorLabel
        );

        // Add the gray box to the root StackPane
        stackPane.getChildren().add(grayBox);

        //TOP BAR STYLING
        TopBarStyle topBar = new TopBarStyle();
        HBox bar = topBar.createTopBar(stage);
        //END OF TOP BAR STYLING

        // Combine top bar and content
        VBox root = new VBox();
        root.getChildren().addAll(bar, stackPane);

        // Create the Scene
        Scene scene = new Scene(root, 800, 600);
        topBar.addResizeListener(root, stage);
        // Create the Scene
        scene.getStylesheets().add(getClass().getResource("/com/example/wheredesktop/styles.css").toExternalForm());

        // Stage setup
        stage.setTitle("Where_Admin_APP");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setMaximized(true); // Maximize the window
        stage.show();
    }

    private void onLogin(Stage stage) {
        //temporarily disable the button
        loginButton.setDisable(true);

        //Cooldown for clicking the button
        PauseTransition cooldown = new PauseTransition(Duration.seconds(0.5));
        cooldown.setOnFinished(e -> loginButton.setDisable(false));
        cooldown.play();

        // Reset error messages
        errorLabel.setText("");
        errorLabel.setVisible(false);

        String username = adminTextField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

        // Validate empty fields with shake animation
        if (username.isEmpty() && password.isEmpty()) {
            errorLabel.setText("Admin and Password fields cannot be empty.");
            errorLabel.setVisible(true);
            shakeNode(adminTextField);
            shakeNode(passwordField);
            return;
        }
        if (username.isEmpty()) {
            errorLabel.setText("Admin field cannot be empty.");
            errorLabel.setVisible(true);
            shakeNode(adminTextField);
            return;
        }
        if (password.isEmpty()) {
            errorLabel.setText("Password field cannot be empty.");
            errorLabel.setVisible(true);
            shakeNode(passwordField);
            return;
        }

        // Validate credentials
        if (!adminManager.validateCredentials(username, password)) {
            errorLabel.setText("Invalid username or password.");
            errorLabel.setVisible(true);
            shakeNode(adminTextField);
            shakeNode(passwordField);
            return;
        }

        token = adminManager.token;

        adminManager.setActiveAdminName(username);

        //Close the current stage
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();

        //Open the main stage
        MainStage mainStage = new MainStage(adminManager, token);
    }

    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10); // Shake movement
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }

    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
/**
 * Gestisce l'evento di login. Valida le credenziali inserite dall'amministratore
 * e avvia la finestra principale se il login è valido.
 *
 * @param stage Il palco principale dell'applicazione.
 */
