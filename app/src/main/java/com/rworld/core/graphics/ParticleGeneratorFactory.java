package com.rworld.core.graphics;

import com.rworld.core.graphics.high.ParticleGeneratorImpl;

import javax.microedition.khronos.opengles.GL10;

public class ParticleGeneratorFactory {

    public static IParticleGenerator createInstance(GL10 gl, int maxParticules, float pointSize) {
        IParticleGenerator particle = new ParticleGeneratorImpl();
        particle.init(gl, maxParticules, pointSize);
        return particle;
    }
}
