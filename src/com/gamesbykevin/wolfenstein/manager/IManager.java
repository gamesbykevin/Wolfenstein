package com.gamesbykevin.wolfenstein.manager;

import com.gamesbykevin.wolfenstein.engine.Engine;
import com.gamesbykevin.framework.resources.Disposable;
import java.awt.Graphics;

/**
 * Basic methods required for game elements
 * @author GOD
 */
public interface IManager extends Disposable
{
    /**
     * Update our game element accordingly
     * @param engine The Engine containing resources if needed
     * @throws Exception 
     */
    public void update(final Engine engine) throws Exception;
    
    /**
     * Draw our game element(s) accordingly
     */
    public void render(final Graphics graphics);
}