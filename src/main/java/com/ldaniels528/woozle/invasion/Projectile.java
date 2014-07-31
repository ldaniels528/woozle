package com.ldaniels528.woozle.invasion;

/**
 * Represents a projectile
 * @author lawrence.daniels@gmail.colm
 */
abstract class Projectile extends Entity {

	/**
	 * Creates a new actor instance
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 * @param width the width of the entity
	 * @param height the height of the entity
	 */
	public Projectile( final double x, 
				  	   final double y, 
				  	   final double width, 
				  	   final double height) {
		super( x, y, width, height );
	}

}
