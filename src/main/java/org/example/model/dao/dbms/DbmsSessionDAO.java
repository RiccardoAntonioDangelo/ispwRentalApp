package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.entity.session.Session;

import java.util.ArrayList;
import java.util.List;

public class DbmsSessionDAO implements SessionDAO {

    @Override
    public boolean save(Session session) {
        return false;
    }

    @Override
    public Session getById(String id) {
        return null;
    }

    @Override
    public List<Session> getAll() {
        return new ArrayList<>();
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}