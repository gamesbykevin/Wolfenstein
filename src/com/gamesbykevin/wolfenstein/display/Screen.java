package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.hero.Input;

public final class Screen extends Render
{
    public Display3D display3d;
    
    public Screen(final int width, final int height, final Input input)
    {
        super(width, height);
        
        this.display3d = new Display3D(width, height, input);
    }
    
    public void render()
    {
        for (int i=0; i < pixels.length; i++)
        {
            pixels[i] = 0;
        }
        
        int xBlock = 5, zBlock = 5; 
        
        //draw floor/ceiling
        display3d.renderTopBottom();
        
        //draw walls
        display3d.renderWalls();
        
        //draw sprites
        display3d.renderSprite(xBlock, 0, zBlock, 0.5);
        
        //apply brightness to pixels based on depth
        display3d.renderDistanceLimiter();
        
        //store pixels to our current instance pixel array
        draw(display3d, 0, 0);
    }
}