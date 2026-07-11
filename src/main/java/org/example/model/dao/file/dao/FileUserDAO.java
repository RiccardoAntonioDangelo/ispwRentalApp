package org.example.model.dao.file.dao;

import org.example.model.dao.abstractfactory.UserDAO;
import org.example.model.entity.actors.User;

public class FileUserDAO extends FileEntityDAO<User> implements UserDAO {
    public FileUserDAO() { super(User.class,User::getEmail); }

}