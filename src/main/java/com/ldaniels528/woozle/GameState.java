package com.ldaniels528.woozle;

/**
 * Represents an enumeration of possible game states
 * @author lawrence.daniels@gmail.com
 */
public enum GameState {
	
	/**
	 * The game is initialize all sub-systems
	 */
	INITIALIZING,
	
	/**
	 * The game is either starting up for the first time
	 * or changing levels
	 */
	STARTING,
	
	/**
	 * The game control has been handed to the user
	 */
	PLAYING,
	
	/**
	 * The computer is executing his move.
	 */
	ARTIFICIAL_INTELLIGENCE,
	
	/**
	 * The player leveled up
	 */
	LEVEL_CHANGE,
	
	/**
	 * Restarts to continue playing
	 */
	LEVEL_RESET,
	
	/**
	 * Time out
	 */
	TIME_OUT,
	
	/**
	 * Indicates that the ball is out of bounds
	 */
	OUT_OF_BOUNDS,
	
	/**
	 * Indicates that the player has run out of moves
	 */
	OUT_OF_MOVES,
	
	/**
	 * Game Over
	 */
	GAME_OVER
	
}
