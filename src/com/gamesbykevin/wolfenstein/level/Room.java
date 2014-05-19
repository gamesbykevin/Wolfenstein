package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.wolfenstein.display.Textures;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Room extends BlockManager implements Disposable
{
    //is this room a secret
    private boolean secret = false;
    
    //is this room the goal
    private boolean goal = false;
    
    //has the player visited the room
    private boolean visited = false;
    
    //where the room is located
    private Cell location;
    
    //the boundaries of the room
    private final int firstRow, lastRow, firstColumn, lastColumn;
    
    /**
     * The different options for each border in each room
     * Open - no walls
     * Closed - all walls
     * Door - all walls with a door in the middle
     */
    protected enum State
    {
        Open, Closed, Door
    }
    
    protected Room(final int column, final int row, final int roomColumnTotal, final int roomRowTotal)
    {
        //call parent constructor
        super(roomColumnTotal, roomRowTotal);
        
        //set the boundaries
        this.firstRow = 0;
        this.firstColumn = 0;
        this.lastRow = getRowCount() - 1;
        this.lastColumn = getColumnCount() - 1;
        
        //store the location of the room
        this.location = new Cell(column, row);
    }
    
    @Override
    public void dispose()
    {
        location = null;
        
        super.dispose();
    }
    
    protected int getFirstRow()
    {
        return this.firstRow;
    }
    
    protected int getLastRow()
    {
        return this.lastRow;
    }
    
    protected int getFirstColumn()
    {
        return this.firstColumn;
    }
    
    protected int getLastColumn()
    {
        return this.lastColumn;
    }
    
    /**
     * Does the location provided match the location of this room
     * @param column Column where room is located
     * @param row Row where room is located
     * @return true if the location matches, false otherwise
     */
    protected boolean isLocation(final int column, final int row)
    {
        return location.equals(column, row);
    }
    
    /**
     * Get the room location in the level
     * @return Cell containing the column, row where the room is in the level
     */
    protected Cell getLocation()
    {
        return this.location;
    }
    
    protected int getRow()
    {
        return (int)this.location.getRow();
    }
    
    protected int getCol()
    {
        return (int)this.location.getCol();
    }
    
    protected boolean hasAdjustedLocation(final double adjustedColumn, final double adjustedRow)
    {
        for (int row = firstRow; row <= lastRow; row++)
        {
            for (int column = firstColumn; column <= lastColumn; column++)
            {
                //if we found the location
                if (getAdjustedCol(column) == adjustedColumn && getAdjustedRow(row) == adjustedRow)
                    return true;
            }
        }
        
        return false;
    }
    
    protected void setSecret(final boolean secret)
    {
        this.secret = secret;
    }
    
    protected boolean isSecret()
    {
        return this.secret;
    }
    
    protected void setVisited(final boolean visited)
    {
        this.visited = visited;
    }
    
    protected boolean hasVisited()
    {
        return this.visited;
    }
    
    protected void setGoal(final boolean goal)
    {
        this.goal = goal;
    }
    
    protected boolean isGoal()
    {
        return this.goal;
    }
    
    public Block getUnlockedBlock()
    {
        for (int row = firstRow; row <= lastRow; row++)
        {
            for (int column = firstColumn; column <= lastColumn; column++)
            {
                Block block = super.get(column, row);
                
                //we only want the blocks that exist and are doors
                if (block == null || !block.isDoor())
                    continue;
                
                //if door is already locked or a secret door skip
                if (block.getDoor().isLocked() || block.getDoor().isSecret())
                    continue;
                
                return block;
            }
        }
        
        return null;
    }
    
    /**
     * Change the border defined by the specific wall according to the specific state provided
     * @param wall The side of the room we want to change
     * @param state The state of that side, Open, Closed, Door
     */
    protected void changeBorder(final Wall wall, final State state)
    {
        for (int row = firstRow; row <= lastRow; row++)
        {
            //if north wall we only want to change the first row
            if (wall == Wall.North && row != firstRow)
                continue;
            
            //if south wall we only want to change the last row
            if (wall == Wall.South && row != lastRow)
                continue;

            for (int column = firstColumn; column <= lastColumn; column++)
            {
                //if west wall we only want to change the first column
                if (wall == Wall.West && column != firstColumn)
                    continue;
                
                //if east wall we only want to change the last column
                if (wall == Wall.East && column != lastColumn)
                    continue;

                switch(wall)
                {
                    case North:
                        
                        //we still want walls on the end regardless of the state
                        if (column == firstColumn || column == lastColumn)
                        {
                            set(column, row, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    set(column, row, new SolidBlock());
                                    break;
                            }
                        }
                        break;
                        
                    case South:
                        
                        //we still want walls on the end
                        if (column == firstColumn || column == lastColumn)
                        {
                            set(column, row, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    
                                    //this will be a solid closed block
                                    set(column, row, new SolidBlock());
                                    break;

                                case Door:
                                    
                                    if (column == (int)(getColumnCount() / 2) || row == (int)(getRowCount() / 2))
                                    {
                                        //if this is the middle add a door
                                        set(column, row, new SolidBlock(true, isSecret()));
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        set(column, row, new SolidBlock());
                                    }
                                    break;
                            }
                        }
                        break;
                        
                    case West:
                        
                        //we still want walls on the end
                        if (row == firstRow || row == lastRow)
                        {
                            set(column, row, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    
                                    set(column, row, new SolidBlock());
                                    break;
                            }
                        }
                        break;

                    case East:
                        
                        //we also want to avoid the end rows
                        if (row == firstRow || row == lastRow)
                        {
                            set(column, row, new SolidBlock());
                        }
                        else
                        {
                            switch(state)
                            {
                                case Closed:
                                    
                                    //this will be a solid closed block
                                    set(column, row, new SolidBlock());
                                    break;

                                case Door:
                                    if (column == (int)(getColumnCount() / 2) || row == (int)(getRowCount() / 2))
                                    {
                                        //if this is the middle add a door
                                        set(column, row, new SolidBlock(true, isSecret()));
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        set(column, row, new SolidBlock());
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
    * Pick a random location in the room
    * @param random Object used to make random decisions
    * @return Random location in the room that won't be inside a wall or blocking the door way. <br>
    * The location will be adjusted according to the room position
    */
    public Cell getRandomLocation(final Random random)
    {
        //new list for possible location
        List<Cell> options = new ArrayList<>();
        
        for (int row = firstRow + 2; row < lastRow - 1; row++)
        {
            //don't choose middle row
            if (row == (int)(getRowCount() / 2))
                continue;
            
            for (int column = firstColumn + 2; column < lastColumn - 1; column++)
            {
                //don't choose middle column
                if (column == (int)(getColumnCount() / 2))
                    continue;
                
                Block block = get(column, row);
                
                //the block exists
                if (block != null)
                {
                    //not a valid location if the block is solid
                    if (block.isSolid())
                        continue;
                }
                
                //calculate the adjusted location
                final int adjustedCol = getAdjustedCol(column);
                final int adjustedRow = getAdjustedRow(row);
                
                //add new valid location to the list
                options.add(new Cell(adjustedCol, adjustedRow));
            }
        }
        
        //get random adjusted location
        return options.get(random.nextInt(options.size()));
    }
    
    /**
     * Get the adjusted column based on the entire size of the maze
     * @param column Column inside the room
     * @return The adjusted column taking into account the room position
     */
    protected int getAdjustedCol(final int column)
    {
        return ((int)(location.getCol() * getColumnCount()) + column);
    }
    
    /**
     * Get the adjusted row based on the entire size of the maze
     * @param row Row inside the room
     * @return The adjusted row taking into account the room position
     */
    protected int getAdjustedRow(final int row)
    {
        return ((int)(location.getRow() * getRowCount()) + row);
    }
    
    /**
     * Get the column inside the room
     * @param x x-location
     * @return The column inside the room
     */
    protected int getRoomColumn(final double x)
    {
        return (int)(x - (location.getCol() * getColumnCount()));
    }
    
    /**
     * Get the row inside the room
     * @param z z-location
     * @return The row inside the room
     */
    protected int getRoomRow(final double z)
    {
        return (int)(z - (location.getRow() * getRowCount()));
    }
    
    /**
     * Get the block at the specified location
     * @param x x-location
     * @param z z-location
     * @return The block at the specified position
     */
    protected Block getAdjustedBlock(final double x, final double z)
    {
        return get(getRoomColumn(x), getRoomRow(z));
    }
    
    /**
     * Set the wall textures for the specified side in the specified room
     * @param wall Side we want to set the texture key
     * @param doorKey Door texture key
     * @param wallKey Wall texture key
     */
    protected void assignTextures(final Wall wall, final Textures.Key doorKey, final Textures.Key wallKey) throws Exception
    {
        for (int row = firstRow; row <= lastRow; row++)
        {
            //if north wall we only want to change the first row
            if (wall == Wall.North && row != firstRow)
                continue;
            
            //if south wall we only want to change the last row
            if (wall == Wall.South && row != lastRow)
                continue;
            
            for (int column = firstColumn; column <= lastColumn; column++)
            {
                //if east wall we only want to change the last column
                if (wall == Wall.East && column != lastColumn)
                    continue;
                
                //if west wall we only want to change the first column
                if (wall == Wall.West && column != firstColumn)
                    continue;
                
                //get current block
                final Block block = this.get(column, row);

                if (!block.isSolid())
                    continue;
                
                switch(wall)
                {
                    case North:
                    case South:
                        assignVerticalWalls(block, wall, column, row, doorKey, wallKey);
                        break;

                    case East:
                    case West:
                        assignHorizontalWalls(block, wall, column, row, doorKey, wallKey);
                        break;
                }

            }
        }
    }
    
    /**
     * Assign textures for walls on the west and east
     */
    private void assignHorizontalWalls(final Block block, final Wall wall, final int column, final int row, final Textures.Key doorKey, final Textures.Key wallKey)
    {
        if (block.isDoor() && !block.getDoor().isSecret())
        {
            block.setNorth(Textures.Key.DoorSide);
            block.setSouth(Textures.Key.DoorSide);

            if (isGoal())
            {
                block.setWest(Textures.Key.DoorGoal1);
                block.setEast(Textures.Key.DoorGoal1);
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
                    block.setWest(doorKey);
                    block.setEast(doorKey);
                }
            }
        }
        else
        {
            if (get(column, row + 1).isDoor() || get(column, row - 1).isDoor())
            {
                block.setNorth(Textures.Key.DoorSide);
                block.setSouth(Textures.Key.DoorSide);
            }
            else
            {
                block.setNorth(wallKey);
                block.setSouth(wallKey);
            }

            block.setWest(wallKey);
            block.setEast(wallKey);

            //if this is the wall where the switch will be
            if (isGoal())
            {
                //the switch will be in the middle of the wall
                if (row == (int)(getRowCount() / 2))
                {
                    //mark this block as the goal
                    block.setGoal(true);

                    if (wall == Wall.West)
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
            }
        }
    }
    
    /**
     * Assign textures for walls on the north and south
     */
    private void assignVerticalWalls(final Block block, final Wall wall, final int column, final int row, final Textures.Key doorKey, final Textures.Key wallKey)
    {
        if (block.isDoor() && !block.getDoor().isSecret())
        {
            block.setWest(Textures.Key.DoorSide);
            block.setEast(Textures.Key.DoorSide);

            if (isGoal())
            {
                block.setNorth(Textures.Key.DoorGoal1);
                block.setSouth(Textures.Key.DoorGoal1);
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
                    block.setNorth(doorKey);
                    block.setSouth(doorKey);
                }
            }
        }
        else
        {
            if (get(column - 1, row).isDoor() || get(column + 1, row).isDoor())
            {
                block.setWest(Textures.Key.DoorSide);
                block.setEast(Textures.Key.DoorSide);
            }
            else
            {
                block.setWest(wallKey);
                block.setEast(wallKey);
            }

            block.setNorth(wallKey);
            block.setSouth(wallKey);

            //if this is the wall where the switch will be
            if (isGoal())
            {
                //the switch will be in the middle of the wall
                if (column == (int)(getColumnCount() / 2))
                {
                    //mark this block as the goal
                    block.setGoal(true);

                    if (wall == Wall.North)
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
            }
        }
    }
}