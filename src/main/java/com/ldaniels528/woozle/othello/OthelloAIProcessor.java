package com.ldaniels528.woozle.othello;

import com.ldaniels528.woozle.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import static com.ldaniels528.woozle.othello.OthelloBoard.COLUMNS;
import static com.ldaniels528.woozle.othello.OthelloBoard.ROWS;

/**
 * Othello Artificial Intelligence Processor
 * @author lawrence.daniels@gmail.com
 */
class OthelloAIProcessor {
	private static final CapturePathComparator PATH_COMPARATOR = new CapturePathComparator();
	private final OthelloBoard board;
	private final OthelloPiece[][] grid;
	private final OthelloPiece pieceAI;
	private final OthelloPiece pieceHU;
	
	/**
	 * Creates a new instance of the Artificial Processor
	 * @param board the given {@link OthelloBoard playing board}
	 * @param pieceAI the given AI's {@link OthelloPiece piece}
	 * @param pieceHU the given human player's {@link OthelloPiece piece}
	 */
	public OthelloAIProcessor( final OthelloBoard board, 
							   final OthelloPiece pieceAI, 
							   final OthelloPiece pieceHU ) {
		this.board		= board;
		this.grid 		= board.getGrid();
		this.pieceAI	= pieceAI;
		this.pieceHU	= pieceHU;
	}
	
	/**
	 * Allows the processor to execute the next move
	 */
	public void execute() {
		Logger.info( "AI: Taking my turn...\n" );
		
		// get the AI owned cells
		final LinkedList<OthelloCell> cells = getOccupiedCells();
		Logger.info( "AI cells = %s\n", cells );
		
		// get the capture paths for the cells
		final LinkedList<OthelloCellCapturePath> paths = getCapturePaths( cells );
		Logger.info( "AI paths = %s\n", paths );
		
		// get the optimal path
		if( !paths.isEmpty() ) {
			// sort in ascending order
			Collections.sort( paths, PATH_COMPARATOR );
			
			boolean done = false;
			while( !done && !paths.isEmpty() ) {
				// grab the last path
				final OthelloCellCapturePath path = paths.removeLast();
				Logger.info( "AI path = %s\n", path );
				
				// execute the action
				Logger.info( "AI placement of %s at {%d,%d} \n", pieceAI, path.getColumnEnd(), path.getRowEnd() );
				Collection<OthelloCellCapturePath> actions = 
					board.placePiece( path.getColumnEnd(), path.getRowEnd(), pieceAI );
				done = !actions.isEmpty();
				if( !done ) {
					actions = board.placePiece( path.getColumnStart(), path.getRowStart(), pieceAI );
					done = !actions.isEmpty();
				}
			}
		}
	}
	
	/** 
	 * Returns the collection of A.I. occupied cells
	 * @return the {@link Collection collection} of {@link OthelloCell cells}
	 */
	private LinkedList<OthelloCell> getOccupiedCells() {
		// create a container for the cells
		final LinkedList<OthelloCell> cells = new LinkedList<OthelloCell>();
		
		// capture the AI owned cells
		for( int col = 0; col < COLUMNS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				if( grid[col][row] == pieceAI ) {
					cells.add( new OthelloCell( col, row ) );
				}
			}
		}
		
