package org.example.model.services;

import org.example.util.observer.SubjectI;

import java.io.Serializable;

public interface EntityI<I>  extends Serializable, SubjectI<EntityI<I>> {
    I getId();
}