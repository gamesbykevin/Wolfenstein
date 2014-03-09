package com.gamesbykevin.wolfenstein.engine;

import com.gamesbykevin.wolfenstein.main.Main;
import com.gamesbykevin.wolfenstein.manager.Manager;
import com.gamesbykevin.wolfenstein.menu.CustomMenu;
import com.gamesbykevin.wolfenstein.resources.*;
import com.gamesbykevin.wolfenstein.shared.Shared;

import com.gamesbykevin.framework.input.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public final class Engine implements KeyListener, MouseMotionListener, MouseListener, IEngine 
{
    //our Main class has important information in it so we need a reference here
    private Main main;
    
    //access this menu here
    private CustomMenu menu;
    
    //object that contains all image/audio resources in the game
    private Resources resources;
    
    //mouse object that will be recording mouse input
    private Mouse mouse;
    
    //keyboard object that will be recording key input
    private Keyboard keyboard;
    
    //original font
    private Font font;
    
    //the menu font
    private Font menuFont;
    
    //object containing all of the game elements
    private Manager manager;
    
    //object used to make random decisions
    private Random random;
    
    /**
     * The Engine that contains the game/menu objects
     * 
     * @param main Main object that contains important information so we need a reference to it
     * @throws CustomException 
     */
    public Engine(final Main main) throws Exception
    {
        this.main = main;
        this.mouse = new Mouse();
        this.keyboard = new Keyboard();
        this.resources = new Resources();
    }
    
    /**
     * Proper house-keeping
     */
    @Override
    public void dispose()
    {
        try
        {
            resources.dispose();
            resources = null;
            
            menu.dispose();
            menu = null;

            mouse.dispose();
            mouse = null;
            
            keyboard.dispose();
            keyboard = null;
            
            if (manager != null)
                manager.dispose();
            
            manager = null;
            
            random = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(Main main)
    {
        try
        {
            //if resources are still loading
            if (resources.isLoading())
            {
                resources.update(main.getContainerClass());

                //resources are now loaded so create the menu
                if (!resources.isLoading())
                {
                    //reset mouse and keyboard input
                    mouse.reset();
                    keyboard.reset();
                    
                    //create new menu
                    menu = new CustomMenu(this);
                }
            }
            else
            {
                //does the menu have focus
                if (!menu.hasFocus())
                {
                    //reset mouse and keyboard input
                    mouse.reset();
                    keyboard.reset();
                }

                //update the menu
                menu.update(this);

                //if the menu is finished and the window has focus
                if (menu.hasFinished() && menu.hasFocus())
                {
                    //NOTE: MAIN GAME LOGIC HERE
                    if (manager != null)
                        manager.update(this);
                }
                
                //if the mouse is released reset all mouse events
                if (mouse.isMouseReleased())
                    mouse.reset();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public Main getMain()
    {
        return main;
    }
    
    /**
     * Get our object used to make random decisions
     * @return Random
     */
    public Random getRandom()
    {
        return this.random;
    }
    
    /**
     * Here lies the logic to start a new game
     * 
     * @throws Exception 
     */
    @Override
    public void reset() throws Exception
    {
        //stop all sound before application starts
        getResources().stopAllSound();
        
        //reset input(s)
        getKeyboard().reset();
        getMouse().reset();
        
        //seed used to generate random numbers
        final long seed = System.nanoTime();
        
        //create new Random object
        random = new Random(seed);
        
        //display seed
        System.out.println("Seed = " + seed);
        
        //if the manager already exists release resources
        if (manager != null)
            manager.dispose();
        
        manager = new Manager(this);
    }
    
    /**
     * Draw our game to the Graphics object whether resources are still loading or the game is intact
     * @param graphics
     * @return Graphics
     * @throws Exception 
     */
    @Override
    public void render(Graphics graphics) throws Exception
    {
        //store the original font if we haven't already
        if (font == null)
            font = graphics.getFont();
        
        //if the resources are still loading
        if (resources.isLoading())
        {
            //draw loading screen
            resources.render(graphics, main.getScreen());
        }
        else
        {
            //draw application elements
            renderElements((Graphics2D)graphics);
            
            //draw menu on top of the game if visible
            renderMenu(graphics);
        }
    }
    
    /**
     * Draw our application elements
     * @param graphics2d Graphics2D object that game elements will be written to
     * @throws Exception 
     */
    private void renderElements(Graphics2D graphics) throws Exception
    {
        //NOTE: DRAW MAIN GAME HERE
        if (manager != null)
        {
            graphics.setFont(resources.getFont(GameFont.Keys.Game).deriveFont(18f));
            
            manager.render(graphics);
        }
    }
    
    /**
     * Draw the Game Menu
     * 
     * @param graphics Graphics object where Images/Objects will be drawn to
     * @throws Exception 
     */
    private void renderMenu(Graphics graphics) throws Exception
    {
        //set the appropriate menu font
        if (this.menuFont == null)
            this.menuFont = resources.getFont(GameFont.Keys.Menu).deriveFont(18f);
        
        graphics.setFont(menuFont);
        
        //if menu is setup draw menu
        if (menu.isSetup())
            menu.render(graphics);

        //if menu is finished and we don't want to hide mouse cursor then draw it, or if the menu is not finished show mouse
        if (menu.hasFinished() && !Shared.HIDE_MOUSE || !menu.hasFinished())
        {
            if (mouse.getLocation() != null)
            {
                if (resources.getGameImage(GameImage.Keys.Mouse) != null && resources.getGameImage(GameImage.Keys.MouseDrag) != null)
                {
                    if (mouse.isMouseDragged())
                    {
                        graphics.drawImage(resources.getGameImage(GameImage.Keys.MouseDrag), mouse.getLocation().x, mouse.getLocation().y, null);
                    }
                    else
                    {
                        graphics.drawImage(resources.getGameImage(GameImage.Keys.Mouse), mouse.getLocation().x, mouse.getLocation().y, null);
                    }
                }
            }
        }
    }
    
    /**
     * Object containin all of the game elements
     * @return Manager
     */
    public Manager getManager()
    {
        return this.manager;
    }
    
    public CustomMenu getMenu()
    {
        return this.menu;
    }
    
    public Resources getResources()
    {
        return resources;
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        keyboard.addKeyReleased(e.getKeyCode());
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
        keyboard.addKeyPressed(e.getKeyCode());
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
        keyboard.addKeyTyped(e.getKeyChar());
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        mouse.setMouseClicked(e);
    }
    
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouse.setMousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouse.setMouseReleased(e);
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
        mouse.setMouseEntered(e.getPoint());
    }
    
    @Override
    public void mouseExited(MouseEvent e)
    {
        mouse.setMouseExited(e.getPoint());
    }
    
    @Override
    public void mouseMoved(MouseEvent e)
    {
        mouse.setMouseMoved(e.getPoint());
    }
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        mouse.setMouseDragged(e.getPoint());
    }
    
    public Mouse getMouse()
    {
        return mouse;
    }
    
    public Keyboard getKeyboard()
    {
        return keyboard;
    }
}