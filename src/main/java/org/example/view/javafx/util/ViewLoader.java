package org.example.view.javafx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.example.util.str.StrAppSystem;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class responsabile del caricamento del layout FXML e dell'inizializzazione
 * dei rispettivi GraphicController all'interno del ciclo di vita di JavaFX.
 */
public class ViewLoader {

    // Costruttore privato per prevenire l'istanza di una classe utility
    private ViewLoader() {}

    /**
     * Carica e inizializza un controller grafico a partire da una rotta predefinita.
     * * @param <C> Il tipo specifico del GraphicController.
     * @param route L'enum o l'oggetto di configurazione della rotta contenente il controller.
     * @return Il GraphicController inizializzato.
     */
    public static <C extends GraphicController<C>> GraphicController<C> load(ViewRoute route) {
        GraphicController<C> graphicController = route.getGraphicController();
        // Attiva la procedura interna al controller per agganciarsi al file FXML associato
        graphicController.initViewLoader();
        return graphicController;
    }

    /**
     * Esegue il parsing e il caricamento di un file FXML partendo dal suo path testuale,
     * estraendo automaticamente il controller associato tramite l'attributo 'fx:controller'.
     * * @param <C> Il tipo specifico del GraphicController atteso.
     * @param path Il percorso relativo del file FXML (es. "/view/MyView.fxml").
     * @return Il GraphicController configurato con la rispettiva View gerarchica iniettata.
     * @throws IllegalArgumentException Se il file FXML non viene trovato o è corrotto.
     */
    public static <C extends GraphicController<C>> GraphicController<C> loadFxml(String path) {
        try {
            // Risolve il path in una risorsa URL valida e istanzia il loader standard di JavaFX
            FXMLLoader loader = new FXMLLoader(stringToUrl(path));
            Parent view = loader.load(); // Esegue il parsing dell'albero XML dei nodi grafici

            // Recupera l'istanza del controller creata automaticamente da JavaFX durante il load
            GraphicController<C> controller = loader.getController();

            // Collega la gerarchia visiva appena creata all'istanza del controller e lo ritorna
            return controller.setView(view);
        } catch (IOException e) {
            throw new IllegalArgumentException(StrAppSystem.get(StrAppSystem.ERR_FXML_NOT_FOUND) + path, e);
        }
    }

    /**
     * Carica in modo esplicito la gerarchia visiva FXML iniettando un'istanza di controller
     * già esistente (Dependency Injection manuale tramite programmazione).
     * * @param graphicController L'istanza del controller da agganciare alla vista.
     * @return Il nodo radice (Parent) del layout grafico caricato.
     * @throws IllegalArgumentException Se il controller passato è nullo.
     * @throws IllegalStateException Se il controller possiede già una vista attiva o l'I/O fallisce.
     */
    public static Parent load(GraphicController<?> graphicController) {
        // Validazione dello stato iniziale del controller
        if (graphicController == null)
            throw new IllegalArgumentException(StrAppSystem.get(StrAppSystem.ERR_CONTROLLER_NULL));

        URL fxmlUrl = stringToUrl(graphicController.getStrUrl());
        if (graphicController.getView() != null)
            throw new IllegalStateException(StrAppSystem.get(StrAppSystem.ERR_VIEW_ALREADY_INIT));

        // Configurazione del loader impostando manualmente il controller
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setController(graphicController);

        try {
            return loader.load(); // Il file FXML non deve avere l'attributo fx:controller in questo scenario
        } catch (IOException e) {
            throw new IllegalStateException(StrAppSystem.get(StrAppSystem.ERR_FXML_LOAD_FAILED) + fxmlUrl, e);
        }
    }

    /**
     * Converte una stringa di percorso in un oggetto URL valido, scansionando in modo resiliente
     * il ClassLoader dell'applicazione per trovare la risorsa FXML nel classpath.
     * * @param path Il percorso testuale del file risorsa.
     * @return L'URL effettivo della risorsa nel sistema.
     * @throws IllegalArgumentException Se il percorso è vuoto, nullo o non punta a nessuna risorsa reale.
     */
    public static URL stringToUrl(String path) {
        if (path == null || path.isBlank())
            throw new IllegalArgumentException(StrAppSystem.get(StrAppSystem.ERR_PATH_EMPTY));

        // 1. Tentativo standard tramite il ClassLoader della classe di routing delle viste
        URL url = ViewRoute.class.getResource(path);

        // 2. Tentativo di riserva tramite il ClassLoader del Thread corrente (utile in ambienti modulari o multi-thread)
        if (url == null)
            url = Thread.currentThread().getContextClassLoader().getResource(path);

        // 3. Controllo finale di sicurezza per intercettare l'errore prima del crash interno di JavaFX
        if (url == null)
            throw new IllegalArgumentException(StrAppSystem.get(StrAppSystem.ERR_FXML_NOT_FOUND) + path);

        return url;
    }
}