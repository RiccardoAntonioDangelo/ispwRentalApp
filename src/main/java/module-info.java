module org.example {
    // --- MODULI CORE E STRUMENTI ---
    requires java.desktop;
    requires java.sql;
    requires java.naming;
    requires jdk.jdi;
    requires mysql.connector.j;

    // --- MODULI JAVAFX ---
    requires javafx.controls;
    requires javafx.fxml;

    // --- MODULI JACKSON ---
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    // Modulo per LocalDate / LocalDateTime
    requires com.fasterxml.jackson.datatype.jsr310;

    // --- APERTURA MODELLI A JACKSON (Riflessione per JSON e JavaTimeModule) ---
    // NOTA: Con la sintassi "opens ... to com.fasterxml.jackson.databind;"
    // permettiamo a Jackson (e ai suoi moduli derivati) di accedere ai costruttori e campi privati.
    opens org.example.model.entity to com.fasterxml.jackson.databind;
    opens org.example.model.entity.actors to com.fasterxml.jackson.databind;
    opens org.example.model.entity.actors.simple to com.fasterxml.jackson.databind;
    opens org.example.model.entity.actors.factory to com.fasterxml.jackson.databind;
    opens org.example.model.entity.actors.strategy to com.fasterxml.jackson.databind;
    opens org.example.model.entity.rental to com.fasterxml.jackson.databind;
    opens org.example.model.entity.session to com.fasterxml.jackson.databind;
    opens org.example.model.entity.product to com.fasterxml.jackson.databind;
    opens org.example.model.entity.util to com.fasterxml.jackson.databind;
    opens org.example.controller.application.login to com.fasterxml.jackson.databind;
    opens org.example.util.observer to com.fasterxml.jackson.databind;

    // --- ESPORTAZIONE MODELLI ---
    exports org.example.model.entity.actors;
    exports org.example.model.entity.actors.simple;
    exports org.example.model.entity.actors.factory;
    exports org.example.model.entity.rental;
    exports org.example.model.entity.session;
    exports org.example.model.entity.product;
    exports org.example.controller.application.login;

    // --- CONFIGURAZIONE JAVAFX (View / Controller) ---
    exports org.example;
    opens org.example to javafx.fxml;

    exports org.example.view;
    opens org.example.view to javafx.fxml;

    exports org.example.view.javafx.util.nav;
    opens org.example.view.javafx.util.nav to javafx.fxml;

    exports org.example.view.javafx.componet;
    opens org.example.view.javafx.componet to javafx.fxml;

    exports org.example.view.javafx.util;
    opens org.example.view.javafx.util to javafx.fxml;

    exports org.example.util.str;
    opens org.example.util.str to javafx.fxml;

    exports org.example.view.javafx.main.test;
    opens org.example.view.javafx.main.test to javafx.fxml, javafx.graphics;

    exports org.example.view.javafx.main;
    opens org.example.view.javafx.main to javafx.fxml;

    exports org.example.view.javafx.gc;
    opens org.example.view.javafx.gc to javafx.fxml;

    exports org.example.view.javafx;
    opens org.example.view.javafx to javafx.fxml;
}