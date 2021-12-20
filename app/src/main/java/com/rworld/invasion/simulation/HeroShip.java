package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;
import com.rworld.invasion.database.EntityRow;

public class HeroShip extends PhysicalEntity {

    public boolean canCollide;
    public float roll;
    public float throttle;
    public boolean canonFired;

    public HeroShip(EntityRow entityRow, float x, float y, float z) {
        life = entityRow.life;
        size = entityRow.size;

        setPosition(x, y, z);

        canCollide = true;
        roll = 8.0f;
        throttle = 0.0f;
        canonFired = false;
        _canonPause = 0.0f;
    }

    public void fire(boolean touched) {
        if (touched) {
            if (_canonPause == 0.0f) {
                canonFired = true;
                _canonPause = 0.5f;
            }
        }
    }

    @Override
    public void update(GameState gameState) {
        float t = gameState.activity.lastFrameDeltaTime;
        float force_x = roll * t;
        float force_z = throttle * t;

        positionX = positionX + force_x - Math.signum(roll) * Math.max(Math.abs(positionX + force_x) - 8.5f, 0.0f);
        positionZ = positionZ - force_z;
        rotationZ = rotationZ - 8.0f * force_x - 6.0f * rotationZ * t;

        if (canonFired) {
            canonFired = false;
        }
        _canonPause = Math.max(_canonPause - t, 0.0f);
    }

    private float _canonPause;
}
