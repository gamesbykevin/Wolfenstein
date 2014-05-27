package com.gamesbykevin.wolfenstein.hero.weapons;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.hero.weapons.Weapons.Type;
import com.gamesbykevin.wolfenstein.shared.Shared;

public final class Weapon implements Disposable
{
    //which weapon is this
    private Type type;
    
    //the current, default start, and max amount of bullets
    private int current, max, start;
    
    protected Weapon(final Type type)
    {
        this.type = type;
        
        //set the limits
        this.current = type.getStart();
        this.start   = type.getStart();
        this.max     = type.getMax();
    }
    
    @Override
    public void dispose()
    {
        this.type = null;
    }
    
    /**
     * Add the specified amount of bullets to the current total.
     * @param amount The number of bullets we want to add.
     */
    protected void add(final int amount)
    {
        setCurrent(getCurrent() + amount);
        
        if (getCurrent() > getMax())
            setCurrent(getMax());
        if (getCurrent() < 0)
            setCurrent(0);
        
        if (Shared.DEBUG)
            System.out.println("Weapon Type: " + type + ". Ammo set to: " + getCurrent());
    }
    
    /**
     * Does this weapon have ammunition?
     * @return true if there is at least 1 bullet, false otherwise
     */
    protected boolean hasAmmunition()
    {
        return (getCurrent() != 0);
    }
    
    /**
     * Does this weapon currently loaded with full ammo.
     * @return true if the current bullet count is >= the maximum allowed, false otherwise
     */
    protected boolean hasMax()
    {
        return (getCurrent() >= getMax());
    }
    
    /**
     * Get the maximum amount of bullets allowed.
     * @return The maximum amount of bullets allowed.
     */
    private int getMax()
    {
        return this.max;
    }
    
    /**
     * Get the current amount of bullets
     * @return the number of bullets ranging from 0 - maximum allowed
     */
    protected int getCurrent()
    {
        return this.current;
    }
    
    /**
     * Get the default starting amount of bullets
     * @return The starting amount of bullets
     */
    protected int getDefault()
    {
        return this.start;
    }
    
    
    /**
     * Set the number of bullets.
     * @param current The number of bullets we want to set
     */
    private void setCurrent(final int current)
    {
        this.current = current;
    }
    
    protected Type getType()
    {
        return this.type;
    }
    
    /**
     * Shoot the weapon and remove 1 bullet
     */
    protected void shoot()
    {
        setCurrent(getCurrent() - 1);
        
        if (Shared.DEBUG)
            System.out.println("Shoot - Weapon Type: " + type + ". Ammo currently: " + getCurrent());
    }
    
}