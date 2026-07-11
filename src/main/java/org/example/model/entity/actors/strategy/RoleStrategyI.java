package org.example.model.entity.actors.strategy;

import org.example.exceptions.dao.file.EntityNotFoundException;
import org.example.model.services.user.UserI;

import java.io.Serializable;


public interface RoleStrategyI extends Serializable, UserI  {
    default String getId() {throw new IllegalArgumentException("getId non e' valido RoleStrategyI");}
    default UserI getRole(){throw new IllegalArgumentException("getRole non e' valido RoleStrategyI");}
}