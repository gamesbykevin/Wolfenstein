package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.labyrinth.Location.Wall;

public class SolidBlock extends Block
{
    /**
     * Create a new solid block.<br>
     * This block isn't a door.
     */
    public SolidBlock(final Wall wall)
    {
        this(wall, false);
    }
    
    /**
     * Create a new solid block.<br>
     * @param door Is this block a door
     * @param secret Is this door a secret
     * @param goal Is this block a goal for the player to complete the level
     */
    
    public SolidBlock(final Wall wall, final boolean door)
    {
        //call to default constructor
        super();
        
        //set the side where this block will be
        super.setWall(wall);
        
        //yes the block will be solid
        super.setSolid(true);
        
        //if this is a door create one
        if (door)
            super.createDoor();
    }
}