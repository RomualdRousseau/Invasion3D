package com.rworld.core.physics;

import com.rworld.core.GameEntity;
import com.rworld.core.GameState;

public class PhysicalEntity extends GameEntity {
	
	public float speedX = 0.0f;
	public float speedY = 0.0f;
	public float speedZ = 0.0f;
	
	public float life = 0.0f;
	public float size = 1.0f;
	
	public void setSpeed(float sx, float sy, float sz) {
		this.speedX = sx;
		this.speedY = sy;
		this.speedZ = sz;
	}
	
	public void update(GameState gameState) {
	}
}
