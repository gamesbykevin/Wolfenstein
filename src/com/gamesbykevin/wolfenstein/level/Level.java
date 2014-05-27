package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.resources.Progress;

import com.gamesbykevin.wolfenstein.display.Textures.*;
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
    
    //the total number of treasures in this level
    private int treasureCount = 0;
    
    //has the switch been hit
    private boolean complete = false;
    
    //keep track of the secret rooms
    private List<Cell> secrets;
    
    //the size of each room
    private final int roomDimensions;
    
    //minimum size for the room and maze
    private static final int MINIMUM_ROOM_DIMENSION = 8;
    private static final int MINIMUM_MAZE_DIMENSION = 3;
    
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
        Step1, Step2, Step3, Step4, Step5, Step6, Step7, Step8, Step9, Step10
    }
    
    //current step
    private Steps step = Steps.Step1;
    
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
        
        //our list of secret rooms
        this.secrets = new ArrayList<>();
        
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
        
        maze.dispose();
        maze = null;
        
        objects.dispose();
        objects = null;
        
        levelKeys.dispose();
        levelKeys = null;
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
        this.treasureCount = objects.getTreasureCount();
        
        if (Shared.DEBUG)
            System.out.println("Total treasure count :" + treasureCount);
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
    
    public boolean isLevelCreated()
    {
        return (this.step == Steps.Step10);
    }
    
    private int getPlayerRow(final double playerZ)
    {
        return (int)(playerZ / ((getRowCount() + 2 - maze.getRows()) / maze.getRows()));
    }
    
    /**
     * Here we will setup the level
     * @param random Object used to make random decisions
     */
    public void update(final Random random) throws Exception
    {
        switch (step)
        {
            case Step1:
                
                //generate the maze until finished
                if(!maze.isComplete())
                    maze.update();
                
                if (maze.isComplete())
                {
                    //move to next step
                    step = Steps.Step2;
                    progress.increase();
                }
                break;
                
            case Step2:
                //locate the goal for our maze
                locateGoal();
                
                //move to next step
                step = Steps.Step3;
                progress.increase();
                break;
                
            case Step3:
                //determine what sides are open/closed/door etc..
                createRooms(random);
                
                //move to next step
                step = Steps.Step4;
                progress.increase();
                break;
                
            case Step4:
                //createSecrets
                createSecrets(random);
                
                //move to next step
                step = Steps.Step5;
                progress.increase();
                break;
                
            case Step5:
                //lock a random door that the player has to access to solve the level
                createLockedDoor(random);
                
                //move to next step
                step = Steps.Step6;
                progress.increase();
                break;
                
            case Step6:
                //populate obstacles/bonuses to level
                populateRooms(random);
                
                //move to next step
                step = Steps.Step7;
                progress.increase();
                break;
                
            case Step7:
                //make the location where the goal is a room
                createGoalRoom();
                
                //move to next step
                step = Steps.Step8;
                progress.increase();
                break;
                
            case Step8:
                //finally fill any null block with an empty block
                fill();
                
                //move to next step
                step = Steps.Step9;
                progress.increase();
                break;
                
            case Step9:
                //now set the wall textures for the rooms
                assignTextures(random);
                
                //level has been setup now
                step = Steps.Step10;
                progress.increase();
                break;
        }
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