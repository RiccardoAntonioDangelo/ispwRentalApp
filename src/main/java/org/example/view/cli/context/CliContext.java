package org.example.view.cli.context;

import org.example.controller.bean.ProductBean;
import org.example.controller.bean.SessionBean;
import java.io.PrintStream;
import java.util.Scanner;

public class CliContext {
    private final Scanner scanner;
    private final PrintStream out;
    
    private SessionBean currentSession;
    private ProductBean selectedProduct; // Memorizza la selezione tra una pagina e l'altra

    public CliContext(PrintStream outStream) {
        this.scanner = new Scanner(System.in);
        this.out = outStream;
    }

    // Getter e Setter
    public Scanner getScanner() { return scanner; }
    public PrintStream getOut() { return out; }
    public SessionBean getCurrentSession() { return currentSession; }
    public void setCurrentSession(SessionBean session) { this.currentSession = session; }
    public ProductBean getSelectedProduct() { return selectedProduct; }
    public void setSelectedProduct(ProductBean product) { this.selectedProduct = product; }

    public void handleGlobalError(Exception e) {
        /**/
    }
}