package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;

public class HeroBullet extends PhysicalEntity {

    public void init(HeroShip heroShip, float life) {
        this.life = life;
        this.size = 0.2f;

        setPosition(heroShip.positionX, 0.0f, 0.0f);
        setRotation(0.0f, 0.0f, heroShip.rotationZ);
        setSpeed(0.0f, 0.0f, -7.0f);
    }

    @Override
    public void update(GameState gameState) {
        float t = gameState.activity.lastFrameDeltaTime;
        life = Math.max(life - t, 0.0f);
        setPosition(positionX + speedX * t, positionY + speedY * t, positionZ + speedZ * t);
    }
}
