package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.wolfenstein.display.Render;
import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.level.Block;
import com.gamesbykevin.wolfenstein.level.Level;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

public final class Input
{
    public double xa, x = 40, za, z = 40, y, rotation, rotationa;
    
    //store the xs and zs when we don't have collision
    public double xs = x, zs = z;
    
    public Render temp;
    
    public BufferedImage bi;
    
    public boolean walking = false;

    public boolean runWalk = false;
    
    public int count = 0;
    
    public Input(final Image image) throws Exception
    {
        final int width = 64;
        final int height = 64;
        
        //make it a multiple of 8 to render correctly
        final int textureWidth = 64;
        final int textureHeight = 64;
        
        temp = new Render(textureWidth, textureHeight);
        
        //create new buffered image
        bi = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_RGB);
        
        final int startX = 0;
        final int startY = 0;
        
        //write image to buffered image
        bi.getGraphics().drawImage(image, 0, 0, textureWidth, textureHeight, startX, startY, startX + width, startY + height, null);
        
        //copy array data to temp pixels
        bi.getRGB(0, 0, textureWidth, textureHeight, temp.pixels, 0, textureWidth);
    }
    
    public void update(final Engine engine)
    {
        double xMove = 0;
        double zMove = 0;
        double rotationSpeed = 0.055;
        double jumpHeight = 1;
        double crouchHeight = 2;
        double walkSpeed = 1;
        
        Keyboard keyboard = engine.getKeyboard();
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_RIGHT))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_RIGHT);
            keyboard.removeKeyReleased(KeyEvent.VK_RIGHT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_LEFT))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_LEFT);
            keyboard.removeKeyReleased(KeyEvent.VK_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_UP))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_UP);
            keyboard.removeKeyReleased(KeyEvent.VK_UP);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_DOWN))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_DOWN);
            keyboard.removeKeyReleased(KeyEvent.VK_DOWN);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_A))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_A);
            keyboard.removeKeyReleased(KeyEvent.VK_A);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_D))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_D);
            keyboard.removeKeyReleased(KeyEvent.VK_D);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_SPACE))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_SPACE);
            keyboard.removeKeyReleased(KeyEvent.VK_SPACE);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_Z))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_Z);
            keyboard.removeKeyReleased(KeyEvent.VK_Z);
        }
        
        if (keyboard.hasKeyReleased(KeyEvent.VK_X))
        {
            keyboard.removeKeyPressed(KeyEvent.VK_X);
            keyboard.removeKeyReleased(KeyEvent.VK_X);
        }
        
        boolean right = keyboard.hasKeyPressed(KeyEvent.VK_RIGHT);
        boolean left = keyboard.hasKeyPressed(KeyEvent.VK_LEFT);
        boolean up = keyboard.hasKeyPressed(KeyEvent.VK_UP);
        boolean down = keyboard.hasKeyPressed(KeyEvent.VK_DOWN);
        boolean turnLeft = keyboard.hasKeyPressed(KeyEvent.VK_A);
        boolean turnRight = keyboard.hasKeyPressed(KeyEvent.VK_D);
        boolean jump = keyboard.hasKeyPressed(KeyEvent.VK_SPACE);
        boolean crouch = keyboard.hasKeyPressed(KeyEvent.VK_Z);
        boolean run = keyboard.hasKeyPressed(KeyEvent.VK_X);
        
        walking = false;
        
        if (up)
        {
            zMove++;
            walking = true;
        }
        
        if (down)
        {
            zMove--;
            walking = true;
        }
        
        if (up || down)
            count++;
        
        if (left)
            xMove--;
        
        if (right)
            xMove++;
        
        if (turnLeft)
            rotationa -= rotationSpeed;
        
        if (turnRight)
            rotationa += rotationSpeed;
        
        if (jump)
        {
            y += jumpHeight;
            run = false;
        }
        
        if (crouch)
        {
            y -= crouchHeight;
            run = false;
            walkSpeed = 0.5;
        }
        
        runWalk = false;
        
        if (run)
        {
            walkSpeed = 3;
            runWalk = true;
        }
        
        xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation)) * walkSpeed;
        za += (zMove * Math.cos(rotation) - xMove * Math.sin(rotation)) * walkSpeed;
        
        x += xa;
        z += za;
        
        //determine which block the player is at for collision detection
        int xLoc = (int)((x + (xa*1.75)) / 8);
        int zLoc = (int)((z + (za*1.75)) / 8);
        
        //do we have collision
        if (hasCollision(engine.getManager().screen.display3d.level, xLoc, zLoc))
        {
            //restore safe location
            x = xs;
            z = zs;
        }
        else
        {
            //store safe location
            xs = x;
            zs = z;
        }
        
        xa *= 0.1;
        za *= 0.1;
        
        y *= 0.9;
        
        rotation += rotationa;
        rotationa *= 0.5;
        
        //System.out.println("xLoc = " + xLoc + ", zLoc = " + zLoc);
    }
    
    /**
     * Check for collision
     * @param level The level that contains the blocks
     * @param xLoc x location
     * @param zLoc z location
     * @return true if we hit a wall, false otherwise
     */
    private boolean hasCollision(final Level level, final int xLoc, final int zLoc)
    {
        final double extra = 1;
        
        //block to the east
        Block e = level.create(xLoc+extra, zLoc);
        
        //block to the west
        Block w = level.create(xLoc-extra, zLoc);
        
        //block to the north
        Block n = level.create(xLoc, zLoc-extra);
        
        //block to the south
        Block s = level.create(xLoc, zLoc+extra);
        
        //block to the north east
        Block ne = level.create(xLoc+extra, zLoc-extra);
        
        //block to the north west
        Block nw = level.create(xLoc-extra, zLoc-extra);
        
        //block to the south east
        Block se = level.create(xLoc+extra, zLoc+extra);
        
        //block to the south west
        Block sw = level.create(xLoc-extra, zLoc+extra);
        
        //center
        Block c = level.create(xLoc, zLoc);
        
        //if any of the blocks are solid we have collision
        return (e.solid || w.solid || n.solid || s.solid || c.solid || ne.solid || nw.solid || se.solid || sw.solid);
    }
}