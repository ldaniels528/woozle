package com.ldaniels528.woozle.invasion;

import static com.ldaniels528.woozle.CustomColors.INFO2_FONT;
import static java.awt.Color.WHITE;
import static java.awt.Cursor.CROSSHAIR_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.lang.String.format;

import java.awt.Image;

import com.ldaniels528.woozle.Camera;
import com.ldaniels528.woozle.ContentManager;
import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.SharedGameData;

/** 
 * Invasion Camera
 * @author lawrence.daniels@gmail.com
 */
class InvasionCamera extends Camera {
	private final SharedGameData gameData;
	private final Image backgroundImage;
	
	/** 
	 * Creates an instance of the camera
	 * @param displayPane
	 */
	public InvasionCamera( final GameDisplayPane displayPane ) {
		super( displayPane );
		this.gameData			= SharedGameData.getInstance();
		this.backgroundImage	= ContentManager.loadImage( "/images/invasion/background.jpg" );
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
	 * @param gameManager the given {@link InvasionGameManager game manager}
	 * @param board the given {@link InvasionVirtualWorld virtual world}
	 */
	public void renderScene( final InvasionGameManager gameManager, final InvasionVirtualWorld world ) {
		// get the level and score
		final int level = gameData.getLevel();
		final int score = gameData.getScore();
		
		// draw the background
		offScreen.drawImage( backgroundImage, 0, 0, displayPane );
		
		// draw the score
		renderGameInfo( level, score );
		
		// render the entities
		final Entity[] entities = world.getEntities();
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
	 */
	private void renderGameInfo( final int level, 
							   	 final int score ) {
		// draw the score 
		offScreen.setColor( WHITE );
		offScreen.setFont( INFO2_FONT );
		offScreen.drawString( format( "Score %05d", score ), 0, 20 );
	}

}
