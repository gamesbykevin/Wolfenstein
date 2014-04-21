package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.wolfenstein.display.Render3D;

import com.gamesbykevin.wolfenstein.level.Block;
import com.gamesbykevin.wolfenstein.level.Level;

import java.awt.event.KeyEvent;

public final class Input extends Sprite
{
    //location of player
    private double xa, za, rotation, rotationa;
    
    //store the xs and zs location so when we have collision we can move the player back
    private double xs = getX(), zs = getZ();
    
    //are we walking or running
    private boolean walking = false, running = false;
    
    //this variable will be used for animation
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
        Run(KeyEvent.VK_X),
        OpenDoor(KeyEvent.VK_SPACE),
        Shoot(KeyEvent.VK_ENTER);
        
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
    
    public void update(final Keyboard keyboard, final Level level)
    {
        int xMove = 0;
        int zMove = 0;
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
        boolean openDoor = keyboard.hasKeyPressed(InputOptions.OpenDoor.getKey());
        boolean shoot = keyboard.hasKeyPressed(InputOptions.Shoot.getKey());
        
        //you can't do both at same time
        if (up)
            down = false;
        if (right)
            left = false;
        if (turnRight)
            turnLeft = false;
        
        //default walking to false
        setWalking(false);
        
        if (up)
        {
            zMove++;
            setWalking(true);
        }
        
        if (down)
        {
            zMove--;
            setWalking(true);
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
        
        if (!turnLeft && !turnRight)
            rotationa = 0;
        
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
        
        //if we are running increase speed
        if (run)
        {
            speed = speedRun;
            setRunning(true);
        }
        else
        {
            //we are not running
            setRunning(false);
        }
        
        //calculate the additional space moved
        setXA((xMove * Math.cos(getRotation()) + zMove * Math.sin(getRotation())) * speed);
        setZA((zMove * Math.cos(getRotation()) - xMove * Math.sin(getRotation())) * speed);
        
        //predict where the player will be next
        final double newX = (getX() + getXA()) / 16;
        final double newZ = (getZ() + getZA()) / 16;
        
        //where the player is currently
        final double originalX = getX() / 16;
        final double originalZ = getZ() / 16;
        
        //just check if x collision has occurred
        final boolean xCollision = hasCollision(level, newX, originalZ);
        
        //just check if z collision has occurred
        final boolean zCollision = hasCollision(level, originalX, newZ);
        
        if (!xCollision)
        {
            //set new position
            setX(getX() + getXA());
            
            //store valid location
            setXS(getX());
        }
        else
        {
            //restore valid location
            setX(getXS());
        }
        
        if (!zCollision)
        {
            //set new position
            setZ(getZ() + getZA());
            
            //store valid location
            setZS(getZ());
        }
        else
        {
            //restore valid location
            setZ(getZS());
        }
        
        //slow down speed
        setXA(getXA() * 0.1);
        setZA(getZA() * 0.1);
        
        //apply gravity
        setY(getY() * .9);
        
        //if the angle is to be moved
        if (rotationa != 0)
        {
            //add rotation angle to overall rotation
            setRotation(getRotation() + rotationa);

            //keep radian value from getting to large
            if (getRotation() > 2 * Math.PI)
                setRotation(getRotation() - (2 * Math.PI));
            if (getRotation() < 0)
                setRotation(getRotation() + (2 * Math.PI));
        
            //decrease rotation angle speed
            rotationa *= 0.5;
        }
    }
    
    /**
     * Check for collision with walls
     * @param level The level that contains the blocks
     * @param xLoc x location where the player is
     * @param zLoc z location where the player is
     * @return true if we hit a wall, false otherwise
     */
    private boolean hasCollision(final Level level, final double xLoc, final double zLoc)
    {
        //if no movement, no collision
        if (getXA() == 0 && getZA() == 0)
            return false;
        
        
        //the blocks in each direction
        Block w, e, n, s;
        
        //the current new block the player will be in
        Block c = level.get(xLoc, zLoc);
        
        //if the current block is not a door
        if (!c.isDoor())
        {
            //wall distance limit
            final double WALL_D = .950;
            
            w = level.get(xLoc - WALL_D, zLoc);
            e = level.get(xLoc + WALL_D, zLoc);
            n = level.get(xLoc, zLoc - WALL_D);
            s = level.get(xLoc, zLoc + WALL_D);
            
            if (w.isSolid() && !w.isDoor())
                return true;
            if (e.isSolid() && !e.isDoor())
                return true;
            if (n.isSolid() && !n.isDoor())
                return true;
            if (s.isSolid() && !s.isDoor())
                return true;
            
            //check corners when closer
            Block nw = level.get(xLoc - Render3D.CLIP, zLoc - Render3D.CLIP);
            Block ne = level.get(xLoc + Render3D.CLIP, zLoc - Render3D.CLIP);
            Block sw = level.get(xLoc - Render3D.CLIP, zLoc + Render3D.CLIP);
            Block se = level.get(xLoc + Render3D.CLIP, zLoc + Render3D.CLIP);
            
            if (nw.isSolid() && !nw.isDoor())
                return true;
            if (ne.isSolid() && !ne.isDoor())
                return true;
            if (sw.isSolid() && !sw.isDoor())
                return true;
            if (se.isSolid() && !se.isDoor())
                return true;
        }
        else
        {
            w = level.get(xLoc - Render3D.CLIP, zLoc);
            e = level.get(xLoc + Render3D.CLIP, zLoc);
            n = level.get(xLoc, zLoc - Render3D.CLIP);
            s = level.get(xLoc, zLoc + Render3D.CLIP);
            
            if (w.isSolid())
                return true;
            if (e.isSolid())
                return true;
            if (n.isSolid())
                return true;
            if (s.isSolid())
                return true;
        }
        
        //no collision
        return false;
    }

    private void setXS(final double xs)
    {
        this.xs = xs;
    }
    
    private void setZS(final double zs)
    {
        this.zs = zs;
    }
    
    private double getXS()
    {
        return this.xs;
    }
    
    private double getZS()
    {
        return this.zs;
    }
    
    private void setXA(final double xa)
    {
        this.xa = xa;
    }
    
    private void setZA(final double za)
    {
        this.za = za;
    }
    
    private double getXA()
    {
        return this.xa;
    }
    
    private double getZA()
    {
        return this.za;
    }
    
    private void setRotation(final double rotation)
    {
        this.rotation = rotation;
    }
    
    public double getRotation()
    {
        return rotation;
    }
    
    private void setRunning(final boolean running)
    {
        this.running = running;
    }
    
    /**
     * Is the player running
     * @return true if running, false otherwise
     */
    public boolean isRunning()
    {
        return running;
    }
    
    private void setWalking(final boolean walking)
    {
        this.walking = walking;
    }
    
    /**
     * Is the player walking
     * @return true if walking, false otherwise
     */
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
}