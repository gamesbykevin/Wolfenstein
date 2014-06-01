package com.gamesbykevin.wolfenstein.enemies;

import com.gamesbykevin.wolfenstein.resources.GameAudio;
import com.gamesbykevin.wolfenstein.resources.GameImages;

public final class Dog extends Enemy
{
    /**
     * Create new enemy and setup animations
     */
    protected Dog()
    {
        super(GameImages.Keys.Dog);
        
        //set damage
        super.setDamage(3);
        
        //set audio keys for this enemy
        super.setAudioKeyAlert(GameAudio.Keys.DogAlert);
        super.setAudioKeyAttack(GameAudio.Keys.DogAttack);
        super.setAudioKeyDeath(GameAudio.Keys.DogDeath);
        
        //add idle animation
        super.addAnimation(State.Idle, 1, 0, 0, DEFAULT_DELAY, false);
        
        //add walking animation
        super.addAnimation(State.Walking, 4, 0, 0, DEFAULT_DELAY, true);
        
        //add attacking animation
        super.addAnimation(State.Attacking, 3, 0, 1, DEFAULT_DELAY, false);
        
        //add death animation
        super.addAnimation(State.Death, 4, 0, 2, DEFAULT_DELAY, false);
        
        //default animation
        super.setAnimation(State.Idle);
    }
}