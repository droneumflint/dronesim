package simpledrone;

import java.awt.Color;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.Double2D;

@SuppressWarnings("serial")
public class Target implements Steppable{
	Double2D myPosition;
	Environment environment;
	Continuous2D yard;
	public RectanglePortrayal2D myPortrayal2D = new RectanglePortrayal2D(Color.BLACK,2);

	@Override
	public void step(SimState state) {
		environment = (Environment) state;
		yard = environment.yard;
		myPosition = environment.yard.getObjectLocation(this);
		
	}

}
