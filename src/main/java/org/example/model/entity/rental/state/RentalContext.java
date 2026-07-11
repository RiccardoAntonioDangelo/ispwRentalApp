package org.example.model.entity.rental.state;

import java.io.Serializable;

public interface RentalContext extends Serializable {
    void setInternalState(RentalState state);
}