package com.gamesbykevin.wolfenstein.hero.weapons;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import java.util.ArrayList;
import java.util.List;

public final class Weapons implements Disposable
{
    //setup variables for gun rules
    private static final int DEFAULT_KNIFE = -1;
    private static final int MAX_KNIFE = -1;
    private static final int DEFAULT_PISTOL = 50;
    private static final int MAX_PISTOL = 200;
    private static final int DEFAULT_ASSAULT_RIFLE = 100;
    private static final int MAX_ASSAULT_RIFLE = 400;
    private static final int DEFAULT_MACHINEGUN = 200;
    private static final int MAX_MACHINEGUN = 500;
    
    //default damage for weapons
    private static final int DEFAULT_DAMAGE_KNIFE = 25;
    private static final int DEFAULT_DAMAGE_PISTOL = 40;
    private static final int DEFAULT_DAMAGE_ASSAULT_RIFLE = 33;
    private static final int DEFAULT_DAMAGE_MACHINEGUN = 50;
    
    public static final long DELAY_KNIFE = Timers.toNanoSeconds(100L);
    public static final long DELAY_PISTOL = Timers.toNanoSeconds(125L);
    public static final long DELAY_ASSAULT_RIFLE = Timers.toNanoSeconds(15L);
    public static final long DELAY_MACHINEGUN = Timers.toNanoSeconds(45L);
    
    public enum Type
    {
        Knife(DEFAULT_KNIFE, MAX_KNIFE, DEFAULT_DAMAGE_KNIFE),
        Pistol(DEFAULT_PISTOL, MAX_PISTOL, DEFAULT_DAMAGE_PISTOL),
        AssaultRifle(DEFAULT_ASSAULT_RIFLE, MAX_ASSAULT_RIFLE, DEFAULT_DAMAGE_ASSAULT_RIFLE),
        MachineGun(DEFAULT_MACHINEGUN, MAX_MACHINEGUN, DEFAULT_DAMAGE_MACHINEGUN);
        
        final int start, max, damage;
        
        /**
         * @param start Default start bullet count
         * @param max Maximum bullet count allowed
         * @param damage The amount of damage from 1 bullet
         */
        private Type(final int start, final int max, final int damage)
        {
            this.start = start;
            this.max = max;
            this.damage = damage;
        }
        
        /**
         * Get the damage for this weapon
         * @return The damage from 1 bullet
         */
        protected int getDamage()
        {
            return this.damage;
        }
        
        /**
         * The default starting bullet count
         * @return The starting amount of bullets
         */
        protected int getStart()
        {
            return this.start;
        }
        
        /**
         * The maximum amount of bullets allowed to be kept
         * @return Maximum amount of bullets allowed
         */
        protected int getMax()
        {
            return this.max;
        }
    }
    
    //our weapons
    private List<Weapon> weapons;
    
    //the current weapon selected
    private Type type;
    
    public Weapons()
    {
        //create new list of weapons
        this.weapons = new ArrayList<>();
        
        //add default weapons
        this.add(Type.Knife);
        this.add(Type.Pistol);
        
        //default to pistol
        this.set(Type.Pistol);
    }
    
    @Override
    public void dispose()
    {
        for (Weapon weapon : weapons)
        {
            weapon.dispose();
            weapon = null;
        }
        
        weapons.clear();
        weapons = null;
        type = null;
    }
    
    /**
     * Does this weapon already exist in our list
     * @param type The type of weapon
     * @return true if we already have the weapon, false otherwise
     */
    public boolean hasWeapon(final Type type)
    {
        return (getWeapon(type) != null);
    }
    
    /**
     * Get the ammo count
     * @return The number of bullets for the current equipped gun
     */
    public int getAmmoCount()
    {
        return getWeapon().getCurrent();
    }
    
    /**
     * Get the damage for the currently equipped weapon
     * @return The damage is for 1 bullet
     */
    public int getDamage()
    {
        return getWeapon().getDamage();
    }
    
    /**
     * Get the currently equipped weapon.
     * @return Weapon the player is currently using.
     */
    protected Weapon getWeapon()
    {
        return getWeapon(getType());
    }
    
    /**
     * Get the weapon of the specified type
     * @param type The type of weapon we want
     * @return The weapon object, if not found null is returned
     */
    protected Weapon getWeapon(final Type type)
    {
        for (Weapon weapon : weapons)
        {
            if (weapon.getType() == type)
                return weapon;
        }
        
        return null;
    }
    
    /**
     * Get the current weapon equipped.
     * @return The current weapon equipped
     */
    public Type getType()
    {
        return this.type;
    }
    
    /**
     * Fire the current weapon
     * @return True if a bullet was fired, false if the weapon does not have ammunition
     */
    public boolean shoot()
    {
        Weapon weapon = getWeapon(getType());
        
        if (weapon.hasAmmunition())
        {
            weapon.shoot();
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Set the current type of weapon to equip.<br>
     * If the weapon does not exist then it won't be equipped
     * @param type The type of weapon we want to equip
     */
    public void set(final Type type)
    {
        if (hasWeapon(type))
            this.type = type;
    }
    
    /**
     * Add this weapon to inventory.<br>
     * If the weapon already exists just add 25% of the default start ammo to current ammo
     * @param type The type of weapon we want to add
     */
    public void add(final Type type)
    {
        //if we already have the weapon add ammo
        if (hasWeapon(type))
        {
            //if the current equipped weapon already has the max ammo add it to another weapon
            if (type == Type.Knife || getWeapon(type).hasMax())
            {
                for (Type tmp : Type.values())
                {
                    //skip knife
                    if (tmp == Type.Knife)
                        continue;
                    
                    //if we have this weapon and don't have the maximum ammo allowed
                    if (hasWeapon(tmp) && !getWeapon(tmp).hasMax())
                    {
                        //add ammo to this weapon object
                        addAmmo(tmp);
                        
                        //exit loop
                        break;
                    }
                }
            }
            else
            {
                //add ammo to this weapon object
                addAmmo(type);
            }
        }
        else
        {
            this.weapons.add(new Weapon(type));
        }
    }
    
    private void addAmmo(final Type type)
    {
        //add ammo to this weapon object
        getWeapon(type).add(getWeapon(type).getDefault() / 4);
    }
    
    /**
     * Add ammo to the currently equipped gun
     */
    public void add()
    {
        add(getType());
    }
}