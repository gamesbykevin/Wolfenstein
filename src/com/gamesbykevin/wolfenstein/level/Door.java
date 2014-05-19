package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

public final class Door implements Disposable
{
    //timer used for animation
    private Timer timer;
    
    //keep the door open for this duration once the player moves away from the door
    private static final long DURATION_OPEN = Timers.toNanoSeconds(1500L);
    
    //it will take 1.5 seconds for the door to close
    private static final long DURATION_CLOSING = Timers.toNanoSeconds(1500L);
    
    //it will take 1 seconds for the door to open
    private static final long DURATION_OPENING = Timers.toNanoSeconds(1000L);
    
    //it will take 2.5 seconds for the secret door to open
    private static final long DURATION_OPENING_SECRET = Timers.toNanoSeconds(3000L);
    
    //is this a secret door
    private boolean secret = false;
    
    //is this door locked
    private boolean locked = false;
    
    /**
     * Possible scenarios for the door
     */
    private enum State
    {
        CLOSED, CLOSING, OPEN, OPENING
    }
    
    //the state of the door
    private State state;
    
    protected Door()
    {
        //create new timer
        timer = new Timer();
        
        //default to closed
        setState(State.CLOSED);
    }
    
    @Override
    public void dispose()
    {
        timer = null;
        state = null;
    }
    
    /**
     * Does this door need to be locked?
     * @param locked true if this door is to be locked, false otherwise
     */
    public void setLocked(final boolean locked)
    {
        this.locked = locked;
    }
    
    /**
     * Is this door locked?<br>
     * If the door is locked it will require a key.
     * @return true if locked, false otherwise
     */
    public boolean isLocked()
    {
        return this.locked;
    }
            
    
    /**
     * Set the door hidden/secret
     * @param secret true if this door a hidden/secret, false otherwise
     */
    public void setSecret(final boolean secret)
    {
        this.secret = secret;
    }
    
    /**
     * Is this door a secret
     * @return true if this is a hidden door, false otherwise
     */
    public boolean isSecret()
    {
        return this.secret;
    }
    
    /**
     * The door will only be opened if it is closed, else nothing happens
     */
    public void open()
    {
        switch(getState())
        {
            //only open the door if it is closed
            case CLOSED:
                
                //we are now opening the door
                setState(State.OPENING);
                
                //secret doors take longer to open
                if (isSecret())
                {
                    //set the appropriate time
                    timer.setReset(DURATION_OPENING_SECRET);
                }
                else
                {
                    //set the appropriate time
                    timer.setReset(DURATION_OPENING);
                }
                
                //reset timer
                timer.reset();
                break;
        }
    }
    
    /**
     * Is the door open
     * @return true if fully open, false otherwise
     */
    public boolean isOpen()
    {
        return (getState() == State.OPEN);
    }
    
    /**
     * Is the door closed
     * @return true if fully closed, false otherwise
     */
    public boolean isClosed()
    {
        return (getState() == State.CLOSED);
    }
    
    /**
     * Is the door opening
     * @return true if in progress of opening, false otherwise
     */
    public boolean isOpening()
    {
        return (getState() == State.OPENING);
    }
    
    /**
     * Is the door closing
     * @return true if in progress of closing, false otherwise
     */
    public boolean isClosing()
    {
        return (getState() == State.CLOSING);
    }
    
    /**
     * Update the status of our door
     * @param time Time duration per update (in nanoseconds)
     */
    public void update(final long time)
    {
        //if the door is closed don't do anything
        if (isClosed())
            return;
        
        //if the timer has finished
        if (timer.hasTimePassed())
        {
            switch (getState())
            {
                //if we are opening the door is now open
                case OPENING:
                    
                    //door is now open
                    setState(State.OPEN);
                    
                    //set the timer countdown to how long it is to be open
                    timer.setReset(DURATION_OPEN);
                    
                    //reset timer
                    timer.reset();
                    break;
                    
                //if we are closing the door is now closed
                case CLOSING:
                    
                    //door is now closed
                    setState(State.CLOSED);
                    break;
                    
                //if the door is open and time has passed start closing
                case OPEN:
                    
                    //we will now start closing
                    setState(State.CLOSING);
                    
                    //set the timer countdown to how long it will take to close
                    timer.setReset(DURATION_CLOSING);
                    
                    //reset timer
                    timer.reset();
                    break;
            }
        }
        else
        {
            //update timer
            timer.update(time);
        }
    }
    
    private void setState(final State state)
    {
        this.state = state;
    }
    
    private State getState()
    {
        return this.state;
    }
    
    /**
     * Get the progress of the timer so we know the status of completion.
     * @return The progress will range from 0.0 (0%) to 1.0 (100%)
     */
    public float getProgress()
    {
        return this.timer.getProgress();
    }
}