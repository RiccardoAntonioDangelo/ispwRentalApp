package org.example.model.dao.demo;

import org.example.model.dao.demo.utility.PersistenceManager;
import org.example.model.dao.demo.utility.PersistenceManagerProxy;

import java.util.List;
import java.util.function.Function;

public  class DemoEntityDAO<T> {

    protected final PersistenceManager persistenceManager=new PersistenceManagerProxy();
    protected final Class<T> entityClass;
    protected final Function<T, String> idExtractor;

    protected DemoEntityDAO(Class<T> entityClass, Function<T, String> idExtractor) {
        this.entityClass = entityClass;
        this.idExtractor = idExtractor;
    }

    public T getById(String id) {
        return persistenceManager.get(entityClass, id);
    }
    public boolean delete(String id) {
        return persistenceManager.delete(entityClass ,id);
    }

    public boolean save(T entity) {
        if (entity == null) return false;
        String id = idExtractor.apply(entity);
        persistenceManager.save(id, entity);
        return true;
    }

    public List<T> getAll() {
        return persistenceManager.getAll(entityClass);
    }

}