package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.hero.Input;

import java.awt.image.BufferedImage;

public final class Screen extends Render
{
    public Display3D display3d;
    
    public BufferedImage spriteImage;
    
    public Screen(final int width, final int height, final Input input, final BufferedImage bi)
    {
        super(width, height);
        
        this.display3d = new Display3D(width, height, input);
        
        this.spriteImage = bi;
    }
    
    public void render()
    {
        for (int i=0; i < pixels.length; i++)
        {
            pixels[i] = 0;
        }
        
        int xBlock = 1, zBlock = 5; 
        
        //draw floor/ceiling
        display3d.renderTopBottom();
        
        //draw floor
        //display3d.renderFloor();
        
        //draw ceiling
        //display3d.renderCeiling();
        
        //draw walls
        display3d.renderWalls();
        
        //draw sprites
        display3d.renderSprite(xBlock, 0, zBlock, 0, this.spriteImage);
        
        //apply brightness to pixels based on depth
        display3d.renderDistanceLimiter();
        
        //store pixels to our current instance pixel array
        draw(display3d, 0, 0);
    }
}