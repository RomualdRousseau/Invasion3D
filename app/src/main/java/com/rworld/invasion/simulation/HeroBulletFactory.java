package com.rworld.invasion.simulation;

import com.rworld.core.physics.IPhysicalEntityFactory;

public class HeroBulletFactory implements IPhysicalEntityFactory<HeroBullet> {

    public HeroBullet getNewInstance() {
        return new HeroBullet();
    }
}
