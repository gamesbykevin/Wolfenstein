package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.ai.AStar;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.wolfenstein.display.Textures;

import com.gamesbykevin.wolfenstein.level.objects.*;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem.Type.*;
import com.gamesbykevin.wolfenstein.shared.Shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class will contain static methods to help setup the room
 * @author GOD
 */
public class RoomHelper 
{
    //the number of bonus items to add to each secret room
    private static final int BONUS_ITEMS_PER_ROOM = 5;
    
    //how many solid non-display items can we place in a single room
    private static final int SOLID_NON_DISPLAY_OBSTACLE_LIMIT = 5;
    
    
    /**
     * Different patterns for adding obstacles
     */
    private enum ObstaclePattern
    {
        //four solid obstacles in each corner of the room
        FourCornersSquare, 
        
        //2 obstacles placed in the middle
        NorthSouthSides,
        
        //2 obstacles placed in the middle
        EastWestSides,
    }
    
    public static void placeObstacles(final LevelObjects objects, final Room room, final Random random, List<Obstacle.Type> solidDisplay, List<Obstacle.Type> nonsolid, List<Obstacle.Type> solidNonDisplay) throws Exception
    {
        //pick a random pattern how obstacles are added
        ObstaclePattern pattern = ObstaclePattern.values()[random.nextInt(ObstaclePattern.values().length)];
        
        Obstacle.Type type = null;
        
        switch(pattern)
        {
            case FourCornersSquare:
                //get random type
                type = solidDisplay.get(random.nextInt(solidDisplay.size()));
                
                //add obstacles
                objects.addObstacle(type, room.getAdjustedCol(room.getFirstColumn() + 2), room.getAdjustedRow(room.getFirstRow() + 2));
                objects.addObstacle(type, room.getAdjustedCol(room.getFirstColumn() + 2), room.getAdjustedRow(room.getLastRow()  - 1));
                objects.addObstacle(type, room.getAdjustedCol(room.getLastColumn() - 1),  room.getAdjustedRow(room.getFirstRow() + 2));
                objects.addObstacle(type, room.getAdjustedCol(room.getLastColumn() - 1),  room.getAdjustedRow(room.getLastRow()  - 1));
                break;
                
            case NorthSouthSides:
                //get random type
                type = nonsolid.get(random.nextInt(nonsolid.size()));
                
                //add obstacles
                objects.addObstacle(type, room.getAdjustedCol((room.getColumnCount() / 2) + 1), room.getAdjustedRow(room.getFirstRow() + 2));
                objects.addObstacle(type, room.getAdjustedCol((room.getColumnCount() / 2) + 1), room.getAdjustedRow(room.getLastRow() - 1));
                break;
                
            case EastWestSides:
                //get random type
                type = nonsolid.get(random.nextInt(nonsolid.size()));
                
                //add obstacles
                objects.addObstacle(type, room.getAdjustedCol(room.getFirstColumn() + 2), room.getAdjustedRow((room.getRowCount() / 2) + 1));
                objects.addObstacle(type, room.getAdjustedCol(room.getLastColumn() - 1),  room.getAdjustedRow((room.getRowCount() / 2) + 1));
                break;
        }
        
        int count = 0;
        
        //place non-display solid items as well
        while (count < SOLID_NON_DISPLAY_OBSTACLE_LIMIT)
        {
            //get random type
            type = solidNonDisplay.get(random.nextInt(solidNonDisplay.size()));
            
            //add obstacle, if the random location already exists nothing will be added
            objects.addObstacle(type, room.getRandomLocation(random));
            
            //increase count
            count++;
        }
    }
    
