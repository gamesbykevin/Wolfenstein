package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.wolfenstein.display.Render3D;
import com.gamesbykevin.wolfenstein.hero.weapons.Weapons;
import com.gamesbykevin.wolfenstein.level.Block;
import com.gamesbykevin.wolfenstein.level.Level;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem;
import com.gamesbykevin.wolfenstein.resources.Resources;
import com.gamesbykevin.wolfenstein.resources.GameAudio;

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
    
    //the players location on the overall map
    private int column, row;
    
    //the speed the player moves while crouching
    private final double speedCrouch = 0.5;
    
    //the speed the player moves while running
    private final double speedRun    = 3.5;
    
    //the speed the player moves while walking
    private final double speedWalk   = 1.75;
    
    //how fast can the player turn
    private final double rotationSpeed = 0.055;
    
    //are we playing sound for gun fire
    private boolean play = false;
    
    //Here is all of our input options
    private static final int KEY_WEAPON_SELECT_1 = KeyEvent.VK_1;
    private static final int KEY_WEAPON_SELECT_2 = KeyEvent.VK_2;
    private static final int KEY_WEAPON_SELECT_3 = KeyEvent.VK_3;
    private static final int KEY_WEAPON_SELECT_4 = KeyEvent.VK_4;
    private static final int KEY_WALK_FORWARD = KeyEvent.VK_UP;
    private static final int KEY_WALK_BACKWARD = KeyEvent.VK_DOWN;
    private static final int KEY_STRAFE_LEFT = KeyEvent.VK_A;
    private static final int KEY_STRAFE_RIGHT = KeyEvent.VK_D;
    private static final int KEY_TURN_LEFT = KeyEvent.VK_LEFT;
    private static final int KEY_TURN_RIGHT = KeyEvent.VK_RIGHT;
    private static final int KEY_CROUCH = KeyEvent.VK_Z;
    private static final int KEY_JUMP = KeyEvent.VK_S;
    private static final int KEY_RUN = KeyEvent.VK_X;
    private static final int KEY_OPEN_DOOR = KeyEvent.VK_SPACE;
    private static final int KEY_SHOOT = KeyEvent.VK_ENTER;
    
    protected Input()
    {
    }
    
    /**
     * Update the player input and status
     * @param keyboard Keyboard Input
     * @param level The current Level
     * @param hero The hero
     * @param resources Resources object to play sound
     * @throws Exception 
     */
    public void update(final Keyboard keyboard, final Level level, final Hero hero, final Resources resources) throws Exception
    {
        int xMove = 0;
        int zMove = 0;
        double jumpHeight = 1;
        double crouchHeight = 2;
        double speed = speedWalk;
        
        final boolean selectWeapon1 = keyboard.hasKeyPressed(KEY_WEAPON_SELECT_1);
        final boolean selectWeapon2 = keyboard.hasKeyPressed(KEY_WEAPON_SELECT_2);
        final boolean selectWeapon3 = keyboard.hasKeyPressed(KEY_WEAPON_SELECT_3);
        final boolean selectWeapon4 = keyboard.hasKeyPressed(KEY_WEAPON_SELECT_4);
        
        boolean right = keyboard.hasKeyPressed(KEY_STRAFE_RIGHT);
        boolean left = keyboard.hasKeyPressed(KEY_STRAFE_LEFT);
        boolean up = keyboard.hasKeyPressed(KEY_WALK_FORWARD);
        boolean down = keyboard.hasKeyPressed(KEY_WALK_BACKWARD);
        boolean turnLeft = keyboard.hasKeyPressed(KEY_TURN_LEFT);
        boolean turnRight = keyboard.hasKeyPressed(KEY_TURN_RIGHT);
        boolean jump = false;//keyboard.hasKeyPressed(KEY_JUMP);
        boolean crouch = false;//keyboard.hasKeyPressed(KEY_CROUCH);
        boolean run = keyboard.hasKeyPressed(KEY_RUN);
        boolean openDoor = keyboard.hasKeyPressed(KEY_OPEN_DOOR);
        boolean openDoorRelease = keyboard.hasKeyReleased(KEY_OPEN_DOOR);
        boolean shoot = keyboard.hasKeyPressed(KEY_SHOOT);
        
        //check all inputs
        manageKeyboard(keyboard);
        
        //set the weapon
        if (selectWeapon1)
        {
            if (hero.getSpriteSheet().hasFinished() || !hero.getSpriteSheet().hasStarted())
                hero.getWeapons().set(Weapons.Type.Knife);
        }
        else if (selectWeapon2)
        {
            if (hero.getSpriteSheet().hasFinished() || !hero.getSpriteSheet().hasStarted())
                hero.getWeapons().set(Weapons.Type.Pistol);
        }
        else if (selectWeapon3)
        {
            if (hero.getSpriteSheet().hasFinished() || !hero.getSpriteSheet().hasStarted())
                hero.getWeapons().set(Weapons.Type.AssaultRifle);
        }
        else if (selectWeapon4)
        {
            if (hero.getSpriteSheet().hasFinished() || !hero.getSpriteSheet().hasStarted())
                hero.getWeapons().set(Weapons.Type.MachineGun);
        }
        else
        {
            //player wants to shoot
            if (shoot)
            {
                //if shooting can't run
                run = false;
                
                //if the current animation has finished or not started yet, then we can shoot
                if (hero.getSpriteSheet().hasFinished() || !hero.getSpriteSheet().hasStarted())
                {
                    //if a bullet was fired
                    if (hero.getWeapons().shoot())
                    {
                        //flag change
                        hero.flagChange();
                        
                        //set the appropriate sprite sheet
                        hero.getSpriteSheet().setCurrent(hero.getWeapons().getType());

                        //don't pause animation
                        hero.getSpriteSheet().setPause(false);

                        if (!play)
                        {
                            play = true;
                            
                            switch (hero.getWeapons().getType())
                            {
                                case Knife:
                                    //play sound effect
                                    resources.playGameAudio(GameAudio.Keys.Knife, true);
                                    break;

                                case Pistol:
                                    //play sound effect
                                    resources.playGameAudio(GameAudio.Keys.PistolFire, true);
                                    break;

                                case AssaultRifle:
                                    //play sound effect
                                    resources.playGameAudio(GameAudio.Keys.AssaultRifleFire, true);
                                    break;

                                case MachineGun:
                                    //play sound effect
                                    resources.playGameAudio(GameAudio.Keys.MachinegunFire, true);
                                    break;
                            }
                        }
                    }
                }
            }
            else
            {
                //if the animation has finished or not started stop the sound
                if (hero.getSpriteSheet().hasFinished() || !hero.getSpriteSheet().hasStarted())
                {
                    //if not shooting stop audio
                    resources.stopGameAudio(GameAudio.Keys.Knife);
                    resources.stopGameAudio(GameAudio.Keys.PistolFire);
                    resources.stopGameAudio(GameAudio.Keys.AssaultRifleFire);
                    resources.stopGameAudio(GameAudio.Keys.MachinegunFire);
                    
                    //stop sound
                    play = false;
                }
            }
        }
        
        if (!shoot)
        {
            //if weapon selection has changed set the correct animation
            if (selectWeapon1 || selectWeapon2 || selectWeapon3 || selectWeapon4)
            {
                //flag change
                hero.flagChange();
                
                hero.getSpriteSheet().setCurrent(hero.getWeapons().getType());
            }
        }
        
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
        {
            xMove--;
            
            //if we are strafing we can't run
            setRunning(false);
        }
        
        if (right)
        {
            xMove++;
            
            //if we are strafing we can't run
            setRunning(false);
        }
        
        if (turnLeft)
            rotationa -= rotationSpeed;
        
        if (turnRight)
            rotationa += rotationSpeed;
        
        if (!turnLeft && !turnRight)
            rotationa = 0;
        
        if (jump)
        {
            setY(getY() + jumpHeight);
            run = false;
        }
        
        //this is to crouch the player
        if (crouch)
        {
            setY(getY() - crouchHeight);
            run = false;
            speed = speedCrouch;
        }
        
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
        
        //if there is no collision here we can move to the new position
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
        
        //if there is no collision here we can move to the new position
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

        //now that the position is set calculate where the player is on the overall level
        this.column = (int)(getX() / 16);
        this.row    = (int)(getZ() / 16);
                
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
        
        //check if the player opened a door
        if (openDoor)
            manageDoors(level, hero, resources, originalX, originalZ, openDoorRelease);
        
        //get the bonus item at the players current location
        BonusItem.Type type = level.getLevelObjects().getBonusItemCollisionType(originalX, originalZ);
        
        //a bonus item was found
        if (type != null)
            manageBonus(type, hero, resources);
    }
    
    private void manageKeyboard(final Keyboard keyboard)
    {
        if (keyboard.hasKeyReleased(KEY_OPEN_DOOR))
        {
            keyboard.removeKeyPressed(KEY_OPEN_DOOR);
            keyboard.removeKeyReleased(KEY_OPEN_DOOR);
        }
        
        if (keyboard.hasKeyReleased(KEY_SHOOT))
        {
            keyboard.removeKeyPressed(KEY_SHOOT);
            keyboard.removeKeyReleased(KEY_SHOOT);
        }
        
        if (keyboard.hasKeyReleased(KEY_RUN))
        {
            keyboard.removeKeyPressed(KEY_RUN);
            keyboard.removeKeyReleased(KEY_RUN);
        }
        
        if (keyboard.hasKeyReleased(KEY_JUMP))
        {
            keyboard.removeKeyPressed(KEY_JUMP);
            keyboard.removeKeyReleased(KEY_JUMP);
        }
        
        if (keyboard.hasKeyReleased(KEY_CROUCH))
        {
            keyboard.removeKeyPressed(KEY_CROUCH);
            keyboard.removeKeyReleased(KEY_CROUCH);
        }
        
        if (keyboard.hasKeyReleased(KEY_TURN_RIGHT))
        {
            keyboard.removeKeyPressed(KEY_TURN_RIGHT);
            keyboard.removeKeyReleased(KEY_TURN_RIGHT);
        }
    
        if (keyboard.hasKeyReleased(KEY_TURN_LEFT))
        {
            keyboard.removeKeyPressed(KEY_TURN_LEFT);
            keyboard.removeKeyReleased(KEY_TURN_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KEY_STRAFE_RIGHT))
        {
            keyboard.removeKeyPressed(KEY_STRAFE_RIGHT);
            keyboard.removeKeyReleased(KEY_STRAFE_RIGHT);
        }
    
        if (keyboard.hasKeyReleased(KEY_STRAFE_LEFT))
        {
            keyboard.removeKeyPressed(KEY_STRAFE_LEFT);
            keyboard.removeKeyReleased(KEY_STRAFE_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KEY_WALK_BACKWARD))
        {
            keyboard.removeKeyPressed(KEY_WALK_BACKWARD);
            keyboard.removeKeyReleased(KEY_WALK_BACKWARD);
        }
        
        if (keyboard.hasKeyReleased(KEY_WALK_FORWARD))
        {
            keyboard.removeKeyPressed(KEY_WALK_FORWARD);
            keyboard.removeKeyReleased(KEY_WALK_FORWARD);
        }
        
        if (keyboard.hasKeyReleased(KEY_WEAPON_SELECT_1))
        {
            keyboard.removeKeyPressed(KEY_WEAPON_SELECT_1);
            keyboard.removeKeyReleased(KEY_WEAPON_SELECT_1);
        }
        
        if (keyboard.hasKeyReleased(KEY_WEAPON_SELECT_2))
        {
            keyboard.removeKeyPressed(KEY_WEAPON_SELECT_2);
            keyboard.removeKeyReleased(KEY_WEAPON_SELECT_2);
        }
        
        if (keyboard.hasKeyReleased(KEY_WEAPON_SELECT_3))
        {
            keyboard.removeKeyPressed(KEY_WEAPON_SELECT_3);
            keyboard.removeKeyReleased(KEY_WEAPON_SELECT_3);
        }
        
        if (keyboard.hasKeyReleased(KEY_WEAPON_SELECT_4))
        {
            keyboard.removeKeyPressed(KEY_WEAPON_SELECT_4);
            keyboard.removeKeyReleased(KEY_WEAPON_SELECT_4);
        }
    }
    
    private void manageDoors(final Level level, final Hero hero, final Resources resources, final double originalX, final double originalZ, final boolean openDoorRelease) throws Exception
    {
        //temp block object
        Block block;

        //check any blocks within this range
        final int distance = 2;

        //now check all blocks surrounding the player
        for (int x = -distance; x <= distance; x++)
        {
            for (int z = -distance; z <= distance; z++)
            {
                //get the current block
                block = level.getBlock(originalX + x, originalZ + z);

                //if this block is a door
                if (block.isDoor())
                {
                    //if the door is locked
                    if (block.getDoor().isLocked())
                    {
                        //check if the hero has a key
                        if (hero.hasKey())
                        {
                            //flag change
                            hero.flagChange();

                            //remove 1 key from inventory
                            hero.removeKey();

                            //the door is no longer locked
                            block.getDoor().setLocked(false);
                        }
                        else
                        {
                            if (openDoorRelease)
                            {
                                //play sound effect
                                resources.playGameAudio(GameAudio.Keys.DoorLocked);
                            }

                            //we do not have a key so don't continue
                            continue;
                        }
                    }

                    //only play sound when door is closed and about to be opened
                    if (block.getDoor().isClosed())
                    {
                        //if the door is a secret one play different audio
                        if (block.getDoor().isSecret())
                        {
                            //play sound effect
                            resources.playGameAudio(GameAudio.Keys.DoorOpenSecret);
                        }
                        else
                        {
                            //play sound effect
                            resources.playGameAudio(GameAudio.Keys.DoorOpen);
                        }
                    }

                    //open the door
                    block.getDoor().open();
                }

                //if we have selected the goal and the level isn't complete yet
                if (block.isGoal() && !level.isComplete())
                {
                    //make sure player is inside the goal
                    if (level.insideGoal(getPlayerX(), getPlayerZ()))
                    {
                        //mark the level as complete
                        level.markComplete(block);

                        //play sound effect
                        resources.playGameAudio(GameAudio.Keys.HitGoalSwitch);
                    }
                }
            }
        }
    }
    
    private void manageBonus(final BonusItem.Type type, final Hero hero, final Resources resources) throws Exception
    {
        //flag change
        hero.flagChange();

        switch (type)
        {
            case Key1:
            case Key2:
                //add key to inventory
                hero.addKey();

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupKey);
                break;

            case SmallFood:
                //add health
                hero.modifyHealth(Hero.SMALL_HEALTH);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupFood);
                break;

            case HealthKit:
                //add health
                hero.modifyHealth(Hero.HEALTH_KIT);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupFood);
                break;

            case AmmoClip:
                //add to hero
                hero.add(type);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupAmmo);
                break;

            case AssaultGun:
                //add to hero
                hero.add(type);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupMachinegun);
                break;

            case MachineGun:
                //add to hero
                hero.add(type);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupChaingun);
                break;

            case Treasure1:
                //add to score
                hero.addScore(Hero.TREASURE_1);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupTreasure1);
                break;

            case Treasure2:
                //add to score
                hero.addScore(Hero.TREASURE_2);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupTreasure2);
                break;

            case Treasure3:
                //add to score
                hero.addScore(Hero.TREASURE_3);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupTreasure3);
                break;

            case Treasure4:
                //add to score
                hero.addScore(Hero.TREASURE_4);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.PickupTreasure4);
                break;

            case ExtraLife:
                //add life
                hero.setLives(hero.getLives() + 1);

                //play sound effect
                resources.playGameAudio(GameAudio.Keys.ExtraLife);
                break;

            default:
                throw new Exception("Bonus Type not handled here");
        }
    }
    
    /**
     * Get the column where the player is located
     * @return The current column where the player is
     */
    public int getPlayerX()
    {
        return this.column;
    }
    
    /**
     * Get the row where the player is located
     * @return The current row where the player is
     */
    public int getPlayerZ()
    {
        return this.row;
    }
    
    /**
     * Check for collision with walls and obstacles in a level
     * @param level The level that contains the blocks
     * @param xLoc x location where the player is
     * @param zLoc z location where the player is
     * @param openDoor Does the player want to open a door
     * @return true if we hit a wall, false otherwise
     */
    private boolean hasCollision(final Level level, final double xLoc, final double zLoc)
    {
        try
        {
            //temp block object
            Block block;

            //the current new block the player will be in
            Block current = level.getBlock(xLoc, zLoc);
            
            //if the current block is not a door
            if (!current.isDoor())
            {
                //check for walls withing this distance
                final double WALL_D = .950;

                //west
                block = level.getBlock(xLoc - WALL_D, zLoc);

                if (hasCollision(block))
                    return true;

                //east
                block = level.getBlock(xLoc + WALL_D, zLoc);

                if (hasCollision(block))
                    return true;

                //north
                block = level.getBlock(xLoc, zLoc - WALL_D);
                
                if (hasCollision(block))
                    return true;

                //south
                block = level.getBlock(xLoc, zLoc + WALL_D);
                
                if (hasCollision(block))
                    return true;

                /**
                 * now check the corners at a closer distance
                 */

                //north west
                block = level.getBlock(xLoc - Render3D.CLIP, zLoc - Render3D.CLIP);
                
                if (hasCollision(block))
                    return true;

                //north east
                block = level.getBlock(xLoc + Render3D.CLIP, zLoc - Render3D.CLIP);

                if (hasCollision(block))
                    return true;
                
                //south west
                block = level.getBlock(xLoc - Render3D.CLIP, zLoc + Render3D.CLIP);

                if (hasCollision(block))
                    return true;
                
                //south east
                block = level.getBlock(xLoc + Render3D.CLIP, zLoc + Render3D.CLIP);
                
                if (hasCollision(block))
                    return true;
            }
            else
            {
                //if the door is open check walls next to door
                if (current.getDoor().isOpen())
                {
                    //west
                    block = level.getBlock(xLoc - Render3D.CLIP, zLoc);

                    if (hasCollision(block))
                        return true;

                    //east
                    block = level.getBlock(xLoc + Render3D.CLIP, zLoc);

                    if (hasCollision(block))
                        return true;

                    //north
                    block = level.getBlock(xLoc, zLoc - Render3D.CLIP);

                    if (hasCollision(block))
                        return true;

                    //south
                    block = level.getBlock(xLoc, zLoc + Render3D.CLIP);

                    if (hasCollision(block))
                        return true;
                }
                else
                {
                    //if door is not open we have collision
                    return true;
                }
            }
            
            //check if any collisions with obstacles
            if (level.getLevelObjects().hasObstacleCollision(xLoc, zLoc))
                return true;
        }
        catch(Exception e)
        {
            //print the error
            e.printStackTrace();
        }
        
        //no collision
        return false;
    }
    
    /**
     * Check the block status to determine if there is a collision
     * @param block The block we want to check
     * @return true will be returned for the following conditions.<br>
     * 1. If the block is solid and isn't a door.<br>
     * 2. If the block is solid and and is a door but the door isn't open.
     */
    private boolean hasCollision(final Block block)
    {
        //is this block solid
        if (block.isSolid())
        {
            //if it is not a door we have collision
            if (!block.isDoor())
                return true;

            //if it is a door but not fully open we have collision
            if (!block.getDoor().isOpen())
                return true;
        }
        
        //no collision was detected
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