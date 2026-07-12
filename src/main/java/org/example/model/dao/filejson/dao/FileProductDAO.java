package org.example.model.dao.filejson.dao;

import org.example.model.dao.abstractfactory.ProductDAO;
import org.example.model.entity.product.Product;

class FileProductDAO extends FileEntityDAO<Product> implements ProductDAO {
    public FileProductDAO() {super(Product.class,Product::getId);}
}