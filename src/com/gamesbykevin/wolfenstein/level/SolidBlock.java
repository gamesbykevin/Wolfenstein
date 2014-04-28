package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

public class SolidBlock extends Block
{
    /**
     * Create a new solid block with the specified wall texture for each side.<br>
     * This block will not be a door
     * @param north Texture for the north wall
     * @param south Texture for the south wall
     * @param east Texture for the east wall
     * @param west Texture for the west wall
     */
    public SolidBlock(final Key north, final Key south, final Key east, final Key west)
    {
        this(north, south, east, west, false);
    }
    
    /**
     * Create a new solid block with the specified wall texture for each side
     * @param north Texture for the north wall
     * @param south Texture for the south wall
     * @param east Texture for the east wall
     * @param west Texture for the west wall
     * @param door Is this block a door
     */
    public SolidBlock(final Key north, final Key south, final Key east, final Key west, final boolean door)
    {
        //call to default constructor
        super();
        
        //yes the block will be solid
        super.setSolid(true);
        
        //if this is a door create one
        if (door)
        {
            super.createDoor();
        }
        
        //set the texture key's for each side of the walls
        setEast(east);
        setWest(west);
        setNorth(north);
        setSouth(south);
    }
}