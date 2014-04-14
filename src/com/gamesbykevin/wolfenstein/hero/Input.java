package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.wolfenstein.level.Block;
import com.gamesbykevin.wolfenstein.level.Level;

import java.awt.event.KeyEvent;

public final class Input extends Sprite
{
    //location of player
    private double xa, za, rotation, rotationa;
    
    //store the xs and zs when we don't have collision
    private double xs = getX(), zs = getZ();
    
    private boolean walking = false, running = false;
    
    private int count = 0;
    
    //the speed the player moves while crouching
    //private final double speedCrouch = 0.5;
    
    //the speed the player moves while running
    private final double speedRun    = 2.5;
    
    //the speed the player moves while walking
    private final double speedWalk   = 1;
    
    //how fast can the player turn
    private final double rotationSpeed = 0.055;
    
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
    
    public void update(final Keyboard keyboard, final Level level) throws Exception
    {
        double xMove = 0;
        double zMove = 0;
        //double jumpHeight = 1;
        //double crouchHeight = 2;
        double speed = speedWalk;
        
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
        //boolean jump = keyboard.hasKeyPressed(InputOptions.Jump.getKey());
        //boolean crouch = keyboard.hasKeyPressed(InputOptions.Crouch.getKey());
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
        //if (up || down)
        //    count++;
        
        if (left)
            xMove--;
        
        if (right)
            xMove++;
        
        if (turnLeft)
            rotationa -= rotationSpeed;
        
        if (turnRight)
            rotationa += rotationSpeed;
        
        /*
        if (jump)
        {
            setY(getY() + jumpHeight);
            run = false;
        }
        */
        
        /*
        //this is to crouch the player
        if (crouch)
        {
            setY(getY() - crouchHeight);
            run = false;
            speed = speedCrouch;
        }
        */
        
        running = false;
        
        //if we are running increase walkSpeed
        if (run)
        {
            speed = speedRun;
            running = true;
        }
        
        //calculate the additional space moved
        xa += (xMove * Math.cos(rotation) + zMove * Math.sin(rotation)) * speed;
        za += (zMove * Math.cos(rotation) - xMove * Math.sin(rotation)) * speed;
        
        //predict where the player will be next
        int newX = (int)((getX() + xa) / 16);
        int newZ = (int)((getZ() + za) / 16);
        
        //where the player is currently
        int originalX = (int)(getX() / 16);
        int originalZ = (int)(getZ() / 16);
        
        //just check if x collision has occurred
        final boolean xCollision = hasCollision(level, newX, originalZ);
        
        //just check if z collision has occurred
        final boolean zCollision = hasCollision(level, originalX, newZ);
        
        if (!xCollision)
        {
            setX(getX() + xa);
            xs = getX();
        }
        else
        {
            setX(xs);
        }
        
        if (!zCollision)
        {
            setZ(getZ() + za);
            zs = getZ();
        }
        else
        {
            setZ(zs);
        }
        
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
    private boolean hasCollision(final Level level, final int xLoc, final int zLoc) throws Exception
    {
        final int extra = 1;
        
        //block to the east
        Block e = level.get(xLoc+extra, zLoc);
        
        //block to the west
        Block w = level.get(xLoc-extra, zLoc);
        
        //block to the north
        Block n = level.get(xLoc, zLoc-extra);
        
        //block to the south
        Block s = level.get(xLoc, zLoc+extra);
        
        //block to the north east
        Block ne = level.get(xLoc+extra, zLoc-extra);
        
        //block to the north west
        Block nw = level.get(xLoc-extra, zLoc-extra);
        
        //block to the south east
        Block se = level.get(xLoc+extra, zLoc+extra);
        
        //block to the south west
        Block sw = level.get(xLoc-extra, zLoc+extra);
        
        //center
        Block c = level.get(xLoc, zLoc);
        
        //if any of the blocks are solid we have collision
        return (e.isSolid() || w.isSolid() || n.isSolid() || s.isSolid() || c.isSolid() || ne.isSolid() || nw.isSolid() || se.isSolid() || sw.isSolid());
    }
}