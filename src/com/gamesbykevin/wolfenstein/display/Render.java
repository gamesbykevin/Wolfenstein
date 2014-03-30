package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Dimension;

public abstract class Render implements Disposable
{
    //dimensions of our image
    private Dimension dimension;
    
    //image pixel data
    private int[] pixels;
    
    public Render(final int width, final int height)
    {
        //create our dimension
        this.dimension = new Dimension(width, height);
        
        //create our pixel data array
        this.pixels = new int[width * height];
    }
    
    /**
     * Clean up resources
     */
    @Override
    public void dispose()
    {
        dimension = null;
        pixels = null;
    }
    
    public int getWidth()
    {
        return dimension.width;
    }
    
    public int getHeight()
    {
        return dimension.height;
    }
    
    protected void setPixels(final int[] pixels)
    {
        this.pixels = pixels;
    }
    
    public int[] getPixels()
    {
        return pixels;
    }
}