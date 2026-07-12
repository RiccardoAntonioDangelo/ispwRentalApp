package org.example.model.entity.actors.strategy;

import org.example.model.services.product.ActionsProductI;
import org.example.model.services.rent.ActionsOwnerRentI;

public class OwnerRole extends ClientRole implements ActionsProductI, RoleStrategyI, ActionsOwnerRentI {

}
