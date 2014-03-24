package com.gamesbykevin.wolfenstein.manager;

import com.gamesbykevin.wolfenstein.display.Screen;
import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.hero.Input;
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

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //the area where gameplay will occur
    private Rectangle window;
    
    //where the pixels are located to render the image
    public Screen screen;
    private BufferedImage image;
    private int[] pixels;
    
    //handle input
    private Input input;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //calculate the game window where game play will occur
        this.window = new Rectangle(engine.getMain().getScreen());
        
        this.input = new Input(engine.getResources().getGameImage(GameImage.Keys.WallTextureImage));
        
        //write image to buffered image
        BufferedImage temp = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        temp.getGraphics().drawImage(engine.getResources().getGameImage(GameImage.Keys.Soldier1), 0, 0, 64, 64, 0, 0, 64, 64, null);
        //create new canvas
        this.screen = new Screen(window.width, window.height, input, temp);
        
        //create new buffered image
        this.image = new BufferedImage(window.width, window.height, BufferedImage.TYPE_INT_RGB);
        
        //store the pixel data to our int[] array
        this.pixels = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();
        
        
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
     * Get the game window
     * @return The Rectangle where game play will take place
     */
    public Rectangle getWindow()
    {
        return this.window;
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
        input.update(engine);
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        screen.render();
        
        for (int i=0; i < window.width * window.height; i++)
        {
            this.pixels[i] = this.screen.pixels[i];
        }
        
        graphics.drawImage(image, 0, 0, window.width, window.height, null);
        
        //graphics.drawImage(screen.spriteImage, 0, 0, null);
    }
}