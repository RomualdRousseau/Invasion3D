package com.rworld.invasion;

import android.util.Log;

import com.rworld.core.GameState;
import com.rworld.core.graphics.GraphicEngine;

public class Benchmark {

    public void update(GameState gameState) {
        _frameCount++;
        _timerTime += gameState.activity.lastFrameDeltaTime;

        if (_timerTime > 5) {
            _lastFPS = (_frameCount / _timerTime);
            _frameCount = 0;
            _timerTime = 0.0f;
            _message.setLength(0);
            _message.append("FPS: ").append(_lastFPS);
        }
    }

    public void render(GraphicEngine ge) {
        if (_lastFPS > 0.0f) {
            Log.d("Invasion3D", _message.toString());
            _lastFPS = 0.0f;
        }
    }

    private int _frameCount = 0;
    private float _timerTime = 0.0f;
    private float _lastFPS = 0.0f;
    private StringBuilder _message = new StringBuilder();
}
