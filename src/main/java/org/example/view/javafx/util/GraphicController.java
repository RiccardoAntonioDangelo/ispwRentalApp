package org.example.view.javafx.util;

import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import org.example.view.javafx.main.MainShellContext;
import org.example.view.javafx.util.state.ConcreteState;
import org.example.view.javafx.util.state.Context;

/**
 * Controller Grafico Astratto con supporto nativo al Method Chaining.
 * * @param <Chain> Rappresenta l'esatto tipo della classe figlia che estende questo controller.
 * * si deve sempre fare .initViewLoader().setContext() or .initViewLoader(getcontext())
 */
@SuppressWarnings("unchecked")
public abstract class GraphicController<C extends GraphicController<C>> extends ConcreteState {//GraphicController<C>
    private Parent view = null;

    /**
     * Helper interno per effettuare il cast sicuro all'istanza figlia corrente.
     */
    protected final C self() {
        return (C) this;
    }

    public MainShellContext getMainShellContext() {return (MainShellContext) getContext();}

    public <T> T memory() {
        MainShellContext context = getMainShellContext();
        if (context == null) {
            throw new IllegalStateException("Impossibile accedere alla memoria: il MainShellContext non è impostato.");
        }
        return (T) context.getMemory();
    }

    /**
     * Override per prevenire l'impostazione di contesti non compatibili.
     */
    @Override
    public final void setContext(Context ctx) {
        if (ctx instanceof MainShellContext) {
            super.setContext(ctx);
            return;
        }
        throw new IllegalArgumentException(String.format("Il contesto deve essere di tipo MainShellContext, ricevuto: %s", ctx != null ? ctx.getClass().getSimpleName() : "null"));
    }

    public void nextPage(GraphicController<?> graphicController) { getMainShellContext().navigateTo(graphicController); }
    public void nextPage(ViewRoute route) { getMainShellContext().navigateTo(route); }
    public void backHome() { getMainShellContext().home(); }
    public void goBack() { getMainShellContext().goBack(); }

    public void alert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        // Evitiamo la dimensione fissa predefinita
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        // Inizializzazione standard senza classi anonime nascoste
        Label contentLabel = new Label(contentText);
        contentLabel.setWrapText(true);

        alert.getDialogPane().setContent(contentLabel);

        alert.showAndWait();
    }
    /**
     * METODO ASTRATTO: Ogni schermata deve implementare la propria logica di aggiornamento.
     * Sfrutta il generico 'Chain' ereditato dalla classe per non interrompere il chaining.
     */
    public abstract C updateView();

    /**
     * METODO ASTRATTO obbligatorio: Ogni vista deve dichiarare la propria rotta.
     */
    public abstract ViewRoute getRoute();

    public String getStrUrl() {
        return getRoute().getFxmlPath();
    }

    // --- GESTIONE DELLA VISTA (TUTTI AGGIORNATI A RETURNARE 'Chain') ---

    public Parent getView() {
        return this.view;
    }

    public C initViewLoader() {
        this.view = ViewLoader.load(this);
        return self();
    }

    public C initViewLoader(MainShellContext context) {
        this.setContext(context);
        return this.initViewLoader();
    }

    public C setView(Parent view) {
        this.view = view;
        return self();
    }

    public static <T extends  GraphicController<T>> T  fxmlLoader(String fxmlPath) {
        return (T) ViewLoader.loadFxml(fxmlPath);
    }
}