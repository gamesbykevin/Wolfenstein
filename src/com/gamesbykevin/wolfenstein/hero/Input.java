package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.wolfenstein.display.Render;
import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.level.Block;
import com.gamesbykevin.wolfenstein.level.Level;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;

public final class Input extends Sprite
{
    //location of player
    private double xa, za, rotation, rotationa;
    
    //store the xs and zs when we don't have collision
    private double xs = getX(), zs = getZ();
    
    private boolean walking = false, running = false;
    
    private int count = 0;
    
    /**
     * Here is all of our input options
     */
    private enum InputOptions
    {
        WalkForward(KeyEvent.VK_UP),
        WalkBackward(KeyEvent.VK_DOWN),
        StrafeLeft(KeyEvent.VK_A),
        StrafeRight(KeyEvent.VK_D),
        TurnLeft(KeyEvent.VK_LEFT),
        TurnRight(KeyEvent.VK_RIGHT),
        Crouch(KeyEvent.VK_Z),
        Jump(KeyEvent.VK_SPACE),
        Run(KeyEvent.VK_X);
        
        private final int key;
        
        private InputOptions(final int key)
        {
            this.key = key;
        }
        
        private int getKey()
        {
            return this.key;
        }
    }
    
    protected Input()
    {
        
    }
    
    public void update(final Keyboard keyboard)
    {
        double xMove = 0;
        double zMove = 0;
        double rotationSpeed = 0.055;
        double jumpHeight = 1;
        double crouchHeight = 2;
        double walkSpeed = 1;
        
        for (InputOptions inputOption : InputOptions.values())
        {
            if (keyboard.hasKeyReleased(inputOption.getKey()))
            {
                keyboard.removeKeyPressed(inputOption.getKey());
                keyboard.removeKeyReleased(inputOption.getKey());
            }
        }
        
        boolean right = keyboard.hasKeyPressed(InputOptions.StrafeRight.getKey());
        boolean left = keyboard.hasKeyPressed(InputOptions.StrafeLeft.getKey());
        boolean up = keyboard.hasKeyPressed(InputOptions.WalkForward.getKey());
        boolean down = keyboard.hasKeyPressed(InputOptions.WalkBackward.getKey());
        boolean turnLeft = keyboard.hasKeyPressed(InputOptions.TurnLeft.getKey());
        boolean turnRight = keyboard.hasKeyPressed(InputOptions.TurnRight.getKey());
        boolean jump = keyboard.hasKeyPressed(InputOptions.Jump.getKey());
        boolean crouch = keyboard.hasKeyPressed(InputOptions.Crouch.getKey());
        boolean run = keyboard.hasKeyPressed(InputOptions.Run.getKey());
        
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
        
        //this is to simulate the heroes head moving up and down while walking
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
            setY(getY() + jumpHeight);
            run = false;
        }
        
        if (crouch)
        {
            setY(getY() - crouchHeight);
            run = false;
            walkSpeed = 0.5;
        }
        
        running = false;
        
        //if we are running increase walkSpeed
        if (run)
        {
            walkSpeed = 2.5;
            running = true;
        }
        
        xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation)) * walkSpeed;
        za += (zMove * Math.cos(rotation) - xMove * Math.sin(rotation)) * walkSpeed;
        
        //place at new location
        setX(getX() + xa);
        setZ(getZ() + za);
        
        //determine which block the player is at for collision detection
        int xLoc = (int)((getX() + (xa*1.75)) / 16);
        int zLoc = (int)((getZ() + (za*1.75)) / 16);
        
        //do we have collision
        /*
        if (hasCollision(engine.getManager().screen.render3d.level, xLoc, zLoc))
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
        */
        
        xa *= 0.1;
        za *= 0.1;
        
        setY(getY() * .9);
        
        rotation += rotationa;
        rotationa *= 0.5;
    }
    
    public double getRotation()
    {
        return rotation;
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public boolean isWalking()
    {
        return walking;
    }
    
    /**
     * Get the current count so we can apply animation
     * @return the current count
     */
    public int getCount()
    {
        return count;
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