package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Image;
import java.util.HashMap;

/**
 * Textures for the walls are defined here
 * @author GOD
 */
public final class Textures implements Disposable
{
    private HashMap<Key, Texture> textures;
    
    public static enum Key
    {
        Ceiling1(0,7),
        Cement1(0,0),
        Cement2(2,0),
        NaziFlag(4,0),
        HitlerPortrait1(0,1),
        PrisonCell1(2,1),
        NaziStatue(4,1),
        PrisonCell2(0,2),
        Blue1(2,2),
        Blue2(4,2),
        NaziPortrait(0,3),
        HitlerPortrait2(2,3),
        FloorWood(4,3),
        DoorGoal1(0,4),
        DoorMessage(2,4),
        Door1(4,4),
        Brick(2,5),
        BrickNaziFlag(4,5),
        GoalSwitchOff(5,6),
        GoalSwitchOn(1,7),
        HitlerPortrait3(4, 10),
        HitlerPortrait4(0, 16),
        DoorLocked(2, 16),
        DoorSide(4, 16),
        DoorGoal2(0, 17);

        private final int column, row;
        
        private Key(final int column, final int row)
        {
            this.column = column;
            this.row = row;
        }
        
        public int getColumn()
        {
            return this.column;
        }
        
        public int getRow()
        {
            return this.row;
        }
    }
    
    @Override
    public void dispose()
    {
        for (Texture texture : textures.values())
        {
            if (texture != null)
            {
                texture.dispose();
                texture = null;
            }
        }
        
        textures.clear();
        textures = null;
    }
    
    /**
     * Create object that will contain all wall/floor/ceiling textures
     * @param image The sprite sheet containing all textures
     */
    public Textures(final Image image)
    {
        //create new list for our textures
        this.textures = new HashMap<>();
        
        //add each texture to list
        for (Key key : Key.values())
        {
            this.textures.put(key, new Texture(image, key.getColumn(), key.getRow()));
        }
    }
    
    public Texture getTexture(final Key key)
    {
        return this.textures.get(key);
    }
}