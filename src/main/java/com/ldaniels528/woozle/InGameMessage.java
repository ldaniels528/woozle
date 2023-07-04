package com.ldaniels528.woozle;

import java.awt.*;

import static com.ldaniels528.woozle.GameDisplayPane.BOARD_HEIGHT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static java.awt.Color.YELLOW;
import static java.awt.Font.BOLD;

/**
 * Represents an In Game Message
 * @author lawrence.daniels@gmail.com
 */
public class InGameMessage {
	private static final Font DEFAULT_FONT = new Font( "Courier", BOLD, 40 );
	private final String text;
	private final Font font;
	private final Color color;
	private final long lifeSpanMillis;
	private long createdTimeMillis;
	private int px,py;
	
	/**
	 * Default Constructor
	 */
	public InGameMessage( final String text, 
						  final long lifeSpanMillis ) {
		this( text, DEFAULT_FONT, YELLOW, -1, -1, lifeSpanMillis );
	}
	
	/**
	 * Default Constructor
	 */
	public InGameMessage( final String text, 
						  final Color color,
						  final long lifeSpanMillis ) {
		this( text, DEFAULT_FONT, color, -1, -1, lifeSpanMillis );
	}
	
	/** 
	 * Creates a new In-Game message
	 * @param text the given text to display
	 * @param font the text {@link Font font}
	 * @param color the text {@link Color color}
	 * @param x the given x-coordinate of the text position
	 * @param y the given y-coordinate of the text position
	 * @param lifeSpanMillis the duration to display the text
	 */
	public InGameMessage( final String text, 
						  final Font font,
						  final Color color,
						  final int x, 
						  final int y, 
						  final long lifeSpanMillis ) {
		this.text				= text;
		this.font				= font;
		this.color				= color;
		this.px					= x;
		this.py					= y;
		this.lifeSpanMillis		= lifeSpanMillis;
		this.createdTimeMillis	= System.currentTimeMillis();
	}
	
	/**
	 * Draws the message to the off-screen context
	 * @param g the given {@link Graphics2D off-screen context}
	 */
	public void render( final Graphics2D g ) {
		// set the text font & color
		g.setFont( font );
		g.setColor( color );
		
		// determine where to display the text?
		if( px == -1 || py == -1 ) {
			// get the font metrics
			final FontMetrics metrics = g.getFontMetrics();
			
			// get the width & height of the text 
			final int width = metrics.stringWidth( text );
			final int height = font.getSize();
			
			// determine the center
			px = ( BOARD_WIDTH - width ) / 2;
			py = ( BOARD_HEIGHT - height ) / 2;
		}
		 
		g.drawString( text, px, py );
	}
	
	/**
	 * Indicates whether the message has expired
	 * @return true, if the message has expired
	 */
	public boolean isExpired() {
		return ( System.currentTimeMillis() - createdTimeMillis ) >= lifeSpanMillis;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return text;
	}
}
