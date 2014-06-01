package com.gamesbykevin.wolfenstein.level.objects;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.wolfenstein.display.Render3D;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

/**
 * All obstacles and bonus items are level objects
 * @author GOD
 */
public final class LevelObjects implements Disposable
{
    //list of obstacles
    private List<Obstacle> obstacles;
    
    //list of bonus items
    private List<BonusItem> bonusItems;
    
    //image that contains all obstacles, bonus items
    private Image obstacleSpriteSheet, bonusItemSpriteSheet;
    
    public LevelObjects(final Image obstacleSpriteSheet, final Image bonusItemSpriteSheet)
    {
        //the images containing all the items
        this.obstacleSpriteSheet = obstacleSpriteSheet;
        this.bonusItemSpriteSheet = bonusItemSpriteSheet;
        
        //create new lists
        this.obstacles = new ArrayList<>();
        this.bonusItems = new ArrayList<>();
    }
    
    @Override
    public void dispose()
    {
        for (Obstacle obstacle : obstacles)
        {
            obstacle.dispose();
            obstacle = null;
        }
        
        obstacles.clear();
        obstacles = null;
        
        for (BonusItem bonusItem : bonusItems)
        {
            bonusItem.dispose();
            bonusItem = null;
        }
        
        bonusItems.clear();
        bonusItems = null;
    }
    
    public void addObstacle(final Obstacle.Type type, final Cell location) throws Exception
    {
        addObstacle(type, location.getCol(), location.getRow());
    }
    
    /**
     * Add obstacle of type at the specified location.<br>
     * If an item already exists at this location nothing will be added.
     * @param type The type of item we want to add
     * @param x x-location
     * @param z z-location
     * @throws Exception 
     */
    public void addObstacle(final Obstacle.Type type, final double x, final double z) throws Exception
    {
        //if item already exists don't add
        if (hasItem(x,z))
            return;
        
        //if the type already exists
        if (hasType(type))
        {
            //just add another location instead of creating a new object
            getType(type).addLocation(x, z);
        }
        else
        {
            Obstacle obstacle = new Obstacle(type);
            obstacle.addLocation(x, z);
            obstacle.setImage(this.obstacleSpriteSheet);

            //update object once so pixel data is written
            obstacle.update(0);

            //add to list
            this.obstacles.add(obstacle);
        }
    }
    
    public void addBonusItem(final BonusItem.Type type, final Cell location) throws Exception
    {
        addBonusItem(type, location.getCol(), location.getRow());
    }
    
    /**
     * Add bonus item of type at the specified location.<br>
     * If an item already exists at this location nothing will be added.
     * @param type The type of item we want to add
     * @param x x-location
     * @param z z-location
     * @throws Exception 
     */
    public void addBonusItem(final BonusItem.Type type, final double x, final double z) throws Exception
    {
        //if item already exists at this location don't add
        if (hasItem(x,z))
            return;
        
        //if the type already exists
        if (hasType(type))
        {
            //just add another location instead of creating a new object
            getType(type).addLocation(x, z);
        }
        else
        {
            BonusItem bonusItem = new BonusItem(type);
            bonusItem.addLocation(x, z);
            bonusItem.setImage(this.bonusItemSpriteSheet);

            //update object once so pixel data is written
            bonusItem.update(0);

            //add to list
            this.bonusItems.add(bonusItem);
        }
    }
    
    private Obstacle getType(final Obstacle.Type type)
    {
        for (Obstacle obstacle : getObstacles())
        {
            if (obstacle.getType() == type)
                return obstacle;
        }
        
        return null;
    }
    
    private BonusItem getType(final BonusItem.Type type)
    {
        for (BonusItem bonusItem : getBonusItems())
        {
            if (bonusItem.getType() == type)
                return bonusItem;
        }
        
        return null;
    }
    
    /**
     * Does this bonus type already exist in our list
     * @param type The type we want to check
     * @return true if the type is found in the current list, false otherwise
     */
    public boolean hasType(final BonusItem.Type type)
    {
        for (BonusItem bonusItem : getBonusItems())
        {
            if (bonusItem.getType() == type)
                return true;
        }
        
        return false;
    }
    
    /**
     * Does this obstacle type already exist in our list
     * @param type The type we want to check
     * @return true if the type is found in the current list, false otherwise
     */
    public boolean hasType(final Obstacle.Type type)
    {
        for (Obstacle obstacle : getObstacles())
        {
            if (obstacle.getType() == type)
                return true;
        }
        
        return false;
    }
    
