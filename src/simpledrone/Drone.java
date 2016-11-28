package simpledrone;

import java.awt.Color;
import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.portrayal.simple.OvalPortrayal2D;


@SuppressWarnings("serial")
public class Drone implements Steppable{
	Double2D myPosition; //Drone Position at Start of Step
	Environment environment;// Environment at Start of Step
	Continuous2D yard; //Yard at Start of Step
	public OvalPortrayal2D myPortrayal2D = new OvalPortrayal2D(Color.GREEN); //Default Portrayal
	double batteryPercent = 75; 
	double batteryDrainRate = 1;//Battery drain for distance traveled
	final double droneSpeed = .5; //Distance drone can move in one step 
	private double cellRange = 25;//Distance drone can be away from Base and communicate
	private double wifiRange = 10;//Distance drone can be used as relay to communicate to Base
	public int droneId;// Declared when initialized in Environment
	
	public Drone (int droneNumber){
		this.droneId = droneNumber;
	};
	
	//Called each Step in simulator
	public void step(SimState state) {
		environment = (Environment) state;
		yard = environment.yard;
		myPosition = environment.yard.getObjectLocation(this);
		moveToPoint(new Double2D((droneId*34),droneId*34), true);
		//if (hasAssist()){
		//	environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.GREEN));
		//}else{			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.RED));}
		//environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(new Color(0,batteryPercent+155,0 )));
		
	};
	
	//Moves to a point in the yard, going directly there at Max speed		
	public void moveToPoint(Double2D point){
		Double2D temp =myPosition.subtract(point);
		if (Math.abs(temp.length())>1){
			if(temp.length()>droneSpeed){
				temp=temp.resize(-droneSpeed);
			}
			temp=temp.add(myPosition);
			batteryPercent -=batteryDrainRate; 
			yard.setObjectLocation(this, temp);		
		}
	};

	//Tries to move to point in yard, doesn't if point is out of bounds and isBoundaryOn is true
	public void moveToPoint(Double2D point, boolean isBoundaryOn){
		Double2D temp =myPosition.subtract(point);
		if(isBoundaryOn){
			if (Math.abs(temp.length())>1){
				if(temp.length()>droneSpeed){
					temp=temp.resize(-droneSpeed);
				}
				temp=temp.add(myPosition);
				
				if(inBounds(temp)){
					batteryPercent -=(temp.length()*batteryDrainRate);
					yard.setObjectLocation(this, temp);
				}			
			}
		}else{
			if (Math.abs(temp.length())>1){
				if(temp.length()>droneSpeed){
					temp=temp.resize(-droneSpeed);
				}
				temp=temp.add(myPosition);
				batteryPercent = batteryPercent-batteryDrainRate; 
				yard.setObjectLocation(this, temp);		
			}
		}
		
	};
	
	//Current Events
	public boolean atPoint(Double2D point, double tolerance){
		return myPosition.distance(point)<tolerance;	
	};
	public boolean inCellRange() {
		return myPosition.distance(environment.baseLocation)<cellRange;
	};	
	private boolean inCellRange(Double2D point) {
		return point.distance(environment.baseLocation)<cellRange;
	};	
	public boolean inBatteryRange() {
		return myPosition.distance(environment.baseLocation)<((batteryPercent-batteryDrainRate)*droneSpeed);
	};	
	private boolean inBatteryRange(Double2D point) {
		return point.distance(environment.baseLocation)<((batteryPercent-batteryDrainRate)*droneSpeed);
	};	
	public boolean hasAssist() {
		Bag inWifiRange = yard.getNeighborsWithinDistance(myPosition, wifiRange);
		for(int i = 0 ; i < inWifiRange.size(); i++){	
			if(!(inWifiRange.get(i) instanceof Drone)){
				inWifiRange.remove(i);
			}
		}
		return (inWifiRange.numObjs>1);
	};
	private boolean hasAssist(Double2D point) {
		Bag inWifiRange = yard.getNeighborsWithinDistance(point, wifiRange);
		for(int i = 0 ; i < inWifiRange.size(); i++){	
			if(!(inWifiRange.get(i) instanceof Drone)){
				inWifiRange.remove(i);
			}
		}
		return (inWifiRange.numObjs>1);
	};
	private boolean inBounds(Double2D point){
		return ((hasAssist(point)||inCellRange(point))&& inBatteryRange(point));
	};

}





/*
public double bxdir(double x){
	//convert the SimState to our Environment
	if(x<0 || x>= environment.gridHeight) {
		xdir = -xdir; //reverse direction
		return x+xdir ; //revert to previous
	}
	else {
		return x;
	}
};
public double bydir(double y){
	//convert the SimState to our Environment
	if(y<0 || y>= environment.gridHeight) {
		ydir = -ydir; //reverse direction
		return y + ydir; // revert to previous
	}
	else {
		return y;
	}
};
public void moveWithBoundaries(SimState state){
	double x=myPosition.x,y=myPosition.y;
	x = x + xdir; //move one step
	y = y + ydir; //move one step
	x = bxdir(x); //correct x location for boundaries
	y = bydir(y);//correct y location for boundaries
	yard.setObjectLocation(this, new Double2D(x,y));
	//sets the particle in the new location.
};*/


