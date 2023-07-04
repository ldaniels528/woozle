package com.ldaniels528.woozle.breakout;

import com.ldaniels528.woozle.Camera;
import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.SharedGameData;

import java.awt.*;

import static com.ldaniels528.woozle.CustomColors.INFO2_FONT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_HEIGHT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static java.awt.Color.WHITE;
import static java.awt.Cursor.CROSSHAIR_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.lang.String.format;

/**
 * Break-Out Camera
 * @author lawrence.daniels@gmail.com
 */
class BreakOutCamera extends Camera {
	// internal fields
	private final SharedGameData gameData;

	/**
	 * Creates a new camera instance
	 * @param displayPane the given {@link GameDisplayPane display pane}
	 */
	public BreakOutCamera( final GameDisplayPane displayPane ) {
		super( displayPane );
		this.gameData = SharedGameData.getInstance();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.Camera#init()
	 */
	public void init() {
		// allow parent to update
		super.init();
		
		// set the cursor type
		displayPane.setCursor( getPredefinedCursor( CROSSHAIR_CURSOR ) );
	}
	
	/**
	 * Renders the complete scene
	 * @param gameManager the given {@link BreakOutGameManager game manager}
	 * @param playingField the given {@link BreakOutPlayingField playing field}
	 */
	public void renderScene( final BreakOutGameManager gameManager, final BreakOutPlayingField playingField ) {
		// get the level and score
		final int level = gameData.getLevel();
		final int score = gameData.getScore();
		final int balls	= playingField.getSpareBalls();
		
		// draw the background
		offScreen.setColor(Color.BLACK);
		offScreen.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
		offScreen.drawImage( gameData.getStageImage(), 0, 0, displayPane );
		
		// draw the score
		renderGameInfo( level, score, balls );
		
		// render the entities
		final Entity[] entities = playingField.getEntities();
		for( final Entity entity : entities ) {
			entity.render( offScreen );
		}
		
		// render the messages
		renderMessages();
		
		// render the complete scene
		displayPane.renderScene();
	}
	
	/**
	 * Renders the game information onto the graphics context
	 * @param level the current game level (stage)
	 * @param score the current player's score
	 * @param balls the current number of spare balls
	 */
	private void renderGameInfo( final int level, 
							   	 final int score, 
							   	 final int balls ) {
		// draw the score 
		offScreen.setColor( WHITE );
		offScreen.setFont( INFO2_FONT );
		offScreen.drawString( format( "Score %05d", score ), 0, 20 );
		offScreen.drawString( format( "Balls %02d", balls ), 220, 20 );
	}

}
