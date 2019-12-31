package com.rworld.invasion.simulation;

import com.rworld.core.physics.IPhysicalEntityFactory;

public class EnemyBulletFactory implements IPhysicalEntityFactory<EnemyBullet> {

	public EnemyBullet getNewInstance() {
		return new EnemyBullet();
	}
}
