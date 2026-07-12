package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;

import java.util.ArrayList;
import java.util.List;

public class DbmsUserDAO implements UserDAO {

    @Override
    public boolean save(User user) {
        return false;
    }

    @Override
    public User getById(String email) {
        return null;
    }

    @Override
    public boolean delete(String email) {
        return false;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>();
    }
}