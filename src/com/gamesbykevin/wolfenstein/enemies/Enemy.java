package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.wolfenstein.level.objects.LevelObject;

public abstract class Enemy extends LevelObject
{
    /**
     * Different actions the enemy can represent
     */
    public enum State
    {
        Idle, Walking, Attacking, Hurt, Death
    }
    
    /**
     * Create new enemy
     */
    protected Enemy()
    {
        super();
    }
    
    /**
     * Is the enemy dead?
     * @return true if dead, false if not or if the animation does not exist
     */
    public boolean isDead()
    {
        if (!hasAnimation(State.Death))
            return false;
        
        return (getKey() == State.Death);
    }
    
    /**
     * Is the enemy hurt?
     * @return true if hurt, false if not or if the animation does not exist
     */
    public boolean isHurt()
    {
        if (!hasAnimation(State.Hurt))
            return false;
        
        return (getKey() == State.Hurt);
    }
    
    /**
     * Is the enemy attacking?
     * @return true if attacking, false if not or if the animation does not exist
     */
    public boolean isAttacking()
    {
        if (!hasAnimation(State.Attacking))
            return false;
        
        return (getKey() == State.Attacking);
    }
    
    /**
     * Is the enemy walking?
     * @return true if walking, false if not or if the animation does not exist
     */
    public boolean isWalking()
    {
        if (!hasAnimation(State.Walking))
            return false;
        
        return (getKey() == State.Walking);
    }
    
    /**
     * Is the enemy idle?
     * @return true if idle, false if not or if the animation does not exist
     */
    public boolean isIdle()
    {
        if (!hasAnimation(State.Idle))
            return false;
        
        return (getKey() == State.Idle);
    }
}