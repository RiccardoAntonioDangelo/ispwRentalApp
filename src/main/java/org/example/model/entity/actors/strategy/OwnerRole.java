package org.example.model.entity.actors.strategy;

import org.example.model.services.product.ActionsProductI;
import org.example.model.services.rent.ActionsOwnerRentI;
import org.example.model.services.rent.ActionsRentIOld;

public class OwnerRole extends ClientRole implements ActionsProductI, ActionsRentIOld, RoleStrategyI, ActionsOwnerRentI {

}
