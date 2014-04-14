package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.framework.labyrinth.Location.Wall;
import com.gamesbykevin.wolfenstein.display.Textures.Key;

public final class Level 
{
    public Block[][] blocks;
    
    //the overall maze of the level, each cell will represent a room
    private Labyrinth maze;
    
    //the dimensions of each room
    private final int roomColumns, roomRows;
    
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
    public Level(final int columns, final int rows, final int roomColumns, final int roomRows) throws Exception
    {
        this.roomColumns = roomColumns;
        this.roomRows    = roomRows;
        
        //generate our overall maze, each cell in this maze will represent a room
        this.maze = new Labyrinth(columns, rows, Labyrinth.Algorithm.Sidewinder);
        this.maze.setStart(0, 0);
        this.maze.generate();
        
        //create all of the blocks for all of the levels
        this.blocks  = new Block[roomRows * rows][roomColumns * columns];
        
        //temp locations
        Location tmp;

        //first we will enclose all rooms with walls
        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                tmp = maze.getLocation(column, row);
                
                //for right now we determine here where to open the wall or create a door
                final State state = (Math.random() > .5) ? State.Open : State.Door;
                
                if (!tmp.hasWall(Wall.North))
                {
                    changeBorder(column, row,     Wall.North, state);
                    changeBorder(column, row - 1, Wall.South, state);
                }
                else
                {
                    changeBorder(column, row,     Wall.North, State.Closed);
                }
                
                if (!tmp.hasWall(Wall.South))
                {
                    changeBorder(column, row,     Wall.South, state);
                    changeBorder(column, row + 1, Wall.North, state);
                }
                else
                {
                    changeBorder(column, row,     Wall.South, State.Closed);
                }
                
                if (!tmp.hasWall(Wall.West))
                {
                    changeBorder(column,     row, Wall.West, state);
                    changeBorder(column - 1, row, Wall.East, state);
                }
                else
                {
                    changeBorder(column, row,     Wall.West, State.Closed);
                }
                
                if (!tmp.hasWall(Wall.East))
                {
                    changeBorder(column    , row, Wall.East, state);
                    changeBorder(column + 1, row, Wall.West, state);
                }
                else
                {
                    changeBorder(column, row,     Wall.East, State.Closed);
                }
            }
        }
        
        //if any remaining blocks are null create empty blocks
        for (int row=0; row < blocks.length; row++)
        {
            for (int col=0; col < blocks[0].length; col++)
            {
                if (blocks[row][col] == null)
                    blocks[row][col] = new Block();
            }
        }
    }
    
    /**
     * Manipulate the border at the specified location.<br>
     * For the specified room we can open the specified wall or create a door.
     * @param column The column of the overall maze
     * @param row The row of the overall maze
     * @param wall The wall we want to manipulate in the room
     * @param state Do we create a wall, leave open, or create a door
     */
    private void changeBorder(final int column, final int row, final Wall wall, final State state)
    {
        for (int roomRow = 0; roomRow < this.roomRows; roomRow++)
        {
            for (int roomColumn = 0; roomColumn < this.roomColumns; roomColumn++)
            {
                //calculate the current index
                final int currentCol = (column * this.roomColumns) + roomColumn;
                final int currentRow = (row    * this.roomRows)    + roomRow;
                
                switch(wall)
                {
                    case North:
                        
                        //if this is not the north row skip
                        if (roomRow != 0)
                            continue;
                        
                        //we still want walls on the end
                        if (roomColumn == 0 || roomColumn == this.roomColumns - 1)
                        {
                            blocks[currentRow][currentCol] = new SolidBlock(Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4);
                        }
                        else
                        {
                            switch(state)
                            {
                                case Open:
                                    //if open then create empty block
                                    blocks[currentRow][currentCol] = new Block();
                                    break;

                                case Closed:
                                    blocks[currentRow][currentCol] = new SolidBlock(Key.DoorMessage, Key.DoorMessage, Key.DoorMessage, Key.DoorMessage);
                                    break;

                                case Door:
                                    if (currentCol == this.roomColumns / 2)
                                    {
                                        blocks[currentRow][currentCol] = new Block();
                                        //if this is the middle add a door
                                        //blocks[currentRow][currentCol] = new SolidBlock(Key.Door1, Key.Door1, Key.DoorSide, Key.DoorSide);
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        blocks[currentRow][currentCol] = new SolidBlock(Key.Cement1, Key.Cement2, Key.DoorSide, Key.DoorSide);
                                    }
                                    break;
                            }
                        }
                        break;

                    case South:
                        
                        //if this is not the south row skip
                        if (roomRow != this.roomRows - 1)
                            continue;
                        
                        //we still want walls on the end
                        if (roomColumn == 0 || roomColumn == this.roomColumns - 1)
                        {
                            blocks[currentRow][currentCol] = new SolidBlock(Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4);
                        }
                        else
                        {
                            switch(state)
                            {
                                case Open:
                                    //if open then create empty block
                                    blocks[currentRow][currentCol] = new Block();
                                    break;

                                case Closed:
                                    blocks[currentRow][currentCol] = new SolidBlock(Key.DoorMessage, Key.DoorMessage, Key.DoorMessage, Key.DoorMessage);
                                    break;

                                case Door:
                                    if (currentCol == this.roomColumns / 2)
                                    {
                                        blocks[currentRow][currentCol] = new Block();
                                        //if this is the middle add a door
                                        //blocks[currentRow][currentCol] = new SolidBlock(Key.Door1, Key.Door1, Key.DoorSide, Key.DoorSide);
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        blocks[currentRow][currentCol] = new SolidBlock(Key.Cement1, Key.Cement2, Key.DoorSide, Key.DoorSide);
                                    }
                                    break;
                            }
                        }
                        break;

                    case West:
                        
                        //if this is not the west column skip
                        if (roomColumn != 0)
                            continue;
                        
                        //we also want to avoid the end rows
                        if (roomRow == 0 || roomRow == this.roomRows - 1)
                        {
                            blocks[currentRow][currentCol] = new SolidBlock(Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4);
                        }
                        else
                        {
                            switch(state)
                            {
                                case Open:
                                    //if open then create empty block
                                    blocks[currentRow][currentCol] = new Block();
                                    break;

                                case Closed:
                                    blocks[currentRow][currentCol] = new SolidBlock(Key.DoorMessage, Key.DoorMessage, Key.DoorMessage, Key.DoorMessage);
                                    break;

                                case Door:
                                    if (currentRow == this.roomRows / 2)
                                    {
                                        blocks[currentRow][currentCol] = new Block();
                                        //if this is the middle add a door
                                        //blocks[currentRow][currentCol] = new SolidBlock(Key.DoorSide, Key.DoorSide, Key.Door1, Key.Door1);
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        blocks[currentRow][currentCol] = new SolidBlock(Key.DoorSide, Key.DoorSide, Key.Cement1, Key.Cement1);
                                    }
                                    break;
                            }
                        }
                        break;

                    case East:
                        
                        //if this is not the east column skip
                        if (roomColumn != this.roomColumns - 1)
                            continue;
                        
                        //we also want to avoid the end rows
                        if (roomRow == 0 || roomRow == this.roomRows - 1)
                        {
                            blocks[currentRow][currentCol] = new SolidBlock(Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4, Key.HitlerPortrait4);
                        }
                        else
                        {
                            switch(state)
                            {
                                case Open:
                                    //if open then create empty block
                                    blocks[currentRow][currentCol] = new Block();
                                    break;

                                case Closed:
                                    blocks[currentRow][currentCol] = new SolidBlock(Key.DoorMessage, Key.DoorMessage, Key.DoorMessage, Key.DoorMessage);
                                    break;

                                case Door:
                                    if (currentRow == this.roomRows / 2)
                                    {
                                        blocks[currentRow][currentCol] = new Block();
                                        //if this is the middle add a door
                                        //blocks[currentRow][currentCol] = new SolidBlock(Key.DoorSide, Key.DoorSide, Key.Door1, Key.Door1);
                                    }
                                    else
                                    {
                                        //everything else is a wall
                                        blocks[currentRow][currentCol] = new SolidBlock(Key.DoorSide, Key.DoorSide, Key.Cement1, Key.Cement1);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        }
    }
    
    public int getCols()
    {
        return this.blocks[0].length;
    }
    
    public int getRows()
    {
        return this.blocks.length;
    }
    
    public Block get(final double x, final double y)
    {
        return get((int)x, (int)y);
    }
    
    public Block get(final int x, final int y)
    {
        //if the index is out of bounds return a default solid block
        if (x < 0 || x >= blocks[0].length || y < 0 || y >= blocks.length)
            return Block.solidBlock;
        
        return blocks[y][x];
    }
}