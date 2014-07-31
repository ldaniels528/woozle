package com.ldaniels528.woozle.hangman;

import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.GameManager;
import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.InGameMessage;
import com.ldaniels528.woozle.SharedGameData;

/**
 * HangMan Game Manager
 * @author lawrence.daniels@gmail.com
 */
public class HangManGameManager extends GameManager {
	private final HangManBoard board;
	private final HangManCamera camera;
	
	/**
	 * Creates a new game manager instance
	 * @param displayPane the given {@link GameDisplayPane display pane}
	 */
	public HangManGameManager( final GameDisplayPane displayPane ) {
		this.board		= new HangManBoard( this );
		this.camera		= new HangManCamera( displayPane );
		this.gameState	= GameState.INITIALIZING;
		
		// load the audio samples
		soundManager.loadAudioSample( 	"/hangman/audio/danger.wav", 		HangManSoundKeys.DANGER );
		soundManager.loadAudioSample( 	"/hangman/audio/gameOver.wav", 		HangManSoundKeys.GAME_OVER );
		soundManager.loadAudioSample( 	"/hangman/audio/getReady.wav", 		HangManSoundKeys.GET_READY );
		soundManager.loadAudioSample( 	"/hangman/audio/levelChange.wav", 	HangManSoundKeys.LEVEL_CHANGE );
		soundManager.loadAudioSample( 	"/hangman/audio/swapLetters.wav",	HangManSoundKeys.SWAP_LETTERS );
		soundManager.loadAudioSample( 	"/hangman/audio/wordFound.wav", 	HangManSoundKeys.WORD_FOUND );
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
				camera.setMessage( new InGameMessage( "Get Ready!", WHITE, 2000 ) );
				soundManager.play( HangManSoundKeys.GET_READY );
				break;
				
			case LEVEL_CHANGE:
				camera.setMessage( getSurvivingMessage() );
				break;
				
			case OUT_OF_MOVES:
				camera.setMessage( new InGameMessage( "Out Of Moves", RED, 2000 ) );
				break;
				
			case GAME_OVER:
				camera.setMessage( new InGameMessage( "Game Over", RED, Integer.MAX_VALUE ) );
				soundManager.play( HangManSoundKeys.GAME_OVER );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped( final KeyEvent event ) {
		// get the key character
		char keyChar = event.getKeyChar();
		
		// if it's 'A'-'Z' ...
		if( ( keyChar >= 'A' && keyChar <= 'Z' )  ) {
			// pick the letter
			board.pickLetter( keyChar );
		}
		
		// if it's 'a'-'z' ...
		else if( ( keyChar >= 'a' && keyChar <= 'z' ) ) {
			// capitalize the character
			keyChar &= 0xDF;
			
			// pick the letter
			board.pickLetter( keyChar  );
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked( final MouseEvent event ) {
		board.handleElementClicked( event.getPoint() );
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
	 * Returns the surviving message
	 * @return the surviving message
	 */
	private InGameMessage getSurvivingMessage() {
		final HangingMan hangingMan = board.getHangingMan();
		final int misses = hangingMan.getMisses();
		final String message;
		final Color color;
		
		// if no misses ...
		if( misses == 0 ) {
			message = "Unbelievable!";
			color = GREEN;
		}
		
		// if up to 2 misses ...
		else if( misses <= 2 ) {
			message = "Fantastic!";
			color = CYAN;
		}
		
		// if up to 3 misses ...
		else if( misses <= 3 ) {
			message = "Great Job!";
			color = CYAN;
		}
		
		// if up to 6 misses ...
		else if( misses <= 6 ) {
			message = "Good Job!";
			color = WHITE;
		}
		
		// if up to 9 misses ...
		else if( misses <= 9 ) {
			message = "Nice Work.";
			color = WHITE;
		}
		
		// otherwise ...
		else {
			message = "Close Call!";
			color = YELLOW;
		}
		
		return new InGameMessage( message, color, 2000 );
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
			SharedGameData.getInstance().levelUp();
			changeGameState( GameState.INITIALIZING );
		}
	}
	
	/**
	 * Handles the 'Out Of Moves' game state
	 */
	private void handleOutOfMoves() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 2000 ) {
			changeGameState( GameState.GAME_OVER );
		}
	}
	
	/**
	 * Handles the 'Game Over' game state
	 */
	private void handleGameOver() {
		// do nothing
	}

}
