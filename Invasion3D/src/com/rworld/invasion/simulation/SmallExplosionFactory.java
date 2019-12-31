package com.rworld.invasion.simulation;

import com.rworld.core.physics.IPhysicalEntityFactory;

public class SmallExplosionFactory implements IPhysicalEntityFactory<SmallExplosion> {

	public SmallExplosion getNewInstance() {
		return new SmallExplosion();
	}
}
