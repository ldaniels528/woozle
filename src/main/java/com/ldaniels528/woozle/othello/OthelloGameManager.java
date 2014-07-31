package com.ldaniels528.woozle.othello;

import static java.awt.Color.RED;
import static java.awt.Color.YELLOW;

import java.awt.event.MouseEvent;

import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.GameManager;
import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.InGameMessage;

/**
 * Othello Game Manager
 * @author lawrence.daniels@gmail.com
 */
public class OthelloGameManager extends GameManager {
	private final OthelloBoard board;
	private final OthelloCamera camera;
	
	/**
	 * Creates a new game manager instance
	 * @param displayPane the given {@link GameDisplayPane display pane}
	 */
	public OthelloGameManager( final GameDisplayPane displayPane ) {
		this.board		= new OthelloBoard( this );
		this.camera		= new OthelloCamera( displayPane );
		this.gameState	= GameState.INITIALIZING;
		
		// load the audio samples
		soundManager.loadAudioSample( "/othello/audio/gameOver.wav", 		OthelloSoundKeys.GAME_OVER );
		soundManager.loadAudioSample( "/othello/audio/getReady.wav", 		OthelloSoundKeys.GET_READY );
		soundManager.loadAudioSample( "/othello/audio/levelChange.wav", 	OthelloSoundKeys.LEVEL_CHANGE );
		soundManager.loadAudioSample( "/othello/audio/pieceMoved.wav",		OthelloSoundKeys.PIECE_MOVED );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#init()
	 */
	public void init() {
		// initialize the camera
		camera.init();
		
		// setup the level information for play
		board.setup();
		
		// render the scene
		camera.renderScene( this, board );
		
		// set the initial game state
		changeGameState( GameState.INITIALIZING );
	}

	/**
	 * Changes the current game state
	 * @param state the given {@link GameState game state}
	 */
	public void changeGameState( final GameState state ) {
		super.changeGameState( state );
		
		// perform game state change specific logic
		switch( state ) {
			case INITIALIZING:
				board.setup();
				break;
				
			case STARTING:
				soundManager.play( OthelloSoundKeys.GET_READY );
				camera.setMessage( new InGameMessage( "Get Ready!", YELLOW, 2000 ) );
				break;
				
			case LEVEL_CHANGE:
				camera.setMessage( new InGameMessage( "Great Job!", YELLOW, 2000 ) );
				break;
				
			case OUT_OF_MOVES:
				camera.setMessage( new InGameMessage( "Out Of Moves", YELLOW, 1000 ) );
				break;
				
			case GAME_OVER:
				soundManager.play( OthelloSoundKeys.GAME_OVER );
				camera.setMessage( new InGameMessage( "Game Over", RED, Integer.MAX_VALUE ) );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#shutdown()
	 */
	public void shutdown() {
		super.shutdown();
		// TODO shutdown stuff here
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#update()
	 */
	public void update() {
		// render the scene
		camera.renderScene( this, board );
		
		// handle the game state
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
				
			case ARTIFICIAL_INTELLIGENCE:
				handleComputerPlaying();
				break;
				
			case LEVEL_CHANGE:
				handleLevelChange();
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
	 * Handles the "Artificial Intelligence" game state
	 */
	private void handleComputerPlaying() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 1000 ) {
			// allow the AI to take his turn
			board.handleCpuGamePlay();
			
			// switch the control back to the player
			if( gameState == GameState.ARTIFICIAL_INTELLIGENCE ) {
				changeGameState( GameState.PLAYING );
			}
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
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked( final MouseEvent event ) {
		board.handleMouseClicked( event.getPoint() );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved( final MouseEvent event ) {
		board.handleMouseMoved( event.getPoint() ); 
	}

}
