package com.ldaniels528.woozle;

import static java.lang.String.format;

/**
 * Represents a time count down device
 * @author lawrence.daniels@gmail.com
 */
public class StopWatch {
	private long startTimeMillis;
	private int timeLimitMillis;
	
	/**
	 * Default Constructor
	 */
	public StopWatch() {
		super();
	}
	
	/** 
	 * Returns the time remaining for the current level
	 * @return the time remaining for the current level
	 */
	public int getTimeLeft() {
		return ( timeLimitMillis - (int)( System.currentTimeMillis() - startTimeMillis ) ) / 1000;
	}
	
	/**
	 * Starts the watch
	 * @param timeLimit the time limit in seconds
	 */
	public void startCountDown( final int timeLimit ) {
		this.startTimeMillis 	= System.currentTimeMillis();
		this.timeLimitMillis	= timeLimit * 1000;
	}
	
	/** 
	 * Resets the watch
	 */
	public void reset() {
		this.startTimeMillis = System.currentTimeMillis();
	}

	public boolean isTimeUp() {
		return getTimeLeft() <= 0;
	}
		
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		final int rawTime	= getTimeLeft();
		final int timeLeft	= rawTime > 0 ? rawTime : 0;
		final int minutes 	= timeLeft / 60;
		final int seconds 	= ( timeLeft % 60 );
		return format( "%02d:%02d", minutes, seconds );
	}
	
}
