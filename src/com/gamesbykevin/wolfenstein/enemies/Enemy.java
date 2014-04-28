package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;

import com.gamesbykevin.wolfenstein.display.Texture;

public abstract class Enemy extends Sprite
{
    //each animation frame on the original image is the same size for each enemy
    private static final int DIMENSIONS = 64;
    
    //we will render our current enemy animation frame as a texture
    private Texture texture;
    
    /**
     * Different actions the enemy can represent
     */
    public enum State
    {
        Idle, Walking, Attacking, Hurt, Death
    }
    
    /**
     * Create new enemy
     * @param image the sprite sheet of all the enemy animation frames 
     */
    protected Enemy()
    {
        //set dimensions of each animation frame
        super.setDimensions(DIMENSIONS, DIMENSIONS);
        
        //create our sprite sheet
        super.createSpriteSheet();
        
        //create new texture
        this.texture = new Texture();
    }
    
    public void update(final long time) throws Exception
    {
        //update animation
        super.getSpriteSheet().update(time);
        
        //update current image
        texture.update(getImage(), getSpriteSheet().getLocation());
    }
    
    /**
     * Add animation to this enemy
     * @param state Which state does this animation represent
     * @param total How many animation frames
     * @param row Which row contains the animation
     * @param loop Do we want to loop the animation once finished
     */
    protected void addAnimation(final State state, final int total, final int row, final long delay, final boolean loop)
    {
        final Animation animation = new Animation();
        
        //find our y coordinate
        final int y = row * DIMENSIONS;
        
        //current count of animations added
        int count = 0;
        
        //add all frames to the animation
        while (count < total)
        {
            animation.add(count * DIMENSIONS, y, DIMENSIONS, DIMENSIONS, delay);
            
            //count animation
            count++;
        }
        
        //does the animation repeat
        animation.setLoop(loop);
        
        //add animation
        super.getSpriteSheet().add(animation, state);
        
        //if the current animation has not been set yet, set a default one
        if (getSpriteSheet().getCurrent() == null)
            getSpriteSheet().setCurrent(state);
    }
    
    /**
     * Does the sprite sheet contain the specified animation
     * @param state The animation we are looking for
     * @return True if animation is setup, false otherwise
     */
    public boolean hasAnimation(final State state)
    {
        return (super.getSpriteSheet().getSpriteSheetAnimation(state) != null);
    }
    
    /**
     * Get the current animation the enemy is in
     * @return The state, if not set null is returned
     */
    public State getState()
    {
        return (State)super.getSpriteSheet().getCurrent();
    }
    
    /**
     * Get pixel data
     * @return pixel array data representing the current image
     */
    public int[] getPixels()
    {
        return this.texture.getPixels();
    }
}