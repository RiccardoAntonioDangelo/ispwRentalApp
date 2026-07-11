package org.example.model.dao.proxy.cache;

import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.services.rent.RentI;

public class CachedRentalDAO extends CachedDAOProxy<RentI> implements RentalDAO {
    public CachedRentalDAO(RentalDAO realDAO) {
        super(realDAO);
    }
}