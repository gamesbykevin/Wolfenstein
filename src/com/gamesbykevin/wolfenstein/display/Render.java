package com.gamesbykevin.wolfenstein.display;

public class Render 
{
    public final int width;
    public final int height;
    public final int[] pixels;
    
    public Render(final int width, final int height)
    {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
    }
    
    public void draw(final Render display, final int xOffset, final int yOffset)
    {
        for (int y=0; y < display.height; y++)
        {
            int yPixel = y + yOffset;
            
            //make sure we are still in bounds
            if (yPixel < 0 || yPixel >= height)
                continue;
            
            for (int x=0; x < display.width; x++)
            {
                int xPixel = x + xOffset;
                
                //make sure we are still in bounds
                if (xPixel < 0 || xPixel >= width)
                    continue;
                
                final int alpha = display.pixels[x + y * display.width];
                
                //this is so we don't render transparent pixels
                if (alpha >= 0)
                {
                    this.pixels[xPixel + yPixel * width] = alpha;
                }
            }
        }
    }
}