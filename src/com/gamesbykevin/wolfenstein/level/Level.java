package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.resources.Progress;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.wolfenstein.display.Render3D;
import com.gamesbykevin.wolfenstein.display.Textures.*;
import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem;
import com.gamesbykevin.wolfenstein.level.objects.LevelObjects;
import com.gamesbykevin.wolfenstein.resources.Resources;
import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Level extends BlockManager implements Disposable
{
    //the overall maze of the level, each cell will represent a room
    private Labyrinth maze;
    
    //here we will manage the bonus items and obstacles in the level
    private LevelObjects objects;
    
    //game timer to keep track of time passed
    private Timer timer;
    
    //the total number of treasures in this level
    private int treasureCount = 0;
    
    //has the switch been hit
    private boolean complete = false;
    
    //keep track of the secret rooms
    private List<Cell> secrets;
    
    //locate the room next to the goal room
    private Cell beforeGoal = new Cell();
    
    //player map so we can direct cpu towards goal
    private List<Location> playerMap;
    
    //the size of each room
    private final int roomDimensions;
    
    //minimum size for the room and maze
    private static final int MINIMUM_ROOM_DIMENSION = 8;
    public static final int MINIMUM_MAZE_DIMENSION = 3;
    
    //how big is each room
    public static final int DEFAULT_ROOM_DIMENSION = 10;
    
    //check for wall collision within this distance
    public static final double WALL_D = 1.0;//.950;
    
    //locate the group of textures we will use for this level
    private LevelTextures levelKeys;
    
    /**
     * The different options for each border in each room
     * Open - no walls
     * Closed - all walls
     * Door - all walls with a door in the middle
     */
    public enum State
    {
        Open, Closed, Door
    }
    
    /**
     * Track level generation
     */
    public enum Steps
    {
        Step1, Step2, Step3, Step4, Step5, Step6, Step7, Step8, Step9, Step10, Step11, Step12
    }
    
    //keep track of current step
    private int stepIndex = 0;
    
    //object to track level creation
    private Progress progress;
    
    /**
     * Create a new level
     * @param mazeDimensions The size of the maze 
     * @param roomDimensions The size of each room
     * @throws Exception 
     */
    public Level(final int mazeDimensions, final int roomDimensions, final Random random, final Image obstacleSpriteSheet, final Image bonusItemSpriteSheet) throws Exception
    {
        super((mazeDimensions * roomDimensions) + mazeDimensions + 1, (mazeDimensions * roomDimensions) + mazeDimensions + 1);
        
        if (roomDimensions < MINIMUM_ROOM_DIMENSION)
            throw new Exception("The minimum allowed room dimension size is: " + MINIMUM_ROOM_DIMENSION);
        
        if (roomDimensions % 2 != 0)
            throw new Exception("Room dimensions have to be an even number. " + roomDimensions);
        
        if (mazeDimensions < MINIMUM_MAZE_DIMENSION)
            throw new Exception("The minimum allowed maze dimension size is: " + MINIMUM_MAZE_DIMENSION);
        
        //create progress object
        this.progress = new Progress(Steps.values().length);
        this.progress.setDescription("Creating Level");
        
        //create new timer
        this.timer = new Timer();
        
        //our list of secret rooms
        this.secrets = new ArrayList<>();
        
        //create player map
        this.playerMap = new ArrayList<>();
        
        //store the size of each room
        this.roomDimensions = roomDimensions;
        
        //pick a random set of textures for this level
        this.levelKeys = new LevelTextures(random);
        
        //create the object that will have bonus items/obstacles etc..
        this.objects = new LevelObjects(obstacleSpriteSheet, bonusItemSpriteSheet);
        
        //pick a random maze generation algorithm
        Labyrinth.Algorithm algorithm = Labyrinth.Algorithm.values()[random.nextInt(Labyrinth.Algorithm.values().length)];
        
        //generate our overall maze, each cell in this maze will represent a room
        this.maze = new Labyrinth(mazeDimensions, mazeDimensions, algorithm);
        this.maze.setStart(0, 0);
        this.maze.update();
        this.maze.getProgress().setDescription("Creating maze");
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (maze != null)
        {
            maze.dispose();
            maze = null;
        }
        
        if (objects != null)
        {
            objects.dispose();
            objects = null;
        }
        
        if (levelKeys != null)
        {
            levelKeys.dispose();
            levelKeys = null;
        }
        
        if (secrets != null)
        {
            secrets.clear();
            secrets = null;
        }
        
        if (progress != null)
        {
            progress.dispose();
            progress = null;
        }
    }
    
    /**
     * Get the size of each room
     * @return The size of each room, the width/height will be the same
     */
    protected int getRoomDimensions()
    {
        return this.roomDimensions;
    }
    
    private void createGoalRoom() throws Exception
    {
        //get goal location
        Location tmp = getMaze().getLocation(getMaze().getFinish());

        for (Location.Wall wall : Location.Wall.values())
        {
            //if there isn't a wall make a door
            if (!tmp.hasWall(wall))
            {
                RoomHelper.changeBorder(this, wall, Level.State.Door, tmp, false, true, false);
                
                //locate the room opposite the goal door
                switch (wall)
                {
                    case East:
                        beforeGoal.setCol(tmp.getCol() + 1);
                        beforeGoal.setRow(tmp.getRow());
                        break;
                        
                    case West:
                        beforeGoal.setCol(tmp.getCol() - 1);
                        beforeGoal.setRow(tmp.getRow());
                        break;
                        
                    case North:
                        beforeGoal.setCol(tmp.getCol());
                        beforeGoal.setRow(tmp.getRow() - 1);
                        break;
                        
                    case South:
                        beforeGoal.setCol(tmp.getCol());
                        beforeGoal.setRow(tmp.getRow() + 1);
                        break;
                }
            }
            else
            {
                //where there is a wall there will be a goal switch
                RoomHelper.changeBorder(this, wall, Level.State.Closed, tmp, false, true, false);
            }
        }
    }
    
    private void createLockedDoor(final Random random) throws Exception
    {
        //optional rooms where the door can be locked
        List<Cell> options = RoomHelper.getLockedDoorOptions(maze, random, secrets);
        
        //options exist to lock a door, so lets see if there is a place to put the key where the player can get to
        if (!options.isEmpty())
        {
            //pick random room to lock
            Cell location = new Cell(options.get(random.nextInt(options.size())));
            
            //determine which wall will be locked in that specific room
            Location.Wall locked = RoomHelper.getLockedDoorWall(maze, random, location);
            
            //locate valid options to place the key based on where locked room is
            options = RoomHelper.getKeyPlacementOptions(maze, random, location, secrets);
            
            //there is a valid room to place a key
            if (!options.isEmpty())
            {
                if (Shared.DEBUG)
                    System.out.println("Locked Door in room (" + location.getCol() + "," + location.getRow() + ")");
                
                //lock the door at this wall
                RoomHelper.changeBorder(this, locked, State.Door, location, false, false, true);
                
                //get a random valid option to where the key is to be placed
                location = options.get(random.nextInt(options.size()));
                
                if (Shared.DEBUG)
                    System.out.println("Key placed in room (" + location.getCol() + "," + location.getRow() + ")");
                
                //get a random list of options at this specific location
                List<Cell> tmp = RoomHelper.getOptions(this, location);
                
                //place key somewhere inside a valid room, pick random key texture since it doesn't matter
                objects.addBonusItem((random.nextBoolean()) ? BonusItem.Type.Key1 : BonusItem.Type.Key2, tmp.get(random.nextInt(tmp.size())));
            }
        }
        
        //recycle unused objects
        options.clear();
        options = null;
    }
    
    private void createRooms(final Random random) throws Exception
    {
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                //get the current maze location
                Location location = maze.getLocation(col, row);
                
                //create the room
                RoomHelper.createRoom(this, location, random);
            }
        }
    }
    
    protected Labyrinth getMaze()
    {
        return this.maze;
    }
    
    /**
     * Locate the room where the goal will be.<br>
     * This room will have the switch for the player to complete the level.<br>
     * Here we also will choose a wall in the goal room where the switch will be.
     * @throws Exception 
     */
    private void locateGoal() throws Exception
    {
        //the cost to reach the specific location
        int cost = 0;
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int column = 0; column < maze.getCols(); column++)
            {
                //get the cost of the current location
                final int tmpCost = maze.getLocation(column, row).getCost();
                
                //the location with the highest cost will be the goal
                if (tmpCost >= cost)
                {
                    //set the new cost limit
                    cost = tmpCost;
                    
                    //now we have a new goal set the finish
                    this.maze.setFinish(column, row);
                }
            }
        }
        
        if (Shared.DEBUG)
            System.out.println("Goal room is (" + maze.getFinish().getCol() + "," + maze.getFinish().getRow() + ")");
    }
    
    /**
     * Create secret rooms in our level.<br>
     * These rooms will not be required to solve the maze and simply are bonus rooms.
     * @param random Object used to make random decisions
     * @throws Exception 
     */
    private void createSecrets(final Random random) throws Exception
    {
        //determine a limit on the number of secret rooms
        final int secretLimit = (int)(maze.getCols() / 2);
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                boolean valid = true;
                
                //the secret room can't be the start/finish and it can't border it either
                for (int x = -1; x <= 1; x++)
                {
                    if (!valid)
                        break;
                    
                    for (int y = -1; y <= 1; y++)
                    {
                        if (!valid)
                            break;
                        
                        if (maze.getStart().equals(col + x, row + y))
                            valid = false;
                        if (maze.getFinish().equals(col + x, row + y))
                            valid = false;
                    }
                }
                
                //if this isn't valid skip
                if (!valid)
                    continue;
             
                //get the current location
                Location location = maze.getLocation(col, row);
                
                //a potential secret room will have to have 3 walls
                if (location.getWalls().size() != 3)
                    continue;
                
                //now choose at random if this is to be a secret room
                if (!random.nextBoolean())
                    continue;
                
                //check each side to determine how the secret will be created
                for (Location.Wall wall : Location.Wall.values())
                {
                    //we only want the side where there isn't a wall because that is where the secret door will be
                    if (location.hasWall(wall))
                        continue;
                    
                    //for debugging purposes
                    if (Shared.DEBUG)
                        System.out.println("Secret Added wall = " + wall.toString() + ", room = (" + col + "," + row + ")");
                    
                    //change the appropriate border to be a secret door
                    RoomHelper.changeBorder(this, wall, State.Door, location, true, false, false);
                        
                    //add to list
                    secrets.add(location);
                    
                    //if we have reached our limit don't continue
                    if (secrets.size() >= secretLimit)
                        return;
                }
            }
        }
    }
    
    private void placeEnemies(final Engine engine) throws Exception
    {
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                //don't place enemies at the start or finish
                if (maze.getStart().equals(col, row) || maze.getFinish().equals(col, row))
                    continue;
                
                boolean valid = true;
                
                //don't place enemies in the secret rooms either
                for (int i = 0; i < secrets.size(); i++)
                {
                    if (secrets.get(i).equals(col, row))
                    {
                        valid = false;
                        break;
                    }
                }
                
                if (!valid)
                    continue;
                
                //if we are in the room before the goal room, we can add bosses here
                boolean canAddBoss = beforeGoal.equals(col, row);
                
                //place enemies in the specified room
                RoomHelper.placeEnemies(maze.getLocation(col, row), engine.getRandom(), engine.getManager().getEnemies(), this, engine.getResources(), canAddBoss);
            }
        }
    }
    
    /**
     * Add the items to the level at random
     * @param random Object used to make random decisions
     */
    private void populateRooms(final Random random) throws Exception
    {
        //get list of valid bonus types
        List<BonusItem.Type> types = RoomHelper.getBonusTypesList();
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                //don't add items to start or finish
                if (maze.getStart().equals(col, row) || maze.getFinish().equals(col, row))
                    continue;
                
                //choose randomly if bonus items are to be added to this room
                boolean addBonus = random.nextBoolean();
                
                //is this location a secret room
                boolean secret = false;
                
                //if a secret room we will definitely add bonuses
                for (Cell cell : secrets)
                {
                    if (cell.equals(col, row))
                    {
                        secret = true;
                        addBonus = true;
                        break;
                    }
                }
                
                //do we add bonuses in this room
                if (addBonus)
                    RoomHelper.addBonusItems(this, new Cell(col, row), objects, random, types, secret);
                
                //place obstacles
                RoomHelper.placeObstacles(this, objects, new Cell(col, row), random);
            }
        }
        
        //now set the total number of treasures so we can track completion
        this.treasureCount = getCurrentTreasureCount();
        
        if (Shared.DEBUG)
            System.out.println("Total treasure count :" + treasureCount);
    }
    
    /**
     * Get the initial treasure count when the level is first created
     * @return total number of existing treasures
     */
    public int getStartingTreasureCount()
    {
        return this.treasureCount;
    }
    
    /**
     * Get the current existing treasure count
     * @return total number of existing treasures
     */
    public int getCurrentTreasureCount()
    {
        return objects.getTreasureCount();
    }
    
    /**
     * Get the secret count
     * @param countVisited If true only count the secret doors visited, otherwise count all secret doors
     * @return The total number of secret doors in this level, each secret room has 1 secret door
     */
    public int getSecretCount(final boolean countVisited)
    {
        int count = 0;
        
        for (int row = 0; row < getRowCount(); row++)
        {
            for (int col = 0; col < getColumnCount(); col++)
            {
                Block block = get(col, row);
                
                //can't be a secret door
                if (!block.isSolid())
                    continue;
                
                //if a secret door add to our count
                if (block.isDoor() && block.getDoor().isSecret())
                {
                    //are we only counting the secret doors visited
                    if (countVisited)
                    {
                        if (block.hasVisited())
                            count++;
                    }
                    else
                    {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Assign textures to the solid blocks
     * @throws Exception 
     */
    private void assignTextures(final Random random) throws Exception
    {
        for (int row = 0; row < getRowCount(); row++)
        {
            for (int col = 0; col < getColumnCount(); col++)
            {
                RoomHelper.assignTexture(this, super.get(col, row), col, row, levelKeys.getRandomTexture(random), random);
            }
        }
    }
    
    /**
     * Get the block at the specified location
     * @param x x-location
     * @param z z-location
     * @return The block at the location, if not found a static SolidBlock is returned
     */
    public Block getBlock(final double x, final double z)
    {
        return super.get(x, z);
    }
    
    public LevelObjects getLevelObjects()
    {
        return this.objects;
    }
    
    /**
     * Mark the block as complete which will indicate the level has been solved.<br>
     * We will also change the wall texture of the switch from off to on to indicate the switch is hit.<br>
     * @param block The block we want to flag as switched
     * @throws Exception 
     */
    public void markComplete(final Block block) throws Exception
    {
        if (block.getWest() == Key.GoalSwitchOff)
            block.setWest(Key.GoalSwitchOn);
        if (block.getEast() == Key.GoalSwitchOff)
            block.setEast(Key.GoalSwitchOn);
        if (block.getNorth() == Key.GoalSwitchOff)
            block.setNorth(Key.GoalSwitchOn);
        if (block.getSouth() == Key.GoalSwitchOff)
            block.setSouth(Key.GoalSwitchOn);
                
        this.complete = true;
    }
    
    /**
     * Has the level been solved.
     * @return true if the level has been solved, false otherwise
     */
    public boolean isComplete()
    {
        return this.complete;
    }
    
    public Progress getProgress()
    {
        return this.progress;
    }
    
    /**
     * Is the player's location inside the goal room
     * @param playerX Current player's location 
     * @param playerZ Current player's location 
     * @return true if the provided location is inside the goal room, false otherwise
     */
    public boolean insideGoal(final double playerX, final double playerZ) throws Exception
    {
        return maze.getFinish().equals(getPlayerColumn(playerX), getPlayerRow(playerZ));
    }
    
    private int getPlayerColumn(final double playerX)
    {
        return (int)(playerX / ((getColumnCount() + 2 - maze.getCols()) / maze.getCols()));
    }
    
    /**
     * Has the level been created
     * @return true if the last step has been reached in level generation, false otherwise
     */
    public boolean isLevelCreated()
    {
        return (this.stepIndex == Steps.values().length - 1);
    }
    
    private int getPlayerRow(final double playerZ)
    {
        return (int)(playerZ / ((getRowCount() + 2 - maze.getRows()) / maze.getRows()));
    }
    
    /**
     * Check for collision with walls and obstacles in a level
     * @param xLoc x-location
     * @param zLoc z-location
     * @return true if we hit a wall, false otherwise
     */
    public boolean hasCollision(final double xLoc, final double zLoc)
    {
        try
        {
            //temp block object
            Block block;

            //the current new block the player will be in
            Block current = getBlock(xLoc, zLoc);
            
            //if the current block is not a door
            if (!current.isDoor())
            {
                //west
                block = getBlock(xLoc - WALL_D, zLoc);

                if (hasCollision(block))
                    return true;

                //east
                block = getBlock(xLoc + WALL_D, zLoc);

                if (hasCollision(block))
                    return true;

                //north
                block = getBlock(xLoc, zLoc - WALL_D);
                
                if (hasCollision(block))
                    return true;

                //south
                block = getBlock(xLoc, zLoc + WALL_D);
                
                if (hasCollision(block))
                    return true;

                /**
                 * now check the corners at a closer distance
                 */

                //north west
                block = getBlock(xLoc - Render3D.CLIP, zLoc - Render3D.CLIP);
                
                if (hasCollision(block))
                    return true;

                //north east
                block = getBlock(xLoc + Render3D.CLIP, zLoc - Render3D.CLIP);

                if (hasCollision(block))
                    return true;
                
                //south west
                block = getBlock(xLoc - Render3D.CLIP, zLoc + Render3D.CLIP);

                if (hasCollision(block))
                    return true;
                
                //south east
                block = getBlock(xLoc + Render3D.CLIP, zLoc + Render3D.CLIP);
                
                if (hasCollision(block))
                    return true;
            }
            else
            {
                //if the door is open check walls next to door
                if (current.getDoor().isOpen())
                {
                    //west
                    block = getBlock(xLoc - Render3D.CLIP, zLoc);

                    if (hasCollision(block))
                        return true;

                    //east
                    block = getBlock(xLoc + Render3D.CLIP, zLoc);

                    if (hasCollision(block))
                        return true;

                    //north
                    block = getBlock(xLoc, zLoc - Render3D.CLIP);

                    if (hasCollision(block))
                        return true;

                    //south
                    block = getBlock(xLoc, zLoc + Render3D.CLIP);

                    if (hasCollision(block))
                        return true;
                }
                else
                {
                    //if door is not open we have collision
                    return true;
                }
            }
            
            //check if any collisions with obstacles
            if (getLevelObjects().hasObstacleCollision(xLoc, zLoc))
                return true;
        }
        catch(Exception e)
        {
            //print the error
            e.printStackTrace();
        }
        
        //no collision
        return false;
    }
    
    /**
     * Check the block status to determine if there is a collision
     * @param block The block we want to check
     * @return true will be returned for the following conditions.<br>
     * 1. If the block is solid and isn't a door.<br>
     * 2. If the block is solid and and is a door but the door isn't open.
     */
    private boolean hasCollision(final Block block)
    {
        //is this block solid
        if (block.isSolid())
        {
            //if it is not a door we have collision
            if (!block.isDoor())
                return true;

            //if it is a door but not fully open we have collision
            if (!block.getDoor().isOpen())
                return true;
        }
        
        //no collision was detected
        return false;
    }
    
    /**
     * Get the player map
     * @return The map of the level from the ai perspective so we know how to move in the level
     */
    public List<Location> getPlayerMap()
    {
        return this.playerMap;
    }
    
    private void createPlayerMap()
    {
        for (int row = 0; row < super.getRowCount(); row++)
        {
            for (int col = 0; col < super.getColumnCount(); col++)
            {
                //get the block at the current location
                Block block = super.get(col, row);
                
                //create new location
                Location location = new Location(col, row);
                
                //make sure there are no obstacles in this position
                if (!getLevelObjects().hasItem(col, row))
                {
                    //if the block isn't solid, or if it is solid and a door remove all walls
                    if (!block.isSolid() || (block.isSolid() && block.isDoor()))
                    {
                        for (Location.Wall wall : Location.Wall.values())
                            location.remove(wall);
                    }
                }
                
                //add location to map
                playerMap.add(location);
            }
        }
    }

    /**
     * Here we setup the level
     * @param engine Object used that contains everything we need
     * @throws Exception 
     */
    public void update(final Engine engine) throws Exception
    {
        final Random random = engine.getRandom();
        
        switch (getStep())
        {
            case Step1:
                
                //generate the maze until finished
                if(!maze.isComplete())
                    maze.update();
                
                if (maze.isComplete())
                {
                    //progress to next step
                    nextStep();
                }
                break;
                
            case Step2:
                //locate the goal for our maze
                locateGoal();
                
                //progress to next step
                nextStep();
                break;
                
            case Step3:
                //determine what sides are open/closed/door etc..
                createRooms(random);
                
                //progress to next step
                nextStep();
                break;
                
            case Step4:
                //createSecrets
                createSecrets(random);
                
                //progress to next step
                nextStep();
                break;
                
            case Step5:
                //lock a random door that the player has to access to solve the level
                createLockedDoor(random);
                
                //progress to next step
                nextStep();
                break;
                
            case Step6:
                //populate obstacles/bonuses to level
                populateRooms(random);
                
                //progress to next step
                nextStep();
                break;
                
            case Step7:
                //make the location where the goal is a room
                createGoalRoom();
                
                //progress to next step
                nextStep();
                break;
                
            case Step8:
                //finally fill any null block with an empty block
                fill();
                
                //progress to next step
                nextStep();
                break;
                
            case Step9:
                placeEnemies(engine);
                
                //progress to next step
                nextStep();
                break;
                
            case Step10:
                //create the map so the artificial intelligence knows how to navigate
                createPlayerMap();
                
                //progress to next step
                nextStep();
                break;
                
            case Step11:
                //now set the wall textures for the rooms
                assignTextures(random);
                
                //progress to next step
                nextStep();
                break;
                
            default:
                throw new Exception("Step not setup here \"" + getStep().toString() + "\"");
        }
    }
    
    private Steps getStep()
    {
        return Steps.values()[stepIndex];
    }
    
    /**
     * Progress to the next step
     */
    private void nextStep()
    {
        //move to the next step
        this.stepIndex++;
        
        //increase progress that is displayed to user
        progress.increase();
    }
    
    /**
     * Get the time passed
     * @return The time passed in format mm:ss
     */
    public String getTimePassed()
    {
        return this.timer.getDescPassed(Timers.FORMAT_8);
    }
    
    /**
     * Here we will manage the level information
     * @param time Time duration per update to deduct from timer (nano-seconds)
     * @param playerX Current player's location 
     * @param playerZ Current player's location
     * @param resources Object containing sound effects etc..
     */
    public void update(final long time, final double playerX, final double playerZ, final Resources resources)
    {
        //check if a door is closing
        boolean closing = hasClosingDoor();

        //update blocks
        super.update(time, playerX, playerZ);

        //if a door was not closing and now is, then play sound effect
        if (!closing && hasClosingDoor())
            resources.playGameAudio(GameAudio.Keys.DoorClose);
        
        //update timer
        timer.update(time);
    }
    
    public void renderProgress(final Graphics graphics, final Rectangle window)
    {
        try
        {
            if (!maze.isComplete())
            {
                maze.renderProgress(graphics, window);
            }
            else
            {
                progress.render(graphics, window);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}