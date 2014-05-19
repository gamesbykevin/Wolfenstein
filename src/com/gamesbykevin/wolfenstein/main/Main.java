package com.gamesbykevin.wolfenstein.main;

import java.awt.*;
import javax.swing.*;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.shared.Shared;

public final class Main extends Thread implements Disposable
{
    //image where all game/menu elements will be written to
    private Image bufferedImage;
    
    //Graphics object used to draw buffered image
    private Graphics bufferedImageGraphics;
    
    //our dimensions for the original screen window
    private Rectangle originalSizeWindow;
    
    //our dimensions for the full screen window
    private Rectangle fullSizeWindow;
    
    //our dimensions for keeping track of the size of the current window
    private Rectangle currentWindow;
    
    //our main game engine
    private Engine engine;
    
    //how many nanoseconds are there in one second
    private static final long NANO_SECONDS_PER_SECOND = 1000000000;
    
    //need double for accuracy
    private double nanoSecondsPerUpdate;
    
    //reference to our applet
    private JApplet applet;
    
    //reference to our panel
    private JPanel panel;
    
    //cache this graphics object so we aren't constantly creating it
    private Graphics graphics;
    
    //count how many updates
    private int updates = 0;
    
    //time used to track updates per second
    private long time = System.nanoTime();
    
    public Main(final int ups, final JApplet applet)
    {
        this(ups);
        
        this.applet = applet;
    }
    
    public Main(final int ups, final JPanel panel)
    {
        this(ups);
        
        this.panel = panel;
    }
    
    /**
     * Main class that runs the game engine
     * 
     * @param ups Engine updates per second
     */
    private Main(final int ups)
    {
        //the dimensions used for original/full screen
        originalSizeWindow = new Rectangle(0, 0, Shared.ORIGINAL_WIDTH, Shared.ORIGINAL_HEIGHT);
        fullSizeWindow     = new Rectangle(originalSizeWindow);

        //duration of each update in nanoseconds
        this.nanoSecondsPerUpdate = NANO_SECONDS_PER_SECOND / ups;
    }
    
    /**
     * Mark objects for garbage collection
     */
    @Override
    public void dispose()
    {
        if (bufferedImage != null)
        {
            bufferedImage.flush();
            bufferedImage = null;
        }
        
        if (bufferedImageGraphics != null)
        {
            bufferedImageGraphics.dispose();
            bufferedImageGraphics = null;
        }
        
        if (graphics != null)
        {
            graphics.dispose();
            graphics = null;
        }
        
        if (engine != null)
        {
            engine.dispose();
            engine = null;
        }
        
        if (applet != null)
        {
            applet.destroy();
            applet = null;
        }
        
        if (panel != null)
        {
            panel.removeAll();
            panel = null;
        }
        
        originalSizeWindow = null;
        fullSizeWindow = null;
        currentWindow = null;
    }
    
    /**
     * Create our main game engine and apply input listeners
     */
    public void create() throws Exception
    {
        engine = new Engine(this);
        
        //now that engine is created apply listeners so we can detect key/mouse input
        if (applet != null)
        {
            applet.addKeyListener(engine);
            applet.addMouseMotionListener(engine);
            applet.addMouseListener(engine);
        }
        else
        {
            panel.addKeyListener(engine);
            panel.addMouseMotionListener(engine);
            panel.addMouseListener(engine);
        }
    }
    
    @Override
    public void run()
    {
        //to keep our game loop active
        boolean active = true;
        
        //previous time
        long previous = System.nanoTime();
        
        //set the current time
        time = previous;
        
        //variable to keep fps constant
        double delta = 0;
        
        while (active)
        {
            try
            {
                //get the current time
                final long now = System.nanoTime();
                
                //update these variables
                delta += ((now - previous) / nanoSecondsPerUpdate);
                
                //set the current time as the last run
                previous = now;
                
                while(delta >= 1)
                {
                    //update game
                    engine.update(this);

                    //render image
                    renderImage();
                    
                    //draw image
                    drawScreen();
                    
                    //add to our counter
                    updates++;
                    
                    //deduct from delta
                    delta--;
                    
                    //check if it is time to display ups
                    checkCount();
                }
                
                Thread.sleep(0, 10);
            }
            catch(Exception e)
            {
                //dislay error
                e.printStackTrace();

                //no longer active thread
                active = false;
            }
        }
    }
    
