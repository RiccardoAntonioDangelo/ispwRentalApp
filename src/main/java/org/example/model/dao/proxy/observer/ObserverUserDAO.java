package org.example.model.dao.proxy.observer;

import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;

public class ObserverUserDAO extends ObserverDAOProxy<User> implements UserDAO {
    public ObserverUserDAO(UserDAO realDAO) {
        super(realDAO);
    }
}