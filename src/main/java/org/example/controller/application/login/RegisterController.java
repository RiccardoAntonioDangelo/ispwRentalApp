package org.example.controller.application.login;

import org.example.controller.bean.RegisterBean;
import org.example.controller.bean.SessionBean;
import org.example.exceptions.RegistrationException;
import org.example.model.entity.session.Session;

public interface RegisterController {

   /**
    * Gestisce la registrazione di un nuovo utente e la creazione della sua prima sessione.
    */
   static SessionBean register(RegisterBean bean) throws RegistrationException {
      if (!bean.isValid()) {throw new RegistrationException(bean.getErrorMessage());}

      if (AuthenticationController.getUser(bean.getEmail()) != null) {throw new RegistrationException("Email già registrata: " + bean.getEmail());}

      try {
         Session session = SessionManager.create(bean.getEmail(), bean.getPassword(), bean.getSelectedRole());
         if (AuthenticationController.saveSession(session) ) {
            return new SessionBean(session);
         }else{
            SessionManager.out(session);
            throw new RegistrationException("Errore critico durante il salvataggio nel database.");
         }
      } catch (IllegalStateException e) {
         throw new RegistrationException(e.getMessage());
      } catch (Exception e) {
         throw new RegistrationException("Errore imprevisto durante la registrazione: " + e.getMessage());
      }
   }
}