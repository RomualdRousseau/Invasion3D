package com.rworld.core.graphics;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.rworld.core.GameEntity;

public interface IParticleGenerator extends ITexturable, IDisposableGL {
	
	void init(GL10 gl, int maxParticules, float pointSize);
	
	void draw(GL10 gl, ArrayList<? extends GameEntity> entities);
}
