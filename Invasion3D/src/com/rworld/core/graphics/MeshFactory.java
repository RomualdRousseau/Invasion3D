package com.rworld.core.graphics;

import javax.microedition.khronos.opengles.GL10;

import com.rworld.core.graphics.high.MeshImpl;

public class MeshFactory {

	public static IMesh createInstance(GL10 gl, MeshLoader loader, float scaleX, float scaleY, float scaleZ) {
		IMesh mesh = new MeshImpl();
		mesh.init(gl, loader, scaleX, scaleY, scaleZ);
		return mesh;
	}
}
