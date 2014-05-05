package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.wolfenstein.hero.Input;
import com.gamesbykevin.wolfenstein.level.*;

public class Render3D extends Render
{
    //our array that stores the depth of the pixels so we can apply opacity etc..
    private double[] zBuffer;
    
    //our zBufferWall will store the depth of the walls so we can determine the closest wall to the player to render
    private double[] zBufferWall;
    
    /**
     * The depth limit we use to determine what is clipped
     */
    public static final double CLIP = .333;
    
    //determine the brightness depending on depth, the higher the value the more brighter
    private final double renderBrightnessDistance = 10000;
    
    //render pixels within a certain depth
    private final double depthLimit = 300.0;
    
    //the height of the ceiling and floor
    private final double floorPosition  = 8;
    private final double ceilingPostion = 8;
    
    //when rendering a door adjust depth for doors
    private final double doorDepth = 0.5;
    
    //when rendering a door adjust depth for secret just a little so the door is not drawn over the walls
    private final double doorSecretDepth = 0.01;    
    
    //the number of blocks the object needs to be within range in order to be rendered
    private final int renderRange = 40;
    
    //temporary texture object
    private Texture tmpTexture;
    
    //store hero input variables because all 3d objects will be rendered around the hero
    private double forward, right, up, walking, sine, cosine, rotation;
    private int count, playerX = 0, playerZ = 0;
    private boolean isWalking, isRunning;
    
    /**
     * Create our object that is responsible for rendering all 3d objects
     * @param width Width of the entire window
     * @param height Height of the entire window
     */
    public Render3D(final int width, final int height) throws Exception
    {
        super(width, height);
        
        this.zBuffer = new double[width * height];
        this.zBufferWall = new double[width];
    }
    
    /**
     * Set the heroes current location/information etc..<br>
     * Update level status
     * @param input Hero input object
     * @param time The time to deduct per update (nano-seconds)
     */
    public void update(final Input input)
    {
        //negative value would move backward
        forward  = input.getZ();
        
        //negative value would move left
        right    = input.getX();
        
        //set the players current position
        playerX = input.getPlayerX();
        playerZ = input.getPlayerZ();
        
        //set to 0 at first
        walking = 0;
        
        //height, negative value would move down
        up = Math.sin(input.getY() / 10) * 2;
        
        //the direction the hero is facing
        rotation = input.getRotation();
        sine = Math.sin(rotation);
        cosine = Math.cos(rotation);
        
        //this is constantly changing for animation purposes
        count = input.getCount();
        
        isWalking = input.isWalking();
        isRunning = input.isRunning();
    }
    
    /**
     * Render the floor and ceiling.<br> 
     * We do both here for optimization purposes.
     * @param textures Collection of textures.
     */
    public void renderTopBottom(final Textures textures)
    {
        //our depth for each floor/ceiling piece
        double z;
        
        //are we dealing with the floor
        boolean floor;
        
        for (int y=0; y < getHeight(); y++)
        {
            //at first assume we are rendering the floor
            floor = true;
            
            double ceiling = (y - getHeight() / 2.0) / getHeight();
            
            walking = Math.sin(count / 6.0) * 0.8;
            
            z = (floorPosition + up + walking) / ceiling;
            
            if (isRunning)
            {
                walking = Math.sin(count / 6.0) * 2;
                z = (floorPosition + up + walking) / ceiling;
            }
            
            if (!isWalking)
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
            
            for (int x=0; x < getWidth(); x++)
            {
                double depth = (x - getWidth() / 2.0) / getHeight();
                depth *= z;
                
                double xx = depth * cosine + z * sine;
                double yy = z * cosine - depth * sine;
                
                int xPix = (int)(xx + right);
                int yPix = (int)(yy + forward);
                
                final int index = x + y * getWidth();
                
                zBuffer[index] = z;
                
                //only render pixels certain depth
                if (z > depthLimit)
                {
                    getPixels()[index] = 0;
                }
                else
                {
                    if (floor)
                    {
                        tmpTexture = textures.getTexture(Textures.Key.FloorWood);
                        
                        //render floor
                        getPixels()[index] = tmpTexture.getPixels()[(xPix & (tmpTexture.getWidth() - 1)) + (yPix & (tmpTexture.getWidth()-1)) * tmpTexture.getWidth()];
                    }
                    else
                    {
                        tmpTexture = textures.getTexture(Textures.Key.Ceiling1);
                        
                        //render ceiling
                        getPixels()[index] = tmpTexture.getPixels()[(xPix & (tmpTexture.getWidth()-1)) + (yPix & (tmpTexture.getWidth()-1)) * tmpTexture.getWidth()];
                    }
                }
            }
        }
    }
    
