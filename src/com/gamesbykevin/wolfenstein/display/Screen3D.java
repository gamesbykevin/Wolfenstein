package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.enemies.Enemy;
import com.gamesbykevin.wolfenstein.engine.Engine;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * This class will render the 3d objects
 */
public final class Screen3D extends Render
{
    //this object will do the 3d rendering
    public Render3D render3d;
    
    //our final image that will be drawn for the 3d objects
    private BufferedImage image;
    
    public Screen3D(final int width, final int height) throws Exception
    {
        super(width, height);
        
        //create new 3d render object with same dimensions
        this.render3d = new Render3D(width, height);
        
        //create a new image
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        //get the pixel data from the image and set as the screen pixels
        this.setPixels(((DataBufferInt)this.image.getRaster().getDataBuffer()).getData());
    }
    
    /**
     * Reset all pixel data to 0
     */
    private void resetPixelData()
    {
        for (int i=0; i < getPixels().length; i++)
        {
            getPixels()[i] = 0;
        }
    }
    
    /**
     * Write pixel data to array for anything 3d: (walls/floor/ceiling/enemies/level-objects)
     */
    public void renderPixelData(final Engine engine, final Enemy sprite) throws Exception
    {
        //reset pixel data
        resetPixelData();
        
        //set hero input first to do 3d rendering in perspective to where the player is located
        render3d.update(engine.getManager().getPlayer().getInput());
        
        //draw floor/ceiling
        render3d.renderTopBottom(engine.getManager().getTextures());
        
        //draw walls
        render3d.renderWalls(engine.getManager().getTextures(), engine.getManager().getLevel());
        
        //draw sprites/level-objects
        final int xBlock = 5, zBlock = 10; 
        render3d.renderSprite(xBlock, 0, zBlock, 0, sprite.getPixels(), (int)sprite.getWidth(), (int)sprite.getHeight());
        
        //apply brightness to pixels based on depth
        render3d.renderDistanceLimiter();
        
        //store pixels to our current instance pixel array
        for (int i=0; i < render3d.getPixels().length; i++)
        {
            //make sure the color exists first
            if (render3d.getPixels()[i] >= 0)
                getPixels()[i] = render3d.getPixels()[i];
        }
    }
    
    public BufferedImage getImage()
    {
        return this.image;
    }
}