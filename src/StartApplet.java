import javax.swing.JApplet;

import com.gamesbykevin.wolfenstein.main.Main;
import com.gamesbykevin.wolfenstein.shared.Shared;

/**
 * WE NEED THIS CLASS HERE FOR JAVA APPLETS OUTSIDE A PACKAGE SO IT CAN FIND THE STARTING POINT
 * @author GOD
 */
public final class StartApplet extends JApplet
{
    private Main main;
    
    @Override
    public void init()
    {
        //the size of our game window
        setSize(Shared.INITIAL_WIDTH, Shared.INITIAL_HEIGHT);

        //allow focus on our applet
        setFocusable(true);

        //request the focus so input will be detected withoug having to click the applet window
        requestFocusInWindow();
        
        //use cursor from Shared class
        setCursor(Shared.CURSOR);
        
        //set default values in case no parameters have been passed
        int ups = Shared.DEFAULT_UPS;
        
        try
        {
            //get parameters here
            ups = Integer.parseInt(getParameter("ups"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        //create new instance of Main with specified ups/fps
        main = new Main(ups, this);
    }
    
    @Override
    public void start()
    {
        try
        {
            //create a new instance of our engine
            main.create();
            
            //start the thread
            main.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop()
    {
        
    }
    
    @Override
    public void destroy()
    {
        if (main != null)
        {
            main.dispose();
            main = null;
        }
    }
}