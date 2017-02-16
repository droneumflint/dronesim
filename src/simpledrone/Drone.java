package simpledrone;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.portrayal.simple.OvalPortrayal2D;

import java.awt.Color;
import java.util.List;

import DroneEvents.*;
import DroneStates.DroneFSM;

@SuppressWarnings("serial")
public class Drone implements Steppable{
	Double2D myPosition; 					//Drone Position at Start of Step
	Environment environment;				// Environment at Start of Step
	Continuous2D yard; 						//Yard at Start of Step
	public OvalPortrayal2D myPortrayal2D = new OvalPortrayal2D(Color.GREEN); //Default Portrayal
	double batteryPercent = 100; 			//Current Battery percent
	double batteryDrainRate = .05;			//Battery drain for each distance traveled
	final double droneSpeed = 5; 			//Distance drone can move in one step 
	private double cellRange = 50;			//Distance drone can be away from Base and communicate
	private double wifiRange = 1;			//Distance drone can be used as relay to communicate to Base
	public int droneId;						//Declared when initialized in Environment
	Double2D[] points = new Double2D[8];	//Array to hold points of search
	int searchPoint=0;						//Number of completed search points
	boolean isBoundaries;					//Sees if boundaries are turned on in simulator
	EventListener localEvents = new EventListener(); //Events that this drones makes
	DroneFSM droneFSM = new DroneFSM(localEvents);   //state machine that listens to events made by drone.
	
	public Drone (int droneNumber){
		this.droneId = droneNumber; 		//Assigns what drone this is
		recievePlan();						//Creates event that drone has received plans 
		for (int i=0;i<8; i++){             //populates points[] based off drone id
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
			
	public void moveInGrid(){
		int error = moveToPoint(points[searchPoint]);
		if ((atPoint(points[searchPoint],1.0))&& searchPoint<7){
			searchPoint++;
		}else if(!(error==0)){
			reqAssist();
		}
	}

	private void reqAssist() {
		int bestDrone=droneId;
		double bestUtility = 100;
		for(int i = 0; i < environment.numDrones; i++){
			if((!(i==droneId))&&(environment.dronesInfo[droneId][5]==0)){
				double utility=this.myPosition.distance(environment.dronesInfo[i][1],environment.dronesInfo[i][2])*(1/environment.dronesInfo[i][3]);
				if (utility<bestUtility){
					bestDrone=i;
					bestUtility=utility;
					environment.dronesInfo[droneId][5]=1;
				}
			}
			
		}
		environment.dronesInfo[bestDrone][0]=droneId;	
		System.out.println("bestDrone = "+bestDrone);
	}
	public void isAssisting(){
		if(!(environment.dronesInfo[droneId][0]==droneId)){
			new RespondingToRequest(localEvents);
			
			
		}
		
	}

	//Tries to move to point in yard, doesn't if point is out of bounds and isBoundaryOn is true
	public int moveToPoint(Double2D point){
		Double2D temp =myPosition.subtract(point);
		int error = 0;
		if(isBoundaries){
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
		boolean temp=atPoint(environment.baseLocation,2);
		if (temp){
			new ReturnComplete(localEvents);
		}
		return temp;	
	};
	public boolean atTarget(){
		boolean temp=atPoint(environment.targetLocation,6);
		if (temp){
			new PossibleTarget(localEvents);
		}
		return temp;
	};
	public boolean inCellRange() {
		return inCellRange(myPosition);
	};	
	private boolean inCellRange(Double2D point) {
		return point.distance(environment.baseLocation)<cellRange;
	};	
	public boolean inBatteryRange() {
		boolean val = true;
		if ((myPosition.distance(environment.baseLocation)>((batteryPercent-batteryDrainRate)*droneSpeed))){
			val = false;
			new PONR(localEvents);
		}
		return val;
	};	
	public boolean inAssistRange() {
		return inAssistRange(myPosition);
	};

	
	private boolean inAssistRange(Double2D point) {
		/*Bag inWifiRange = yard.getNeighborsWithinDistance(point, wifiRange);
		for(int i = 0 ; i < inWifiRange.size(); i++){	
			if(!(inWifiRange.get(i) instanceof Drone)||(inWifiRange.get(i).equals(this))){
				inWifiRange.remove(i);
				inWifiRange.toArray();
			}
			
		}
		return (inWifiRange.numObjs>0);
		*/
        return false;
	};
	private boolean inBounds(Double2D point){
		return ((inAssistRange()||inCellRange(point)));
	};
	private boolean inBounds(){
		return inBounds(myPosition);
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
	
	public void printToGlobalArray(){
		environment.dronesInfo[droneId][1]=myPosition.getX();
		environment.dronesInfo[droneId][2]=myPosition.getY();
		environment.dronesInfo[droneId][3]=batteryPercent;
		
	}
	private void updateEvents() {
		inBatteryRange();
		isBatteryFull();
		atBase();
		atTarget();
		isAssisting();
		droneFSM.update();
		printToGlobalArray();
		
	}
	
	//For Each Drone State, says the actions should perform
	private void droneActions(){
		batteryPercent = batteryPercent- .5;
		if(droneFSM.getCurrentState() == droneFSM.getSearching()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.green));
			moveInGrid();
			
		}else if(droneFSM.getCurrentState() ==droneFSM.getReturning()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.red));
			moveToPoint(environment.baseLocation);
		}else if(droneFSM.getCurrentState() == droneFSM.getAssisting()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.yellow));
			moveToPoint(new Double2D(environment.dronesInfo[(int) (environment.dronesInfo[droneId][0])][1],environment.dronesInfo[(int) (environment.dronesInfo[droneId][0])][2]));
			
		}else if(droneFSM.getCurrentState() == droneFSM.getTracking()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.blue));		
		}else if(droneFSM.getCurrentState() == droneFSM.getAtBase()){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.pink));
			rechargeBattery();
		}else{
			System.out.println("Don't Know State");
		}
		
	}
}


