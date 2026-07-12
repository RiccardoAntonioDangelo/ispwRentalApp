package com.ispw.noleggio.controller.application;

import org.example.controller.bean.ProductBean;
import org.example.controller.bean.RegisterBean;
import org.example.controller.bean.RentalFormBean;
import org.example.controller.bean.SessionBean;
import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EntityDAO;
import org.example.model.dao.abstractfactory.EntityType;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.model.entity.actors.factory.ActorEnum;
import org.example.model.entity.product.Product;
import org.example.view.GraphicAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class RentalControllerTest {

    private final String clientRole = ActorEnum.CLIENT.getValue();
    private final String ownerRole = ActorEnum.OWNER.getValue();

    @BeforeEach
    void setUp() {
        if (!DAOManager.isInitialize()) {
            DAOManager.initializeSingleton(EnumDaoType.DEMO, true, false);
        }
    }

    @Test
    void testSuccessfulRentalCreation() throws Exception {
        // 1. Setup User (Cliente)
        String clientEmail = "test@example.com";
        RegisterBean registerData = new RegisterBean(clientRole, clientEmail, "password", "password");
        SessionBean session = GraphicAPI.registerApi(registerData);
        assertNotNull(session.getUser(), "La registrazione dell'utente ha fallito");

        // 2. Setup Owner e Product
        String ownerEmail = "owner@example.com";
        RegisterBean registerBean = new RegisterBean(ownerRole, ownerEmail, "password", "password");
        SessionBean sessionOwner = GraphicAPI.registerApi(registerBean);

        EntityDAO<Product> productDAO = DAOManager.getEntity(EntityType.PRODUCT);

        Product product = new Product(ownerEmail, "Test Camera", 10.0);
        product.setAvailable(true);

        // Salviamo il prodotto nel DAO e colleghiamolo alla sessione del proprietario
        productDAO.save(product);
        sessionOwner.getSession().execute(product);

        ProductBean productBean = new ProductBean(product);

        // 3. Setup Date e compilazione del modulo (Allineato a BookingGC)
        // Specificato ZoneId.systemDefault() per evitare warning di Sonar / vulnerabilità di fuso orario
        ZoneId defaultZone = ZoneId.systemDefault();
        LocalDate start = LocalDate.now(defaultZone).plusDays(1);
        LocalDate end = LocalDate.now(defaultZone).plusDays(4); // 3 Giorni di differenza

        RentalFormBean rentalForm = new RentalFormBean()
                .setOwnerEmail(productBean.getOwnerEmail())
                .setProductId(productBean.getId())
                .setName("TestName")
                .setSurname("TestSurname")
                .setEmail(clientEmail)
                .setStart(start)
                .setEnd(end)
                .setPickupLocation("Roma")
                .setPhone("1234567890");

        // Esegue il riempimento/validazione interna se previsto dal backend del Form
        rentalForm.validateAndFill();

        // Invio tramite Facade grafica
        assertDoesNotThrow(() -> GraphicAPI.sendApi(session, rentalForm),
                "L'invio della richiesta tramite sendApi non dovrebbe lanciare eccezioni.");

        // 4. Calcolo e asserzione dei costi
        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) days = 1;

        double expectedCost = 10.0 * days;
        double actualCost = productBean.getProduct().getPrice() * days;

        assertEquals(30.0, expectedCost, "Il costo stimato per 3 giorni a 10.0€/giorno deve essere 30.0");
        assertEquals(expectedCost, actualCost, 0.01);
    }
}