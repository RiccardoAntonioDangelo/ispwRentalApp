package org.example.controller.bean;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.example.model.entity.actors.User;

public class UserBean  {
    public UserBean(User user){
        this.fromEntity(user);
    }
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty fullName = new SimpleStringProperty("");
    private final StringProperty role = new SimpleStringProperty("");
    private final StringProperty phone = new SimpleStringProperty("");

    public StringProperty emailProperty() { return email; }
    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty roleProperty() { return role; }
    public StringProperty phoneProperty() { return phone; }


    public void fromEntity(User user) {
        this.email.set(user.getEmail());
        this.fullName.set(user.getName() + " " + user.getSurname());
        this.role.set(user.getRole().toString());
        this.phone.set(user.getPhone());
    }

    public String getEmail() {
        return email.get();
    }
    public String getFullName() {
        return fullName.get();
    }
    public String getRole() {
        return role.get();
    }
    public String getPhone() {
        return phone.get();
    }

}