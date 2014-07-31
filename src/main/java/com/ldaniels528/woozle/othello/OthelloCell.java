package com.ldaniels528.woozle.othello;

/**
 * Represents an Othello Cell
 * @author lawrence.daniels@gmail.com
 */
class OthelloCell {
	protected int column;
	protected int row;

	/**
	 * Creates a new cell
	 * @param column the given column of the cell
	 * @param row the given row of the cell
	 */
	public OthelloCell( final int column, final int row ) {
		this.column = column;
		this.row	= row;
	}

	/**
	 * @return the column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return String.format( "{%d,%d}", column, row );
	}
	
}
