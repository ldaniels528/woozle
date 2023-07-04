package com.ldaniels528.woozle.scramble;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a game element
 * @author lawrence.daniels@gmail.com
 */
class LetterElement {
	private static final Set<Character> VOWELS = 
		new HashSet<Character>( Arrays.asList( new Character[] { 'A', 'E', 'I', 'O', 'U', 'Y' }  ) );
	private static int HASH_CODE = 0;
	private final String letter;
	private final char letterCh;
	private final int value;
	private final int hashCode;
	private Point anchor;
	
	/** 
	 * Creates a new Letter element
	 * @param letter the given character (e.g. 'A')
	 * @param value the given point value of the letter
	 */
	public LetterElement( final char letter, final int value ) {
		this.letterCh	= letter;
		this.letter 	= String.valueOf( letter );
		this.value		= value;
		this.hashCode	= HASH_CODE++;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals( final Object object ) {
		return ( object instanceof LetterElement ) && 
				( ((LetterElement)object).hashCode() == this.hashCode() );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return hashCode;
	}
	
	/**
	 * Returns the point value of the element
	 * @return the point value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Returns the letter represented by this element
	 * @return the letter  (e.g. 'A')
	 */
	public char getLetter() {
		return letterCh;
	}

	/**
	 * @return the anchor
	 */
	public Point getAnchor() {
		return anchor;
	}

	/**
	 * @param anchor the anchor to set
	 */
	public void setAnchor(Point anchor) {
		this.anchor = anchor;
	}
	
	/**
	 * Indicated whether the element is a wild card letter (?)
	 * @return true, if the element is a wild card letter (?)
	 */
	public boolean isWildCard() {
		return letterCh == '?';
	}
	
	/** 
	 * Indicates whether the letter represented by this element is a vowel
	 * @return true, if the letter is 'A', 'E', 'I', 'O', or 'U'
	 */
	public boolean isVowel() {
		return VOWELS.contains( letterCh );
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return letter;
	}

}
