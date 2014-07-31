package com.ldaniels528.woozle.hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.ldaniels528.woozle.GameDictionary;
import com.ldaniels528.woozle.Randomizer;

/**
 * WooZle: Hangman Dictionary 
 * @author lawrence.daniels@gmail.com
 */
class HangmanDictionary extends GameDictionary {
	private static final int MIN_LENGTH = 6;
	private static final int MAX_LENGTH = 13;
	private final Set<String> usedWords;
	private final Random random;
	
	/**
	 * Default Constructor
	 */
	public HangmanDictionary() {
		this.random		= Randomizer.getInstance();
		this.usedWords	= new HashSet<String>( MAX_SIZE );
	}

	/** 
	 * Returns a random word
	 * @return a random word
	 */
	public String getRandomWord() {
		// get the count of word
		final int wordCount = words.size();
		
		// find a random unused word
		String word = null;
		do {
			// get a random index
			final int index = random.nextInt( wordCount );
			
			// get the word
			word = words.get( index );	
		} 
		while( usedWords.contains( word ) );
		
		// record the use of the word
		usedWords.add( word );
		
		// return the word
		return word;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameDictionary#load()
	 */
	protected void load() 
	throws IOException {
		BufferedReader reader = null;
		
		// load the dictionary
		try {
			// get the input stream for the resource
			reader = new BufferedReader( new InputStreamReader( getDictionaryStream() ) );
			
			// read the contents of the file
			String line;
			while( ( line = reader.readLine() ) != null ) {
				final int length = line.length();
				if( length >= MIN_LENGTH && length <= MAX_LENGTH ) {
					words.add( line );
				}
			}
			
			// report words loaded
			System.err.printf( "%s words loaded\n", words.size() );
		}
		finally {
			if( reader != null ) {
				try { reader.close(); } catch( IOException e ) { }
			}
		}
	}

}
 