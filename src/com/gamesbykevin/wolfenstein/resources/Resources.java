package com.gamesbykevin.wolfenstein.resources;

import com.gamesbykevin.framework.resources.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.util.HashMap;

/**
 * This class will load all resources in the collection and provide a way to access them
 * @author GOD
 */
public final class Resources implements IResources
{
    //root directory of all resources
    public static final String RESOURCE_DIR = "resources/"; 
    
    //are we done loading resources
    private boolean loading = true;
    
    //all audio containers here
    private enum TypeAudio
    {
        GameAudio, 
    }
    
    //all image containers here
    private enum TypeImage
    {
        GameImage, 
    }
    
    private enum TypeText
    {
        GameText, 
    }
    
    //object containing all audio objects
    private final HashMap<Object, AudioManager> audio;
    
    //object containing all image objects
    private final HashMap<Object, ImageManager> images;
    
    //object containing all font objects
    private final GameFont fonts;
    
    //object containing all text files
    private final HashMap<Object, TextManager> text;
    
    public Resources() throws Exception
    {
        audio = new HashMap<>();
        audio.put(TypeAudio.GameAudio, new GameAudio());
        
        images = new HashMap<>();
        images.put(TypeImage.GameImage, new GameImage());
        
        fonts = new GameFont();
        
        text = new HashMap<>();
        text.put(TypeText.GameText, new GameText());
        
        //make sure each constant has been added to hashmap
        for (TypeAudio key : TypeAudio.values())
        {
            if (audio.get(key) == null)
                throw new Exception(key + " needs to be added to HashMap audio");
        }
        
        //make sure each constant has been added to hashmap
        for (TypeImage key : TypeImage.values())
        {
            if (images.get(key) == null)
                throw new Exception(key + " needs to be added to HashMap images");
        }
        
        //make sure each constant has been added to hashmap
        for (TypeText key : TypeText.values())
        {
            if (text.get(key) == null)
                throw new Exception(key + " needs to be added to HashMap text");
        }
    }
    
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
        for (Object key : audio.keySet())
        {
            audio.get(key).stopAll();
        }
    }
    
    /**
     * Here we will load the resources one by one and then marking the process finished once done
     * @param source Class in root directory of project so we have a relative location so we know how to access resources
     * @throws Exception 
     */
    @Override
    public void update(final Class source) throws Exception
    {
        if (fonts != null)
        {
            if (fonts.isLoading())
            {
                fonts.update(source);
                return;
            }
        }
        
        if (audio != null)
        {
            for (Object key : audio.keySet())
            {
                if (audio.get(key).isLoading())
                {
                    audio.get(key).update(source);
                    return;
                }
            }
        }
        
        if (images != null)
        {
            for (Object key : images.keySet())
            {
                if (images.get(key).isLoading())
                {
                    images.get(key).update(source);
                    return;
                }
            }
        }
        
        if (text != null)
        {
            for (Object key : text.keySet())
            {
                if (text.get(key).isLoading())
                {
                    text.get(key).update(source);
                    return;
                }
            }
        }
        
        //we are done loading the resources
        this.loading = false;
    }
    
    /**
     * Checks to see if audio is turned on
     * @return 
     */
    public boolean isAudioEnabled()
    {
        for (Object key : audio.keySet())
        {
            return audio.get(key).isEnabled();
        }
        
        return false;
    }
    
    /**
     * Set the audio enabled/disabled. <br>
     * If the audio is disabled even when play() is called no audio will play.
     * 
     * @param boolean Is the audio enabled 
     */
    public void setAudioEnabled(final boolean enabled)
    {
        for (Object key : audio.keySet())
        {
            audio.get(key).setEnabled(enabled);
        }
    }
    
    /**
     * Get the specified Image from the Game list
     * @param key
     * @return Image
     */
    public Image getGameImage(final Object key)
    {
        return images.get(TypeImage.GameImage).get(key);
    }
    
    public Text getGameText(final Object key)
    {
        return text.get(TypeText.GameText).get(key);
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
        audio.get(TypeAudio.GameAudio).play(key, loop);
    }
    
    public void stopGameAudio(final Object key)
    {
        audio.get(TypeAudio.GameAudio).stop(key);
    }
    
    public Font getFont(final Object key)
    {
        return this.fonts.get(key);
    }
    
    @Override
    public void dispose()
    {
        if (audio != null)
        {
            for (Object key : audio.keySet())
            {
                audio.get(key).dispose();
                audio.put(key, null);
            }
            
            audio.clear();
        }
        
        if (images != null)
        {
            for (Object key : images.keySet())
            {
                images.get(key).dispose();
                images.put(key, null);
            }
            
            images.clear();
        }
        
        fonts.dispose();
        
        if (text != null)
        {
            for (Object key : text.keySet())
            {
                text.get(key).dispose();
                text.put(key, null);
            }
            
            text.clear();
        }
    }
    
    @Override
    public void render(final Graphics graphics, final Rectangle screen)
    {
        if (!isLoading())
            return;
        
        if (fonts != null)
        {
            if (fonts.isLoading())
            {
                fonts.render(graphics, screen);
                return;
            }
        }
        
        if (audio != null)
        {
            for (Object key : audio.keySet())
            {
                if (audio.get(key).isLoading())
                {
                    audio.get(key).render(graphics, screen);
                    return;
                }
            }
        }
        
        if (images != null)
        {
            for (Object key : images.keySet())
            {
                if (images.get(key).isLoading())
                {
                    images.get(key).render(graphics, screen);
                    return;
                }
            }
        }
        
        if (text != null)
        {
            for (Object key : text.keySet())
            {
                if (text.get(key).isLoading())
                {
                    text.get(key).render(graphics, screen);
                    return;
                }
            }
        }
    }
}