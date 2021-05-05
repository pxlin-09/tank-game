// Character.java
// Jin Hao Dong & Alex Lin
/* This program is a character class that stores/initializes and manipulates tank's data.
 * It includes storing the x,y position, health, energy, angle, magnitude, sizes of the tank, image, and indicating the owner (i.e. user/comptuer).
 * Its functions includes:
 * - setting tank's position
 * - setting tank's image
 * - outputing key variables
 * - adjusting x,y position, projectile angle & magnitude, health, and energy
 */
package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;

class Character {
	public static final int USER = 1;  // determines if it is the user's tank or
	public static final int CPU = -1; // the computer's tank
	  
	  
	private int baseX;  // x,y position and size of base of the tank
	private int baseY;
	private int baseSizeX = 60;
	private int baseSizeY = 20;
	
	private int headX; // x,y position and size of head of the tank
	private int headY;
	private int headSizeX = 30;
	private int headSizeY = 5;
	  
	private int health;
	private double angle;
	private int owner; // user or computer
	private int energy;

	private Texture tank, gun; // image of tank and its gun
	  
	  
	public Character(int health, int energy, double angle, int owner) { 
	// this method is an initializer of the object
	  this.health = health;  
	  this.angle = angle;
	  this.owner = owner;
	  this.energy = energy;
	}
	
	public void randSpot() {
	// this method sets a relatively random x,y position for a tank
		Random rand = new Random();
		if(owner == USER) {	
			baseX = rand.nextInt(599)+1;
		}
		else {
			baseX = rand.nextInt(599)+800;
		}
		headX = baseX + 30;
		
		baseY = 0;
		headY = baseY + 25;
	}
	
	public void setImg(Texture img, Texture gun) {
	// this method sets the image of the tank and its gun by input images
		this.tank = img;
		this.gun = gun;
	}
	
	public int getX() {
	// this method outputs the x-position of the base
		return baseX;
	}
	
	public int getY() {
	// this method outputs the y-position of the base
		return baseY;
	}
	
	public int getHeadX() {
	// this method outputs the x-position of the head
		return headX;
	}
	
	public int getHeadY() {
	// this method outputs the y-position of the head
		return headY;
	}
	
	public int getHeadSizeX() {
	// this method outputs the width of the head
		return headSizeX;
	}
	
	public int getHeadSizeY() {
	// this method outputs the height of the head
		return headSizeY;
	}
	
	public int getBaseSizeX() {
	// this method outputs the width of the base
		return baseSizeX;
	}
	
	public int getBaseSizeY() {
	// this method outputs the height of the base
		return baseSizeY;
	}
	
	public double getHealth() { 
	// this method outputs the health of the tank
		return health;
	}
	
	public Texture getTank() { 
	// this method outputs the image of the tank
		return tank;
	}
	
	public Texture getGun() {
	// this method outputs the image of them tank's gun
		return gun;
	}
	
	public float getAngleInDegrees() {
	// this method outputs the angle the tank is aiming in degrees
		return (float) Math.toDegrees(angle);
	}
	
	public double getAngle() {
	// this method outputs the angle the tank is aiming in radians
		return angle;
	}

	public void userRotateRight() {
	// this method decreases the aiming angle of user's tank
		if(angle > 0) {
			angle -= 0.01;
		}
	}
  
	public void userRotateLeft() { 
	// this method increases the aiming angle of user's tank
		if(angle < Math.PI/2) {
			angle += 0.01;
		}
	}
  
	public void cpuRotateRight() {
	// this method decreases the aiming angle of computer's tank
		if(angle > (0-Math.PI/2)) {
			angle -= 0.01;
		}
	}
  
	public void cpuRotateLeft() {
	// this method increases the aiming angle of computer's tank
		if(angle < 0) {
			angle += 0.01;
    	}
	}
  
	public void moveLeft() {
	// this method decreases the horizontal position of user's tank by an input magnitude
		baseX -= 1;
		headX -= 1;
		baseX = baseX >= 100 ? baseX : 100; // tank cannot go off screen
		headX = headX >= 130 ? headX : 130; // nor can it go across the middle
	}
  
	public void moveRight() {
	// this method increases the horizontal position of user's tank by an input magnitude
		baseX += 1;
		headX += 1;
		baseX = baseX <= 600 ? baseX : 600;
		headX = headX <= 630 ? headX : 630;
	}
  
	public void cpuMoveLeft(double factor) {
	// this method decreases the horizontal position of computer's tank by an input magnitude
		baseX -= factor;
		headX -= factor;
		baseX = baseX >= 800 ? baseX : 800;
		headX = headX >= 830 ? headX : 930;
	}
  
	public void cpuMoveRight(double factor) {
	// this method increases the horizontal position of computer's tank by an input magnitude
		baseX += factor;
		headX += factor;
		baseX = baseX <= 1400 ? baseX : 1400;
		headX = headX <= 1430 ? headX : 1430;
	}
  
	public void setAngle(double degrees) {
	// this method sets the angle of the tank by an input
		angle = Math.toRadians(degrees);
	}
  
	public void getHit(int enemyEnergy) {
	// this method decreases the health of the tank according to the type of hit
		if (enemyEnergy == 3) { // health goes down to 0 if the enemy used a special bullet
			health = 0;
		}
		else if(health > 0) {
			health -= 1;  
		}
	}
  
	public void energyUp() {
	// this method increases the energy of the tank
		energy += 1;
		energy = energy > 3 ? 3 : energy; // energy is 3 at maximum
	}
  
	public int getEnergy() {
	// this method outputs the energy of the tank
		return energy;
	}
  
	public void clearEnergy() {
	// this method resets the energy of the tank
		energy = 0;
	}
  
	public void reset() {
	// this method resets the health, energy, and angle of the tank
		health = 3;
		energy = 0;
		angle = 0;
	}
}