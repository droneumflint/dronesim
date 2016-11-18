package dronesim;

import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;

public class Environment extends SimState {
	
	public SparseGrid2D particleSpace;//2D space for holding particles
	public int gridWidth =64; //Width space integer units, x-axis
	public int gridHeight = 64;//The height of the space, y-axis
	public int n = 100; //the number of agents
	public boolean avoidCollisions = false; //avoid collisions?
	public int attemptsToAvoidCollisions = 2; //attempts to avoide collisions
	public double probabilityOfChange = 0.1; //probability of changing direction
	public boolean boundaries = false; //does the space have boundaries?
	public boolean unoccupied = true; //should a cell be unoccupied to move into it?
	public boolean aggregate = true;
	public int searchRadius = 2;
	public boolean coordinate = false; //particles coordinate their behavior with
										//their local neigbors
	public boolean isCoordinate() {
		return coordinate;
	}

	public void setCoordinate(boolean coordinate) {
		this.coordinate = coordinate;
	}
	
	public boolean isAggregate() {
		return aggregate;
	}

	public void setAggregate(boolean aggregate) {
		this.aggregate = aggregate;
	}

	public int getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(int searchRadius) {
		if(searchRadius >0)
		this.searchRadius = searchRadius;
	}

	public boolean isUnoccupied() {
		return unoccupied;
	}

	public void setUnoccupied(boolean unoccupied) {
		this.unoccupied = unoccupied;
	}

	public boolean getBoundaries() {
		return boundaries;
	}

	public void setBoundaries(boolean boundaries) {
		this.boundaries = boundaries;
	}

	public double getProbabilityOfChange() {
		return probabilityOfChange;
	}

	public void setProbabilityOfChange(double probabilityOfChange) {
		if(probabilityOfChange >= 0 && probabilityOfChange <= 1)
		this.probabilityOfChange = probabilityOfChange;
	}

	public int getAttemptsToAvoidCollisions() {
		return attemptsToAvoidCollisions;
	}

	public void setAttemptsToAvoidCollisions(int attemptsToAvoidCollisions){
		if(attemptsToAvoidCollisions > 0)
			this.attemptsToAvoidCollisions = attemptsToAvoidCollisions;
	}

	public Environment(long seed) { //The constructor method
		super(seed);
		// TODO Auto-generated constructor stub
	}
	
	public int getgridWidth(){//A pair get and set methods for changing
		//the width of the particle space
		return gridWidth;
	}
	
	public void setgridWidth(int x){
		if(x >0){
			gridWidth = x;
		}
	}
	
	public int getgridHeight(){ //A pair of set and get methods for
		//changing the height of the particle space.
		return gridHeight;
	}
	
	public void setgridHeight(int x){
		if(x >0){
			gridHeight = x;
		}
	}
	
	public int getn(){ //A pair of set and get methods for changing
		//the number of particles/agents in a simulation
		return n;
	}
	
	public void setn(int i){
		if(i >0){
			n = i;
		}
	}
	
	public boolean getavoidCollisions(){
		return avoidCollisions;
	}
	
	public void setavoidCollisions(boolean b){
		avoidCollisions = b;
	}
	
	/**
	* A new method that handles initial placement of agents
	*/
	public void placeAgent(){
	    Particle p = new Particle();
	    int x,y;
	    if(!unoccupied){ //allows more than one agent to occupy a cell
	         x = random.nextInt(gridWidth);
	         y = random.nextInt(gridHeight);
	     }
	     else{
	         x = random.nextInt(gridWidth);
	         y = random.nextInt(gridHeight);
	         for(;;){
	             Bag b = particleSpace.getObjectsAtLocation(x, y);
	             if(b == null || b.numObjs == 0){
	                 break;
	             }
	             x = random.nextInt(gridWidth);
	             y = random.nextInt(gridHeight);
	             }
	        }

	      //Now set all the parameters

	      	int xdir =random.nextInt(3)-1; //returns a random integer
		        //in the range -1 to 1.
		int ydir = random.nextInt(3)-1; //returns a random integer
		        //in the range -1 to 1.
		p.setLocation(x, y, xdir, ydir); //A method in the class
		        //Particle that sets the values listed to variable in
			//the class.
		p.setCollision(avoidCollisions); //boolean that determines whether
			//particles handle collisions
		p.setAttemptsToAvoidCollisions(attemptsToAvoidCollisions); //Integer
			//that determines how many attempts will be made 
	                //to avoid collisions
		p.setProbabilityOfChange(probabilityOfChange); //the probability that a
			//a particle will change directions
		p.setBoundaries(boundaries); //set whether there are boundaries or not
		p.setUnoccupied(unoccupied);
		p.setAggregate(aggregate);
		p.setSearchRadius(searchRadius);
		p.setCoordinate(coordinate);
		particleSpace.setObjectLocation(p, x, y); //Method for
		       //placing particles in 2D space at location x, y.
		schedule.scheduleRepeating(p); //It places the particle in
			//a list of all the particles.  Then on each time step, the
			//list is randomly shuffled and the Environment calls each
			//particle's step method one at a time.
	}
	
	public void start(){
		super.start();
		particleSpace = new SparseGrid2D(gridWidth, gridHeight); //create a 2D
		//space for our agents.

		if(unoccupied && n <= gridWidth*gridHeight){ 
			for(int i=0;i<n;i++){ //make n particles
			    placeAgent();			
			}
		}
		else if(!unoccupied) {
			for(int i=0;i<n;i++){//make n particles 
	                    placeAgent(); 		
	                } 	
	         } 	
	        else { 	
	            System.out.println("An error occurred: unoccupied && n > gridWidth*gridHeight"); //If unoccupied = true and n > gridWidth*gridHeight
	            } //end else
	    } //end start


}