package com.rworld.invasion.simulation;

import com.rworld.core.physics.IPhysicalEntityFactory;

public class BigExplosionFactory implements IPhysicalEntityFactory<BigExplosion> {

	public BigExplosion getNewInstance() {
		return new BigExplosion();
	}
}
