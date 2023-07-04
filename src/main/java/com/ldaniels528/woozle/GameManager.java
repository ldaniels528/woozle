package com.ldaniels528.woozle;

import java.awt.event.*;

/**
 * WooZle Game Manager
 * @author lawrence.daniels@gmail.com
 */
public abstract class GameManager implements KeyListener, MouseListener, MouseMotionListener { 
	public  static final int STAGES = 8;
	protected final SoundManager soundManager;
	protected GameState gameState;
	protected long gameStateChangeTime;
	protected boolean switchGame;
	protected boolean musicOn;
	protected int level;
	
	/**
	 * Default Constructor
	 */
	public GameManager() {
		this.soundManager	= SoundManager.getInstance();
		this.gameState		= GameState.INITIALIZING;
		this.musicOn		= false;
		this.level			= 1;
	}
	
	/**
	 * Initializes the game manager
	 */
	public abstract void init();
	
	/**
	 * @return the switchGame
	 */
	public boolean isSwitchGame() {
		return switchGame;
	}

	/**
	 * @param switchGame the switchGame to set
	 */
	public void setSwitchGame( final boolean switchGame ) {
		this.switchGame = switchGame;
	}

	/**
	 * Shutdowns all related subsystems
	 */
	public void shutdown() {
		soundManager.shutdown();
	}

	/**
	 * Updates the game based on it's state
	 */
	public abstract void update();
	
	/**
	 * Changes the current game state
	 * @param state the given {@link GameState game state}
	 */
	public void changeGameState( final GameState state ) {
		this.gameState			 = state;
		this.gameStateChangeTime = System.currentTimeMillis();
	}
	
	/** 
	 * Returns the current game state
	 * @return the current {@link GameState game state}
	 */
	public GameState getGameState() {
		return gameState;
	}
	
	/**
	 * Plays a sound by the sound key
	 * @param soundKey the given sound key
	 */
	public void play( final Object soundKey ) {
		soundManager.play( soundKey );
	}
	
	/**
	 * Plays a sound by the sound key
	 * @param soundKey the given sound key
	 */
	public void playBackroundMusic( final Object soundKey ) {
		soundManager.playBackroundMusic( soundKey );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed( final KeyEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased( final KeyEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped( final KeyEvent event ) {
		// may be overridden 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked( final MouseEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered( final MouseEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited( final MouseEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed( final MouseEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased( final MouseEvent event ) {
		// may be overridden 
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged( final MouseEvent event ) {
		// may be overridden 
	}

	/* 
	 * (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved( final MouseEvent event ) {
		// may be overridden 
	}
	
}
