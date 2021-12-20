package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;

public class EnemyBullet extends PhysicalEntity {

    public void init(EnemyShip enemy, float life, PhysicalEntity target) {
        this.life = life;
        this.size = 0.2f;

        float x = target.positionX - enemy.positionX;
        float y = target.positionY - enemy.positionY;
        float z = target.positionZ - enemy.positionZ;
        float l = 1.0f / (float) Math.sqrt(x * x + y * y + z * z);

        setPosition(enemy.positionX, enemy.positionY, enemy.positionZ);
        setSpeed(x * l * 7.0f, y * l * 7.0f, z * l * 7.0f);
    }

    @Override
    public void update(GameState gameState) {
        float t = gameState.activity.lastFrameDeltaTime;
        life = Math.max(life - t, 0.0f);
        setPosition(positionX + speedX * t, positionY + speedY * t, positionZ + speedZ * t);
    }
}
