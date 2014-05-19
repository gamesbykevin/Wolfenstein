package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.util.Timers;

public final class BigSoldier extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    public BigSoldier()
    {
        super();
        
        //default delay for now
        final long delay = Timers.toNanoSeconds(250L);
        
        //add idle animation
        super.addAnimation(State.Idle, 1, 0, 0, delay, true);
        
        //add walking animation
        super.addAnimation(State.Walking, 4, 0, 1, delay, true);
        
        //add attacking animation
        super.addAnimation(State.Attacking, 3, 0, 2, delay, false);
        
        //add hurt animation
        super.addAnimation(State.Hurt, 2, 0, 3, delay, false);
        
        //add death animation
        super.addAnimation(State.Death, 5, 0, 4, delay, false);
        
        //default animation
        super.setAnimation(State.Idle);
    }
}