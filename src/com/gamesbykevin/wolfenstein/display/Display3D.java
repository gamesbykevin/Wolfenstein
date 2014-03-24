package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.hero.Input;
import com.gamesbykevin.wolfenstein.level.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Display3D extends Render
{
    //our array that stores the depth of the pixels so we can apply opacity etc..
    private double[] zBuffer;
    
    //our zBufferWall will store the depth of the walls so we can determine the closest wall to the player to render
    private double[] zBufferWall;
    
    //this will help determine the brightness depending on how far away the object is
    private double renderBrightnessDistance = 5000;
    
    //render pixels within a certain depth
    private final double depthLimit = 200.0;
    
    //the height of the ceiling and floor
    private final double floorPosition  = 8;
    private final double ceilingPostion = 8;
    
    private Input input;
    
    private double forward, right, up, walking, cosine, sine;
    
    public Level level;
    
    public Display3D(final int width, final int height, final Input input)
    {
        super(width, height);
        
        this.zBuffer = new double[width * height];
        this.zBufferWall = new double[width];
        
        this.input = input;
        
        this.level = new Level(10, 10);
    }
    
    /**
     * Render the floor and ceiling
     */
    public void renderTopBottom()
    {
        forward  = input.z;   //negative value would move backward
        right    = input.x;   //negative value would move left
        walking = 0;
        up = Math.sin(input.y / 10) * 2;
        
        //our depth
        double z;
        
        //are we dealing with the floor
        boolean floor;
        
        final double rotation = input.rotation;
        
        cosine = Math.cos(rotation);
        sine = Math.sin(rotation);
        
        for (int y=0; y < height; y++)
        {
            floor = true;
            
            double ceiling = (y - height / 2.0) / height;
            
            walking = Math.sin(input.count / 6.0) * 0.8;
            
            z = (floorPosition + up + walking) / ceiling;
            
            if (input.runWalk)
            {
                walking = Math.sin(input.count / 6.0) * 2;
                z = (floorPosition + up + walking) / ceiling;
            }
            
            if (!input.walking)
            {
                walking = 0;
                z = (floorPosition + up + walking) / ceiling;
            }
            
            if (ceiling < 0)
            {
                z = (ceilingPostion - up - walking) / -ceiling;
                
                //this is part of the ceiling
                floor = false;
            }
            
            for (int x=0; x < width; x++)
            {
                double depth = (x - width / 2.0) / height;
                depth *= z;
                
                double xx = depth * cosine + z * sine;
                double yy = z * cosine - depth * sine;
                
                int xPix = (int)(xx + right);
                int yPix = (int)(yy + forward);
                
                final int index = x + y * width;
                
                zBuffer[index] = z;
                
                //only render pixels certain depth
                if (z > depthLimit)
                {
                    pixels[index] = 0;
                }
                else
                {
                    if (floor)
                    {
                        //render floor
                        pixels[index] = input.temp.pixels[(xPix & (input.bi.getWidth()-1)) + (yPix & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                    }
                    else
                    {
                        //render ceiling
                        pixels[index] = input.temp.pixels[(xPix & (input.bi.getWidth()-1)) + (yPix & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                    }
                }
            }
        }
    }
    
    /**
     * Draw our walls
     */
    public void renderWalls()
    {
        //reset our buffer to 0
        for (int x=0; x < zBufferWall.length; x++)
        {
            this.zBufferWall[x] = 0;
        }
        
        for (int xBlock = -1; xBlock <= level.blocks.length; xBlock++)
        {
            for (int zBlock = -1; zBlock <= level.blocks.length; zBlock++)
            {
                final int extra = 1;
                
                Block block = level.create(xBlock, zBlock);
                
                Block east = level.create(xBlock + extra, zBlock);
                Block south = level.create(xBlock, zBlock + extra);
                
                if (block.solid)
                {
                    //draw west wall
                    if (!east.solid)
                    {
                        //renderWall(xBlock + extra, xBlock + extra, zBlock, zBlock + extra, 0);
                        renderWall(xBlock + extra, xBlock + extra, zBlock, zBlock + extra, 0.5);
                    }
                    
                    //draw north wall
                    if (!south.solid)
                    {
                        //renderWall(xBlock + extra, xBlock, zBlock + extra, zBlock + extra, 0);
                        renderWall(xBlock + extra, xBlock, zBlock + extra, zBlock + extra, 0.5);
                    }
                }
                else
                {
                    //draw east wall
                    if (east.solid)
                    {
                        //renderWall(xBlock + extra, xBlock + extra, zBlock + extra, zBlock, 0);
                        renderWall(xBlock + extra, xBlock + extra, zBlock + extra, zBlock, 0.5);
                    }
                    
                    //draw south wall
                    if (south.solid)
                    {
                        //renderWall(xBlock, xBlock + extra, zBlock + extra, zBlock + extra, 0);
                        renderWall(xBlock, xBlock + extra, zBlock + extra, zBlock + extra, 0.5);
                    }
                }
            }
        }
    }
    
    public void renderSprite(final double x, final double y, final double z, final double heightOffset, final BufferedImage bi)
    {
        //adjustment variables
        final double upCorrect = -0.125;
        final double rightCorrect = 0.0625;
        final double forwardCorrect = 0.0625;
        final double walkCorrect = 0.0625;
        
        //add adjustment variables in perspective to our hero
        double xc = (x - (right * rightCorrect)) * 2;
        double yc = (y - (up * upCorrect)) + (walking * walkCorrect) * 2 + heightOffset;
        double zc = (z - (forward * forwardCorrect)) * 2;
        
        double rotX = xc * cosine - zc * sine;
        double rotY = yc;
        double rotZ = zc * cosine + xc * sine;
        
        //locate the center of our window
        double xCenter = width / 2.0;
        double yCenter = height / 2.0;
        
        //locate where the sprite is to the center
        double xPixel = rotX / rotZ  * height + xCenter;
        double yPixel = rotY / rotZ  * height + yCenter;
        
        //scale so the sprite will be bigger
        final double scale = 6.0;
        
        //the left and right x pixels, manipulate these to increase the width of the sprite
        double xPixelL = xPixel - (bi.getWidth() * scale) / rotZ;
        double xPixelR = xPixel + (bi.getWidth() * scale) / rotZ;
        
        //the top and bottom y pixels manipulate these to increase the height of the sprite
        double yPixelU = yPixel - (bi.getHeight() * scale) / rotZ;
        double yPixelD = yPixel + (bi.getHeight() * scale) / rotZ;
        
        //convert to int
        int xpl = (int)xPixelL;
        int xpr = (int)xPixelR;
        int ypu = (int)yPixelU;
        int ypd = (int)yPixelD;
        
        //make sure we are within window boundary
        if (xpl < 0)
            xpl = 0;
        if (xpr > width)
            xpr = width;
        if (ypu < 0)
            ypu = 0;
        if (ypd > height)
            ypd = height;
        
        rotZ *= 8;
        
        //fill in sprite pixels to our destination
        for (int yp = ypu; yp < ypd; yp++)
        {
            double pixelRotationY = (yp - yPixelU) / (yPixelD - yPixelU);
            int yTexture = (int)(pixelRotationY * bi.getHeight());
            
            for (int xp = xpl; xp < xpr; xp++)
            {
                double pixelRotationX = (xp - xPixelL) / (xPixelR - xPixelL);
                int xTexture = (int)(pixelRotationX * bi.getWidth());
                
                final int index = xp + yp * width;
                
                if (zBuffer[index] > rotZ)
                {
                    //get the pixel array data from the image
                    int[] tmpPixels = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
                    
                    //get the color of a specific pixel
                    int color = tmpPixels[(xTexture & (bi.getWidth()-1)) + (yTexture & (bi.getHeight()-1)) * bi.getWidth()];
                    
                    //don't render transparent pixels
                    if (color != 0xffff00ff && color != 0)
                    {
                        pixels[index] = color;
                        zBuffer[index] = rotZ;
                    }
                }
            }
        }
    }
    
    /**
     * Render wall at the specified location
     * @param xLeft x left location
     * @param xRight x right location
     * @param zDistanceLeft depth of left side
     * @param zDistanceRight depth of right side
     * @param yHeight The height where the image will be drawn
     */
    public void renderWall(final double xLeft, final double xRight, final double zDistanceLeft, final double zDistanceRight, final double yHeight)
    {
        final double upCorrect = 0.0625;
        final double rightCorrect = 0.0625;
        final double forwardCorrect = 0.0625;
        final double walkCorrect = -0.0625;
        
        //double xcLeft = ((xLeft / 2) - (right * rightCorrect)) * 2;
        //double zcLeft = ((zDistanceLeft / 2) - (forward * forwardCorrect)) * 2;
        double xcLeft = (xLeft - (right * rightCorrect)) * 2;
        double zcLeft = (zDistanceLeft - (forward * forwardCorrect)) * 2;
        
        double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
        double yCornerTL = ((-yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        //this will increase the texture height
        double yCornerBL = ((1 - yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;
        
        //double xcRight = ((xRight / 2) - (right * rightCorrect)) * 2;
        //double zcRight = ((zDistanceRight / 2) - (forward * forwardCorrect)) * 2;
        double xcRight = (xRight - (right * rightCorrect)) * 2;
        double zcRight = (zDistanceRight - (forward * forwardCorrect)) * 2;
        
        double rotRightSideX = xcRight * cosine - zcRight * sine;
        double yCornerTR = ((-yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        //this will increase the texture height
        double yCornerBR = ((1 - yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        double rotRightSideZ = zcRight * cosine + xcRight * sine;
        
        double tex30 = 0;
        double tex40 = input.bi.getWidth();
        final double clip = 1.75;
        
        //if both sides are going to be clipped don't bother rendering the wall
        if (rotLeftSideZ < clip && rotRightSideZ < clip)
            return;
        
        //for clipping so walls aren't infinitely drawn
        if (rotLeftSideZ < clip)
        {
            double clip0 = (clip - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
            rotLeftSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
            rotLeftSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
            tex30 = tex30 + (tex40 - tex30) * clip0;
        }
        
        //for clipping so walls aren't infinitely drawn
        if (rotLeftSideZ < clip)
        {
            double clip0 = (clip - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
            rotRightSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
            rotRightSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
            tex30 = tex30 + (tex40 - tex30) * clip0;
        }
        
        //calculate all corner x pixels of this wall texture, left side will have the same 2 xPixelLeft and same for the 2 xPixelRight
        double xPixelLeft  = (rotLeftSideX  / rotLeftSideZ  * height + width / 2);
        double xPixelRight = (rotRightSideX / rotRightSideZ * height + width / 2);
        
        //don't draw
        if (xPixelLeft >= xPixelRight)
            return;
        
        int xPixelLeftInt = (int)xPixelLeft;
        int xPixelRightInt = (int)xPixelRight;
        
        if (xPixelLeftInt < 0)
            xPixelLeftInt = 0;
        
        if (xPixelRightInt > width)
            xPixelRightInt = width;
        
        //calculate all 4 corner y pixels of this wall texture
        double yPixelLeftTop =      ((yCornerTL / rotLeftSideZ  * height) + (height / 2.0));
        double yPixelLeftBottom =   ((yCornerBL / rotLeftSideZ  * height) + (height / 2.0));
        double yPixelRightTop =     ((yCornerTR / rotRightSideZ * height) + (height / 2.0));
        double yPixelRightBottom =  ((yCornerBR / rotRightSideZ * height) + (height / 2.0));
        
        double tex1 = 1 / rotLeftSideZ;
        double tex2 = 1 / rotRightSideZ;
        double tex3 = tex30 / rotLeftSideZ;
        double tex4 = tex40 / rotRightSideZ - tex3;
        
        //render every x pixel from the left to the right
        for (int x = xPixelLeftInt; x < xPixelRightInt; x++)
        {
            //rotate pixel
            double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);
            
            //calculate depth
            double zWall = (tex1 + (tex2 - tex1) * pixelRotation);
            
            //if the depth of our zWall is closer to the player than zBufferWall skip this pixel
            if (zBufferWall[x] > zWall)
                continue;
            
            //set the zWall to the buffer so wall is drawn over other current wall depth
            zBufferWall[x] = zWall;
            
            //for locating the pixel in the image pixel array
            int xTexture = (int)((tex3 + tex4 * pixelRotation) / zWall);
            
            //locate the top and bottom y pixels
            double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
            double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation;
            
            //cast to integer
            int yPixelTopInt    = (int)yPixelTop;
            int yPixelBottomInt = (int)yPixelBottom;
            
            //make sure pixel is within window dimension
            if (yPixelTopInt < 0)
                yPixelTopInt = 0;
            
            //make sure pixel is within window dimension
            if (yPixelBottomInt > height)
                yPixelBottomInt = height;
            
            //render every y pixel from the top to the bottom
            for (int y = yPixelTopInt; y < yPixelBottomInt; y++)
            {
                //rotate pixel
                double pixelRotationY = (y - yPixelTop) / (yPixelBottom - yPixelTop);
                
                //for locating the pixel in the image pixel array
                int yTexture = (int)(input.bi.getWidth() * pixelRotationY);
                
                //make sure the index is in boounds
                if (x >= 0 && x <= width && y >=0 && y <= height)
                {
                    //store zBuffer value to help determine pixel brightness (opacity)
                    zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 8;
                    
                    //if the depth is farther than our limit don't render
                    if (zBuffer[x + y * width] > depthLimit)
                    {
                        pixels[x + y * width] = 0;
                    }
                    else
                    {
                        //take the pixel from our image and store in pixel array
                        pixels[x + y * width] = input.temp.pixels[(xTexture & (input.bi.getWidth()-1)) + (yTexture & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                    }

                    //pixels[x + y * width] = input.temp.pixels[(xTexture & 30) + input.bi.getWidth() + (yTexture & 30) * input.bi.getWidth()];
                }
            }
        }
    }
    
    /**
     * This will apply shadow to objects that are farther away
     */
    public void renderDistanceLimiter()
    {
        for (int i=0; i < pixels.length; i++)
        {
            int color = pixels[i];
            
            //the zBuffer contains the depth of objects which will help us determine the brightness
            int brightness = (int)(renderBrightnessDistance / zBuffer[i]);
            
            if (brightness < 0)
                brightness = 0;
            if (brightness > 255)
                brightness = 255;
            
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;
            
            r = r * brightness / 255;
            g = g * brightness / 255;
            b = b * brightness / 255;
            
            pixels[i] = r << 16 | g << 8 | b;
        }
    }
}