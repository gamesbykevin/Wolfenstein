package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem;
import com.gamesbykevin.wolfenstein.hero.weapons.Weapons;

public final class Hero implements Disposable
{
    //we will track player location/speed here
    private Input input;
    
    //how many keys the hero has
    private int keys = 0;
    
    //the number of lives the player has
    private int lives = 3;
    
    //the heroes health
    private int health = HEALTH_MAX;
    
    //the min/max for the health
    protected static final int HEALTH_MAX = 100;
    protected static final int HEALTH_MIN = 0;
    
    //the heroes score
    private int score = 0;
    
    //the inventory of weapons
    private Weapons weapons;
    
    //bonus item info etc...
    protected static final int SMALL_HEALTH = 10;
    protected static final int HEALTH_KIT = 25;
    protected static final int TREASURE_1 = 500;
    protected static final int TREASURE_2 = 1000;
    protected static final int TREASURE_3 = 2000;
    protected static final int TREASURE_4 = 5000;
    
    public Hero()
    {
        //object that will check keyboard input and collision etc...
        this.input = new Input();
        
        //create new weapon inventory object
        this.weapons = new Weapons();
    }
    
    @Override
    public void dispose()
    {
        weapons.dispose();
        weapons = null;
        
        input.dispose();
        input = null;
    }
    
    public void setLocation(final double x, final double z)
    {
        getInput().setX(x);
        getInput().setZ(z);
    }
    
    /**
     * Add weapon/ammo to inventory. <br>
     * If we already have the weapon ammo will be added to it.<br>
     * If this is ammo then it will be added to the current equipped weapon
     * @param type The type of weapon/ammo we want to add
     */
    public void add(final BonusItem.Type type) throws Exception
    {
        switch(type)
        {
            case AmmoClip:
                weapons.add();
                break;
                
            case AssaultGun:
                weapons.add(Weapons.Type.AssaultRifle);
                break;
                
            case MachineGun:
                weapons.add(Weapons.Type.MachineGun);
                break;
                
            default:
                throw new Exception("Weapon type not setup here");
        }
    }
    
    public Input getInput()
    {
        return input;
    }
    
    public void update(final Engine engine) throws Exception
    {
        getInput().update(engine.getKeyboard(), engine.getManager().getLevel(), this, engine.getResources());
    }
    
    /**
     * Does the hero have a key?
     * @return true if the hero has at least 1 key, false otherwise
     */
    protected boolean hasKey()
    {
        return (this.keys > 0);
    }
    
    /**
     * Change the heroes health
     * @param change The adjustment to the health we want to make.
     */
    protected void modifyHealth(final int change)
    {
        //make the adjustment
        setHealth(getHealth() + change);
        
        //keep the health within range of 0 - 100
        if (getHealth() < HEALTH_MIN)
            setHealth(HEALTH_MIN);
        if (getHealth() > HEALTH_MAX)
            setHealth(HEALTH_MAX);
    }
    
    private void setHealth(final int health)
    {
        this.health = health;
    }
    
    /**
     * Get the heroes health
     * @return the players health ranging from 0 - 100
     */
    protected int getHealth()
    {
        return this.health;
    }
    
    /**
     * Add 1 key to inventory
     */
    protected void addKey()
    {
        this.keys++;
    }
    
    /**
     * Remove 1 key from inventory
     */
    protected void removeKey()
    {
        this.keys--;
    }
    
    public int getLives()
    {
        return this.lives;
    }
    
    public void setLives(final int lives)
    {
        this.lives = lives;
    }
    
    /**
     * Remove all keys
     */
    public void removeKeys()
    {
        this.keys = 0;
    }
    
    protected void addScore(final int score)
    {
        this.score += score;
    }
    
    protected int getScore()
    {
        return this.score;
    }
}