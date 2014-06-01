package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.ai.AStar;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.wolfenstein.level.objects.LevelObject;
import com.gamesbykevin.wolfenstein.resources.GameImages;
import com.gamesbykevin.wolfenstein.resources.GameAudio;

import java.util.List;
import java.util.Random;

public abstract class Enemy extends LevelObject implements Disposable
{
    /**
     * Different actions the enemy can perform
     */
    public enum State
    {
        Idle, Walking, AttackStance, Attacking, Hurt, Death
    }
    
    //store the key so we know which enemy this is
    private final GameImages.Keys type;
    
    //keep track of the enemies health
    private int health;
    
    //the amount of damage an enemy deals when they attack
    private int damage;
    
    //these are the specific audio clips to play for these scenarios
    private GameAudio.Keys death, attack, notice;
    
    //does the enemy know the hero is nearby if not the enemy will remain idle
    private boolean alert = false;
    
    //the timer to keep track of how often to attack
    private Timer timer;
    
    //the velocity of the player
    protected static final double VELOCITY = 0.05d;
    
    //the default delay for the animation(s)
    protected static final long DEFAULT_DELAY = Timers.toNanoSeconds(250L);
    
    //the speed at which the enemy can move
    private double speed;
    
    //object used for path finding
    private AStar astar;
    
    /**
     * Create new enemy
     */
    protected Enemy(final GameImages.Keys type)
    {
        super();
        
        //store the enemy type
        this.type = type;
        
        //default health
        this.health = 100;
        
        //default damage
        this.damage = 10;
        
        //create new object that will help find the path for the enemies
        this.astar = new AStar();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (astar != null)
        {
            astar.dispose();
            astar = null;
        }
        
        if (this.timer != null)
            this.timer = null;
    }
    
    /**
     * Create a new timer
     * @param time The duration in milli-seconds
     */
    protected void createTimer(final long time)
    {
        //create a new timer
        this.timer = new Timer(Timers.toNanoSeconds(time));
    }
    
    protected void setMap(final List<Location> map)
    {
        this.astar.setMap(map);
    }
    
    protected boolean hasMap()
    {
        return this.astar.hasMap();
    }
    
    protected boolean hasPath()
    {
        return (!astar.getPath().isEmpty());
    }
    
    /**
     * Calculate the shortest path to the hero
     * @param x hero location
     * @param z hero location
     * @param random Object used to make random decisions
     * @throws Exception 
     */
    protected void calculatePath(final int x, final int z, final Random random) throws Exception
    {
        astar.setStart((int)getX(), (int)getZ());
        astar.setGoal(x, z);
        astar.calculate(random);
        
        //the first element is the hero location so remove that so the enemy doesn't touch the hero
        astar.getPath().remove(0);
    }
    
    protected double getSpeed()
    {
        return this.speed;
    }
    
    protected void setSpeed(final double speed)
    {
        this.speed = speed;
    }
    
    protected void resetPath()
    {
        astar.getPath().clear();
    }
    
    public void updatePosition()
    {
        //if there is no path
        if (!hasPath())
        {
            //stop moving
            super.resetVelocity();
        }
        else
        {
            //move the enemy based on the velocity
            super.update();

            //get the current goal
            Cell current = astar.getPath().get(astar.getPath().size() - 1);
            
            if (getX() < current.getCol())
            {
                if (getX() + getSpeed() > current.getCol())
                {
                    setX(current.getCol());
                    resetVelocityX();
                }
                else
                {
                    setVelocityX(getSpeed());
                }
            }
            
            if (getX() > current.getCol())
            {
                if (getX() - getSpeed() < current.getCol())
                {
                    setX(current.getCol());
                    resetVelocityX();
                }
                else
                {
                    setVelocityX(-getSpeed());
                }
            }
            
            if (getZ() < current.getRow())
            {
                if (getZ() + getSpeed() > current.getRow())
                {
                    setZ(current.getRow());
                    resetVelocityZ();
                }
                else
                {
                    setVelocityZ(getSpeed());
                }
            }
            
            if (getZ() > current.getRow())
            {
                if (getZ() - getSpeed() < current.getRow())
                {
                    setZ(current.getRow());
                    resetVelocityZ();
                }
                else
                {
                    setVelocityZ(-getSpeed());
                }
            }
            
            //set walking animation
            if (super.hasAnimation(State.Walking) && super.getKey() != State.Walking)
                super.setAnimation(State.Walking);
            
            //if we made it to the current position, remove it from the path
            if (current.equals(getX(), getZ()))
                astar.getPath().remove(astar.getPath().size() - 1);
        }
    }
    
    protected void setDamage(final int damage)
    {
        this.damage = damage;
    }
    
    protected int getDamage()
    {
        return this.damage;
    }
    
    protected void updateTimer(final long time)
    {
        this.timer.update(time);
    }
    
    protected boolean hasTimePassed()
    {
        return this.timer.hasTimePassed();
    }
    
    protected void resetTimer()
    {
        this.timer.reset();
    }
    
    protected void setAlert(final boolean alert)
    {
        this.alert = alert;
    }
    
    protected boolean isAlert()
    {
        return this.alert;
    }
    
    public boolean hasHealth()
    {
        return (this.health > 0);
    }
    
    protected void modifyHealth(final int change)
    {
        this.health += change;
    }
    
    public GameImages.Keys getType()
    {
        return this.type;
    }
    
    protected void setAudioKeyDeath(final GameAudio.Keys death)
    {
        this.death = death;
    }
    
    protected void setAudioKeyAttack(final GameAudio.Keys attack)
    {
        this.attack = attack;
    }
    
    protected void setAudioKeyAlert(final GameAudio.Keys notice)
    {
        this.notice = notice;
    }
    
    protected GameAudio.Keys getAudioKeyDeath()
    {
        return this.death;
    }
    
    protected GameAudio.Keys getAudioKeyAttack()
    {
        return this.attack;
    }
    
    protected GameAudio.Keys getAudioKeyAlert()
    {
        return this.notice;
    }
    
    /**
     * Is the enemy dead?
     * @return true if dead, false if not or if the animation does not exist
     */
    public boolean isDead()
    {
        if (!hasAnimation(State.Death))
            return false;
        
        return (getKey() == State.Death);
    }
    
    /**
     * Is the enemy hurt?
     * @return true if hurt, false if not or if the animation does not exist
     */
    public boolean isHurt()
    {
        if (!hasAnimation(State.Hurt))
            return false;
        
        return (getKey() == State.Hurt);
    }
    
    /**
     * Is the enemy attacking?
     * @return true if attacking, false if not or if the animation does not exist
     */
    public boolean isAttacking()
    {
        if (!hasAnimation(State.Attacking))
            return false;
        
        return (getKey() == State.Attacking);
    }
    
    /**
     * Is the enemy walking?
     * @return true if walking, false if not or if the animation does not exist
     */
    public boolean isWalking()
    {
        if (!hasAnimation(State.Walking))
            return false;
        
        return (getKey() == State.Walking);
    }
    
    /**
     * Is the enemy idle?
     * @return true if idle, false if not or if the animation does not exist
     */
    public boolean isIdle()
    {
        if (!hasAnimation(State.Idle))
            return false;
        
        return (getKey() == State.Idle);
    }
}