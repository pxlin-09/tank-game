// Blackhole.java
// Jin Hao Dong & Alex Lin
/* This program is a blackhole class that stores/initializes the x,y position and radius of the blackhole,
 * as well as outputing those variables using its functions.
 */
package com.mygdx.game;

public class Blackhole {
	private int x; // x,y position of the black hole (center point)
	private int y;
	private int size; // radius of the black hole
	
	public Blackhole(int x, int y, int size) {
	// thie method is an initalizer of the object
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	public int getX() {
	// this method outputs the x-position
		return x;
	}
	
	public int getY() {
	// this method outputs the y-position
		return y;
	}
	
	public int getSize() {
	// this method outputs the radius
		return size;
	}
}