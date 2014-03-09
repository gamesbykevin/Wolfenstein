package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.hero.Input;

public final class Screen extends Render
{
    private Display3D display3d;
    
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
        
        int xBlock = 2, zBlock = 5; 
        
        display3d.renderFloor();
        
        //display3d.renderWall(xBlock + 1, xBlock + 1, zBlock, zBlock + 1, 0);
        //display3d.renderWall(xBlock + 1, xBlock, zBlock + 1, zBlock + 1, 0);
        //display3d.renderWall(xBlock + 1, xBlock + 1, zBlock + 1, zBlock, 0);
        //display3d.renderWall(xBlock, xBlock + 1, zBlock + 1, zBlock + 1, 0);
        
        /*
        display3d.renderWall(xBlock + 1, xBlock + 1, zBlock, zBlock + 1, 0.5);
        display3d.renderWall(xBlock + 1, xBlock, zBlock + 1, zBlock + 1, 0.5);
        display3d.renderWall(xBlock + 1, xBlock + 1, zBlock + 1, zBlock, 0.5);
        display3d.renderWall(xBlock, xBlock + 1, zBlock + 1, zBlock + 1, 0.5);
        */
        
        display3d.renderSprite(xBlock + 3, 0, zBlock + 3, 1);
        //display3d.renderWall(0, 8, 10, 10, 0);
        //display3d.renderSprite(2, 3, 7);
        /*
        display3d.renderWall(0, 0.5, 1.5, 1.5, 0);
        display3d.renderWall(0, 0, 1, 1.5, 0);
        display3d.renderWall(0, 0.5, 1, 1, 0);
        display3d.renderWall(0.5, 0.5, 1, 1.5, 0);
        
        display3d.renderWall(3, 3.5, 4.5, 4.5, 0);
        display3d.renderWall(3, 3, 4, 4.5, 0);
        display3d.renderWall(3, 3.5, 4, 4, 0);
        display3d.renderWall(3.5, 3.5, 4, 4.5, 0);
        * */
        
        
        display3d.renderDistanceLimiter();
        draw(display3d, 0, 0);
    }
}