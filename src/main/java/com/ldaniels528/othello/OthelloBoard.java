package com.ldaniels528.othello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;

import static com.ldaniels528.othello.Camera.*;
import static com.ldaniels528.othello.OthelloPiece.BLACK_PIECE;
import static com.ldaniels528.othello.OthelloPiece.WHITE_PIECE;
import static com.ldaniels528.othello.SoundKeys.PIECE_MOVED;

/**
 * This class represents the virtual game board for Othello
 *
 * @author lawrence.daniels@gmail.com
 */
class OthelloBoard {
    public static final int COLUMNS = 8;
    public static final int ROWS = 8;

    // internal constants
    private static final int Y_FUDGE = 12;

    // internal fields
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Othello gameManager;
    private final AIProcessor cpu;
    private final OthelloPiece computer;
    private final OthelloPiece[][] grid;
    private final GridStatistics statistics;
    private final OthelloPiece player;
    private final OthelloCell hoverCell;
    public int wins = 0;
    public int losses = 0;
    public int draws = 0;

    /**
     * Creates an instance of the Othello playing board
     *
     * @param gameManager the given {@link Othello game manager}
     */
    public OthelloBoard(final Othello gameManager) {
        this.gameManager = gameManager;
        this.player = WHITE_PIECE;
        this.computer = BLACK_PIECE;
        this.grid = new OthelloPiece[COLUMNS][ROWS];
        this.statistics = new GridStatistics();
        this.cpu = new AIProcessor(this, computer, player);
        this.hoverCell = new OthelloCell(-1, -1);
    }

    public static int computeCellNumber(final int col, final int row) {
        return row * ROWS + col;
    }

    /**
     * Returns the grid of pieces
     *
     * @return the grid of {@link OthelloPiece pieces}
     */
    public OthelloPiece[][] getGrid() {
        return grid;
    }

    /**
     * Returns the current cell the mouse is hovering above
     *
     * @return the cell the mouse is hovering above
     */
    public OthelloCell getHoverCell() {
        return hoverCell;
    }

    public int getRounds() {
        return wins + losses + draws;
    }

    /**
     * Return the statistics about the playing board
     *
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
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS; row++) {
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
     *
     * @param piece the given {@link OthelloPiece piece}
     * @return the opposite {@link OthelloPiece piece}
     */
    public static OthelloPiece opposite(final OthelloPiece piece) {
        switch (piece) {
            case WHITE_PIECE:
                return OthelloPiece.BLACK_PIECE;
            case BLACK_PIECE:
                return WHITE_PIECE;
            default:
                return null;
        }
    }

    /**
     * Handles a mouse click
     *
     * @param point the given mouse click {@link Point point}
     */
    public void handleMouseClicked(final Point point) {
        if (!cpu.isProcessing()) {
            // get the (x,y) coordinates
            final double x = point.getX();
            final double y = point.getY();
            SoundManager.getInstance().play(PIECE_MOVED);

            // determine the (column,row) position
            final int column = (int) ((x - X_OFFSET) / CELL_WIDTH);
            final int row = (int) ((y - (Y_OFFSET + Y_FUDGE)) / CELL_HEIGHT);
            logger.info(String.format("clicked at column %d, row %d (%.0f,%.0f)", column, row, x, y));

            // if the column and row are within bounds
            // place the piece
            if ((column >= 0) && (column < COLUMNS) && (row >= 0) && (row < ROWS)) {
                // attempt to acquire the capture path
                final Collection<CellCapturePath> paths = placePiece(column, row, player);
                logger.info(String.format("player path = %s", paths));

                // if a path was found ...
                if (!paths.isEmpty()) {
                    // update the statistics
                    statistics.update();

                    // get the counts
                    final int playerCount = statistics.getPlayerCount();
                    final int computerCount = statistics.getComputerCount();
                    final int emptyCount = statistics.getEmptyCount();

                    // if no more moves...
                    if (playerCount == 0 || computerCount == 0 || emptyCount == 0) {
                        gameManager.changeGameState(GameState.GAME_OVER);
                    }

                    // allow the AI to take a turn
                    else {
                        gameManager.changeGameState(GameState.ARTIFICIAL_INTELLIGENCE);
                    }
                }
            }
        }
    }

    /**
     * Handles a mouse movement
     *
     * @param point the given mouse movement {@link Point point}
     */
    public void handleMouseMoved(final Point point) {
        // get the (x,y) coordinates
        final double x = point.getX();
        final double y = point.getY();

        // determine the (column,row) position
        final int column = (int) ((x - X_OFFSET) / CELL_WIDTH);
        final int row = (int) ((y - Y_OFFSET - Y_FUDGE) / CELL_HEIGHT);

        // set the column & row
        hoverCell.column = column;
        hoverCell.row = row;
    }

    /**
     * Handles the computer's turn on the board game
     */
    public void handleCpuGamePlay() {
        // if thd AI has no more moves...
        if (cpu.getCaptureMoves(computer, player).isEmpty()) gameManager.changeGameState(GameState.GAME_OVER);
        else {
            // execute the A.I. code
            cpu.execute();

            // update the statistics
            statistics.update();

            // get the counts
            final int playerCount = statistics.getPlayerCount();
            final int computerCount = statistics.getComputerCount();
            final int emptyCount = statistics.getEmptyCount();

            // if no more moves...
            if (playerCount == 0 ||
                    computerCount == 0 ||
                    emptyCount == 0 ||
                    cpu.getCaptureMoves(player, computer).isEmpty()) {
                gameManager.changeGameState(GameState.GAME_OVER);
            }
        }
    }

