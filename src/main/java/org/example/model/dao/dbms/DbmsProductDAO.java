package org.example.model.dao.dbms;

import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.entity.product.Product;

import java.util.ArrayList;
import java.util.List;

public class DbmsProductDAO implements ProductDAO {

    @Override
    public boolean save(Product product) {
        return false;
    }

    @Override
    public Product getById(String id) {
        return null;
    }

    @Override
    public List<Product> getAll() {
        return new ArrayList<>();
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}