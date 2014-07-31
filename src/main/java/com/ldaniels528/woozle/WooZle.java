package com.ldaniels528.woozle;

import static com.ldaniels528.woozle.CustomColors.APP_FONT;
import static com.ldaniels528.woozle.CustomColors.CHOOSE_FONT;
import static com.ldaniels528.woozle.CustomColors.DARK_BLUE;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_HEIGHT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static java.awt.Color.CYAN;
import static java.awt.Color.WHITE;
import static java.lang.String.format;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JFrame;

/**
 * WooZle: Retro Mini-Game Collection
 * @author lawrence.daniels@gmail.com
 */
@SuppressWarnings("serial")
public class WooZle extends JFrame 
implements KeyListener, MouseListener, MouseMotionListener, WindowListener {
	private static final double VERSION	 = 0.48;
	private final GameDisplayPane contentPane;
	private GameManager gameChooser;
	private GameManager gameManager;
	private GameManager changedGame;
	private boolean alive;
	
	/**
	 * Default Constructor
	 */
	public WooZle() {
		super( format( "WooZle v%2.2f", VERSION ) );
		super.setDefaultCloseOperation( EXIT_ON_CLOSE );
		super.setContentPane( contentPane = new GameDisplayPane() );
		super.pack();
		super.setResizable( false );
		super.setVisible( true );
		
		// add a shutdown hook
		Runtime.getRuntime().addShutdownHook( new MyShutdownHook() );
	}
	
	/**
	 * For stand alone operation
	 * @param args the given command line arguments
	 */
	public static void main( final String[] args ) {
		WooZle game = null;
		try {
			game = new WooZle();
			game.init();
			game.execute();
		}
		catch( final Throwable cause ) {
			showMessageDialog( game, cause.getMessage(), "Initialization Error", ERROR_MESSAGE );  
			cause.printStackTrace();
		}
	}
	
	/**
	 * Executes the game
	 */
	public void execute() {		
		// loop indefinitely
		alive = true;
		while( alive ) {
			try {
				// update the game cycle
				gameManager.update();
				
				// level change?
				if( gameManager.isSwitchGame() ) {
					// advance to the next level
					final SharedGameData gameData = SharedGameData.getInstance();
					gameData.levelUp();
					
					// setup the game manager
					gameManager = GameManagerFactory.getNextGame();
					gameManager.init();
					gameManager.setSwitchGame( false );
					gameManager.changeGameState( GameState.INITIALIZING );
				}
				
				// change the game?
				if( changedGame != null ) {
					changedGame.init();
					gameManager = changedGame;
					changedGame = null;
				}
			}
			catch( final Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Initializes the game
	 * @throws IOException 
	 */
	private void init() {
		// initialize the content pane
		contentPane.init();
		
		// initialize the game manager factory
		GameManagerFactory.init( contentPane );
		
		// get the next game
		gameChooser = new GameChooser( contentPane );
		gameManager = gameChooser;
		gameManager.init();
		
		// attach listeners
		super.addKeyListener( this );
		super.addMouseListener( this );
		super.addMouseMotionListener( this );
		super.addWindowListener( this );
	}
	
	/**
	 * Restarts the game
	 */
	private void restartGame() {
		gameChooser.changeGameState( GameState.INITIALIZING );
		changedGame = gameChooser;
	}
	
	/** 
	 * Shuts down the game
	 */
	private void shutdown() {
		if( alive ) {
			alive = false;
			Logger.info( "Shutting down subsystems...\n" );
			GameManagerFactory.shutdownAll();
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed( final KeyEvent event ) {
		switch( gameManager.getGameState() ) {
			case PLAYING:
				gameManager.keyPressed( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased( final KeyEvent event ) {
		switch( gameManager.getGameState() ) {
			case PLAYING:
				gameManager.keyReleased( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped( final KeyEvent event ) {
		switch( event.getKeyChar() ) {
			// DEBUG display game state
			case '.': 
				Logger.info( "gameState = %s", gameManager.getGameState() );
				break;
				
			// DEBUG snapshot
			case '/':;
				break;
				
			// allow the game manager to handle it
			default:
				switch( gameManager.getGameState() ) {
					case GAME_OVER:
						restartGame();
						break;
						
					case PLAYING:
						gameManager.keyTyped( event );
						break;
				}
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case PLAYING:
				gameManager.mouseDragged( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case PLAYING:
				gameManager.mouseMoved( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case GAME_OVER:
				restartGame();
				break;
				
			case PLAYING:
				gameManager.mouseClicked( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case PLAYING:
				gameManager.mouseEntered( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case PLAYING:
				gameManager.mouseExited( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case GAME_OVER:
				restartGame();
				break;
				
			case PLAYING:
				gameManager.mousePressed( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased( final MouseEvent event ) {
		switch( gameManager.getGameState() ) {
			case GAME_OVER:
				restartGame();
				break;
				
			case PLAYING:
				gameManager.mouseReleased( event );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated( final WindowEvent e ) {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed( final WindowEvent e ) {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing( final WindowEvent e ) {
		shutdown();
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated( final WindowEvent e ) {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified( final WindowEvent e ) {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified( final WindowEvent e ) {
		// do nothing
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened( final WindowEvent e ) {
		// do nothing
	}
	
	/**
	 * WooZle Game Chooser
	 * @author lawrence.daniels@gmail.com
	 */
	private class GameChooser extends GameManager {
		private final GameDisplayPane displayPane;
		private final Rectangle[] clickAreas;
		private final Image[] images;
		
		/**
		 * Creates a new game chooser instance
		 * @param woozle the given {@link WooZle game client}
		 * @param displayPane the given {@link GameDisplayPane display pane}
		 */
		public GameChooser( final GameDisplayPane displayPane ) {
			this.displayPane	= displayPane;
			
			// create the images
			this.images = new Image[] {
				ContentManager.loadImage( "/common/images/title/HangMan.png" ),
				ContentManager.loadImage( "/common/images/title/Scramble.png" ),
				ContentManager.loadImage( "/common/images/title/BreakOut.png" ),
				ContentManager.loadImage( "/common/images/title/Reversi.png" )
			};
			
			// get the width & heights
			final int width = images[0].getWidth( displayPane );
			final int height = images[0].getHeight( displayPane );
			
			// create the click areas
			this.clickAreas = new Rectangle[] {
					new Rectangle(  15,   0, width, height ),
					new Rectangle( 163,   0, width, height ),
					new Rectangle(  15, 113, width, height ),
					new Rectangle( 163, 113, width, height )
			};
		}

		/* 
		 * (non-Javadoc)
		 * @see com.ldaniels528.woozle.GameManager#init()
		 */
		@Override
		public void init() {
			changeGameState( GameState.INITIALIZING );
		}

		/* 
		 * (non-Javadoc)
		 * @see com.ldaniels528.woozle.GameManager#update()
		 */
		@Override
		public void update() {
			// handle the game state
			switch( gameState ) {
				case INITIALIZING:
					handleGameInitializing();
					break;
					
				case PLAYING:
					handleGamePlaying();
					break;
			}
		}
		
		/* 
		 * (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked( final MouseEvent event ) {
			// get the mouse point
			final Point p = event.getPoint();
			
			// check to see whether the click was
			for( int n = 0; n < clickAreas.length; n++ ) {
				final Rectangle clickArea = clickAreas[n];
				if( clickArea.contains( p ) ) {
					changedGame = GameManagerFactory.getGame( n );
					return;
				}
			}
		}
		
		/**
		 * Renders the complete scene
		 */
		private void renderScene() {
			// get the graphics context
			final Graphics2D g = displayPane.getOffScreen();
			
			// paint the background blue
			g.setColor( DARK_BLUE );
			g.fillRect( 0, 0, BOARD_WIDTH, BOARD_HEIGHT );
			
			// draw the images
			int n = 0;
			for( final Image image : images ) {
				final Rectangle r = clickAreas[n++];
				g.drawImage( image, r.x, r.y, displayPane );
			}
			
			// add the 'Choose game' text
			g.setFont( CHOOSE_FONT );
			final Color[] CHOOSE_COLORS = { DARK_BLUE, CYAN };
			for( int i = 0; i < 2; i++ ) {
				g.setColor( CHOOSE_COLORS[i] );
				g.drawString( "Choose a game", 50+i, 126+i );
			}
		
			// add test for each application
			g.setFont( APP_FONT );
			final Color[] APP_COLORS = { DARK_BLUE, WHITE };
			for( int i = 0; i < 2; i++ ) {
				g.setColor( APP_COLORS[i] );
				g.drawString( "Hang 'Em",  30+i,  50+i );
				g.drawString( "Scramble", 177+i,  50+i );
				g.drawString( "BreakOut",  30+i, 162+i );
				g.drawString( "Reversi", 177+i, 162+i );
			}
			
			// render the entire scene
			displayPane.renderScene();
		}

		/**
		 * Handles the "Initializing" game state
		 */
		private void handleGameInitializing() {
			if( System.currentTimeMillis() - gameStateChangeTime >= 500 ) {
				changeGameState( GameState.PLAYING );
			}
		}
			
		/**
		 * Handles the "Playing" game state
		 */
		private void handleGamePlaying() {
			renderScene();
		}
		
	}
	
	/**
	 * WooZle Shutdown Hook
	 * @author lawrence.daniels@gmail.com
	 */
	private class MyShutdownHook extends Thread {
		
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			shutdown();
		}
		
	}

}
