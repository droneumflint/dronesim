package simpledrone;

import java.awt.Color;
import java.util.List;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.portrayal.simple.OvalPortrayal2D;


@SuppressWarnings("serial")
public class Drone implements Steppable{
	Double2D myPosition;
	Environment environment;
	Continuous2D yard;
	public OvalPortrayal2D myPortrayal2D = new OvalPortrayal2D(Color.GREEN);
	double batteryPercent = 100.00;
	double batteryDrainRate = .5;
	final double droneSpeed = .50; 
	private double xdir= Math.random()*Math.sqrt(25/5);//Math.sqrt(25/5)
	private double ydir= Math.random()*Math.sqrt(25/5);
	private double cellRange = 50;
	private double wifiRange = 20;
	private int droneId;
	public Drone (int droneNumber){
		this.droneId = droneNumber;
		
	}
	

	@Override
	public void step(SimState state) {
		environment = (Environment) state;
		yard = environment.yard;
		myPosition = environment.yard.getObjectLocation(this);
		moveToPoint(new Double2D((droneId*34),droneId*34));
		if (hasAssist()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.GREEN));
		}else{			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.RED));}
	}
	
			


	
	public double bxdir(double x){
		//convert the SimState to our Environment
		if(x<0 || x>= environment.gridHeight) {
			xdir = -xdir; //reverse direction
			return x+xdir ; //revert to previous
		}
		else {
			return x;
		}
	}
	public double bydir(double y){
		//convert the SimState to our Environment
		if(y<0 || y>= environment.gridHeight) {
			ydir = -ydir; //reverse direction
			return y + ydir; // revert to previous
		}
		else {
			return y;
		}
	}
	public void moveWithBoundaries(SimState state){
		double x=myPosition.x,y=myPosition.y;
		x = x + xdir; //move one step
		y = y + ydir; //move one step
		x = bxdir(x); //correct x location for boundaries
		y = bydir(y);//correct y location for boundaries
		yard.setObjectLocation(this, new Double2D(x,y));
		//sets the particle in the new location.
	}
	public void moveToPoint(Double2D point){
		Double2D temp =myPosition.subtract(point);
		if (Math.abs(temp.length())>1){
			if(temp.length()>droneSpeed){
				temp=temp.resize(-droneSpeed);
			}
			temp=temp.add(myPosition);
			yard.setObjectLocation(this, temp);
			
		}
	}
	//Current Events
	public boolean atPoint(Double2D point, double tolerance){
		return myPosition.distance(point)<tolerance;	
	}
	private boolean inCellRange() {
		return myPosition.distance(environment.baseLocation)>cellRange;
	}
	private boolean hasAssist() {
		Bag dronesInWifiRange = yard.getNeighborsWithinDistance(myPosition, wifiRange);
		return (dronesInWifiRange.numObjs>1);
	}
	
	
}

