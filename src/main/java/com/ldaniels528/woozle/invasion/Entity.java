package com.ldaniels528.woozle.invasion;

import java.awt.*;

/**
 * This class is the base class for all entities
 * within the Invasion Virtual World.
 * @author lawrence.daniels@gmail.com
 */
abstract class Entity {
	protected double x,y;
	protected double width;
	protected double height;
	private boolean alive;
	
	/**
	 * Creates a new entity instance
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 * @param width the width of the entity
	 * @param height the height of the entity
	 */
	public Entity( final double x, 
				   final double y, 
				   final double width, 
				   final double height ) {
		this.x		= x;
		this.y		= y;
		this.width	= width;
		this.height	= height;
		this.alive	= true;
	}
	
	/** 
	 * Handles the collision between this entity and the given entity
	 * @param entity the given {@link Entity entity}
	 */
	public abstract void handleCollsion( Entity entity );
	
	/**
	 * Renders the entity onto the graphics context
	 * @param g the given {@link Graphics2D graphics context}
	 */
	public abstract void render( Graphics2D g );
	
	/** 
	 * Resets the entity to it's original position
	 */
	public abstract void reset();

	/**
	 * Updates the entity
	 * @param ct the given cycle time
	 */
	public abstract void update( double ct );
	
	/**
	 * Causes the entity to die
	 */
	public void die() {
		this.alive = false;
	}
	
	/**
	 * Returns the x-axis coordinate 
	 * @return the x-axis coordinate 
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Returns the y-axis coordinate 
	 * @return the y-axis coordinate 
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * Indicates whether the entity is alive
	 * @return true, if the entity is alive
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/** 
	 * Returns the width of the entity
	 * @return the width of the entity
	 */
	public double getWidth() {
		return width;
	}
	
	/** 
	 * Returns the height of the entity
	 * @return the height of the entity
	 */
	public double getHeight() {
		return height;
	}
	
	/** 
	 * Determines whether an intersection has occurred
	 * between this entity and the given entity
	 * @param entity the given {@link Entity entity}
	 * @return true, if an intersection has occurred
	 * between this entity and the given entity
	 */
	public boolean intersects( final Entity entity ) {
		// get the dimensions for entity A
		final double x1A = x - ( (double)width / 2 );
		final double y1A = y - ( (double)height / 2 );
		final double x2A = x1A + width;
		final double y2A = y1A + height;
		
		// check for intersections
		//	(x1A,y1A) in (x1B,y1B,x2B,y2b)?
		return ( entity.intersects( x1A, y1A ) ||
				// (x2A,y2A) in (x1B,y1B,x2B,y2b)?
				entity.intersects( x2A, y2A ) );
	}
	
	/**
	 * Determines whether the given (x,y) point intersects the entity
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 * @return true, if the given (x,y) point intersects the entity
	 */
	public boolean intersects( final double x, final double y ) {
		// get the dimensions for entity A
		final double x1 = this.x - ( (double)width / 2 );
		final double y1 = this.y - ( (double)height / 2 );
		final double x2 = x1 + width;
		final double y2 = y1 + height;
		return ( x >= x1 ) && ( x <= x2 ) && 
					( y >= y1 ) && ( y <= y2 );
	}

}
