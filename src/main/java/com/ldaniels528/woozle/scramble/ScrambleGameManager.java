package com.ldaniels528.woozle.scramble;

import static com.ldaniels528.woozle.scramble.ScrambleSoundKeys.*;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.GameManager;
import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.InGameMessage;
import com.ldaniels528.woozle.Logger;
import com.ldaniels528.woozle.StopWatch;

/**
 * WooZle: Scramble Game Manager
 * @author lawrence.daniels@gmail.com
 */
public class ScrambleGameManager extends GameManager { 
	private final StopWatch stopWatch;
	private final ScrambleBoard board;
	private final ScrambleCamera camera;
	private LetterElement movingElement;
	
	/**
	 * Creates a new game manager instance
	 * @param displayPane the given {@link GameDisplayPane display pane}
	 */
	public ScrambleGameManager( final GameDisplayPane displayPane ) {
		this.camera		= new ScrambleCamera( displayPane );
		this.stopWatch	= new StopWatch();
		this.board		= new ScrambleBoard( this );
		this.gameState	= GameState.INITIALIZING;
		
		// load the audio samples
		soundManager.loadAudioSampleAU( "/scramble/audio/spaceMusic.au", 	AMBIENT );
		soundManager.loadAudioSample( 	"/scramble/audio/danger.wav", 		DANGER );
		soundManager.loadAudioSample( 	"/scramble/audio/gameOver.wav", 	GAME_OVER );
		soundManager.loadAudioSample( 	"/scramble/audio/getReady.wav", 	GET_READY );
		soundManager.loadAudioSample( 	"/scramble/audio/levelChange.wav", 	LEVEL_CHANGE );
		soundManager.loadAudioSample( 	"/scramble/audio/swapLetters.wav",	SWAP_LETTERS );
		soundManager.loadAudioSample( 	"/scramble/audio/wordFound.wav", 	WORD_FOUND );
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#init()
	 */
	public void init() {
		// initialize the camera
		camera.init();
		
		// setup the level information for play
		setupLevel( level, true );
		
		// render the scene
		camera.renderScene( this, board );
		
		// set the initial game state
		changeGameState( GameState.INITIALIZING );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#shutdown()
	 */
	public void shutdown() {
		super.shutdown();
		board.shutdown();
	}
	
	/**
	 * Changes the current game state
	 * @param state the given {@link GameState game state}
	 */
	public void changeGameState( final GameState state ) {
		super.changeGameState( state );
		
		// perform game state change specific logic
		switch( state ) {
			case STARTING:
				camera.setMessage( new InGameMessage( "Get Ready!", WHITE, 2000 ) );
				soundManager.play( GET_READY );
				break;
				
			case LEVEL_CHANGE:
				camera.setMessage( new InGameMessage( "Great Job!", WHITE, 1000 ) );
				break;
				
			case TIME_OUT:
				camera.setMessage( new InGameMessage( "Time Up!", WHITE, 2000 ) );
				break;
				
			case GAME_OVER:
				camera.setMessage( new InGameMessage( "Game Over", RED, Integer.MAX_VALUE ) );
				soundManager.play( ScrambleSoundKeys.GAME_OVER );
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#update()
	 */
	public void update() {
		// render the scene
		camera.renderScene( this, board );
		
		// handle game state
		switch( gameState ) {
			case INITIALIZING:
				handleGameInitializing();
				break;
				
			case STARTING:
				handleGameStarting();
				break;
				
			case PLAYING:
				handleGamePlaying();
				break;
				
			case LEVEL_CHANGE:
				handleLevelChange();
				break;
				
			case TIME_OUT:
				handleTimeOut();
				break;
				
			case OUT_OF_MOVES:
				handleOutOfMoves();
				break;
				
			case GAME_OVER:
				handleGameOver();
				break;
		}
	}
	
	/**
	 * Queues a message for display
	 * @param message the given {@link InGameMessage message}
	 */
	public void queueMessage( InGameMessage message ) {
		camera.setMessage( message );
	}

	/** 
	 * Returns the time remaining for the current level
	 * @return the time remaining for the current level
	 */
	public StopWatch getStopWatch() {
		return stopWatch;
	}
	
	/**
	 * Sets up to the given level
	 * @param level the given level
	 * @param setupOnly indicates whether only to setup the level
	 */
	private void setupLevel( final int level, final boolean setupOnly ) {
		Logger.info( "Changing level to %d\n", level );
		board.clearSelection();
		board.populateMatrix();
		
		// sets the level information
		board.setLettersLeft( 20 / level );
		board.setWordsLeft( 5 + ( level - 1 ) * 2 );
		
		// reset the clock: start the count down
		final int timeLimit = 365 - ( ( level - 1 ) * 60 );
		stopWatch.startCountDown( ( timeLimit < 60 ) ? 60 : timeLimit ); 
		
		// change the game state
		if( !setupOnly ) {
			changeGameState( GameState.LEVEL_CHANGE );
		}
	}
	
	/**
	 * Handles the "Starting" game state
	 */
	private void handleGameInitializing() {	
		if( System.currentTimeMillis() - gameStateChangeTime >= 1000 ) {
			changeGameState( GameState.STARTING );
		}
	}
	
	/**
	 * Handles the "Starting" game state
	 */
	private void handleGameStarting() {	
		if( System.currentTimeMillis() - gameStateChangeTime >= 1000 ) {
			changeGameState( GameState.PLAYING );
		}
	}
	
	/**
	 * Handles the "Playing" game state
	 */
	private void handleGamePlaying() {
		// update the game board
		board.update();
		
		// check the stop watch
		if( stopWatch.isTimeUp() ) {
			changeGameState( GameState.TIME_OUT );
		}
		
		// if all word are cleared, go to next level
		else if( board.getWordsLeft() == 0 ) {
			changeGameState( GameState.LEVEL_CHANGE );
		}
	}
	
	/**
	 * Handles the 'Level Change' game state
	 */
	private void handleLevelChange() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 2000 ) {
			changeGameState( GameState.INITIALIZING );
		}
	}
	
	/**
	 * Handles the 'Time Out' game state
	 */
	private void handleTimeOut() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 1000 ) {
			changeGameState( GameState.GAME_OVER );
		}
	}
	
