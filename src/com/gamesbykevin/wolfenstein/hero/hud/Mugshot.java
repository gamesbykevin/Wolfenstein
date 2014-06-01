package com.gamesbykevin.wolfenstein.hero.hud;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import java.awt.Image;
import java.awt.Point;

public final class Mugshot extends Sprite implements Disposable
{
    //placement location
    private static final Point LOCATION_MUGSHOT = new Point(137,5);
    
    public enum MugKey
    {
        Health100(0,0,24,31,3,100),
        Health85 (0,33,24,31,3,85),
        Health70 (0,66,24,31,3,70),
        Health55 (0,99,24,31,3,55),
        Health40 (0,132,24,31,3,40),
        Health25 (0,165,24,31,3,25),
        Health10 (0,198,24,31,3,10),
        Health0  (25,231,24,28,1,0);
        
        private int x, y, w, h, count, health;
        
        private MugKey(final int x, final int y, final int w, final int h, final int count, final int health)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.count = count;
            this.health = health;
        }
    }
    
    public Mugshot(final Image mugshotImage)
    {
        //set the location
        super.setLocation(LOCATION_MUGSHOT);
        
        //set image
        super.setImage(mugshotImage);
        
        //create sprite sheet
        super.createSpriteSheet();
        
        //add all animations
        for (MugKey key : MugKey.values())
        {
            addAnimation(key);
        }
        
        //set default
        super.getSpriteSheet().setCurrent(MugKey.Health100);
        
        //set the dimensions based on the current sprite sheet animation frame
        super.setDimensions();
        
        //don't pause
        super.getSpriteSheet().setPause(false);
    }
    
    public void update(final long time) throws Exception
    {
        //update animation
        super.getSpriteSheet().update(time);
    }
    
    /**
     * Set the current mug shot animation
     * @param health The hero's health will determine the animation shown
     */
    public void setCurrent(final int health)
    {
        //default
        MugKey key = MugKey.Health100;
        
        if (health >= MugKey.Health0.health)
            key = MugKey.Health0;
        if (health >= MugKey.Health10.health)
            key = MugKey.Health10;
        if (health >= MugKey.Health25.health)
            key = MugKey.Health25;
        if (health >= MugKey.Health40.health)
            key = MugKey.Health40;
        if (health >= MugKey.Health55.health)
            key = MugKey.Health55;
        if (health >= MugKey.Health70.health)
            key = MugKey.Health70;
        if (health >= MugKey.Health85.health)
            key = MugKey.Health85;
        if (health >= MugKey.Health100.health)
            key = MugKey.Health100;
        
        //set the current animation
        super.getSpriteSheet().setCurrent(key);
    }
 
    /**
     * Add animation to sprite sheet
     * @param key Object that contains x, y, width, height etc..
     */
    private void addAnimation(final MugKey key)
    {
        Animation animation = new Animation();
        
        //all animations here will loop
        animation.setLoop(true);
        
        //add all animation(s), most will be 3 frames
        for (int i = 0; i < key.count; i++)
        {
            animation.add(key.x + ((key.w + 1) * i), key.y, key.w, key.h, Timers.toNanoSeconds(750L));
        }
        
        //add animation
        super.getSpriteSheet().add(animation, key);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}