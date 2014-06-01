package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.display.Render3D;
import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.level.Level;
import com.gamesbykevin.wolfenstein.resources.GameImages;
import com.gamesbykevin.wolfenstein.resources.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will manage all enemies
 * @author GOD
 */
public final class Enemies implements Disposable
{
    //list of enemies
    private List<Enemy> enemies;
    
    //if the bullet comes within this distance we have a hit
    private static final double BULLET_HIT_RANGE = (Level.WALL_D / 2);
    
    //if the bullet misses but comes within this close, alert the enemy
    private static final double BULLET_MISS_ALERT_RANGE = (BULLET_HIT_RANGE * 2);
    
    //if enemies are within the distance of an alert enemy, they will be alerted also
    private static final int ALERT_ALLY_RANGE = 6;
    
    //if the hero is this close the enemy will automatically be alerted
    private static final int ALERT_AUTO_RANGE = 4;
    
    //if the hero is this close the enemy can attack
    private static final int ATTACK_HERO_RANGE = 8;
    
    //if the hero is this close the enemy will play an alert sound once
    private static final int ALERT_HERO_RANGE = (ATTACK_HERO_RANGE * 2);
    
    //the smallest attack delay in milliseconds
    private static final int ATTACK_DELAY_MIN = 1250;
    
    public Enemies()
    {
        //create a new empty list
        this.enemies = new ArrayList<>();
    }
    
    /**
     * Add the enemy to the list
     * @param type The type of enemy
     * @param x x-coordinate
     * @param z z-coordinate
     * @param resources Object used to get enemy sprite sheet
     * @param random Object used to make random decisions
     * @throws Exception Exception will be thrown if type is not an enemy
     */
    public void add(final GameImages.Keys type, final double x, final double z, final Resources resources, final Random random) throws Exception
    {
        Enemy enemy = null;
        
        switch (type)
        {
            case Soldier1:
                enemy = new Soldier1();
                break;
                
            case Soldier2:
                enemy = new Soldier2();
                break;
                
            case Soldier3:
                enemy = new Soldier3();
                break;
                
            case BigSoldier:
                enemy = new BigSoldier();
                break;
                
            case Dog:
                enemy = new Dog();
                break;
                
            case Boss1:
                enemy = new Boss1();
                break;
                
            case Boss2:
                enemy = new Boss2();
                break;
                
            case Boss3:
                enemy = new Boss3();
                break;
                
            case Boss4:
                enemy = new Boss4();
                break;
                
            case Boss5:
                enemy = new Boss5();
                break;
                
            default:
                throw new Exception("This type is not set as an enemy: " + type.toString());
        }
        
        if (enemy != null)
        {
            //set start position
            enemy.setX(x);
            enemy.setZ(z);

            //store the sprite sheet
            enemy.setImage(resources.getGameImage(type));
            
            //create our timer for the attack delay, with a random delay
            enemy.createTimer(ATTACK_DELAY_MIN + random.nextInt(ATTACK_DELAY_MIN * 2));
            
            //the boss can move twice as fast
            enemy.setSpeed((isBoss(type) ? Enemy.VELOCITY * 2 : Enemy.VELOCITY));
            
            //add to list
            enemies.add(enemy);
        }
    }
    
    /**
     * Remove all enemies in game
     */
    public void reset()
    {
        getEnemies().clear();
    }
    
    public List<Enemy> getEnemies()
    {
        return this.enemies;
    }
    
    public boolean hasCollision(final double xLoc, final double zLoc)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get current enemy
            Enemy enemy = enemies.get(i);
            
            //don't check for collision of dead enemies
            if (enemy.isDead())
                continue;
            
            //we don't use the y-coordinate so set it to the value of z so we can calculate distance
            enemy.setY(enemy.getZ());
            
