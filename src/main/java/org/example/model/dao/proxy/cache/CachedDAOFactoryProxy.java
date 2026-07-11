package org.example.model.dao.proxy.cache;

import org.example.model.dao.abstractfactory.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedDAOFactoryProxy extends DAOFactory {

    private final DAOFactory realFactory;
    private final Map<Class<?>, Object> daoCache = new ConcurrentHashMap<>();

    public CachedDAOFactoryProxy(DAOFactory realFactory) {
        this.realFactory = realFactory;
    }

    @Override
    public UserDAO getUserDAO() {
        return (UserDAO) daoCache.computeIfAbsent(UserDAO.class,
                k -> new CachedUserDAO(realFactory.getUserDAO()));
    }

    @Override
    public ProductDAO getProductDAO() {
        return (ProductDAO) daoCache.computeIfAbsent(ProductDAO.class,
                k -> new CachedProductDAO(realFactory.getProductDAO()));
    }

    @Override
    public RentalDAO getRentalDAO() {
        return (RentalDAO) daoCache.computeIfAbsent(RentalDAO.class,
                k -> new CachedRentalDAO(realFactory.getRentalDAO()));
    }

    @Override
    public SessionDAO getSessionDAO() {
        return (SessionDAO) daoCache.computeIfAbsent(SessionDAO.class,
                k -> new CachedSessionDAO(realFactory.getSessionDAO()));
    }
}