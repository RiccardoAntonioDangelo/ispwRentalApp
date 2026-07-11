package org.example.view.javafx.util.nav;

import javafx.scene.Parent;
import org.example.view.javafx.util.GraphicController;

import java.util.List;
import java.util.function.Consumer;

public class NavigationController {
    private final NavigationHistory<GraphicController<?>> history;

    // Il Consumer viene registrato e gestito internamente
    private Consumer<Parent> onViewChanged;

    // Costruttore pulito senza parametri
    public NavigationController() {
        this.history = new NavigationHistory<>();
    }
    public NavigationController(Consumer<Parent> onViewChanged) {
        this.onViewChanged =onViewChanged;
        this.history = new NavigationHistory<>();

    }

    /**
     * 1. METODO PRINCIPALE: Coordina il flusso decidendo se ripristinare
     * una vecchia schermata o crearne una nuova.
     */
    public void navigateTo(GraphicController<?> targetController) {
        if (targetController == null) return;

        // Prova a cercare la schermata nella pila
        boolean foundAndRestored = navigateToExisting(targetController);

        // Se non era nella pila, gestisce la creazione della nuova schermata
        if (!foundAndRestored) {
            navigateToNew(targetController);
        }
    }
    public void navigateToHome() {
        navigateToExisting(history.getFirstInserted());
    }

    /**
     * 2. METODO PER SCHERMATA ESISTENTE: Cerca nella pila.
     * Se trova il javafx, sfoltisce la cronologia e aggiorna la grafica.
     * Ritorna true se l'operazione è andata a buon fine, altrimenti false.
     */
    private boolean navigateToExisting(GraphicController<?> targetController) {
        int existingIndex = history.find(targetController);

        if (existingIndex != -1) {
            // TROVATO: Sfoltisce la pila fino a quel punto (l'elemento diventa l'indice 0)
            history.removeUntilIndex(existingIndex);

            // Recupera il javafx rimasto/ripristinato in cima alla pila
            GraphicController<?> restoredController = history.peek();

            if (restoredController != null && onViewChanged != null) {
                onViewChanged.accept(restoredController.getView());
            }
            return true;
        }
        return false;
    }

    /**
     * 3. METODO PER NUOVA SCHERMATA: Verifica la validità della view,
     * fa il push sulla pila e genera la nuova grafica.
     */
    private void navigateToNew(GraphicController<?> targetController) {
        // Controllo di sicurezza: se la view è null e non era in cronologia, esplode
        if (targetController.getView() == null) {
            throw new IllegalStateException("Errore: Il javafx non ha una View valida e non è in cronologia.");
        }

        // Aggiunge la nuova schermata in cima alla pila
        history.push(targetController);

        // Notifica il cambio interfaccia
        if (onViewChanged != null) {
            onViewChanged.accept(targetController.getView());
        }
    }
    public void goBack() {
        if (history.size() > 1) {
            history.pop();
            GraphicController<?> previousController = history.peek();

            if (previousController != null && onViewChanged != null) {
                onViewChanged.accept(previousController.getView());
            }
        }
    }

    /**
     * 🟢 METODO RICHIESTO: Espone il Consumer per permettere a classi esterne
     * (come il MainShellContext) di agganciare la logica di aggiornamento grafica.
     */
    public Consumer<Parent> getOnViewChanged() {
        return this.onViewChanged;
    }

    /**
     * Permette di configurare la callback di cambio vista.
     */
    public NavigationController setOnViewChanged(Consumer<Parent> onViewChanged) {
        this.onViewChanged = onViewChanged;
        return this;
    }

    public void addHistoryListener(Consumer<List<GraphicController<?>>> listener) {
        history.addListener(c -> listener.accept(history.getAll()));
    }
}