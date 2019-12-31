package com.rworld.core.graphics;

import javax.microedition.khronos.opengles.GL10;

import com.rworld.core.maths.Rectangle2D;
import com.rworld.core.utilities.Utility;

public class Surface extends Rectangle2D implements ITexturable, IDisposableGL {

	public Surface(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	public void dispose(GL10 gl) {
		if (_texture != null) {
			_texture = Utility.safeDispose(_texture.unref(), gl);
		}
	}
	
	public Texture getTexture() {
		return _texture;
	}

	public void setTexture(Texture value) {
		_texture = value.ref();
	}
	
	private Texture _texture;
}
