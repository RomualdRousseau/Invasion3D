package com.rworld.core.maths;

public class Rectangle2D {

	public int x = 0;
	public int y = 0;
	public int width = 100;
	public int height = 50;
	
	public Rectangle2D() {
	}
	
	public Rectangle2D(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void set(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(int a, int b) {
		return (x <= a) && (a < x + width) && (y <= b) && (b < y + height);
	}
}
