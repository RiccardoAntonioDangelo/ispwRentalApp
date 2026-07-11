package org.example.model.dao.proxy.observer;

import org.example.model.dao.abstractfactory.*;

public class ObserverDAOFactoryProxy extends DAOFactory {

    private final DAOFactory realFactory;
    public ObserverDAOFactoryProxy(DAOFactory realFactory) {
        this.realFactory = realFactory;
    }

    @Override
    public UserDAO getUserDAO() {
        return new ObserverUserDAO(realFactory.getUserDAO());
    }

    @Override
    public ProductDAO getProductDAO() {
        return new ObserverProductDAO(realFactory.getProductDAO());
    }

    @Override
    public RentalDAO getRentalDAO() {
        return new ObserverRentalDAO(realFactory.getRentalDAO());
    }

    @Override
    public SessionDAO getSessionDAO() {
        return new ObserverSessionDAO(realFactory.getSessionDAO());
    }
}