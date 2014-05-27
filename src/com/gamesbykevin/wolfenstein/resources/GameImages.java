package com.gamesbykevin.wolfenstein.resources;

import com.gamesbykevin.framework.resources.*;

/**
 * All game images
 * @author GOD
 */
public final class GameImages extends ImageManager
{
    //description for progress bar
    private static final String DESCRIPTION = "Loading Image Resources";
    
    /**
     * These are the keys used to access the resources and need to match the id in the xml file
     */
    public enum Keys
    {
        WallTextures, 
        Guns,
        Obstacles,
        Soldier1, Soldier2, Soldier3, BigSoldier, 
        Dog, 
        Boss1, Boss2, Boss3, Boss4, Boss5, 
        BonusItems,
        PlayerHud, Mugshots
    }
    
    public GameImages() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_IMAGE);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
    }
}