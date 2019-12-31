package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;

public class SmallExplosion extends PhysicalEntity {
	
	public void init(PhysicalEntity entity, float life) {
this.life = life;
		
		setPosition(entity.positionX, entity.positionY, entity.positionZ);
	}
	
	@Override
	public void update(GameState gameState) {
		life = Math.max(life - gameState.activity.lastFrameDeltaTime, 0.0f);
	}	
}
