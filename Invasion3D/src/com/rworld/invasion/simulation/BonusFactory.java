package com.rworld.invasion.simulation;

import com.rworld.core.physics.IPhysicalEntityFactory;

public class BonusFactory implements IPhysicalEntityFactory<Bonus> {
	
	public Bonus getNewInstance() {
		return new Bonus();
	}
}
