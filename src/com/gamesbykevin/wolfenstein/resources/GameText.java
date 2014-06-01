package com.gamesbykevin.wolfenstein.resources;

import com.gamesbykevin.framework.resources.TextManager;

/**
 * All audio for game
 * @author GOD
 */
public final class GameText extends TextManager
{
    //description for progress bar
    private static final String DESCRIPTION = "Loading Game Text Resources";
    
    /**
     * These are the keys used to access the resources and need to match the id in the xml file
     */
    public enum Keys
    {
        
    }
    
    public GameText() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_TEXT);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
        
        if (Keys.values().length < 1)
            super.increase();
    }
}