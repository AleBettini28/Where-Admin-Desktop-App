package com.example.wheredesktop.Objects;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * La classe {@code User} rappresenta un utente nel sistema con un nome utente, un'email, una password, un ID univoco
 * e una data di creazione. La classe tiene traccia anche dello stato di ban dell'utente.
 * <p>
 * Fornisce getter e setter per le informazioni dell'utente, e metodi per ottenere le proprietà JavaFX utili per il binding
 * dei dati nelle interfacce utente basate su JavaFX.
 * </p>
 */
public class User {
    private String username;
    private String email;
    private String password;
    private final int ID;
    private final String createdAt;
    private boolean banned;
    private String ruolo;
    private int score;

    /**
     * Costruttore della classe {@code User}.
     * Crea un nuovo utente con un nome utente, un'email, una password, un ID univoco e una data di creazione.
     * L'utente viene inizialmente impostato come non bannato.
     *
     * @param username Il nome utente dell'utente.
     * @param email L'email dell'utente.
     * @param password La password dell'utente.
     * @param ID L'ID univoco dell'utente.
     * @param createdAt La data di creazione dell'utente.
     */
    public User(String username, String email, String password, int ID, String createdAt, String ruolo, int score, int banned) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.ID = ID;
        this.createdAt = createdAt;

        if(banned == 1)
            this.banned = true;
        else
            this.banned = false;

        this.ruolo = ruolo;
        this.score = score;
    }

    public int getID() {
        return ID;
    }

    /**
     * Restituisce il nome utente dell'utente.
     *
     * @return Il nome utente dell'utente.
     */
    public String getName() {
        return username;
    }

    /**
     * Restituisce l'email dell'utente.
     *
     * @return L'email dell'utente.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Imposta un nuovo nome utente per l'utente.
     *
     * @param username Il nuovo nome utente.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Imposta una nuova email per l'utente.
     *
     * @param email La nuova email dell'utente.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Imposta una nuova password per l'utente.
     *
     * @param password La nuova password dell'utente.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Imposta lo stato di ban dell'utente.
     *
     * @param banned Lo stato di ban dell'utente. {@code true} indica che l'utente è bannato, {@code false} altrimenti.
     */
    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    /**
     * Restituisce la password dell'utente.
     *
     * @return La password dell'utente.
     */
    public String getPassword() {
        return password;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Restituisce una proprietà di tipo {@link SimpleStringProperty} per il nome utente, utile per l'integrazione con JavaFX.
     *
     * @return La proprietà {@link SimpleStringProperty} per il nome utente.
     */
    public SimpleStringProperty usernameProperty() {
        return new SimpleStringProperty(username);
    }

    /**
     * Restituisce una proprietà di tipo {@link SimpleStringProperty} per l'email, utile per l'integrazione con JavaFX.
     *
     * @return La proprietà {@link SimpleStringProperty} per l'email.
     */
    public SimpleStringProperty emailProperty() {
        return new SimpleStringProperty(email);
    }

    /**
     * Restituisce una proprietà di tipo {@link SimpleStringProperty} per la password, utile per l'integrazione con JavaFX.
     *
     * @return La proprietà {@link SimpleStringProperty} per la password.
     */
    public SimpleStringProperty passwordProperty() {
        return new SimpleStringProperty(password);
    }

    /**
     * Restituisce una proprietà di tipo {@link SimpleStringProperty} per la data di creazione dell'utente,
     * utile per l'integrazione con JavaFX.
     *
     * @return La proprietà {@link SimpleStringProperty} per la data di creazione.
     */
    public SimpleStringProperty createdAtProperty() {
        return new SimpleStringProperty(createdAt);
    }

    /**
     * Restituisce una proprietà di tipo {@link SimpleStringProperty} per l'ID dell'utente, utile per l'integrazione con JavaFX.
     *
     * @return La proprietà {@link SimpleStringProperty} per l'ID.
     */
    public SimpleStringProperty idProperty() {
        return new SimpleStringProperty(String.valueOf(ID));
    }

    /**
     * Restituisce una proprietà di tipo {@link SimpleBooleanProperty} per lo stato di ban dell'utente, utile per l'integrazione con JavaFX.
     *
     * @return La proprietà {@link SimpleBooleanProperty} per lo stato di ban dell'utente.
     */
    public SimpleBooleanProperty bannedProperty() {
        return new SimpleBooleanProperty(banned);
    }

    public SimpleStringProperty ruoloProperty() {
        return new SimpleStringProperty(ruolo);
    }

    public SimpleStringProperty scoreProperty() {
        return new SimpleStringProperty(String.valueOf(score));
    }
}

