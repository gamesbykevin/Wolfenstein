package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.resources.Disposable;

public abstract class BlockManager implements Disposable
{
    //all blocks in the level
    private Block[][] blocks;
    
    //the dimensions of each room
    private final int roomColumns, roomRows;
    
    protected BlockManager(final int columns, final int rows, final int roomColumns, final int roomRows)
    {
        //how big is each room
        this.roomColumns = roomColumns;
        this.roomRows    = roomRows;
        
        //create all of the blocks for all of the levels
        this.blocks  = new Block[(roomRows * rows) + (rows + 1)][(roomColumns * columns) + (columns + 1)];
    }
    
    @Override
    public void dispose()
    {
        Block block;
        
        for (int row = 0; row < getRows(); row++)
        {
            for (int col = 0; col < getCols(); col++)
            {
                block = get(col, row);
                block.dispose();
                block = null;
            }
        }
        
        blocks = null;
    }
    
    /**
     * Create empty blocks for the remaining objects in the array
     */
    protected void fill()
    {
        for (int row=0; row < blocks.length; row++)
        {
            for (int col=0; col < blocks[0].length; col++)
            {
                //if any remaining blocks are null then they are empty blocks
                if (get(col, row) == null)
                    set(col, row, new Block());
            }
        }
    }
    
    /**
     * How many blocks are in each room
     * @return The total number of block columns per room
     */
    public int getRoomColumnTotal()
    {
        return this.roomColumns;
    }
    
    /**
     * How many blocks are in each room
     * @return The total number of block rows per room
     */
    public int getRoomRowTotal()
    {
        return this.roomRows;
    }
    
    public Block get(final double column, final double row)
    {
        return get((int)column, (int)row);
    }
    
    public Block get(final int column, final int row)
    {
        //if the index is out of bounds return a default solid block
        if (column < 0 || column >= blocks[0].length || row < 0 || row >= blocks.length)
            return Block.solidBlock;
        
        return blocks[row][column];
    }
    
    public void set(final int column, final int row, final Block block)
    {
        blocks[row][column] = block;
    }
    
    
    /**
     * Get the total number of columns for the entire maze
     * @return Total number of columns in complete maze
     */
    public int getCols()
    {
        return this.blocks[0].length;
    }
    
    /**
     * Get the total number of rows for the entire maze
     * @return Total number of rows in complete maze
     */
    public int getRows()
    {
        return this.blocks.length;
    }
}