package org.example.model.services.user;
import org.example.model.entity.actors.User;
import org.example.model.entity.actors.factory.ActorsManager;
import org.example.model.services.EntityI;
import org.example.model.services.WorkI;
import org.example.model.services.session.SessionI;

import java.util.function.Consumer;

public interface UserI extends EntityI<String> {
    static User createUser(String email, String password, String role) {return ActorsManager.create(email,password,role);}


    default boolean execute(SessionI session, WorkI work) {

        if(work.canWork(session,this))return true;
        throw new UnsupportedOperationException(
                String.format("L'operazione 'execute' in [%s] non supporta la combinazione di Sessione [%s] e Lavoro ",
                        this.getClass().getSimpleName(),
                        (session != null ? session.getClass().getSimpleName() : "null"))
        );
    }
    /**
     * Se l'utente possiede il ruolo specificato, esegue la funzione passata
     * fornendole l'istanza dell'utente già castata a quel ruolo.
     *
     * @param roleClass La classe/interfaccia del ruolo (es. ActionsRentI.class)
     * @param action    La funzione lambda da eseguire (es. role -> role.addRent(...))
     * @return true se l'azione è stata eseguita, false se l'utente non ha quel ruolo
     *
     *
     */
    default <R> boolean hasRole(Class<R> roleClass, Consumer<R> action) {
        if (roleClass != null && roleClass.isInstance(this)) {
            // Cast sicuro a runtime ed esecuzione della funzione
            action.accept(roleClass.cast(this));
            return true;
        }
        return false;
    }


    UserI getRole();
}