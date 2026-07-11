package org.example.model.dao.proxy.observer;

import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.entity.product.Product;

public class ObserverProductDAO extends ObserverDAOProxy<Product> implements ProductDAO {
    public ObserverProductDAO(ProductDAO realDAO) {
        super(realDAO);
    }
}