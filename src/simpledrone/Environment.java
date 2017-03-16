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
	public Double2D baseLocation ;
	public Double2D targetLocation;
	public Bag allDrones;
	public boolean isBoundaries = true;
	private double baseLocationX=50;
	private double baseLocationY=5;
	private double targetLocationX =50;
	private double targetLocationY= 50;
	public double[][] dronesInfo;
	private double cellRange = 50;
	

	public Environment(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args){
	doLoop(Environment.class, args);
	System.exit(0);
	}
	
	public boolean getBoundaries() {
		return isBoundaries;
	}

	public void setBoundaries(boolean boundaries) {
		this.isBoundaries = boundaries;
	}
	public double getBaseLocationX() {
		return baseLocationX;
	}

	public void setBaseLocationX(double x) {
		this.baseLocationX= x;
	}
	public double getBaseLocationY() {
		return baseLocationY;
	}
	public void setCellRange(double cellrange) {
		this.cellRange= cellrange;
	}
	public double getCellRange(double cellrange) {
		return cellRange;
	}

	public void setBaseLocationY(double y) {
		this.baseLocationY= y;
	}
	public double getTargetLocationX() {
		return targetLocationX;
	}

	public void setTargetLocationX(double x) {
		this.targetLocationX= x;
	}
	public double getTargetLocationY() {
		return targetLocationY;
	}

	public void setTargetLocationY(double y) {
		this.targetLocationY= y;
	}
	public void setNumDrones(int numDrones) {
		this.numDrones= numDrones;
	}
	public int getNumDrones() {
		return numDrones;
	}
	
	
	
	public void start(){
	super.start();
	// clear the yard
	yard.clear();
	dronesInfo = new double[numDrones][5];
	placeAgents();

	
	}
	
	private void placeAgents(){
		baseLocation = new Double2D(baseLocationX,baseLocationY);
		targetLocation = new Double2D(targetLocationX,targetLocationY);
		//allDrones.resize(numDrones);
		
		
		HomeBase base = new HomeBase();
		yard.setObjectLocation(base, baseLocation);
		yardPortrayal.setPortrayalForObject(base, base.myPortrayal2D);
		schedule.scheduleRepeating(base);
		
		Target target = new Target();
		yard.setObjectLocation(target, targetLocation);
		yardPortrayal.setPortrayalForObject(target, target.myPortrayal2D);
		schedule.scheduleRepeating(target);
		
		for(int i = 0; i < numDrones; i++){
			dronesInfo[i][0]=i;
			dronesInfo[i][4]=0;
			Drone drone = new Drone(i);
			drone.setBoundaries(isBoundaries);
			yard.setObjectLocation(drone, baseLocation);
			yardPortrayal.setPortrayalForObject(drone, drone.myPortrayal2D);
			//allDrones.add(drone);
			schedule.scheduleRepeating(drone);
			}
		
	}
	

}
