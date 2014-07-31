package com.ldaniels528.woozle.breakout;

/**
 * This interface is implemented by {@link Entity entities}
 * that increase the players score upon 
 * their death.
 * @author lawrence.daniels@gmail.com
 */
interface Scoreable {
	
	/**
	 * Returns the number of points earned for
	 * destroying the implementing entity
	 * @return
	 */
	int getScorePoints();

}