    /**
     * Is there already a bonus item or obstacle at this location
     * @param x x-location
     * @param z z-location
     * @return true if an item exists false otherwise
     */
    public boolean hasItem(final double x, final double z)
    {
        for (int i = 0; i < getBonusItems().size(); i++)
        {
            BonusItem bonusItem = getBonusItems().get(i);
            
            if (bonusItem.hasLocation(x, z))
                return true;
        }
        
        for (int i = 0; i < getObstacles().size(); i++)
        {
            Obstacle obstacle = getObstacles().get(i);
            
            if (obstacle.hasLocation(x, z))
                return true;
        }
        
        return false;
    }
    
    /**
     * Check if the current location intersects with any of the existing obstacles.<br>
     * Some obstacles are excluded because they don't need to be checked.
     * @param x x-location
     * @param z z-location
     * @return true if collision, false otherwise
     */
    public boolean hasObstacleCollision(final double x, final double z)
    {
        for (int i = 0; i < obstacles.size(); i++)
        {
            Obstacle obstacle = obstacles.get(i);
            
            //some obstacles we don't have to check for collision
            if (!obstacle.getType().isSolid())
                continue;
            
            for (int e = 0; e < obstacle.getLocations().size(); e++)
            {
                Cell location = obstacle.getLocations().get(e);
                
                //set the current location for distance calculation
                obstacle.setX(location.getCol());
                obstacle.setY(location.getRow());
                
                if (obstacle.getDistance(x, z) <= (Render3D.CLIP * 1.25))
                    return true;
            }
        }
        
        //no collision
        return false;
    }
    
    /**
     * Get the bonus item type that is in collision with the specified x,z location.<br>
     * Behind the scenes we will also remove that bonus item
     * @param x x-location
     * @param z z-location
     * @return BonusItem found when collision, if no collision null is returned
     */
    public BonusItem.Type getBonusItemCollisionType(final double x, final double z)
    {
        
        for (int i = 0; i < getBonusItems().size(); i++)
        {
            BonusItem bonusItem = getBonusItems().get(i);
            
            for (int e = 0; e < bonusItem.getLocations().size(); e++)
            {
                Cell location = bonusItem.getLocations().get(e);
                
                //set the current location for distance calculation
                bonusItem.setX(location.getCol());
                bonusItem.setY(location.getRow());
                
                if (bonusItem.getDistance(x, z) <= (Render3D.CLIP * 1.5))
                {
                    BonusItem.Type type = bonusItem.getType();
                    
                    //remove item from list
                    this.removeBonusItem(type, bonusItem.getX(), bonusItem.getY());
                    
                    //return the type
                    return type;
                }
            }
        }
        
        //no collision
        return null;
    }
    
    /**
     * Remove the specified bonusItem found
     * @param type The item we want to remove
     * @param x x-location of the object we want to remove
     * @param z z-location of the object we want to remove
     */
    private void removeBonusItem(final BonusItem.Type type, final double x, final double z)
    {
        //get bonus type
        BonusItem bonusItem = getType(type);
        
        if (bonusItem != null)
        {
            for (Cell location : bonusItem.getLocations())
            {
                //if this is the location we want to remove
                if (location.equals(x, z))
                {
                    bonusItem.removeLocation(location.getCol(), location.getRow());
                    break;
                }
            }
            
            //if no more locations exist remove entirely from list
            if (bonusItem.getLocations().isEmpty())
                remove(type);
        }
    }
    
    private void remove(final BonusItem.Type type)
    {
        for (int i=0; i < getBonusItems().size(); i++)
        {
            if (getBonusItems().get(i).getType() == type)
            {
                getBonusItems().remove(i);
                break;
            }
        }
    }
    
    /**
     * Count the number of treasures
     * @return The current number of existing treasures
     */
    public int getTreasureCount()
    {
        int count = 0;
        
        for (BonusItem bonusItem : getBonusItems())
        {
            //if this is treasure then add the total to the count
            if (bonusItem.isTreaure())
                count += bonusItem.getLocations().size();
        }
        
        return count;
    }
    
    public List<Obstacle> getObstacles()
    {
        return this.obstacles;
    }
    
    public List<BonusItem> getBonusItems()
    {
        return this.bonusItems;
    }
}