package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.wolfenstein.hero.hud.*;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem;
import com.gamesbykevin.wolfenstein.level.objects.LevelObject;
import com.gamesbykevin.wolfenstein.hero.weapons.Weapons;
import com.gamesbykevin.wolfenstein.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

public final class Hero extends LevelObject implements Disposable
{
    //we will track player location/speed here
    private Input input;
    
    //how many keys the hero has
    private int keys = 0;
    
    //the number of lives the player has
    private int lives = 3;
    
    //the heroes health
    private int health = HEALTH_MAX;
    
    //the min/max for the health
    protected static final int HEALTH_MAX = 100;
    protected static final int HEALTH_MIN = 0;
    
    //the heroes score
    private int score = 0;
    
    //the current level we are on
    private int level = 1;
    
    //the inventory of weapons
    private Weapons weapons;
    
    //player heads-up-display
    private Hud hud;
    
    //the hero's mugshot
    private Mugshot mugshot;
    
    //where the hud is to be drawn
    private Rectangle hudLocation;
    
    //write hud info to 1 image
    private BufferedImage hudImage;
    
    //graphics object for buffered image
    private Graphics g;
    
    //used to control number of times we render our H.U.D.
    private boolean change = true;
    
    //bonus item info etc...
    protected static final int SMALL_HEALTH = 10;
    protected static final int HEALTH_KIT = 25;
    protected static final int TREASURE_1 = 100;
    protected static final int TREASURE_2 = 250;
    protected static final int TREASURE_3 = 500;
    protected static final int TREASURE_4 = 1000;
    
    public Hero(final Image spriteSheet, final Image heroHud, final Image mugshotImage) throws Exception
    {
        //set the spritesheet for the hero
        super.setImage(spriteSheet);
        
        //add animations
        super.addAnimation(Weapons.Type.Knife,        5, 0, 0, Weapons.DELAY_KNIFE, false);
        super.addAnimation(Weapons.Type.Pistol,       5, 0, 1, Weapons.DELAY_PISTOL, false);
        super.addAnimation(Weapons.Type.AssaultRifle, 5, 0, 2, Weapons.DELAY_ASSAULT_RIFLE, false);
        super.addAnimation(Weapons.Type.MachineGun,   5, 0, 3, Weapons.DELAY_MACHINEGUN, false);
        
        //pause animation by default
        super.getSpriteSheet().setPause(true);
        
        //object that will check keyboard input and collision etc...
        this.input = new Input();
        
        //create new weapon inventory object
        this.weapons = new Weapons();
        
        //set the current weapon equipped as the animation
        super.getSpriteSheet().setCurrent(weapons.getType());
        
        //create heads-up-display
        this.hud = new Hud(heroHud);
        
        //create new mugshot
        this.mugshot = new Mugshot(mugshotImage);
        
        //create buffered image
        this.hudImage = new BufferedImage(320, 40, BufferedImage.TYPE_INT_ARGB);
        
        //get graphics object
        this.g = this.hudImage.createGraphics();
    }
    
    @Override
    public void dispose()
    {
        if (weapons != null)
        {
            weapons.dispose();
            weapons = null;
        }
        
        if (input != null)
        {
            input.dispose();
            input = null;
        }
        
        if (hud != null)
        {
            hud.dispose();
            hud = null;
        }
        
        if (mugshot != null)
        {
            mugshot.dispose();
            mugshot = null;
        }
        
        if (hudLocation != null)
            hudLocation = null;
        
        if (hudImage != null)
        {
            hudImage.flush();
            hudImage = null;
        }
        
        if (g != null)
        {
            g.dispose();
            g = null;
        }
    }
    
    public void setLevelLocation(final double x, final double z)
    {
        getInput().setX(x);
        getInput().setZ(z);
    }
    
    /**
     * Add weapon/ammo to inventory. <br>
     * If we already have the weapon ammo will be added to it.<br>
     * If this is ammo then it will be added to the current equipped weapon
     * @param type The type of weapon/ammo we want to add
     */
    public void add(final BonusItem.Type type) throws Exception
    {
        switch(type)
        {
            case AmmoClip:
                weapons.add();
                break;
                
            case AssaultGun:
                weapons.add(Weapons.Type.AssaultRifle);
                break;
                
            case MachineGun:
                weapons.add(Weapons.Type.MachineGun);
                break;
                
            default:
                throw new Exception("Weapon type not setup here");
        }
    }
    
