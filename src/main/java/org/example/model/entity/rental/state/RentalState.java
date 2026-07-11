package org.example.model.entity.rental.state;

import org.example.model.entity.rental.StatusEnum;

import java.io.Serializable;

public interface RentalState extends Serializable {
    void approve(RentalS rental);
    void reject(RentalS rental);
    void activate(RentalS rental);
    void complete(RentalS rental);
    void cancel(RentalS rental);

    boolean canBeCancelled();
    StatusEnum getStatusEnum();
}