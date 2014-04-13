package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

public class Block 
{
    public boolean solid = false;
    
    public static Block solidBlock = new SolidBlock(Key.Cement1, Key.Cement1, Key.Cement1, Key.Cement1);
    
    private Key north, south, east, west;
    
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
    
    public Key getWest()
    {
        return this.west;
    }
    
    public Key getEast()
    {
        return this.east;
    }
    
    public Key getNorth()
    {
        return this.north;
    }
    
    public Key getSouth()
    {
        return this.south;
    }
}