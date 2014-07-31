package com.ldaniels528.woozle;

import com.ldaniels528.woozle.breakout.BreakOutGameManager;
import com.ldaniels528.woozle.hangman.HangManGameManager;
import com.ldaniels528.woozle.othello.OthelloGameManager;
import com.ldaniels528.woozle.scramble.ScrambleGameManager;

/**
 * WooZle Game Manager Factory
 * @author lawrence.daniels@gmail.com
 */
public class GameManagerFactory {
	private static GameManager[] gameManagers;
	private static int index;
	
	/**
	 * Private Constructor
	 */
	private GameManagerFactory() {
		super();
	}
	
	/**
	 * Initializes the game manager factory
	 * @param displayPane the given {@link GameDisplayPane display pane}
	 */
	public static void init( final GameDisplayPane displayPane ) {
		// create the game managers
		gameManagers = new GameManager[] {
			new HangManGameManager( displayPane ),
			new ScrambleGameManager( displayPane ),
			new BreakOutGameManager( displayPane ),
			new OthelloGameManager( displayPane )
		};
	}
	
	/**
	 * Retrieves a game via game index
	 * @param gameIndex the given game index
	 * @return the {@link GameManager game} 
	 */
	public static GameManager getGame( final int gameIndex ) {
		return gameManagers[ gameIndex ];
	}

	/**
	 * Retrieves the next game for playing
	 * @return the next {@link GameManager game} for playing
	 */
	public static GameManager getNextGame() {
		return gameManagers[ index++ % gameManagers.length ];
	}
	
	
	/**
	 * Shuts down all game managers
	 */
	public static void shutdownAll() {
		for( final GameManager gameManager : gameManagers ) {
			gameManager.shutdown();
		}
	}

}
