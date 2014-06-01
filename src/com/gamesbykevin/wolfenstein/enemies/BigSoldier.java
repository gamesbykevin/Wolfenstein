package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.resources.GameImages;

public final class BigSoldier extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    protected BigSoldier()
    {
        super(GameImages.Keys.BigSoldier);
        
        //set damage
        super.setDamage(11);
        
        //add health to 100 default
        super.modifyHealth(20);
        
        //set audio keys for this enemy
        super.setAudioKeyAlert(GameAudio.Keys.BigSoldierAlert);
        super.setAudioKeyAttack(GameAudio.Keys.MachinegunFire);
        super.setAudioKeyDeath(GameAudio.Keys.BigSoldierDeath);
        
        //add idle animation
        super.addAnimation(State.Idle, 1, 0, 0, DEFAULT_DELAY, false);
        
        //add walking animation
        super.addAnimation(State.Walking, 4, 0, 0, DEFAULT_DELAY, true);
        
        //add attack stance animation
        super.addAnimation(State.AttackStance, 1, 0, 1, DEFAULT_DELAY, false);
        
        //add attacking animation
        super.addAnimation(State.Attacking, 3, 0, 1, DEFAULT_DELAY, false);
        
        //add death animation
        super.addAnimation(State.Death, 4, 0, 2, DEFAULT_DELAY, false);
        
        //default animation
        super.setAnimation(State.Idle);
    }
}