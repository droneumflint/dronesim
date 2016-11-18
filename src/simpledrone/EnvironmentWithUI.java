package simpledrone;

import sim.portrayal.continuous.*;
import sim.engine.*;
import sim.display.*;

import javax.swing.*;
import java.awt.Color;

public class EnvironmentWithUI extends GUIState{
	public Display2D display;
	public JFrame displayFrame;
	static Environment environment;
	ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
	
	public static void main(String[] args)
	{
	EnvironmentWithUI vid = new EnvironmentWithUI();
	Console c = new Console(vid);
	c.setVisible(true);}

	public EnvironmentWithUI(SimState state) {super(state);}
	public EnvironmentWithUI(){
		super(environment = new Environment(System.currentTimeMillis()));
		environment.yardPortrayal = yardPortrayal;
		
		}
	public static String getName(){return "Drones Searching Grounds";}
	
	
	
	
	
	public void start()
	{
	super.start();
	setupPortrayals();
	}
	public void load(SimState state)
	{
	super.load(state);
	setupPortrayals();
	}
	public void setupPortrayals()
	{
	Environment environment = (Environment) state;
	// tell the portrayals what to portray and how to portray them
	yardPortrayal.setField( environment.yard );
	//yardPortrayal.setPortrayalForAll(new OvalPortrayal2D(Color.GREEN));
	// reschedule the displayer
	display.reset();
	display.setBackdrop(Color.white);
	// redraw the display
	display.repaint();
	}
	public void init(Controller c)
	{
	super.init(c);
	display = new Display2D(600,600,this);
	display.setClipping(false);
	displayFrame = display.createFrame();
	displayFrame.setTitle("SearchArea Display");
	c.registerFrame(displayFrame);        // so the frame appears in the "Display" list
	displayFrame.setVisible(true);
	display.attach( yardPortrayal, "Yard" );
	}
	public void quit()
	{
	super.quit();
	if (displayFrame!=null) displayFrame.dispose();
	displayFrame = null;
	display = null;
	}
	

}
