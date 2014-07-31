package com.ldaniels528.woozle.othello;

import static com.ldaniels528.woozle.othello.OthelloCamera.CELL_HEIGHT;
import static com.ldaniels528.woozle.othello.OthelloCamera.CELL_WIDTH;
import static com.ldaniels528.woozle.othello.OthelloCamera.X_OFFSET;
import static com.ldaniels528.woozle.othello.OthelloCamera.Y_OFFSET;

import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;

import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.Logger;

/**
 * This class represents the virtual game board for Othello
 * @author lawrence.daniels@gmail.com
 */
class OthelloBoard {
	public  static final int COLUMNS	= 8;
	public  static final int ROWS		= 8;

	// internal constants
	private static final int Y_FUDGE	= 12;
	
	// internal fields
	private final OthelloGameManager gameManager;
	private final OthelloAIProcessor cpu;
	private final OthelloPiece computer;
	private final OthelloPiece[][] grid;
	private final GridStatistics statistics;
	private final OthelloPiece player;
	private final OthelloCell hoverCell;

	/**
	 * Creates an instance of the Othello playing board
	 * @param gameManager the given {@link OthelloGameManager game manager}
	 */
	public OthelloBoard( final OthelloGameManager gameManager ) {
		this.gameManager	= gameManager;
		this.player 		= OthelloPiece.YING;
		this.computer		= OthelloPiece.YANG;
		this.grid			= new OthelloPiece[COLUMNS][ROWS];
		this.statistics		= new GridStatistics();
		this.cpu			= new OthelloAIProcessor( this, computer, player );
		this.hoverCell		= new OthelloCell( -1, -1 );
	}
	
	/** 
	 * Returns the grid of pieces
	 * @return the grid of {@link OthelloPiece pieces}
	 */
	public OthelloPiece[][] getGrid() {
		return grid;
	}
	
	/** 
	 * Returns the current cell the mouse is hovering above
	 * @return the cell the mouse is hovering above
	 */
	public OthelloCell getHoverCell() {
		return hoverCell;
	}

	/** 
	 * Return the statistics about the playing board
	 * @return the {@link GridStatistics statistics}
	 */
	public GridStatistics getStatistics() {
		return statistics;
	}

	/**
	 * Setup the game board
	 */
	public void setup() {
		// clear the board
		for( int col = 0; col < COLUMNS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				grid[col][row] = null;
			}
		}
		
		// set the initial pieces
		grid[3][3] = player;
		grid[3][4] = computer;
		grid[4][3] = computer;
		grid[4][4] = player;
		
