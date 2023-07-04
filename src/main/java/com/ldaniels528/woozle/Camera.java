package com.ldaniels528.woozle;

import java.awt.*;

import static com.ldaniels528.woozle.GameDisplayPane.BOARD_HEIGHT;

/**
 * WooZle Camera
 * @author lawrence.daniels@gmail.com
 */
public abstract class Camera {
	// text status lines
	protected static final int LINE1 	= BOARD_HEIGHT - 25;
	protected static final int LINE2 	= BOARD_HEIGHT - 5;
	
	// internal fields
	protected final GameDisplayPane displayPane;
	protected Graphics2D offScreen;
	protected InGameMessage message;
	
	/**
	 * Creates a new camera instance
	 */
	public Camera( final GameDisplayPane displayPane ) {
		this.displayPane = displayPane;
	}
	
	/**
	 * Initializes the camera
	 */
	public void init() {
		// get the graphics context
		this.offScreen	= displayPane.getOffScreen();
	}
	
	/** 
	 * Adds the given message to the queue for display
	 * @param message the given {@link InGameMessage message}
	 */
	public void setMessage( final InGameMessage message ) {
		this.message = message;
	}
	
	/**
	 * Renders all queued messages
	 */
	protected void renderMessages() {
		if( message != null ) {
			// render the message
			message.render( offScreen );
			
			// remove the message if it's expired
			if( message.isExpired() ) {
				message = null;
			}
		}
	}
	

}
