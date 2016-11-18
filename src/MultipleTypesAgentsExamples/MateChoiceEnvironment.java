package MultipleTypesAgentsExamples;


import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.util.Bag;

public class MateChoiceEnvironment extends SimState {

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

	public double aggregateProbability = 0.1;
	public double coordinateProbability = 0.1;

	public final static int FEMALE = 0;
	public final static int MALE = 1;
	public int females = 1000;
	public int males = 1000;
	public Bag femalePop;
	public Bag malePop;
	public double maxAttractiveness = 10;
	public int maxDates =50;
	public double r = 3; //choosiness exponent
	public boolean chooseTheBest = true;
	public int dateSearchRadius = 2;



	public SparseGridPortrayal2D particlesPortrayal; //This space portrayal is needed for changing
	//the color of agents.  it is set in ParticlesWithUI

	public final static int AGGREGATE_COORDINATE = 0;
	public final static int KH = 1; //Kalick and Hamilton model
	public final static int KHM = 2;//Kalick and Hamilton model with movement


	public int simulationType = KH;
	public int getsimulationType(){
		return simulationType;
	}

	public void setsimulationType(int i){
		switch(i){
		case 0: simulationType = this.AGGREGATE_COORDINATE; break;
		case 1: simulationType = this.KH; break;
		case 2: simulationType = this.KHM; break;
		default: simulationType = this.KH;
		}
	}

	public Object domsimulationType(){
		return new String[] {"Aggregation and Flocking","KH-Model","KH-Movement"};
	}

	public int getFemales() {
		return females;
	}

	public void setFemales(int females) {
		this.females = females;
	}

	public int getMales() {
		return males;
	}

	public void setMales(int males) {
		this.males = males;
	}

	public boolean isChooseTheBest() {
		return chooseTheBest;
	}

	public void setChooseTheBest(boolean chooseTheBest) {
		this.chooseTheBest = chooseTheBest;
	}

	public double getMaxDates() {
		return maxDates;
	}

	public void setMaxDates(int maxDates) {
		this.maxDates = maxDates;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public int getDateSearchRadius() {
		return dateSearchRadius;
	}

	public void setDateSearchRadius(int dateSearchRadius) {
		this.dateSearchRadius = dateSearchRadius;
	}


	public double getAggregateProbability() {
		return aggregateProbability;
	}

	public void setAggregateProbability(double aggregateProbability) {
		if(aggregateProbability >= 0 && aggregateProbability<=1)
			this.aggregateProbability = aggregateProbability;
	}

	public double getCoordinateProbability() {
		return coordinateProbability;
	}

	public void setCoordinateProbability(double coordinateProbability) {
		if(coordinateProbability>=0 && coordinateProbability<=1)
			this.coordinateProbability = coordinateProbability;
	}

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

	public MateChoiceEnvironment(long seed) { //The constructor method
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
	public void placeAgent(int sex, Bag pop, PearsonCorrelation correlation ){
		Agent p = new Agent();
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
		p.setAggregateProbability(aggregateProbability);
		p.setCoordinateProbability(coordinateProbability);

		p.setSexID(sex);
		p.setMaxAttractiveness(maxAttractiveness);
		p.setAttractiveness((double)this.random.nextInt((int)maxAttractiveness)+1);
		p.setColor(this);
		p.setMaxDates(maxDates);
		p.setR(r);
		p.setChooseTheBest(chooseTheBest);
		p.setDateSearchRadius(dateSearchRadius);
		p.setPC(correlation);
		particleSpace.setObjectLocation(p, x, y); //Method for
		//placing particles in 2D space at location x, y.
		p.event = schedule.scheduleRepeating(p); //It places the particle in
		//a list of all the particles.  Then on each time step, the
		//list is randmalePopomly shuffled and the Environment calls each
		//particle's step method one at a time.
		pop.add(p);
	}

	public void makeMalesandFemales(Bag femalePop, Bag malePop, PearsonCorrelation correlation ){
		if(unoccupied && males+females <= gridWidth*gridHeight){ 
			for(int i=0;i<females;i++){ //make female agents
				placeAgent(FEMALE, femalePop, correlation);			
			}
			for(int i=0;i<males;i++){ //make male agents
				placeAgent(MALE, malePop, correlation);			
			}
		}
		else if(!unoccupied) {
			for(int i=0;i<females;i++){ //make female agents
				placeAgent(FEMALE, femalePop, correlation);			
			}
			for(int i=0;i<males;i++){ //make male agents
				placeAgent(MALE, malePop, correlation);			
			}	
		} 	
		else { 	
			System.out.println("An error occurred: unoccupied && males + females > gridWidth*gridHeight"); //If unoccupied = true and n > gridWidth*gridHeight
		} //end else
	} //end makeMalesandFemales


	public void start(){
		super.start();
		particleSpace = new SparseGrid2D(gridWidth, gridHeight); //create a 2D
		femalePop = new Bag(females);
		malePop = new Bag(males);
		PearsonCorrelation correlation = new PearsonCorrelation();
		Observer ex = new Observer(this,correlation);

		//space for our agents.

		makeMalesandFemales(femalePop,malePop,correlation );
	}

}
