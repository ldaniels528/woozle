package com.ldaniels528.woozle;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * WooZle Sound Manager (Java Sound API Version)
 * @author lawrence.daniels@gmail.com
 */
public class SoundManager {	
	// singleton instance
	private static final SoundManager instance = new SoundManager();
	
	// internals fields
	private final Collection<SoundEffectsThread> threads;
	private final Map<Object,AudioSample> samples;
	private final LinkedList<Object> queue;

	/**
	 * Default Constructor
	 */
	private SoundManager() {
		this.queue		= new LinkedList<Object>();
		this.samples	= new HashMap<Object, AudioSample>( 25 );
		this.threads 	= createSoundEffectThreads( 4 );
	}
	
	/**
	 * Returns the singleton instance of the sound manager
	 * @return the singleton {@link SoundManager instance}
	 */
	public static SoundManager getInstance() {
		return instance;
	}
	
	/**
	 * Clears the queue
	 */
	public void clearQueue() {
		synchronized( queue ) {
			queue.clear();
			queue.notify();
		}
	}
	
	/**
	 * Plays a sound by the sound key
	 * @param soundKey the given sound key
	 */
	public void play( final Object soundKey ) {
		synchronized( queue ) {
			if( queue.size() < 3 ) {
				queue.add( soundKey );
				queue.notify();
			}
		}
	}

	/**
	 * Plays a sound by the sound key
	 * @param soundKey the given sound key
	 */
	public void playBackroundMusic( final Object soundKey ) {
//		// attempt to get the audio data
//		AudioData audioData = null;
//		synchronized( samplesAU ) {
//			audioData = samplesAU.get( soundKey );
//		}
//
//		// if the audio data was found ...
//		if( audioData != null ) {
//			final ContinuousAudioDataStream cas = new ContinuousAudioDataStream( audioData );
//		    AudioPlayer.player.start( cas );
//		}
	}

	/**
	 * Shutdowns the sound manager
	 */
	public void shutdown() {	
		// shutdown sound effect threads
		synchronized( threads ) {
			for( final SoundEffectsThread thread : threads ) {
				thread.die();
			}
		}
	}
	
