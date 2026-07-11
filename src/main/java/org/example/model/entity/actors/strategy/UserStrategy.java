package org.example.model.entity.actors.strategy;

import org.example.model.entity.actors.User;
import org.example.model.services.WorkI;
import org.example.model.services.session.SessionI;
import org.example.model.services.user.UserI;

import java.util.function.Consumer;

public class UserStrategy extends User {
    private RoleStrategyI role;
    public UserStrategy(String email, String password, RoleStrategyI role) {super(email,password);this.role=role;}

    //public RoleStrategyI getRole() {return role;}

    public void setRole(RoleStrategyI role) {
        this.role = role;
        this.commitChange();
    }

    public boolean execute(SessionI session, WorkI work) {
       return role.execute(session,work);
    }
    @Override
     public <R> boolean hasRole(Class<R> roleClass, Consumer<R> action) {
        if(roleClass != null) {
            if (roleClass.isInstance(this)) {
                // Cast sicuro a runtime ed esecuzione della funzione
                action.accept(roleClass.cast(this));
                return true;
            }else if (roleClass.isInstance(this.getRole())) {
                // Cast sicuro a runtime ed esecuzione della funzione
                action.accept(roleClass.cast(this.getRole()));
                return true;
            }
        }
        return false;
    }
    @Override
    public UserI getRole(){
        return this.role;
    }
}