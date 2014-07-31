package com.ldaniels528.woozle.scramble;

import static java.lang.String.format;

import java.util.Random;

import com.ldaniels528.woozle.Randomizer;

/**
 * Letter Element Factory
 * @author lawrence.daniels@gmail.com
 */
class LetterElementFactory {
	private static final char[] VOWELS = {
		'A','E','I','O','U'
	};
	private static final Random random = Randomizer.getInstance();
	
	/**
	 * Retrieves a random letter element instance
	 * @param includeSpecialCharacters indicates whether special characters (?)
	 * should be included.
	 * @return a random {@link LetterElement letter element}
	 */
	public static LetterElement getLetter( final boolean includeSpecialCharacters ) {
		// randomly generate the letter index
		final int index	 	= random.nextInt( includeSpecialCharacters ? 27 : 26 );
		final char letter	= ( index < 26 ) ? (char)( 'A' + index ) : '?';
		final int value		= getPointValue( letter );
		
		// return the letter element
		return new LetterElement( letter, value );
	}
	
	/**
	 * Retrieves a random letter element instance
	 * should be included.
	 * @return a random {@link LetterElement letter element}
	 */
	public static LetterElement getVowel() {
		// randomly generate the letter index
		final int index	 	= random.nextInt( VOWELS.length );
		final char letter	= VOWELS[ index ];
		final int value		= getPointValue( letter );
		
		// return the letter element
		return new LetterElement( letter, value );
	}
	
	/**
	 * Determines the point value of the given letter
	 * @param letter the given letter
	 * @return the point value
	 */
	private static int getPointValue( char letter ) {
		switch( letter ) {
			// special characters
			case '?': return 0;
			
			// vowels
			case 'A':;
			case 'E':;
			case 'I':;
			case 'O':;
			case 'U': return 5;
			
			// Tier 1 consonants
			case 'C':;
			case 'D':;
			case 'L':;
			case 'M':;
			case 'N':;
			case 'P':;
			case 'R':;
			case 'S':;
			case 'T': return 10;
			
			// Tier 2 consonants
			case 'B':;
			case 'F':;
			case 'G':;
			case 'H':;
			case 'J':;
			case 'K': return 15;
			
			// Tier 3 consonants
			case 'V':;
			case 'W':; 
			case 'Y': return 20;
			
			// Tier 4 consonants
			case 'Q':;
			case 'Z': return 25;
			
			// Tier 5 consonants
			case 'X': return 50;
			
			// unrecognized
			default:
				throw new IllegalArgumentException( format( "Unrecognized character '%c'", letter ) );
		}
	}

}
