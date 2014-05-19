package com.gamesbykevin.wolfenstein.level.objects;

import com.gamesbykevin.framework.resources.Disposable;

public class BonusItem extends LevelObject implements Disposable
{
    public enum Type
    {
        Key1(0,0), Key2(1,0), SmallFood(2,0), HealthKit(3,0),
        AmmoClip(4,0), AssaultGun(5,0), MachineGun(6,0), 
        Treasure1(7,0), Treasure2(8,0), Treasure3(9,0), Treasure4(10,0),
        ExtraLife(11,0);
        
        private int column, row;
        
        private Type(final int column, final int row)
        {
            this.column = column;
            this.row = row;
        }
    }
    
    //the object type
    private final Type type;
    
    protected BonusItem(final Type type)
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
    
    /**
     * Is this bonus item a treasure
     * @return true if a treasure, false otherwise
     */
    protected boolean isTreaure()
    {
        switch(getType())
        {
            case Treasure1:
            case Treasure2:
            case Treasure3:
            case Treasure4:
                return true;
                
            default:
                return false;
        }
    }
    
    public Type getType()
    {
        return this.type;
    }
}