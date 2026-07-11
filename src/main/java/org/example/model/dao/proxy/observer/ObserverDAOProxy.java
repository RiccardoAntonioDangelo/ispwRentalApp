package org.example.model.dao.proxy.observer;

import org.example.model.dao.abstractfactory.EntityDAO;
import org.example.model.services.EntityI;
import org.example.util.observer.ObserverI;
import org.example.util.observer.SubjectI;

import java.util.List;

public class ObserverDAOProxy<T extends EntityI<?>> implements EntityDAO<T>, ObserverI<T> {
    private final EntityDAO<T> realDAO;

    public ObserverDAOProxy(EntityDAO<T> realDAO) {
        this.realDAO = realDAO;
    }

    @Override
    public T getById(String id) {
        T entity = realDAO.getById(id);
        if (entity != null) {((SubjectI) entity).attach(this);}
        return entity;
    }

    @Override
    public boolean save(T entity) {
        return realDAO.save(entity);
    }

    @Override
    public boolean delete(String id) {
        return realDAO.delete(id);
    }

    @Override
    public List<T> getAll() {
        List<T> entities = realDAO.getAll();
        if (entities != null) {
            for (T entity : entities) {
                if (entity != null) {((SubjectI) entity).attach(this);}
            }
        }
        return entities;
    }

    @Override
    public void update(T eventData) {
        if (eventData != null) {
            save(eventData);
        }
    }
}