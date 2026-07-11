package org.example.model.dao.demo;


import org.example.model.dao.abstractfactory.DAOFactory;
import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.dao.abstractfactory.UserDAO;

public class DemoDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new UserDemoDAO();
    }

    @Override
    public ProductDAO getProductDAO() {return new ProductDemoDAO();}

    @Override
    public RentalDAO getRentalDAO() {
        return new RentalDemoDAO();
    }

    @Override
    public SessionDAO getSessionDAO() {return new SessionDemoDAO();}
}