	/**
	 * Handles the 'Out Of Moves' game state
	 */
	private void handleOutOfMoves() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 1000 ) {
			changeGameState( GameState.GAME_OVER );
		}
	}
	
	/**
	 * Handles the 'Game Over' game state
	 */
	private void handleGameOver() {
		// do nothing
	}

	private void updateMovingElement( final MouseEvent event ) {
		if( movingElement != null ) {
			movingElement.setAnchor( event.getPoint() );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped( final KeyEvent event ) {
		switch( event.getKeyChar() ) {
			// level up
			case '+':;
			case '=':
				changeGameState( GameState.LEVEL_CHANGE );
				break;
				
			// forces a word to be claimed
			case 'F':;
			case 'f':
				board.forceWord();
				break;
				
			// music on/off
			case 'M':;
			case 'm':
				if( !musicOn ) {
					musicOn = true;
					playBackroundMusic( AMBIENT );
				}
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged( final MouseEvent event ) {
		// get the mouse coordinations
		final Point pt = event.getPoint();
		
		// if no moving element was chosen, get one now
		if( movingElement == null ) {
			final LetterElement element = board.getElementAt( pt.x, pt.y );
			if( element != null ) {
				movingElement = element;
				movingElement.setAnchor( pt );
			}
		}
		else {
			updateMovingElement( event );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved( final MouseEvent event ) {
		updateMovingElement( event );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked( final MouseEvent event ) {
		// get the mouse coordinations
		final int mx = event.getX();
		final int my = event.getY();
		
		// notify the board to highlight the element
		board.handleElementClicked( mx, my );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered( final MouseEvent event ) {
		updateMovingElement( event );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited( final MouseEvent event ) {
		updateMovingElement( event );
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased( final MouseEvent event ) {
		// was an element being moved?
		if( movingElement != null ) {
			// first update the position of the moving element
			updateMovingElement( event );
			
			// get the mouse coordinations
			final int mx = event.getX();
			final int my = event.getY();
			
			final LetterElement elemB = board.getElementAt( mx, my );
			if( elemB != null ) {
				board.swapSelectedElements( movingElement, elemB );
			}
			
			// reset the moving element
			movingElement.setAnchor( null );
			movingElement = null;
		}
	}
	
}
