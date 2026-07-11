package org.example.view.javafx.componet;

import org.example.view.javafx.main.MainShellContext;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;

/**
 * Classe astratta di base per tutti i sottocomponenti grafici (es. Card).
 * Centralizza la logica di caricamento dei file FXML e supporta la propagazione del contesto di esecuzione.
 *
 * @param <C> Il tipo specifico della sottoclasse controller che eredita da questa classe.
 */
public abstract class AbstractComponentGC<C extends AbstractComponentGC<C>> extends GraphicController<C> {

    /**
     * Carica il componente grafico sfruttando la rotta predefinita della sottoclasse.
     * * @return L'istanza del controller fortemente tipizzata e inizializzata da JavaFX.
     */
    @SuppressWarnings("unchecked")
    public C getComponentGraphicController() {
        return AbstractComponentGC.create(getRoute());
    }

    /**
     * Carica il componente grafico e vi inietta il contesto globale dell'applicazione (MainShellContext).
     * * @param context Il contesto della shell principale contenente i dati di sessione.
     * @return L'istanza del controller configurata con il relativo contesto.
     */
    @SuppressWarnings("unchecked")
    public C getComponentGraphicController(MainShellContext context) {
        C controller =  AbstractComponentGC.create(getRoute());
        controller.setContext(context);
        return controller;
    }

    /**
     * Metodo di supporto statico che interroga l'inizializzatore nativo per caricare l'FXML dal classpath.
     *
     * @param <A> Tipo generico di ritorno vincolato a un GraphicController.
     * @param route La configurazione di rotta FXML da caricare.
     * @return Il controller associato estratto dal file FXML.
     */
    public static <A extends GraphicController<A>> A create(ViewRoute route) {
        return GraphicController.fxmlLoader(route.getFxmlPath());
    }
}