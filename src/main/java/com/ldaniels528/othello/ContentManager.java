package com.ldaniels528.othello;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static java.lang.String.format;

/**
 * Othello Content Manager
 *
 * @author lawrence.daniels@gmail.com
 */
public class ContentManager {
    public static final int STAGES = 8;
    // singleton instance
    private static final ContentManager instance = new ContentManager();

    // internal fields
    private final Image[] stageImages;

    /**
     * Protected Constructor
     */
    protected ContentManager() {
        this.stageImages = new Image[STAGES];

        // load the stage images
        for (int n = 0; n < stageImages.length; n++) {
            stageImages[n] = ContentManager.loadImage(format("/common/images/stage/stage%03d.jpg", n + 1));
        }
    }

    /**
     * Returns the shared game data instance
     *
     * @return the shared game data instance
     */
    public static ContentManager getInstance() {
        return instance;
    }

    /**
     * Returns the background image for the current stage
     *
     * @return the background {@link Image image}
     */
    public Image getStageImage(int stage) {
        return stageImages[stage % stageImages.length];
    }

    /**
     * Retrieves the icon from the given resource path
     *
     * @param resourcePath the given resource path
     * @return the requested {@link Image image icon} or <tt>null</tt> if not found
     */
    public static Image loadImage(final String resourcePath) {
        return new ImageIcon(loadResource(resourcePath)).getImage();
    }

    /**
     * Retrieves the URL for the resource specified by the given resource path
     *
     * @param resourcePath the given resource path
     * @return the requested {@link URL} or <tt>null</tt> if not found
     */
    public static URL loadResource(final String resourcePath) {
        // first, try to load the resource from the .jar file
        final URL url = ContentManager.class.getResource(resourcePath);
        if (url == null) {
            throw new IllegalStateException(format("Resource '%s' was not found", resourcePath));
        }

        // return the URL
        return url;
    }

}
