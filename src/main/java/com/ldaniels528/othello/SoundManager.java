package com.ldaniels528.othello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Othello Sound Manager (Java Sound API Version)
 *
 * @author lawrence.daniels@gmail.com
 */
public class SoundManager {
    // singleton instance
    private static final SoundManager instance = new SoundManager();

    // internals fields
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Collection<SoundEffectsThread> threads;
    private final Map<Object, AudioSample> samples;
    private final LinkedList<SoundKeys> queue;

    /**
     * Default Constructor
     */
    private SoundManager() {
        this.queue = new LinkedList<>();
        this.samples = new HashMap<>(25);
        this.threads = createSoundEffectThreads(4);
    }

    /**
     * Returns the singleton instance of the sound manager
     *
     * @return the singleton {@link SoundManager instance}
     */
    public static SoundManager getInstance() {
        return instance;
    }

    /**
     * Plays a sound by the sound key
     *
     * @param soundKey the given sound key
     */
    public void play(final SoundKeys soundKey) {
        synchronized (queue) {
            if (queue.size() < 3) {
                queue.add(soundKey);
                queue.notify();
            }
        }
    }

    /**
     * Shutdowns the sound manager
     */
    public void shutdown() {
        // shutdown sound effect threads
        synchronized (threads) {
            for (final SoundEffectsThread thread : threads) {
                thread.die();
            }
        }
    }

    /**
     * Retrieves an audio sample from the given resource path
     *
     * @param resourcePath the resource naming pattern (e.g. 'weapons/missile.wav')
     * @param sampleName   the name of the audio sample
     */
    public void loadAudioSample(final String resourcePath, final Object sampleName) {
        try {
            final AudioSample sample = loadAudioInputStream(resourcePath);
            samples.put(sampleName, sample);
            logger.debug(String.format("Loaded audio sample '%s' as '%s'", resourcePath, sampleName));
        } catch (final IOException e) {
            logger.debug(String.format("Failed to load audio sample '%s' as '%s'", resourcePath, sampleName));
            e.printStackTrace();
        } catch (final UnsupportedAudioFileException e) {
            logger.debug(String.format("Audio sample '%s' (%s) is an unsuported audio format", resourcePath, sampleName));
            e.printStackTrace();
        } catch (final LineUnavailableException e) {
            logger.debug(String.format("Line unavailable for audio sample '%s' (%s)", resourcePath, sampleName));
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a set of images which match the given pattern
     *
     * @return the list loaded images
     */
    private static AudioSample loadAudioInputStream(final String resourcePath)
            throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        final URL url = ContentManager.loadResource(resourcePath);
        final AudioInputStream stream = AudioSystem.getAudioInputStream(url);
        return new AudioSample(stream);
    }

    /**
     * Creates a collection of sound effect threads
     *
     * @param count the number of threads to create
     * @return a {@link Collection collection} of {@link SoundEffectsThread sound effect threads}
     */
    private Collection<SoundEffectsThread> createSoundEffectThreads(final int count) {
        final Collection<SoundEffectsThread> threads = new ArrayList<>(count);
        for (int n = 0; n < count; n++) {
            threads.add(new SoundEffectsThread());
        }
        return threads;
    }

    /**
     * Othello Sound Effects Thread
     *
     * @author lawrence.daniels@gmail.com
     */
    private class SoundEffectsThread extends Thread {
        private final byte[] buffer;
        private boolean alive;

        /**
         * Default constructor
         */
        public SoundEffectsThread() {
            this.alive = true;
            this.buffer = new byte[1024];
            start();
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            logger.debug("Sound Effects server ready...");
            while (alive) {
                // get the next sound key
                final Object soundKey = getNextObject();

                // if a key was retrieved ...
                if (soundKey != null) {
                    // play requested audio sample
                    playSound(soundKey);
                }
            }
            logger.debug("Sound effects system shutdown");
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
         *
         * @return the sound key
         */
        private Object getNextObject() {
            Object soundKey = null;
            synchronized (queue) {
                // while the queue is empty ...
                while (alive && queue.isEmpty()) {
                    // just wait
                    try {
                        queue.wait();
                    } catch (final InterruptedException e) {
                        return null;
                    }
                }

                // if the queue isn't empty, get the next key
                if (!queue.isEmpty()) {
                    soundKey = queue.removeFirst();
                }
            }
            return soundKey;
        }

        /**
         * Plays the audio sample based on the given sound key
         *
         * @param soundKey the given {@link Object sound key}
         */
        private void playSound(final Object soundKey) {
            // attempt to get the audio sample
            AudioSample sample = null;
            synchronized (samples) {
                sample = samples.get(soundKey);
            }

            // if the sample was found ...
            if (sample != null) {
                try {
                    logger.debug(String.format("Playing '%s'...", soundKey));
                    playSound(sample);
                } catch (Exception e) {
                    logger.error(String.format("Failed to play back audio sample '%s'", soundKey));
                    e.printStackTrace();
                }
            } else {
                logger.error(String.format("Audio sample '%s' not found", soundKey));
            }
        }

        /**
         * Plays the given audio sample
         *
         * @param sample the given {@link AudioSample audio sample}
         */
        private void playSound(final AudioSample sample)
                throws IOException, LineUnavailableException {
            // get the sample stream
            final ByteArrayInputStream stream = sample.getStream();
            stream.reset();

            // open the source data line
            final SourceDataLine srcDataLine = (SourceDataLine) AudioSystem.getLine(sample.getInfo());
            srcDataLine.open(sample.getFormat());

            // start the line
            srcDataLine.start();

            // feed the line the audio sample data
            try {
                int count;
                while ((count = stream.read(buffer)) != -1) {
                    srcDataLine.write(buffer, 0, count);
                }
            } finally {
                srcDataLine.drain();
                srcDataLine.close();
            }
        }
    }

    /**
     * Represents playable audio sample
     *
     * @author lawrence.daniels@gmail.com
     */
    private static class AudioSample {
        private final ByteArrayInputStream stream;
        private final DataLine.Info info;
        private final AudioFormat format;

        /**
         * Creates a new audio sample
         */
        public AudioSample(final AudioInputStream stream) throws IOException {
            this.format = stream.getFormat();
            this.info = new DataLine.Info(SourceDataLine.class, format);
            this.stream = getAudioStream(stream);
        }

        /**
         * Return the audio sample data
         *
         * @return the audio sample data
         */
        public ByteArrayInputStream getStream() {
            return stream;
        }

        /**
         * Returns the audio format
         *
         * @return the audio format
         */
        public AudioFormat getFormat() {
            return format;
        }

        /**
         * Returns the data line information
         *
         * @return the data line information
         */
        public DataLine.Info getInfo() {
            return info;
        }

        /**
         * Retrieves the audio data from the given stream
         *
         * @param stream the given {@link AudioInputStream stream}
         * @return the binary audio data
         */
        private static ByteArrayInputStream getAudioStream(final AudioInputStream stream)
                throws IOException {
            // create the memory stream
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(290 * 1024);

            // create a small buffer
            final byte[] buf = new byte[16384];

            // read a block of data into memory
            int count;
            while ((count = stream.read(buf)) != -1) {
                // store the block of data
                baos.write(buf, 0, count);
            }

            return new ByteArrayInputStream(baos.toByteArray());
        }

    }
}
