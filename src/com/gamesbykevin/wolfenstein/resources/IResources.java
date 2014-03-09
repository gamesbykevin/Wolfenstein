package com.gamesbykevin.wolfenstein.resources;

import com.gamesbykevin.framework.resources.Disposable;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface IResources extends Disposable
{
    /**
     * Here we will handle loading the resources
     * @param source Class in root directory of project.
     * 
     * @throws Exception 
     */
    public void update(final Class source) throws Exception;
    
    /**
     * This method will determine if all resources have been loaded into memory
     * @return true if we are still loading resources
     */
    public boolean isLoading();
    
    /**
     * 
     * @param graphics Graphics object to write to
     * @param screen The container for rendering the progress bar
     */
    public void render(final Graphics graphics, final Rectangle screen);
}