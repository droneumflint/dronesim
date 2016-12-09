package simpledrone;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.portrayal.simple.OvalPortrayal2D;
import java.awt.Color;


import DroneEvents.*;
import DroneStates.DroneFSM;

@SuppressWarnings("serial")
public class Drone implements Steppable{
	Double2D myPosition; //Drone Position at Start of Step
	Environment environment;// Environment at Start of Step
	Continuous2D yard; //Yard at Start of Step
	public OvalPortrayal2D myPortrayal2D = new OvalPortrayal2D(Color.GREEN); //Default Portrayal
	double batteryPercent = 100; 
	double batteryDrainRate = .05;//Battery drain for each distance traveled
	final double droneSpeed = 5; //Distance drone can move in one step 
	private double cellRange = 50;//Distance drone can be away from Base and communicate
	private double wifiRange = 10;//Distance drone can be used as relay to communicate to Base
	public int droneId;// Declared when initialized in Environment
	Double2D[] points = new Double2D[8];
	int help=0;
	boolean isBoundaries;
	EventListener localEvents = new EventListener();
	DroneFSM droneFSM = new DroneFSM(localEvents);
	
	public Drone (int droneNumber){
		this.droneId = droneNumber;
		recievePlan();
		for (int i=0;i<8; i++){
			if((i%2)==0){
				points[i]= new Double2D((droneId*5)+i*10,0);
			}else{
				points[i]= new Double2D((droneId*5)+i*10,80);
			}			
		}
	};
	
	//Called each Step in simulator
	public void step(SimState state) {
		environment = (Environment) state;
		yard = environment.yard;
		myPosition = environment.yard.getObjectLocation(this);
		updateEvents();
		droneActions();
	
		
	}
	
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
	public void moveInGrid(){
		int error = moveToPoint(points[help],isBoundaries);
		if ((atPoint(points[help],1.0)  || error==1)&& help<7){
			help++;
		};
	}

	//Tries to move to point in yard, doesn't if point is out of bounds and isBoundaryOn is true
	public int moveToPoint(Double2D point, boolean isBoundaryOn){
		Double2D temp =myPosition.subtract(point);
		int error = 0;
		if(isBoundaryOn){
			if (Math.abs(temp.length())>1){
				if(temp.length()>droneSpeed){
					temp=temp.resize(-droneSpeed);
				}
				temp=temp.add(myPosition);
				
				if(inBounds(temp)){
					batteryPercent -=(batteryDrainRate*temp.length());
					yard.setObjectLocation(this, temp);
					
				}else{error++;}			
			}
		}
		else{
			if (Math.abs(temp.length())>1){
				if(temp.length()>droneSpeed){
					temp=temp.resize(-droneSpeed);
				}
				temp=temp.add(myPosition);
				batteryPercent -=(batteryDrainRate*temp.length()); 
				yard.setObjectLocation(this, temp);	
			}
		}
		return error;
		
	};
	public void rechargeBattery(int chargeAdded){
		batteryPercent += chargeAdded;
		
	};
	public void rechargeBattery(){
		batteryPercent =100;
		
	};
	
	//Current Events
	public boolean atPoint(Double2D point, double tolerance){
		boolean val = false ;
		if ((myPosition.distance(point)<tolerance)){
			val = true;
		}
		return val;	
	};
	public boolean atBase(){
		boolean val = false ;
		if ((myPosition.distance(environment.baseLocation)<2)){
			val = true;
			new ReturnComplete(localEvents);
		}
		return val;	
	};
	public boolean atTarget(){
		boolean val = false ;
		if ((myPosition.distance(environment.targetLocation)<6)){
			val = true;
			new PossibleTarget(localEvents);
		}
		return val;	
	};
	public boolean inCellRange() {
		return myPosition.distance(environment.baseLocation)<cellRange;
	};	
	private boolean inCellRange(Double2D point) {
		return point.distance(environment.baseLocation)<cellRange;
	};	
	public boolean inBatteryRange() {
		boolean val = true;
		if (!(myPosition.distance(environment.baseLocation)<((batteryPercent-batteryDrainRate)*droneSpeed))){
			val = false;
			new PONR(localEvents);
		}
		return val;
	};	
	/*private boolean inBatteryRange(Double2D point) {
		return point.distance(environment.baseLocation)<((batteryPercent)*droneSpeed);
	};*/	
	public boolean hasAssist() {
		Bag inWifiRange = yard.getNeighborsWithinDistance(myPosition, wifiRange);
		for(int i = 0 ; i < inWifiRange.size(); i++){	
			if(!(inWifiRange.get(i) instanceof Drone)){
				inWifiRange.remove(i);
			}
		}
		//return (inWifiRange.numObjs>1);
		return false;
	};
	private boolean hasAssist(Double2D point) {
		Bag inWifiRange = yard.getNeighborsWithinDistance(point, wifiRange);
		for(int i = 0 ; i < inWifiRange.size(); i++){	
			if(!(inWifiRange.get(i) instanceof Drone)){
				inWifiRange.remove(i);
			}
		}
		//return (inWifiRange.numObjs>1);]
		return false;
	};
	private boolean inBounds(Double2D point){
		return ((hasAssist(point)||inCellRange(point)));
	};
	private boolean inBounds(){
		return ((hasAssist()||inCellRange()));
	};

	public void setBoundaries(boolean boundaries) {
		this.isBoundaries = boundaries;
	}
	
	private void isBatteryFull() {
		if (getBattery() == 100.0){
			new BatteryFull(localEvents);
		}
	}
	//Creates Plan Sent Event 
	private void recievePlan() {
		new PlanSent(localEvents);
	}
	//Sets the Drone Batttery to full
	
	public double getBattery(){
		return batteryPercent;
	}
	
	
	private void updateEvents() {
		inBatteryRange();
		isBatteryFull();
		atBase();
		atTarget();
		droneFSM.update();
		
	}
	
	//For Each Drone State, says the actions should perform
	private void droneActions(){
		if(droneFSM.getCurrentState() == droneFSM.getSearching()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.green));
			moveInGrid();
			batteryPercent = batteryPercent- .5;
		}else if(droneFSM.getCurrentState() ==droneFSM.getReturning()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.red));
			moveToPoint(environment.baseLocation);
		}else if(droneFSM.getCurrentState() == droneFSM.getAssisting()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.yellow));
			
		}else if(droneFSM.getCurrentState() == droneFSM.getTracking()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.blue));		
		}else if(droneFSM.getCurrentState() == droneFSM.getAtBase()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.pink));
			rechargeBattery();
		}else{
			//System.out.println("Dont Know State");
		}
		//batteryPercent = batteryPercent- .00005;
	}
}


