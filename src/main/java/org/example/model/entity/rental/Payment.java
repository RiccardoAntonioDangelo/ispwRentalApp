package org.example.model.entity.rental;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Payment implements Serializable {
    private double amount;
    private LocalDateTime timestamp;
    private PaymentMethod method;
    private PaymentStatus status;

    public enum PaymentMethod { CREDIT_CARD, PAYPAL, CASH, TRANSFER }
    public enum PaymentStatus { PENDING, PAID, FAILED, REFUNDED }

    public Payment() {
        // Specificato ZoneId per evitare dipendenze implicite dal fuso orario del sistema operativo
        this.timestamp = LocalDateTime.now(ZoneId.systemDefault());
    }

    public Payment(double amount) {
        this.amount = amount;
        // Specificato ZoneId per evitare dipendenze implicite dal fuso orario del sistema operativo
        this.timestamp = LocalDateTime.now(ZoneId.systemDefault());
        this.status = PaymentStatus.PENDING;
        // Impostiamo il metodo di default correttamente tramite il setter
        this.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    }

    public double getVatAmount() {
        return amount * 0.22;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    // Corretti i nomi dei metodi seguendo le convenzioni JavaBean (set/get)
    public void setPaymentMethod(PaymentMethod type) {
        this.method = type;
    }

    public void setPaymentStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }
}