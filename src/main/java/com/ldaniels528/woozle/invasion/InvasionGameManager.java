package com.ldaniels528.woozle.invasion;

import com.ldaniels528.woozle.*;

import java.awt.event.MouseEvent;

import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

/**
 * Invasion Game Manager
 * @author lawrence.daniels@gmail.com
 */
public class InvasionGameManager extends GameManager {
	private final InvasionVirtualWorld world;
	private final InvasionCamera camera;
	
	/**
	 * Creates a new game manager instance
	 * @param displayPane the given {@link GameDisplayPane display pane}
	 */
	public InvasionGameManager( final GameDisplayPane displayPane ) {
		this.world	= new InvasionVirtualWorld( this );
		this.camera	= new InvasionCamera( displayPane );
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
				break;
				
			case STARTING:
				soundManager.play( InvasionSoundKeys.GET_READY );
				camera.setMessage( new InGameMessage( "Get Ready!", WHITE, 2000 ) );
				world.setupLevel();
				break;
				
			case LEVEL_CHANGE:
				camera.setMessage( new InGameMessage( "Great Job!", WHITE, 2000 ) );
				SharedGameData.getInstance().levelUp();
				world.setupLevel();
				break;
				
			case LEVEL_RESET:
				soundManager.play( InvasionSoundKeys.GET_READY );
				world.resetEntities();
				camera.setMessage( new InGameMessage( "Get Ready!", WHITE, 2000 ) );
				break;
				
			case OUT_OF_BOUNDS:
				soundManager.play( InvasionSoundKeys.OUT_OF_BOUNDS );
				camera.setMessage( new InGameMessage( "You were killed", WHITE, 1000 ) );
				break;
				
			case GAME_OVER:
				soundManager.play( InvasionSoundKeys.GAME_OVER_ALIENS );
				camera.setMessage( new InGameMessage( "Game Over", RED, Integer.MAX_VALUE ) );
				break;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#init()
	 */
	@Override
	public void init() {
		// initialize the camera
		camera.init();
		
		// move to initializing state
		changeGameState( GameState.INITIALIZING );
	}

	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameManager#update()
	 */
	public void update() {
		// render the scene
		if( gameState != GameState.PLAYING ) {
			camera.renderScene( this, world );
		}
		
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
				
			case LEVEL_RESET:
				handleLevelReset();
				break;
				
			case OUT_OF_BOUNDS:
				handleBallOutOfBounds();
				break;
				
			case GAME_OVER:
				handleGameOver();
				break;
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked( final MouseEvent event ) {
		world.playerFire();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved( final MouseEvent event ) {
		world.movePlayer( event.getX(), event.getY() );
	}
	
	/**
	 * Handles the "Initializing" game state
	 */
	private void handleGameInitializing() {	
		world.setSpareShips( 3 );
		changeGameState( GameState.STARTING );
	}
	
	/**
	 * Handles the 'Starting' game state
	 */
	private void handleGameStarting() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 2000 ) {
			changeGameState( GameState.PLAYING );
		}
	}

	/**
	 * Handles the "Playing" game state
	 */
	private void handleGamePlaying() {
		// capture the start time
		final long startTime = System.currentTimeMillis();
		
		// render the scene
		camera.renderScene( this, world );
		
		// capture the rendering time
		final long elapsedTime = System.currentTimeMillis() - startTime;
		final double ct = (double)elapsedTime / 33.33d;
		//System.err.printf( "elapsedTime = %d, ct = %2.1f\n", elapsedTime, ct );
		
		// update the playing field
		world.update( ct );
	}
	
	/**
	 * Handles the 'Level Change' game state
	 */
	private void handleLevelChange() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 2000 ) {
			changeGameState( GameState.STARTING );
		}
	}
	
	/**
	 * Handles the 'Level Reset' game state
	 */
	private void handleLevelReset() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 2000 ) {
			changeGameState( GameState.PLAYING );
		}
	}
	
	/**
	 * Handles the 'Out Of Bounds' game state
	 */
	private void handleBallOutOfBounds() {
		if( System.currentTimeMillis() - gameStateChangeTime >= 1000 ) {
			changeGameState( GameState.LEVEL_RESET );
		}
	}
	
	/**
	 * Handles the 'Game Over' game state
	 */
	private void handleGameOver() {
		// do nothing
	}

}
