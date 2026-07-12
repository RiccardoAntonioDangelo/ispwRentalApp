package com.ispw.noleggio.controller.application;

import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.model.entity.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductControllerTest {

    @BeforeEach
    void setUp() {
        if (!DAOManager.isInitialize())
            DAOManager.initializeSingleton(EnumDaoType.DEMO, true, false);
    }
    @Test
    void testGetAllProducts() {
        Product p = new Product();
        p.setTitle("Test All Product");
        p.setAvailable(true);
        p.setCategory("General");
        DAOManager.getProductDAO().save(p);

        List<Product> allProducts = DAOManager.getProductDAO().getAll();

        assertNotNull(allProducts, "La lista dei prodotti non deve essere null");
        assertFalse(allProducts.isEmpty(), "La lista dei prodotti non deve essere vuota");
        assertTrue(allProducts.stream().anyMatch(prod -> prod.getId().equals(p.getId())),
                   "Il prodotto inserito deve essere presente nella lista");
    }

    @Test
    void testGetProductById() {
        // Setup
        Product p = new Product();
        p.setTitle("Specific Product");
        p.setCategory("Specific");
        // Test: Prodotto esistente
        DAOManager.getProductDAO().save(p);

        Product found =  DAOManager.getProductDAO().getById(p.getId());
        assertNotNull(found, "Il prodotto dovrebbe essere trovato");
        assertEquals("Specific Product", found.getTitle());

        // Test: Prodotto non esistente
        Product notFound =DAOManager.getProductDAO().getById("non_existent_id");
        assertNull(notFound, "La ricerca di un ID inesistente dovrebbe restituire null");
    }


}
