package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.wolfenstein.display.Textures.Key;

public final class Level 
{
    public Block[][] blocks;
    
    //the overall maze of the level, each cell will represent a room
    private Labyrinth maze;
    
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
        //generate our overall maze, each cell in this maze will represent a room
        this.maze = new Labyrinth(columns, rows, Labyrinth.Algorithm.Sidewinder);
        this.maze.setStart(0, 0);
        this.maze.generate();
        
        //create all of the blocks for all of the levels
        this.blocks  = new Block[roomRows * rows][roomColumns * columns];
        
        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                //get the current room
                Location tmp = maze.getLocation(column, row);
                
                for (int roomRow = 0; roomRow < roomRows; roomRow++)
                {
                    for (int roomColumn = 0; roomColumn < roomColumns; roomColumn++)
                    {
                        if (!tmp.hasWall(Location.Wall.West) && roomColumn == 0)
                        {
                            if (roomRow != 0 && roomRow != roomRows - 1)
                                continue;
                        }
                        
                        if (!tmp.hasWall(Location.Wall.East) && roomColumn == roomColumns - 1)
                        {
                            if (roomRow != 0 && roomRow != roomRows - 1)
                                continue;
                        }
                        
                        if (!tmp.hasWall(Location.Wall.North) && roomRow == 0)
                        {
                            if (roomColumn != 0 && roomColumn != roomColumns - 1)
                                continue;
                        }
                        
                        if (!tmp.hasWall(Location.Wall.South) && roomRow == roomRows - 1)
                        {
                            if (roomColumn != 0 && roomColumn != roomColumns - 1)
                                continue;
                        }
                        
                        //determine the current index
                        final int currentCol = (column * roomColumns) + roomColumn;
                        final int currentRow = (row * roomRows) + roomRow;
                        
                        if (roomColumn == 0 || roomColumn == roomColumns - 1 || roomRow == 0 || roomRow == roomRows - 1)
                        {
                            blocks[currentRow][currentCol] = new SolidBlock(Key.BrickNaziFlag, Key.Blue1, Key.Cement1, Key.DoorMessage);
                        }
                        else
                        {
                            blocks[currentRow][currentCol] = new Block();
                        }
                    }
                }
            }
        }
        
        //default empty block for all
        for (int row = 0; row < blocks.length; row++)
        {
            for (int col = 0; col < blocks[0].length; col++)
            {
                if (blocks[row][col] == null)
                    blocks[row][col] = new Block();
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