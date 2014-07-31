package com.ldaniels528.woozle;

import java.util.Random;

/**
 * WooZle Random Seed Generator
 * @author lawrence.daniels@gmail.com
 */
public class Randomizer {
	private static final Random random = new Random( System.currentTimeMillis() );
	
	/**
	 * Private Constructor
	 */
	private Randomizer() {
		super();
	}
	
	/**
	 * Returns the random instance
	 * @return the {@link Random random} instance
	 */
	public static Random getInstance() {
		return random;
	}
	
}
