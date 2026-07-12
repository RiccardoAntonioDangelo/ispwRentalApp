package com.ispw.noleggio.controller.application;

import org.example.controller.bean.LoginBean;
import org.example.controller.bean.RegisterBean;
import org.example.controller.bean.SessionBean;
import org.example.exceptions.AuthenticationException;
import org.example.exceptions.RegistrationException;
import org.example.model.dao.DAOManager;
import org.example.model.dao.abstractfactory.EnumDaoType;
import org.example.model.entity.actors.factory.ActorEnum;
import org.example.view.GraphicAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationControllerTest {
    String selectedRole= ActorEnum.CLIENT.getValue() ;
    @BeforeEach
    void setUp() {
        if(!DAOManager.isInitialize())
            DAOManager.initializeSingleton(EnumDaoType.DEMO, true, true);

    }

    @Test
    void testRegisterAndLogin() throws AuthenticationException {
        String email = "newuser@test.com";
        String password = "securePass";

        // 1. Test Registrazione tramite Bean dedicato

        RegisterBean registerData = new RegisterBean(selectedRole,email, password,password);
        SessionBean sessionAfterRegister = GraphicAPI.registerApi(registerData);

        assertNotNull(sessionAfterRegister, "La registrazione deve restituire una sessione valida");
        assertEquals(email, sessionAfterRegister.getUser(), "L'utente in sessione deve corrispondere all'email registrata");
        GraphicAPI.logoutApi(sessionAfterRegister);

        // 2. Test Login immediato con le stesse credenziali
        LoginBean loginData = new LoginBean(email, password);
        SessionBean sessionAfterLogin = GraphicAPI.loginApi(loginData);

        assertNotNull(sessionAfterLogin, "Il login deve andare a buon fine");
        assertEquals(email, sessionAfterLogin.getUser());
    }

    @Test
    void testDuplicateRegistration() throws RegistrationException {
        String email = "duplicate@test.com";
        String password = "pass123";
        RegisterBean firstRegister = new RegisterBean(selectedRole,email, password,password);

        // La prima registrazione va a buon fine
        GraphicAPI.registerApi(firstRegister);

        // La seconda registrazione con la stessa email deve lanciare una RegistrationException
        RegisterBean duplicateRegister = new RegisterBean(selectedRole,email, "differentPass","differentPass");

        assertThrows(RegistrationException.class, () -> GraphicAPI.registerApi(duplicateRegister), "Il sistema non deve permettere la registrazione di email duplicate");
    }

    @Test
    void testInvalidLogin() throws RegistrationException {
        String email = "valid@test.com";
        String password = "correctPassword";

        // Registriamo prima un utente valido
        GraphicAPI.registerApi(new RegisterBean(selectedRole,email, password,password));

        // 1. Password errata: deve lanciare AuthenticationException
        LoginBean wrongPassData = new LoginBean(email, "wrong_password");
        assertThrows(AuthenticationException.class, () -> GraphicAPI.loginApi(wrongPassData), "Il login con password errata deve fallire lanciando un'eccezione");

        // 2. Email inesistente: deve lanciare AuthenticationException
        LoginBean wrongEmailData = new LoginBean(email + "notfound", password);
        assertThrows(AuthenticationException.class, () -> GraphicAPI.loginApi(wrongEmailData), "Il login con email non esistente deve fallire lanciando un'eccezione");
    }
}