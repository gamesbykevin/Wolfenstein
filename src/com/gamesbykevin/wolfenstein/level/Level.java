package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.display.Textures.Key;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem;
import com.gamesbykevin.wolfenstein.level.objects.LevelObjects;
import com.gamesbykevin.wolfenstein.level.objects.Obstacle;
import com.gamesbykevin.wolfenstein.resources.Resources;
import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.shared.Shared;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Level implements Disposable
{
    //the overall maze of the level, each cell will represent a room
    private Labyrinth maze;
    
    //here we will manage the bonus items and obstacles in the level
    private LevelObjects objects;
    
    //all of the rooms in the maze
    private List<Room> rooms;
    
    //the total number of treasures in this level
    private int treasureCount = 0;
    
    //has the switch been hit
    private boolean complete = false;
    
    /**
     * Create a new level
     * @param columns The total number of columns in our overall maze
     * @param rows The total number of rows in our overall maze
     * @param roomColumns The total number of columns for each room
     * @param roomRows The total number of rows for each room
     * @throws Exception 
     */
    public Level(final int columns, final int rows, final int roomColumnTotal, final int roomRowTotal, final Random random, final Image obstacleSpriteSheet, final Image bonusItemSpriteSheet) throws Exception
    {
        //make sure the room dimensions are odd so the door can properly be placed in the middle
        if (roomColumnTotal % 2 == 0 || roomRowTotal % 2 == 0)
            throw new Exception("Please verify the room column/row total is an odd number.");
        
        //create an empty list that will contain the rooms of the maze
        this.rooms = new ArrayList<>();
        
        //create the object that will have bonus items/obstacles etc..
        this.objects = new LevelObjects(obstacleSpriteSheet, bonusItemSpriteSheet);
        
        //pick a random maze generation algorithm
        //Labyrinth.Algorithm algorithm = Labyrinth.Algorithm.values()[random.nextInt(Labyrinth.Algorithm.values().length)];
        Labyrinth.Algorithm algorithm = Labyrinth.Algorithm.DepthFirstSearch;
        
        //generate our overall maze, each cell in this maze will represent a room
        this.maze = new Labyrinth(columns, rows, algorithm);
        this.maze.setStart(0, 0);
        this.maze.generate();
        
        //create rooms
        createRooms(roomColumnTotal, roomRowTotal);
        
        //locate the goal for our maze
        locateGoal();
        
        //add secret rooms to our maze
        createSecrets(random);
        
        //create all rooms with walls/doors etc..
        createWalls(random);
        
        //lock random door needed to pass to solve the level
        RoomHelper.lockRoom(random, maze, this, objects);
        
        //add items to level bonus/obstacles
        addItems(random);
        
        //make the goal room a room by itself
        createGoalRoom();
        
        //anything remaining that is null will be an empty block
        fill();
        
        //now set the wall textures for the rooms
        assignTextures();
    }
    
    @Override
    public void dispose()
    {
        maze.dispose();
        maze = null;
        
        for (Room room : rooms)
        {
            room.dispose();
            room = null;
        }
        
        rooms.clear();
        rooms = null;
        
        objects.dispose();
        objects = null;
    }
    
    private void createRooms(final int roomColumnTotal, final int roomRowTotal)
    {
        for (int row=0; row < maze.getRows(); row++)
        {
            for (int col=0; col < maze.getCols(); col++)
            {
                //add room to list with the location of the room and dimension of the room
                this.rooms.add(new Room(col, row, roomColumnTotal, roomRowTotal));
            }
        }
    }
    
    protected Room getRoom(final Cell location)
    {
        return getRoom((int)location.getCol(), (int)location.getRow());
    }
    
    protected Room getRoom(final int column, final int row)
    {
        for (Room room : rooms)
        {
            if (room.isLocation(column, row))
                return room;
        }
        
        return null;
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
        
        //mark the room as the goal
        getRoom(maze.getFinish()).setGoal(true);
    }
    
    /**
     * Create the walls/doors and open spaces for the room
     * @param random Object used for Random decisions
     * @throws Exception 
     */
    private void createWalls(final Random random) throws Exception
    {
        for (Room room : rooms)
        {
            //get the current maze location
            Location location = maze.getLocation(room.getLocation());
            
            //check each side
            for (Wall wall : Wall.values())
            {
                //if there is a wall here
                if (location.hasWall(wall))
                {
                    room.changeBorder(wall, Room.State.Closed);
                }
                else
                {
                    room.changeBorder(wall, (random.nextBoolean()) ? Room.State.Open : Room.State.Door);
                }
            }
        }
    }
    
    /**
     * Count the number of secret rooms in our level
     * @return The current total count of secrets rooms
     */
    private int getSecretCount()
    {
        int count = 0;
        
        for (Room room : rooms)
        {
            if (room.isSecret())
                count++;
        }
        
        return count;
    }
    
    /**
     * Create secret rooms in our level.<br>
     * These rooms will not be required to solve the maze and simply are bonus rooms.
     * @param random Object used to make random decisions
     * @throws Exception 
     */
    private void createSecrets(final Random random) throws Exception
    {
        //create a cap on the number of secret rooms
        final int secretLimit = (int)(maze.getCols() / 2);
        
        for (Room room : rooms)
        {
            //if we reached our limit no need to continue
            if (getSecretCount() >= secretLimit)
                return;

            //check if this room can be a secret room
            RoomHelper.checkCreateSecret(this, room, maze, random);
        }
    }
    
    /**
     * Add the items to the level at random
     * @param random Object used to make random decisions
     */
    private void addItems(final Random random) throws Exception
    {
        //create list of valid bonus items
        List<BonusItem.Type> types = new ArrayList<>();
        types.add(BonusItem.Type.AmmoClip);
        types.add(BonusItem.Type.AssaultGun);
        types.add(BonusItem.Type.MachineGun);
        types.add(BonusItem.Type.SmallFood);
        types.add(BonusItem.Type.HealthKit);
        types.add(BonusItem.Type.ExtraLife);
        types.add(BonusItem.Type.Treasure1);
        types.add(BonusItem.Type.Treasure2);
        types.add(BonusItem.Type.Treasure3);
        types.add(BonusItem.Type.Treasure4);
        
        //create a new list of solid obstacles for display
        List<Obstacle.Type> solidDisplay = new ArrayList<>();
        
        //create a new list of non-solid obstacles
        List<Obstacle.Type> nonsolid = new ArrayList<>();
        
        //create a new list of solid obstacles that are non-display
        List<Obstacle.Type> solidNonDisplay = new ArrayList<>();
        
        //populate our list of solid obstacles
        for (Obstacle.Type type : Obstacle.Type.values())
        {
            if (type.isSolid())
            {
                if (!type.isDisplay())
                {
                    solidNonDisplay.add(type);
                }
                else
                {
                    solidDisplay.add(type);
                }
            }
            else
            {
                nonsolid.add(type);
            }
        }
        
        for (Room room : rooms)
        {
            //don't add items to start or finish
            if (room.getLocation().equals(maze.getStart()))
                continue;
            if (room.getLocation().equals(maze.getFinish()))
                continue;
            
            //choose randomly if bonus items are to be added to this room
            boolean addBonus = random.nextBoolean();
            
            //if a secret room we will definitely add bonuses
            if (room.isSecret())
                addBonus = true;

            //do we add bonuses in this room
            if (addBonus)
            {
                RoomHelper.addBonusItems(objects, room, random, types);
            }

            //place the obstacles
            RoomHelper.placeObstacles(objects, room, random, solidDisplay, nonsolid, solidNonDisplay);
        }
        
        //now set the total number of treasures so we can track completion
        this.treasureCount = objects.getTreasureCount();
        
        if (Shared.DEBUG)
            System.out.println("Total treasure count :" + treasureCount);
    }
    
    /**
     * We want our goal location to be a room by itself
     * @throws Exception 
     */
    private void createGoalRoom() throws Exception
    {
        for (Room room : rooms)
        {
            //we only want the goal room
            if (!room.isGoal())
                continue;
            
            //get the location from the maze
            Location tmp = maze.getLocation(room.getLocation());
            
            for (Wall wall : Wall.values())
            {
                //if there isn't a wall make a door
                if (!tmp.hasWall(wall))
                {
                    room.changeBorder(wall, Room.State.Door);
                }
            }
        }
    }
    
    /**
     * For all rooms create an empty block for the remaining null blocks
     */
    private void fill()
    {
        for (Room room : rooms)
        {
            room.fill();
        }
    }
    
    /**
     * Assign textures to the solid blocks
     * @throws Exception 
     */
    private void assignTextures() throws Exception
    {
        //do all rooms except secret and goal first
        for (Room room : rooms)
        {
            if (room.isSecret() || room.isGoal())
                continue;
            
            //set the textures for the walls in the room
            for (Wall wall : Wall.values())
            {
                room.assignTextures(wall, Key.Door1, Key.Cement1);
            }
        }
        
        //then do secret room and goal last
        for (Room room : rooms)
        {
            if (!room.isSecret() && !room.isGoal())
                continue;
            
            //set the textures for the walls in the room
            for (Wall wall : Wall.values())
            {
                room.assignTextures(wall, Key.Door1, Key.Cement1);
            }
        }
    }
    
    /**
     * Get the block at the specified location
     * @param x x-location
     * @param z z-location
     * @return Block at location, null is returned if the block is not found
     */
    public Block getBlock(final double x, final double z)
    {
        //first determine what room this is in
        Room room = getRoom((int)(x / getRoom(0,0).getColumnCount()), (int)(z / getRoom(0,0).getRowCount()));
        
        if (room != null)
        {
            return room.getAdjustedBlock(x, z);
        }
        else
        {
            //this location is not in a room so return the default solid block
            return Block.solidBlock;
        }
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
    
    /**
     * Get the total number of block columns in the entire maze
     * @return Total number of columns from all rooms combined
     */
    public int getTotalColumns()
    {
        return (getRoom(0,0).getColumnCount() * maze.getCols());
    }
    
    /**
     * Get the total number of block rows in the entire maze
     * @return Total number of rows from all rooms combined
     */
    public int getTotalRows()
    {
        return (getRoom(0,0).getRowCount() * maze.getRows());
    }
    
    /**
     * Is the player's location inside the goal room
     * @param playerX Current player's location 
     * @param playerZ Current player's location 
     * @return true if the provided location is inside the goal room, false otherwise
     */
    public boolean insideGoal(final double playerX, final double playerZ)
    {
        for (Room room : rooms)
        {
            //if this isn't the goal or we haven't visited the room yet, skip
            if (!room.hasVisited() || !room.isGoal())
                continue;
            
            //is the player location inside this room
            if (room.hasAdjustedLocation(playerX, playerZ))
                return true;
        }
        
        return false;
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
        //update the rooms
        for (Room room : rooms)
        {
            //update the block(s) inside the room
            room.update(time, room.getRoomColumn(playerX), room.getRoomRow(playerZ));
            
            //have we visited this room yet
            if (!room.hasVisited())
            {
                //if we are inside this room mark it as visited
                if (room.hasAdjustedLocation(playerX, playerZ))
                {
                    room.setVisited(true);
                }
            }
            
            //if a door just started to close, play sound effect
            if (room.hasClosingDoor())
                resources.playGameAudio(GameAudio.Keys.DoorClose);
        }
    }
}