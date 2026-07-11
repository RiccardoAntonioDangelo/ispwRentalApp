package org.example.model.entity.actors.simple;

import org.example.model.services.product.ActionsProductI;
import org.example.model.services.rent.ActionsOwnerRentI;

public class Owner extends Client implements ActionsProductI, ActionsOwnerRentI {
    public Owner() {super();}
    public Owner(String email, String password) {super(email, password);}

}