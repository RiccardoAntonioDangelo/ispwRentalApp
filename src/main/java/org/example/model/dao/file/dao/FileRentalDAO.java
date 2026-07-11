package org.example.model.dao.file.dao;

import org.example.model.dao.abstractfactory.RentalDAO;
import org.example.model.services.rent.RentI;

public class FileRentalDAO extends FileEntityDAO<RentI> implements RentalDAO {
    public FileRentalDAO() {super(RentI.class,RentI::getClientEmail);}
}