package org.example.model.dao.abstractfactory;


import java.util.List;

public interface EntityDAO<T> {
    T getById(String id);
    boolean save(T entity);
    boolean delete(String id);
    List<T> getAll();
}