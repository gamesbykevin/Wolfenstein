package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.resources.GameImages;

public final class Boss3 extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    protected Boss3()
    {
        super(GameImages.Keys.Boss3);
        
        //set damage
        super.setDamage(20);
        
        //add health to 100 default
        super.modifyHealth(500);
        
        //set audio keys for this enemy
        super.setAudioKeyAlert(GameAudio.Keys.Boss3Alert);
        super.setAudioKeyAttack(GameAudio.Keys.WeaponFireOther1);
        super.setAudioKeyDeath(GameAudio.Keys.Boss3Death);
        
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