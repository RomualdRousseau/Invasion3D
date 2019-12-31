package com.rworld.core.graphics.high;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import com.rworld.core.GameActivity;
import com.rworld.core.graphics.Font;
import com.rworld.core.graphics.GraphicEngine;
import com.rworld.core.graphics.Surface;

public class GraphicEngineImpl extends GraphicEngine {

	public GraphicEngineImpl(GameActivity activity) {
		super(activity);
	}
	
	@Override
	public void enterView2D() {
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glPushMatrix();
		gl.glLoadIdentity();
	}

	@Override
	public void leaveView2D() {
		gl.glPopMatrix();
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_CULL_FACE);
	}
	
	@Override
	public void drawSurface2D(Surface surface, int x, int y, int width, int height) {
		virtualRectToDeviceRect(x, y, width, height, rect);
		
		crop[0] = surface.x;
		crop[1] = surface.y + surface.height;
		crop[2] = surface.width;
		crop[3] = -surface.height;
		
		if (surface.getTexture() != null) {
			surface.getTexture().bind(gl);
		}
		
		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, crop, 0);
		((GL11Ext) gl).glDrawTexfOES(rect[0], rect[1], 0.0f, rect[2], rect[3]);
	}
	
	@Override
	public void drawText2D(Font font, int x, int y, float scale, String text) {
		virtualRectToDeviceRect(x, y, (int) (font.getWidth() * scale), (int) (font.getHeight() * scale), rect);
		float xx = rect[0];
		
		if (font.getTexture() != null) {
			font.getTexture().bind(gl);
		}

		for(int i = 0; i < text.length(); i++, rect[0] += rect[2]) {
			if(text.charAt(i) == '\n') {
				rect[0] = xx - rect[2];
				rect[1] -= rect[3];
			}
			else if(text.charAt(i) >= ' ') {
				int c = text.codePointAt(i) - 32;

				crop[0] = font.getX(c);
				crop[1] = font.getY(c) + font.getHeight();
				crop[2] = font.getWidth();
				crop[3] = -font.getHeight();
	            
				((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, crop,0);
				((GL11Ext) gl).glDrawTexfOES(rect[0], rect[1], 0.0f, rect[2], rect[3]);
			}
		}
	}
	
	private float[] rect = {0, 0, 0, 0};
	private int[] crop = {0, 0, 0, 0};
}
