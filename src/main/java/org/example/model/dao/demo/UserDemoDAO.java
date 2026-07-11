package org.example.model.dao.demo;

import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;

public class UserDemoDAO extends DemoEntityDAO<User> implements UserDAO {
    public UserDemoDAO() {super(User.class, User::getEmail);}
}