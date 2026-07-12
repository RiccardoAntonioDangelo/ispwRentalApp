package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.entity.rental.RentalOld;
import org.example.model.services.rent.RentI;

import java.util.ArrayList;
import java.util.List;

public class DbmsRentalDAO implements RentalDAO {

    @Override
    public boolean save(RentI rental) {
        return false;
    }

    @Override
    public RentalOld getById(String id) {
        return null;
    }

    public List<RentI> getAll() {
        return new ArrayList<>();
    }

    public List<RentalOld> getRentalsByUserId(String userId) {
        if(userId.isEmpty())
            return new ArrayList<>();
        return new ArrayList<>();
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}