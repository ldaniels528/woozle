package com.ldaniels528.woozle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.ldaniels528.woozle.ContentManager.loadResourceAsStream;
import static java.lang.String.format;

/**
 * WooZle Dictionary 
 * @author lawrence.daniels@gmail.com
 */
public abstract class GameDictionary {
	protected static final String DICTIONARY_NAME = "WooZle.dict";
	protected static final int MAX_SIZE = 1000;
	protected final File userHomeDirectory;
	protected final List<String> words;
	
	/**
	 * Default Constructor
	 */
	public GameDictionary() {
		this.words				= new ArrayList<String>( MAX_SIZE );
		this.userHomeDirectory	= new File( System.getProperty( "user.home" ) );
		
		try {
			load();
		} 
		catch( final IOException e ) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the dictionary's data into memory
	 * @throws IOException
	 */
	protected abstract void load() throws IOException;
	
	/**
	 * Returns the local dictionary file
	 * @return the local dictionary {@link File file}
	 */
	protected File getLocalDictionaryFile() {
		return new File( format( "%s%s%s", 
				userHomeDirectory.getAbsolutePath(),
				File.separator, 
				DICTIONARY_NAME ) );
	}
	
	/**
	 * Returns the stream which points to the dictionary
	 * file either locally ($USER_HOME/dictionary.data) or
	 * from the Java Archive (JAR)
	 * @return the {@link InputStream stream} which points to the dictionary
	 * @throws IOException
	 */
	protected InputStream getDictionaryStream() 
	throws IOException {
		// is there a local file?
		final File localFile = getLocalDictionaryFile();
		if( localFile.exists() ) {
			return new FileInputStream( localFile );
		}
		else {
			return loadResourceAsStream( format( "/common/data/%s",DICTIONARY_NAME ) );
		}	
	}

}
