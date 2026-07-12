package org.example.model.dao;

import org.example.model.dao.abstractfactory.*;
import org.example.model.dao.proxy.cache.CachedDAOFactoryProxy;
import org.example.model.dao.proxy.observer.ObserverDAOFactoryProxy;
import org.example.util.singleton.SingletonI;

public class DAOManager implements SingletonI {
    private final DAOFactory activeFactory;
    private static boolean sigleton=false;
    // Costruttore privato
    private DAOManager(EnumDaoType type, boolean useCache) {
        DAOFactory realFactory = DAOFactory.getDAOFactory(type);
        if (useCache) {realFactory = new CachedDAOFactoryProxy(realFactory);}
        this.activeFactory=new ObserverDAOFactoryProxy(realFactory);

    }
    public static boolean isInitialize() {
        return sigleton;
    }


    /**
     * Inizializzazione globale. Qui decidi UNA VOLTA per tutto il ciclo di vita
     * dell'app se vuoi la cache attiva.
     */
    public static void initializeSingleton(EnumDaoType type, boolean useCache) {SingletonI.registerSingleton(new DAOManager(type, useCache));sigleton=true;}
    public static void initializeSingleton(EnumDaoType type, boolean useCache, boolean demoData) {
        initializeSingleton(type,useCache);
        if (demoData)DemoDataInitializer.loadDemoData();
    }

    public static DAOManager getSingletonInstance() {return SingletonI.getSingleton(DAOManager.class);}


    public static UserDAO getUserDAO() {
        return getSingletonInstance().activeFactory.getUserDAO();
    }
    public static ProductDAO getProductDAO() {
        return  getSingletonInstance().activeFactory.getProductDAO();
    }
    public static RentalDAO getRentalDAO() {
        return getSingletonInstance().activeFactory.getRentalDAO();
    }
    public static SessionDAO getSessionDAO() {
        return getSingletonInstance().activeFactory.getSessionDAO();
    }
    public static <T extends EntityDAO<?>> T getEntity(EntityType type){return getSingletonInstance().activeFactory.getEntity(type);}
    public static <T extends EntityDAO<?>> T getEntity(Class<?> type){return getEntity(EntityType.fromClass(type));}

}