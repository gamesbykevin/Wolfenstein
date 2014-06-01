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
        MainTheme, StageMusic, HitGoalSwitch, DoorLocked, HeroDeath,
        Soldier1Attack, Soldier1Death, Soldier1Alert, 
        Soldier2Attack, Soldier2Death, Soldier2Alert, 
        Soldier3Attack, Soldier3Death, Soldier3Alert, 
        DogAttack, DogDeath, DogAlert, 
        BigSoldierDeath, BigSoldierAlert, 
        Boss1Death, Boss1Alert, 
        Boss2Death, Boss2Alert, 
        Boss3Death, Boss3Alert, 
        Boss4Death, Boss4Alert, 
        Boss5Death, Boss5Alert, 
        WeaponFireOther1, WeaponFireOther2, WeaponFireOther3, 
    }
    
    public GameAudio() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_AUDIO);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
        
        if (Keys.values().length < 1)
            super.increase();
    }
}