package com.ldaniels528.woozle.breakout;

import com.ldaniels528.woozle.SoundManager;

import java.awt.*;

/**
 * Represents a non-interactive block
 * @author lawrence.daniels@gmail.com
 */
class Block extends Entity implements Scoreable {
	private final BreakOutPlayingField playingField;
	private final Color color;

	/** 
	 * Creates a new block
	 * @param playingField the given {@link BreakOutPlayingField playing field}
	 * @param x the given x-axis coordinate 
	 * @param y the given y-axis coordinate
	 * @param width the given width of the block
	 * @param height the given height of the block
	 */
	public Block( final BreakOutPlayingField playingField, 
				  final double x, 
				  final double y, 
				  final double width,
				  final double height, final Color color ) {
		super( x, y, width, height );
		this.playingField	= playingField;
		this.color 			= color;
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#handleCollsion(com.ldaniels528.woozle.breakout.Entity)
	 */
	@Override
	public void handleCollsion( final Entity entity ) {
		SoundManager.getInstance().play( BreakOutSoundKeys.BOUNCE );
		playingField.score( this );
		playingField.blockDied();
		die();
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#render(java.awt.Graphics2D)
	 */
	@Override
	public void render( final Graphics2D g ) {
		g.setColor( color );
		g.fill3DRect( (int)x, (int)y, (int)width, (int)height, true );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#reset()
	 */
	@Override
	public void reset() {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Entity#update(double)
	 */
	@Override
	public void update( final double ct ) {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.breakout.Scoreable#getScorePoints()
	 */
	public int getScorePoints() {
		return 5;
	}

}
