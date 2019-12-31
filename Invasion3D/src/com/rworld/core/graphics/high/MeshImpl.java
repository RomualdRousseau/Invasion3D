package com.rworld.core.graphics.high;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import com.rworld.core.GameEntity;
import com.rworld.core.graphics.IMesh;
import com.rworld.core.graphics.MeshLoader;
import com.rworld.core.graphics.Texture;
import com.rworld.core.utilities.Utility;

public class MeshImpl implements IMesh {
	
	public void init(GL10 gl, MeshLoader loader, float scaleX, float scaleY, float scaleZ) {
		int[] buffer = new int[1];
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(loader.GetElementCount() * 3 * Float.SIZE);
		vbb.order(ByteOrder.nativeOrder());
		FloatBuffer _vertexBuffer = vbb.asFloatBuffer();

		ByteBuffer tbb = ByteBuffer.allocateDirect(loader.GetElementCount() * 2 * Float.SIZE);
		tbb.order(ByteOrder.nativeOrder());
		FloatBuffer _texCoordBuffer = tbb.asFloatBuffer();
	
		ByteBuffer ibb = ByteBuffer.allocateDirect(loader.GetIndexCount() * Short.SIZE);
		ibb.order(ByteOrder.nativeOrder());
		ShortBuffer _indexBuffer = ibb.asShortBuffer();

		_indexCount = loader.GetIndexCount();
		loader.loadBuffers(_vertexBuffer, _texCoordBuffer, _indexBuffer, scaleX, scaleY, scaleZ);

		_vertexBuffer.rewind();
		((GL11) gl).glGenBuffers(1, buffer, 0);
        _vertexBufferIndex = buffer[0];
        ((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, _vertexBufferIndex);
        ((GL11) gl).glBufferData(GL11.GL_ARRAY_BUFFER, loader.GetElementCount() * 3 * Float.SIZE, _vertexBuffer, GL11.GL_STATIC_DRAW);
		
		_texCoordBuffer.rewind();
		((GL11) gl).glGenBuffers(1, buffer, 0);
        _textureBufferIndex = buffer[0];
        ((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, _textureBufferIndex);
        ((GL11) gl).glBufferData(GL11.GL_ARRAY_BUFFER, loader.GetElementCount() * 2 * Float.SIZE, _texCoordBuffer, GL11.GL_STATIC_DRAW);
        
		_indexBuffer.rewind();
		((GL11) gl).glGenBuffers(1, buffer, 0);
        _indexBufferIndex = buffer[0];
        ((GL11) gl).glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _indexBufferIndex);
        ((GL11) gl).glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, loader.GetIndexCount() * Short.SIZE, _indexBuffer, GL11.GL_STATIC_DRAW);
        
        loader.dispose();
	}

	public void dispose(GL10 gl) {
		int[] buffer = new int[1];
		
		if (_texture != null) {
			_texture = Utility.safeDispose(_texture.unref(), gl);
		}
		buffer[0] = _vertexBufferIndex;
		((GL11) gl).glDeleteBuffers(1, buffer, 0);
		buffer[0] = _textureBufferIndex;
		((GL11) gl).glDeleteBuffers(1, buffer, 0);
		buffer[0] = _indexBufferIndex;
		((GL11) gl).glDeleteBuffers(1, buffer, 0);
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
		((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, _vertexBufferIndex);
		((GL11) gl).glVertexPointer(3, GL10.GL_FLOAT, 0, 0);

		if (_texture != null) {
			_texture.bind(gl);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, _textureBufferIndex);
			((GL11) gl).glTexCoordPointer(2, GL10.GL_FLOAT, 0, 0);
		}
		
		((GL11) gl).glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, _indexBufferIndex);
		((GL11) gl).glDrawElements(GL10.GL_TRIANGLES, _indexCount, GL10.GL_UNSIGNED_SHORT, 0);
		
		if (_texture != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		((GL11) gl).glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
		((GL11) gl).glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        
		gl.glPopMatrix();
	}
	
	public void draw(GL10 gl, ArrayList<? extends GameEntity> entities) {
		int s = entities.size();
		for(int i = 0; i < s; i++) {
			draw(gl, entities.get(i));
		}
	}
	
	private int _vertexBufferIndex;
	private int _textureBufferIndex;
	private int _indexBufferIndex;
	private int _indexCount;
	private Texture _texture;
}
