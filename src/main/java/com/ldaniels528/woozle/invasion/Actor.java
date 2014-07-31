package com.ldaniels528.woozle.invasion;

/**
 * Represents an armed sentient entity
 * @author lawrence.daniels@gmail.colm
 */
abstract class Actor extends Entity {

	/**
	 * Creates a new actor instance
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 * @param width the width of the entity
	 * @param height the height of the entity
	 */
	public Actor( final double x, 
				  final double y, 
				  final double width, 
				  final double height) {
		super( x, y, width, height );
	}
	
	/**
	 * Causes the actor to fire their weapon
	 */
	public abstract void fire();

}
