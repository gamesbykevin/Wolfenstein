package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.util.Timers;

public final class Boss2 extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    public Boss2()
    {
        super();
        
        //default delay for now
        final long delay = Timers.toNanoSeconds(250L);
        
        //add idle animation
        super.addAnimation(State.Idle, 1, 0, 0, delay, true);
        
        //add walking animation
        super.addAnimation(State.Walking, 4, 0, 0, delay, true);
        
        //add attacking animation
        super.addAnimation(State.Attacking, 4, 0, 1, delay, false);
        
        //add death animation
        super.addAnimation(State.Death, 4, 0, 2, delay, false);
        
        //default animation
        super.setAnimation(State.Idle);
    }
}