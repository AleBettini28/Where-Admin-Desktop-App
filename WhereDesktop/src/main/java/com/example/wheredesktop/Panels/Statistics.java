/**
 * La classe {@code Statistics} fornisce una panoramica statistica sull'utilizzo dell'applicazione.
 * Mostra grafici relativi agli utenti online, distribuzione dei like, nuovi utenti e altre metriche.
 * Include funzionalità per la gestione di due viste: statistiche sugli utenti e statistiche sull'app.
 */
package com.example.wheredesktop.Panels;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Statistics {

    /**
     * Grafico a barre per la visualizzazione degli utenti online.
     */
    @FXML
    private BarChart<String, Number> onlineUsersChart;
    /**
     * Grafico a barre per la distribuzione dei like.
     */
    @FXML
    private BarChart<String, Number> likesDistributionChart;
    /**
     * Grafico a linee per i post creati per giorno.
     */
    @FXML
    private LineChart<String, Number> postsPerDayChart;
    /**
     * Grafico a barre per i nuovi utenti registrati per giorno.
     */
    @FXML
    private BarChart<String, Number> newUsersPerDayChart;
    /**
     * Radice dell'interfaccia utente, rappresentata da un {@link AnchorPane}.
     */
    private final AnchorPane root;
    /**
     * Pannello per le statistiche sugli utenti.
     */
    private Pane userStatsPane;
    /**
     * Pannello per le statistiche sull'applicazione.
     */
    private Pane appStatsPane;
    /**
     * Token di autenticazione per effettuare richieste protette.
     */
    private String token;

    /**
     * Costruttore della classe {@code Statistics}. Configura i grafici e le viste.
     *
     * @param token Token di autenticazione utilizzato per effettuare richieste protette.
     */
    public Statistics(String token) {
        this.token = token;
        root = new AnchorPane();

        // Cambia il colore di sfondo in verde scuro
        root.setStyle("-fx-background-color: #333333;");

        // VBox per contenere tutto
        VBox mainContent = new VBox(20); // Spaziatura di 20
        mainContent.setStyle("-fx-background-color: #333333;");
        mainContent.setAlignment(Pos.CENTER); // Centrare tutto verticalmente e orizzontalmente
        mainContent.setPadding(new Insets(20));

        AnchorPane.setTopAnchor(mainContent, 0.0);
        AnchorPane.setLeftAnchor(mainContent, 0.0);
        AnchorPane.setRightAnchor(mainContent, 0.0);
        AnchorPane.setBottomAnchor(mainContent, 0.0);

        // Creazione dei bottoni
        Button userStatistics = new Button("Statistiche utenti");
        //Button appStatistics = new Button("Statistiche app");
        userStatistics.getStyleClass().add("glow-button-normal");
        //appStatistics.getStyleClass().add("glow-button-normal");


        // Creazione dell'HBox per i bottoni
        HBox buttonBox = new HBox(10); // Spaziatura di 10 tra i bottoni
        //buttonBox.getChildren().addAll(userStatistics, appStatistics);
        buttonBox.setAlignment(Pos.CENTER); // Centrare i bottoni nell'HBox

        // Creazione dei grafici
        onlineUsersChart = createBarChart("Utenti Online", "Tempo", "Numero di Utenti");
        Number[] data = fetchAllLikesData("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/statistics/getOnlineUsers.php", "online_users");
        populateBarChart(onlineUsersChart, new String[]{"Ultimo Giorno", "Ultima Settimana", "Ultimo Mese", "Ultimo Anno", "Lifetime"}, data);

        //creazione grafico per i like, usando fetchAllLikesData()
        likesDistributionChart = createBarChart("Like Totali", "Tempo", "Like");
        data = fetchAllLikesData("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/statistics/getNewLikes.php", "likes");
        populateBarChart(likesDistributionChart, new String[]{"Ultimo Giorno", "Ultima Settimana", "Ultimo Mese", "Ultimo Anno", "Lifetime"}, data);

        /*
        postsPerDayChart = createLineChart("Post Totali vs Giornalieri", "Giorni", "Numero di Post");
        populateLineChart(postsPerDayChart, new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"},
                new Number[]{60, 80, 90, 100, 120});*/

        newUsersPerDayChart = createBarChart("Nuovi Utenti", "Periodi", "Numero di Utenti");
        data = fetchAllLikesData("https://valentinofrancocatozzi.altervista.org/where/endpoint/administrator/statistics/getNewUsers.php", "new_users");
        populateBarChart(newUsersPerDayChart, new String[]{"Ultimo Giorno", "Ultima Settimana", "Ultimo Mese", "Ultimo Anno", "Lifetime"}, data);

        // VBox per i grafici
        userStatsPane = UserStats();
        appStatsPane = AppStats();

        // StackPane to overlay the two panes
        StackPane statsPane = new StackPane();
        statsPane.getChildren().addAll(userStatsPane, appStatsPane);

        // Ensure both panes occupy the same position
        StackPane.setAlignment(userStatsPane, Pos.CENTER);
        StackPane.setAlignment(appStatsPane, Pos.CENTER);

        // Initially, show userStatsPane and hide appStatsPane
        userStatsPane.setVisible(true);
        appStatsPane.setVisible(false);

        // Add the StackPane to the main content
        mainContent.getChildren().addAll(buttonBox, statsPane);

        // Button actions to toggle visibility
        userStatistics.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                userStatsPane.setVisible(true);
                appStatsPane.setVisible(false);
            }
        });

        /*appStatistics.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                userStatsPane.setVisible(false);
                appStatsPane.setVisible(true);
            }
        });*/

        // Aggiungi il contenuto al ScrollPane
        root.getChildren().add(mainContent);
    }

    /**
     * Crea il pannello delle statistiche sugli utenti.
     *
     * @return Un {@link Pane} che contiene i grafici relativi agli utenti.
     */
    private Pane UserStats() {
        GridPane chartsContainer = new GridPane();
        chartsContainer.setMaxWidth(1600);
        chartsContainer.setMaxHeight(1200);
        chartsContainer.getStyleClass().add("vbox");
        chartsContainer.setHgap(20); // Horizontal gap between columns
        chartsContainer.setVgap(20); // Vertical gap between rows
        chartsContainer.setPadding(new Insets(20)); // Padding around the grid

        // Configure each chart with a fixed preferred size
        onlineUsersChart.setPrefWidth(700);
        onlineUsersChart.setPrefHeight(400);
        onlineUsersChart.setLegendVisible(false);

        likesDistributionChart.setPrefWidth(700);
        likesDistributionChart.setPrefHeight(400);
        likesDistributionChart.setLegendVisible(false);

        /*
        postsPerDayChart.setPrefWidth(700);
        postsPerDayChart.setPrefHeight(400);
        postsPerDayChart.setLegendVisible(false);*/

        newUsersPerDayChart.setPrefWidth(700);
        newUsersPerDayChart.setPrefHeight(400);
        newUsersPerDayChart.setLegendVisible(false);

        // Add charts to the GridPane
        chartsContainer.add(onlineUsersChart, 0, 0); // Column 0, Row 0
        chartsContainer.add(likesDistributionChart, 1, 0); // Column 1, Row 0
        //chartsContainer.add(postsPerDayChart, 0, 1); // Column 0, Row 1

        HBox centeredBox = new HBox(newUsersPerDayChart);
        centeredBox.setAlignment(Pos.CENTER); // Center the chart horizontally
        chartsContainer.add(centeredBox, 0, 1, 2, 1); // Span 2 columns in Row

        // Ensure charts scale properly within their container
        GridPane.setHgrow(onlineUsersChart, Priority.ALWAYS);
        GridPane.setVgrow(onlineUsersChart, Priority.ALWAYS);
        GridPane.setHgrow(likesDistributionChart, Priority.ALWAYS);
        GridPane.setVgrow(likesDistributionChart, Priority.ALWAYS);
        //GridPane.setHgrow(postsPerDayChart, Priority.ALWAYS);
        //GridPane.setVgrow(postsPerDayChart, Priority.ALWAYS);
        GridPane.setHgrow(centeredBox, Priority.ALWAYS);
        GridPane.setVgrow(centeredBox, Priority.ALWAYS);

        return chartsContainer;
    }



    private Number[] fetchAllLikesData(String url, String dataRequested) {
        OkHttpClient client = new OkHttpClient.Builder().build();

        // Types to fetch
        String[] types = {"daily", "weekly", "monthly", "yearly", "lifetime"};
        Number[] likesData = new Number[types.length]; // Array to hold the like counts for each type


        for (int i = 0; i < types.length; i++) {
            String type = types[i];
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("type", type)
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        likesData[i] = data.getInt(dataRequested); // Store the like count in the array
                    } else {
                        System.err.println("Failed to fetch " + type + " likes: " + jsonObject.getString("message"));
                    }
                } else {
                    System.err.println("Failed to fetch " + type + " likes: " + response.message());
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }

        return likesData;
    }

    private Pane AppStats() {
        // GridPane for automatic layout
        GridPane chartsContainer = new GridPane();
        chartsContainer.setMaxWidth(1600);
        chartsContainer.setMaxHeight(1200);
        chartsContainer.getStyleClass().add("vbox");
        chartsContainer.setHgap(20); // Horizontal gap between columns
        chartsContainer.setVgap(20); // Vertical gap between rows
        chartsContainer.setPadding(new Insets(20)); // Padding around the grid

        // Create and configure each chart
        BarChart<String, Number> dailyDownloadsChart = createBarChart("Download Giornalieri", "Giorni", "Numero di Download");
        populateBarChart(dailyDownloadsChart, new String[]{"Lun", "Mar", "Mer", "Gio", "Ven"}, new Number[]{120, 140, 160, 180, 200});
        dailyDownloadsChart.setPrefWidth(700);
        dailyDownloadsChart.setPrefHeight(400);
        dailyDownloadsChart.setLegendVisible(false);

        LineChart<String, Number> totalDownloadsChart = createLineChart("Download Totali", "Settimane", "Numero di Download");
        populateLineChart(totalDownloadsChart, new String[]{"Settimana 1", "Settimana 2", "Settimana 3", "Settimana 4"}, new Number[]{500, 1200, 2500, 4000});
        totalDownloadsChart.setPrefWidth(700);
        totalDownloadsChart.setPrefHeight(400);
        totalDownloadsChart.setLegendVisible(false);

        BarChart<String, Number> weeklyActiveUsersChart = createBarChart("Utenti Attivi Settimanali", "Settimane", "Numero di Utenti");
        populateBarChart(weeklyActiveUsersChart, new String[]{"Settimana 1", "Settimana 2", "Settimana 3", "Settimana 4"}, new Number[]{200, 300, 400, 500});
        weeklyActiveUsersChart.setPrefWidth(700);
        weeklyActiveUsersChart.setPrefHeight(400);
        weeklyActiveUsersChart.setLegendVisible(false);

        StackedBarChart<String, Number> averageRatingsChart = createStackedBarChart("Utenti Totali", "Giorni", "Utenti");
        populateStackedBarChart(averageRatingsChart, new String[]{"Lun", "Mar", "Mer", "Gio", "Ven"}, new Number[]{50, 60, 55, 70, 65}, new Number[]{30, 40, 35, 50, 45});
        averageRatingsChart.setPrefWidth(700);
        averageRatingsChart.setPrefHeight(400);
        averageRatingsChart.setLegendVisible(false);

        // Add charts to the GridPane
        chartsContainer.add(dailyDownloadsChart, 0, 0); // Column 0, Row 0
        chartsContainer.add(totalDownloadsChart, 1, 0); // Column 1, Row 0
        chartsContainer.add(weeklyActiveUsersChart, 0, 1); // Column 0, Row 1
        chartsContainer.add(averageRatingsChart, 1, 1); // Column 1, Row 1

        // Ensure charts scale properly within their container
        GridPane.setHgrow(dailyDownloadsChart, Priority.ALWAYS);
        GridPane.setVgrow(dailyDownloadsChart, Priority.ALWAYS);
        GridPane.setHgrow(totalDownloadsChart, Priority.ALWAYS);
        GridPane.setVgrow(totalDownloadsChart, Priority.ALWAYS);
        GridPane.setHgrow(weeklyActiveUsersChart, Priority.ALWAYS);
        GridPane.setVgrow(weeklyActiveUsersChart, Priority.ALWAYS);
        GridPane.setHgrow(averageRatingsChart, Priority.ALWAYS);
        GridPane.setVgrow(averageRatingsChart, Priority.ALWAYS);

        return chartsContainer;
    }


    private LineChart<String, Number> createLineChart(String title, String xAxisLabel, String yAxisLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);
        return lineChart;
    }

    /**
     * Crea un grafico a barre.
     *
     * @param title Titolo del grafico.
     * @param xAxisLabel Etichetta dell'asse X.
     * @param yAxisLabel Etichetta dell'asse Y.
     * @return Un {@link BarChart} configurato.
     */
    private BarChart<String, Number> createBarChart(String title, String xAxisLabel, String yAxisLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        return barChart;
    }

    private StackedBarChart<String, Number> createStackedBarChart(String title, String xAxisLabel, String yAxisLabel) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
        barChart.setTitle(title);
        return barChart;
    }

    private void populateLineChart(LineChart<String, Number> chart, String[] xValues, Number[] yValues) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xValues.length; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(xValues[i], yValues[i]);
            addHoverAnimationWithTooltip(data);
            series.getData().add(data);
        }
        chart.getData().add(series);
    }

    private void populateBarChart(BarChart<String, Number> chart, String[] xValues, Number[] yValues) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < xValues.length; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(xValues[i], yValues[i]);
            addHoverAnimationWithTooltip(data);
            series.getData().add(data);
        }
        chart.getData().add(series);
    }

    private void populateStackedBarChart(StackedBarChart<String, Number> chart, String[] xValues, Number[] yValues1, Number[] yValues2) {
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        for (int i = 0; i < xValues.length; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(xValues[i], yValues1[i]);
            addHoverAnimationWithTooltip(data);
            series1.getData().add(data);
        }
        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        for (int i = 0; i < xValues.length; i++) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(xValues[i], yValues2[i]);
            addHoverAnimationWithTooltip(data);
            series2.getData().add(data);
        }
        chart.getData().addAll(series1, series2);
    }

    private void addHoverAnimationWithTooltip(XYChart.Data<String, Number> data) {
        // Tooltip per mostrare il valore attuale
        Tooltip tooltip = new Tooltip();
        tooltip.setStyle("-fx-font-size: 14px; -fx-background-color: #444; -fx-text-fill: white;");

        // Aggiungi un listener per monitorare quando il nodo è disponibile
        data.nodeProperty().addListener((observable, oldNode, newNode) -> {
            if (newNode != null) {
                // Imposta il testo del tooltip con il valore attuale
                tooltip.setText(data.getXValue() + ": " + data.getYValue());
                Tooltip.install(newNode, tooltip);

                // Aggiungi l'animazione al passaggio del mouse
                newNode.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), newNode);
                    st.setToX(1.2);
                    st.setToY(1.2);
                    st.play();
                });

                newNode.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), newNode);
                    st.setToX(1.0);
                    st.setToY(1.0);
                    st.play();
                });
            }
        });
    }

    /**
     * Restituisce la radice dell'interfaccia.
     *
     * @return Un {@link AnchorPane} che rappresenta la radice dell'interfaccia.
     */
    public AnchorPane getRoot() {
        return root;
    }
}