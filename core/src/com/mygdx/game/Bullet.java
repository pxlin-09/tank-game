// Bullet.java
// Jin Hao Dong & Alex Lin
/* This program is a bullet class that stores/initializes and manipulates bullet's data.
 * It includes storing x,y position, x,y velocity, flying angle, image, and indicating its state (i.e. whether it is flying or not).
 * Functions includes:
 * - initializing its x,y position, velocity and angle
 * - updating its new position per frame depending on the above factors
 * - setting bullet image
 * - outputing key variables
 * - determining if the bullet has collided with objects like tank, wall, ground and black hole. 
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

public class Bullet 
{
	private boolean fly; // determines if the bullet is in air
	private double angle; // angle the bullet is flying to
	
	private double x; // x,y position of the bullet
	private double y;
	
	private double forward; // horizontal and vertical velocity of the bullet
	private double upward;
	
	private Texture img; // image of the bullet

	public Bullet(double angle, double x, double y) {
	// this method is an initializer of the object
		fly = true;
		this.angle = angle;
		this.x = x;
		this.y = y;
	 }

	public boolean inAir() {
	// this method outputs the state of the bullet
		return fly;
	}
	
	public void userShot(double power, double windSpeed) {
	// this method sets the horizontal/vertical velocity according to the power of the shot, angle and wind speed for user's tank
		forward =  power*Math.cos(angle)+windSpeed;
		upward = power*Math.sin(angle);
	}
	
	public void cpuShot(double power) {
	// this method sets the horizontal/vertical velocity according to the power of the shot and angle for computer's tank
		forward =  power*Math.cos(angle); // computer's bullet is not affected by wind speed
		upward = power*Math.sin(angle);
	}
	
	public void fly(double gravity) {
	// this method changes x,y position and velocity of the bullet affected by gravity
		x += forward;
		y += upward;
		upward = upward - gravity;
		
		if(y <= 0 || x<=0 || x>=1500)
		{
			fly = false;
		}
	}
	
	public void setImg(Texture img) {
	// this method sets the image of the bullet by an input
		this.img = img;
	}
	
	public float getX() {
	// this method outputs the x-position of the bullet
		return (float)x;
	}
	
	public float getY() {
	// this method outputs the y-position of the bullet
		return (float)y;
	}
	
	public Texture getImg() {
	// this method outputs the image of the bullet
		return img;
	}
	
	public boolean collideHead(Character tank) {
	// this method determines if the bullet has hit the tank's head
		if (x >= tank.getHeadX() && x <= tank.getHeadX() + tank.getHeadSizeX()) {
			if (y >= tank.getHeadY() && y <= tank.getHeadY() + tank.getHeadSizeY()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean collideBase(Character tank) {
		// this method determines if the bullet has hit the tank's base
		if (x >= tank.getX() && x <= tank.getX() + tank.getBaseSizeX()) {
			if (y >= tank.getY() && y <= tank.getY() + tank.getBaseSizeY()) {
				return true;
			}
		}
		return false;
	}
	
	public void endFly() {
	// this method stops the bullet from flying
		fly = false;
	}
	
	public boolean collideWall(Wall wall) {
	// this method determines if the bullet has hit the wall
		if (x >= wall.getX() && x <= wall.getX() + wall.getWidth()) {
			if (y >= wall.getY() && y <= wall.getY() + wall.getHeight()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean collideBlackhole(Blackhole hole) {
	// this method determines if the bullet has hit the black hole
		double d = Math.sqrt((x - hole.getX()) * (x - hole.getX()) + (y - hole.getY()) * (y - hole.getY()));
		if (d <= hole.getSize()) {
			return true;
		}
		return false;
	}
	
	public void changePos(int newX, int newY) {
	// this method changes the x,y position by inputs
		x = newX;
		y = newY;
	}
	
	public void clearMovement() {
	// this method clears all movement of the bullet
		forward = 0;
		upward = 0;
	}
	
}
