package particles;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.grid.*;
import java.awt.*;
import javax.swing.*;
import sim.portrayal.simple.OvalPortrayal2D;

public class AgentsWithUI extends GUIState {
	public Display2D display;
	public JFrame displayFrame;
	SparseGridPortrayal2D particlesPortrayal = 
			new SparseGridPortrayal2D();
	static MateChoiceEnvironment e;

	public static void main(String[] args) {
		AgentsWithUI ex = new AgentsWithUI();
		Console c = new Console(ex);
		c.setVisible(true);
		System.out.println("Start Simulation");
	}

	public AgentsWithUI() {
		super(e = new MateChoiceEnvironment(System.currentTimeMillis())); //a hack!
		e.particlesPortrayal = particlesPortrayal; //connect the particlesPortrayal to the Environment!
	}

	public void quit() {
		super.quit(); 

		if (displayFrame!=null) displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

	public void start() {
		super.start();
		setupPortrayals();
	}

	public void load(SimState state) {
		super.load(state);
		setupPortrayals();
	}

	public void setupPortrayals() {
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		particlesPortrayal.setField(e.particleSpace);
		//OvalPortrayal2D o = new OvalPortrayal2D(Color.red);
		//particlesPortrayal.setPortrayalForAll(o);
		display.reset();
		display.repaint();
	}

	public void init(Controller c){
		super.init(c);
		display = new Display2D(800,800,this);
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.setBackdrop(Color.white);
		display.attach(particlesPortrayal,"Particles");
	}

	public Object getSimulationInspectedObject() {
		return state;
	}
}