    public Input getInput()
    {
        return input;
    }
    
    public void update(final Engine engine) throws Exception
    {
        getInput().update(engine.getKeyboard(), engine.getManager().getLevel(), this, engine.getResources());
        
        //update parent object as well
        super.update(engine.getMain().getTime());
        
        //if the animation has finished
        if (super.getSpriteSheet().hasFinished())
        {
            //reset back to start
            super.getSpriteSheet().reset();
            
            //pause animation
            super.getSpriteSheet().setPause(true);
        }
        
        //update mugshot animation
        getMugshot().update(engine.getMain().getTime());
    }
    
    public void setHeroLocation(final double middleX, final double bottomY)
    {
        int width = (int)(Hud.HudKey.Background.getWidth() * 1.5);
        int height = (int)(Hud.HudKey.Background.getHeight() * 1.5);
        
        //set the hud location
        this.hudLocation = new Rectangle((int)(middleX - (width / 2)), (int)(bottomY - height), width, height);
        
        //place the player weapon right above the hud
        super.setX(middleX - (getWidth() / 2));
        super.setY(this.hudLocation.y - super.getHeight() - 1);
    }
    
    public void setLevel(final int level)
    {
        this.level = level;
    }
    
    public int getLevel()
    {
        return this.level;
    }
    
    /**
     * Does the hero have a key?
     * @return true if the hero has at least 1 key, false otherwise
     */
    public boolean hasKey()
    {
        return (this.keys > 0);
    }
    
    /**
     * Change the heroes health
     * @param change The adjustment to the health we want to make.
     */
    protected void modifyHealth(final int change)
    {
        //keep the health within range of 0 - 100
        if (getHealth() + change < HEALTH_MIN)
        {
            setHealth(HEALTH_MIN);
        }
        else if (getHealth() + change > HEALTH_MAX)
        {
            setHealth(HEALTH_MAX);
        }
        else
        {
            //make the adjustment
            setHealth(getHealth() + change);
        }
    }
    
    private void setHealth(final int health)
    {
        this.health = health;
        
        //update the current animation
        this.mugshot.setCurrent(getHealth());
        
        if(Shared.DEBUG)
            System.out.println("Health: " + this.health);
    }
    
    /**
     * Get the heroes health
     * @return the players health ranging from 0 - 100
     */
    public int getHealth()
    {
        return this.health;
    }
    
    /**
     * Add 1 key to inventory
     */
    protected void addKey()
    {
        this.keys++;
        
        if(Shared.DEBUG)
            System.out.println("Keys: " + this.keys);
    }
    
    /**
     * Remove 1 key from inventory
     */
    protected void removeKey()
    {
        this.keys--;
    }
    
    public int getLives()
    {
        return this.lives;
    }
    
    public void setLives(final int lives)
    {
        this.lives = lives;
        
        if(Shared.DEBUG)
            System.out.println("Lives: " + this.lives);
    }
    
    /**
     * Remove all keys
     */
    public void removeKeys()
    {
        this.keys = 0;
    }
    
    protected void addScore(final int score)
    {
        this.score += score;
        
        if(Shared.DEBUG)
            System.out.println("Score: " + this.score);
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    public Weapons getWeapons()
    {
        return this.weapons;
    }
    
    public Mugshot getMugshot()
    {
        return this.mugshot;
    }
    
    public Hud getHud()
    {
        return this.hud;
    }
    
    public void flagChange()
    {
        this.change = true;
    }
    
    private void setChange(final boolean change)
    {
        this.change = change;
    }
    
    private boolean hasChange()
    {
        return this.change;
    }
    
    public void render(final Graphics graphics)
    {
        try
        {
            //draw hero weapon animation
            super.draw(graphics);
            
            if (hasChange())
            {
                setChange(false);
                
                //draw hud elements etc...
                getHud().render(g, this);
            }
            
            //draw mug shot
            getMugshot().draw(g);
            
            //draw buffered image
            graphics.drawImage(hudImage, hudLocation.x, hudLocation.y, hudLocation.width, hudLocation.height, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}