package com.rworld.invasion.simulation;

import com.rworld.core.physics.IPhysicalEntityFactory;

public class EnemyFactory implements IPhysicalEntityFactory<EnemyShip> {

    public EnemyShip getNewInstance() {
        return new EnemyShip();
    }
}
