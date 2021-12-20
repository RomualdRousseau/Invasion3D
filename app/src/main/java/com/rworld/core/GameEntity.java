package com.rworld.core;

public class GameEntity {

    public float positionX = 0.0f;
    public float positionY = 0.0f;
    public float positionZ = 0.0f;
    public float rotationX = 0.0f;
    public float rotationY = 0.0f;
    public float rotationZ = 0.0f;

    public GameEntity() {
    }

    public GameEntity(float px, float py, float pz) {
        this.positionX = px;
        this.positionY = py;
        this.positionZ = pz;
    }

    public GameEntity(float px, float py, float pz, float rx, float ry, float rz) {
        this.positionX = px;
        this.positionY = py;
        this.positionZ = pz;
        this.rotationX = rx;
        this.rotationY = ry;
        this.rotationZ = rz;
    }

    public void setPosition(float px, float py, float pz) {
        this.positionX = px;
        this.positionY = py;
        this.positionZ = pz;
    }

    public void setRotation(float rx, float ry, float rz) {
        this.rotationX = rx;
        this.rotationY = ry;
        this.rotationZ = rz;
    }

    public void linearInterpolation(GameEntity e1, GameEntity e2, float t) {
        this.positionX = e1.positionX + (e2.positionX - e1.positionX) * t;
        this.positionY = e1.positionY + (e2.positionY - e1.positionY) * t;
        this.positionZ = e1.positionZ + (e2.positionZ - e1.positionZ) * t;
        this.rotationX = e1.rotationX + (e2.rotationX - e1.rotationX) * t;
        this.rotationY = e1.rotationY + (e2.rotationY - e1.rotationY) * t;
        this.rotationZ = e1.rotationZ + (e2.rotationZ - e1.rotationZ) * t;
    }
}
