package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.DAOFactory;
import org.example.model.dao.abstractfactory.*;

public class DbmsDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new DbmsUserDAO();
    }

    @Override
    public ProductDAO getProductDAO() {
        return new DbmsProductDAO();
    }

    @Override
    public RentalDAO getRentalDAO() {
        return new DbmsRentalDAO();
    }

    @Override
    public SessionDAO getSessionDAO() {
        return new DbmsSessionDAO();
    }
}