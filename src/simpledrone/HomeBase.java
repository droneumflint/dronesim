package simpledrone;

import java.awt.Color;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.portrayal.simple.HexagonalPortrayal2D;

@SuppressWarnings("serial")
public class HomeBase implements Steppable{
	Double2D myPosition;
	Environment environment;
	Continuous2D yard;
	public HexagonalPortrayal2D myPortrayal2D = new HexagonalPortrayal2D(Color.BLUE,3);

	@Override
	public void step(SimState state) {
		environment = (Environment) state;
		yard = environment.yard;
		myPosition = environment.yard.getObjectLocation(this);
		
	}

}
