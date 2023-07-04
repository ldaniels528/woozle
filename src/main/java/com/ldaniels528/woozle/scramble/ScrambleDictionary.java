package com.ldaniels528.woozle.scramble;

import com.ldaniels528.woozle.GameDictionary;
import com.ldaniels528.woozle.Logger;

import java.io.*;
import java.util.Collections;

/**
 * WooZle: Scramble Dictionary 
 * @author lawrence.daniels@gmail.com
 */
class ScrambleDictionary extends GameDictionary {
	private int wordsAdded;
	
	/**
	 * Default Constructor
	 */
	public ScrambleDictionary() {
		this.wordsAdded	= 0;
	}
	
	/** 
	 * Adds the given word to the dictionary
	 * @param word the given word (e.g. 'ABOVE')
	 */
	public void addWord( final String word ) {
		// add the word to the dictionary
		synchronized( words ) {
			words.add( word );	
		}
		
		// count this word
		wordsAdded++;
	}
	
	/** 
	 * Searches the dictionary for the given word
	 * @param searchWord the given word to search for (e.g. 'SELE?T")
	 * @return true, if the word is found in the dictionary
	 */
	public boolean contains( final String searchWord ) {
		synchronized( words ) {
			return words.contains( searchWord );
		}
	}

	/**
	 * Writes the local dictionary to the disk
	 */
	public void persistDictionaryChanges() {
		if( wordsAdded > 0 ) {
			// get the reference to the local file
			final File localFile = getLocalDictionaryFile();
			
			// sort the words
			Collections.sort( words );
			
			// write the words to disk
			PrintWriter writer = null;
			int count = 0;
			try {
				// write the dictionary to disk
				writer = new PrintWriter( localFile );
				synchronized( words ) {
					for( final String word : words ) {
						writer.println( word );
						count++;
					}
				}
				
				Logger.info( "%d dictionary entries were added during game play.\n", wordsAdded );
				Logger.info( "Wrote %d dictionary entries to '%s'\n", count, localFile.getAbsolutePath() );
			}
			catch( final IOException e ) {
				Logger.error( "Unable to persist the local dictionary: %s\n", e.getMessage() );
			}
			finally {
				if( writer != null ) {
					writer.close();
				}
			}
		}
	}
	
	/* 
	 * (non-Javadoc)
	 * @see com.ldaniels528.woozle.GameDictionary#load()
	 */
	protected void load() 
	throws IOException {
		BufferedReader reader = null;
		
		// load the dictionary
		synchronized( words ) {
			try {
				// get the input stream for the resource
				reader = new BufferedReader( new InputStreamReader( getDictionaryStream() ) );
				
				// read the contents of the file
				String line;
				while( ( line = reader.readLine() ) != null ) {
					words.add( line );
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

}
 