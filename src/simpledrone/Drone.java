package simpledrone;

import java.awt.Color;

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
	final double droneSpeed = 5.0; 
	boolean longRangeCom=true;
	boolean shortRangeCom=true;
	private double xdir= Math.random()*Math.sqrt(25/5);//Math.sqrt(25/5)
	private double ydir= Math.random()*Math.sqrt(25/5);
	private double longRange = 34;
	int droneState=0;
	private int droneId;
	public Drone (int droneNumber){
		this.droneId = droneNumber;
		
	}
	

	@Override
	public void step(SimState state) {
		environment = (Environment) state;
		yard = environment.yard;
		myPosition = environment.yard.getObjectLocation(this);
		//decideAction(state);
		//checkState(state);
		moveToPoint(new Double2D((droneId),droneId));
		//moveWithBoundaries(state);
		
	}
	private void checkState(SimState state) {
		if(droneState == 0){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.GREEN));
			//Nomral State
			moveWithBoundaries(state);
			/*if((batteryPercent>(myPosition.distance(environment.baseLocation)/droneSpeed))){
				droneState=1;
			}*/
			if(isLongRange()==true){
				droneState=2;
				}
			if (isAtTarget()==true){
				droneState=3;
			}
		}
		else if(droneState==1){
			//needs charging
			//moveToBase();
			if(myPosition==environment.baseLocation){
				batteryPercent= batteryPercent+(100-batteryPercent);
				droneState=0;
			}else{batteryPercent=batteryPercent-1;}		
		}
		else if (droneState==2){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.RED));
			moveWithBoundaries(state);
			if(isLongRange()==false){
				droneState=0;
				
				}
			if (isAtTarget()==true){
				droneState=3;
			}
			
			//out of range
		}else if (droneState==3){
			environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.YELLOW));
			
		}
		// TODO Auto-generated method stub
		
	}
	/*private void decideAction(SimState state) {
		if((batteryPercent>(myPosition.distance(environment.baseLocation)/droneSpeed))){	
			moveToBase();
		}
		else{
			moveWithBoundaries(state);
			if(isLongRange()==true){
				environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.RED));
			}else{
				environment.yardPortrayal.setPortrayalForObject(this, new OvalPortrayal2D(Color.GREEN));
				}			
		}
		if(myPosition==environment.baseLocation){
			batteryPercent= batteryPercent+(100-batteryPercent);
		}else{batteryPercent=batteryPercent-1;}
		
			
	}*/

	
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
		double x=myPosition.x,y=myPosition.y;
		if (point.x > x){
			xdir= Math.sqrt(25/5);	
		}else if(point.x < x){
			xdir= -Math.sqrt(25/5);
		}else{
			xdir=0;
		}
		if (point.y > y){
			ydir= Math.sqrt(25/5);	
		}else if(point.y < y){
			ydir= -Math.sqrt(25/5);
		}else{
			ydir=0;
		}
		//ydir= (environment.baseLocation.y - y)/(environment.baseLocation.x - x);
		x = x + xdir; //move one step
		y = y + ydir;
		yard.setObjectLocation(this, new Double2D(x,y));//move one step
		
	};
	//Current Events
	public boolean isAtTarget(){
		return myPosition.distance(environment.targetLocation)<5;	
	}
	private boolean isLongRange() {
		return myPosition.distance(environment.baseLocation)>longRange;
	}
	
	
}
