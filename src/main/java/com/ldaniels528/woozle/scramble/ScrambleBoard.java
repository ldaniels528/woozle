package com.ldaniels528.woozle.scramble;

import static com.ldaniels528.woozle.scramble.LetterElementFactory.getLetter;
import static com.ldaniels528.woozle.scramble.LetterElementFactory.getVowel;
import static com.ldaniels528.woozle.scramble.ScrambleCamera.CELL_HEIGHT;
import static com.ldaniels528.woozle.scramble.ScrambleCamera.CELL_WIDTH;
import static com.ldaniels528.woozle.scramble.ScrambleSoundKeys.DANGER;
import static com.ldaniels528.woozle.scramble.ScrambleSoundKeys.SWAP_LETTERS;
import static com.ldaniels528.woozle.scramble.ScrambleSoundKeys.WORD_FOUND;
import static java.awt.Color.RED;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.ldaniels528.woozle.InGameMessage;
import com.ldaniels528.woozle.Logger;
import com.ldaniels528.woozle.SharedGameData;

/**
 * This class represents the virtual game board for Scramble
 * @author lawrence.daniels@gmail.com
 */
class ScrambleBoard {
	private static final CellComparator COMPARATOR = new CellComparator();
	// row & column constants
	public static final int COLS = 9;
	public static final int ROWS = 5;
	
	// X & Y- click offsets
	private static final int XFUDGE = 2;
	private static final int YFUDGE = 9;
	
	// internal fields
	private final SharedGameData gameData;
	private final Set<LetterElement> selectedElements;
	private final ScrambleGameManager gameManager;
	private final ScrambleDictionary dictionary;
	private final LetterElement[][] matrix;
	private int lettersLeft;
	private int wordsLeft;
 
	/**
	 * Creates a new instance of the game board
	 * @param gameManager the given {@link ScrambleGameManager game manager}
	 */
	public ScrambleBoard( final ScrambleGameManager gameManager ) {
		this.gameManager		= gameManager;
		this.dictionary			= new ScrambleDictionary();
		this.gameData			= SharedGameData.getInstance();
		this.selectedElements	= new LinkedHashSet<LetterElement>( COLS * ROWS );
		this.matrix 			= new LetterElement[COLS][ROWS];
	}
	
	/** 
	 * Updates the game board (allowing for physics like gravity)
	 */
	public void update() {		
		// apply a downward pull on the elements
		applyGravity();
		
		// replace missing elements
		replaceMissingElements();
		
		// attempt to match the word
		peformWordMatch();
	}

	/**
	 * Removes the selection of all game elements
	 */
	public void clearSelection() {
		synchronized( selectedElements ) {
			selectedElements.clear();
		}
	}
	
