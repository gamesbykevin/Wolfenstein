package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.util.Timers;

public final class Soldier1 extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    public Soldier1()
    {
        super();
        
        //default delay for now
        final long delay = Timers.toNanoSeconds(350L);
        
        //add idle animation
        super.addAnimation(State.Idle, 1, 0, delay, true);
        
        //add walking animation
        super.addAnimation(State.Walking, 4, 1, delay, true);
        
        //add attacking animation
        super.addAnimation(State.Attacking, 3, 2, delay, false);
        
        //add hurt animation
        super.addAnimation(State.Hurt, 2, 3, delay, false);
        
        //add death animation
        super.addAnimation(State.Death, 4, 4, delay, false);
    }
}