// Wall.java
// Jin Hao Dong & Alex Lin
/* This program is a wall class that stores/initialize the x,y position and width/height of the wall,
 * as well as outputing those stats using its functions.
 */
package com.mygdx.game;

public class Wall {
	private int x; // x,y position of the wall
	private int y;
	private int width; // width and height of the wall
	private int height;
	
	public Wall(int x, int y, int width, int height) {
	// this method is an initializer of the object
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
	// this method outputs the x-position
		return x;
	}
	
	public int getY() {
	// this method outputs the y-position
		return y;
	}
	
	public int getWidth() {
	// this method outputs the width
		return width;
	}
	
	public int getHeight() {
	// this method outputs the height
		return height;
	}
}