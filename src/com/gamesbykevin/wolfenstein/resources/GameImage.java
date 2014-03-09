package com.gamesbykevin.wolfenstein.resources;

import static com.gamesbykevin.wolfenstein.resources.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.*;

/**
 * All game images
 * @author GOD
 */
public final class GameImage extends ImageManager
{
    //location of resources
    private static final String DIRECTORY = "images/game/{0}.png";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Image Resources";
    
    /**
     * These keys need to be in a specific order to match the order in the source DIRECTORY
     */
    public enum Keys
    {
        Mouse, MouseDrag, TextureImage
    }
    
    public GameImage() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}