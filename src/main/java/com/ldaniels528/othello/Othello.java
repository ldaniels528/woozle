package com.ldaniels528.othello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.*;

import static java.awt.Color.*;
import static java.lang.String.format;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 * Othello board game
 *
 * @author lawrence.daniels@gmail.com
 */
public class Othello extends JFrame implements KeyListener, MouseListener, MouseMotionListener, WindowListener {
    private static final double VERSION = 0.48;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GameDisplayPane displayPane;
    private final SoundManager soundManager;
    private final OthelloBoard board;
    private final Camera camera;
    private GameState gameState;
    private long gameStateChangeTime;
    private boolean alive;

    /**
     * Default Constructor
     */
    public Othello() {
        super(format("Othello v%2.2f", VERSION));
        super.setDefaultCloseOperation(EXIT_ON_CLOSE);
        super.setContentPane(displayPane = new GameDisplayPane());
        super.pack();
        super.setResizable(false);
        super.setVisible(true);

        this.soundManager = SoundManager.getInstance();
        this.gameState = GameState.INITIALIZING;
        this.board = new OthelloBoard(this);
        this.camera = new Camera(displayPane);
        this.gameState = GameState.INITIALIZING;

        // add a shutdown hook
        Runtime.getRuntime().addShutdownHook(new MyShutdownHook());
    }

    /**
     * For stand-alone operation
     *
     * @param args the given command line arguments
     */
    public static void main(final String[] args) {
        Othello game = null;
        try {
            game = new Othello();
            game.init();
            game.execute();
        } catch (final Throwable cause) {
            showMessageDialog(game, cause.getMessage(), "Initialization Error", ERROR_MESSAGE);
            cause.printStackTrace();
        }
    }

