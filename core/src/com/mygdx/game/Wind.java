// Wind.java
// Jin Hao Dong & Alex Lin
/* This program is a wind class that stores/initializes the direction and magnitude of the wind,
 * as well as outputing the speed using its functions.
 */
package com.mygdx.game;
import java.util.*;


public class Wind 
{
	private int direction; // determines the direction of the wind (left/right)
	private double speed; // speed of the wind

	public Wind() {
	// this method is an initializer of the object
		Random rand = new Random();
		direction = rand.nextInt(2) == 0? -1:1; // randomly determines the direction the wind blows
		speed = rand.nextDouble() * 10 * direction; // and its speed
	}

	public double getSpeed() {
	// this method outputs the speed of the wind
		return speed;
	}
}
