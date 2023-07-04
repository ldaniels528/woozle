package com.ldaniels528.woozle.hangman;

import java.awt.*;

import static java.awt.Color.WHITE;

/**
 * Represents the hanging man
 * @author lawrence.daniels@gmail.com
 */
class HangingMan {
	// line thickness constants
	private static final Stroke THICKLINE	=  new BasicStroke( 4 );
	private static final Stroke THINLINE	=  new BasicStroke( 1 );
	
	// the maximum number of moves
	private static final int MAX_MOVES 		= 12;
	
	// the positions of the body parts
	private static final int BASE_W			= 100;
	private static final int POLE_H 		= 120;
	private static final int HEAD_HW		= 20;
	private static final int ROPE_H			= 10;
	private static final int NECK_H			= 5;
	private static final int BODY_H			= 50;
	private static final int BODY_W			= 30;
	private static final int LIMB_L			= 20;
	
	// color constants
	private static final Color CLOTHES 	= WHITE;
	private static final Color HANGER	= WHITE;
	private static final Color SKIN		= WHITE; 
	
	// internal fields
	private int misses;
	
	/**
	 * Default Constructor
	 */
	public HangingMan() {
		super();
	}
	
	/**
	 * Indicates whether the player is out of moves
	 * @return true, if the player is out of moves
	 */
	public boolean isOutOfMoves() {
		return ( misses == MAX_MOVES );
	}
	
	/**
	 * Indicates whether only one more move remains
	 * @return true, if only one more move remains
	 */
	public boolean hasOneMoreMove() {
		return ( misses == ( MAX_MOVES - 1 ) );
	}
	
	/**
	 * Returns the number of incorrect letters
	 * @return the number of incorrect letters
	 */
	public int getMisses() {
		return misses;
	}
	
	/**
	 * Counts a letter miss
	 */
	public void miss() {
		this.misses++;
	}
	
	/** 
	 * Resets the hanging man
	 */
	public void reset() {
		this.misses = 0;
	}
	
	/**
	 * Draws the hanging man
	 * @param g the given {@link Graphics2D graphics context}
	 */
	public void render( final Graphics2D g ) {
		// set the color
		int cx = BASE_W / 2;
		int cy = 10;
		
		// set the drawing color
		g.setStroke( THICKLINE );
		
		// draw the hanging man
		switch( misses ) {	
			// draw leg #2
			case 12:
				g.setColor( CLOTHES );
				g.drawLine( cx + HEAD_HW * 3/2, 
									cy + HEAD_HW + ROPE_H + NECK_H + BODY_H, 
									cx + HEAD_HW * 3/2, 
									cy + HEAD_HW + ROPE_H + NECK_H + BODY_H + LIMB_L );
			
			// draw leg #1
			case 11:
				g.setColor( CLOTHES );
				g.drawLine( cx + HEAD_HW, 
									cy + HEAD_HW + ROPE_H + NECK_H + BODY_H, 
									cx + HEAD_HW, 
									cy + HEAD_HW + ROPE_H + NECK_H + BODY_H + LIMB_L );
				
			// draw arm #2
			case 10:
				g.setColor( CLOTHES );
				g.drawLine( cx + HEAD_HW * 3/2, 
									cy + HEAD_HW + ROPE_H + NECK_H, 
									cx + HEAD_HW * 3/2 + LIMB_L, 
									cy + HEAD_HW + ROPE_H + NECK_H + LIMB_L );
			
			// draw arm #1
			case 9:
				g.setColor( CLOTHES );
				g.drawLine( cx + HEAD_HW, 
									cy + HEAD_HW + ROPE_H + NECK_H, 
									cx + HEAD_HW - LIMB_L, 
									cy + HEAD_HW + ROPE_H + NECK_H + LIMB_L );
				
			// draw body
			case 8:
				g.setColor( CLOTHES );
				g.drawOval( cx + HEAD_HW/2, 
									cy + HEAD_HW + ROPE_H + NECK_H, 
									BODY_W,
									BODY_H );
				
			// draw neck
			case 7:
				g.setColor( SKIN );
				g.drawLine( cx + BASE_W/4, 
									cy + HEAD_HW + ROPE_H, 
									cx + BASE_W/4, 
									cy + HEAD_HW + ROPE_H + NECK_H );
				
			// draw head
			case 6:
				g.setColor( SKIN );
				g.drawOval( cx + BASE_W/4 - HEAD_HW/2, cy + ROPE_H, HEAD_HW, HEAD_HW );
				
				// draw rope
			case 5:
				g.setColor( HANGER );
				g.drawLine( cx + BASE_W/4, cy, cx + BASE_W/4, cy + 10 );
				
			// draw bar (horizontal pole)
			case 4:
				g.setColor( HANGER );
				g.drawLine( cx - BASE_W/4, cy, cx + BASE_W/4, cy );
				
			// draw the pole support
			case 3:
				g.setColor( HANGER );
				g.drawLine( cx - BASE_W/4, cy + POLE_H - 15, cx, cy + POLE_H );
				
			// draw the pole
			case 2:
				g.setColor( HANGER );
				g.drawLine( cx - BASE_W/4, cy, cx - BASE_W/4, cy + POLE_H );
				
			// draw the base
			case 1: 
				g.setColor( HANGER );
				g.drawLine( cx - BASE_W/2, cy + POLE_H, cx + BASE_W/2, cy + POLE_H );
		}
		
		// restore the old stroke
		g.setStroke( THINLINE );
	}

}
