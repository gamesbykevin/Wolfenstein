package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

public class Block implements Disposable
{
    //this block is empty
    private boolean solid = false;
    
    //our door object
    private Door door;
    
    //default solid block
    public static Block SOLID_BLOCK = new SolidBlock(Wall.West);
    
    //the texture for each side of the block
    private Key north, south, east, west;
    
    //this is block the goal
    private boolean goal = false;
    
    //store the side where this block is placed
    private Wall wall;
    
    protected Block()
    {
    }

    @Override
    public void dispose()
    {
        if (door != null)
            door.dispose();
        
        door = null;
    }
    
    protected void setWall(final Wall wall)
    {
        this.wall = wall;
    }
    
    protected Wall getWall()
    {
        return this.wall;
    }
    
    /**
     * Set this block as the goal for the level
     * @param goal true if this is a goal, false otherwise
     */
    protected void setGoal(final boolean goal)
    {
        this.goal = goal;
    }
    
    /**
     * Is this block the goal?
     * @return true if this is the goal that ends the level, false otherwise
     */
    public boolean isGoal()
    {
        return this.goal;
    }
    
    /**
     * Get the door instance
     * @return null if this block isn't a door
     */
    public Door getDoor()
    {
        return this.door;
    }
    
    /**
     * Set the texture of the west side
     * @param west The texture we want for this side
     */
    protected void setWest(final Key west)
    {
        this.west = west;
    }
    
    /**
     * Set the texture of the east side
     * @param east The texture we want for this side
     */
    protected void setEast(final Key east)
    {
        this.east = east;
    }
    
    /**
     * Set the texture of the north side
     * @param north The texture we want for this side
     */
    protected void setNorth(final Key north)
    {
        this.north = north;
    }
    
    /**
     * Set the texture of the south side
     * @param south The texture we want for this side
     */
    protected void setSouth(final Key south)
    {
        this.south = south;
    }
    
    /**
     * Create a door for this block
     */
    protected void createDoor()
    {
        //create a door
        this.door = new Door();
    }
    
    protected void setSecret(final boolean secret)
    {
        //is this a secret and currently we don't have a door
        if (secret && !isDoor())
        {
            //create a door
            createDoor();
        }
        
        if (isDoor())
            getDoor().setSecret(secret);
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