	/**
	 * Retrieves an audio sample from the given resource path
	 * @param resourcePath the resource naming pattern (e.g. 'weapons/missile.wav')
	 * @param sampleName the name of the audio sample
	 */
	public void loadAudioSample( final String resourcePath, final Object sampleName ) {
		try {
			final AudioSample sample = loadAudioInputStream( resourcePath );
			if( sample != null ) {
				samples.put( sampleName, sample );
				Logger.debug( "Loaded audio sample '%s' as '%s'\n", resourcePath, sampleName );
			}
		}
		catch( final IOException e ) {
			Logger.debug( "Failed to load audio sample '%s' as '%s'\n", resourcePath, sampleName );
			e.printStackTrace();
		}
		catch( final UnsupportedAudioFileException e ) {
			Logger.debug( "Audio sample '%s' (%s) is an unsuported audio format\n", resourcePath, sampleName );
			e.printStackTrace();
		} 
		catch(  final LineUnavailableException e ) {
			Logger.debug( "Line unavailable for audio sample '%s' (%s)\n", resourcePath, sampleName );
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves an audio sample from the given resource path
	 * @param resourcePath the resource naming pattern (e.g. 'weapons/missile.au')
	 * @param clipName the name of the audio sample
	 */
	public void loadAudioSampleAU( final String resourcePath, final Object clipName ) {
//		try {
//				final AudioData audioData = loadAudioData( resourcePath );
//				if( audioData != null ) {
//					samplesAU.put( clipName, audioData );
//					Logger.info( "Loaded audio sample '%s' as '%s'\n", resourcePath, clipName );
//				}
//		}
//		catch( IOException e ) {
//			e.printStackTrace();
//		}
	}

	/**
	 * Retrieves a set of images which match the given pattern
	 * @return the list loaded images
	 */
	private static AudioSample loadAudioInputStream( final String resourcePath ) 
	throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		final URL url = ContentManager.loadResource(resourcePath);
		final AudioInputStream stream = AudioSystem.getAudioInputStream( url );
		return new AudioSample( stream );
	}
	
	/** 
	 * Creates a collection of sound effect threads
	 * @param count the number of threads to create
	 * @return a {@link Collection collection} of {@link SoundEffectsThread sound effect threads}
	 */
	private Collection<SoundEffectsThread> createSoundEffectThreads( final int count ) {
		final Collection<SoundEffectsThread> threads = new ArrayList<SoundEffectsThread>( count );
		for( int n = 0; n < count; n++ ) {
			threads.add( new SoundEffectsThread() );
		}
		return threads;
	}
	
	/** 
	 * WooZle Sound Effects Thread
	 * @author lawrence.daniels@gmail.com
	 */
	private class SoundEffectsThread extends Thread {
		private final byte[] buffer;
		private boolean alive;

		/** 
		 * Default constructor
		 */
		public SoundEffectsThread() {
			this.alive	= true;
			this.buffer = new byte[ 1024 ];
			start();
		}

		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			Logger.debug( "Sound Effects server ready...\n" );
			while( alive ) {
				// get the next sound key
				final Object soundKey = getNextObject();
				
				// if a key was retrieved ...
				if( soundKey != null ) {
					// play requested audio sample
					playSound( soundKey );
				}
			}
			Logger.debug( "Sound effects system shutdown\n" );
		}
		
		/**
		 * Causes the thread to die
		 */
		public void die() {
			alive = false;
			super.interrupt();
		}
		
		/**
		 * Return the next sound key from the queue
		 * @return the sound key
		 */
		private Object getNextObject() {
			Object soundKey = null;
			synchronized( queue ) {
				// while the queue is empty ...
				while( alive && queue.isEmpty() ) {
					// just wait
					try { queue.wait(); } 
					catch( final InterruptedException e ) { 
						return null;
					}
				}
				
				// if the queue isn't empty, get the next key
				if( !queue.isEmpty() ) {
					soundKey = queue.removeFirst();
				}
			}
			return soundKey;
		}
		
		/** 
		 * Plays the audio sample based on the given sound key
		 * @param soundKey the given {@link Object sound key}
		 */
		private void playSound( final Object soundKey ) {
			// attempt to get the audio sample
			AudioSample sample = null;
			synchronized( samples ) {
				sample = samples.get( soundKey );
			}
			
			// if the sample was found ...
			if( sample != null ) {
				try {
					Logger.debug( "Playing '%s'...\n", soundKey );
					playSound( sample );
				}
				catch( Exception e ) {
					Logger.error( "Failed to play back audio sample '%s'\n", soundKey );
					e.printStackTrace();
				}
			}
			else {
				Logger.error( "Audio sample '%s' not found\n", soundKey );
			}
		}
		
		/**
		 * Plays the given audio sample
		 * @param sample the given {@link AudioSample audio sample}
		 * @throws IOException
		 * @throws LineUnavailableException
		 */
		private void playSound( final AudioSample sample ) 
		throws IOException, LineUnavailableException {
			// get the sample stream
			final ByteArrayInputStream stream = sample.getStream();
			stream.reset();
			
			// open the source data line
			final SourceDataLine srcDataLine = (SourceDataLine)AudioSystem.getLine( sample.getInfo() );
			srcDataLine.open( sample.getFormat() );

			// start the line
			srcDataLine.start();
	 
			// feed the line the audio sample data
			try {
				int count;
				while( ( count = stream.read( buffer ) ) != -1 ) {
					srcDataLine.write( buffer, 0, count );
				}
			} 
			finally {
				srcDataLine.drain();
				srcDataLine.close();
			}
		}
	}
	
	/**
	 * Represents playable audio sample
	 * @author lawrence.daniels@gmail.com
	 */
	private static class AudioSample {
		private final ByteArrayInputStream stream;
		private final DataLine.Info info;
		private final AudioFormat format;
		
		/**
		 * Creates a new audio sample
		 * @throws LineUnavailableException 
		 * @throws IOException 
		 */
		public AudioSample( final AudioInputStream stream ) 
		throws LineUnavailableException, IOException {
			this.format = stream.getFormat();
			this.info 	= new DataLine.Info( SourceDataLine.class, format );
			this.stream	= getAudioStream( stream );
		}
		
		/**
		 * Return the audio sample data
		 * @return the audio sample data
		 */
		public ByteArrayInputStream getStream() {
			return stream;
		}
		
		/**
		 * Returns the audio format
		 * @return the audio format
		 */
		public AudioFormat getFormat() {
			return format;
		}
		
		/**
		 * Returns the data line information
		 * @return the data line information
		 */
		public DataLine.Info getInfo() {
			return info;
		}
		
		/**
		 * Retrieves the audio data from the given stream
		 * @param stream the given {@link AudioInputStream stream}
		 * @return the binary audio data
		 * @throws IOException 
		 */
		private static ByteArrayInputStream getAudioStream( final AudioInputStream stream ) 
		throws IOException {
			// create the memory stream
			final ByteArrayOutputStream baos = new ByteArrayOutputStream( 290 * 1024 );
	
			// create a small buffer
			final byte[] buf = new byte[ 16384 ];
			
			// read a block of data into memory
			int count;
			while( ( count = stream.read( buf ) ) != -1 ) {
				// store the block of data
				baos.write( buf, 0, count );
			};
			
			return new ByteArrayInputStream( baos.toByteArray() );
		}
		
	}
}
