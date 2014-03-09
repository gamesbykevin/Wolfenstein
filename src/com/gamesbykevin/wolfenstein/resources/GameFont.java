package com.gamesbykevin.wolfenstein.resources;

import static com.gamesbykevin.wolfenstein.resources.Resources.RESOURCE_DIR;
import com.gamesbykevin.framework.resources.FontManager;

/**
 *
 * @author GOD
 */
public final class GameFont extends FontManager
{
    //location of resources
    private static final String DIRECTORY = "font/{0}.ttf";
    
    //description for progress bar
    private static final String DESCRIPTION = "Loading Font Resources";
    
    public enum Keys
    {
        Menu, Game, 
    }
    
    public GameFont() throws Exception
    {
        super(RESOURCE_DIR + DIRECTORY, Keys.values());
        
        //the description that will be displayed for the progress bar
        super.setDescription(DESCRIPTION);
    }
}