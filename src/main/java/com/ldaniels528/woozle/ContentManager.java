package com.ldaniels528.woozle;

import static java.lang.String.format;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * WooZle Content Manager
 * @author lawrence.daniels@gmail.com
 */
public abstract class ContentManager {
	private static final String LOCAL_PATH = "./src/main/resources%s";
	
	/**
	 * Protected Constructor
	 */
	protected ContentManager() {
		super();
	}
	
	/**
	 * Retrieves the icon from the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link Image image icon} or <tt>null</tt> if not found
	 */
	public static Image loadImage( final String resourcePath ) {
		// first, try to load the resource from the .jar file
		final URL url = ContentManager.class.getResource( resourcePath );
		if( url != null ) {
			return new ImageIcon( url ).getImage();
		} 
		
		// if not found, look for the file locally
		else {
			final File localFile = new File( String.format( LOCAL_PATH, resourcePath ) );
			if( localFile.exists() ) {
				return new ImageIcon( localFile.getAbsolutePath() ).getImage();
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the input stream for the resource specified by the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link InputStream stream} 
	 * @throws IOException 
	 */
	public static InputStream loadResourceAsStream( final String resourcePath ) 
	throws IOException {
		// first, try to load the resource from the .jar file
		final URL url = ContentManager.class.getResource( resourcePath );
		if( url != null ) {
			return url.openStream();
		} 
		
		// if not found, look for the file locally
		else {
			final File localFile = new File( format( LOCAL_PATH, resourcePath ) );
			return new FileInputStream( localFile );
		}
	}
	
	/**
	 * Retrieves the URL for the resource specified by the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link URL} or <tt>null</tt> if not found
	 */
	public static URL loadResource( final String resourcePath ) {
		// first, try to load the resource from the .jar file
		final URL url = ContentManager.class.getResource( resourcePath );
		if( url != null ) {
			return url;
		} 
		
		// if not found, look for the file locally
		else {
			final File localFile = new File( format( "resources%s", resourcePath ) );
			if( localFile.exists() ) {
				try {
					return new URL( format( "file://%s", localFile.getAbsolutePath() ) );
				} 
				catch( final MalformedURLException e ) {
					Logger.error( "resource => 'file://%s'\n", localFile.getAbsolutePath() );
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}

}
