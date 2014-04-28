package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

public class Door 
{
    //timer used for animation
    private Timer timer;
    
    //keep the door open for 5 seconds
    private static final long DURATION_OPEN = Timers.toNanoSeconds(5000L);
    
    //it will take 2 seconds for the door to open and close
    private static final long DURATION_CHANGING = Timers.toNanoSeconds(2000L);
    
    protected Door()
    {
        //create new timer
        timer = new Timer();
        
        //default to closed
        setState(State.CLOSED);
    }
    
    /**
     * Possible scenarios for the door
     */
    private enum State
    {
        CLOSED, CLOSING, OPEN, OPENING
    }
    
    //the state of the door
    private State state;
    
    /**
     * The door will only be opened if it is closed, else nothing happens
     */
    protected void open()
    {
        switch(getState())
        {
            //only open the door if it is closed
            case CLOSED:
                
                //we are now opening the door
                setState(State.OPENING);
                
                //set the appropriate time
                timer.setReset(DURATION_CHANGING);
                
                //reset timer
                timer.reset();
                break;
        }
    }
    
    /**
     * Force the door open
     */
    protected void forceOpen()
    {
        setState(State.OPEN);
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
                    timer.setReset(DURATION_CHANGING);
                    
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