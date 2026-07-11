package org.example.model.dao.proxy.observer;

import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.entity.session.Session;

public class ObserverSessionDAO extends ObserverDAOProxy<Session> implements SessionDAO {
    public ObserverSessionDAO(SessionDAO realDAO) {
        super(realDAO);
    }
}