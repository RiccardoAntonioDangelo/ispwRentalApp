package org.example.controller.bean;

import org.example.controller.bean.util.AbstractBean;
import org.example.util.str.StrApp; // Import delle tue costanti stringa

public class LoginBean extends AbstractBean {
    private String email = "";
    private String password = "";

    public LoginBean() {}
    public LoginBean(String email,String password) {
        this.setEmail(email).setPassword(password);
    }
    public String getEmail() {return email;}
    protected LoginBean setEmail(String email) {this.email = email;return this;}

    public String getPassword() {return password;}
    protected LoginBean setPassword(String password) {this.password = password;return this;}

    @Override
    public boolean isValid() {
        if (demoValid())return demoValid();
        this.setErrorMessage("");
        return isEmailValid() && isPasswordValid();
    }
    private boolean isEmailValid() {
        if (email == null || email.trim().isEmpty()) {
            this.setErrorMessage(StrApp.ERR_EMAIL_REQUIRED);
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            this.setErrorMessage(StrApp.ERR_EMAIL_INVALID);
            return false;
        }
        return true;
    }
    private boolean isPasswordValid() {
        if (password == null || password.trim().isEmpty()) {
            this.setErrorMessage(StrApp.ERR_PASSWORD_REQUIRED);
            return false;
        }
        return isPasswordSecure();
    }
    private boolean isPasswordSecure() {
        if (password == null || password.length() < 5) {
            this.setErrorMessage(StrApp.ERR_PASSWORD_SHORT);
            return false;
        }
        return true;
    }


}