    public static void addBonusItems(final LevelObjects objects, final Room room, final Random random, final List<BonusItem.Type> types) throws Exception
    {
        //if this room is a secret every empty block will contain a bonus item
        if (room.isSecret())
        {
            //check each block
            for (int row = room.getFirstRow() + 2; row <= room.getLastRow() - 1; row++)
            {
                //get random bonus item
                BonusItem.Type type = types.get(random.nextInt(types.size()));
                
                //add the same bonus item for this entire row
                for (int column = room.getFirstColumn() + 2; column <= room.getLastColumn() - 1; column++)
                {
                    //make sure we aren't adding this to a place we can't get to
                    if (room.get(column, row) == null || !room.get(column, row).isSolid())
                    {
                        //add bonus item to proper location
                        objects.addBonusItem(type, room.getAdjustedCol(column), room.getAdjustedRow(row));
                    }
                }
                
                //make sure duplicates aren't added again
                checkDuplicateBonuses(types, type);
            }
        }
        else
        {
            //count the number of items added for non-bonus items
            int count = 0;
            
            //keep adding bonus items until we reach our limit
            while (count < SOLID_NON_DISPLAY_OBSTACLE_LIMIT)
            {
                //get random bonus item
                BonusItem.Type type = types.get(random.nextInt(types.size()));
                
                //add bonus item to random location, if an item already exists at location nothing will add
                objects.addBonusItem(type, room.getRandomLocation(random));
                
                //make sure duplicates aren't added again
                checkDuplicateBonuses(types, type);

                count++;
            }
        }
    }
    
    private static void checkDuplicateBonuses(final List<BonusItem.Type> types, final BonusItem.Type type)
    {
        //prevent these bonus items from being added multiple times
        switch (type)
        {
            //if any of these remove from possible list
            case AssaultGun:
            case MachineGun:
            case ExtraLife:
                types.remove(type);
                break;
        }
    }
    
    /**
     * Check if this room can be a secret and if so randomly decide if this is should be a secret room
     * @param level The level
     * @param room The room we are checking
     * @param maze The overall Labyrinth for the Level
     * @param random Object used to make random decisions
     * @throws Exception 
     */
    public static void checkCreateSecret(final Level level, final Room room, final Labyrinth maze, final Random random) throws Exception
    {
        //the secret room can't be the start or finish of the maze
        if (room.getLocation().equals(maze.getStart()) || room.getLocation().equals(maze.getFinish()))
            return;

        //also make sure secret room isn't next to the finish to prevent any issues
        if (maze.getFinish().equals(room.getLocation().getCol() - 1, room.getLocation().getRow()))
            return;
        if (maze.getFinish().equals(room.getLocation().getCol() + 1, room.getLocation().getRow()))
            return;
        if (maze.getFinish().equals(room.getLocation().getCol(), room.getLocation().getRow() - 1))
            return;
        if (maze.getFinish().equals(room.getLocation().getCol(), room.getLocation().getRow() + 1))
            return;

        //get the current location
        Location location = maze.getLocation(room.getLocation());

        //it has to have 3 walls to be a secret room
        if (location.getWalls().size() != 3)
            return;

        //choose at random if this is to be a secret room
        if (!random.nextBoolean())
            return;

        //mark room as secret
        room.setSecret(true);

        //check each side to determine how the secret will be created
        for (Location.Wall wall : Location.Wall.values())
        {
            //we only want the side where there isn't a wall
            if (location.hasWall(wall))
                continue;

            if (Shared.DEBUG)
                System.out.println("Secret Added wall = " + wall.toString() + ", room = (" + room.getLocation().getCol() + "," + room.getLocation().getRow() + ")");

            //change the appropriate border
            switch(wall)
            {
                case North:
                    //change the border
                    level.getRoom(room.getCol(), room.getRow() - 1).changeBorder(Location.Wall.South, Room.State.Door);
                    break;

                case South:
                    //change the border
                    room.changeBorder(wall, Room.State.Door);
                    break;

                case East:
                    //change the border
                    room.changeBorder(wall, Room.State.Door);
                    break;

                case West:
                    //change the border
                    level.getRoom(room.getCol() - 1, room.getRow()).changeBorder(Location.Wall.East, Room.State.Door);
                    break;
            }
        }
    }
    
