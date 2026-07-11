package org.example.model.dao.demo;

import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.services.rent.RentI;

public class RentalDemoDAO extends DemoEntityDAO<RentI> implements RentalDAO {
    public RentalDemoDAO() {super( RentI.class, RentI::getId);}
}