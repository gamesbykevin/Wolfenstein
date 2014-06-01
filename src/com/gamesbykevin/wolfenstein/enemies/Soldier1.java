package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.resources.GameImages;

public final class Soldier1 extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    protected Soldier1()
    {
        super(GameImages.Keys.Soldier1);
        
        //set damage
        super.setDamage(5);
        
        //set audio keys for this enemy
        super.setAudioKeyAlert(GameAudio.Keys.Soldier1Alert);
        super.setAudioKeyAttack(GameAudio.Keys.Soldier1Attack);
        super.setAudioKeyDeath(GameAudio.Keys.Soldier1Death);
        
        //add idle animation
        super.addAnimation(State.Idle, 1, 0, 0, DEFAULT_DELAY, false);
        
        //add walking animation
        super.addAnimation(State.Walking, 4, 0, 1, DEFAULT_DELAY, true);
        
        //add attack stance animation
        super.addAnimation(State.AttackStance, 1, 1, 2, DEFAULT_DELAY, false);
        
        //add attacking animation
        super.addAnimation(State.Attacking, 3, 0, 2, Timers.toNanoSeconds(100L), false);
        
        //add hurt animation
        super.addAnimation(State.Hurt, 1, 0, 3, Timers.toNanoSeconds(500L), false);
        
        //add death animation
        super.addAnimation(State.Death, 4, 0, 4, DEFAULT_DELAY, false);
        
        //default animation
        super.setAnimation(State.Idle);
    }
}