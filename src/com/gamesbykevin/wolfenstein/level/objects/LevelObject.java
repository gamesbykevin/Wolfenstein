package com.gamesbykevin.wolfenstein.level.objects;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.wolfenstein.display.Texture;

import java.util.ArrayList;
import java.util.List;

/**
 * Enemies, Obstacles, Bonus Items are level objects
 * @author GOD
 */
public abstract class LevelObject extends Sprite implements Disposable
{
    //each animation frame on the original image is the same width/height for each level object
    private static final int DIMENSION = 64;
    
    //texture that will contain the rendering portion
    private Texture texture;
    
    //list of locations in case there are multiple bonus items of the same type
    private List<Cell> locations;
    
    protected LevelObject()
    {
        //set dimensions of each animation frame
        super.setDimensions(DIMENSION, DIMENSION);
        
        //create our sprite sheet
        super.createSpriteSheet();
        
        //create new texture
        this.texture = new Texture();
        
        //create new list
        this.locations = new ArrayList<>();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        texture.dispose();
        texture = null;
        
        locations.clear();
        locations = null;
    }
 
    public List<Cell> getLocations()
    {
        return this.locations;
    }
    
    protected void addLocation(final double x, final double z)
    {
        this.locations.add(new Cell(x, z));
    }
    
    protected void removeLocation(final double x, final double z)
    {
        for (int i=0; i < locations.size(); i++)
        {
            if (locations.get(i).equals(x, z))
            {
                locations.remove(i);
                break;
            }
        }
    }
    
    public boolean hasLocation(final double x, final double z)
    {
        for (int i=0; i < locations.size(); i++)
        {
            if (locations.get(i).equals(x, z))
                return true;
        }
        
        return false;
    }
    
    /**
     * Add animation to object
     * @param key The unique key for this animation
     * @param total The number of animaton frames
     * @param column The starting column
     * @param row The starting row
     * @param delay The time delay between each frame (nano-seconds)
     * @param loop Does this animation loop when it finishes
     */
    protected void addAnimation(final Object key, final int total, final int column, final int row, final long delay, final boolean loop)
    {
        final Animation animation = new Animation();
        
        //find our y coordinate
        final int y = row * DIMENSION;
        
        //final our x coordinate
        final int x = column * DIMENSION;
        
        //current count of animations added
        int count = 0;
        
        //add all frames to the animation
        while (count < total)
        {
            animation.add(x + (count * DIMENSION), y, DIMENSION, DIMENSION, delay);
            
            //count animation
            count++;
        }
        
        //does the animation repeat
        animation.setLoop(loop);
        
        //add animation
        super.getSpriteSheet().add(animation, key);
        
        //if the current animation has not been set yet, set a default one
        if (getKey() == null)
            setAnimation(key);
    }
    
    /**
     * Does the sprite sheet contain the specified animation
     * @param key The animation we are looking for
     * @return True if animation exists, false otherwise
     */
    public boolean hasAnimation(final Object key)
    {
        return (super.getSpriteSheet().getSpriteSheetAnimation(key) != null);
    }
    
    /**
     * Set the current animation
     * @param key The key that identifies the animation
     */
    public void setAnimation(final Object key)
    {
        super.getSpriteSheet().setCurrent(key);
    }
    
    /**
     * Get the key of the current animation
     * @return The key, if not set null is returned
     */
    public Object getKey()
    {
        return super.getSpriteSheet().getCurrent();
    }
    
    /**
     * Get pixel data
     * @return pixel array data representing the current image
     */
    public int[] getPixels()
    {
        return this.texture.getPixels();
    }
    
    /**
     * Update the current animation
     * @param time Time to deduct from remaining animation time (nano-seconds)
     * @throws Exception 
     */
    public void update(final long time) throws Exception
    {
        //update animation
        super.getSpriteSheet().update(time);
        
        //update current image
        texture.update(getImage(), getSpriteSheet().getLocation());
    }
}