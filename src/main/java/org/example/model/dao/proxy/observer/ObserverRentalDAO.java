package org.example.model.dao.proxy.observer;

import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.services.rent.RentI;

public class ObserverRentalDAO extends ObserverDAOProxy<RentI> implements RentalDAO {
    public ObserverRentalDAO(RentalDAO realDAO) {
        super(realDAO);
    }
}