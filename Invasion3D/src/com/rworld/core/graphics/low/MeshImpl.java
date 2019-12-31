package com.rworld.core.graphics.low;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import com.rworld.core.GameEntity;
import com.rworld.core.graphics.IMesh;
import com.rworld.core.graphics.MeshLoader;
import com.rworld.core.graphics.Texture;
import com.rworld.core.utilities.Utility;

public class MeshImpl implements IMesh {
	
	public void init(GL10 gl, MeshLoader loader, float scaleX, float scaleY, float scaleZ) {
		ByteBuffer vbb = ByteBuffer.allocateDirect(loader.GetElementCount() * 3 * Float.SIZE);
		vbb.order(ByteOrder.nativeOrder());
		_vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(loader.GetElementCount() * 2 * Float.SIZE);
		tbb.order(ByteOrder.nativeOrder());
		_texCoordBuffer = tbb.asFloatBuffer();

		ByteBuffer ibb = ByteBuffer.allocateDirect(loader.GetIndexCount() * Short.SIZE);
		ibb.order(ByteOrder.nativeOrder());
		_indexBuffer = ibb.asShortBuffer();

		_indexCount = loader.GetIndexCount();
		loader.loadBuffers(_vertexBuffer, _texCoordBuffer, _indexBuffer, scaleX, scaleY, scaleZ);
		loader.dispose();

		_vertexBuffer.rewind();
		_texCoordBuffer.rewind();
		_indexBuffer.rewind();
	}

	public void dispose(GL10 gl) {
		if (_texture != null) {
			_texture = Utility.safeDispose(_texture.unref(), gl);
		}
		_indexBuffer = null;
		_vertexBuffer = null;
		_texCoordBuffer = null;
		_indexCount = 0;
	}
	
	public Texture getTexture() {
		return _texture;
	}

	public void setTexture(Texture value) {
		_texture = value.ref();
	}

	public void draw(GL10 gl, GameEntity entity) {
		gl.glPushMatrix();
		gl.glTranslatef(entity.positionX, entity.positionY, entity.positionZ);
		if(entity.rotationX != 0.0f) {
			gl.glRotatef(entity.rotationX, 1.0f, 0.0f, 0.0f);
		}
		if(entity.rotationY != 0.0f) {
			gl.glRotatef(entity.rotationY, 0.0f, 1.0f, 0.0f);
		}
		if(entity.rotationZ != 0.0f) {
			gl.glRotatef(entity.rotationZ, 0.0f, 0.0f, 1.0f);
		}
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _vertexBuffer);

		if (_texture != null) {
			_texture.bind(gl);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _texCoordBuffer);
		}

		gl.glDrawElements(GL10.GL_TRIANGLES, _indexCount, GL10.GL_UNSIGNED_SHORT, _indexBuffer);
		
		if (_texture != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glPopMatrix();
	}
	
	public void draw(GL10 gl, ArrayList<? extends GameEntity> entities) {
		int s = entities.size();
		for(int i = 0; i < s; i++) {
			draw(gl, entities.get(i));
		}
	}
	
	private int _indexCount;
	private FloatBuffer _vertexBuffer;
	private FloatBuffer _texCoordBuffer;
	private ShortBuffer _indexBuffer;
	private Texture _texture;
}
