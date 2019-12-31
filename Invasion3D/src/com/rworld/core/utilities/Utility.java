package com.rworld.core.utilities;

import javax.microedition.khronos.opengles.GL10;

import com.rworld.core.IDisposable;
import com.rworld.core.graphics.IDisposableGL;

public class Utility {
	
	public static <T extends IDisposable> T safeDispose(T o) {
		if(o != null) {
			o.dispose();
		}
		return null;
	}
	
	public static <T extends IDisposableGL> T safeDispose(T o, GL10 gl) {
		if(o != null) {
			o.dispose(gl);
		}
		return null;
	}
	
}
