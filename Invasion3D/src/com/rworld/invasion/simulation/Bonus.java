package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;

public class Bonus extends PhysicalEntity {
	
	public static final int NONE		= 0;
	public static final int SHIELD		= 1;
	public static final int ENERGY		= 2;
	public static final int DOUBLE_FIRE = 3;
	public static final int TRIPLE_FIRE = 4;
	public static final int CROSS_FIRE	= 5;
	
	public int type = 0;
	
	public void init(float life) {
		this.type = (int) ((float) Math.random() * 5.0f) + 1;
		this.life = life;
		this.size = 0.2f;
		
		float x = 5.0f * (float) Math.random();
		float y = 0.0f;
		float z = -5.0f;

		setPosition(x, y, z);
		setSpeed(0.0f, 0.0f, 1.0f);
	}
	
	@Override
	public void update(GameState gameState) {
		float t = gameState.activity.lastFrameDeltaTime;
		life = Math.max(life - t, 0.0f);
		setPosition(positionX + speedX * t, positionY + speedY * t, positionZ + speedZ * t);
	}
}
