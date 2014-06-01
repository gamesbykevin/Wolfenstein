package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.wolfenstein.display.Texture;

import com.gamesbykevin.wolfenstein.enemies.Enemies;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Point;

public final class LevelStats extends Sprite implements Disposable
{
    private String enemyRatioDescription = "";
    private String secretRatioDescription = "";
    private String treasureRatioDescription = "";
    private String timeDurationDescription = "";
    
    private static final Point LOCATION_TIME_DURATION   = new Point(215, 95);
    private static final Point LOCATION_KILL_RATIO      = new Point(260, 125);
    private static final Point LOCATION_SECRET_RATIO    = new Point(260, 143);
    private static final Point LOCATION_TREASURE_RATIO  = new Point(260, 160);
    
    //the default font size
    private static final float DEFAULT_FONT_SIZE = 20;
    
    //our image that will contain the stat numbers only
    private BufferedImage image;
    
    //our graphics object to write to the buffered image
    private Graphics2D g;
    
    //do we write image again
    private boolean change = true;
    
    public LevelStats(final Image image)
    {
        super.setImage(image);
        
        //create new buffered image
        this.image = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (this.image != null)
        {
            this.image.flush();
            this.image = null;
        }
        
        if (this.g != null)
        {
            this.g.dispose();
            this.g = null;
        }
    }
    
    public void calculateStats(final Enemies enemies, final Level level)
    {
        //flag change
        this.change = true;
        
        //set the time to display
        setTimeDurationDescription(level.getTimePassed());

        //count the total and only alive enemies
        final int totalEnemies = enemies.getCount(false);
        final int aliveEnemies = enemies.getCount(true);
        final int killedEnemies = totalEnemies - aliveEnemies;
        
        int killRatio = getRatio(killedEnemies, totalEnemies);
        
        //if 0 then ratio will be 100%
        if (totalEnemies < 1)
            killRatio = 100;
        
        setEnemyRatioDescription(killRatio + "");

        final int totalTreasureCount = level.getStartingTreasureCount();
        final int currentTreasureCount = level.getCurrentTreasureCount();
        final int foundTreasureCount = totalTreasureCount - currentTreasureCount;
        
        int treasureRatio = getRatio(foundTreasureCount, totalTreasureCount);
        
        //if 0 then ratio will be 100%
        if (totalTreasureCount < 1)
            treasureRatio = 100;
        
        setTreasureRatioDescription(treasureRatio + "");

        final int totalSecrets = level.getSecretCount(false);
        final int foundSecrets = level.getSecretCount(true);
        
        int secretRatio = getRatio(foundSecrets, totalSecrets);
        
        //if 0 then ratio will be 100%
        if (totalSecrets < 1)
            secretRatio = 100;
        
        setSecretRatioDescription(secretRatio + "");
    }
    
    private int getRatio(final int progress, final int total)
    {
        return (int)(((double)progress / (double)total) * 100);
    }
    
    private void setTimeDurationDescription(final String desc)
    {
        this.timeDurationDescription = desc;
    }
    
    private String getTimeDurationDescription()
    {
        return this.timeDurationDescription;
    }
    
    private void setSecretRatioDescription(final String desc)
    {
        this.secretRatioDescription = desc;
    }
    
    private String getSecretRatioDescription()
    {
        return this.secretRatioDescription;
    }
    
    private void setTreasureRatioDescription(final String desc)
    {
        this.treasureRatioDescription = desc;
    }
    
    private String getTreasureRatioDescription()
    {
        return this.treasureRatioDescription;
    }
    
    private void setEnemyRatioDescription(final String desc)
    {
        this.enemyRatioDescription = desc;
    }
    
    private String getEnemyRatioDescription()
    {
        return this.enemyRatioDescription;
    }
    
    public void render(final Graphics graphics)
    {
        //draw background image first
        super.draw(graphics);
        
        if (change)
        {
            change = false;
            
            if (g == null)
            {
                //create graphics object
                g = this.image.createGraphics();
            }
            
            //clear image
            g.setBackground(Texture.TRANSPARENT_COLOR);
            g.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
            
            //set font
            g.setFont(graphics.getFont().deriveFont(DEFAULT_FONT_SIZE));
            
            //write stat information
            g.drawString(getEnemyRatioDescription(),    LOCATION_KILL_RATIO.x, LOCATION_KILL_RATIO.y);
            g.drawString(getTimeDurationDescription(),  LOCATION_TIME_DURATION.x, LOCATION_TIME_DURATION.y);
            g.drawString(getSecretRatioDescription(),   LOCATION_SECRET_RATIO.x, LOCATION_SECRET_RATIO.y);
            g.drawString(getTreasureRatioDescription(), LOCATION_TREASURE_RATIO.x, LOCATION_TREASURE_RATIO.y);
        }
        
        //draw stats image
        graphics.drawImage(this.image, (int)getX(), (int)getY(), (int)getWidth(), (int)getHeight(), null);
    }
}