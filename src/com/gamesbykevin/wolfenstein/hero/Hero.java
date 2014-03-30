package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.wolfenstein.engine.Engine;

public final class Hero 
{
    //we will track player location/speed here
    private Input input;
    
    public Hero()
    {
        this.input = new Input();
    }
    
    public void setLocation(final double x, final double z)
    {
        getInput().setX(x);
        getInput().setZ(z);
    }
    
    public Input getInput()
    {
        return input;
    }
    
    public void update(final Engine engine)
    {
        getInput().update(engine.getKeyboard());
    }
}