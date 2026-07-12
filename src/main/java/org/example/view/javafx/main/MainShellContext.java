package org.example.view.javafx.main;

import javafx.stage.Stage;
import org.example.controller.bean.SessionBean;
import org.example.util.str.StrApp;
import org.example.view.javafx.gc.LoginGC;
import org.example.view.javafx.gc.RegisterGC;
import org.example.view.javafx.gc.HomeGC;
import org.example.view.javafx.gc.UserRentalGC;
import org.example.view.javafx.componet.HeaderGC;
import org.example.view.GraphicAPI;
import org.example.view.javafx.util.GraphicController;
import org.example.view.javafx.util.ViewRoute;
import org.example.view.javafx.util.nav.NavigationController;
import org.example.view.javafx.util.state.Context;



public class MainShellContext extends Context {

    private final MainShellView view;
    private final NavigationController navController;
    private SessionBean memory;

    public MainShellContext(Stage stage) {
        super(new HomeGC().initViewLoader());
        this.view = new MainShellView(stage);
        this.navController = new NavigationController(this.view::setCenterView);
        initHeaderSetup();
        navigateTo(getCurrentState());
    }

    private void initHeaderSetup() {
        LoginGC loginController =new LoginGC().initViewLoader(this);
        RegisterGC registerController =new RegisterGC().initViewLoader(this);
        UserRentalGC userRentalsController = new UserRentalGC().initViewLoader(this);
        HeaderGC header = new HeaderGC().initViewLoader(this);
        // Passa il javafx dell'header al navigatore
        this.view.setTopView(header.getView());
        this.navController.addHistoryListener(header::updateBreadcrumbs);

        // Configurazione delle azioni dei pulsanti dell'header
        header.addActionButton(StrApp.NAV_USER_RENTAL, e -> navigateTo(userRentalsController.updateView()));
        header.addActionButton(StrApp.NAV_REGISTER, e -> navigateTo(registerController));
        //header.addActionButton(StrApp.NAV_LOGIN, e -> navigateTo(loginController));
        header.addActionButton(StrApp.LOGO_LOGOUT, e -> GraphicAPI.logoutApi(getMemory() ));
        header.addActionButtonToggle(
                () -> {
                    if ( getMemory() != null &&  getMemory().getUser() != null) {
                        return  getMemory().getUser(); // Ritorna l'email dell'utente loggato
                    }
                    return StrApp.get(StrApp.NAV_LOGIN); // Ritorna "Login" se anonimo
                },
                e -> navigateTo(loginController),    // Azione se cliccato in stato "Login"
                e -> navigateTo(loginController)
        );

    }

    @Override
    public GraphicController<?> getCurrentState() {
        if (super.getCurrentState() instanceof GraphicController<?> graphicController) {
            return graphicController;
        }//xtodo strapp
        throw new IllegalArgumentException(String.format(
                "Lo stato deve essere di tipo GraphicController, ricevuto: %s",
                super.getCurrentState() != null ? super.getCurrentState().getClass().getSimpleName() : "null"
        ));
    }
    public SessionBean getMemory(){return memory;}
    public void setMemory(SessionBean memory){this.memory=memory;}


    public void navigateTo(GraphicController<?> targetController) {
        if (targetController == null) return;
        targetController.setContext(this);
        if (navController != null) {
            navController.navigateTo(targetController);
        } else {
            throw new IllegalStateException("Impossibile navigare: NavigationController non inizializzato.");
        }
    }
    public void navigateTo(ViewRoute route) {
        navigateTo(route.getGraphicController().initViewLoader());
    }
    public void home() {
        navController.navigateToHome();
    }
    public void goBack() {
        navController.goBack();
    }

    public void show() {
        view.show();
    }
}