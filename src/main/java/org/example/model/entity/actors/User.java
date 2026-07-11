package org.example.model.entity.actors;

import org.example.model.services.user.UserI;

public class User implements UserI {
    private String email;
    private String name;
    private String surname;
    private String password;
    private String phone;

    public User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public boolean isValid(String password) {
        return this.password.equals(password);
    }

    /**
     * Spara la notifica verso il proxy dell'ObserverDAO.
     * Funziona perché UserI estende EntityI, che a sua volta fornisce notifyObservers().
     */
    public void commitChange() {
        this.notifyObservers(this);
    }

    // ========================================
    // GETTER E SETTER CON AUTO-PERSISTENZA
    // ========================================

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        this.commitChange(); // Notifica il cambio al Proxy
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.commitChange();
    }

    public String getSurname() { return surname; }
    public void setSurname(String surname) {
        this.surname = surname;
        this.commitChange();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
        this.commitChange();
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
        this.commitChange();
    }

    // ========================================
    // IDENTIFICATIVO UNICO (Ereditato via UserI -> EntityI)
    // ========================================
    @Override
    public String getId() {
        return getEmail();
    }

    public static User create(String email, String password, String role) {
        return UserI.createUser(email, password, role);
    }


    @Override
    public UserI getRole() {
        return this;
    }
}