		// return the cells
		return cells;
	}

	/** 
	 * Retrieves all of the Capture Paths for the given cell 
	 * @param cell the given {@link OthelloCell cell}
	 * @return the {@link OthelloCellCapturePath Capture Paths}
	 */
	private LinkedList<OthelloCellCapturePath> getCapturePath( final OthelloCell cell ) {
		// create a container for the cell capture paths
		final LinkedList<OthelloCellCapturePath> list = new LinkedList<OthelloCellCapturePath>();
		boolean ok;
		int x, y;
		
		////////////////////////////////////////////////////////////////////
		// Step 1: Scan horizontally (left to right)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn() + 1;
		y = cell.getRow();
		ok = false;
		while( ( x < COLUMNS ) && ( grid[x][y] == pieceHU ) ) { ok = true; x++; }
		
		// did we find an open cell?
		if( ok && ( x < COLUMNS ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 2: Scan horizontally (right to left)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn() - 1;
		y = cell.getRow();
		ok = false;
		while( ( x >= 0 ) && ( grid[x][y] == pieceHU ) ) { ok = true; x--; }

		// did we find an open cell?
		if( ok && ( x >= 0 ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 3: Scan vertically (low to high)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn();
		y = cell.getRow() + 1;
		ok = false;
		while( ( y < ROWS ) && ( grid[x][y] == pieceHU ) ) { ok = true; y++; }
		
		// did we find an open cell?
		if( ok && ( y < ROWS ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 4: Scan vertically (high to low)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn();
		y = cell.getRow() - 1;
		ok = false;
		while( ( y >= 0 ) && ( grid[x][y] == pieceHU ) ) { ok = true; y--; }

		// did we find an open cell?
		if( ok && ( y >= 0 ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 5: Scan diagonally (upper right)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn() + 1;
		y = cell.getRow() - 1;
		ok = false;
		while( ( x < COLUMNS ) && 
			   ( y >= 0 ) && 
			   ( grid[x][y] == pieceHU ) ) { ok = true; x++; y--; }
		
		// did we find an open cell?
		if( ok && ( x < COLUMNS ) && ( y >= 0 ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 6: Scan diagonally (upper left)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn() - 1;
		y = cell.getRow() - 1;
		ok = false;
		while( ( x >= 0 ) && 
			   ( y >= 0 ) && 
			   ( grid[x][y] == pieceHU ) ) { ok = true; x--; y--; }
		
		// did we find an open cell?
		if( ok && ( x >= 0 ) && ( y >= 0 ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 7: Scan diagonally (lower right)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn() + 1;
		y = cell.getRow() + 1;
		ok = false;
		while( ( x < COLUMNS ) && 
			   ( y < ROWS ) && 
			   ( grid[x][y] == pieceHU ) ) { ok = true; x++; y++; }
		
		// did we find an open cell?
		if( ok && ( x < COLUMNS ) && ( y < ROWS ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		////////////////////////////////////////////////////////////////////
		// Step 8: Scan diagonally (lower left)
		////////////////////////////////////////////////////////////////////
		x = cell.getColumn() - 1;
		y = cell.getRow() + 1;
		ok = false;
		while( ( x >= 0 ) && 
			   ( y < ROWS ) && 
			   ( grid[x][y] == pieceHU ) ) { ok = true; x--; y++; }
		
		// did we find an open cell?
		if( ok && ( x >= 0 ) && ( y < ROWS ) && ( grid[x][y] == null ) ) {
			list.add( new OthelloCellCapturePath( cell.getColumn(), cell.getRow(), x, y, pieceAI ) );
		}
		
		return list;
	}
	
	/** 
	 * Retrieves all of the Capture Paths for the given cells
	 * @param cells the given {@link Collection collection} of {@link OthelloCell cells}
	 * @return the {@link Collection collection} of {@link OthelloCellCapturePath Capture Paths}
	 */
	private LinkedList<OthelloCellCapturePath> getCapturePaths( final Collection<OthelloCell> cells ) {
		// create a container for the cell capture paths
		final LinkedList<OthelloCellCapturePath> list = new LinkedList<OthelloCellCapturePath>();
		
		// gather the capture paths for each cell
		for( final OthelloCell cell : cells ) {
			list.addAll( getCapturePath( cell ) );
		}
		
		return list;
	}
	
	/**
	 * Capture Path Comparator
	 * @author lawrence.daniels@gmail.com
	 */
	private static class CapturePathComparator implements Comparator<OthelloCellCapturePath> {

		/* 
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare( final OthelloCellCapturePath o1, final OthelloCellCapturePath o2 ) {
			return o1.captureCount() - o2.captureCount();
		}
		
	}
	
}
