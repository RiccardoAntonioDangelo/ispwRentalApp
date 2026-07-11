package org.example.model.services.rent;

import org.example.model.entity.rental.Payment;
import org.example.model.services.EntityI;
import org.example.model.services.WorkI;
import org.example.model.services.session.SessionI;
import org.example.model.services.user.UserI;
import org.example.model.entity.rental.StatusEnum; // Importa l'enum se serve qui

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * interfaccia che definisce i dati di un contratto di Noleggio.
 * Estende RentalContext per integrarsi nativamente con lo State Pattern.
 */
public interface RentI extends EntityI<String>, WorkI {

    LocalDate getStartDate();
    LocalDate getEndDate();

    // =========================================================================
    // API DI BUSINESS DELEGATE ALLO STATO
    // =========================================================================
    void approve();
    void reject();
    void activate();
    void complete();
    void cancel();
    void setPayment(Payment payment);

    boolean canBeCancelled();
    StatusEnum getStatus();

    // =========================================================================
    // METODI DEFAULT DI LOGICA
    // =========================================================================
    default double getTotalPrice(double dailyCost) {
        long days = ChronoUnit.DAYS.between(getStartDate(), getEndDate());
        if (days <= 0) { days = 1; }
        return dailyCost * days;
    }

    default boolean canWork(SessionI sessionI, UserI userI) {
        if (userI instanceof ActionsRentIOld worker) {
            return worker.execute(sessionI, this);
        }
        return false;
    }

    boolean isOwner(ActionsOwnerRentI actionsOwnerRentI);

    String getOwnerEmail();
    String getClientEmail();

    String getProductId();

    void setEndDate(LocalDate localDate);
}