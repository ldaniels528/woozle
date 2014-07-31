package test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Chris, this graphics demo will get you started
 * on your way to creating graphics in Java.
 * 
 * You'll notice I've extended JFrame; meaning the GraphicsDemo
 * class is a subclass of JFrame.  JFrame is a stand alone window
 * implement in Java, and was designed for both graphics and text 
 * components like buttons, check boxes, and sliders.
 * @author lawrence.daniels@gmail.com
 */
public class GraphicsDemo extends JFrame {
	private final ContentPane contentPane;
	private boolean alive;
	
	/**
	 * This is the class' constructor, it's the
	 * first thing that gets executed when the 
	 * class is instantiated (loaded and run)
	 */
	public GraphicsDemo() {
		super( "First Graphics Demo" ); // this is the window's title
		
		// create the content pane for displaying the graphics
		contentPane = new ContentPane();
		
		setContentPane( contentPane );	// I'm telling the window to use my own custom content pane
		setResizable( false );			// I'm asking the super class to make the window non-resizable
		pack();							// I'm saying make the window as compact as possible
		setVisible( true );				// I'm asking the super class to make the window visible
		
		// initialize the content pane
		contentPane.initialize();
	}
	
	/**
	 * This method is called when the program is started
	 * @param args the command line arguments
	 */
	public static void main( final String[] args ) {
		GraphicsDemo demo = new GraphicsDemo();
		demo.execute();
	}
	
	/** 
	 * Starts the demo 
	 */
	public void execute() {
		// the process is now live
		alive = true;
		
		// get the graphics info
		Graphics2D offScreen = contentPane.getOffScreen();
		Graphics2D theScreen = contentPane.getTheScreen();
		Image buffer = contentPane.getBuffer();
		
		// let's create a "red" ball
		Ball ball = new Ball( 200, 200, 20, 20, Color.RED ); 
		
		// loop until alive is false
		while( alive ) {
			// if you want the ball to move, uncomment the lines below.
			//ball.x += 1;
			
			// let's clear the off-screen buffer by painting
			// it black.
			offScreen.setColor( Color.BLACK );
			offScreen.fillRect( 0, 0, contentPane.getWidth(), contentPane.getHeight() );
			
			// paint the ball onto the off-screen buffer
			ball.paintYourself( offScreen );
			
			// let's paint a green square too
			offScreen.setColor( Color.GREEN );
			offScreen.fillRect( 50, 50, 75, 75 );
			
			// paint it all to the screen
			theScreen.drawImage( buffer, 0, 0, this );
		}
	}
	
	/**
	 * Represents a solid-colored ball
	 * @author lawrence.daniels@gmail.com
	 */
	private class Ball {
		private int x,y;
		private int width;
		private int height;
		private Color color;
		
		/** 
		 * Creates a new ball instance
		 * @param x the x-coordinate for the ball
		 * @param y the y-coordinate for the ball
		 * @param width the width of the ball
		 * @param height the height of the ball
		 * @param color the color of the ball
		 */
		public Ball( int x, int y, int width, int height, Color color ) {
			this.x		= x;
			this.y		= y;
			this.width 	= width; 
			this.height = height; 
			this.color	= color;
		}
		
		/**
		 * This method allows the ball to paint itself onto the 
		 * given graphics context.
		 * @param g the given graphics context
		 */
		public void paintYourself( Graphics2D g ) {
			g.setColor( color );
			g.fillOval( x, y, width, height );
		}
	}
	
	/** 
	 * This inner class is the definition of the 
	 * "content pane" for the window.  It is what
	 * we'll use to paint graphics onto the screen.
	 * @author lawrence.daniels@gmail.com
	 */
	private class ContentPane extends JPanel {
		private Graphics2D offScreen;
		private Graphics2D theScreen;
		private Image buffer;
		
		/**
		 * Creates a new content pane
		 */
		public ContentPane() {
			super( true );  // here "true" means use double buffering... don't worry about this for now
			super.setPreferredSize( new Dimension( 600, 600 ) ); // this says I want to window to be at least 600x600
		}
		
		/**
		 * Initializes all of the members within the content pane
		 */
		public void initialize() {
			// get the width and height of the window
			final int width = getWidth(); // the width of the panel as defined in JPanel
			final int height = getHeight(); // the height of the panel as defined in JPanel
			
			// setting up the off-screen buffer
			buffer 		= createImage( width, height );
			offScreen	= (Graphics2D)buffer.getGraphics();
			theScreen	= (Graphics2D)super.getGraphics();
		}
		
		/**
		 * Returns the off-screen graphics context.  This context actually
		 * points to an image where the graphics data is stored until it
		 * was ready to be painted to the screen.
		 * @return the off-screen graphics context
		 */
		public Graphics2D getOffScreen() {
			return offScreen;
		}
		
		/**
		 * Returns the on-screen graphics context.  This context actually
		 * points to a panel, which has the ability to display images or
		 * components (e.g. buttons)
		 * @return the on-screen graphics context.
		 */
		public Graphics2D getTheScreen() {
			return theScreen;
		}
		
		/**
		 * Returns the image that is being used as a buffer for displaying
		 * a graphical scene.
		 * @return the image
		 */
		public Image getBuffer() {
			return buffer;
		}
		
	}

}
