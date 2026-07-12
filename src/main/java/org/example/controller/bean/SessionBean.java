package org.example.controller.bean;

import org.example.model.entity.session.Session;

import java.io.Serializable;

/**
 * Rappresenta lo stato della sessione da passare alla View.
 * È una versione semplificata della classe Session per il trasporto dati.
 */
public class SessionBean implements Serializable {
    Session session;
    public SessionBean() {}
    public SessionBean(Session session) {
        this.session=session;
    }

    public String getUser(){
        if(session!=null) return session.getUserid();
        return null;
    }


    public Session getSession() {return session;}

    public boolean isValid() {
        return  session.isValid();
    }

    public void logout() {
        session=new Session();
    }
}