    /**
     * Executes the game
     */
    public void execute() {
        // loop indefinitely
        alive = true;
        while (alive) {
            try {
                // update the game cycle
                update();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Changes the current game state
     *
     * @param state the given {@link GameState game state}
     */
    public void changeGameState(final GameState state) {
        this.gameState = state;
        this.gameStateChangeTime = System.currentTimeMillis();

        // perform game state change specific logic
        switch (state) {
            case INITIALIZING:
                board.setup();
                break;

            case STARTING:
                soundManager.play(SoundKeys.GET_READY);
                camera.setMessage(new InGameMessage("Get Ready!", YELLOW, 2000));
                break;

            case LEVEL_CHANGE:
                camera.setMessage(new InGameMessage("Great Job!", YELLOW, 2000));
                break;

            case OUT_OF_MOVES:
                camera.setMessage(new InGameMessage("Out Of Moves", YELLOW, 1000));
                break;

            case GAME_OVER:
                soundManager.play(SoundKeys.GAME_OVER);
                final OthelloBoard.GridStatistics stats = board.getStatistics();
                final InGameMessage message;
                if (stats.isWinnerPlayer()) {
                    board.wins++;
                    message = new InGameMessage("Player Wins!", GREEN, Integer.MAX_VALUE);
                } else if (stats.isDraw()) {
                    board.draws++;
                    message = new InGameMessage("Draw!", YELLOW, Integer.MAX_VALUE);
                } else {
                    board.losses++;
                    message = new InGameMessage("Computer Wins!", RED, Integer.MAX_VALUE);
                }
                camera.setMessage(message);
                break;
        }
    }

    /**
     * Initializes the game
     */
    private void init() {
        // initialize the content pane
        displayPane.init();

        // initialize the camera
        camera.init();

        // setup the level information for play
        board.setup();

        // render the scene
        //camera.renderScene(board);

        // load the audio samples
        soundManager.loadAudioSample("/othello/audio/gameOver.wav", SoundKeys.GAME_OVER);
        soundManager.loadAudioSample("/othello/audio/getReady.wav", SoundKeys.GET_READY);
        soundManager.loadAudioSample("/othello/audio/levelChange.wav", SoundKeys.LEVEL_CHANGE);
        soundManager.loadAudioSample("/othello/audio/pieceMoved.wav", SoundKeys.PIECE_MOVED);

        // set the initial game state
        changeGameState(GameState.INITIALIZING);

        // attach listeners
        super.addKeyListener(this);
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
        super.addWindowListener(this);
    }

    /*
     * (non-Javadoc)
     * @see com.ldaniels528.woozle.GameManager#update()
     */
    public void update() {
        // render the scene
        camera.renderScene(board);

        // handle the game state
        switch (gameState) {
            case INITIALIZING:
                handleGameInitializing();
                break;

            case STARTING:
                handleGameStarting();
                break;

            case PLAYING:
                handleGamePlaying();
                break;

            case ARTIFICIAL_INTELLIGENCE:
                handleComputerPlaying();
                break;

            case LEVEL_CHANGE:
                handleLevelChange();
                break;

            case OUT_OF_MOVES:
                handleOutOfMoves();
                break;

            case GAME_OVER:
                handleGameOver();
                break;
        }
    }

    /**
     * Handles the "Artificial Intelligence" game state
     */
    private void handleComputerPlaying() {
        if (System.currentTimeMillis() - gameStateChangeTime >= 1000) {
            // allow the AI to take his turn
            board.handleCpuGamePlay();

            // switch the control back to the player
            if (gameState == GameState.ARTIFICIAL_INTELLIGENCE) {
                changeGameState(GameState.PLAYING);
            }
        }
    }

    /**
     * Handles the "Starting" game state
     */
    private void handleGameInitializing() {
        if (System.currentTimeMillis() - gameStateChangeTime >= 1000) {
            changeGameState(GameState.STARTING);
        }
    }

    /**
     * Handles the "Starting" game state
     */
    private void handleGameStarting() {
        if (System.currentTimeMillis() - gameStateChangeTime >= 1000) {
            changeGameState(GameState.PLAYING);
        }
    }

    /**
     * Handles the "Playing" game state
     */
    private void handleGamePlaying() {

    }

    /**
     * Handles the 'Level Change' game state
     */
    private void handleLevelChange() {
        if (System.currentTimeMillis() - gameStateChangeTime >= 2000) {
            changeGameState(GameState.INITIALIZING);
        }
    }

    /**
     * Handles the 'Out Of Moves' game state
     */
    private void handleOutOfMoves() {
        if (System.currentTimeMillis() - gameStateChangeTime >= 1000) {
            changeGameState(GameState.GAME_OVER);
        }
    }

    /**
     * Handles the 'Game Over' game state
     */
    private void handleGameOver() {
        // do nothing
    }

    /**
     * Restarts the game
     */
    private void restartGame() {
        changeGameState(GameState.INITIALIZING);
    }

    /**
     * Shuts down the game
     */
    private void shutdown() {
        if (alive) {
            alive = false;
            soundManager.shutdown();
            logger.info("Shutting down subsystems...");
        }
    }

    @Override
    public void keyPressed(final KeyEvent event) {
        // do nothing
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        // do nothing
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        switch (event.getKeyChar()) {
            // DEBUG display game state
            case '.':
                logger.info(String.format("gameState = %s", gameState));
                break;

            // DEBUG snapshot
            case '/':
                break;

            // allow the game manager to handle it
            default:
                switch (gameState) {
                    case GAME_OVER:
                        restartGame();
                        break;

                    case PLAYING:
                        keyTyped(event);
                        break;
                }
        }
    }

    @Override
    public void mouseDragged(final MouseEvent event) {
        // do nothing
    }

    @Override
    public void mouseMoved(final MouseEvent event) {
        if (gameState == GameState.PLAYING) {
            board.handleMouseMoved(event.getPoint());
        }
    }

    @Override
    public void mouseClicked(final MouseEvent event) {
        switch (gameState) {
            case GAME_OVER:
                restartGame();
                break;

            case PLAYING:
                board.handleMouseClicked(event.getPoint());
                break;
        }
    }

    @Override
    public void mouseEntered(final MouseEvent event) {
        if (gameState == GameState.PLAYING) {
            board.handleMouseMoved(event.getPoint());
        }
    }

    @Override
    public void mouseExited(final MouseEvent event) {
        if (gameState == GameState.PLAYING) {
            board.handleMouseExited(event);
        }
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        switch (gameState) {
            case GAME_OVER:
                restartGame();
                break;

            case PLAYING:
                // nothing to do
                break;
        }
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
        switch (gameState) {
            case GAME_OVER:
                restartGame();
                break;

            case PLAYING:
                // nothing to do
                break;
        }
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        // do nothing
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        // do nothing
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        shutdown();
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
        // do nothing
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
        // do nothing
    }

    @Override
    public void windowIconified(final WindowEvent e) {
        // do nothing
    }

    @Override
    public void windowOpened(final WindowEvent e) {
        // do nothing
    }

    /**
     * Othello Shutdown Hook
     *
     * @author lawrence.daniels@gmail.com
     */
    private class MyShutdownHook extends Thread {

        @Override
        public void run() {
            shutdown();
        }

    }

}
