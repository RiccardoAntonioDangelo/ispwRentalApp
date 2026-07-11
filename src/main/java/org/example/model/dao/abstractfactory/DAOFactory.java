package org.example.model.dao.abstractfactory;

import org.example.model.dao.dbms.DbmsDAOFactory;
import org.example.model.dao.demo.DemoDAOFactory;
import org.example.model.dao.file.dao.FileDAOFactory;
import org.example.model.dao.filejson.dao.FileJsonDAOFactory;

public abstract class DAOFactory {

    public abstract UserDAO getUserDAO();
    public abstract ProductDAO getProductDAO();
    public abstract RentalDAO getRentalDAO();
    public abstract SessionDAO getSessionDAO();
    @SuppressWarnings("unchecked")
    public <T extends EntityDAO<?>> T getEntity(EntityType type){
        if (type == null) {throw new IllegalArgumentException("Il tipo Entity non può essere null.");}
        return (T) switch (type) {
            case USER -> this.getUserDAO();
            case PRODUCT -> this.getProductDAO();
            case RENTAL -> this.getRentalDAO();
            case SESSION -> this.getSessionDAO();
        };
    }
    public static DAOFactory getDAOFactory(EnumDaoType type) {
        if (type == null) {throw new IllegalArgumentException("Il tipo DAO non può essere null.");}
        return switch (type) {
            case DEMO -> new DemoDAOFactory();
            case FILE -> new FileJsonDAOFactory();
            case DBMS -> new DbmsDAOFactory();
        };
    }
    public static <T extends EntityDAO<?>> T getDAOFactory(EnumDaoType daoType,EntityType entityType){
       return DAOFactory.getDAOFactory(daoType).getEntity(entityType);
    }
    public static DAOFactory getDAOFactory(int whichFactory) {
        return getDAOFactory(EnumDaoType.fromValue(whichFactory));
    }

}
