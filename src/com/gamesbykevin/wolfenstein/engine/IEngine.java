package com.gamesbykevin.wolfenstein.engine;

import com.gamesbykevin.wolfenstein.main.Main;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Graphics;

public interface IEngine extends Disposable
{
    /**
     * Draw the engine which contains all of the game elements
     * @param graphics Graphics object that game will be written to
     * @return Graphics object containing game/menu elements
     * @throws Exception 
     */
    public void render(Graphics graphics) throws Exception;
    
    /**
     * The Main class where the application is initialized
     * and contains our main loop so we need a method for the
     * Main class to be able to update our game engine
     * 
     * @param main The main class
     */
    public void update(Main main);
}