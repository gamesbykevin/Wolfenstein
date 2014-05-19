package com.gamesbykevin.wolfenstein.level;

import com.gamesbykevin.framework.resources.Disposable;

public abstract class BlockManager implements Disposable
{
    //all blocks in the level
    private Block[][] blocks;
    
    //is a door closing in this room, used to play sound effect
    private boolean closing = false;
    
    protected BlockManager(final int columnTotal, final int rowTotal)
    {
        //create an array of these blocks
        this.blocks  = new Block[rowTotal][columnTotal];
    }
    
    @Override
    public void dispose()
    {
        Block block;
                
        for (int row=0; row < blocks.length; row++)
        {
            for (int col=0; col < blocks[0].length; col++)
            {
                block = get(col, row);
                
                if (block != null)
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
    
    protected int getRowCount()
    {
        return this.blocks.length;
    }
    
    protected int getColumnCount()
    {
        return this.blocks[0].length;
    }
    
    protected Block get(final double column, final double row)
    {
        return get((int)column, (int)row);
    }
    
    protected Block get(final int column, final int row)
    {
        //if the index is out of bounds return a default solid block
        if (column < 0 || column >= getColumnCount() || row < 0 || row >= getRowCount())
            return Block.solidBlock;
        
        return blocks[row][column];
    }
    
    protected Block[][] getBlocks()
    {
        return blocks;
    }
    
    protected void set(final int column, final int row, final Block block)
    {
        blocks[row][column] = block;
    }
    
    /**
     * Here we will manage the door animations
     * @param time Time duration per update to deduct from timer (nano-seconds)
     * @param playerX Current player's location 
     * @param playerZ Current player's location
     */
    public void update(final long time, final double playerX, final double playerZ)
    {
        //default to false
        this.closing = false;
        
        final int distance = 2;
        
        for (int row = 0; row < blocks.length; row++)
        {
            for (int col = 0; col < blocks[0].length; col++)
            {
                //get current block
                final Block b = get(col, row);
                
                //we are only interested in the door(s)
                if (!b.isDoor())
                    continue;
                
                //check if door is open
                final boolean isOpen = b.getDoor().isOpen();
                
                //if the door is open don't update if the player is to close to it
                if (isOpen)
                {
                    //if the player is close enough to a block, then skip it
                    if (col >= playerX - distance && col <= playerX + distance &&
                        row >= playerZ - distance && row <= playerZ + distance)
                        continue;
                }
                
                //update door status
                b.getDoor().update(time);
                
                //if the current door goes from open to closed
                if (!b.getDoor().isOpen() && isOpen)
                    closing = true;
            }
        }
    }
    
    /**
     * Is there a block that is just closing?
     * @return true if a door just started to close, false otherwise
     */
    public boolean hasClosingDoor()
    {
        return this.closing;
    }
}