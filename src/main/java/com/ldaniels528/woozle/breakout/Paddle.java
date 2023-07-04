package com.ldaniels528.woozle.breakout;

import com.ldaniels528.woozle.SoundManager;

import java.awt.*;

import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static com.ldaniels528.woozle.breakout.BreakOutSoundKeys.BOUNCE;
import static java.awt.Color.WHITE;

/**
 * Break-Out Paddle
 * @author lawrence.daniels@gmail.com
 */
class Paddle extends Entity {

	/**
	 * Creates a new paddle instance
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 */
	public Paddle( final double x, final double y, double width, double height ) {
		super( x, y, width, height );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#handleCollsion(com.ldaniels528.woozle.breakout.Entity)
	 */
	public void handleCollsion( final Entity entity ) {
		SoundManager.getInstance().play( BOUNCE );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#render(java.awt.Graphics2D)
	 */
	public void render( final Graphics2D g ) {
		// compute the center
		final int cx = (int)x - (int)width/2;
		final int cy = (int)y;
		
		// render the paddle
		g.setColor( WHITE );
		g.fillRect( cx, cy, (int)width, (int)height );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#reset()
	 */
	@Override
	public void reset() {
		this.x 	= BOARD_WIDTH / 2;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#update(double)
	 */
	public void update( final double ct ) {
		
	}

}
