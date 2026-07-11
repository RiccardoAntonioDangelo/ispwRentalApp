package org.example.model.dao.proxy.cache;

import org.example.model.dao.abstractfactory.EntityDAO;
import org.example.model.services.EntityI;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedDAOProxy<T> implements EntityDAO<T> {
    private final EntityDAO<T> realDAO;
    private final Map<String, T> cache = new ConcurrentHashMap<>();

    public CachedDAOProxy(EntityDAO<T> realDAO) {
        this.realDAO = realDAO;
    }

    @Override
    public T getById(String id) {
        return cache.computeIfAbsent(id, realDAO::getById);
    }

    @Override
    public boolean save(T entity) {
        boolean success = realDAO.save(entity);
        if (success) {
          EntityI<String> id=(EntityI) entity;
          cache.put(id.getId(),entity);
        }
        return success;
    }

    @Override
    public boolean delete(String id) {
        boolean success = realDAO.delete(id);
        if (success) {
            cache.remove(id);
        }
        return success;
    }

    @Override
    public List<T> getAll() {
        List<T> entities = realDAO.getAll();
        for (T entity : entities) {
            if (entity instanceof EntityI) {
                EntityI<String> idProvider = (EntityI<String>) entity;
                cache.put(idProvider.getId(), entity);
            }
        }
        return entities;
    }
}