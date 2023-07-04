package com.ldaniels528.woozle;

import java.awt.*;

import static com.ldaniels528.woozle.GameManager.STAGES;
import static java.lang.String.format;

/**
 * Represents the common data that is shared between all mini-games.
 * @author lawrence.daniels@gmail.com
 */
public class SharedGameData {
	// singleton instance
	private static final SharedGameData instance = new SharedGameData();
	
	// internal fields
	private final Image[] stageImages;
	private int score;
	private int level;
	
	/**
	 * Default Constructor
	 */
	public SharedGameData() {
		super();
		this.score		 = 0;
		this.level		 = 1;
		this.stageImages = new Image[ STAGES ];
		
		// load the stage images
		for( int n = 0; n < stageImages.length; n++ ) {
			stageImages[n] = ContentManager.loadImage( format( "/common/images/stage/stage%03d.jpg", n+1 ) );
		}
	}
	
	/** 
	 * Returns the shared game data instance
	 * @return the shared game data instance
	 */
	public static SharedGameData getInstance() {
		return instance;
	}
	
	/**
	 * Returns the background image for the current stage
	 * @return the background {@link Image image}
	 */
	public Image getStageImage() {
		return stageImages[ (level-1) % STAGES];
	}

	/**
	 * Adjusts the player's score by the given delta
	 * @param delta  the given delta
	 */
	public void adjustScore( final int delta ) {
		this.score += delta;
	}
	
	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore( final int score ) {
		this.score = score;
	}
	
	/**
	 * Levels the game up by a stage
	 */
	public int levelUp() {
		level++;
		return level;
	}
	
	/**
	 * Levels the game down by a stage
	 */
	public int levelDown() {
		if( level > 1 ) {
			level--;
		}
		return level;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel( final int level ) {
		this.level = level;
	}

}
