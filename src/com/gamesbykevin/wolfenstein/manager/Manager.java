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
import com.gamesbykevin.wolfenstein.menu.CustomMenu;
import com.gamesbykevin.wolfenstein.menu.CustomMenu.LayerKey;
import com.gamesbykevin.wolfenstein.menu.CustomMenu.OptionKey;
import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.resources.GameFont;
import com.gamesbykevin.wolfenstein.resources.GameImages;
import com.gamesbykevin.wolfenstein.resources.Resources;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
    
    //our hero in the game
    private Hero hero;
    
    //the object containing the level info
    private Level level;
    
    //private Texture wall, floor, ceiling;
    
    private Enemy soldier;
    
    //wall textures
    private Textures textures;
    
    //object that will contain all fonts for the game
    private FontManager fonts;
    
    //unique keys for each font
    private enum FontKey 
    {
        GameFont
    }
    
    //the default font size
    private static final float DEFAULT_FONT_SIZE = 24;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //calculate the game window where game play will occur
        this.window = new Rectangle(engine.getMain().getScreen());

        //create container for game font
        this.fonts = new FontManager(Resources.XML_CONFIG_GAME_FONT);

        //load all font resources
        while (!fonts.isComplete())
        {
            fonts.update(engine.getMain().getContainerClass());
        }

        //verify everything is specified
        fonts.verifyLocations(GameFont.Keys.values());

        //now update the Font with the new Font size
        fonts.set(GameFont.Keys.GameFont, fonts.get(GameFont.Keys.GameFont).deriveFont(DEFAULT_FONT_SIZE));

        double startCol = 4;
        double startRow = 4;

        this.hero = new Hero(
            engine.getResources().getGameImage(GameImages.Keys.Guns), 
            engine.getResources().getGameImage(GameImages.Keys.PlayerHud), 
            engine.getResources().getGameImage(GameImages.Keys.Mugshots));
        this.hero.setLevelLocation(startCol * 16, startRow * 16);
        
        //place the hero appropriately on the window along with the hud
        this.hero.setDimensions(192, 192);
        this.hero.setHeroLocation(window.x + (window.getWidth() / 2), window.y + window.getHeight());
        
        //create the textures for the walls
        this.textures = new Textures(engine.getResources().getGameImage(GameImages.Keys.WallTextures));

        //create a new level
        this.createLevel(8, 10, engine.getRandom(), engine.getResources().getGameImage(GameImages.Keys.Obstacles), engine.getResources().getGameImage(GameImages.Keys.BonusItems));

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
        this.soldier.setImage(engine.getResources().getGameImage(GameImages.Keys.Soldier1));

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
     * @param mazeDimensions The size of the maze
     * @param roomDimensions The size of each room
     * @throws Exception 
     */
    private void createLevel(final int mazeDimensions, final int roomDimensions, final Random random, final Image obstacleSpriteSheet, final Image bonusItemSpriteSheet) throws Exception
    {
        //create a new level
        this.level = new Level(mazeDimensions, roomDimensions, random, obstacleSpriteSheet, bonusItemSpriteSheet);
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
        window = null;
        
        screen.dispose();
        screen = null;

        hero.dispose();
        hero = null;
        
        level.dispose();
        level = null;
        
        textures.dispose();
        textures = null;
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
        if (level.isLevelCreated())
        {
            //update our hero object
            hero.update(engine);

            //update level status
            level.update(
                engine.getMain().getTime(), 
                hero.getInput().getPlayerX(), 
                hero.getInput().getPlayerZ(), 
                engine.getResources());
            
            //update soldier animation
            soldier.update(engine.getMain().getTime());

            //write our 3d screen objects etc.. to pixel array
            screen.renderPixelData(engine, soldier);
        }
        else
        {
            //continue to generate level
            level.update(engine.getRandom());
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
            //set the font
            graphics.setFont(fonts.get(GameFont.Keys.GameFont));

            //draw the buffered image
            graphics.drawImage(screen.getImage(), 0, 0, window.width, window.height, null);

            //draw hero
            hero.render(graphics);
        }
        else
        {
            //draw progress
            level.renderProgress(graphics, window);
        }
    }
}