		// update the statistics
		statistics.update();
	}
	
	/**
	 * Returns the opposite of the given piece
	 * @param piece the given {@link OthelloPiece piece}
	 * @return the opposite {@link OthelloPiece piece}
	 */
	public static OthelloPiece opposite( final OthelloPiece piece ) {
		switch( piece ) {
			case YING: 	return OthelloPiece.YANG;
			case YANG: 	return OthelloPiece.YING;
			default:	return null;
		}
	}

	/** 
	 * Handles a mouse click
	 * @param point the given mouse click {@link Point point}
	 */
	public void handleMouseClicked( final Point point ) {
		// get the (x,y) coordinates
		final double x = point.getX();
		final double y = point.getY();
		
		// determine the (column,row) position
		final int column = (int)( ( x - X_OFFSET ) / CELL_WIDTH );
		final int row 	 = (int)( ( y - ( Y_OFFSET + Y_FUDGE ) ) / CELL_HEIGHT );
		Logger.info( "clicked at column %d, row %d (%.0f,%.0f)\n", column, row, x, y );
		
		// if the column and row are within bounds
		// place the piece
		if( ( column >= 0 ) && ( column < COLUMNS ) && 
			( row >= 0 ) && ( row < ROWS ) ) {
			// attempt to acquire the capture path
			final Collection<OthelloCellCapturePath> paths = placePiece( column, row, player );
			Logger.info( "player path = %s\n", paths );
			
			// if a path was found ...
			if( !paths.isEmpty() ) {
				// update the statistics
				statistics.update();
				
				// get the counts
				final int playerCount	= statistics.getPlayerCount();
				final int computerCount = statistics.getComputerCount();
				final int emptyCount	= statistics.getEmptyCount();
				
				// if no more moves... 
				if( playerCount == 0 || computerCount == 0 || emptyCount == 0 ) {
					gameManager.changeGameState( GameState.GAME_OVER );
				}
				
				// allow the AI to take a turn
				else {
					gameManager.changeGameState( GameState.ARTIFICIAL_INTELLIGENCE );
				}
			}
		}	
	}
	
	/** 
	 * Handles a mouse movement
	 * @param point the given mouse movement {@link Point point}
	 */
	public void handleMouseMoved( final Point point ) {
		// get the (x,y) coordinates
		final double x = point.getX();
		final double y = point.getY();
		
		// determine the (column,row) position
		final int column = (int)( ( x - X_OFFSET ) / CELL_WIDTH );
		final int row = (int)( ( y - Y_OFFSET - Y_FUDGE ) / CELL_HEIGHT );
		
		// set the column & row
		hoverCell.column	= column;
		hoverCell.row		= row;
	}
	
	/** 
	 * Handles the computer's turn on the board game
	 */
	public void handleCpuGamePlay() {
		// execute the A.I. code
		cpu.execute();
		
		// update the statistics
		statistics.update();
		
		// get the counts
		final int playerCount	= statistics.getPlayerCount();
		final int computerCount = statistics.getComputerCount();
		final int emptyCount	= statistics.getEmptyCount();
		
		// if no more moves... 
		if( playerCount == 0 || 
			computerCount == 0 || 
			emptyCount == 0 ) {
			gameManager.changeGameState( GameState.GAME_OVER );
		}
	}
	
	/**
	 * Places a piece onto the board
	 * @param column the given column position
	 * @param row the given row position
	 * @param piece the {@link OthelloBoard piece} to place
	 */
	protected Collection<OthelloCellCapturePath> placePiece( final int column, 
						   	 				   			   	 final int row, 
						   	 				   			   	 final OthelloPiece piece ) {
		// create a container for returning results
		final Collection<OthelloCellCapturePath> paths = new LinkedList<OthelloCellCapturePath>();
		
		// the target position must be empty
		if( grid[column][row] != null ) {
			Logger.error( "A piece already exists at {%d,%d}\n", column, row );
			return paths;
		}
		
		// get the opponent's piece
		final OthelloPiece opponent = opposite( piece );
		
		// define work variables
		boolean ok;
		int x,y;
		
		////////////////////////////////////////////////////////////////////
		// Step 1: Scan Horizontally Left to Right 
		////////////////////////////////////////////////////////////////////
		x = column+1;
		ok = false;
		while( ( x < COLUMNS ) && ( grid[x][row] == opponent ) ) { ok = true; x++; }
		if( ok && ( x < COLUMNS ) && ( grid[x][row] == piece ) ) {
			paths.add( new OthelloCellCapturePath( column, row, x, row, piece ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 2: Scan Horizontally Right to Left 
		////////////////////////////////////////////////////////////////////
		x = column-1;
		ok = false;
		while( ( x >= 0 ) && ( grid[x][row] == opponent ) ) { ok = true; x--; }
		if( ok && ( x >= 0 ) && grid[x][row] == piece ) {
			paths.add( new OthelloCellCapturePath( x, row, column, row, piece ) );
		}
			
		////////////////////////////////////////////////////////////////////
		// Step 3: Scan Vertically Low to High
		////////////////////////////////////////////////////////////////////
		y = row+1;
		ok = false;
		while( ( y < ROWS ) && ( grid[column][y] == opponent ) ) { ok = true; y++; }
		if( ok && ( y < ROWS ) && grid[column][y] == piece ) {
			paths.add( new OthelloCellCapturePath( column, row, column, y, piece ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 4: Scan Vertically High to Low
		////////////////////////////////////////////////////////////////////
		y = row-1;
		ok = false;
		while( ( y >= 0 ) && ( grid[column][y] == opponent ) ) { ok = true; y--; }
		if( ok && ( y >= 0 ) && grid[column][y] == piece ) {
			paths.add( new OthelloCellCapturePath( column, row, column, y, piece ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 5: Scan Diagonally Down Right
		////////////////////////////////////////////////////////////////////
		x = column+1;
		y = row+1;
		ok = false;
		while( ( x < COLUMNS ) && ( y < ROWS ) && ( grid[x][y] == opponent ) ) { ok = true; x++; y++; }
		if( ok && ( x < COLUMNS ) && ( y < ROWS ) && grid[x][y] == piece ) {
			paths.add( new OthelloCellCapturePath( column, row, x, y, piece ) );
		}

		////////////////////////////////////////////////////////////////////
		// Step 6: Scan Diagonally Up Right
		////////////////////////////////////////////////////////////////////
		x = column+1;
		y = row-1;
		ok = false;
		while( ( x < COLUMNS ) && ( y >= 0 ) && ( grid[x][y] == opponent ) ) { ok = true; x++; y--; }	
		if( ok && ( x < COLUMNS ) && ( y >= 0 ) && grid[x][y] == piece ) {
			paths.add( new OthelloCellCapturePath( column, row, x, y, piece ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 7: Scan Diagonally Down Left
		////////////////////////////////////////////////////////////////////
		x = column-1;
		y = row+1;
		ok = false;
		while( ( x >= 0 ) && ( y < ROWS ) && ( grid[x][y] == opponent ) ) { ok = true; x--; y++; }
		if( ok && ( x >= 0 ) && ( y < ROWS ) && grid[x][y] == piece ) {
			paths.add( new OthelloCellCapturePath( column, row, x, y, piece ) );
		}
				
		////////////////////////////////////////////////////////////////////
		// Step 8: Scan Diagonally Up Left 
		////////////////////////////////////////////////////////////////////
		x = column-1;
		y = row-1;
		ok = false;
		while( ( x >= 0 ) && ( y >= 0 ) && ( grid[x][y] == opponent ) ) { ok = true; x--; y--; }	
		if( ok && ( x >= 0 ) && ( y >= 0 ) && grid[x][y] == piece ) {
			paths.add( new OthelloCellCapturePath( column, row, x, y, piece ) );
		}
		
		// execute the actions
		for( final OthelloCellCapturePath path : paths ) {
			path.execute( grid );
		}
		
		return paths;
	}
	
	/**
	 * Grid Statistics
	 * @author lawrence.daniels@gmail.com
	 */
	class GridStatistics {
		private int playerCount;
		private int computerCount;
		private int emptyCount;
		
		/**
		 * Default Constructor
		 */
		public GridStatistics() {
			super();
		}
		
		public void update() {
			playerCount		= 0;
			computerCount	= 0;
			emptyCount		= 0;
			
			// count the elements
			for( int col = 0; col < COLUMNS; col++ ) {
				for( int row = 0; row < ROWS; row++ ) {
					final OthelloPiece piece = grid[col][row];
					if( piece == null ) {
						emptyCount++;
					}
					else {
						switch( piece ) {
							case YING: 	playerCount++; break;
							case YANG: 	computerCount++; break;	
						}
					}
				}
			}
		}

		/**
		 * @return the playerCount
		 */
		public int getPlayerCount() {
			return playerCount;
		}

		/**
		 * @return the computerCount
		 */
		public int getComputerCount() {
			return computerCount;
		}

		/**
		 * @return the emptyCount
		 */
		public int getEmptyCount() {
			return emptyCount;
		}
		
	}


}