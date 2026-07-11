package org.example.controller.bean;

import org.example.model.entity.actors.factory.ActorEnum;
import org.example.util.str.StrApp;

public class RegisterBean extends LoginBean {
    private String confirmPassword = "";
    private String selectedRole = "";

    public RegisterBean() {}
    public RegisterBean(String selectedRole,String email,String password,String confirmPassword) {
        super(email,password);
        this.setSelectedRole(selectedRole).setConfirmPassword(confirmPassword);
    }

    public String getConfirmPassword() {return confirmPassword;}
    protected RegisterBean setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }
    public String getSelectedRole() {return selectedRole;}
    protected RegisterBean setSelectedRole(String selectedRole) {
        this.selectedRole = selectedRole;
        return this;
    }

    // Override dei setter del padre per mantenere la Fluent API (ritornano RegisterBean)
    @Override
    public RegisterBean setEmail(String email) {
        super.setEmail(email);
        return this;
    }
    @Override
    public RegisterBean setPassword(String password) {
        super.setPassword(password);
        return this;
    }


    @Override
    public boolean isValid() {
        if (demoValid())return demoValid();
        // Reset dell'errore ereditato tramite il metodo di AbstractBean
        this.setErrorMessage("");

        // 1. Validazione del Ruolo
        try {
            ActorEnum.fromValue(this.getSelectedRole());
        } catch (IllegalArgumentException e) {
            this.setErrorMessage(StrApp.ERR_ROLE_INVALID);
            return false;
        }

        // 2. Controllo coincidenza password
        if (!confirmPassword.equals(getPassword())) {
            this.setErrorMessage(StrApp.ERR_PASSWORD_MISMATCH);
            return false;
        }

        // 3. Esegue a cascata i controlli di LoginBean (formato email e robustezza password)
        return super.isValid();
    }
}