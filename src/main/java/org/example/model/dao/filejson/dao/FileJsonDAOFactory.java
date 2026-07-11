package org.example.model.dao.filejson.dao;

import org.example.model.dao.abstractfactory.*;

/**
 * Concrete Factory per la gestione dei dati su File JSON.
 */
public class FileJsonDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new FileUserDAO();
    }

    @Override
    public ProductDAO getProductDAO() {
        return new FileProductDAO();
    }

    @Override
    public RentalDAO getRentalDAO() {
        return new FileRentalDAO();
    }

    @Override
    public SessionDAO getSessionDAO() {
        return new FileSessionDAO();
    }
}
