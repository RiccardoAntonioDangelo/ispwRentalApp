package org.example.model.dao.demo;

import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.entity.session.Session;

public class SessionDemoDAO extends DemoEntityDAO<Session> implements SessionDAO {
    public SessionDemoDAO() {super(  Session.class, Session::getId);}
}
