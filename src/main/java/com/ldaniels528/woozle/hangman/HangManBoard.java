package com.ldaniels528.woozle.hangman;

import com.ldaniels528.woozle.GameState;
import com.ldaniels528.woozle.Logger;
import com.ldaniels528.woozle.SharedGameData;

import java.awt.*;

/**
 * This class represents the virtual game board for HangMan
 * @author lawrence.daniels@gmail.com
 */
class HangManBoard {
	private final HangmanDictionary dictionary;
	private final Character[] availableLetters;
	private final HangManGameManager gameManager;
	private final SharedGameData gameData;
	private final HangingMan hangingMan;
	private Character[] wordLetters;
	private Rectangle clickArea;
	private String word;
	
	/**
	 * Creates a new HangMan playing board
	 * @param gameManager the given {@link HangManGameManager game manager}
	 */
	public HangManBoard( final HangManGameManager gameManager ) {
		this.gameManager		= gameManager;
		this.gameData			= SharedGameData.getInstance();
		this.hangingMan			= new HangingMan();
		this.dictionary 		= new HangmanDictionary();
		this.availableLetters	= new Character[ 26 ];
		this.clickArea			= new Rectangle( 0, 185, 640, 110 );
	}
	
	/**
	 * Setup the game board
	 */
	public void setup() {
		this.word			= dictionary.getRandomWord( );
		this.wordLetters	= new Character[ word.length() ];
		hangingMan.reset();
		
		// setup the available characters
		for( int n = 0; n < availableLetters.length; n++ ) {
			availableLetters[n] = (char)( 'A' + n );
		}
	}
	
	/** 
	 * Handles a mouse click
	 * @param point the given mouse {@link Point x-coordinate and y-coordinates}
	 */
	public void handleElementClicked( final Point point ) {
		Logger.info( "Mouse clicked at (%.1f,%.1f)\n", point.getX(), point.getY() );
		if( clickArea.contains( point ) ) {
			final int col = (int)( point.getX() / 25 );
			final int row = (int)( ( point.getY() - clickArea.y ) / 38 );
			Logger.info( "col = %d, row = %d\n", col, row );
			
			// identify the letter
			final int index = row * 13 + col;
			final Character letter = availableLetters[ index ];
			if( letter != null ) {
				pickLetter( letter );
			}
		}
	}

	/**
	 * Picks the given letter
	 * @param letter the given letter
	 */
	public void pickLetter( final char letter ) {
		int count = 0;
		int last  = 0;
		int index;
		
		// get the available letter index
		final int avIndex = ( letter - 'A' );
		
		// is the letter still available?
		if( availableLetters[avIndex] != null ) {
			// find all occurrences of the letter
			while( ( index = word.indexOf( letter, last ) ) != -1 ) {
				// set the letter
				wordLetters[index] = letter;
				last = index + 1;
				count++;
			}
			
			// clear the letter from the available letters
			availableLetters[avIndex] = null;
			
			// update the score
			if( count > 0 ) {
				gameManager.play( HangManSoundKeys.SWAP_LETTERS );
				gameData.adjustScore( 25 * count );
				
				// has the puzzle been solved?
				if( isWordSolved() ) {
					gameData.adjustScore( 100 );
					gameManager.changeGameState( GameState.LEVEL_CHANGE );
				}	
			}
			
			// if no letters counted ...
			else {
				gameManager.play( HangManSoundKeys.WORD_FOUND );
				
				// it was a miss
				hangingMan.miss();
				
				// has the play run out of moves?
				if( hangingMan.isOutOfMoves() ) {
					gameManager.changeGameState( GameState.OUT_OF_MOVES );
				}
				
				// is only one move left?
				else if( hangingMan.hasOneMoreMove() ) {
					gameManager.play( HangManSoundKeys.DANGER );
				}
			}
		}
	}
	
	/**
	 * Returns the click area for selecting letters
	 * @return the {@link Rectangle click area}
	 */
	public Rectangle getClickArea() {
		return clickArea;
	}
	
	/**
	 * Returns the hanging man instance
	 * @return the {@link HangingMan hanging} man instance
	 */
	public HangingMan getHangingMan() {
		return hangingMan;
	}
	
	/**
	 * Returns the current word to be solved
	 * @return the current word
	 */
	public String getWord() {
		return word;
	}
	
	/** 
	 * Returns the letters that comprised the word
	 * @return the letters that comprised the word
	 */
	public Character[] getWordLetters() {
		return wordLetters;
	}

	/** 
	 * Returns the letters that are available for use
	 * @return the letters that are available for use
	 */
	public Character[] getAvailableLetters() {
		return availableLetters;
	}

	/** 
	 * Indicates whether the word has been solved
	 * @return true, if all the word's letter have been correctly
	 * selected.
	 */
	private boolean isWordSolved() {
		int left = 0;
		for( int n = 0; n < wordLetters.length; n++ ) {
			if( wordLetters[n] == null ) {
				left++;
			}
		}
		return ( left == 0 );
	}

}
