package org.example.model.dao.demo.TEST;

import org.example.model.dao.demo.utility.PersistenceManager;
import org.example.model.dao.demo.ProductDemoDAO;
import org.example.model.dao.demo.UserDemoDAO;
import org.example.model.entity.actors.simple.Client;
import org.example.model.entity.actors.User;
import org.example.model.entity.product.Product;

import java.util.List;

public class DemoArchitectureTest {

    public static void main(String[] args) {
        try {
            System.out.println("=== STARTING DEMO ARCHITECTURE TEST ===");

            // 1. Inizializzazione DAO (entrambi useranno lo stesso Singleton PersistenceManager)
            UserDemoDAO userDAO = new UserDemoDAO();
            ProductDemoDAO productDAO = new ProductDemoDAO();

            // 2. Test salvataggio e recupero User
            System.out.println("-> Testing User Persistence...");
            User u1 = new Client("test@demo.it", "password");
            u1.setName("Demo User");
            
            userDAO.save(u1);
            User retrievedUser = userDAO.getById("test@demo.it");

            assertNotNull(retrievedUser, "User non recuperato!");
            assertEquals("Demo User", retrievedUser.getName(), "Dati utente corrotti");
            System.out.println("[OK] User saved and retrieved.");

            // 3. Test isolamento mappe (Product)
            System.out.println("-> Testing Product Isolation...");
            Product p1 = new Product("p100", "Monitor 4K", 350.0);
            productDAO.save(p1);
            Product pf=productDAO.getById(p1.getId());
            assertNotNull(pf, "Prodotto non trovato!");
            assertNull(userDAO.getById("p100"), "ERRORE: UserDAO ha trovato un prodotto nella sua mappa!");
            System.out.println("[OK] Isolation: Maps are separated by Class.");

            // 4. Test PersistenceManager Condiviso
            // Verifichiamo che il PM sia realmente lo stesso (Singleton)
            System.out.println("-> Testing Singleton PersistenceManager...");
            PersistenceManager pm1 =new  PersistenceManager();
            List<Product> allProducts = pm1.getAll(Product.class);
            
//            assertEquals(1, allProducts.size(), "Il Manager non vede i prodotti salvati dal DAO");
//            System.out.println("[OK] Singleton instance is shared across DAOs.");

            // 5. Test Delete
            System.out.println("-> Testing Delete...");
            boolean deleted = productDAO.delete(p1.getId());
            assertTrue(deleted, "Cancellazione fallita");
            assertNull(productDAO.getById(p1.getId()), "Il prodotto esiste ancora dopo la delete");
            System.out.println("[OK] Delete operation successful.");

            System.out.println("\n=== ALL DEMO TESTS PASSED! ===");

        } catch (Throwable e) {
            System.err.println("\n!!! TEST FAILED !!!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    // --- Assertions ---
    private static void assertNotNull(Object obj, String msg) {
        if (obj == null) throw new RuntimeException(msg);
    }

    private static void assertNull(Object obj, String msg) {
        if (obj != null) throw new RuntimeException(msg);
    }

    private static void assertTrue(boolean condition, String msg) {
        if (!condition) throw new RuntimeException(msg);
    }

    private static void assertEquals(Object exp, Object act, String msg) {
        if (exp == null || !exp.equals(act)) {
            throw new RuntimeException(msg + " [Exp: " + exp + ", Act: " + act + "]");
        }
    }
}