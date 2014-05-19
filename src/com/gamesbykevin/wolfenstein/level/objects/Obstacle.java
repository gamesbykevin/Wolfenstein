package com.gamesbykevin.wolfenstein.level.objects;

import com.gamesbykevin.framework.resources.Disposable;

public class Obstacle extends LevelObject implements Disposable
{
    public enum Type
    {
        WaterPuddle(0,0,true,false), 
        GreenBarrel(1,0,true,true), 
        Sink(2,0,true,false), 
        SmallBlueVase(3,0,true,true), 
        LargeBlueVase(4,0,true,true), 
        WoodenTable(0,1,true,false), 
        Ceilinglight(1,1,false,false), 
        HangingPots(2,1,false,false), 
        Knight(3,1,true,true), 
        EmptyCage(4,1,true,false), 
        FilledCage(0,2,true,true), 
        Bones(1,2, true,false), 
        DiningTable(2,2, true,false), 
        Bed(3,2,true,false), 
        DirtyBowl(4,2,true,false),
        StandingLamp(0,3,true,true), 
        BloodyBones(1,3,true,false), 
        WoodenBarrel(2,3,true,true), 
        FullWell(3,3,true,false), 
        EmptyWell(4,3,true,false),
        BloodOnFloor(0,4,true,false), 
        FlagPole(1,4,true,true), 
        Chandelier(2,4,false,false), 
        Debris1(3,4,true,false), 
        Debris2(4,4,true,false), 
        Debris3(0,5,true,false), 
        Debris4(1,5,true,false), 
        HangingPots2(2,5,false,false), 
        Furnace(3,5,true,true), 
        PoleRack(4,5,true,true), 
        HangingSkeleton(0,6,true,false), 
        DogFood(1,6,true,false), 
        Pillar(2,6,true,true), 
        VaseTree(3,6,true,true), 
        Bones2(4,6,true,false);
        
        //the location on the sprite sheet where image is
        private int column, row;
        
        //is this an obstacle where we would check for collision
        private boolean solid = false;
        
        //is this item for display purposes
        private boolean display = false;
        
        private Type(final int column, final int row, final boolean solid, final boolean display)
        {
            this.column = column;
            this.row = row;
            this.solid = solid;
            this.display = display;
        }
        
        private int getColumn()
        {
            return this.column;
        }
        
        private int getRow()
        {
            return this.row;
        }
        
        public boolean isSolid()
        {
            return solid;
        }
        
        public boolean isDisplay()
        {
            return display;
        }
    }
    
    //the object type
    private final Type type;
    
    protected Obstacle(final Type type)
    {
        super();
        
        //add animation
        super.addAnimation(type, 1, type.column, type.row, 0, false);
        
        //set the type
        this.type = type;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    public Type getType()
    {
        return this.type;
    }
}