    /**
     * At random choose a door(s) to lock that will require a key to open.<br>
     * We will also randomly place a key required to open that door.
     * @param random Object used to make random decisions
     * @param maze Overall maze
     * @param level The level
     * @param objects The level objects which we will use to place key
     * @throws Exception 
     */
    public static void lockRoom(final Random random, final Labyrinth maze, final Level level, final LevelObjects objects) throws Exception
    {
        //optional rooms where the door can be locked
        List<Cell> options = new ArrayList<>();
        
        //object used to calculate shortest path to goal
        AStar astar = new AStar();
        
        //set the start and finish
        astar.setStart(maze.getStart());
        astar.setGoal(maze.getFinish());
        
        //set our map
        astar.setMap(maze.getLocations());
        
        //calculate shortest path to goal
        astar.calculate(random);
        
        //get the shortest path
        List<Cell> path = astar.getPath();
        
        for (int row=0; row < maze.getRows(); row++)
        {
            for (int col=0; col < maze.getCols(); col++)
            {
                //the finish room won't contain any locked doors and the start room won't either
                if (maze.getFinish().equals(col, row) || maze.getStart().equals(col, row))
                    continue;
                
                //also make sure it isn't next to the goal either
                if (maze.getFinish().equals(col + 1, row) || maze.getFinish().equals(col - 1, row))
                    continue;
                if (maze.getFinish().equals(col, row + 1) || maze.getFinish().equals(col, row - 1))
                    continue;
                
                //also make sure it isn't next to the start either
                if (maze.getStart().equals(col + 1, row) || maze.getStart().equals(col - 1, row))
                    continue;
                if (maze.getStart().equals(col, row + 1) || maze.getStart().equals(col, row - 1))
                    continue;
                
                //is this room on the path towards reaching the goal
                boolean valid = false;
                
                //check our goal path and make sure this room is part of that path
                for (Cell cell : path)
                {
                    if (cell.equals(col, row))
                    {
                        valid = true;
                        break;
                    }
                }
                
                if (!valid)
                    continue;
                
                //get current room
                Room room = level.getRoom(col, row);
                
                //don't lock secret door
                if (room.isSecret())
                    continue;
                
                //get an unlocked door in the current room
                Block block = room.getUnlockedBlock();
                
                //if the room contains an unlocked door we have a valid option to choose from
                if (block != null)
                {
                    //at this point we have a valid door that can be locked so add the room to list
                    options.add(new Cell(col, row));
                }
            }
        }
        
        //if there are options lets lock a door
        if (!options.isEmpty())
        {
            //pick random room to lock
            Cell location = new Cell(options.get(random.nextInt(options.size())));
            
            //now locate a location to place the key that isn't blocked by the locked door
            options.clear();
            
            astar.setStart(maze.getStart());
            
            //check each room in our maze
            for (int row=0; row < maze.getRows(); row++)
            {
                for (int col=0; col < maze.getCols(); col++)
                {
                    //don't choose the same location as the room with locked door
                    if (location.equals(col, row))
                        continue;
                    
                    //don't pick any room next to room with locked door either
                    if (location.equals(col - 1, row) || location.equals(col + 1, row))
                        continue;
                    if (location.equals(col, row - 1) || location.equals(col, row + 1))
                        continue;
                    
                    //set the current location as the goal
                    astar.setGoal(col, row);

                    //we already have the map so calculate the path to the finish
                    astar.calculate(random);

                    //get the shortest path
                    path = astar.getPath();
                    
                    boolean valid = true;
                    
                    //check if the locked door is part of the path
                    for (Cell cell : path)
                    {
                        //if our locked room is part of this path then this isn't a valid location
                        if (cell.equals(location))
                        {
                            valid = false;
                            break;
                        }
                    }
                    
                    if (valid)
                        options.add(new Cell(col, row));
                }
            }
            
            //there is a valid room to place a key
            if (!options.isEmpty())
            {
                //get the room where we want to lock the door
                Room room = level.getRoom(location);
                
                if (Shared.DEBUG)
                    System.out.println("Locked Door in room (" + room.getLocation().getCol() + "," + room.getLocation().getRow() + ")");
                
                //lock the unlocked door
                room.getUnlockedBlock().getDoor().setLocked(true);
                
                //now get random room for where the key is to be placed
                room = level.getRoom(options.get(random.nextInt(options.size())));
                
                if (Shared.DEBUG)
                    System.out.println("Key placed in room (" + room.getLocation().getCol() + "," + room.getLocation().getRow() + ")");
                
                //place key somewhere inside a valid room, pick random key texture since it doesn't matter
                objects.addBonusItem((random.nextBoolean()) ? BonusItem.Type.Key1 : BonusItem.Type.Key2, room.getRandomLocation(random));
            }
        }
        
        //recycle unused objects
        astar.dispose();
        astar = null;
        options.clear();
        options = null;
    }
}