    private void checkCount()
    {
        //if we are debugging and 1 second passed
        if (Shared.DEBUG)
        {
            if (System.nanoTime() - time >= NANO_SECONDS_PER_SECOND)
            {
                //add 1 second to timer
                time += NANO_SECONDS_PER_SECOND;

                //display updates per second
                System.out.println("UPS = " + updates);

                //reset update counter
                updates = 0;
            }
        }
    }
    
    public JApplet getApplet()
    {
        return applet;
    }
    
    public JPanel getPanel()
    {
        return panel;
    }
    
    public Class<?> getContainerClass()
    {
        if (applet != null)
            return applet.getClass();
        
        if (panel != null)
            return panel.getClass();
        
        return null;
    }
    
    /**
     * Create buffered Image
     */
    private void createBufferedImage()
    {
        if (applet != null)
        {
            bufferedImage = applet.createImage(originalSizeWindow.width, originalSizeWindow.height);
        }
        else
        {
            bufferedImage = panel.createImage(originalSizeWindow.width, originalSizeWindow.height);
        }
    }
    
    /**
     * Get the size of the original window
     * @return Rectangle
     */
    public Rectangle getScreen()
    {
        return this.originalSizeWindow;
    }
    
    /**
     * This method will be called whenever the user turns full-screen on/off
     */
    public void setFullScreen()
    {
        if (applet != null)
        {
            fullSizeWindow = new Rectangle(0, 0, applet.getWidth(), applet.getHeight());
        }
        else
        {
            fullSizeWindow = new Rectangle(0, 0, panel.getWidth(), panel.getHeight());
        }
        
        //set the current window size
        currentWindow = new Rectangle(fullSizeWindow);
        
        //since full screen switched on/off create a new graphics object
        createGraphicsObject();
    }
    
    /**
     * Get the number of nanoseconds per each update.
     * @return long The nanosecond duration per each update which is based on updates per second.
     */
    public long getTime()
    {
        return (long)nanoSecondsPerUpdate;
    }
    
    /**
     * Writes all game/menu elements in our 
     * engine to our single bufferedImage.
     * 
     * @throws Exception 
     */
    private void renderImage() throws Exception
    {
        if (bufferedImage != null)
        {
            if (bufferedImageGraphics == null)
                bufferedImageGraphics = bufferedImage.getGraphics();
            
            //background by itself will be a black rectangle
            bufferedImageGraphics.setColor(Color.BLACK);
            bufferedImageGraphics.fillRect(0, 0, Shared.ORIGINAL_WIDTH, Shared.ORIGINAL_HEIGHT);

            engine.render(bufferedImageGraphics);
        }
        else
        {
            //create the image that will be displayed to the user
            createBufferedImage();
        }
    }
    
    /**
     * Does the applet have focus, if this is a JPanel it will always return true
     * @return boolean
     */
    public boolean hasFocus()
    {
        if (applet != null)
        {
            return applet.hasFocus();
        }
        else
        {
            //jPanel will always have focus
            return true;
        }
    }
    
    /**
     * Set the graphic object for drawing the rendered image
     */
    private void createGraphicsObject()
    {
        if (applet != null)
            graphics = applet.getGraphics();
        
        if (panel != null)
            graphics = panel.getGraphics();
    }
    
    /**
     * Draw Image onto screen
     */
    private void drawScreen()
    {
        //if no image has been rendered yet return
        if (bufferedImage == null)
            return;
        
        //cache graphics object to save resources
        if (graphics == null)
            createGraphicsObject();
        
        //make sure current window dimensions are set
        if (currentWindow == null)
            setFullScreen();
        
        try
        {
            //the destination will be the size of the window
            int dx1 = currentWindow.x;
            int dy1 = currentWindow.y;
            int dx2 = currentWindow.x + currentWindow.width;
            int dy2 = currentWindow.y + currentWindow.height;

            //the source will be the entire image
            int sx1 = 0;
            int sy1 = 0;
            int sx2 = bufferedImage.getWidth(null);
            int sy2 = bufferedImage.getHeight(null);
            
            //draw our rendered image at the specified location
            graphics.drawImage(bufferedImage, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}