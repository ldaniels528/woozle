package com.ldaniels528.othello;

import com.ldaniels528.othello.OthelloBoard.*;

import java.awt.*;

import static com.ldaniels528.othello.GameDisplayPane.BOARD_HEIGHT;
import static com.ldaniels528.othello.GameDisplayPane.BOARD_WIDTH;
import static com.ldaniels528.othello.OthelloBoard.*;
import static java.awt.Color.*;
import static java.awt.Cursor.HAND_CURSOR;
import static java.awt.Cursor.getPredefinedCursor;
import static java.awt.Font.BOLD;
import static java.lang.String.format;

/**
 * Othello Camera
 *
 * @author lawrence.daniels@gmail.com
 */
class Camera {
    public static final int CELL_WIDTH = 64;
    public static final int CELL_HEIGHT = 64;
    public static final int X_OFFSET = (BOARD_WIDTH - BOARD_HEIGHT) / 2;
    public static final int Y_OFFSET = 0;
    private static final int PIECE_WIDTH = CELL_WIDTH - 6;
    private static final int PIECE_HEIGHT = CELL_HEIGHT - 6;
    private static final int GRID_WIDTH = CELL_WIDTH * COLUMNS;
    private static final int GRID_HEIGHT = CELL_HEIGHT * ROWS;
    public static Font CELL_FONT = new Font("Courier", BOLD, 12);
    public static Font INFO2_FONT = new Font("Courier", BOLD, 20);

    // internal fields
    private final GameDisplayPane displayPane;
    private Graphics2D offScreen;
    private InGameMessage message;
    private final ContentManager contentManager;

    /**
     * Creates a new camera instance
     */
    public Camera(final GameDisplayPane displayPane) {
        this.displayPane = displayPane;
        this.contentManager = ContentManager.getInstance();
    }

    /**
     * Initializes the camera
     */
    public void init() {
        // get the graphics context
        this.offScreen = displayPane.getOffScreen();

        // set the cursor type
        displayPane.setCursor(getPredefinedCursor(HAND_CURSOR));
    }

    /**
     * Renders all queued messages
     */
    private void renderMessages() {
        if (message != null) {
            // render the message
            message.render(offScreen);

            // remove the message if it's expired
            if (message.isExpired()) {
                message = null;
            }
        }
    }

    /**
     * Renders the complete scene
     * @param board       the given {@link OthelloBoard game board}
     */
    public void renderScene(final OthelloBoard board) {
        // draw the background
        offScreen.drawImage(contentManager.getStageImage(board.getRounds()), 0, 0, displayPane);

        // draw the cells
        renderGameBoard(board);

        // draw the game pieces
        renderGamePieces(board);

        // draw the score
        renderGameInfo(board.getStatistics());

        // draw the game record (wins vs. losses)
        renderGameRecords(board);

        // display the messages
        renderMessages();

        // render the complete scene
        displayPane.renderScene();
    }

