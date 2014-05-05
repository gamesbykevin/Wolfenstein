package com.gamesbykevin.wolfenstein.level;

public class SolidBlock extends Block
{
    /**
     * Create a new solid block.<br>
     * This block will not be a door or a secret.
     */
    public SolidBlock()
    {
        this(false, false);
    }
    
    /**
     * Create a new solid block.<br>
     * @param door Is this block a door
     * @param secret Is this door a secret
     */
    public SolidBlock(final boolean door, final boolean secret)
    {
        //call to default constructor
        super();
        
        //yes the block will be solid
        super.setSolid(true);
        
        //if this is a door create one, and if this is a secret it is definitely a door
        if (door || secret)
        {
            super.createDoor(secret);
        }
    }
}