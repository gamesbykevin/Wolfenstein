package com.gamesbykevin.wolfenstein.main;

import java.awt.*;
import javax.swing.*;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.shared.Shared;

public final class Main extends Thread
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
        
        //how many updates
        int updates = 0;
        
        //time used to track ups
        long  time = System.nanoTime();
        
        //previous time
        //long previous = System.nanoTime();
        
        //set the current time
        //time = previous;
        
        //double delta = 0;
        
        //how much extra time do we need to account for
        long extra = 0;
        
        while (active)
        {
            try
            {
                //get current system nano time
                long now = System.nanoTime();
                
                //update game
                engine.update(this);

                //render image
                renderImage();

                //draw image
                drawScreen();

                //add to our counter
                updates++;
                
                if (Shared.DEBUG)
                {
                    if (System.nanoTime() - time > NANO_SECONDS_PER_SECOND)
                    {
                        //add 1 second to time
                        time += NANO_SECONDS_PER_SECOND;
                        
                        //a second has passed, reset extra time counter
                        extra = 0;
                        
                        //print ups
                        System.out.println("UPS = " + updates);

                        //reset updates as well
                        updates = 0;
                    }
                }
                
                //get the finish time
                long finish = System.nanoTime();
                
                //determine how much time has passed
                long elapsed = finish - now;
                
                //if more time has elapsed than should per each update
                if (elapsed > nanoSecondsPerUpdate)
                {
                    //how much extra time do we need to account for
                    extra += (elapsed - nanoSecondsPerUpdate);
                    
                    //don't sleep thread
                    Thread.sleep(0, 0);
                }
                else
                {
                    //if there is extra time to account for we can burn some of it here
                    if (extra > 0)
                    {
                        //if the elapsed + extra time is greater than the time per update
                        if (elapsed + extra > nanoSecondsPerUpdate)
                        {
                            //determine how much time we can burn
                            long extraTimeBurned = (long)(nanoSecondsPerUpdate - elapsed);
                            
                            //add it to our total elapsed time
                            elapsed += extraTimeBurned;
                            
                            //take away from extra time we still need to burn
                            extra -= extraTimeBurned;
                        }
                        else
                        {
                            //we can burn the extra time here
                            elapsed += extra;
                            
                            //there is no more extra time
                            extra = 0;
                        }
                    }
                    
                    //count how many milliseconds are left, note: there are 1,000,000 nanoseconds in 1 millisecond
                    long milliseconds = (elapsed / 1000000);
                    
                    //take away the milliseconds left to determine the left over nanoseconds
                    int nanoseconds = (int)(elapsed - (milliseconds * 1000000));
                    
                    //sleep our thread
                    Thread.sleep(milliseconds, nanoseconds);
                }
                
                /*
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
                }
                
                if (Shared.DEBUG)
                {
                    if (System.nanoTime() - time > NANO_SECONDS_PER_SECOND)
                    {
                        time += NANO_SECONDS_PER_SECOND;

                        System.out.println("UPS = " + updates);

                        updates = 0;
                    }
                }
                
                Thread.sleep(0, 500);
                */
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
    
    /**
     * Set all objects null for garbage collection
     */
    public void dispose()
    {
        bufferedImage = null;
        bufferedImageGraphics = null;
    
        originalSizeWindow = null;
        fullSizeWindow = null;
        
        currentWindow = null;
        
        engine.dispose();
        engine = null;
        
        graphics.dispose();
        graphics = null;
    
        if (applet != null)
            applet = null;
        
        if (panel != null)
            panel = null;
    }
}