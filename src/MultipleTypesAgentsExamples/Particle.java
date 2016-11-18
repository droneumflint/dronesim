package MultipleTypesAgentsExamples;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import sim.util.Int2D;

public class Particle implements Steppable {
	public int x; //x location in 2D space
	public int y; //y location in 2D space
	public int xdir; //x direction of movement in 2D space: -1,0,1
	public int ydir; //y direction of movement in 2D space: -1,0,1
	public boolean avoidCollisions;
	public int attemptsToAvoidCollisions;
	public double probabilityOfChange;
	public boolean boundaries = false;
	public boolean wander = false;
	public boolean unoccupied;
	public boolean aggregate;
	public int searchRadius;
	public boolean coordinate;
	public double aggregateProbability;
	public double coordinateProbability;



	public void setAggregateProbability(double aggregateProbability) {
		this.aggregateProbability = aggregateProbability;
	}

	public void setCoordinateProbability(double coordinateProbability) {
		this.coordinateProbability = coordinateProbability;
	}

	public void setCoordinate(boolean coordinate) {
		this.coordinate = coordinate;
	}

	public void setBoundaries(boolean boundaries) {
		this.boundaries = boundaries;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public void setSearchRadius(int searchRadius) {
		this.searchRadius = searchRadius;
	}

	public void setProbabilityOfChange(double probabilityOfChange) {
		this.probabilityOfChange = probabilityOfChange;
	}

	public void setLocation(int x, int y, int xdir, int ydir){
		this.x = x; //sets the x location determined in the Environment
		this.y = y; //sets the y location determined in the Environment
		this.xdir = xdir; //sets the direction of movement from the Environment
		this.ydir = ydir; //sets the direction of movement from the Environment
	}

	public void setCollision(boolean doCollisions){
		this.avoidCollisions = doCollisions;
	}

	public void setAttemptsToAvoidCollisions(int attemptsToAvoidCollisions) {
		this.attemptsToAvoidCollisions = attemptsToAvoidCollisions;
	}

	public int bxdir(SimState state, int x){
		MateChoiceEnvironment se = (MateChoiceEnvironment)state;
		//convert the SimState to our Environment
		if(x<0 || x>= se.gridWidth) {
			xdir = -xdir; //reverse direction
			return x+xdir; //revert to previous
		}
		else {
			return x;
		}
	}

	public int bydir(SimState state, int y){
		MateChoiceEnvironment se = (MateChoiceEnvironment)state;
		//convert the SimState to our Environment
		if(y<0 || y>= se.gridHeight) {
			ydir = -ydir; //reverse direction
			return y + ydir; // revert to previous
		}
		else {
			return y;
		}
	}

	public void move(SimState state){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;//Make explicit the Environment
		if(e.random.nextBoolean(probabilityOfChange)){
			xdir= e.random.nextInt(3)-1;
			ydir= e.random.nextInt(3)-1;
		}
		x = x + xdir; //move one step
		y = y + ydir; //move one step
		x = e.particleSpace.stx(x); //correct x location for being on a torus
		y = e.particleSpace.sty(y);//correct y location for being on a torus
		handleCells( state); //Method
		//sets the particle in the new location.
	}

	public void moveWithBoundaries(SimState state){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;//Make explicit the Environment
		if(e.random.nextBoolean(probabilityOfChange)){
			xdir= e.random.nextInt(3)-1;
			ydir= e.random.nextInt(3)-1;
		}
		x = x + xdir; //move one step
		y = y + ydir; //move one step
		x = bxdir(state,x); //correct x location for boundaries
		y = bydir(state,y);//correct y location for boundaries
		handleCells( state); //Method
		//sets the particle in the new location.
	}

	public void moveWithCollisions(SimState state, int tries){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state; //Environment variable
		if(e.random.nextBoolean(probabilityOfChange)){
			xdir= e.random.nextInt(3)-1;
			ydir= e.random.nextInt(3)-1;
		}
		int newx = x + xdir;
		int newy = y + ydir;
		newx = e.particleSpace.stx(newx);
		newy = e.particleSpace.sty(newy);
		Bag b = e.particleSpace.getObjectsAtLocation(newx, newy);
		for(int j=0; j<tries;j++){
			if(!(b != null && b.numObjs >0)){
				x = newx;
				y = newy;
				handleCells( state);
				break; //stop the for loop!
			}

			int xdir =e.random.nextInt(3)-1; //returns a random integer
			//in the range -1 to 1.
			int ydir = e.random.nextInt(3)-1; //returns a random integer
			//in the range -1 to 1.
			newx = x + xdir;
			newy = y + ydir;
			newx = e.particleSpace.stx(newx);
			newy = e.particleSpace.sty(newy);
			b = e.particleSpace.getObjectsAtLocation(newx, newy);
		}
	}

	public void moveWithCollisionsBoundaries(SimState state, int tries){
		MateChoiceEnvironment e = (MateChoiceEnvironment)state;
		if(e.random.nextBoolean(probabilityOfChange)){
			xdir= e.random.nextInt(3)-1;
			ydir= e.random.nextInt(3)-1;
		}
		//convert the SimState to our Environment
		int newx = x + xdir; //generate the coordinates of the
		int newy = y + ydir; //new location to move to
		newx = bxdir(state,newx);
		newy = bydir(state,newy);

		Bag b = e.particleSpace.getObjectsAtLocation(newx, newy);
		//get the bag at that location
		for(int j=0; j<tries; j++){ //try up to tries times
			if(!(b!= null && b.numObjs >0)){
				x = newx;
				y = newy;
				handleCells( state);
				break; //end for loop and place agent
			}                         
			xdir = e.random.nextInt(3)-1; 
			//change direction randomly
			ydir = e.random.nextInt(3)-1;
			newx = x + xdir; //generate the coordinates of the
			newy = y + ydir; //new location to move to
			newx = bxdir(state,newx);
			newy = bydir(state,newy);
			b = e.particleSpace.getObjectsAtLocation(newx, newy);
		} //end for
	}

	public void wander(SimState state){
		if(avoidCollisions){
			if(boundaries){
				moveWithCollisionsBoundaries(state, attemptsToAvoidCollisions);
			}
			else {
				moveWithCollisions(state, attemptsToAvoidCollisions); 
				//if doCollisions is true
			}
		}
		else {  //particles pass through each other
			if(boundaries){ //there are boundaries
				moveWithBoundaries(state);
			}
			else { //there are no boundaries
				move(state); //else move as before (torus)
			}
		}
		wander = false;
	}

	public void handleCells(SimState state){
		MateChoiceEnvironment e =(MateChoiceEnvironment)state;
		if(unoccupied){
			Bag b = e.particleSpace.getObjectsAtLocation(x, y);
			if(b == null || b.numObjs == 0){
				e.particleSpace.setObjectLocation(this, x, y);
			}
			else {// change the coordinates back to where they were
				Int2D xy = e.particleSpace.getObjectLocation(this);
				x = xy.x;
				//reset the coordinates to the objects location
				y = xy.y;
				wander = true;
			}
		}
		else {
			e.particleSpace.setObjectLocation(this, x, y);
		}
	}

	public void setUnoccupied(boolean unoccupied) {
		this.unoccupied = unoccupied;
	}

	public int decideX (Bag neighbors){
		int posx =0, negx=0;
		for(int i=0; i<neighbors.numObjs;i++){
			Particle p = (Particle)neighbors.objs[i];
			int px = p.x; //we have to correct x for a torus
			if(px < x - searchRadius){ //a particle is on the
				//opposite side <--
				px = x + 1; //make sure px is greater than x
			}
			else if(px > x + searchRadius)//a particle is on the 
				//opposite side -->
				px = x - 1; //make sure px is less than x
			if(px > x){
				posx++;
			}
			else if(px < x){
				negx++;
			}
		} // end for

		if(posx > negx){
			return 1;
		}
		else if(negx > posx){
			return -1;
		}
		else {
			return 0;
		}
	}

	public int decideY (Bag neighbors){
		int posy =0, negy=0;
		for(int i=0; i<neighbors.numObjs;i++){
			Particle p = (Particle)neighbors.objs[i];
			int py = p.y; //we have to correct y for a torus
			if(py < y - searchRadius){ //a particle is on the
				//opposite side <--
				py = y + 1; //make sure py is greater than y
			}
			else if(py > y + searchRadius)//a particle is on the 
				//opposite side -->
				py = y - 1; //make sure py is less than y

			if(py > y){
				posy++;
			}
			else if(py < y){
				negy++;
			}
		} // end for

		if(posy > negy){
			return 1;
		}
		else if(negy > posy){
			return -1;
		}
		else {
			return 0;
		}
	}

	public void aggregate(SimState state){
		MateChoiceEnvironment e =(MateChoiceEnvironment)state;
		Bag neighbors =
				e.particleSpace.getNeighborsMaxDistance(x, y,
						searchRadius, !boundaries, null, null, null);
		//!boundaries is true if a torus
		if(neighbors.numObjs < 2){//The particle itself will be in the
			wander(state);        //bag, but there may be no other
		}                         //particles around it so it should 
		else{                     //wander
			xdir = decideX(neighbors);
			ydir = decideY(neighbors);
			x+=xdir;
			y+=ydir;
			if(!boundaries){
				x = e.particleSpace.stx(x);
				y = e.particleSpace.sty(y);
			}
			else {
				x = bxdir(state,x);
				y = bydir(state,y);
			}
			handleCells(state);
		}
	}

	public int decideCX (SimState state, Bag neighbors){
		MateChoiceEnvironment e =(MateChoiceEnvironment)state;
		double dxplus = 0, dxneg=0, dxzero=0;
		for(int i=0; i<neighbors.numObjs;i++){
			Particle p = (Particle)neighbors.objs[i];
			if(p.xdir>0){
				dxplus++;
			}
			else if (p.xdir < 0){
				dxneg++;
			}
			else {
				dxzero++;
			}
		} // end for
		if(dxplus > dxneg && dxplus > dxzero){
			return 1;
		}
		else if(dxneg > dxplus && dxneg > dxzero){
			return -1;
		}
		else if(dxzero> dxplus && dxzero > dxneg){
			return 0;
		}
		else if(dxplus > dxzero && dxneg > dxzero){
			if(dxplus > dxneg){
				return 1;
			}
			else if(dxneg > dxplus){
				return -1;
			}
			else if(e.random.nextBoolean()){ //they are equal
				return 1;
			}
			else {
				return -1;
			}
		}
		else if(dxplus > dxneg && dxzero > dxneg){
			if(dxplus > dxzero){
				return 1;
			}
			else if(dxzero > dxplus){
				return 0;
			}
			else if(e.random.nextBoolean()){
				return 1;
			}
			else {
				return 0;
			}
		}
		else if (dxneg > dxplus && dxzero > dxplus){
			if(dxneg > dxzero){
				return -1;
			}
			else if(dxzero > dxneg){
				return 0;
			}
			else if(e.random.nextBoolean()){
				return -1;
			}
			else {
				return 0;
			}
		}
		else {
			return e.random.nextInt(3)-1;
		}
	}

	public int decideCY (SimState state, Bag neighbors){
		MateChoiceEnvironment e =(MateChoiceEnvironment)state;
		double dyplus = 0, dyneg=0, dyzero=0;
		for(int i=0; i<neighbors.numObjs;i++){
			Particle p = (Particle)neighbors.objs[i];
			if(p.ydir>0){
				dyplus++;
			}
			else if (p.ydir < 0){
				dyneg++;
			}
			else {
				dyzero++;
			}
		} // end for
		if(dyplus > dyneg && dyplus > dyzero){
			return 1;
		}
		else if(dyneg > dyplus && dyneg > dyzero){
			return -1;
		}
		else if(dyzero> dyplus && dyzero > dyneg){
			return 0;
		}
		else if(dyplus > dyzero && dyneg > dyzero){
			if(dyplus > dyneg){
				return 1;
			}
			else if(dyneg > dyplus){
				return -1;
			}
			else if(e.random.nextBoolean()){ //they are equal
				return 1;
			}
			else {
				return -1;
			}
		}
		else if(dyplus > dyneg && dyzero > dyneg){
			if(dyplus > dyzero){
				return 1;
			}
			else if(dyzero > dyplus){
				return 0;
			}
			else if(e.random.nextBoolean()){
				return 1;
			}
			else {
				return 0;
			}
		}
		else if (dyneg > dyplus && dyzero > dyplus){
			if(dyneg > dyzero){
				return -1;
			}
			else if(dyzero > dyneg){
				return 0;
			}
			else if(e.random.nextBoolean()){
				return -1;
			}
			else {
				return 0;
			}
		}
		else {
			return e.random.nextInt(3)-1;
		}
	}

	public void coordinate(SimState state){
		MateChoiceEnvironment e =(MateChoiceEnvironment)state;
		Bag neighbors = e.particleSpace.getNeighborsMaxDistance(x, y, searchRadius, 
				!boundaries, null, null, null); //!boundaries is true if a torus
		neighbors.remove(this);
		xdir = decideCX(state, neighbors);
		ydir = decideCY(state,neighbors);
		x+=xdir;
		y+=ydir;	
		if(!boundaries){
			x = e.particleSpace.stx(x);
			y = e.particleSpace.sty(y);
		}
		else {
			x = bxdir(state,x);
			y = bydir(state,y);
		}
		handleCells( state);
	}


	public void step(SimState state) {
		if(aggregate && !coordinate){
			if(state.random.nextBoolean(aggregateProbability))
				aggregate(state);
			else
				wander(state);
		}
		else if(coordinate && !aggregate){
			if(state.random.nextBoolean(coordinateProbability))
				coordinate(state);
			else
				wander(state);
		}
		else if(coordinate && aggregate){
			if(state.random.nextBoolean(coordinateProbability))
				coordinate(state);
			else
				wander(state);
			if(state.random.nextBoolean(aggregateProbability))
				aggregate(state);
			else
				wander(state);
		}
		else {
			wander(state);
		}
	}

}
