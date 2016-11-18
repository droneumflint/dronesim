package simpledrone;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;

import sim.portrayal.continuous.ContinuousPortrayal2D;


public class Environment extends SimState{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2596652665511752118L;
	public Continuous2D yard = new Continuous2D(1.0,100,100);
	public int numDrones = 10;
	public ContinuousPortrayal2D yardPortrayal;
	public double gridHeight= 100;
	public Double2D baseLocation = new Double2D(50,5);
	public Double2D targetLocation = new Double2D(75,75);

	public Environment(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args){
	doLoop(Environment.class, args);
	System.exit(0);
	}
	
	public void start(){
	super.start();
	// clear the yard
	yard.clear();
	placeAgents();
	// clear the buddies
	// add some students to the yard
	
	}
	
	private void placeAgents(){
		for(int i = 0; i < numDrones; i++){
			Drone drone = new Drone();
			yard.setObjectLocation(drone, new Double2D(45+(i*3),10));
			yardPortrayal.setPortrayalForObject(drone, drone.myPortrayal2D);
			schedule.scheduleRepeating(drone);
			}
		
		HomeBase base = new HomeBase();
		yard.setObjectLocation(base, baseLocation);
		yardPortrayal.setPortrayalForObject(base, base.myPortrayal2D);
		schedule.scheduleRepeating(base);
		
		Target target = new Target();
		yard.setObjectLocation(target, targetLocation);
		yardPortrayal.setPortrayalForObject(target, target.myPortrayal2D);
		schedule.scheduleRepeating(target);
		
	}

}
