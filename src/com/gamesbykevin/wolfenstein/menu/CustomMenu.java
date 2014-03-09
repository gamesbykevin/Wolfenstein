package com.gamesbykevin.wolfenstein.menu;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.shared.IElement;

import com.gamesbykevin.framework.display.FullScreen;
import com.gamesbykevin.framework.menu.*;
import com.gamesbykevin.wolfenstein.resources.Resources;

import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 * Custom menu setup
 * @author GOD
 */
public final class CustomMenu extends Menu implements IElement
{
    //reset = create a new game
    private boolean reset = true;
    
    //object used to switch container to full screen
    private FullScreen fullScreen;
    
    //previous Layer key used so when container loses focus we remember where we were at
    private Object previousLayerKey;
    
    /**
     * identify each option we want to access, spelling should match the options id=? in the .xml file
     */
    public enum OptionKey 
    {
        Sound, FullScreen, Mode, Difficulty, Lives 
    }
    
    /**
     * unique key to identify each Layer, spelling should match the layer id=? in the .xml file
     */
    public enum LayerKey 
    {
        Initial, Credits, MainTitle, Options, OptionsInGame, NewGameConfirm, 
        ExitGameConfirm, NoFocus, GameStart, CreateNewGame, Controls, Instructions 
    }
    
    /**
     * Basic option selection on/off, must match text in menu.xml to work properly
     */
    public enum Toggle
    {
        Off, On 
    }
    
    //is sound enabled, default true
    private Toggle sound = Toggle.On;
    
    //is full screen enabled, default false
    private Toggle fullWindow = Toggle.Off;
    
    //does the container have focus
    private Toggle focus = Toggle.On;
    
    public CustomMenu(final Engine engine) throws Exception
    {
        //set the container the menu will reside within
        super(engine.getMain().getScreen(), Resources.RESOURCE_DIR + "menu.xml", engine.getMain().getContainerClass());
        
        //set the first layer
        super.setLayer(LayerKey.Initial);
        
        //set the last layer so we know when the menu has completed
        super.setFinish(LayerKey.GameStart);
    }
    
    /**
     * Update game menu
     * @param engine Our game engine containing all resources etc... needed to update menu
     * 
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //if the menu is not on the last layer we need to check for changes made in the menu
        if (!super.hasFinished())
        {
            //if we are on the main title screen and reset is not enabled
            if (super.hasCurrent(LayerKey.MainTitle) && !reset)
            {
                reset = true;
                engine.getResources().stopAllSound();
            }
            
            //the option selection for the sound and fullscreen
            Toggle tmpSound = sound, tmpFullWindow = fullWindow;
            
            //if on the options screen check if sound/fullScreen enabled
            if (super.hasCurrent(LayerKey.Options))
            {
                //tmpSound = Toggle.values()[getOptionSelectionIndex(LayerKey.Options, OptionKey.Sound)];
                tmpFullWindow = Toggle.values()[getOptionSelectionIndex(LayerKey.Options, OptionKey.FullScreen)];
            }
            
            //if on the in-game options screen check if sound/fullScreen enabled
            if (super.hasCurrent(LayerKey.OptionsInGame))
            {
                tmpSound = Toggle.values()[getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.Sound)];
                tmpFullWindow = Toggle.values()[getOptionSelectionIndex(LayerKey.OptionsInGame, OptionKey.FullScreen)];
            }
            
            //if starting a new game change layer, stop all sound
            if (super.hasCurrent(LayerKey.CreateNewGame))
            {
                //go to specified layer
                super.setLayer(LayerKey.GameStart);
                
                //mark flag that we can reset
                reset = true;
                
                //stop all sound
                engine.getResources().stopAllSound();
            }
            
            //if the values are not equal to each other a change was made
            if (tmpSound != sound)
            {
                //stop all currently playing sound
                if (tmpSound == Toggle.Off)
                    engine.getResources().stopAllSound();
                
                //turn the audio on or off
                engine.getResources().setAudioEnabled(tmpSound == Toggle.On);
                
                //store the new setting
                this.sound = tmpSound;
            }
            
            //if the values are not equal to each other a change was made
            if (tmpFullWindow != fullWindow)
            {
                if (fullScreen == null)
                    fullScreen = new FullScreen();

                //switch from fullscreen to window or vice versa
                fullScreen.switchFullScreen(engine.getMain().getApplet(), engine.getMain().getPanel());
                
                //grab the rectangle coordinates of the full screen
                engine.getMain().setFullScreen();

                this.fullWindow = tmpFullWindow;
            }
            
            //does the container have focus
            final Toggle tmpFocus = (engine.getMain().hasFocus()) ? Toggle.On : Toggle.Off;
            
            //if the values are not equal a change was made
            if (focus != tmpFocus)
            {
                //if the previous Layer is stored
                if (previousLayerKey != null)
                {
                    //set the menu to the previous Layer
                    super.setLayer(previousLayerKey);
                    
                    //there no longer is a previous Layer
                    previousLayerKey = null;
                }
                else
                {
                    //the previous Layer has not been set 
                    previousLayerKey = getKey();
                    
                    //set the current Layer to NoFocus
                    super.setLayer(LayerKey.NoFocus);
                }
                
                this.focus = tmpFocus;
            }
            
            super.update(engine.getMouse(), engine.getKeyboard(), engine.getMain().getTime());
        }
        else
        {
            //if resetGame is enabled and the menu is finished reset all game objects within engine
            if (reset)
            {
                reset = false;
                engine.reset();
            }
            
            //the menu has finished and the user has pressed 'escape' so we will bring up the in game options
            if (engine.getKeyboard().hasKeyPressed(KeyEvent.VK_ESCAPE))
            {
                super.setLayer(LayerKey.OptionsInGame);
                engine.getKeyboard().reset();
            }
        }
    }
    
    public boolean hasFocus()
    {
        return (this.focus == Toggle.On);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.render(graphics);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (fullScreen != null)
            fullScreen.dispose();
        
        fullScreen = null;
        
        previousLayerKey = null;
    }
}