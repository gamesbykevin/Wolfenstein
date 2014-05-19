package com.gamesbykevin.wolfenstein.resources;

import com.gamesbykevin.framework.resources.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * This class will load all resources in the collection and provide a way to access them
 * @author GOD
 */
public final class Resources implements IResources
{
    //root directory of all resources
    private static final String RESOURCE_DIR = "resources/"; 
    
    //where our configuration file that contains the resource locations
    public static final String XML_CONFIG_GAME_AUDIO = RESOURCE_DIR + "gameAudio.xml"; 
    public static final String XML_CONFIG_GAME_FONT  = RESOURCE_DIR + "gameFont.xml"; 
    public static final String XML_CONFIG_GAME_IMAGE = RESOURCE_DIR + "gameImage.xml"; 
    public static final String XML_CONFIG_GAME_TEXT  = RESOURCE_DIR + "gameText.xml"; 
    public static final String XML_CONFIG_MENU       = RESOURCE_DIR + "menu.xml"; 
    
    //are we loading resources
    private boolean loading = true;
    
    //objects that contain resources
    private GameAudio audio;
    private GameImages images;
    private GameFont fonts;
    private GameText textFiles;
    
    public Resources() throws Exception
    {
        //object to contain audio resources
        this.audio = new GameAudio();
        
        //object to contain images resources
        this.images = new GameImages();
        
        //object to contain font resources
        this.fonts = new GameFont();
        
        //object to contain text resources
        this.textFiles = new GameText();
    }
    
    /**
     * Are we loading resources?
     * @return true if yes, false otherwise
     */
    @Override
    public boolean isLoading()
    {
        return loading;
    }
    
    /**
     * Stop all sound
     */
    public void stopAllSound()
    {
        audio.stopAll();
    }
    
    /**
     * Here we will load the resources one by one and then marking the process finished once done
     * @param source Class in root directory of project so we have a relative location so we know how to access resources
     * @throws Exception 
     */
    @Override
    public void update(final Class source) throws Exception
    {
        if (!audio.isComplete())
        {
            //load 1 resource at a time
            audio.update(source);

            //exit method so progress can be drawn
            return;
        }
        
        if (!images.isComplete())
        {
            //load 1 resource at a time
            images.update(source);

            //exit method so progress can be drawn
            return;
        }
        
        if (!fonts.isComplete())
        {
            //load 1 resource at a time
            fonts.update(source);

            //exit method so progress can be drawn
            return;
        }
        
        if (!textFiles.isComplete())
        {
            //load 1 resource at a time
            textFiles.update(source);

            //exit method so progress can be drawn
            return;
        }
        
        //verify all existing keys are contained in the xml file
        audio.verifyLocations(GameAudio.Keys.values());
        
        //verify all existing keys are contained in the xml file
        images.verifyLocations(GameImages.Keys.values());
        
        //verify all existing keys are contained in the xml file
        textFiles.verifyLocations(GameText.Keys.values());
        
        //verify all existing keys are contained in the xml file
        fonts.verifyLocations(GameFont.Keys.values());
        
        //we are done loading the resources
        this.loading = false;
    }
    
    /**
     * Checks to see if audio is turned on
     * @return 
     */
    public boolean isAudioEnabled()
    {
        return audio.isEnabled();
    }
    
    /**
     * Set the audio enabled/disabled. <br>
     * @param boolean Is the audio enabled 
     */
    public void setAudioEnabled(final boolean enabled)
    {
        audio.setEnabled(enabled);
    }
    
    /**
     * Get the specified Image
     * @param key
     * @return Image
     */
    public Image getGameImage(final Object key)
    {
        return images.get(key);
    }
    
    public Text getGameText(final Object key)
    {
        return textFiles.get(key);
    }
    
    /**
     * Play game audio with no loop
     * @param key 
     */
    public void playGameAudio(final Object key)
    {
        playGameAudio(key, false);
    }
    
    public void playGameAudio(final Object key, final boolean loop)
    {
        audio.play(key, loop);
    }
    
    public void stopGameAudio(final Object key)
    {
        audio.stop(key);
    }
    
    public Font getFont(final Object key)
    {
        return fonts.get(key);
    }
    
    @Override
    public void dispose()
    {
        if (audio != null)
        {
            audio.dispose();
            audio = null;
        }
        
        if (images != null)
        {
            images.dispose();
            images = null;
        }
        
        if (textFiles != null)
        {
            textFiles.dispose();
            textFiles = null;
        }
        
        if (fonts != null)
        {
            fonts.dispose();
            fonts = null;
        }
    }
    
    @Override
    public void render(final Graphics graphics, final Rectangle screen)
    {
        if (!isLoading())
            return;
        
        if (audio != null)
        {
            if (!audio.isComplete())
            {
                audio.render(graphics, screen);
                return;
            }
        }
        
        if (images != null)
        {
            if (!images.isComplete())
            {
                images.render(graphics, screen);
                return;
            }
        }
        
        if (textFiles != null)
        {
            if (!textFiles.isComplete())
            {
                textFiles.render(graphics, screen);
                return;
            }
        }
        
        if (fonts != null)
        {
            if (!fonts.isComplete())
            {
                fonts.render(graphics, screen);
                return;
            }
        }
    }
}