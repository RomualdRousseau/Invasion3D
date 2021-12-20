package com.rworld.core.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

public class Texture implements IDisposableGL {

    public Texture(GL10 gl, InputStream stream) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        _textureID = textures[0];

        gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureID);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        Bitmap image = BitmapFactory.decodeStream(stream);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);
        image.recycle();
    }

    public void dispose(GL10 gl) {
        if (_ref == 0) {
            int[] textures = {_textureID};
            gl.glDeleteTextures(1, textures, 0);
            _textureID = 0;
        }
    }

    public Texture ref() {
        _ref++;
        return this;
    }

    public Texture unref() {
        _ref--;
        return this;
    }

    public void bind(GL10 gl) {
        gl.glBindTexture(GL10.GL_TEXTURE_2D, _textureID);
    }

    private int _textureID = 0;
    private int _ref = 0;
}
