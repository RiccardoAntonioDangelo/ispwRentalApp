package org.example.model.dao.demo;

import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.entity.product.Product;

public class ProductDemoDAO extends DemoEntityDAO<Product> implements ProductDAO {
    public ProductDemoDAO() {super(Product.class, Product::getId);}
}