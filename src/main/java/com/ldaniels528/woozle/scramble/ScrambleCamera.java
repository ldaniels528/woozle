package com.ldaniels528.woozle.scramble;

import static com.ldaniels528.woozle.CustomColors.*;
import static com.ldaniels528.woozle.scramble.ScrambleBoard.COLS;
import static com.ldaniels528.woozle.scramble.ScrambleBoard.ROWS;
import static java.awt.Color.BLUE;
import static java.awt.Color.GREEN;
import static java.awt.Color.LIGHT_GRAY;
import static java.awt.Color.ORANGE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.lang.String.format;

import java.awt.Color;

import com.ldaniels528.woozle.Camera;
import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.SharedGameData;
import com.ldaniels528.woozle.StopWatch;

/**
 * Scramble Camera
 * @author lawrence.daniels@gmail.com
 */
class ScrambleCamera extends Camera {
	// board dimension constants
	public static final int CELL_WIDTH 		= 36; 
	public static final int CELL_HEIGHT 	= 35;
	
	// element dimension constants
	public static final int ARC_WIDTH		= 12;
	public static final int ARC_HEIGHT		= 12;
	public static final int ELEM_WIDTH		= 30;
	public static final int ELEM_HEIGHT		= 30;
	
	// internal fields
	private final SharedGameData gameData;
	
	/**
	 * Creates a new camera instance
	 */
	public ScrambleCamera( final GameDisplayPane displayPane ) {
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
	 * @param gameManager the given {@link ScrambleGameManager game manager}
	 * @param board the given {@link ScrambleBoard game board}
	 */
	public void renderScene( final ScrambleGameManager gameManager, final ScrambleBoard board ) {
		// get the level and score
		final StopWatch stopWatch	= gameManager.getStopWatch();
		final int level 			= gameData.getLevel();
		final int score 			= gameData.getScore();
		final int lettersLeft		= board.getLettersLeft();
		final int wordsLeft			= board.getWordsLeft();
		
		// draw the background
		offScreen.drawImage( gameData.getStageImage(), 0, 0, displayPane );
		
		// draw the game board
		renderGameBoard( board );
		
		// draw the score
		renderGameInfo( level, score, lettersLeft, wordsLeft, stopWatch );
		
		// display the messages
		renderMessages();
		
		// render the complete scene
		displayPane.renderScene();
	}
	
	/**
	 * Renders the game information onto the graphics context
	 * @param level the current game level (stage)
	 * @param score the current player's score
	 * @param lettersLeft the number of replacement letters remaining
	 * @param wordsLeft the number of words remaining
	 * @param stopWatch the {@link StopWatch stop watch}
	 */
	private void renderGameInfo( final int level, 
							   	 final int score, 
							   	 final int lettersLeft, 
							   	 final int wordsLeft, 
							   	 final StopWatch stopWatch ) {
		// draw the level detail
		offScreen.setFont( INFO1_FONT );
		offScreen.setColor( WHITE );
		offScreen.drawString( format( "Level %02d: %d words remaining", level, wordsLeft ), 0, LINE2 );
		
		// draw the score 
		offScreen.setFont( INFO2_FONT );
		offScreen.drawString( format( "Score %05d", score ), 0, LINE1 );
		
		// draw the time remaining
		offScreen.drawString( "Time", 195, LINE1 );
		final int timeLeft = stopWatch.getTimeLeft();
		offScreen.setColor( ( timeLeft < 60 ) ? RED : ( timeLeft < 120 ? YELLOW : GREEN ) );
		offScreen.drawString( stopWatch.toString(), 255, LINE1 );
	}
	
	
	/**
	 * Renders the game elements onto the given graphics context
	 * @param board the given {@link ScrambleBoard game board}
	 */
	private void renderGameBoard( final ScrambleBoard board ) {		
		LetterElement anchoredElement = null;
		
		// get the matrix
		final LetterElement[][] matrix = board.getMatrix();
		
		// draw the game elements
		for( int col = 0; col < COLS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				// compute the (x,y) coordinates of the element
				final int x = ( CELL_WIDTH * col );
				final int y = ( CELL_HEIGHT * row );
				
				// cache the element
				final LetterElement element = matrix[col][row];
				
				// draw the element
				if( element != null ) {
					// skip the anchored element
					final boolean anchored = ( element.getAnchor() != null );
					if( anchored ) {
						anchoredElement = element;
					}
					else {
						renderLetter( x, y, element, board.isSelected( element ) );
					}
				}
			}
		}
		
		// draw the anchored element
		if( anchoredElement != null ) { 
			renderLetter( 0, 0, anchoredElement, false );
		}
	}
	
	/**
	 * Renders the game element onto the graphics context
	 * @param x the given X-coordinate of the element
	 * @param y the given Y-coordinate of the element
	 * @param element the given {@link LetterElement game element}
	 * @param selected indicates whether the element is currently selected
	 */
	private void renderLetter( final int x, 
						 	   final int y, 
						 	   final LetterElement element, 
						 	   final boolean selected ) {
		// is the element anchored?
		final boolean anchored = ( element.getAnchor() != null );
		
		// determine the adjusted (x,y) coordinate
		final int px = ( anchored ? element.getAnchor().x-CELL_WIDTH/2 : x + 2 );
		final int py = ( anchored ? element.getAnchor().y-CELL_HEIGHT/2 - 9 : y + 2 );
		
		// draw the solid block
		offScreen.setColor( determineElementColor( anchored, selected ) );
		offScreen.fillRoundRect( px, py, ELEM_WIDTH, ELEM_HEIGHT, ARC_WIDTH, ARC_HEIGHT );
		
		// draw the outline
		offScreen.setColor( element.isVowel() ? RED : BLUE ); 
		offScreen.drawRoundRect( px, py, ELEM_WIDTH, ELEM_HEIGHT, ARC_WIDTH, ARC_HEIGHT );
				
		// draw the small font
		offScreen.setFont( POINT_FONT );
		offScreen.drawString( String.format( "%d", element.getValue() ), px + 3, py + ELEM_HEIGHT - 2 );
		
		// draw the letter character
		offScreen.setFont( LETTER_FONT );
		offScreen.drawString( element.toString(), px + ( CELL_WIDTH / 2 ) - 8, py + ( CELL_HEIGHT / 2 ) + 4 );
	}
	
	/**
	 * Determines the color of the game element based on whether
	 * it's selected or highlighted
	 * @param anchored indicates whether the element is anchored
	 * @param selected indicates whether the element is selected
	 * @return the appropriate {@link Color color}
	 */
	private Color determineElementColor( final boolean anchored, 
								   		 final boolean selected ) {
		if( anchored  ) {
			return ORANGE;
		}
		else if( selected ) {
			return YELLOW;
		}
		else {
			return LIGHT_GRAY;
		}
	}

}