    /**
     * Renders the pieces onto the grid
     */
    private void renderGamePieces(final OthelloBoard board) {
        // get the grid
        final OthelloPiece[][] grid = board.getGrid();

        // draw the pieces
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS; row++) {
                // cache the piece
                final OthelloPiece piece = grid[col][row];
                if (piece != null) {
                    // compute the (x,y) coordinates
                    final int x = col * CELL_WIDTH + X_OFFSET + (CELL_WIDTH - PIECE_WIDTH) / 2;
                    final int y = row * CELL_HEIGHT + Y_OFFSET + (CELL_HEIGHT - PIECE_HEIGHT) / 2;

                    // draw the piece
                    switch (grid[col][row]) {
                        case WHITE_PIECE:
                            renderYing(x, y);
                            break;

                        case BLACK_PIECE:
                            renderYang(x, y);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Renders the Ying (White) piece
     *
     * @param x the given x-coordinate
     * @param y the given x-coordinate
     */
    private void renderYing(final int x, final int y) {
        offScreen.setColor(WHITE);
        offScreen.fillOval(x, y, PIECE_WIDTH, PIECE_HEIGHT);
        offScreen.setColor(GRAY);
        offScreen.drawOval(x, y, PIECE_WIDTH, PIECE_HEIGHT);
    }

    /**
     * Renders the Yang (Black) piece
     *
     * @param x the given x-coordinate
     * @param y the given x-coordinate
     */
    private void renderYang(final int x, final int y) {
        offScreen.setColor(BLACK);
        offScreen.fillOval(x, y, PIECE_WIDTH, PIECE_HEIGHT);
        offScreen.setColor(GRAY);
        offScreen.drawOval(x, y, PIECE_WIDTH, PIECE_HEIGHT);
    }

    /**
     * Renders the game board
     *
     * @param board the given {@link OthelloBoard playing board}
     */
    private void renderGameBoard(final OthelloBoard board) {
        // draw the solid background
        offScreen.setColor(BLUE);
        offScreen.fillRect(X_OFFSET, Y_OFFSET, GRID_WIDTH, GRID_HEIGHT);

        // draw the outline of the grid
        offScreen.setColor(GRAY);
        offScreen.drawRect(X_OFFSET, Y_OFFSET, GRID_WIDTH, GRID_HEIGHT);

        // get the hover cell
        final OthelloCell hoverCell = board.getHoverCell();

        // draw the interior lines
        for (int col = 0; col < COLUMNS; col++) {
            for (int row = 0; row < ROWS; row++) {
                // compute the (x,y) positions
                final int x = col * CELL_WIDTH + X_OFFSET;
                final int y = row * CELL_HEIGHT + Y_OFFSET;

                // if the mouse is hovering over the cell, highlight it
                final boolean hovering = hoverCell.column == col && hoverCell.row == row;
                if (hovering) {
                    offScreen.setColor(CYAN);
                    offScreen.fillRect(x, y, CELL_WIDTH, CELL_HEIGHT);
                }

                // render the cell number
                final int cellNo = computeCellNumber(col, row);
                offScreen.setFont(CELL_FONT);
                offScreen.setColor(MAGENTA);
                offScreen.drawString(String.valueOf(cellNo), x + CELL_WIDTH / 2, y + CELL_HEIGHT / 2);

                // draw the cross sections
                offScreen.setColor(GRAY);
                offScreen.drawLine(x, Y_OFFSET, x, GRID_HEIGHT);
                offScreen.drawLine(X_OFFSET, y, X_OFFSET + GRID_WIDTH, y);
            }
        }
    }

    /**
     * Renders the game information onto the graphics context
     *
     * @param statistics the {{@link GridStatistics}}
     */
    private void renderGameInfo(final GridStatistics statistics) {
        // get the counts
        final int playerCount = statistics.getPlayerCount();
        final int computerCount = statistics.getComputerCount();

        // draw the score
        offScreen.setColor(WHITE);
        offScreen.setFont(INFO2_FONT);

        // draw the player's score
        renderYing(10, 40);
        offScreen.setColor(determineStatusColor(playerCount, computerCount));
        offScreen.drawString(format("%02d", playerCount), 24, 120);

        // draw the computer's score
        renderYang(10, 300);
        offScreen.setColor(determineStatusColor(computerCount, playerCount));
        offScreen.drawString(format("%02d", computerCount), 24, 380);
    }

    private void renderGameRecords(final OthelloBoard board) {
        offScreen.setColor(CYAN);
        offScreen.drawString(String.format("Wins: %d, Losses: %d, Draws: %d",
                board.wins, board.losses, board.draws), 160, BOARD_HEIGHT - 20);
    }

    private Color determineStatusColor(int playerACount, int playerBCount) {
        if (playerACount > playerBCount) return GREEN;
        else if (playerACount == playerBCount) return YELLOW;
        else return RED;
    }

    /**
     * Adds the given message to the queue for display
     *
     * @param message the given {@link InGameMessage message}
     */
    public void setMessage(final InGameMessage message) {
        this.message = message;
    }

}