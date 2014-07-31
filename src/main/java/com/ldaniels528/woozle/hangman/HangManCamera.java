package com.ldaniels528.woozle.hangman;

import static com.ldaniels528.woozle.CustomColors.INFO2_FONT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static java.awt.Color.*;
import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.lang.String.format;

import java.awt.Font;

import com.ldaniels528.woozle.Camera;
import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.SharedGameData;
	
/**
 * HangMan High Camera
 * @author lawrence.daniels@gmail.com
 */
class HangManCamera extends Camera {
	private static final Font LETTER_FONT 	= new Font( "Courier", Font.BOLD, 25 );
	private static final int CELL_WIDTH 	= 22;
	private static final int WORD_LINE 		= 170;
	private static final int SPACING  		= CELL_WIDTH + 3;
	
	// internal fields
	private final SharedGameData gameData;
	
	/**
	 * Creates a new camera instance
	 */
	public HangManCamera( final GameDisplayPane displayPane ) {
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
		displayPane.setCursor( getPredefinedCursor( HAND_CURSOR ) );
	}

	/**
	 * Renders the complete scene
	 * @param gameManager the given {@link HangManGameManager game manager}
	 * @param board the given {@link HangManBoard game board}
	 */
	public void renderScene( final HangManGameManager gameManager, final HangManBoard board ) {
		// get the level and score
		final int level = gameData.getLevel();
		final int score = gameData.getScore();
		
		// draw the background
		offScreen.drawImage( gameData.getStageImage(), 0, 0, displayPane );
		
		// draw the remaining letters
		renderAvailableLetters( board );
		
		// draw the word progress
		if( gameManager.getGameState() == GameState.GAME_OVER ) {
			renderWordProgressGameOver( board );
		}
		else {
			renderWordProgress( board );
		}
		
		// draw the hanging man
		final HangingMan hangingMan = board.getHangingMan();
		hangingMan.render( offScreen );
		
		// draw the score
		renderGameInfo( level, score );
		
		// display the messages
		renderMessages();
		
		// render the complete scene
		displayPane.renderScene();
	}
	
	
	
	/**
	 * Draws the lines representing the template for the current word
	 */
	private void renderWordProgress( final HangManBoard board ) {
		// get the current word
		final Character[] letters = board.getWordLetters();
		
		// compute the offset of the word template
		final int x = ( BOARD_WIDTH - ( letters.length * SPACING ) ) / 2;
		
		// draw the letters & lines
		offScreen.setFont( LETTER_FONT );
		for( int n = 0; n < letters.length; n++ ) {
			// draw the line
			offScreen.setColor( WHITE );
			offScreen.fillRect( x + n * SPACING, WORD_LINE, CELL_WIDTH, 5 );
			
			// draw the letter
			if( letters[n] != null ) {
				offScreen.setColor( CYAN );
				offScreen.drawString( String.valueOf( letters[n] ), x + n * SPACING + 3, WORD_LINE );
			}
		}
	}
	
	/**
	 * Draws the lines representing the template for the current word
	 */
	private void renderWordProgressGameOver( final HangManBoard board ) {
		// get the current word
		final Character[] letters = board.getWordLetters();
		
		final char[] missingLetters = board.getWord().toCharArray();
		
		// compute the offset of the word template
		final int x = ( BOARD_WIDTH - ( letters.length * SPACING ) ) / 2;
		
		// draw the letters & lines
		offScreen.setFont( LETTER_FONT );
		for( int n = 0; n < letters.length; n++ ) {
			// draw the line
			offScreen.setColor( WHITE );
			offScreen.fillRect( x + n * SPACING, WORD_LINE, CELL_WIDTH, 5 );
			
			// draw the letter
			final boolean missing = ( letters[n] == null );
			final char letter = missing ?  missingLetters[n] : letters[n];
			offScreen.setColor( missing ? YELLOW : CYAN );
			offScreen.drawString( String.valueOf( letter ), x + n * SPACING + 3, WORD_LINE );
		}
	}
	
	/**
	 * Renders the available (unused) letters
	 * @param board the given {@link HangManBoard playing board}
	 */
	private void renderAvailableLetters( final HangManBoard board ) {
		// get the available letters
		final Character[] availableLetters = board.getAvailableLetters();

		// set the color & font
		offScreen.setFont( LETTER_FONT );
		
		// Letters: A through L
		for( int n = 0; n < 13; n++ ) {
			// compute the X-axis position
			final int x = 25 * n + 3;
			
			// display the letter in Row #1: A through L
			final Character letterA = availableLetters[n];
			if( letterA != null ) {
				offScreen.setColor( BLUE );
				offScreen.fillRect( x, LINE1 - 16, 16, 17 );
				offScreen.setColor( LIGHT_GRAY );
				offScreen.drawString( String.valueOf( letterA ), x, LINE1 );
			}
			
			// display the letter in Row #2: M through Z
			final Character letterB = availableLetters[n+13];
			if( letterB != null ) {
				offScreen.setColor( BLUE );
				offScreen.fillRect( x, LINE2 - 16, 16, 17 );
				offScreen.setColor( LIGHT_GRAY );
				offScreen.drawString( String.valueOf( letterB ), x, LINE2 );
			}
		}
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
		offScreen.drawString( format( "Score %05d", score ), 180, 20 );
	}
	
}
