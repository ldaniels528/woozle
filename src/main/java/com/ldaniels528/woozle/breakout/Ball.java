package com.ldaniels528.woozle.breakout;

import static com.ldaniels528.woozle.GameDisplayPane.BOARD_HEIGHT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static com.ldaniels528.woozle.breakout.Direction.NE;
import static com.ldaniels528.woozle.breakout.Direction.NW;
import static com.ldaniels528.woozle.breakout.Direction.SE;
import static com.ldaniels528.woozle.breakout.Direction.SW;
import static java.awt.Color.WHITE;

import java.awt.Graphics2D;

import com.ldaniels528.woozle.Randomizer;

/**
 * Break-Out Ball
 * @author lawrence.daniels@gmail.com
 */
class Ball extends Entity {
	private static final double INITIAL_SPEED 	=  4;
	private static final double MAXIMUM_SPEED 	= 12;
	private static final double SPEED_INCREMENT	=  0.075;
	private static final int SIZE = 10;
	private final BreakOutPlayingField playingField;
	private Direction direction;
	private double initialX;
	private double initialY;
	private double speed;
	
	/**
	 * Creates a new ball instance
	 * @param playingField the given {@link BreakOutPlayingField playing field}
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 */
	public Ball( final BreakOutPlayingField playingField, 
				 final double x, 
				 final double y ) {
		super( x, y, SIZE, SIZE );
		this.initialX		= x;
		this.initialY		= y;
		this.playingField	= playingField;
		this.direction 		= getRandomDirection( NE, NW );
		this.speed			= INITIAL_SPEED;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#handleCollsion(com.ldaniels528.woozle.breakout.Entity)
	 */
	public void handleCollsion( final Entity entity ) {
		// redirect the ball's bearing
		switch( direction ) {
			case NE: direction = SW; break;
			case NW: direction = SE; break;
			case SE: direction = NE; break;
			case SW: direction = NW; break;
		}
		
		// move the ball away from the collision
		if( entity instanceof Paddle ) {
			while( intersects( entity ) ) {
				update( .1 );
			}
		}
		
		// speed up the ball
		if( speed < MAXIMUM_SPEED ) {
			speed += SPEED_INCREMENT;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#render(java.awt.Graphics2D)
	 */
	public void render( final Graphics2D g ) {
		// compute the center
		final int cx = (int)x;
		final int cy = (int)y;
		
		// draw the ball
		g.setColor( WHITE );
		g.fillOval( cx, cy, SIZE, SIZE );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#reset()
	 */
	@Override
	public void reset() {
		this.x 			= initialX;
		this.y			= initialY;
		this.speed		= INITIAL_SPEED;
		this.direction 	= getRandomDirection( NE, NW );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#update(double)
	 */
	public void update( final double ct ) {
		// compute the delta values
		final double dx = speed * ct;
		final double dy = speed * ct;
		
		// move the ball
		switch( direction ) {
			case NE: moveNorthEast( dx, dy ); break;
			case NW: moveNorthWest( dx, dy ); break;
			case SE: moveSouthEast( dx, dy ); break;
			case SW: moveSouthWest( dx, dy ); break;
		}
	}
	
	/** 
	 * Moves the ball in a north east heading
	 * @param dx the delta X value
	 * @param dy the delta Y value
	 */
	private void moveNorthEast( final double dx, final double dy ) {
		// if the east wall is hit ..
		if( x + dx >= BOARD_WIDTH ) {
			direction = NW; 
		}
		// if the north wall is hit ..
		if( y - dx <= 0 ) {
			direction = SE; 
		}
		// move forward
		else {
			x += dx;
			y -= dy;
		}
	}
	
	/** 
	 * Moves the ball in a north west heading
	 * @param dx the delta X value
	 * @param dy the delta Y value
	 */
	private void moveNorthWest( final double dx, final double dy ) {
		// if the west wall is hit ..
		if( x - dx <= 0 ) {
			direction = NE; 
		}
		// if the north wall is hit ..
		if( y - dx <= 0 ) {
			direction = SW;
		}
		// move forward
		else {
			x -= dx;
			y -= dy;
		}
	}
	
	/** 
	 * Moves the ball in a south east heading
	 * @param dx the delta X value
	 * @param dy the delta Y value
	 */
	private void moveSouthEast( final double dx, final double dy ) {
		// if the east wall is hit ..
		if( x + dx >= BOARD_WIDTH ) {
			direction = SW;
		}
		// if the south wall is hit ..
		if( y + dx >= BOARD_HEIGHT ) {
			playingField.outOfBounds();
		}
		// move forward
		else {
			x += dx;
			y += dy;
		}
	}
	
	/** 
	 * Moves the ball in a south west heading
	 * @param dx the delta X value
	 * @param dy the delta Y value
	 */
	private void moveSouthWest( final double dx, final double dy ) {
		// if the west wall is hit ..
		if( x - dx <= 0 ) {
			direction = SE; 
		}
		// if the south wall is hit ..
		if( y + dx >= BOARD_HEIGHT ) {
			playingField.outOfBounds();
		}
		// move forward
		else {
			x -= dx;
			y += dy;
		}
	}
	
	/** 
	 * Returns a random direction
	 * @param directions the array of {@link Direction directions} available for return
	 * @return the random {@link Direction direction}
	 */
	private static Direction getRandomDirection( final Direction... directions ) {
		final int index = Randomizer.getInstance().nextInt( Integer.MAX_VALUE ) % directions.length;
		return directions[ index ];
	}

}
