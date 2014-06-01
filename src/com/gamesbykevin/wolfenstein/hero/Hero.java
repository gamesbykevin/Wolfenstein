package com.gamesbykevin.wolfenstein.hero;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.hero.hud.*;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem;
import com.gamesbykevin.wolfenstein.level.objects.LevelObject;
import com.gamesbykevin.wolfenstein.hero.weapons.Weapons;
import com.gamesbykevin.wolfenstein.resources.GameAudio;


import java.awt.Color;
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
    private int level = 0;
    
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
    
    //transparent red color
    private static final Color RED_COLOR = new Color(1.0f, 0.0f, 0.0f, 0.2f);
    
    //timer for how long to display red hurt image
    private Timer timer;
    
    //used to control number of times we render our H.U.D.
    private boolean change = true;
    
    //has the hero died
    private boolean flagDeath = false;
    
    //bonus item info etc...
    protected static final int SMALL_HEALTH = 10;
    protected static final int HEALTH_KIT = 25;
    protected static final int TREASURE_1 = 100;
    protected static final int TREASURE_2 = 250;
    protected static final int TREASURE_3 = 500;
    protected static final int TREASURE_4 = 1000;
    
    private static final int HERO_START_X = 2;
    private static final int HERO_START_Z = 2;
    
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
        
        //create a new timer for our red hurt image
        this.timer = new Timer(Timers.toNanoSeconds(200L));
        
        //expire the time until we need to render the red hurt image
        this.timer.update(this.timer.getReset() + 1);
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
    
    /**
     * Reset player whether new level or they died
     * @param levelComplete If true will increase current level number
     */
    public void reset(final boolean levelComplete)
    {
        //if new level increase the count
        if (levelComplete)
        {
            //keep track of the level we are on
            increaseLevel();
        }
        
        //remove all keys from hero
        removeKeys();
        
        //reset hero defaults
        resetDeath(false);
    }
    
    public void resetDeath(final boolean loseLife)
    {
        //are we deducting a life
        if (loseLife)
            setLives(getLives() - 1);
        
        //set hero back at start
        resetLocation();
        
        //reset the angle the hero is facing
        resetAngle();
        
        //flag change to update hud
        flagChange();
        
        //set health to default
        setHealth(HEALTH_MAX);
    }
    
    /**
     * Reset the heroes location to the start position
     */
    private void resetLocation()
    {
        getInput().setX(HERO_START_X * 16);
        getInput().setZ(HERO_START_Z * 16);
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
        //update timer if it has not finished
        if (!timer.hasTimePassed())
            timer.update(engine.getMain().getTime());
        
        //if the hero just died
        if (flagDeath)
        {
            flagDeath = false;
            setLives(getLives() - 1);
            engine.getResources().stopAllSound();
            engine.getResources().playGameAudio(GameAudio.Keys.HeroDeath);
        }
        
        if (hasHealth())
            getInput().update(engine);
        
        //update sprite animation
        super.getSpriteSheet().update(engine.getMain().getTime());
        
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
    
    private void increaseLevel()
    {
        this.level++;
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
    public void modifyHealth(final int change)
    {
        //keep the health within range of 0 - 100
        if (getHealth() + change < HEALTH_MIN)
        {
            setHealth(HEALTH_MIN);
            
            //flag death
            flagDeath = true;
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
        
        //if we deduct health then the player has been hurt
        if (change < 0)
        {
            //reset the time to display
            timer.reset();
        }
        
        //flag change
        flagChange();
    }
    
    private void setHealth(final int health)
    {
        this.health = health;
        
        //update the current animation
        this.mugshot.setCurrent(getHealth());
    }
    
    /**
     * Get the heroes health
     * @return the players health ranging from 0 - 100
     */
    public int getHealth()
    {
        return this.health;
    }
    
    public boolean hasHealth()
    {
        return (getHealth() > 0);
    }
    
    /**
     * Add 1 key to inventory
     */
    protected void addKey()
    {
        this.keys++;
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
    }
    
    public boolean hasLives()
    {
        return (getLives() > 0);
    }
    
    /**
     * Remove all keys
     */
    private void removeKeys()
    {
        this.keys = 0;
    }
    
    protected void addScore(final int score)
    {
        this.score += score;
    }
    
    public int getScore()
    {
        return this.score;
    }
    
    private void resetAngle()
    {
        getInput().setRotation(Math.toRadians(45));
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
        //draw hero weapon animation
        super.draw(graphics);
    }
    
    public void renderHurt(final Graphics graphics, final int w, final int h)
    {
        //if there is remaining time the hero has been hurt, or if the player is dead
        if (!timer.hasTimePassed() || !hasHealth())
        {
            graphics.setColor(RED_COLOR);
            graphics.fillRect(0, 0, w, h);
            
            //draw text if do not have health
            if (!hasHealth())
            {
                graphics.setColor(Color.WHITE);
                graphics.setFont(graphics.getFont().deriveFont(24f));
                
                if (hasLives())
                {
                    graphics.drawString("Press \"R\" to restart existing level.", (w/2) - 150, (h/2));
                }
                else
                {
                    graphics.drawString("Game Over! Press \"Esc\" to access menu.", (w/2) - 130, (h/2));
                }
            }
        }
    }
    
    public void renderHud(final Graphics graphics)
    {
        try
        {
            //if there was a change draw a new hud image
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