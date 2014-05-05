package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.wolfenstein.display.Textures.Key;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Level extends BlockManager implements Disposable
{
    //the overall maze of the level, each cell will represent a room
    private Labyrinth maze;
    
    //the wall where the goal switch is located
    private Wall wallGoal;
    
    //what is the limit of secrets and the current count
    private int secretLimit;
    
    //a list containing all of the secrets in the level
    private List<Cell> secrets;
    
    //has the switch been hit
    private boolean complete = false;
    
    /**
     * The different options for each border in each room
     * Open - no walls
     * Closed - all walls
     * Door - all walls with a door in the middle
     */
    private enum State
    {
        Open, Closed, Door
    }
    
    /**
     * Create a new level
     * @param columns The total number of columns in our overall maze
     * @param rows The total number of rows in our overall maze
     * @param roomColumns The total number of columns for each room
     * @param roomRows The total number of rows for each room
     * @throws Exception 
     */
    public Level(final int columns, final int rows, final int roomColumns, final int roomRows, final Random random) throws Exception
    {
        super(columns, rows, roomColumns, roomRows);
        
        //the number of maximum secrets allowed will be half the number of columns
        this.secretLimit = (columns / 2);
        
        //create new list
        this.secrets = new ArrayList<>();
        
        //pick a random maze generation algorithm
        Labyrinth.Algorithm algorithm = Labyrinth.Algorithm.values()[random.nextInt(Labyrinth.Algorithm.values().length)];
        
        //generate our overall maze, each cell in this maze will represent a room
        this.maze = new Labyrinth(columns, rows, algorithm);
        this.maze.setStart(0, 0);
        this.maze.generate();
        
        //locate the goal for our maze
        locateGoal();
        
        //first we will enclose all rooms with walls
        createWalls(random);
        
        //make the goal room a room
        createGoalRoom();
        
        //add secret rooms to our maze
        createSecrets(random);
        
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
        
        wallGoal = null;
        
        secrets.clear();
        secrets = null;
        
        super.dispose();
    }
    
    /**
     * Locate the room where the goal switch will be as well as the wall where the switch will be placed
     * @throws Exception 
     */
    private void locateGoal() throws Exception
    {
        //the cost to reach the specific location
        int cost = 0;
        
        //temp variable
        Location tmp;
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int column = 0; column < maze.getCols(); column++)
            {
                //get the current location
                tmp = maze.getLocation(column, row);
                
                //the location with the highest cost will be the goal
                if (tmp.getCost() >= cost)
                {
                    //set the new cost limit
                    cost = tmp.getCost();
                    
                    //now we have a new goal set the finish
                    this.maze.setFinish(column, row);
                    
                    //locate the side where the goal switch will be
                    for (Wall wall : Wall.values())
                    {
                        if (tmp.hasWall(wall))
                        {
                            this.wallGoal = wall;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void createSecrets(final Random random) throws Exception
    {
        //temporary locations
        Location tmp;

        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int column = 0; column < maze.getCols(); column++)
            {
                //if we reached our limit no need to continue
                if (secrets.size() == this.secretLimit)
                    return;
                
                //the secret room can't be the start or finish of the maze
                if (maze.getFinish().equals(column, row) || maze.getStart().equals(column, row))
                    continue;
                
                //get the current location
                tmp = maze.getLocation(column, row);
                
                boolean secret = false;
                
                //there are 3 walls so this is a possible secret room
                if (tmp.getWalls().size() == 3)
                {
                    //random choose if this door is a secret or not
                    secret = random.nextBoolean();

                    if (secret)
                    {
                        secrets.add(new Cell(column, row));

                        for (Wall wall : Wall.values())
                        {
                            if (!tmp.hasWall(wall))
                            {
                                changeBorder(column, row, wall, State.Door, secret);
                                
                                switch(wall)
                                {
                                    case North:
                                        changeBorder(column, row-1, Wall.South, State.Door, secret);
                                        break;
                                        
                                    case South:
                                        changeBorder(column, row+1, Wall.North, State.Door, secret);
                                        break;
                                        
                                    case West:
                                        changeBorder(column-1, row, Wall.East, State.Door, secret);
                                        break;
                                        
                                    case East:
                                        changeBorder(column+1, row, Wall.West, State.Door, secret);
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void createWalls(final Random random) throws Exception
    {
        //temporary locations
        Location tmp;

        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int column = 0; column < maze.getCols(); column++)
            {
                tmp = maze.getLocation(column, row);
                
                //determine if we are to open the wall or create a door
                State state = (random.nextBoolean()) ? State.Open : State.Door;
                
                //close the borders that are walls
                for (Wall wall : Wall.values())
                {
                    if (tmp.hasWall(wall))
                    {
                        //add wall to this side
                        changeBorder(column, row, wall, State.Closed, false);
                    }
                    else
                    {
                        //add opening or door to this side
                        changeBorder(column, row, wall, state, false);
                    }
                }
            }
        }
    }
    
    private void createGoalRoom() throws Exception
    {
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int column = 0; column < maze.getCols(); column++)
            {
                //we are only interested in the goal
                if (!isGoal(column, row))
                    continue;
                
                Location tmp = maze.getLocation(column, row);
                
                for (Wall wall : Wall.values())
                {
                    if (!tmp.hasWall(wall))
                        changeBorder(column, row, wall, State.Door, false);
                }
                
                //we found the goal room so no need to continue
                return;
            }
        }
    }
    
    private boolean isGoal(final int column, final int row) throws Exception
    {
        return this.maze.getFinish().equals(column, row);
    }
    
    /**
     * Assign textures to the solid blocks
     * @throws Exception 
     */
    private void assignTextures() throws Exception
    {
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int column = 0; column < maze.getCols(); column++)
            {
                //set the textures for the walls in the room
                for (Wall wall : Wall.values())
                {
                    setBorderTextures(column, row, wall, Key.Door1, Key.Cement1);
                }
            }
        }
    }
    
    /**
     * Set the wall textures for the specified side in the specified room
     * @param column Used to identify the current room
     * @param row Used to identify the current room
     * @param wall Side we want to set the texture key
     * @param doorKey Door texture key
     * @param wallKey Wall texture key
     */
    private void setBorderTextures(final int column, final int row, final Wall wall, final Key doorKey, final Key wallKey) throws Exception
    {
        //is this room our goal and the wall where the goal switch will appear
        final boolean isGoal = isGoal(column, row);
        final boolean isGoalWall = (wall == this.wallGoal);
        
        for (int roomRow = 0; roomRow < getRoomRowTotal() + 1; roomRow++)
        {
            if (wall == Wall.North && roomRow != 0)
                continue;
            if (wall == Wall.South && roomRow != getRoomRowTotal())
                continue;
            
            for (int roomColumn = 0; roomColumn < getRoomColumnTotal() + 1; roomColumn++)
            {
                if (wall == Wall.East && roomColumn != getRoomColumnTotal())
                    continue;
                if (wall == Wall.West && roomColumn != 0)
                    continue;
                
                //calculate the current index
                final int currentCol = (column * getRoomColumnTotal()) + roomColumn;
                final int currentRow = (row    * getRoomRowTotal()) + roomRow;
                
                //get current block
                final Block block = this.get(currentCol, currentRow);
                
                //only solid blocks will have textures
                if (block.isSolid())
                {
                    //if this isn't the goal
                    if (!isGoal)
                    {
                        //if any of the texture keys are set no need to continue
                        if (block.getEast() != null || block.getWest()!= null)
                            continue;
                        if (block.getNorth()!= null || block.getSouth()!= null)
                            continue;
                    }
                    
                    //the different textures for each side
                    Key n = null, s = null, e = null, w = null;
                    
                    switch(wall)
                    {
                        case North:
                        case South:
                            if (block.isDoor() && !block.getDoor().isSecret())
                            {
                                n = doorKey;
                                s = doorKey;
                                w = Key.DoorSide;
                                e = Key.DoorSide;
                                
                                if (isGoal)
                                {
                                    n = Key.DoorGoal1;
                                    s = Key.DoorGoal1;
                                }
                            }
                            else
                            {
                                if (get(currentCol - 1, currentRow).isDoor() || get(currentCol + 1, currentRow).isDoor())
                                {
                                    w = Key.DoorSide;
                                    e = Key.DoorSide;
                                }
                                else
                                {
                                    w = wallKey;
                                    e = wallKey;
                                }

                                n = wallKey;
                                s = wallKey;
                                
                                //if this is the wall where the switch will be
                                if (isGoal && isGoalWall)
                                {
                                    //the switch will be in the middle of the wall
                                    if (roomColumn == (int)(getRoomColumnTotal() / 2))
                                    {
                                        //mark this block as the goal
                                        block.setGoal(true);
                                        
                                        if (wall == Wall.North)
                                        {
                                            n = wallKey;
                                            s = Key.GoalSwitchOff;
                                        }
                                        else
                                        {
                                            n = Key.GoalSwitchOff;
                                            s = wallKey;
                                        }
                                    }
                                }
                            }
                            break;

                        case East:
                        case West:
                            if (block.isDoor() && !block.getDoor().isSecret())
                            {
                                n = Key.DoorSide;
                                s = Key.DoorSide;
                                w = doorKey;
                                e = doorKey;
                                
                                if (isGoal)
                                {
                                    w = Key.DoorGoal1;
                                    e = Key.DoorGoal1;
                                }
                            }
                            else
                            {
                                if (get(currentCol, currentRow + 1).isDoor() || get(currentCol, currentRow - 1).isDoor())
                                {
                                    n = Key.DoorSide;
                                    s = Key.DoorSide;
                                }
                                else
                                {
                                    n = wallKey;
                                    s = wallKey;
                                }
                                
                                w = wallKey;
                                e = wallKey;
                                
                                //if this is the wall where the switch will be
                                if (isGoal && isGoalWall)
                                {
                                    //the switch will be in the middle of the wall
                                    if (roomRow == (int)(getRoomRowTotal() / 2))
                                    {
                                        //mark this block as the goal
                                        block.setGoal(true);
                                        
                                        if (wall == Wall.West)
                                        {
                                            w = wallKey;
                                            e = Key.GoalSwitchOff;
                                        }
                                        else
                                        {
                                            w = Key.GoalSwitchOff;
                                            e = wallKey;
                                        }
                                    }
                                }
                            }
                            break;
                    }
                    
                    block.setNorth(n);
                    block.setSouth(s);
                    block.setEast(e);
                    block.setWest(w);
                }
            }
        }
    }
    
    /**
     * Turn the switch on to show that the level has been solved
     */
    public void markComplete() throws Exception
    {
        for (int row=0; row < getRows(); row++)
        {
            for (int col=0; col < getCols(); col++)
            {
                Block block = this.get(col, row);
                    
                //if not the goal skip to next
                if (!block.isGoal())
                    continue;
                
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
        }
    }
    
    public boolean isComplete()
    {
        return this.complete;
    }
    
    /**
     * Manipulate the border at the specified location.<br>
     * For the specified room we can open the specified wall or create a door.
     * @param column The column of the overall maze
     * @param row The row of the overall maze
     * @param wall The wall we want to manipulate in the room
     * @param state Do we create a wall, leave open, or create a door
     * @param secret If a door is being created is it a secret
     */
    private void changeBorder(final int column, final int row, final Wall wall, final State state, final boolean secret)
    {
        for (int roomRow = 0; roomRow < getRoomRowTotal() + 1; roomRow++)
        {
            if (wall == Wall.North && roomRow != 0)
                continue;
            if (wall == Wall.South && roomRow != getRoomRowTotal())
                continue;
            
            for (int roomColumn = 0; roomColumn < getRoomColumnTotal() + 1; roomColumn++)
            {
                if (wall == Wall.East && roomColumn != getRoomColumnTotal())
                    continue;
                if (wall == Wall.West && roomColumn != 0)
                    continue;
                
                //calculate the current index
                final int currentCol = (column * getRoomColumnTotal()) + roomColumn;
                final int currentRow = (row    * getRoomRowTotal())    + roomRow;
                
                switch(wall)
                {
                    case North:
                        
                        //we still want walls on the end
                        if (roomColumn == 0 || roomColumn == getRoomColumnTotal())
                        {
                            set(currentCol, currentRow, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    set(currentCol, currentRow, new SolidBlock());
                                    break;
                            }
                        }
                        break;

                    case South:
                        
                        //we still want walls on the end
                        if (roomColumn == 0 || roomColumn == getRoomColumnTotal())
                        {
                            set(currentCol, currentRow, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    set(currentCol, currentRow, new SolidBlock());
                                    break;

                                case Door:
                                    if (roomColumn == getRoomColumnTotal() / 2 || roomRow == getRoomRowTotal() / 2)
                                    {
                                        //if this is the middle add a door
                                        set(currentCol, currentRow, new SolidBlock(true, secret));
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        set(currentCol, currentRow, new SolidBlock());
                                    }
                                    break;
                            }
                        }
                        break;

                    case West:
                        
                        //we also want to avoid the end rows
                        if (roomRow == 0 || roomRow == getRoomRowTotal())
                        {
                            set(currentCol, currentRow, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    set(currentCol, currentRow, new SolidBlock());
                                    break;
                            }
                        }
                        break;

                    case East:
                        
                        //we also want to avoid the end rows
                        if (roomRow == 0 || roomRow == getRoomRowTotal())
                        {
                            set(currentCol, currentRow, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    set(currentCol, currentRow, new SolidBlock());
                                    break;

                                case Door:
                                    if (roomColumn == getRoomColumnTotal() / 2 || roomRow == getRoomRowTotal() / 2)
                                    {
                                        //if this is the middle add a door
                                        set(currentCol, currentRow, new SolidBlock(true, secret));
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        set(currentCol, currentRow, new SolidBlock());
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
    }
    
    /**
     * Here we will manage the door animations
     * @param time Time duration per update to deduct from timer
     * @param playerX Current player's location 
     * @param playerZ Current player's location
     */
    public void update(final long time, final int playerX, final int playerZ)
    {
        final int distance = 2;
        
        for (int row=0; row < super.getRows(); row++)
        {
            for (int col=0; col < super.getCols(); col++)
            {
                //get current block
                final Block b = get(col, row);
                
                //we are only interested in the door(s)
                if (!b.isDoor())
                    continue;
                
                //if the door is open don't update if the player is to close to it
                if (b.getDoor().isOpen())
                {
                    //if the player is close enough to a block, then skip it
                    if (col >= playerX - distance && col <= playerX + distance &&
                        row >= playerZ - distance && row <= playerZ + distance)
                        continue;
                }
                
                //update door status
                b.getDoor().update(time);
            }
        }
    }
}