	/**
	 * Fully populates the matrix with letter elements
	 */
	public void populateMatrix() {
		// populate the matrix
		for( int col = 0; col < COLS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				matrix[col][row] = getLetter( false );
			}
		}
	}

	/** 
	 * Returns the matrix of game elements
	 * @return the matrix of game elements
	 */
	public LetterElement[][] getMatrix() {
		return matrix;
	}
	
	/**
	 * Returns the element found at the given coordinates
	 * @param x the given X-coordination of the position
	 * @param y the given Y-coordination of the position
	 * @return the element found at (x,y)
	 */
	public LetterElement getElementAt( final int x, final int y ) {
		// determine the column and row of the clicked area
		final int col = ( x - XFUDGE ) >= 0 ? ( ( x - XFUDGE ) / CELL_WIDTH ) : 0;
		final int row = ( y - YFUDGE ) >= 0 ? ( ( y - YFUDGE ) / CELL_HEIGHT ) : 0;
		
		// return the element
		if( ( col < COLS ) && ( row < ROWS ) ) {
			return matrix[col][row];
		}
		else {
			return null;
		}
	}

	/**
	 * Highlights the element found at coordinate (x,y)
	 * @param x the given X-axis of the coordinate
	 * @param y the given Y-axis of the coordinate
	 * @return true, if an element was highlighted
	 */
	public void handleElementClicked( final int x, final int y ) {
		// identify the clicked element
		final LetterElement clicked = getElementAt( x, y );
		if( clicked == null ) {
			return;
		}
		
		// is it a wild card character?
		else if( clicked.isWildCard() ) {
			final Cell cell = lookupCell( clicked );
			if( cell != null ) {
				matrix[cell.col][cell.row] = getVowel();
			}
		}
		
		// toggle the selected state of the clicked element
		else {
			updateSelection( clicked, !isSelected( clicked ) );
		}
	}
	
	/**
	 * Indicates whether the given element is currently selected
	 * @param element the given {@link LetterElement game element}
	 * @return true, if the given element is currently selected
	 */
	public boolean isSelected( final LetterElement element ) {
		synchronized( selectedElements ) {
			return selectedElements.contains( element );
		}
	}

	/**
	 * Attached to match the highlighted word
	 */
	public void peformWordMatch() {		
		// get the selected word
		final String word = getSelectedWord();
		
		// is the word in the dictionary?
		if( dictionary.contains( word ) ) {
			claimWord( word );
		}
		
		// check the reverse spelling
		else {
			// get the reverse spelling of the word
			final String reverseWord = new StringBuffer( word ).reverse().toString();
			
			// is the reverse word in the dictionary?
			if( dictionary.contains( reverseWord ) ) {
				claimWord( reverseWord );
			}
		}
	}
	
	/**
	 * Force a word to be claimed (DEBUG ONLY)
	 */
	public void forceWord() {
		// get the selected word
		final String word = getSelectedWord();
		
		// claim the word
		claimWord( word );
		
		// add the word to the dictionary
		dictionary.addWord( word );
	}

	/**
	 * Shutdowns all related subsystems
	 */
	public void shutdown() {
		dictionary.persistDictionaryChanges();
	}
	
	/**
	 * Swaps selected elements
	 */
	public void swapSelectedElements( final LetterElement elementA, final LetterElement elementB ) {
		// play the audio clip
		gameManager.play( SWAP_LETTERS );
		
		// get the cell's of the elements
		final Cell cellA = lookupCell( elementA );
		final Cell cellB = lookupCell( elementB );
		
		// swap the positions of elements A and B
		matrix[cellA.col][cellA.row] = elementB;
		matrix[cellB.col][cellB.row] = elementA;
	}
	
	/**
	 * Returns the number of remaining replacement letters
	 * @return the number of remaining letters
	 */
	public int getLettersLeft() {
		return lettersLeft;
	}
	
	public void setLettersLeft(int lettersLeft) {
		this.lettersLeft = lettersLeft;
	}
	
	/** 
	 * Returns the number of remaining words to complete the level.
	 * @return the number of remaining words
	 */
	public int getWordsLeft() {
		return wordsLeft;
	}
	
	public void setWordsLeft(int wordsLeft) {
		this.wordsLeft = wordsLeft;
	}

	/** 
	 * Returns the number of vowels that are present in the matrix
	 * @return the number of vowels
	 */
	public int getVowelCount() {
		int count = 0;
		for( int col = 0; col < COLS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				final LetterElement element = matrix[col][row];
				if( element instanceof LetterElement ) {
					final LetterElement letterElem = (LetterElement)element;
					if( letterElem.isVowel() ) {
						count++;
					}
				}
			}
		}
		return count;
	}

	/**
	 * Claims the given word
	 * @param word the given word (e.g. 'EVERYONE')
	 */
	private void claimWord( final String word ) {
		// play the audio clip
		gameManager.play( WORD_FOUND );
		
		// write notification
		Logger.info( "Found word '%s'\n", word );
		
		// clear the letter from the matrix
		synchronized( selectedElements ) {
			for( final LetterElement element : selectedElements ) {
				final Cell cell = lookupCell( element );
				gameData.adjustScore( element.getValue() );
				matrix[cell.col][cell.row] = null;
			}
			selectedElements.clear();
		}
		
		// decrease the number of words left
		wordsLeft--;
		
		// check the number of vowels
		final int vowelCount = getVowelCount();
		Logger.info( "%d vowel(s) left\n", vowelCount );
		if( vowelCount == 1 && wordsLeft > 1 ) {
			gameManager.play( DANGER );
			gameManager.queueMessage( new InGameMessage( "Low Vowels", RED, 3000 ) );
		}
	}

	/**
	 * Shifts all cells with open space beneath them downward
	 */
	private void applyGravity() {
		// iterate row by row the matrix
		for( int col = 0; col < COLS; col++ ) {
			for( int row = ROWS-1; row > 0; row-- ) {
				final LetterElement cell0 = matrix[col][row];
				final LetterElement cell1 = matrix[col][row-1];
				
				// if there is a cell with nothing beneath it...
				if( cell0 == null && cell1 != null ) {
					// move the object down
					matrix[col][row-1] = null;
					matrix[col][row]   = cell1;
				}
			}
		}
	}
	
	/**
	 * Creates the row of replacement elements
	 */
	private void replaceMissingElements() {
		if( lettersLeft > 0 ) {
			final int row = 0;
			
			// populate the array
			for( int col = 0; col < COLS; col++ ) {
				if( matrix[col][row] == null ) {
					// set the letter element
					matrix[col][row] = getLetter( true );
					lettersLeft --;
					return;
				}
			}
		}
	}
	
	/** 
	 * Returns the word identified by the current selection
	 * @return the selected word
	 */
	private String getSelectedWord() {
		// get the data in proper order
		final Set<Cell> cells = new TreeSet<Cell>( COMPARATOR );
		for( final LetterElement element : selectedElements ) {
			cells.add( lookupCell( element ) );
		}
		
		// put the data into a character array
		final char[] data = new char[ selectedElements.size() ];
		int n = 0;
		for( final Cell cell : cells ) {
			data[n++] = cell.data;
		}
		
		// return the data string
		return String.valueOf( data );
	}
	
	/**
	 * Updates the selection of the given element
	 * @param element the given {@link LetterElement element}
	 * @param selected indicates whether the element is to be selected
	 * @return the number of selected elements
	 */
	private void updateSelection( final LetterElement element, final boolean selected ) {
		synchronized( selectedElements ) {
			if( selected ) {
				selectedElements.add( element );
			}
			else {
				selectedElements.remove( element );
			}
		}
	}
	
	/**
	 * Determines where the element can be found within the matrix
	 * @param element the given {@link LetterElement game element}
	 * @return the {@link Cell cell}
	 */
	private Cell lookupCell( final LetterElement element ) {
		for( int col = 0; col < COLS; col++ ) {
			for( int row = 0; row < ROWS; row++ ) {
				if( matrix[col][row] == element ) {
					return new Cell( col, row, element.getLetter() );
				}
			}
		}
		throw new IllegalStateException();
	}
	
	/**
	 * Represents a cell; a single position within the matrix
	 * @author lawrence.daniels@gmail.com
	 */
	private class Cell {
		private final int col;
		private final int row;
		private final char data;
		
		/**
		 * Creates a new cell
		 * @param col the given column position
		 * @param row the given row position
		 */
		public Cell( final int col, final int row, final char data ) {
			this.col = col;
			this.row = row;
			this.data = data;
		}
	}
	
	/**
	 * A comparator for comparing Cell instances
	 * @author lawrence.daniels@gmail.com
	 */
	private static class CellComparator implements Comparator<Cell> {

		/* 
		 * (non-Javadoc)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		public int compare( final Cell cellA, final Cell cellB ) {
			final int cols = ( cellA.col - cellB.col );
			final int rows = ( cellA.row - cellB.row );
			return cols == 0 ? rows : cols;
		}
		
	}
 	
}
