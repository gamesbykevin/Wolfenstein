package com.gamesbykevin.wolfenstein.level;

import java.util.Random;

public class Level 
{
    public Block[] blocks;
    
    private final int width, height;
    
    public Level(final int width, final int height)
    {
        this.width = width;
        this.height = height;
        this.blocks = new Block[width * height];
        
        Random random = new Random();
        
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                final Block block;
                
                if (random.nextInt(4) == 0)
                {
                    block = new SolidBlock();
                }
                else
                {
                    block = new Block();
                }
                
                blocks[x + y * width] = block;
            }
        }
    }
    
    public Block create(final int x, final int y)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return Block.solidwall;
        
        return blocks[x + y * width];
    }
}
