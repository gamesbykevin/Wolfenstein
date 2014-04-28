package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

public class Block 
{
    //this block is empty
    private boolean solid = false;
    
    //our door object
    private Door door;
    
    //default solid block
    public static Block solidBlock = new SolidBlock(null, null, null, null, false);
    
    //the texture for each side of the block
    private Key north, south, east, west;
    
    protected Block()
    {
    }
    
    /**
     * Open the door, if this isn't a door nothing will happen
     */
    public void open()
    {
        //make sure this is a door
        if (!isDoor())
            return;
        
        //open door
        door.open();
    }
    
    /**
     * Get the door instance
     * @return null if this block isn't a door
     */
    public Door getDoor()
    {
        return this.door;
    }
    
    protected void setWest(final Key west)
    {
        this.west = west;
    }
    
    protected void setEast(final Key east)
    {
        this.east = east;
    }
    
    protected void setNorth(final Key north)
    {
        this.north = north;
    }
    
    protected void setSouth(final Key south)
    {
        this.south = south;
    }
    
    /**
     * Create a door for this block
     */
    protected void createDoor()
    {
        this.door = new Door();
    }
    
    /**
     * Is this block a door?
     * @return true if our door object is not null, false otherwise
     */
    public boolean isDoor()
    {
        return (this.door != null);
    }
    
    public boolean isSolid()
    {
        return this.solid;
    }
    
    protected void setSolid(final boolean solid)
    {
        this.solid = solid;
    }
    
    /**
     * Get the specified side texture key
     * @return Key so we know which texture to render
     */
    public Key getWest()
    {
        return this.west;
    }
    
    /**
     * Get the specified side texture key
     * @return Key so we know which texture to render
     */
    public Key getEast()
    {
        return this.east;
    }
    
    /**
     * Get the specified side texture key
     * @return Key so we know which texture to render
     */
    public Key getNorth()
    {
        return this.north;
    }
    
    /**
     * Get the specified side texture key
     * @return Key so we know which texture to render
     */
    public Key getSouth()
    {
        return this.south;
    }
}