    /**
     * Draw our walls
     */
    public void renderWalls(final Textures textures, final Level level)
    {
        //reset our wall depth buffer to 0
        for (int x=0; x < zBufferWall.length; x++)
        {
            this.zBufferWall[x] = 0;
        }
        
        //we do we start and end
        final int startX = playerX - renderRange;
        final int endX = playerX + renderRange;
        final int startZ = playerZ - renderRange;
        final int endZ = playerZ + renderRange;
        
        //check blocks in render range
        for (int xBlock = startX; xBlock < endX; xBlock++)
        {
            //stay in bounds
            if (xBlock < 0 || xBlock >= level.getCols() || !hasRangeX(xBlock))
                continue;
            
            for (int zBlock = startZ; zBlock < endZ; zBlock++)
            {
                //stay in bounds
                if (zBlock < 0 || zBlock >= level.getRows() || !hasRangeZ(zBlock))
                    continue;
                
                final double extra = 1;
                
                Block block = level.get(xBlock, zBlock);
                
                Block east = level.get(xBlock + extra, zBlock);
                Block south = level.get(xBlock, zBlock + extra);
                
                if (block.isSolid())
                {
                    //draw west wall
                    if (!east.isSolid())
                    {
                        if (block.isDoor())
                        {
                            //get progress for animation
                            float progress = getProgress(block);
                            
                            if (!block.getDoor().isSecret())
                            {
                                //increase depth for the door
                                renderWall(xBlock + extra - doorDepth, xBlock + extra - doorDepth, zBlock + (extra * progress), zBlock + extra + (extra * progress), 0.5, textures.getTexture(block.getEast()));
                            }
                            else
                            {
                                //increase depth very little
                                renderWall(xBlock + extra - doorSecretDepth, xBlock + extra - doorSecretDepth, zBlock + (extra * progress), zBlock + extra + (extra * progress), 0.5, textures.getTexture(block.getEast()));
                            }
                        }
                        else
                        {
                            renderWall(xBlock + extra, xBlock + extra, zBlock, zBlock + extra, 0.5, textures.getTexture(block.getEast()));
                        }
                    }
                    else
                    {
                        if (block.isDoor())
                            renderWall(xBlock + extra, xBlock + extra, zBlock + extra, zBlock, 0.5, textures.getTexture(east.getWest()));
                        
                        //draw side wall of door
                        if (east.isDoor())
                            renderWall(xBlock + extra, xBlock + extra, zBlock, zBlock + extra, 0.5, textures.getTexture(block.getEast()));
                    }
                    
                    //draw north wall
                    if (!south.isSolid())
                    {
                        if (block.isDoor())
                        {
                            //get progress for animation
                            float progress = getProgress(block);
                            
                            if (!block.getDoor().isSecret())
                            {
                                //increase depth for the door
                                renderWall(xBlock + extra + (extra * progress), xBlock + (extra * progress), zBlock + extra - doorDepth, zBlock + extra - doorDepth, 0.5, textures.getTexture(block.getSouth()));
                            }
                            else
                            {
                                //increase depth very little
                                renderWall(xBlock + extra + (extra * progress), xBlock + (extra * progress), zBlock + extra - doorSecretDepth, zBlock + extra - doorSecretDepth, 0.5, textures.getTexture(block.getSouth()));
                            }
                        }
                        else
                        {
                            renderWall(xBlock + extra, xBlock, zBlock + extra, zBlock + extra, 0.5, textures.getTexture(block.getSouth()));
                        }
                    }
                    else
                    {
                        //draw side wall of door
                        if (block.isDoor())
                            renderWall(xBlock, xBlock + extra, zBlock + extra, zBlock + extra, 0.5, textures.getTexture(south.getNorth()));
                        
                        //draw side wall of door
                        if (south.isDoor())
                            renderWall(xBlock + extra, xBlock, zBlock + extra, zBlock + extra, 0.5, textures.getTexture(block.getSouth()));
                    }
                }
                else
                {
                    //draw east wall
                    if (east.isSolid())
                    {
                        if (east.isDoor())
                        {
                            //get progress for animation
                            float progress = getProgress(east);
                            
                            if (!east.getDoor().isSecret())
                            {
                                //increase depth for the door
                                renderWall(xBlock + extra + doorDepth, xBlock + extra + doorDepth, zBlock + extra + (extra * progress), zBlock + (extra * progress), 0.5, textures.getTexture(east.getWest()));
                            }
                            else
                            {
                                //increase depth very little
                                renderWall(xBlock + extra + doorSecretDepth, xBlock + extra + doorSecretDepth, zBlock + extra + (extra * progress), zBlock + (extra * progress), 0.5, textures.getTexture(east.getWest()));
                            }
                        }
                        else
                        {
                            renderWall(xBlock + extra, xBlock + extra, zBlock + extra, zBlock, 0.5, textures.getTexture(east.getWest()));
                        }
                    }
                    
                    //draw south wall
                    if (south.isSolid())
                    {
                        if (south.isDoor())
                        {
                            //get progress for animation
                            float progress = getProgress(south);
                            
                            if (!south.getDoor().isSecret())
                            {
                                //increase depth for the door
                                renderWall(xBlock + (extra * progress), xBlock + extra + (extra * progress), zBlock + extra + .5, zBlock + extra + .5, 0.5, textures.getTexture(south.getNorth()));
                            }
                            else
                            {
                                //increase depth very little
                                renderWall(xBlock + (extra * progress), xBlock + extra + (extra * progress), zBlock + extra + doorSecretDepth, zBlock + extra + doorSecretDepth, 0.5, textures.getTexture(south.getNorth()));
                            }
                        }
                        else
                        {
                            renderWall(xBlock, xBlock + extra, zBlock + extra, zBlock + extra, 0.5, textures.getTexture(south.getNorth()));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Get the progress of the door timer towards completion.<br>
     * If not a door then 0 will be returned.
     * @param block The block we want to check
     * @return percent complete ranging from 0.0 - 1.0 (0% - 100%)
     */
    private float getProgress(final Block block)
    {
        
        float progress = 0f;
        
        //if not a door there has been no progress
        if (!block.isDoor())
            return progress;
        
        if (block.getDoor().isClosed())
            progress = 0f;
        if (block.getDoor().isOpen())
            progress = 1f;
        if (block.getDoor().isClosing())
            progress = 1f - block.getDoor().getProgress();
        if (block.getDoor().isOpening())
            progress = block.getDoor().getProgress();
        
        return progress;
    }
    
    /**
     * Is the parameter provided within range to be considered for rendering.
     * @param cell The x location of the object we want to check
     * @return true if the x location provided is close enough to the player to be rendered, false otherwise
     */
    private boolean hasRangeX(final int cell)
    {
        return (cell - (right/16) <= renderRange && cell - (right/16) >= -renderRange);
    }
    
    /**
     * Is the parameter provided within range to be considered for rendering.
     * @param cell The z location of the object we want to check
     * @return true if the z location provided is close enough to the player to be rendered, false otherwise
     */
    private boolean hasRangeZ(final int cell)
    {
        return (cell - (forward/16) <= renderRange && cell - (forward/16) >= -renderRange);
    }
    
    /**
     * Draw Sprite at the specified location
     * @param x x-coordinate
     * @param y y-coordinate (height)
     * @param z z-coordinate (depth)
     * @param heightOffset Offset variable for the height
     * @param tmpPixels Pixel array containing image data
     * @param imageWidth Width of original 2d image
     * @param imageHeight Height of original 2d image
     */
    public void renderSprite(final double x, final double y, final double z, final double heightOffset, final int[] tmpPixels, final int imageWidth, final int imageHeight)
    {
        //only sprites within a certain range will be rendered
        if (!hasRangeZ((int)z))
            return;
        
        //only sprites within a certain range will be rendered
        if (!hasRangeX((int)x))
            return;
                
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
        double xCenter = getWidth() / 2.0;
        double yCenter = getHeight() / 2.0;
        
        //locate where the sprite is to the center
        double xPixel = rotX / rotZ  * getHeight() + xCenter;
        double yPixel = rotY / rotZ  * getHeight() + yCenter;
        
        //scale so the sprite will be bigger
        final double scale = 6.0;
        
        //the left and right x pixels, manipulate these to increase the width of the sprite
        double xPixelL = xPixel - (imageWidth * scale) / rotZ;
        double xPixelR = xPixel + (imageWidth * scale) / rotZ;
        
        //the top and bottom y pixels manipulate these to increase the height of the sprite
        double yPixelU = yPixel - (imageHeight * scale) / rotZ;
        double yPixelD = yPixel + (imageHeight * scale) / rotZ;
        
        //convert to int
        int xpl = (int)xPixelL;
        int xpr = (int)xPixelR;
        int ypu = (int)yPixelU;
        int ypd = (int)yPixelD;
        
        //make sure we are within window boundary
        if (xpl < 0)
            xpl = 0;
        if (xpr > getWidth())
            xpr = getWidth();
        if (ypu < 0)
            ypu = 0;
        if (ypd > getHeight())
            ypd = getHeight();
        
        //this affects brightness
        rotZ *= 8;
        
        //fill in sprite pixels to our destination
        for (int yp = ypu; yp < ypd; yp++)
        {
            double pixelRotationY = (yp - yPixelU) / (yPixelD - yPixelU);
            int yTexture = (int)(pixelRotationY * imageHeight);
            
            for (int xp = xpl; xp < xpr; xp++)
            {
                double pixelRotationX = (xp - xPixelL) / (xPixelR - xPixelL);
                int xTexture = (int)(pixelRotationX * imageWidth);
                
                final int index = xp + yp * getWidth();
                
                //if the pixel is closer than the one than our zBuffer
                if (zBuffer[index] > rotZ)
                {
                    //get the color of a specific pixel
                    int color = tmpPixels[(xTexture & (imageWidth-1)) + (yTexture & (imageHeight-1)) * imageWidth];
                    
                    //don't render transparent pixels
                    if (color != 0xffff00ff && color != 0 && color != -16777216)
                    {
                        getPixels()[index] = color;
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
     * @param wallTexture The image of the wall that we want to draw
     */
    public void renderWall(final double xLeft, final double xRight, final double zDistanceLeft, final double zDistanceRight, final double yHeight, final Texture wallTexture)
    {
        //don't render if there is no texture
        if (wallTexture == null)
            return;
        
        final double upCorrect = 0.0625;
        final double rightCorrect = 0.0625;
        final double forwardCorrect = 0.0625;
        final double walkCorrect = -0.0625;
        
        double xcLeft = (xLeft - (right * rightCorrect)) * 2;
        double zcLeft = (zDistanceLeft - (forward * forwardCorrect)) * 2;
        
        double rotLeftSideX = xcLeft * cosine - zcLeft * sine;
        double yCornerTL = ((-yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        //this will increase the texture height
        double yCornerBL = ((1 - yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        double rotLeftSideZ = zcLeft * cosine + xcLeft * sine;
        
        double xcRight = (xRight - (right * rightCorrect)) * 2;
        double zcRight = (zDistanceRight - (forward * forwardCorrect)) * 2;
        
        double rotRightSideX = xcRight * cosine - zcRight * sine;
        double yCornerTR = ((-yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        //this will increase the texture height
        double yCornerBR = ((1 - yHeight) - (-up * upCorrect + (walking * walkCorrect))) * 2;
        
        double rotRightSideZ = zcRight * cosine + xcRight * sine;
        
        //if both sides are going to be clipped don't bother rendering the wall
        if (rotLeftSideZ < CLIP && rotRightSideZ < CLIP)
            return;
        
        double tex30 = 0;
        double tex40 = wallTexture.getWidth();
        
        //for clipping so walls aren't infinitely drawn
        if (rotLeftSideZ < CLIP)
        {
            double clip0 = (CLIP - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
            rotLeftSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
            rotLeftSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
            tex30 = tex30 + (tex40 - tex30) * clip0;
        }
        
        //for clipping so walls aren't infinitely drawn
        if (rotRightSideZ < CLIP)
        {
            double clip0 = (CLIP - rotLeftSideZ) / (rotRightSideZ - rotLeftSideZ);
            rotRightSideZ = rotLeftSideZ + (rotRightSideZ - rotLeftSideZ) * clip0;
            rotRightSideX = rotLeftSideX + (rotRightSideX - rotLeftSideX) * clip0;
            tex30 = tex30 + (tex40 - tex30) * clip0;
        }
        
        //calculate all corner x pixels of this wall texture, left side will have the same 2 xPixelLeft and same for the 2 xPixelRight
        double xPixelLeft  = (rotLeftSideX  / rotLeftSideZ  * getHeight() + getWidth() / 2);
        double xPixelRight = (rotRightSideX / rotRightSideZ * getHeight() + getWidth() / 2);
        
        //don't draw because it would overlap
        if (xPixelLeft >= xPixelRight)
            return;
        
        int xPixelLeftInt = (int)xPixelLeft;
        int xPixelRightInt = (int)xPixelRight;
        
        //keep in boundary of screen
        if (xPixelLeftInt < 0)
            xPixelLeftInt = 0;
        
        //keep in boundary of screen
        if (xPixelRightInt > getWidth())
            xPixelRightInt = getWidth();
        
        //calculate all 4 corner y pixels of this wall texture
        double yPixelLeftTop =      ((yCornerTL / rotLeftSideZ  * getHeight()) + (getHeight() / 2.0));
        double yPixelLeftBottom =   ((yCornerBL / rotLeftSideZ  * getHeight()) + (getHeight() / 2.0));
        double yPixelRightTop =     ((yCornerTR / rotRightSideZ * getHeight()) + (getHeight() / 2.0));
        double yPixelRightBottom =  ((yCornerBR / rotRightSideZ * getHeight()) + (getHeight() / 2.0));
        
        double tex1 = 1.0 / rotLeftSideZ;
        double tex2 = 1.0 / rotRightSideZ;
        double tex3 = tex30 / rotLeftSideZ;
        double tex4 = tex40 / rotRightSideZ - tex3;
        
        //render every x pixel from the left to the right
        for (int x = xPixelLeftInt; x < xPixelRightInt; x++)
        {
            //rotate pixel
            double pixelRotation = (x - xPixelLeft) / (xPixelRight - xPixelLeft);
            
            //calculate depth
            double zWall = (tex1 + (tex2 - tex1) * pixelRotation);
            
            //if the depth of our zBufferWall is greater than the current depth zWall skip this pixel
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
            if (yPixelBottomInt > getHeight())
                yPixelBottomInt = getHeight();
            
            //render every y pixel from the top to the bottom
            for (int y = yPixelTopInt; y < yPixelBottomInt; y++)
            {
                //rotate pixel
                double pixelRotationY = (y - yPixelTop) / (yPixelBottom - yPixelTop);
                
                //for locating the pixel in the image pixel array
                int yTexture = (int)(wallTexture.getWidth() * pixelRotationY);
                
                //make sure the index is in boounds
                if (x >= 0 && x <= getWidth() && y >=0 && y <= getHeight())
                {
                    //store zBuffer value to help determine pixel brightness (opacity)
                    zBuffer[x + y * getWidth()] = 1 / (tex1 + (tex2 - tex1) * pixelRotation) * 8;
                    
                    //if the depth is farther than our limit don't render
                    if (zBuffer[x + y * getWidth()] > depthLimit)
                    {
                        getPixels()[x + y * getWidth()] = 0;
                    }
                    else
                    {
                        //take the pixel from our image and store in pixel array
                        getPixels()[x + y * getWidth()] = wallTexture.getPixels()[(xTexture & (wallTexture.getWidth()-1)) + (yTexture & (wallTexture.getWidth()-1)) * wallTexture.getWidth()];
                    }
                }
            }
        }
    }
    
    /**
     * This will apply different level of brightness depending on our depth
     */
    public void renderDistanceLimiter()
    {
        for (int i=0; i < getPixels().length; i++)
        {
            /*
            int color = getPixels()[i];
            
            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;
            
            getPixels()[i] = r << 16 | g << 8 | b;
            */
            
            int color = getPixels()[i];
            
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
            
            getPixels()[i] = r << 16 | g << 8 | b;
        }
    }
}