    /**
     * Places a piece onto the board
     *
     * @param column the given column position
     * @param row    the given row position
     * @param piece  the {@link OthelloBoard piece} to place
     */
    protected Collection<CellCapturePath> placePiece(final int column,
                                                     final int row,
                                                     final OthelloPiece piece) {
        // create a container for returning results
        final Collection<CellCapturePath> paths = new LinkedList<>();

        // the target position must be empty
        if (grid[column][row] != null) {
            logger.error(String.format("A piece already exists at {%d,%d}", column, row));
            return paths;
        }

        // get the opponent's piece
        final OthelloPiece opponent = opposite(piece);

        // define work variables
        boolean ok;
        int x, y;

        ////////////////////////////////////////////////////////////////////
        // Step 1: Scan Horizontally Left to Right
        ////////////////////////////////////////////////////////////////////
        x = column + 1;
        ok = false;
        while ((x < COLUMNS) && (grid[x][row] == opponent)) {
            ok = true;
            x++;
        }
        if (ok && (x < COLUMNS) && (grid[x][row] == piece)) {
            paths.add(new CellCapturePath(column, row, x, row, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 2: Scan Horizontally Right to Left
        ////////////////////////////////////////////////////////////////////
        x = column - 1;
        ok = false;
        while ((x >= 0) && (grid[x][row] == opponent)) {
            ok = true;
            x--;
        }
        if (ok && (x >= 0) && grid[x][row] == piece) {
            paths.add(new CellCapturePath(x, row, column, row, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 3: Scan Vertically Low to High
        ////////////////////////////////////////////////////////////////////
        y = row + 1;
        ok = false;
        while ((y < ROWS) && (grid[column][y] == opponent)) {
            ok = true;
            y++;
        }
        if (ok && (y < ROWS) && grid[column][y] == piece) {
            paths.add(new CellCapturePath(column, row, column, y, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 4: Scan Vertically High to Low
        ////////////////////////////////////////////////////////////////////
        y = row - 1;
        ok = false;
        while ((y >= 0) && (grid[column][y] == opponent)) {
            ok = true;
            y--;
        }
        if (ok && (y >= 0) && grid[column][y] == piece) {
            paths.add(new CellCapturePath(column, row, column, y, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 5: Scan Diagonally Down Right
        ////////////////////////////////////////////////////////////////////
        x = column + 1;
        y = row + 1;
        ok = false;
        while ((x < COLUMNS) && (y < ROWS) && (grid[x][y] == opponent)) {
            ok = true;
            x++;
            y++;
        }
        if (ok && (x < COLUMNS) && (y < ROWS) && grid[x][y] == piece) {
            paths.add(new CellCapturePath(column, row, x, y, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 6: Scan Diagonally Up Right
        ////////////////////////////////////////////////////////////////////
        x = column + 1;
        y = row - 1;
        ok = false;
        while ((x < COLUMNS) && (y >= 0) && (grid[x][y] == opponent)) {
            ok = true;
            x++;
            y--;
        }
        if (ok && (x < COLUMNS) && (y >= 0) && grid[x][y] == piece) {
            paths.add(new CellCapturePath(column, row, x, y, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 7: Scan Diagonally Down Left
        ////////////////////////////////////////////////////////////////////
        x = column - 1;
        y = row + 1;
        ok = false;
        while ((x >= 0) && (y < ROWS) && (grid[x][y] == opponent)) {
            ok = true;
            x--;
            y++;
        }
        if (ok && (x >= 0) && (y < ROWS) && grid[x][y] == piece) {
            paths.add(new CellCapturePath(column, row, x, y, piece));
        }

        ////////////////////////////////////////////////////////////////////
        // Step 8: Scan Diagonally Up Left
        ////////////////////////////////////////////////////////////////////
        x = column - 1;
        y = row - 1;
        ok = false;
        while ((x >= 0) && (y >= 0) && (grid[x][y] == opponent)) {
            ok = true;
            x--;
            y--;
        }
        if (ok && (x >= 0) && (y >= 0) && grid[x][y] == piece) {
            paths.add(new CellCapturePath(column, row, x, y, piece));
        }

        // execute the actions
        for (final CellCapturePath path : paths) {
            path.execute(grid);
        }

        return paths;
    }

    public void handleMouseExited(MouseEvent event) {
        hoverCell.column = -1;
        hoverCell.row = -1;
    }

    /**
     * Grid Statistics
     *
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
            playerCount = 0;
            computerCount = 0;
            emptyCount = 0;

            // count the elements
            for (int col = 0; col < COLUMNS; col++) {
                for (int row = 0; row < ROWS; row++) {
                    final OthelloPiece piece = grid[col][row];
                    if (piece == null) emptyCount++;
                    else {
                        switch (piece) {
                            case WHITE_PIECE:
                                playerCount++;
                                break;
                            case BLACK_PIECE:
                                computerCount++;
                                break;
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

        public boolean isWinnerPlayer() {
            return playerCount > computerCount;
        }

        public boolean isDraw() {
            return playerCount == computerCount;
        }

    }

}