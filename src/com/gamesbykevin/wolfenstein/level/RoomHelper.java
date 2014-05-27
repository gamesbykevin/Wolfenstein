package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.ai.AStar;
import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Labyrinth;
import com.gamesbykevin.framework.labyrinth.Location;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.wolfenstein.display.Textures;

import com.gamesbykevin.wolfenstein.level.objects.*;
import com.gamesbykevin.wolfenstein.level.objects.BonusItem.Type.*;
import static com.gamesbykevin.wolfenstein.level.objects.BonusItem.Type.Key1;
import static com.gamesbykevin.wolfenstein.level.objects.BonusItem.Type.Key2;
import static com.gamesbykevin.wolfenstein.level.Level.State;

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
    private static final int SOLID_NON_DISPLAY_OBSTACLE_LIMIT = 3;
    
    //this is used to determine
    private static final int RANDOM_DOOR_PROBABILITY = 3;
    
    /**
     * Different patterns for placing obstacles
     */
    protected enum ObstaclePattern
    {
        //four solid obstacles in each corner of the room
        FourCornersSquare, 
        
        //2 obstacles placed in the middle on north and south side
        NorthSouthSides,
        
        //2 obstacles placed in the middle on west and east side
        EastWestSides,
    }
    
    private static int getStartRow(final Level level, final Cell location)
    {
        return (int)(location.getRow() * level.getRoomDimensions());
    }
    
    private static int getStartCol(final Level level, final Cell location)
    {
        return (int)(location.getCol() * level.getRoomDimensions());
    }
    
    /**
     * Change the border to open/door/closed etc...
     * @param level The level object so we can manipulate the blocks
     * @param wall The side we want to change
     * @param state What we want to change the side to
     * @param location The location in the maze
     * @param secret Does this border contain a secret
     * @param goal Does this border contain the goal
     * @param lock Does this border need to be locked
     */
    protected static void changeBorder(final Level level, final Location.Wall wall, final Level.State state, final Cell location, final boolean secret, final boolean goal, final boolean lock) throws Exception
    {
        //find start position
        final int startCol = getStartCol(level, location);
        final int startRow = getStartRow(level, location);
        
        switch(wall)
        {
            case North:
            case South:
                
                for (int col = 0; col < level.getRoomDimensions() + 2; col++)
                {
                    Block block;
                    
                    switch(state)
                    {
                        case Open:
                            if (col == 0 || col == level.getRoomDimensions() + 1)
                            {
                                block = Block.SOLID_BLOCK;
                            }
                            else
                            {
                                block = new Block();
                            }
                            break;
                            
                        case Closed:
                        case Door:
                            block = new SolidBlock(wall);
                            break;
                            
                        default:
                            throw new Exception("State is not setup here");
                    }
                    
                    //determine if this is in the middle
                    if (col == (int)((level.getRoomDimensions() + 1) / 2))
                    {
                        if (state == Level.State.Door)
                        {
                            if (!block.isDoor())
                                block.createDoor();
                            
                            //set the door locked
                            block.getDoor().setLocked(lock);
                        }
                        
                        //set the goal parameter
                        block.setGoal(goal);
                        
                        //set the secret paramter
                        block.setSecret(secret);
                    }
                    
                    if (wall == Location.Wall.North)
                    {
                        level.set(col + startCol, startRow, block);
                    }
                    else
                    {
                        level.set(col + startCol, startRow + level.getRoomDimensions(), block);
                    }
                }
                break;
                
            case East:
            case West:
                
                for (int row = 0; row < level.getRoomDimensions() + 2; row++)
                {
                    Block block;
                    
                    switch(state)
                    {
                        case Open:
                            if (row == 0 || row == level.getRoomDimensions() + 1)
                            {
                                block = Block.SOLID_BLOCK;
                            }
                            else
                            {
                                block = new Block();
                            }
                            break;
                            
                        case Closed:
                        case Door:
                            block = new SolidBlock(wall);
                            break;
                            
                        default:
                            throw new Exception("State is not setup here");
                    }
                    
                    //determine if this is in the middle
                    if (row == (int)((level.getRoomDimensions() + 1) / 2))
                    {
                        if (state == Level.State.Door)
                        {
                            if (!block.isDoor())
                                block.createDoor();
                            
                            //set the door locked
                            block.getDoor().setLocked(lock);
                        }
                        
                        //set the goal parameter
                        block.setGoal(goal);
                        
                        //set the secret paramter
                        block.setSecret(secret);
                    }
                    
                    if (wall == Location.Wall.West)
                        level.set(startCol, startRow + row, block);
                    else
                        level.set(startCol + level.getRoomDimensions(), startRow + row, block);
                }
                break;
        }
    }
    
    protected static void placeObstacles(final Level level, final LevelObjects objects, final Cell location, final Random random) throws Exception
    {
        //create a new list of solid obstacles for display
        List<Obstacle.Type> solidDisplay = new ArrayList<>();
        
        //create a new list of non-solid obstacles
        List<Obstacle.Type> nonsolid = new ArrayList<>();
        
        //create a new list of solid obstacles that are non-display
        List<Obstacle.Type> solidNonDisplay = new ArrayList<>();
        
        //populate our list(s)
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
        
        //find start position
        final int startCol = getStartCol(level, location);
        final int startRow = getStartRow(level, location);
        
        //pick a random pattern how obstacles are added
        ObstaclePattern pattern = ObstaclePattern.values()[random.nextInt(ObstaclePattern.values().length)];
        
        Obstacle.Type type = null;
        
        final double beginCol = startCol + 2;
        final double middleCol = startCol + ((level.getRoomDimensions() + 1) / 2);
        final double finishCol = startCol + level.getRoomDimensions() - 2;
        
        final double beginRow = startRow + 2;
        final double middleRow = startRow + ((level.getRoomDimensions() + 2) / 2);
        final double finishRow = startRow + level.getRoomDimensions() - 2;
        
        switch(pattern)
        {
            case FourCornersSquare:
                //get random type
                type = solidDisplay.get(random.nextInt(solidDisplay.size()));
                
                //add obstacles
                objects.addObstacle(type, beginCol, beginRow);
                objects.addObstacle(type, beginCol, finishRow);
                objects.addObstacle(type, finishCol, beginRow);
                objects.addObstacle(type, finishCol, finishRow);
                break;
                
            case NorthSouthSides:
                //get random type
                type = nonsolid.get(random.nextInt(nonsolid.size()));
                
                //add obstacles
                objects.addObstacle(type, middleCol, beginRow);
                objects.addObstacle(type, middleCol, finishRow);
                break;
                
            case EastWestSides:
                //get random type
                type = nonsolid.get(random.nextInt(nonsolid.size()));
                
                //add obstacles
                objects.addObstacle(type, beginCol,  middleRow);
                objects.addObstacle(type, finishCol, middleRow);
                break;
        }
        
        List<Cell> options = getOptions(startCol, startRow, level.getRoomDimensions());
        
        //keep track of objects added
        int count = 0;
        
        //place non-display solid items as well
        while (count < SOLID_NON_DISPLAY_OBSTACLE_LIMIT)
        {
            //get random type
            type = solidNonDisplay.get(random.nextInt(solidNonDisplay.size()));
            
            final int index = random.nextInt(options.size());
            
            //add obstacle, if the random location already exists nothing will be added
            objects.addObstacle(type, options.get(index));
            
            //increase count
            count++;
            
            //remove location from list
            options.remove(index);

            //if no more locations exit loop
            if (options.isEmpty())
                break;
        }
    }
    
    protected static void addBonusItems(final Level level, final Cell location, final LevelObjects objects, final Random random, final List<BonusItem.Type> types, final boolean secret) throws Exception
    {
        //find start position
        final int startCol = getStartCol(level, location);
        final int startRow = getStartRow(level, location);
        
        //if this room is a secret every empty block will contain a bonus item
        if (secret)
        {
            //check each block
            for (int row = 2; row <= level.getRoomDimensions() - 1; row++)
            {
                //get random bonus item
                BonusItem.Type type = types.get(random.nextInt(types.size()));

                for (int col = 2; col <= level.getRoomDimensions() - 1; col++)
                {
                    //add bonus item to proper location
                    objects.addBonusItem(type, startCol + col, startRow + row);
                }
                
                //make sure duplicates aren't added again
                checkDuplicateBonuses(types, type);
            }
        }
        else
        {
            List<Cell> options = getOptions(startCol, startRow, level.getRoomDimensions());

            //count the number of items added for non-bonus items
            int count = 0;
            
            //keep adding bonus items until we reach our limit
            while (count < BONUS_ITEMS_PER_ROOM)
            {
                //get random bonus item
                BonusItem.Type type = types.get(random.nextInt(types.size()));
                
                final int index = random.nextInt(options.size());
                
                //add bonus item to random location, if an item already exists at location nothing will add
                objects.addBonusItem(type, options.get(index));
                
                //make sure duplicates aren't added again
                checkDuplicateBonuses(types, type);

                //add to count
                count++;
                
                //remove location from list
                options.remove(index);

                //if no more locations exit loop
                if (options.isEmpty())
                    break;
            }
        }
    }
    
    protected static List<Cell> getOptions(final Level level, final Cell location)
    {
        return getOptions(getStartCol(level, location), getStartRow(level, location), level.getRoomDimensions());
    }
    
    protected static List<Cell> getOptions(final int startCol, final int startRow, final int roomDimensions)
    {
        List<Cell> options = new ArrayList<>();

        //create list of possible locations
        for (int row = 3; row <= roomDimensions - 2; row++)
        {
            for (int col = 3; col <= roomDimensions - 2; col++)
            {
                options.add(new Cell(startCol + col, startRow + row));
            }
        }

        return options;
    }
    
    protected static void checkDuplicateBonuses(final List<BonusItem.Type> types, final BonusItem.Type type)
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
    
    protected static void assignTexture(final Level level, final Block block, final int column, final int row, final Textures.Key wallKey, final Random random)
    {
        //only solid blocks will have textures
        if (!block.isSolid())
            return;
        
        switch (block.getWall())
        {
            case West:
            case East:
                
                if (block.isDoor() && !block.getDoor().isSecret())
                {
                    block.setNorth(Textures.Key.DoorSide);
                    block.setSouth(Textures.Key.DoorSide);

                    if (block.isGoal())
                    {
                        Textures.Key key = (random.nextBoolean()) ? Textures.Key.DoorGoal1 : Textures.Key.DoorGoal2;
                        
                        block.setWest(key);
                        block.setEast(key);
                    }
                    else
                    {
                        if (block.getDoor().isLocked())
                        {
                            block.setWest(Textures.Key.DoorLocked);
                            block.setEast(Textures.Key.DoorLocked);
                        }
                        else
                        {
                            Textures.Key key = (random.nextBoolean()) ? Textures.Key.Door1 : Textures.Key.Door2Message;
                            
                            block.setWest(key);
                            block.setEast(key);
                        }
                    }
                }
                else
                {
                    if (level.get(column, row + 1).isDoor() || level.get(column, row - 1).isDoor())
                    {
                        block.setNorth(Textures.Key.DoorSide);
                        block.setSouth(Textures.Key.DoorSide);
                    }
                    else
                    {
                        block.setNorth(wallKey);
                        block.setSouth(wallKey);
                    }

                    //if this is the wall where the switch will be
                    if (block.isGoal())
                    {
                        if (block.getWall() == Location.Wall.West)
                        {
                            block.setWest(wallKey);
                            block.setEast(Textures.Key.GoalSwitchOff);
                        }
                        else
                        {
                            block.setWest(Textures.Key.GoalSwitchOff);
                            block.setEast(wallKey);
                        }
                    }
                    else
                    {
                        block.setWest(wallKey);
                        block.setEast(wallKey);
                    }
                }
                break;
                
            case North:
            case South:
                
                if (block.isDoor() && !block.getDoor().isSecret())
                {
                    block.setWest(Textures.Key.DoorSide);
                    block.setEast(Textures.Key.DoorSide);

                    if (block.isGoal())
                    {
                        Textures.Key key = (random.nextBoolean()) ? Textures.Key.DoorGoal1 : Textures.Key.DoorGoal2;
                        
                        block.setNorth(key);
                        block.setSouth(key);
                    }
                    else
                    {
                        if (block.getDoor().isLocked())
                        {
                            block.setNorth(Textures.Key.DoorLocked);
                            block.setSouth(Textures.Key.DoorLocked);
                        }
                        else
                        {
                            Textures.Key key = (random.nextBoolean()) ? Textures.Key.Door1 : Textures.Key.Door2Message;
                            
                            block.setNorth(key);
                            block.setSouth(key);
                        }
                    }
                }
                else
                {
                    if (level.get(column - 1, row).isDoor() || level.get(column + 1, row).isDoor())
                    {
                        block.setWest(Textures.Key.DoorSide);
                        block.setEast(Textures.Key.DoorSide);
                    }
                    else
                    {
                        block.setWest(wallKey);
                        block.setEast(wallKey);
                    }

                    //if this is the wall where the switch will be
                    if (block.isGoal())
                    {
                        if (block.getWall() == Location.Wall.North)
                        {
                            block.setNorth(wallKey);
                            block.setSouth(Textures.Key.GoalSwitchOff);
                        }
                        else
                        {
                            block.setNorth(Textures.Key.GoalSwitchOff);
                            block.setSouth(wallKey);
                        }
                    }
                    else
                    {
                        block.setNorth(wallKey);
                        block.setSouth(wallKey);
                    }
                }
                break;
        }
    }
    
    /**
     * Get a list of possible locations where a door can be locked
     * @param maze The object representing our level maze
     * @param random Object used to make random decisions
     * @param secrets List of secret rooms we have to avoid
     * @return List of possible locations where the door can be locked
     * @throws Exception 
     */
    protected static List<Cell> getLockedDoorOptions(final Labyrinth maze, final Random random, final List<Cell> secrets) throws Exception
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
                
                //skip if not valid
                if (!valid)
                    continue;
                
                //don't pick anything near the finish either
                for (int x = -1; x <= 1; x++)
                {
                    if (!valid)
                        break;
                    
                    for (int y = -1; y <= 1; y++)
                    {
                        if (!valid)
                            break;
                        
                        if (maze.getFinish().equals(col + x, row + y))
                            valid =  false;
                    }
                }
                
                //skip if not valid
                if (!valid)
                    continue;
                
                //the secret room is not an option
                for (Cell cell : secrets)
                {
                    if (cell.equals(col, row))
                    {
                        valid = false;
                        break;
                    }
                }
                
                if (valid)
                {
                    //at this point we have verified the location is on the unique path to the goal so this is a valid option
                    options.add(new Cell(col, row));
                }
            }
        }
        
        //recycle unused objects
        astar.dispose();
        astar = null;
        
        //return the list of options
        return options;
    }
    
    /**
     * Get the wall we want to lock for the locked room
     * @param maze The object representing our level maze
     * @param random Object used to make random decisions
     * @param location The location of the locked dooR
     * @return The wall that we want to be locked
     * @throws Exception 
     */
    protected static Location.Wall getLockedDoorWall(final Labyrinth maze, final Random random, final Cell location) throws Exception
    {
        //determine which wall will be locked in the specific room
        Location.Wall locked = null;
        
        //object used to calculate shortest path to goal
        AStar astar = new AStar();
        
        //set the start and finish
        astar.setStart(maze.getStart());
        astar.setGoal(maze.getFinish());
        
        //set our map
        astar.setMap(maze.getLocations());
        
        //calculate the path to the goal
        astar.calculate(random);

        for (int i=0; i < astar.getPath().size(); i++)
        {
            //we found our chosen locked door
            if (astar.getPath().get(i).equals(location))
            {
                //check the next location to the goal to determine which side is locked
                Cell tmp = astar.getPath().get(i + 1);

                //if the new location is to the east lock the east door
                if (tmp.getCol() > location.getCol())
                    locked = Location.Wall.East;

                //if the new location is to the west lock the west door
                if (tmp.getCol() < location.getCol())
                    locked = Location.Wall.West;

                //if the new location is to the north lock the north door
                if (tmp.getRow() > location.getRow())
                    locked = Location.Wall.South;

                //if the new location is to the south lock the south door
                if (tmp.getRow() < location.getRow())
                    locked = Location.Wall.North;

                //exit loop
                break;
            }
        }
        
        //recycle unused objects
        astar.dispose();
        astar = null;
        
        return locked;
    }
    
    /**
     * Locate a list of options where we could place the key
     * @param maze The object representing our level maze
     * @param random Object used to make random decisions
     * @param location The location of the locked door we want to avoid
     * @param secrets The list of secret rooms we also want to avoid
     * @return List of valid rooms where the key can be placed
     * @throws Exception 
     */
    protected static List<Cell> getKeyPlacementOptions(final Labyrinth maze, final Random random, final Cell location, final List<Cell> secrets) throws Exception
    {
        //optional rooms where the door can be locked
        List<Cell> options = new ArrayList<>();
        
        //object used to calculate shortest path to goal
        AStar astar = new AStar();
        
        //set the start and finish
        astar.setStart(maze.getStart());
        
        //set our map
        astar.setMap(maze.getLocations());

        //check each room in our maze
        for (int row=0; row < maze.getRows(); row++)
        {
            for (int col=0; col < maze.getCols(); col++)
            {
                //don't choose the same location as the room with locked door
                if (location.equals(col, row))
                    continue;

                //set the current location as the goal
                astar.setGoal(col, row);

                //we already have the map so calculate the path to the finish
                astar.calculate(random);

                //is this a valid location for the key
                boolean valid = true;

                //check if the locked door is part of the path
                for (Cell cell : astar.getPath())
                {
                    //if our locked room is part of this path then this isn't a valid location
                    if (cell.equals(location))
                    {
                        valid = false;
                        break;
                    }
                }

                //the key can't be placed in a secret room
                for (Cell cell : secrets)
                {
                    if (cell.equals(col, row))
                    {
                        valid = false;
                        break;
                    }
                }

                //don't pick anything near the locked door either
                for (int x = -1; x <= 1; x++)
                {
                    if (!valid)
                        break;

                    for (int y = -1; y <= 1; y++)
                    {
                        if (!valid)
                            break;

                        if (location.equals(col + x, row + y))
                            valid =  false;
                    }
                }

                //this is valid so add to list
                if (valid)
                    options.add(new Cell(col, row));
            }
        }
        
        //recycle unused objects
        astar.dispose();
        astar = null;
        
        //return our list of options
        return options;
    }
    
    protected static List<BonusItem.Type> getBonusTypesList()
    {
        //create list of valid bonus items
        List<BonusItem.Type> types = new ArrayList<>();
        
        //add types to list
        for (BonusItem.Type type : BonusItem.Type.values())
        {
            //exclude keys
            switch(type)
            {
                case Key1:
                case Key2:
                    continue;
            }
            
            types.add(type);
        }
        
        //return our list
        return types;
    }
    
    protected static void createRoom(final Level level, final Location location, final Random random) throws Exception
    {
        if (location.getCol() == 0)
        {
            changeBorder(level, Wall.West, State.Closed, location, false, false, false);

            if (location.getRow() == 0)
                changeBorder(level, Wall.North, State.Closed, location, false, false, false);
            if (location.getRow() == level.getMaze().getRows() - 1)
                changeBorder(level, Wall.South, State.Closed, location, false, false, false);
        } 
        else if (location.getRow() == 0)
        {
            changeBorder(level, Wall.North, State.Closed, location, false, false, false);

            if (location.getCol() == 0)
                changeBorder(level, Wall.West, State.Closed, location, false, false, false);
            if (location.getCol() == level.getMaze().getCols() - 1)
                changeBorder(level, Wall.East, State.Closed, location, false, false, false);
        }
        else if (location.getCol() == level.getMaze().getCols() - 1)
        {
            changeBorder(level, Wall.East, State.Closed, location, false, false, false);

            if (location.getRow() == 0)
                changeBorder(level, Wall.North, State.Closed, location, false, false, false);
            if (location.getRow() == level.getMaze().getRows() - 1)
                changeBorder(level, Wall.South, State.Closed, location, false, false, false);
        }
        else if (location.getRow() == level.getMaze().getRows() - 1)
        {
            changeBorder(level, Wall.South, State.Closed, location, false, false, false);

            if (location.getCol() == 0)
                changeBorder(level, Wall.West, State.Closed, location, false, false, false);
            if (location.getCol() == level.getMaze().getCols() - 1)
                changeBorder(level, Wall.East, State.Closed, location, false, false, false);
        }

        if (location.hasWall(Wall.South))
        {
            changeBorder(level, Wall.South, State.Closed, location, false, false, false);
        }
        else
        {
            //decide at random whether the wall stays open or becomes a door
            changeBorder(level, Wall.South, (hasRandomDecision(random)) ? State.Door : State.Open, location, false, false, false);
        }

        if (location.hasWall(Wall.East))
        {
            changeBorder(level, Wall.East, State.Closed, location, false, false, false);
        }
        else
        {
            //decide at random whether the wall stays open or becomes a door
            changeBorder(level, Wall.East, (hasRandomDecision(random)) ? State.Door : State.Open, location, false, false, false);
        }
    }
    
    private static boolean hasRandomDecision(final Random random)
    {
        return (random.nextInt(RANDOM_DOOR_PROBABILITY) == 0);
    }
}