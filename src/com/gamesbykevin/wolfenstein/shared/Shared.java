package com.gamesbykevin.wolfenstein.shared;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * This Shared class will have shared objects
 * 
 * @author GOD
 */
public final class Shared 
{
    /**
     * These dimensions is the size of the game window the user will see.
     * NOTE: If these dimensions do not match the original then the mouse input will be off.
     */
    public static final int INITIAL_WIDTH  = 640;
    public static final int INITIAL_HEIGHT = 400;
    
    //the game is originally programmed for these dimensions
    public static final int ORIGINAL_WIDTH  = 640;
    public static final int ORIGINAL_HEIGHT = 400;
    
    //show UPS/FPS counters
    public static final boolean DEBUG = true;
    
    //do we hide mouse when the menu is not visible and actual gameplay has started
    public static boolean HIDE_MOUSE = true;
    
    //how many updates per second, controls speed of game
    public static final int DEFAULT_UPS = 30;
    
    //what is the name of our game
    public static final String GAME_NAME = "Wolfenstein";
    
    //blank cursor created here to hide the mouse cursor
    public static final Cursor CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "blank cursor");
}