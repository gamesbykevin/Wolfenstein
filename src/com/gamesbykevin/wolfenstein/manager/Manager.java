package com.gamesbykevin.wolfenstein.manager;

import com.gamesbykevin.wolfenstein.display.Screen3D;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.resources.FontManager;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.wolfenstein.enemies.*;
import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.hero.Hero;
import com.gamesbykevin.wolfenstein.display.Texture;
import com.gamesbykevin.wolfenstein.display.Textures;
import com.gamesbykevin.wolfenstein.level.Level;
import com.gamesbykevin.wolfenstein.level.LevelStats;
import com.gamesbykevin.wolfenstein.menu.CustomMenu;
import com.gamesbykevin.wolfenstein.menu.CustomMenu.LayerKey;
import com.gamesbykevin.wolfenstein.menu.CustomMenu.OptionKey;
import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.resources.GameImages;
import com.gamesbykevin.wolfenstein.resources.GameFont;
import com.gamesbykevin.wolfenstein.resources.Resources;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
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
    
    //where we will render level create progress
    private Rectangle progressWindow;
    
    //this screen will render our 3d objects
    public Screen3D screen;
    
    //our hero in the game
    private Hero hero;
    
    //the object containing the level info
    private Level level;
    
    //this will display level stats
    private LevelStats stats;
    
    //object that will contain the enemies for the level
    private Enemies enemies;
    
    //wall textures
    private Textures textures;
    
    //unique keys for each font
    private enum FontKey 
    {
        GameFont
    }
    
    //the initial starting size of the maze
    private int mazeDimensions = Level.MINIMUM_MAZE_DIMENSION;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //calculate the game window where game play will occur
        this.window = new Rectangle(engine.getMain().getScreen());

        //area where progress is displayed to user
        this.progressWindow = new Rectangle(window.x, window.y, window.width, window.height - 50);

        //the stats for the level will be rendered here
        this.stats = new LevelStats(engine.getResources().getGameImage(GameImages.Keys.LevelComplete));
        this.stats.setLocation(window.x, window.y);
        this.stats.setDimensions(window);
        
        //create a new hero
        this.hero = new Hero(
            engine.getResources().getGameImage(GameImages.Keys.Guns), 
            engine.getResources().getGameImage(GameImages.Keys.PlayerHud), 
            engine.getResources().getGameImage(GameImages.Keys.Mugshots));
        
        //place the hero appropriately on the window along with the hud
        this.hero.setDimensions(192, 192);
        this.hero.setHeroLocation(window.x + (window.getWidth() / 2), window.y + window.getHeight());
        
        //create new enemies list
        this.enemies = new Enemies();
        
        //create the textures for the walls
        this.textures = new Textures(engine.getResources().getGameImage(GameImages.Keys.WallTextures));

        //create new canvas
        this.screen = new Screen3D(window.width, window.height);

        //get the menu object
        final Menu menu = engine.getMenu();

        //the starting amount of lives
        switch (menu.getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Lives))
        {
            case 0:
                hero.setLives(5);
                break;
                
            case 1:
                hero.setLives(10);
                break;
                
            case 2:
                hero.setLives(20);
                break;
                
            default:
                throw new Exception("Lives option not setup here.");
        }
        
        //create new game
        reset(engine);
    }
    
    private void reset(final Engine engine) throws Exception
    {
        //reset hero first before creating a new level
        hero.reset((level != null) ? level.isComplete() : true);
        
        //remove all existing enemies
        enemies.reset();
        
        //increase the dimensions
        mazeDimensions++;
        
        //create a new level
        this.level = new Level(mazeDimensions, Level.DEFAULT_ROOM_DIMENSION, engine.getRandom(), engine.getResources().getGameImage(GameImages.Keys.Obstacles), engine.getResources().getGameImage(GameImages.Keys.BonusItems));
    }
    
    /**
     * Get the game window
     * @return The Rectangle where game play will take place
     */
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    public Enemies getEnemies()
    {
        return this.enemies;
    }
    
    public Textures getTextures()
    {
        return this.textures;
    }
    
    public Hero getHero()
    {
        return this.hero;
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
        if (screen != null)
        {
            screen.dispose();
            screen = null;
        }

        if (hero != null)
        {
            hero.dispose();
            hero = null;
        }
        
        if (level != null)
        {
            level.dispose();
            level = null;
        }
        
        if (textures != null)
        {
            textures.dispose();
            textures = null;
        }
        
        if (enemies != null)
        {
            enemies.dispose();
            enemies = null;
        }
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
        //continue to generate level
        if (!level.isLevelCreated())
        {
            level.update(engine);
            
            if (level.isLevelCreated())
            {
                //play music
                engine.getResources().playGameAudio(GameAudio.Keys.StageMusic, true);
            }
        }
            
        if (level.isLevelCreated())
        {
            //has the level been solved
            boolean complete = level.isComplete();
            
            //only do the following if the level is not complete yet
            if (!complete)
            {
                //do the following if the hero is alive
                if (hero.hasHealth())
                {
                    //update level status
                    level.update(
                        engine.getMain().getTime(), 
                        hero.getInput().getPlayerX(), 
                        hero.getInput().getPlayerZ(), 
                        engine.getResources());

                    //update enemies in play
                    enemies.update(engine);

                    //update our hero object
                    hero.update(engine);
                
                    //write our 3d screen objects etc.. to pixel array
                    screen.renderPixelData(engine);
                }
                else
                {
                    //if "r" was pressed reset player
                    if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_R) && hero.hasLives())
                    {
                        //make sure we are no longer pressing this key
                        engine.getKeyboard().removeKeyPressed(KeyEvent.VK_R);
                        
                        //reset hero stats
                        hero.resetDeath(true);
                        
                        //start music again
                        engine.getResources().playGameAudio(GameAudio.Keys.StageMusic, true);
                    }
                }
            }
            else
            {
                //check if "y" is pressed so we know to create a new level
                if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_Y))
                {
                    //make sure we are no longer pressing this key
                    engine.getKeyboard().removeKeyPressed(KeyEvent.VK_Y);
                    
                    //create new game
                    reset(engine);
                    
                    //stop music
                    engine.getResources().stopGameAudio(GameAudio.Keys.MainTheme);
                }
            }
            
            //if the level previously wasn't finished, but is now calculate stats
            if (!complete && level.isComplete())
            {
                //calculate stats
                stats.calculateStats(enemies, level);
                
                //stop music
                engine.getResources().stopGameAudio(GameAudio.Keys.StageMusic);
                
                //play music
                engine.getResources().playGameAudio(GameAudio.Keys.MainTheme, true);
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        if (level.isLevelCreated())
        {
            if (!level.isComplete())
            {
                //draw the buffered image
                graphics.drawImage(screen.getImage(), 0, 0, window.width, window.height, null);
                
                //draw hero
                hero.render(graphics);
                
                //draw hurt overlay, if not hurt nothing will be drawn
                hero.renderHurt(graphics, window.width, window.height);
            }
            else
            {
                //draw stats in white
                graphics.setColor(Color.WHITE);
                
                //render level complete stats
                stats.render(graphics);
            }
            
            //draw hero hud
            hero.renderHud(graphics);
        }
        else
        {
            //draw progress
            level.renderProgress(graphics, progressWindow);
        }
    }
}