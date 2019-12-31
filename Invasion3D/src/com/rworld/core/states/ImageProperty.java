package com.rworld.core.states;

public class ImageProperty {

	public String filePath = null;
	public int width = 256;
	public int height = 256;
	
	public void set(String aFilePath, int aWidth, int aHeight) {
		filePath = aFilePath;
		width = aWidth;
		height = aHeight;
	}
}
