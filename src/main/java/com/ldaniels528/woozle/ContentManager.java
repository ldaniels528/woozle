package com.ldaniels528.woozle;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static java.lang.String.format;

/**
 * WooZle Content Manager
 * @author lawrence.daniels@gmail.com
 */
public abstract class ContentManager {

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
		return new ImageIcon( loadResource( resourcePath ) ).getImage();
	}
	
	/**
	 * Retrieves the input stream for the resource specified by the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link InputStream stream} 
	 * @throws IOException 
	 */
	public static InputStream loadResourceAsStream( final String resourcePath ) 
	throws IOException {
		return loadResource( resourcePath ).openStream();
	}
	
	/**
	 * Retrieves the URL for the resource specified by the given resource path
	 * @param resourcePath the given resource path
	 * @return the requested {@link URL} or <tt>null</tt> if not found
	 */
	public static URL loadResource( final String resourcePath ) {
		// first, try to load the resource from the .jar file
		final URL url = ContentManager.class.getResource( resourcePath );
		if( url == null ) {
			throw new IllegalStateException(format("Resource '%s' was not found", resourcePath));
		} 
		
		// return the URL
		return url;
	}

}
