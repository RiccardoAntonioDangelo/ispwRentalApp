package org.example.model.dao.proxy.cache;

import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.entity.product.Product;

public class CachedProductDAO extends CachedDAOProxy<Product> implements ProductDAO {
    public CachedProductDAO(ProductDAO realDAO) {
        super(realDAO);
    }
}