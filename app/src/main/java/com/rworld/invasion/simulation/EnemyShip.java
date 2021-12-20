package com.rworld.invasion.simulation;

import com.rworld.core.GameState;
import com.rworld.core.physics.PhysicalEntity;
import com.rworld.invasion.database.EntityRow;
import com.rworld.invasion.database.WaveKey;

public class EnemyShip extends PhysicalEntity {

    private WaveKey[] _keys;
    private int _currentKey;
    private float _time;
    private boolean _hasFired;

    public void init(EntityRow entityRow, WaveKey[] keys) {
        life = entityRow.life;
        size = entityRow.size;

        setPosition(keys[0].positionX, keys[0].positionY, keys[0].positionZ);
        setRotation(keys[0].rotationX, keys[0].rotationY, keys[0].rotationZ);

        _keys = keys;
        _time = 0;
        _currentKey = 0;
        _hasFired = false;
    }

    public boolean isCanonFired(int level) {
        if ((-7.0f <= positionX) && (positionX <= 7.0f)
                && (-14.0f <= positionZ) && (positionZ <= 0.0f)
                && !_hasFired && (Math.random() < (0.02f * level))) {
            _hasFired = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void update(GameState gameState) {
        float t = gameState.activity.lastFrameDeltaTime;
        WaveKey key1;
        WaveKey key2;

        if (_currentKey >= (_keys.length - 1)) {
            life = 0.0f;
            return;
        }

        if (_time >= _keys[_currentKey + 1].time) {
            _currentKey++;
            if (_currentKey >= (_keys.length - 1)) {
                life = 0.0f;
                return;
            }
        }

        key1 = _keys[_currentKey];
        key2 = _keys[_currentKey + 1];
        linearInterpolation(key1, key2, (_time + t - key1.time) / (key2.time - key1.time));

        _time += t;
    }
}
