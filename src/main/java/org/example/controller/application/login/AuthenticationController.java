package org.example.controller.application.login;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;
import org.example.model.entity.session.Session;

public class AuthenticationController {
     private AuthenticationController() {}

    public static Session authenticate(String email, String password) {
        Session session = getSession(email);
        if (session != null && session.isValid(password)) {return session;}
        return null;
    }

    public  static User getUser(String id) {return getUserDAO().getById(id);}
    public  static boolean saveUser(User user) {return getUserDAO().save(user);}
    private static UserDAO getUserDAO(){return DAOManager.getUserDAO();}

    public static Session getSession(String id) {return getSessionDAO().getById(id);}
    public static boolean saveSession(Session session) {return getSessionDAO().save(session);}
    private static SessionDAO getSessionDAO(){return DAOManager.getSessionDAO();}

}