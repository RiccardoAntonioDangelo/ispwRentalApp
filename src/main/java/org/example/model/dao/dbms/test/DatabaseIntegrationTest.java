package org.example.model.dao.dbms.test;

import org.example.model.dao.dbms.*;
import org.example.model.entity.LazyEntityList;
import org.example.model.entity.actors.simple.Owner;
import org.example.model.entity.product.ConditionEnum;
import org.example.model.entity.product.Product;
import org.example.model.entity.rental.RentalOld;
import org.example.model.entity.rental.StatusEnum;
import org.example.model.entity.session.Session;

import java.time.LocalDate;

public class DatabaseIntegrationTest {

    public static void main(String[] args) {
        System.out.println("=== INIZIO TEST INTEGRALE DBMS (Deep Verification) ===");

        try {
            // --- 1. SETUP DAO ---
            DbmsUserDAO userDAO = new DbmsUserDAO();
            DbmsProductDAO productDAO = new DbmsProductDAO();
            DbmsRentalDAO rentalDAO = new DbmsRentalDAO();

            DbmsSessionDAO sessionDAO = new DbmsSessionDAO();



            String testEmail = "TEST@gemini.com";

            // Pulizia iniziale (per evitare conflitti con TEST precedenti)
            userDAO.delete(testEmail);

            // --- 2. TEST OWNER ---
            Owner owner = new Owner(testEmail, "securePass123");
            owner.setName("Mario");
            owner.setSurname("Rossi");
            owner.setPhone("3331234567");

            System.out.print("[USER] Salvataggio... ");
            userDAO.save(owner);

            Owner fetchedOwner = (Owner) userDAO.getById(testEmail);
            verifyField("User Email", testEmail, fetchedOwner.getEmail());
            verifyField("User Name", "Mario", fetchedOwner.getName());
            verifyField("User Surname", "Rossi", fetchedOwner.getSurname());
            verifyField("User Phone", "3331234567", fetchedOwner.getPhone());

            // --- 3. TEST PRODUCT ---
            Product product = new Product(testEmail, "Trapano Professionale", 25.99);
            product.setDescription("Trapano a percussione 18V");
            product.setCondition(ConditionEnum.NEW);
            product.setCategory("Elettroutensili");
            product.setImageUrl("http://image.com/trapano.png");
            product.setAvailable(true);

            System.out.print("[PRODUCT] Salvataggio... ");
            productDAO.save(product);

            Product fetchedProduct = productDAO.getById(product.getId());
            verifyField("Product ID", product.getId(), fetchedProduct.getId());
            verifyField("Product Title", "Trapano Professionale", fetchedProduct.getTitle());
            verifyField("Product Price", 25.99, fetchedProduct.getDailyPrice());
            verifyField("Product Condition", ConditionEnum.NEW, fetchedProduct.getCondition());
            verifyField("Product Available", true, fetchedProduct.isAvailable());

            // --- 4. TEST RENTAL ---
            LocalDate start = LocalDate.now().plusDays(1);
            LocalDate end = LocalDate.now().plusDays(5);
            RentalOld rental = new RentalOld(testEmail,"33333333333", product.getId(),"RR", start, end);
            rental.setOwnerEmail(testEmail);
            //rental.setTotalCost(103.96);
            rental.setLocate("Roma, Via Roma 1");
            rental.setStatus(StatusEnum.PENDING);
            //rental.setPayment("PAY-999");

            System.out.print("[RENTAL] Salvataggio... ");
            rentalDAO.save(rental);

            RentalOld fetchedRental = rentalDAO.getById(rental.getId());
            verifyField("Rental ID", rental.getId(), fetchedRental.getId());
            verifyField("Rental StartDate", start, fetchedRental.getStartDate());
            //verifyField("Rental TotalCost", 103.96, fetchedRental.getTotalCost());
            verifyField("Rental Status", StatusEnum.PENDING, fetchedRental.getStatus());
            verifyField("Rental PaymentID", "PAY-999", fetchedRental.getPayment());

            // --- 5. TEST SESSION ---
            Session session = new Session(owner);

            LazyEntityList<Product> cart = new LazyEntityList<>();
            cart.add(new Product("prod_001", "Smartphone", 7993.0));
            cart.add(product);
            session.addCollection("cart", cart);
            // Qui verifichiamo se l'email viene gestita correttamente (il tuo problema testgemini)
            System.out.print("[SESSION] Salvataggio... ");
            sessionDAO.save(session);

            Session fetchedSession = sessionDAO.getById(testEmail);
            verifyField("Session ID (Email)", testEmail, fetchedSession.getId());

            System.out.println("\n✅ TEST COMPLETATO: Tutti i campi corrispondono!");

        } catch (Exception e) {
            System.err.println("\n❌ TEST FALLITO!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Metodo di utility per verificare l'uguaglianza dei campi e segnalare errori specifici
     */
    private static void verifyField(String fieldName, Object expected, Object actual) throws Exception {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) {
            return;
        }
        throw new Exception(String.format("Mismatch nel campo [%s]. Atteso: <%s>, Ricevuto: <%s>",
                fieldName, expected, actual));
    }
}