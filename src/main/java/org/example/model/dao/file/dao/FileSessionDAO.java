package org.example.model.dao.file.dao;


import org.example.model.dao.abstractfactory.SessionDAO;
import org.example.model.entity.session.Session;

public class FileSessionDAO  extends FileEntityDAO<Session> implements SessionDAO {
    public FileSessionDAO() {super( Session.class, Session::getId);}
}
