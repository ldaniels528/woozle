package com.ldaniels528.woozle;

import javax.swing.*;
import java.awt.*;

/**
 * WooZle Game Display Panel
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class GameDisplayPane extends JPanel {
	// constants
	public static final int BOARD_WIDTH 	= 1024;
	public static final int BOARD_HEIGHT 	= 768;
	
	// internal fields
	private Graphics2D offScreen;
	private Graphics2D theScreen;
	private Image buffer;
	private int width; 
	private int height;
	
	/**
	 * Default Constructor
	 */
	public GameDisplayPane() {
		super( true );
		super.setPreferredSize( new Dimension( BOARD_WIDTH, BOARD_HEIGHT ) );
	}
	
	/**
	 * Initializes the display pane
	 */
	public void init() {
		// get the width and height of the pane
		this.width		= BOARD_WIDTH; // super.getWidth();
		this.height		= BOARD_HEIGHT; // super.getHeight();
		
		// create the off-screen image buffer
		this.buffer		= super.createImage( width, height );
		
		// get the graphics contexts
		this.offScreen	= (Graphics2D)buffer.getGraphics();
		this.theScreen	= (Graphics2D)super.getGraphics();
	}
	
	/**
	 * Returns the off-Screen drawing context
	 * @return the off-Screen drawing context
	 */
	public Graphics2D getOffScreen() {
		return offScreen;
	}
	
	/**
	 * Renders the complete scene
	 */
	public void renderScene() {
		theScreen.drawImage( buffer, 0, 0, this );
	}

}
