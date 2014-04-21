package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

public class Block 
{
    //this block is empty
    private boolean solid = false;
    
    //this block is not a door by default
    private boolean door;
    
    //default solid block
    public static Block solidBlock = new SolidBlock(null, null, null, null, false);
    
    private Key north, south, east, west;
    
    public Block(final boolean door)
    {
        setDoor(door);
    }
    
    public Block()
    {
        this(false);
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
    
    protected void setDoor(final boolean door)
    {
        this.door = door;
    }
    
    public boolean isDoor()
    {
        return this.door;
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