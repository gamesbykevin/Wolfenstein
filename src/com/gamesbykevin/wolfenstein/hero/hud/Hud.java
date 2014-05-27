package com.gamesbykevin.wolfenstein.hero.hud;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.hero.Hero;
import com.gamesbykevin.wolfenstein.hero.weapons.Weapons;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

/**
 * Heads Up Display for Hero
 * @author GOD
 */
public final class Hud extends Sprite implements Disposable
{
    public enum HudKey
    {
        Background(0,0,320,40), Knife(0,41,48,24), Pistol(49,41,48,24), AssaultRifle(98,41,48,24), 
        MachineGun(147,41,48,24), DoorKey1(196,41,8,16), DoorKey2(205,41,8,16), 
        Text0(228,41,8,16), Text1(237,41,8,16), Text2(246,41,8,16), Text3(255,41,8,16), 
        Text4(264,41,8,16), Text5(273,41,8,16), Text6(282,41,8,16), Text7(291,41,8,16), 
        Text8(300,41,8,16), Text9(309,41,8,16);
        
        private int x, y, w, h;
        
        private HudKey(final int x, final int y, final int w, final int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
        
        public int getWidth()
        {
            return this.w;
        }
        
        public int getHeight()
        {
            return this.h;
        }
    }
    
    private enum RenderPosition
    {
        Place, RightSide, Middle
    }
    
    //placement location
    private static final Point LOCATION_BACKGROUND = new Point(0,0);
    
    //middle location
    private static final Point LOCATION_LEVEL = new Point(25,16);
    
    //right side location
    private static final Point LOCATION_SCORE = new Point(98,16);
    
    //middle location
    private static final Point LOCATION_LIVES = new Point(115,16);
    
    //right side location
    private static final Point LOCATION_HEALTH = new Point(191,16);
    
    //right side location
    private static final Point LOCATION_AMMO = new Point(230,16);
    
    //placement location
    private static final Point LOCATION_KEY = new Point(240,4);
    
    //placement location
    private static final Point LOCATION_WEAPON = new Point(255,8);
    
    //used to parse number to string
    private StringBuilder sb;
    
    public Hud(final Image heroHud)
    {
        super.setLocation(HudKey.Background.x, HudKey.Background.y);
        super.setDimensions(HudKey.Background.w, HudKey.Background.h);
        
        //store the image
        super.setImage(heroHud);
        
        //create sprite sheet
        super.createSpriteSheet();
        
        //no actual animations are here so pause
        super.getSpriteSheet().setPause(true);
        
        //add all hud items as animations
        for (HudKey key : HudKey.values())
        {
            addAnimation(key);
        }
        
        //used to parse int
        this.sb = new StringBuilder();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        sb = null;
    }
    
    private void addAnimation(final HudKey key)
    {
        //create new animation
        Animation animation = new Animation();
        
        //setup animation
        animation.add(key.x, key.y, key.w, key.h, 0);
        
        //each part of the hud will be a separate animation
        super.getSpriteSheet().add(animation, key);
        
        super.getSpriteSheet().setCurrent(key);
    }
    
    public void render(final Graphics graphics, final Hero hero) throws Exception
    {
        super.setLocation(LOCATION_BACKGROUND);
        super.setDimensions(HudKey.Background.w, HudKey.Background.h);
        super.getSpriteSheet().setCurrent(HudKey.Background);
        super.draw(graphics);

        //draw level number
        drawNumber(hero.getLevel(), LOCATION_LEVEL, RenderPosition.Middle, graphics);

        //draw score number
        drawNumber(hero.getScore(), LOCATION_SCORE, RenderPosition.RightSide, graphics);

        //draw lives number
        drawNumber(hero.getLives(), LOCATION_LIVES, RenderPosition.Middle, graphics);

        //draw health number
        drawNumber(hero.getHealth(), LOCATION_HEALTH, RenderPosition.RightSide, graphics);

        //don't draw ammo count if knife is equipped
        if (hero.getWeapons().getType() != Weapons.Type.Knife)
        {
            //draw ammo number
            drawNumber(hero.getWeapons().getAmmoCount(), LOCATION_AMMO, RenderPosition.RightSide, graphics);
        }

        //draw key if hero has it
        if (hero.hasKey())
        {
            super.setLocation(LOCATION_KEY);
            super.getSpriteSheet().setCurrent(HudKey.DoorKey1);
            super.draw(graphics);
        }


        //set location for weapon
        super.setLocation(LOCATION_WEAPON);

        switch (hero.getWeapons().getType())
        {
            case Knife:
                super.getSpriteSheet().setCurrent(HudKey.Knife);
                break;

            case Pistol:
                super.getSpriteSheet().setCurrent(HudKey.Pistol);
                break;

            case AssaultRifle:
                super.getSpriteSheet().setCurrent(HudKey.AssaultRifle);
                break;

            case MachineGun:
                super.getSpriteSheet().setCurrent(HudKey.MachineGun);
                break;

            default:
                throw new Exception("Weapon type not setup here: " + hero.getWeapons().getType());
        }

        //set the size
        super.setDimensions(HudKey.Knife.w, HudKey.Knife.h);

        //now draw the correct image
        super.draw(graphics);
        
        //draw the image
        //super.draw(graphics, finalImage);
        
        
    }
    
    private void drawNumber(final int number, final Point location, final RenderPosition position, final Graphics graphics) throws Exception
    {
        //add number to string
        sb.append(number);
                
        //where we will start drawing the number
        int startX;
        int startY = location.y;
        
        //set the start x where the number will be drawn
        switch(position)
        {
            case Place:
                startX = location.x;
                break;
                
            case RightSide:
                startX = location.x - (sb.length() * HudKey.Text0.w);
                break;
                
            case Middle:
                startX = location.x - ((sb.length() * HudKey.Text0.w) / 2);
                break;
                
            default:
                throw new Exception("Position not setup here: " + position.toString());
        }
        
        for (int i=0; i < sb.length(); i++)
        {
            HudKey key = getKey(sb.substring(i, i + 1));
            
            super.setLocation(startX + (key.w * i), startY);
            super.setDimensions(key.w, key.h);
            super.getSpriteSheet().setCurrent(key);
            super.draw(graphics);
        }
        
        //remove characters from string
        sb.setLength(0);
    }
    
    private HudKey getKey(final String number) throws Exception
    {
        switch (number)
        {
            case "0":
                return HudKey.Text0;
            case "1":
                return HudKey.Text1;
            case "2":
                return HudKey.Text2;
            case "3":
                return HudKey.Text3;
            case "4":
                return HudKey.Text4;
            case "5":
                return HudKey.Text5;
            case "6":
                return HudKey.Text6;
            case "7":
                return HudKey.Text7;
            case "8":
                return HudKey.Text8;
            case "9":
                return HudKey.Text9;
            default:
                throw new Exception("Number was not found: " + number);
        }
    }
}