            //if the enemy is too close we have collision
            if (enemy.getDistance(xLoc, zLoc) <= (Render3D.CLIP * 2))
                return true;
        }
        
        //no collision found
        return false;
    }
    
    @Override
    public void dispose()
    {
        if (enemies != null)
        {
            for(Enemy enemy : enemies)
            {
                if (enemy != null)
                {
                    enemy.dispose();
                    enemy = null;
                }
            }
            
            enemies.clear();
            enemies = null;
        }
    }
    
    /**
     * Is there an existing boss
     * @return true if there is at least 1 boss that is alive, false otherwise
     */
    public boolean hasBoss()
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get current enemy
            Enemy enemy = enemies.get(i);
            
            //only check enemies that are alive
            if (enemy.isDead())
                continue;
            
            if (isBoss(enemy.getType()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Check if the bullet at the specified location hits an enemy
     * @param x x-coordinate
     * @param z z-coordinate
     * @param damage The damage from the weapon for 1 bulllet
     * @param resources Object used to play sound effects
     * @return true if the bullet hit an enemy, false otherwise
     */
    public boolean checkHit(final double x, final double z, final int damage, final Resources resources)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get current enemy
            Enemy enemy = enemies.get(i);
            
            //don't continue if enemy is already dead
            if (enemy.isDead())
                continue;
            
            //set value so we can calculate distance
            enemy.setY(enemy.getZ());
            
            //if the bullet is so close to enemy they have been hit
            if (enemy.getDistance(x, z) <= BULLET_HIT_RANGE)
            {
                //deduct the damage from the health
                enemy.modifyHealth(-damage);
                
                //is the enemy dead
                if (!enemy.hasHealth())
                {
                    enemy.setAnimation(Enemy.State.Death);
                    
                    //play sound effect
                    if (enemy.getAudioKeyDeath() != null)
                        resources.playGameAudio(enemy.getAudioKeyDeath());
                }
                else
                {
                    //if the enemy has the hurt animation set it
                    if (enemy.hasAnimation(Enemy.State.Hurt))
                        enemy.setAnimation(Enemy.State.Hurt);
                    
                    //the enemy was hit by a bullet so they are now alert for sure
                    enemy.setAlert(true);
                }
                
                //return true that enemy was hit
                return true;
            }
            else
            {
                //if the bullet missed but was very close, alert the soldier
                if (enemy.getDistance(x, z) <= BULLET_MISS_ALERT_RANGE)
                    enemy.setAlert(true);
            }
        }
        
        //bullet did not hit any enemies
        return false;
    }
    
    public static GameImages.Keys getRandomEnemy(final Random random, final boolean boss) throws Exception
    {
        List<GameImages.Keys> enemies = new ArrayList<>();
        
        for (int i = 0; i < GameImages.Keys.values().length; i++)
        {
            if (boss)
            {
                if (isBoss(GameImages.Keys.values()[i]))
                    enemies.add(GameImages.Keys.values()[i]);
            }
            else
            {
                if (isBasicEnemy(GameImages.Keys.values()[i]))
                    enemies.add(GameImages.Keys.values()[i]);
            }
        }
        
        if (enemies.isEmpty())
            throw new Exception("No enemies where found");
        
        return enemies.get(random.nextInt(enemies.size()));
    }
    
    public static boolean isEnemy(final GameImages.Keys type)
    {
        return (isBasicEnemy(type) || isBoss(type));
    }
    
    public static boolean isBasicEnemy(final GameImages.Keys type)
    {
        switch (type)
        {
            case Soldier1:
            case Soldier2:
            case Soldier3:
            case BigSoldier:
            //case Dog:
                return true;
            
            default:
                return false;
        }
    }
    
    public static boolean isBoss(final GameImages.Keys type)
    {
        switch (type)
        {
            case Boss1:
            case Boss2:
            case Boss3:
            case Boss4:
            case Boss5:
                return true;
            
            default:
                return false;
        }
    }
    
    /**
     * Get the number of enemies
     * @param checkAlive If true we will only count the number of alive enemies
     * @return The total number of enemies
     */
    public int getCount(final boolean checkAlive)
    {
        //if we want to count all
        if (!checkAlive)
            return enemies.size();
        
        int count = 0;
        
        for (int i = 0; i < enemies.size(); i++)
        {
            //only count enemies that are alive
            if (!enemies.get(i).isDead())
                count++;
        }
        
        return count;
    }
    
    /**
     * Notify any enemies nearby to be alert
     * @param enemy The enemy that is already alert
     */
    private void alertEnemyAllies(final Enemy enemy, final Resources resources)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            Enemy tmp = enemies.get(i);
            
            //no need to alert self
            if (tmp.getId() == enemy.getId())
                continue;
            
            //if already alerted don't continue
            if (tmp.isAlert())
                continue;
            
            //if within range alert enemy also
            if (enemy.getDistance(tmp) <= ALERT_ALLY_RANGE)
            {
                //alert enemy
                tmp.setAlert(true);
                
                //play sound
                resources.playGameAudio(tmp.getAudioKeyAlert());
            }
        }
    }
    
    public void update(final Engine engine)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            try
            {
                //get current enemy
                Enemy enemy = enemies.get(i);
                
                //if we have started the animation and finished it and we don't loop the animation
                final boolean animationComplete = (enemy.getSpriteSheet().hasStarted() && enemy.getSpriteSheet().hasFinished() && !enemy.getSpriteSheet().hasLoop());
                
                //only update the animation if we need to
                if (!animationComplete)
                {
                    //update the animation
                    enemy.update(engine.getMain().getTime());
                    
                    //if the animation has finished determine next animation
                    if (enemy.getSpriteSheet().hasFinished())
                    {
                        switch ((Enemy.State)enemy.getKey())
                        {
                            case Hurt:
                            case Attacking:
                                enemy.setAnimation(Enemy.State.Idle);
                                break;
                        }
                    }
                }
                
                //no need to continue if the enemy is dead
                if (enemy.isDead())
                    continue;
                
                //get the coordinates where the player is
                final double heroX = (engine.getManager().getHero().getInput().getX() / 16);
                final double heroZ = (engine.getManager().getHero().getInput().getZ() / 16);
                
                //set value so we can calculate distance
                enemy.setY(enemy.getZ());
                
                //if the enemy can not clearly see the hero skip
                if (!hasVision(engine.getManager().getLevel(), enemy, heroX, heroZ))
                    continue;
                    
                //is the enemy alert
                if (enemy.isAlert())
                {
                    //alert any other nearby enemy allies
                    alertEnemyAllies(enemy, engine.getResources());
                    
                    //is the enemy close enough to view the hero
                    final boolean viewHero = (enemy.getDistance(heroX, heroZ) <= ALERT_HERO_RANGE);
                    
                    //is the enemy close enough to attack the hero
                    final boolean attackHero = (enemy.getDistance(heroX, heroZ) <= ATTACK_HERO_RANGE);
                    
                    //we are within viewing range
                    if (viewHero)
                    {
                        //we are close enough to attack the hero
                        if (attackHero)
                        {
                            //make sure we have attack stance
                            if (enemy.hasAnimation(Enemy.State.AttackStance))
                            {
                                //make sure the enemy isn't hurt first
                                if (enemy.isHurt())
                                {
                                    //if we aren't attacking and not already in an attack stance
                                    if (enemy.getKey() != Enemy.State.Attacking && enemy.getKey() != Enemy.State.AttackStance)
                                        enemy.setAnimation(Enemy.State.AttackStance);
                                }
                            }

                            //if enough time has passed, attack
                            if (enemy.hasTimePassed())
                            {
                                //reset timer
                                enemy.resetTimer();

                                //set animation
                                enemy.setAnimation(Enemy.State.Attacking);

                                //deduct player health
                                engine.getManager().getHero().modifyHealth(-enemy.getDamage());

                                //play sound effect
                                engine.getResources().playGameAudio(enemy.getAudioKeyAttack());
                            }
                            else
                            {
                                //update timer
                                enemy.updateTimer(engine.getMain().getTime());
                            }
                        }
                        else
                        {
                            if (enemy.hasPath())
                            {
                                //the enemy can move if not hurt
                                if (!enemy.isHurt())
                                    enemy.updatePosition();
                            }
                            else
                            {
                                //set map if it already hasn't
                                if (!enemy.hasMap())
                                    enemy.setMap(engine.getManager().getLevel().getPlayerMap());

                                //we aren't close enough to hero so calculate a path
                                enemy.calculatePath((int)heroX, (int)heroZ, engine.getRandom());
                            }
                        }
                    }
                    else
                    {
                        //we aren't close enough so remove path
                        enemy.resetPath();
                        
                        //set enemy to be idle for now
                        setIdle(enemy);
                    }
                }
                else
                {
                    //automatically alert if the hero is too close
                    if (enemy.getDistance(heroX, heroZ) <= ALERT_AUTO_RANGE)
                    {
                        enemy.setAlert(true);
                        engine.getResources().playGameAudio(enemy.getAudioKeyAlert());
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Set the enemy idle if not already set
     * @param enemy 
     */
    private void setIdle(final Enemy enemy)
    {
        if (enemy.getKey() != Enemy.State.Idle)
            enemy.setAnimation(Enemy.State.Idle);
    }
    
    /**
     * Is the enemy able to see the hero without any walls or doors in the way
     * @param level Level to check for walls
     * @param enemy The enemy we want to check
     * @param heroX The hero location
     * @param heroZ The hero location
     * @return true if the enemy can see the hero without any wall/door in the way, false otherwise
     */
    private boolean hasVision(final Level level, final Enemy enemy, final double heroX, final double heroZ)
    {
        //our start location
        double startX = enemy.getX();
        double startZ = enemy.getZ();
        
        //we will assume there is vision if the enemy is so close
        if (enemy.getDistance(heroX, heroZ) < 1)
            return true;
        
        //calculate slope
        double slope = (heroZ - startZ) / (heroX - startX);
        
        //force the slope to be a positive number
        slope = (slope < 0) ? -slope : slope;
        
        final double velocityX = (startX > heroX) ? -.5 : .5;
        final double velocityZ = (startZ > heroZ) ? -(slope/2) : (slope/2);
        
        int count = 0;
        
        while (count < 50)
        {
            //move location
            startX += velocityX;
            startZ += velocityZ;
            
            //exit loop, we hit a solid block
            if (level.getBlock(startX, startZ).isSolid())
                break;
            
            //if there is no solid block, check if can see human
            for (int x = -1; x < 2; x++)
            {
                for (int y = -1; y < 2; y++)
                {
                    //we were able to reach the hero before hitting a solid block, we have vision
                    if ((int)(startX+x) == (int)heroX && (int)(startZ+y) == (int)heroZ)
                        return true;
                }
            }
            
            count++;
        }
        
        //we do not have vision
        return false;
    }
}