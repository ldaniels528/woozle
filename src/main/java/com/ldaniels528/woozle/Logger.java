package com.ldaniels528.woozle;

/**
 * WooZle Logger
 * @author lawrence.daniels@gmail.com
 */
public class Logger {
	private static boolean DEBUG = false;
	private static boolean INFO = true;
	private static boolean ERROR = true;
	
	/** 
	 * Logs a debug message
	 * @param format
	 * @param objects
	 */
	public static void debug( final String format, final Object...objects ) {
		if( DEBUG ) {
			System.err.printf( format, objects );
		}
	}
	
	/** 
	 * Logs a informational message
	 * @param format
	 * @param objects
	 */
	public static void info( final String format, final Object...objects ) {
		if( INFO ) {
			System.err.printf( format, objects );
		}
	}
	
	/** 
	 * Logs a error message
	 * @param format
	 * @param objects
	 */
	public static void error( final String format, final Object...objects ) {
		if( ERROR ) {
			System.err.printf( format, objects );
		}
	}

}
