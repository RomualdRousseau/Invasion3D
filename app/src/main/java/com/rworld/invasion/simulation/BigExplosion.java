package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;

public class BigExplosion extends PhysicalEntity {

    public void init(PhysicalEntity entity, float life) {
        this.life = life;

        setPosition(entity.positionX, entity.positionY, entity.positionZ);
        setSpeed(8.0f * (float) Math.cos(Math.random() * 2 * Math.PI), 0.0f, 8.0f * (float) Math.sin(Math.random() * 2 * Math.PI));
    }

    @Override
    public void update(GameState gameState) {
        float t = gameState.activity.lastFrameDeltaTime;
        life = Math.max(life - t, 0.0f);
        setPosition(positionX + speedX * t, positionY + speedY * t, positionZ + speedZ * t);
    }
}
