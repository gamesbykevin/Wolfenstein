package com.gamesbykevin.wolfenstein.display;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Image;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Textures for the walls are defined here
 * @author GOD
 */
public final class Textures implements Disposable
{
    //contain an instance of each wall texture
    private HashMap<Key, Texture> textures;
    
    public enum Key
    {
        //level 1 textures
        Cement1(0,0),
        Cement2(2,0),
        NaziFlag(4,0),
        HitlerPortrait1(0,1),
        NaziStatue(4,1),
        
        //level 2 textures
        PrisonCell1(2,1),
        PrisonCell2(0,2),
        Blue1(2,2),
        Blue2(4,2),
        BlueSign(2,13),
        
        //level 3 textures
        NaziPortrait(0,3),
        HitlerPortrait2(2,3),
        FloorWood(4,3),
        PortraitWood(2,7),
        
        //level 4 textures
        Brick(2,5),
        BrickNaziFlag(4,5),
        PortraitBrick(2,6),
        BrickMisc(2, 12),
        
        //level 5 textures
        CementMoss1(4,7),
        CementOther(4,8),
        CementMoss2(2,8),
        CementSign(0,9),
        
        //level 6 textures
        BrownWall1(2,9),
        BrownWall2(4,9),
        BrownWall3(0,10),
        BrownWall4(2,10),
        
        //level 7 textures
        BlueSkull(0, 11),
        BlueNazi(4, 11),
        BlueOther(0, 13),
        
        //level 8 textures
        GrayPortrait(4, 10),
        GrayBrick1(2, 11),
        GrayBrick2(0, 12),
        GrayBrick3(4, 12),
        GrayBrick4(0, 14),
        HitlerPortrait4(0, 16), 
        
        //level 9 textures
        Brown1(4, 14),
        Brown2(0, 15),
        Brown3(2, 15),
        Brown4(4, 15),
        
        Ceiling1(0,7),
        DoorGoal1(0,4),
        Door1(4,4),
        GoalSwitchOff(5,6),
        GoalSwitchOn(1,7),
        DoorLocked(2, 16),
        DoorSide(4, 16),
        DoorGoal2(0, 17),
        Door2Message(2,4);

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
    
    /**
     * Create object that will contain all wall/floor/ceiling textures
     * @param image The sprite sheet containing all textures
     */
    public Textures(final Image image) throws Exception
    {
        for (int i = 0; i < Key.values().length; i++)
        {
            for (int x = 0; x < Key.values().length; x++)
            {
                //don't check same object
                if (i == x)
                    continue;
                
                if (Key.values()[i].column == Key.values()[x].column && Key.values()[i].row == Key.values()[x].row)
                    throw new Exception("Error: Same Location found for multiple Keys: " + Key.values()[i] + ", " + Key.values()[x]);
            }
        }
        
        //create new list for our textures
        this.textures = new HashMap<>();
        
        //add each texture to list
        for (Key key : Key.values())
        {
            this.textures.put(key, new Texture(image, key.getColumn(), key.getRow()));
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
    
    public Texture getTexture(final Key key)
    {
        return this.textures.get(key);
    }
}