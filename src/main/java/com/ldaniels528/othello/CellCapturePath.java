package com.ldaniels528.othello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.ldaniels528.othello.OthelloBoard.*;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Represents a Cell Capture Path
 *
 * @author lawrence.daniels@gmail.com
 */
class CellCapturePath {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OthelloPiece piece;
    private final int columnStart;
    private final int columnEnd;
    private final int rowStart;
    private final int rowEnd;

    /**
     * Creates a new Cell Capture Path instance
     *
     * @param columnA the given column start
     * @param rowA    the given row start
     * @param columnB the given column end
     * @param rowB    the given row end
     * @param piece   the given {@link OthelloPiece piece}
     */
    public CellCapturePath(final int columnA,
                           final int rowA,
                           final int columnB,
                           final int rowB,
                           final OthelloPiece piece) {
        this.columnStart = columnA;
        this.columnEnd = columnB;
        this.rowStart = rowA;
        this.rowEnd = rowB;
        this.piece = piece;
    }

    /**
     * Returns the number of cells covered by that range of this object
     *
     * @return the number of cells covered by that range of this object
     */
    public int captureCount() {
        return (int) sqrt(pow(rowEnd - rowStart, 2) + pow(columnEnd - columnStart, 2));
    }

    /**
     * Executes the capture of the cells along the path
     *
     * @param grid the given {@link OthelloPiece piece} grid
     */
    public void execute(final OthelloPiece[][] grid) {
        // determine whether the column & row increase or decrease
        final int dx = getSlope(columnStart, columnEnd);
        final int dy = getSlope(rowStart, rowEnd);

        // set the starting positions
        int x = columnStart;
        int y = rowStart;

        logger.info(String.format("CapturePath: %d->%d, dx = %d, dy = %d",
                computeCellNumber(columnStart, rowStart), computeCellNumber(columnEnd, rowEnd), dx, dy));
        logger.info(String.format("CapturePath: setting %d with %s", computeCellNumber(x, y), piece));
        grid[x][y] = piece;

        // iterate until the limit has been reached for both x and y.
        while ((x != columnEnd) || (y != rowEnd)) {
            // increment/decrement the column and row
            x += dx;
            y += dy;

            // set the cell of the grid
            if ((x >= 0) && (x < COLUMNS) && (y >= 0) && (y < ROWS)) {
                logger.info(String.format("CapturePath: setting %d with %s", computeCellNumber(x, y), piece));
                grid[x][y] = piece;
            } else {
                logger.error(String.format("Move is out of bounds - %d", computeCellNumber(x, y)));
            }
        }
    }

    /**
     * @return the columnStart
     */
    public int getColumnStart() {
        return columnStart;
    }

    /**
     * @return the columnEnd
     */
    public int getColumnEnd() {
        return columnEnd;
    }

    /**
     * @return the rowStart
     */
    public int getRowStart() {
        return rowStart;
    }

    /**
     * @return the rowEnd
     */
    public int getRowEnd() {
        return rowEnd;
    }

    /**
     * Determines the slope (rise over run) for the coordinate
     *
     * @param start the initial (start) value
     * @param end   the limit (end) value
     * @return 1 for increase, -1 for decrease, 0 for neutral
     */
    private int getSlope(final int start, final int end) {
        if (end > start) return +1;
        else if (start > end) return -1;
        else return 0;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        final int fromCell = computeCellNumber(columnStart, rowStart);
        final int toCell = computeCellNumber(columnEnd, rowEnd);
        return String.format("%d->%d:%d", fromCell, toCell, captureCount());
    }

}