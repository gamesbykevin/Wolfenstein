package com.gamesbykevin.wolfenstein.manager;

import com.gamesbykevin.wolfenstein.display.Screen3D;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.wolfenstein.enemies.*;
import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.hero.Hero;
import com.gamesbykevin.wolfenstein.display.Texture;
import com.gamesbykevin.wolfenstein.display.Textures;
import com.gamesbykevin.wolfenstein.level.Level;
import com.gamesbykevin.wolfenstein.menu.CustomMenu;
import com.gamesbykevin.wolfenstein.menu.CustomMenu.LayerKey;
import com.gamesbykevin.wolfenstein.menu.CustomMenu.OptionKey;
import com.gamesbykevin.wolfenstein.resources.GameImage;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //the area where gameplay will occur
    private Rectangle window;
    
    //this screen will render our 3d objects
    public Screen3D screen;
    
    //our player in the game
    private Hero player;
    
    //the object containing the level info
    private Level level;
    
    //private Texture wall, floor, ceiling;
    
    private Enemy soldier;
    
    //wall textures
    private Textures textures;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //calculate the game window where game play will occur
        this.window = new Rectangle(engine.getMain().getScreen());
        
        int startCol = 4;
        int startRow = 4;
        
        this.player = new Hero();
        this.player.setLocation(startCol * 16, startRow * 16);
        
        this.textures = new Textures(engine.getResources().getGameImage(GameImage.Keys.WallTextureImage));
        
        //create a new level
        this.createLevel(3, 3, 9, 9, engine.getRandom());
        
        //create new texture and set pixel data array
        //this.wall = new Texture();
        //this.wall.update(engine.getResources().getGameImage(GameImage.Keys.WallTextureImage), 0, 0);
        
        //create new texture and set pixel data array
        //this.floor = new Texture();
        //this.floor.update(engine.getResources().getGameImage(GameImage.Keys.WallTextureImage), 4, 3);
        
        //create new texture and set pixel data array
        //this.ceiling = new Texture();
        //this.ceiling.update(engine.getResources().getGameImage(GameImage.Keys.WallTextureImage), 0, 7);
        
        //this.sprite = new Texture(engine.getResources().getGameImage(GameImage.Keys.Soldier1), 0, 0);
        
        this.soldier = new Soldier1();
        this.soldier.setImage(engine.getResources().getGameImage(GameImage.Keys.Soldier1));
        
        //create new canvas
        this.screen = new Screen3D(window.width, window.height);
        
        
        
        //BufferStrategy bs = 
        
        //get the menu object
        //final Menu menu = engine.getMenu();
       
        //the starting difficulty level
        //this.difficultyIndex = menu.getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Difficulty);
        
        //pick random key
        //GameImage.Keys key = keys.get(engine.getRandom().nextInt(keys.size()));
        
        //create new background
        //this.background = new Background(engine.getResources().getGameImage(key), window.getWidth(), window.y + window.height);
    }
    
    /**
     * Create a new level.
     * @param roomCol The number of rooms
     * @param roomRow The number of rooms
     * @param eachRoomCol The size of each room
     * @param eachRoomRow The size of each room
     * @throws Exception 
     */
    private void createLevel(final int roomCol, final int roomRow, final int eachRoomCol, final int eachRoomRow, final Random random) throws Exception
    {
        if (eachRoomRow % 2 == 0 || eachRoomCol % 2 == 0 || eachRoomRow != eachRoomCol)
            throw new Exception("Each room must have the same amount of rows and columns and that number must be odd");
        
        //create a new level
        this.level = new Level(roomCol, roomRow, eachRoomCol, eachRoomRow, random);
    }
    
    /**
     * Get the game window
     * @return The Rectangle where game play will take place
     */
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    public Textures getTextures()
    {
        return this.textures;
    }
    
    public Hero getPlayer()
    {
        return this.player;
    }
    
    public Level getLevel()
    {
        return this.level;
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        window = null;
    }
    
    /**
     * Update all application elements
     * 
     * @param engine Our main game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //update our player object
        player.update(engine);
        
        //update level status
        level.update(engine.getMain().getTime(), player.getInput().getPlayerX(), player.getInput().getPlayerZ());
        
        //update soldier animation
        soldier.update(engine.getMain().getTime());
        
        //write our 3d screen objects etc.. to pixel array
        screen.renderPixelData(engine, soldier);
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        graphics.drawImage(screen.getImage(), 0, 0, window.width, window.height, null);
        
        //graphics.drawImage(screen.spriteImage, 0, 0, null);
    }
}