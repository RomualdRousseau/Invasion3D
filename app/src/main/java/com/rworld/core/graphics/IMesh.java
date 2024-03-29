package com.rworld.core.graphics;

import com.rworld.core.GameEntity;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public interface IMesh extends ITexturable, IDisposableGL {

    void init(GL10 gl, MeshLoader loader, float scaleX, float scaleY, float scaleZ);

    void draw(GL10 gl, GameEntity entity);

    void draw(GL10 gl, ArrayList<? extends GameEntity> entities);
}
