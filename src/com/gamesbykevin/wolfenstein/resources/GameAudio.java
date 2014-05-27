package com.gamesbykevin.wolfenstein.resources;

import com.gamesbykevin.framework.resources.*;

/**
 * All audio for game
 * @author GOD
 */
public final class GameAudio extends AudioManager
{
    //description for progress bar
    private static final String DESCRIPTION = "Loading Audio Resources";
    
    /**
     * These are the keys used to access the resources and need to match the id in the xml file
     */
    public enum Keys
    {
        PickupAmmo, PickupMachinegun, ExtraLife, PickupChaingun, PickupKey, PickupFood, 
        PickupTreasure1, PickupTreasure2, PickupTreasure3, PickupTreasure4, 
        DoorClose, DoorOpen, DoorOpenSecret, MachinegunFire, Knife, PistolFire, AssaultRifleFire,
        MainTheme, StageMusic, MenuMusic, HitGoalSwitch, DoorLocked
    }
    
    public GameAudio() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_AUDIO);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
    }
}