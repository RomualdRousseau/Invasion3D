package com.rworld.core.graphics.low;

import com.rworld.core.GameEntity;
import com.rworld.core.graphics.IParticleGenerator;
import com.rworld.core.graphics.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class ParticleGeneratorImpl implements IParticleGenerator {

    private FloatBuffer _vertexBuffer;
    private Texture _texture;
    private int _maxParticules;
    private float _pointSize;

    public void init(GL10 gl, int maxParticules, float pointSize) {
        _maxParticules = maxParticules;
        _pointSize = pointSize;

        ByteBuffer vbb = ByteBuffer.allocateDirect(_maxParticules * 3 * Float.SIZE);
        vbb.order(ByteOrder.nativeOrder());
        _vertexBuffer = vbb.asFloatBuffer();
    }

    public void dispose(GL10 gl) {
        if (_texture != null) {
            _texture.dispose(gl);
            _texture = null;
        }
        _vertexBuffer = null;
    }

    public Texture getTexture() {
        return _texture;
    }

    public void setTexture(Texture value) {
        _texture = value;
    }

    public void draw(GL10 gl, ArrayList<? extends GameEntity> particules) {
        int nbParticules = particules.size();

        gl.glPointSize(_pointSize);

        _vertexBuffer.clear();

        for (int i = 0; i < nbParticules; i++) {
            if (_vertexBuffer.hasRemaining()) {
                GameEntity entity = particules.get(i);
                _vertexBuffer.put(entity.positionX);
                _vertexBuffer.put(entity.positionY);
                _vertexBuffer.put(entity.positionZ);
            }
        }
        _vertexBuffer.rewind();
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);

        if (_texture != null) {
            _texture.bind(gl);
            gl.glEnable(GL11.GL_POINT_SPRITE_OES);
            ((GL11) gl).glTexEnvi(GL11.GL_POINT_SPRITE_OES, GL11.GL_COORD_REPLACE_OES, GL10.GL_TRUE);
        }

        gl.glDrawArrays(GL10.GL_POINTS, 0, Math.min(nbParticules, _maxParticules));

        if (_texture != null) {
            gl.glDisable(GL11.GL_POINT_SPRITE_OES);
        }
    }

}
