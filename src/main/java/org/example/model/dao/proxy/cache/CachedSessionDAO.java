package org.example.model.dao.proxy.cache;

import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.entity.session.Session;

public class CachedSessionDAO extends CachedDAOProxy<Session> implements SessionDAO {
    public CachedSessionDAO(SessionDAO realDAO) {
        super(realDAO);
    }
}