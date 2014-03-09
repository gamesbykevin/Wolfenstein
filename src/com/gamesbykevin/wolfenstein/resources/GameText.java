package com.gamesbykevin.wolfenstein.resources;

import static com.gamesbykevin.wolfenstein.resources.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.TextManager;

/**
 * All audio for game
 * @author GOD
 */
public final class GameText extends TextManager
{
    //location of resources
    private static final String DIRECTORY = "text/game/{0}.txt";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Game Text Resources";
    
    public enum Keys
    {
        
    }
    
    public GameText() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}