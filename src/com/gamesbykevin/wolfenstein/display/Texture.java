package com.gamesbykevin.wolfenstein.display;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Rectangle;

public final class Texture extends Render
{
    //all wall textures are 64 x 64, this value also needs to be a multiple of 8
    private static final int WIDTH  = 64;
    private static final int HEIGHT = 64;
    
    //our wall texture image
    private BufferedImage bufferedImage;
    
    /**
     * 
     * @param image Our single image that contains all wall textures
     * @param col The column where the wall texture resides
     * @param row The row where the wall texture resides
     */
    public Texture(final Image image, final int col, final int row)
    {
        this();
        
        update(image, col, row);
    }
    
    public Texture()
    {
        super(WIDTH, HEIGHT);
        
        //create new buffered image
        bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    }
    
    /**
     * Update pixel array data with the current
     * @param image An image that we want to grab a section of
     * @param col The column location
     * @param row The row location
     */
    private void update(final Image image, final int col, final int row)
    {
        //find the x,y where our wall texture starts
        final int startX = (col * WIDTH);
        final int startY = (row * HEIGHT);
        
        update(image, startX, startY, WIDTH, HEIGHT);
    }
    
    /**
     * Update pixel array data with the current
     * @param image An image that we want to grab a section of
     * @param location The portion of the image
     */
    public void update(final Image image, final Rectangle location)
    {
        update(image, location.x, location.y, location.width, location.height);
    }
    
    /**
     * Update pixel array data with the current
     * @param image An image that we want to grab a section of
     * @param x Start x coordinate we want to grab from image
     * @param y Start y coordinate we want to grab from image
     * @param width Width we want to extend to
     * @param height Height we want to extend to
     */
    public void update(final Image image, final int x, final int y, final int width, final int height)
    {
        //clear pixel data
        bufferedImage.createGraphics().clearRect(0, 0, WIDTH, HEIGHT);
        
        //write image to buffered image
        bufferedImage.createGraphics().drawImage(image, 0, 0, WIDTH, HEIGHT, x, y, x + width, y + height, null);
        
        //copy array data to temp pixels
        bufferedImage.getRGB(0, 0, WIDTH, HEIGHT, getPixels(), 0, WIDTH);
    }
    
    public BufferedImage getBufferedImage()
    {
        return bufferedImage;
    }
}