package com.ldaniels528.woozle.othello;

import static java.lang.String.format;
import static com.ldaniels528.woozle.CustomColors.INFO2_FONT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_HEIGHT;
import static com.ldaniels528.woozle.GameDisplayPane.BOARD_WIDTH;
import static com.ldaniels528.woozle.othello.OthelloBoard.COLUMNS;
import static com.ldaniels528.woozle.othello.OthelloBoard.ROWS;
import static java.awt.Color.*;
import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;

import com.ldaniels528.woozle.Camera;
import com.ldaniels528.woozle.GameDisplayPane;
import com.ldaniels528.woozle.SharedGameData;
import com.ldaniels528.woozle.hangman.HangManGameManager;
import com.ldaniels528.woozle.othello.OthelloBoard.GridStatistics;

/**
 * Othello Camera
 * @author lawrence.daniels@gmail.com
 */
class OthelloCamera extends Camera {
	public  static final int CELL_WIDTH 	= 28;
	public  static final int CELL_HEIGHT 	= 28;
	public  static final int X_OFFSET		= ( BOARD_WIDTH - BOARD_HEIGHT ) / 2;
	public  static final int Y_OFFSET		= 0;
	private static final int PIECE_WIDTH 	= CELL_WIDTH - 6;
	private static final int PIECE_HEIGHT 	= CELL_HEIGHT - 6;
	private static final int GRID_WIDTH		= CELL_WIDTH * COLUMNS;
	private static final int GRID_HEIGHT	= CELL_HEIGHT * ROWS;
	
	// internal fields
	private final SharedGameData gameData;
	
	/**
	 * Creates a new camera instance
	 */
	public OthelloCamera( final GameDisplayPane displayPane ) {
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
	public void renderScene( final OthelloGameManager gameManager, final OthelloBoard board ) {
		// draw the background
		offScreen.drawImage( gameData.getStageImage(), 0, 0, displayPane );
		
		// draw the cells
		renderGameBoard(board);
		
		// draw the game pieces
		renderGamePieces( board );
		
		// draw the score
		renderGameInfo( board.getStatistics() );
		
		// display the messages
		renderMessages();
		
		// render the complete scene
		displayPane.renderScene();
	}
	
	/**
	 * Renders the pieces onto the grid
	 */
	private void renderGamePieces( final OthelloBoard board ) {
		// get the grid
		final OthelloPiece[][] grid = board.getGrid();
		
		// draw the pieces
		for( int col = 0; col < COLUMNS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				// cache the piece
				final OthelloPiece piece = grid[col][row];
				if( piece != null ) {
					// compute the (x,y) coordinates
					final int x = col * CELL_WIDTH + X_OFFSET + ( CELL_WIDTH - PIECE_WIDTH ) / 2;
					final int y = row * CELL_HEIGHT + Y_OFFSET + ( CELL_HEIGHT - PIECE_HEIGHT ) / 2;
					
					// draw the piece
					switch( grid[col][row] ) {
						case YING:
							renderYing( x, y );
							break;
							
						case YANG:
							renderYang( x, y );
							break;
					}
				}
			}
		}
	}
	
	/**
	 * Renders the Ying piece
	 * @param x the given x-coordinate
	 * @param y the given x-coordinate
	 */
	private void renderYing( final int x, final int y ) {
		offScreen.setColor( WHITE );
		offScreen.fillOval( x, y, PIECE_WIDTH, PIECE_HEIGHT );
		offScreen.setColor( BLACK );
		offScreen.drawOval( x, y, PIECE_WIDTH, PIECE_HEIGHT );
	}
	
	/**
	 * Renders the Yang piece
	 * @param x the given x-coordinate
	 * @param y the given x-coordinate
	 */
	private void renderYang( final int x, final int y ) {
		offScreen.setColor( BLACK );
		offScreen.fillOval( x, y, PIECE_WIDTH, PIECE_HEIGHT );
		offScreen.setColor( WHITE );
		offScreen.drawOval( x, y, PIECE_WIDTH, PIECE_HEIGHT );
	}
	
	/**
	 * Renders the game board
	 * @param board the given {@link OthelloBoard playing board}
	 */
	private void renderGameBoard( final OthelloBoard board ) {
		// draw the solid background
		offScreen.setColor( BLUE );
		offScreen.fillRect( X_OFFSET, Y_OFFSET, GRID_WIDTH, GRID_HEIGHT );
		
		// draw the outline of the grid
		offScreen.setColor( GRAY );
		offScreen.drawRect( X_OFFSET, Y_OFFSET, GRID_WIDTH, GRID_HEIGHT );
		
		// get the hover cell
		final OthelloCell hoverCell = board.getHoverCell();
		
		// draw the interior lines
		for( int col = 0; col < COLUMNS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				// compute the (x,y) positions
				final int x = col * CELL_WIDTH + X_OFFSET;
				final int y = row * CELL_HEIGHT + Y_OFFSET;
				
				// if the mouse is hovering over the cell, highlight it
				final boolean hovering = hoverCell.column == col && hoverCell.row == row;
				if( hovering ) {
					offScreen.setColor( CYAN );
					offScreen.fillRect( x, y, CELL_WIDTH, CELL_HEIGHT );
				}
				
				// draw the cross sections
				offScreen.setColor( GRAY );
				offScreen.drawLine( x, Y_OFFSET, x, GRID_HEIGHT );
				offScreen.drawLine( X_OFFSET, y, X_OFFSET + GRID_WIDTH, y );
			}
		}
 	}
	
	/**
	 * Renders the game information onto the graphics context
	 * @param level the current game level (stage)
	 * @param score the current player's score
	 */
	private void renderGameInfo( final GridStatistics statistics ) {
		// get the counts
		final int playerCount	= statistics.getPlayerCount();
		final int computerCount = statistics.getComputerCount();
		
		// draw the score 
		offScreen.setColor( WHITE );
		offScreen.setFont( INFO2_FONT );
		
		// draw the player's score
		renderYing( 0, 40 );
		offScreen.drawString( format( "%02d", playerCount ), 0, 80 );
		
		// draw the computer's score
		renderYang( 300, 40 );
		offScreen.drawString( format( "%02d", computerCount ), 300, 80 );
	}
	
}