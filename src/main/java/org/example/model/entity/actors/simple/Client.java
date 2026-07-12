package org.example.model.entity.actors.simple;


import org.example.model.entity.actors.User;
import org.example.model.services.rent.ActionsClientRentI;

public class Client extends User implements  ActionsClientRentI {
    public Client() { super();}
    public Client(String email, String password) {super(email, password);}
}