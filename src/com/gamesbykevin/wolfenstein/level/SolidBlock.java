package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

public class SolidBlock extends Block
{
    /**
     * Create a new solid block with the specified wall texture for each side
     * @param north Texture for the north wall
     * @param south Texture for the south wall
     * @param east Texture for the east wall
     * @param west Texture for the west wall
     */
    public SolidBlock(final Key north, final Key south, final Key east, final Key west)
    {
        setEast(east);
        setWest(west);
        setNorth(north);
        setSouth(south);
        
        this.solid = true;
    }
}