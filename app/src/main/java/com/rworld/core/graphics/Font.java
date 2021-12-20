package com.rworld.core.graphics;

import javax.microedition.khronos.opengles.GL10;

public class Font implements ITexturable, IDisposableGL {

    public Font(int width, int height) {
        _width = width;
        _height = height;
    }

    public void dispose(GL10 gl) {
        if (_texture != null) {
            _texture.unref();
            _texture.dispose(gl);
            _texture = null;
        }
    }

    public Texture getTexture() {
        return _texture;
    }

    public void setTexture(Texture value) {
        _texture = value.ref();
    }

    public int getX(int c) {
        return (c % _height) * _width;
    }

    public int getY(int c) {
        return (c / _height) * _height;
    }

    public int getWidth() {
        return _width;
    }

    public int getHeight() {
        return _height;
    }

    public int getBaseLine() {
        return 2;
    }

    private Texture _texture;
    private int _width;
    private int _height;
}
