package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.hero.Input;
import com.gamesbykevin.wolfenstein.level.*;


import java.util.Random;

public class Display3D extends Render
{
    public double[] zBuffer;
    public double[] zBufferWall;
    
    private double renderDistance = 5000;
    
    private Input input;
    
    private double forward, right, up, walking;
    
    private boolean renderCeiling = false;
    
    private Level level;
    
    public Display3D(final int width, final int height, final Input input)
    {
        super(width, height);
        
        this.zBuffer = new double[width * height];
        this.zBufferWall = new double[width];
        
        this.input = input;
        
        
        this.level = new Level(20, 20);
    }
    
    public void renderFloor()
    {
        for (int x=0; x < width; x++)
        {
            this.zBufferWall[x] = 0;
        }
        
        final double floorPosition  = 10;
        final double ceilingPostion = 10;
        
        //since we have distance limiter the ceiling should not show
        //final double ceilingPostion = 800;
        
        forward  = input.z;   //negative value would move backward
        right    = input.x;   //negative value would move left
        walking = 0;
        up = Math.sin(input.y / 10) * 2;
        
        double z;
        
        final double rotation = input.rotation;
        final double cosine = Math.cos(rotation);
        final double sine = Math.sin(rotation);
        
        for (int y=0; y < height; y++)
        {
            renderCeiling = false;
            
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
                renderCeiling = true;
            }
            
            for (int x=0; x < width; x++)
            {
                double depth = (x - width / 2.0) / height;
                depth *= z;
                
                double xx = depth * cosine + z * sine;
                double yy = z * cosine - depth * sine;
                
                int xPix = (int)(xx + right);
                int yPix = (int)(yy + forward);
                
                zBuffer[x+y*width] = z;
                
                //only render certain depth
                if (z > 500)
                {
                    pixels[x+y*width] = 0;
                }
                else
                {
                    if (!renderCeiling)
                    {
                        //pixels[x+y*width] = input.temp.pixels[(xPix & 30) + (yPix & 30) * input.bi.getWidth()];
                        pixels[x+y*width] = input.temp.pixels[(xPix & (input.bi.getWidth()-1)) + (yPix & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                    }
                    else
                    {
                        //pixels[x+y*width] = input.temp.pixels[(xPix & (input.bi.getWidth()-1)) + (yPix & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                    }
                }
            }
        }
        
        final int size = level.blocks.length;
        
        for (int xBlock = -size; xBlock <= size; xBlock++)
        {
            for (int zBlock = -size + 1; zBlock < size; zBlock++)
            {
                Block block = level.create(xBlock, zBlock);
                
                Block east = level.create(xBlock+1, zBlock);
                Block south = level.create(xBlock, zBlock + 1);
                
                if (block.solid)
                {
                    if (!east.solid)
                        renderWall(xBlock + 1, xBlock + 1, zBlock, zBlock + 1, 0);
                    if (!south.solid)
                        renderWall(xBlock + 1, xBlock, zBlock + 1, zBlock + 1, 0);
                }
                else
                {
                    if (east.solid)
                        renderWall(xBlock + 1, xBlock + 1, zBlock + 1, zBlock, 0);
                    if (south.solid)
                        renderWall(xBlock, xBlock + 1, zBlock + 1, zBlock + 1, 0);
                }
            }
        }
        
        for (int xBlock = -size; xBlock <= size; xBlock++)
        {
            for (int zBlock = -size + 1; zBlock < size; zBlock++)
            {
                Block block = level.create(xBlock, zBlock);
                
                Block east = level.create(xBlock+1, zBlock);
                Block south = level.create(xBlock, zBlock + 1);
                
                if (block.solid)
                {
                    if (!east.solid)
                        renderWall(xBlock + 1, xBlock + 1, zBlock, zBlock + 1, 0.5);
                    if (!south.solid)
                        renderWall(xBlock + 1, xBlock, zBlock + 1, zBlock + 1, 0.5);
                }
                else
                {
                    if (east.solid)
                        renderWall(xBlock + 1, xBlock + 1, zBlock + 1, zBlock, 0.5);
                    if (south.solid)
                        renderWall(xBlock, xBlock + 1, zBlock + 1, zBlock + 1, 0.5);
                }
            }
        }
    }
    
    public void renderSprite(final double x, final double y, final double z, final double heightOffset)
    {
        //copied from method floor()
        final double rotation = input.rotation;
        final double cosine = Math.cos(rotation);
        final double sine = Math.sin(rotation);
        
        final double upCorrect = -0.125;
        final double rightCorrect = 0.0625;
        final double forwardCorrect = 0.0625;
        final double walkCorrect = 0.0625;
        
        double xc = ((x / 2) - (right * rightCorrect)) * 2;
        double yc = ((y / 2) - (up * upCorrect)) * 2 + (walking * walkCorrect) * 2 + heightOffset;
        double zc = ((z / 2) - (forward * forwardCorrect)) * 2;
        
        double rotX = xc * cosine - zc * sine;
        double rotY = yc;
        double rotZ = zc * cosine + xc * sine;
        
        double xCenter = width / 2.0;
        double yCenter = height / 2.0;
        
        double xPixel = rotX / rotZ  * height + xCenter;
        double yPixel = rotY / rotZ  * height + yCenter;
        
        //number is the dimension
        double xPixelL = xPixel - input.bi.getWidth() / rotZ;
        double xPixelR = xPixel + input.bi.getWidth() / rotZ;
        
        //number is the dimension
        double yPixelL = yPixel - input.bi.getWidth() / rotZ;
        double yPixelR = yPixel + input.bi.getWidth() / rotZ;
        
        int xpl = (int)xPixelL;
        int xpr = (int)xPixelR;
        int ypl = (int)yPixelL;
        int ypr = (int)yPixelR;
        
        if (xpl < 0)
            xpl = 0;
        if (xpr > width)
            xpr = width;
        if (ypl < 0)
            ypl = 0;
        if (ypr > height)
            ypr = height;
        
        rotZ *= 8;
        
        for (int yp = ypl; yp < ypr; yp++)
        {
            double pixelRotationY = (yp - yPixelR) / (yPixelL - yPixelR);
            int yTexture = (int)(pixelRotationY * input.bi.getWidth());
            
            for (int xp = xpl; xp < xpr; xp++)
            {
                double pixelRotationX = (xp - xPixelR) / (xPixelL - xPixelR);
                int xTexture = (int)(pixelRotationX * input.bi.getWidth());
                
                if (zBuffer[xp + yp * width] > rotZ)
                {
                    int color = input.temp.pixels[(xTexture & (input.bi.getWidth()-1)) + (yTexture & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                    
                    //don't render transparent pixels
                    if (color != 0xffff00ff)
                    {
                        pixels[xp + yp * width] = color;
                        zBuffer[xp + yp * width] = rotZ;
                    }
                }
            }
        }
    }
    
    public void renderWall(final double xLeft, final double xRight, final double zDistanceLeft, final double zDistanceRight, final double yHeight)
    {
        //copied from method floor()
        final double rotation = input.rotation;
        final double cosine = Math.cos(rotation);
        final double sine = Math.sin(rotation);
        
        
        
        
        
        
        final double upCorrect = 0.0625;
        final double rightCorrect = 0.0625;
        final double forwardCorrect = 0.0625;
        final double walkCorrect = -0.0625;
        
        double xcLeft = ((xLeft/2) - (right * rightCorrect)) * 2;
        double zcLeft = ((zDistanceLeft/2) - (forward * forwardCorrect)) * 2;
        
        double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
        double yCornerTL = ((-yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        double yCornerBL = ((+0.5 - yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;
        
        double xcRight = ((xRight/2) - (right * rightCorrect)) * 2;
        double zcRight = ((zDistanceRight/2) - (forward * forwardCorrect)) * 2;
        
        double rotRightSideX = xcRight * cosine - zcRight * sine;
        double yCornerTR = ((yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        double yCornerBR = ((+0.5 - yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        double rotRightSideZ = zcRight * cosine + xcRight * sine;
        
        double tex30 = 0;
        double tex40 = input.bi.getWidth();//8;
        double clip = 0.5;
        
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
        
        double yPixelLeftTop =      (yCornerTL / rotLeftSideZ  * height + height / 2.0);
        double yPixelLeftBottom =   (yCornerBL / rotLeftSideZ  * height + height / 2.0);
        double yPixelRightTop =     (yCornerTR / rotRightSideZ * height + height / 2.0);
        double yPixelRightBottom =  (yCornerBR / rotRightSideZ * height + height / 2.0);
        
        double tex1 = 1 / rotLeftSideZ;
        double tex2 = 1 / rotRightSideZ;
        double tex3 = tex30 / rotLeftSideZ;
        double tex4 = tex40 / rotRightSideZ - tex3;
        
        for (int x = xPixelLeftInt; x < xPixelRightInt; x++)
        {
            double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);
            
            double zWall = (tex1 + (tex2 - tex1) * pixelRotation);
            
            if (zBufferWall[x] > zWall)
                continue;
            
            zBufferWall[x] = zWall;
            
            int xTexture = (int)((tex3 + tex4 * pixelRotation) / zWall);
            
            double yPixelTop = yPixelLeftTop + (yPixelRightTop - yPixelLeftTop) * pixelRotation;
            double yPixelBottom = yPixelLeftBottom + (yPixelRightBottom - yPixelLeftBottom) * pixelRotation;
            
            int yPixelTopInt = (int)yPixelTop;
            int yPixelBottomInt = (int)yPixelBottom;
            
            if (yPixelTopInt < 0)
                yPixelTopInt = 0;
            
            if (yPixelBottomInt > height)
                yPixelBottomInt = height;
            
            for (int y = yPixelTopInt; y < yPixelBottomInt; y++)
            {
                double pixelRotationY = (y - yPixelTop) / (yPixelBottom - yPixelTop);
                
                int yTexture = (int)(input.bi.getWidth() * pixelRotationY);
                
                
                //pixels[x + y * width] = 0x1B91E0;
                //pixels[x + y * width] = xTexture * 100 + yTexture * 100 * 256;
                pixels[x+y*width] = input.temp.pixels[(xTexture & (input.bi.getWidth()-1)) + (yTexture & (input.bi.getWidth()-1)) * input.bi.getWidth()];
                //pixels[x+y*width] = input.temp.pixels[(xTexture & 30) + input.bi.getWidth() + (yTexture & 30) * input.bi.getWidth()];
                zBuffer[x + y * width] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 8;
            }
        }
    }
    
    /**
     * This will make the color in the distance fade out
     */
    public void renderDistanceLimiter()
    {
        for (int i=0; i < width * height; i++)
        {
            int color = pixels[i];
            int brightness = (int)(renderDistance / zBuffer[i]);
            
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