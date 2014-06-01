package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.display.Textures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class LevelTextures implements Disposable
{
    //contain a list of wall texture keys for each level
    private HashMap<LevelKey, List<Textures.Key>> levelKeys;
    
    public enum LevelKey
    {
        Level1, Level2, Level3, Level4, Level5, Level6, Level7, Level8, Level9
    }
    
    //which set of textures we want
    private LevelKey levelKey;
            
    protected LevelTextures(final Random random)
    {
        this(LevelKey.values()[random.nextInt(LevelKey.values().length)]);
    }
    
    protected LevelTextures(final LevelKey levelKey)
    {
        //assign value
        this.levelKey = levelKey;
        
        //create new list of wall keys for each level
        this.levelKeys = new HashMap<>();
        
        //list of wall textures for level 1
        List<Textures.Key> textureKeysLevel1 = new ArrayList<>();
        textureKeysLevel1.add(Textures.Key.Cement1);
        textureKeysLevel1.add(Textures.Key.Cement2);
        textureKeysLevel1.add(Textures.Key.NaziFlag);
        textureKeysLevel1.add(Textures.Key.HitlerPortrait1);
        textureKeysLevel1.add(Textures.Key.NaziStatue);
        levelKeys.put(LevelKey.Level1, textureKeysLevel1);
        
        //list of wall textures for level 2
        List<Textures.Key> textureKeysLevel2 = new ArrayList<>();
        textureKeysLevel2.add(Textures.Key.PrisonCell1);
        textureKeysLevel2.add(Textures.Key.PrisonCell2);
        textureKeysLevel2.add(Textures.Key.Blue1);
        textureKeysLevel2.add(Textures.Key.Blue2);
        textureKeysLevel2.add(Textures.Key.BlueSign);
        levelKeys.put(LevelKey.Level2, textureKeysLevel2);
        
        //list of wall textures for level 3
        List<Textures.Key> textureKeysLevel3 = new ArrayList<>();
        textureKeysLevel3.add(Textures.Key.NaziPortrait);
        textureKeysLevel3.add(Textures.Key.HitlerPortrait2);
        textureKeysLevel3.add(Textures.Key.FloorWood);
        textureKeysLevel3.add(Textures.Key.PortraitWood);
        levelKeys.put(LevelKey.Level3, textureKeysLevel3);
        
        //list of wall textures for level 4
        List<Textures.Key> textureKeysLevel4 = new ArrayList<>();
        textureKeysLevel4.add(Textures.Key.Brick);
        textureKeysLevel4.add(Textures.Key.BrickNaziFlag);
        textureKeysLevel4.add(Textures.Key.PortraitBrick);
        textureKeysLevel4.add(Textures.Key.BrickMisc);
        levelKeys.put(LevelKey.Level4, textureKeysLevel4);
        
        //list of wall textures for level 5
        List<Textures.Key> textureKeysLevel5 = new ArrayList<>();
        textureKeysLevel5.add(Textures.Key.CementMoss1);
        textureKeysLevel5.add(Textures.Key.CementOther);
        textureKeysLevel5.add(Textures.Key.CementMoss2);
        textureKeysLevel5.add(Textures.Key.CementSign);
        levelKeys.put(LevelKey.Level5, textureKeysLevel5);
        
        //list of wall textures for level 6
        List<Textures.Key> textureKeysLevel6 = new ArrayList<>();
        textureKeysLevel6.add(Textures.Key.BrownWall1);
        textureKeysLevel6.add(Textures.Key.BrownWall2);
        textureKeysLevel6.add(Textures.Key.BrownWall3);
        textureKeysLevel6.add(Textures.Key.BrownWall4);
        levelKeys.put(LevelKey.Level6, textureKeysLevel6);
        
        //list of wall textures for level 7
        List<Textures.Key> textureKeysLevel7 = new ArrayList<>();
        textureKeysLevel7.add(Textures.Key.BlueSkull);
        textureKeysLevel7.add(Textures.Key.BlueNazi);
        textureKeysLevel7.add(Textures.Key.BlueOther);
        levelKeys.put(LevelKey.Level7, textureKeysLevel7);
        
        //list of wall textures for level 8
        List<Textures.Key> textureKeysLevel8 = new ArrayList<>();
        textureKeysLevel8.add(Textures.Key.GrayPortrait);
        textureKeysLevel8.add(Textures.Key.GrayBrick1);
        textureKeysLevel8.add(Textures.Key.GrayBrick2);
        textureKeysLevel8.add(Textures.Key.GrayBrick3);
        textureKeysLevel8.add(Textures.Key.GrayBrick4);
        textureKeysLevel8.add(Textures.Key.HitlerPortrait4);
        levelKeys.put(LevelKey.Level8, textureKeysLevel8);
        
        //list of wall textures for level 9
        List<Textures.Key> textureKeysLevel9 = new ArrayList<>();
        textureKeysLevel9.add(Textures.Key.Brown1);
        textureKeysLevel9.add(Textures.Key.Brown2);
        textureKeysLevel9.add(Textures.Key.Brown3);
        textureKeysLevel9.add(Textures.Key.Brown4);
        levelKeys.put(LevelKey.Level9, textureKeysLevel9);
    }
    
    @Override
    public void dispose()
    {
        this.levelKeys.clear();
        this.levelKeys = null;
    }
    
    protected void setLevelKey(final LevelKey levelKey)
    {
        this.levelKey = levelKey;
    }
    
    public Textures.Key getRandomTexture(final Random random)
    {
        //get the current list
        List<Textures.Key> textures = levelKeys.get(levelKey);
        
        //make a random selection
        return textures.get(random.nextInt(textures.size()));
    }
}