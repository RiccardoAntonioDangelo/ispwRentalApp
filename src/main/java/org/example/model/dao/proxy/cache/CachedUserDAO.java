package org.example.model.dao.proxy.cache;

import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;

public class CachedUserDAO extends CachedDAOProxy<User> implements UserDAO {
    public CachedUserDAO(UserDAO realDAO) {
        super(realDAO);
    }
}